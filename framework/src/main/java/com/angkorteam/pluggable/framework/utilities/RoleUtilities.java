package com.angkorteam.pluggable.framework.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.angkorteam.pluggable.framework.database.EntityMapper;
import com.angkorteam.pluggable.framework.mapper.RoleMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.angkorteam.pluggable.framework.entity.AbstractUser;
import com.angkorteam.pluggable.framework.entity.Group;
import com.angkorteam.pluggable.framework.entity.Role;
import com.angkorteam.pluggable.framework.entity.RoleGroup;
import com.angkorteam.pluggable.framework.entity.RoleUser;

/**
 * @author Socheat KHAUV
 */
public class RoleUtilities {

    private RoleUtilities() {
    }

    public static final List<String> lookupJdbcRoles(JdbcTemplate jdbcTemplate,
            String login) {

        List<String> roles = new ArrayList<String>();

        Map<String, Object> user = jdbcTemplate.queryForMap("select * from "
                + TableUtilities.getTableName(AbstractUser.class) + " where "
                + AbstractUser.LOGIN + " = ?", login);

        StringBuffer select = null;

        select = new StringBuffer();
        select.append("select `group`." + Group.ID + " from tbl_group `group` ");
        select.append("inner join tbl_user_group user_group on `group`.group_id = user_group.group_id ");
        select.append("where user_group.user_id = ? and `group`.disable = ?");
        List<Long> groups = jdbcTemplate.queryForList(select.toString(),
                Long.class, user.get(AbstractUser.ID), false);

        if (groups != null && !groups.isEmpty()) {
            select = new StringBuffer();
            select.append("select role." + Role.NAME + " from "
                    + TableUtilities.getTableName(Role.class) + " role ");
            select.append("inner join "
                    + TableUtilities.getTableName(RoleGroup.class)
                    + " role_group on role." + Role.ID + " = role_group."
                    + RoleGroup.ROLE_ID + " ");
            select.append("where role_group." + RoleGroup.GROUP_ID + " in ("
                    + StringUtils.join(groups, ",") + ") and role."
                    + Role.DISABLE + " = ? group by role." + Role.ID);
            roles.addAll(jdbcTemplate.queryForList(select.toString(),
                    String.class, false));
        }
        select = new StringBuffer();
        select.append("select role." + Role.NAME + " from "
                + TableUtilities.getTableName(Role.class) + " role ");
        select.append("inner join "
                + TableUtilities.getTableName(RoleUser.class)
                + " role_user on role." + Role.ID + " = role_user."
                + RoleUser.ROLE_ID + " ");
        select.append("where role_user." + RoleUser.USER_ID + " = ? and role."
                + Role.DISABLE + " = ?");
        roles.addAll(jdbcTemplate.queryForList(select.toString(), String.class,
                user.get(AbstractUser.ID), false));

        return Collections.<String> unmodifiableList(roles);
    }

    public static final void removeJdbcRole(JdbcTemplate jdbcTemplate,
            String name) {
        Role role = jdbcTemplate.queryForObject("select * from "
                + TableUtilities.getTableName(Role.class) + " where "
                + Role.NAME + " = ?", new RoleMapper(),
                name);
        jdbcTemplate.update(
                "delete from " + TableUtilities.getTableName(RoleUser.class)
                        + " where " + RoleUser.ROLE_ID + " = ?", role.getId());
        jdbcTemplate.update(
                "delete from " + TableUtilities.getTableName(RoleGroup.class)
                        + " where " + RoleGroup.ROLE_ID + " = ?", role.getId());
        jdbcTemplate.update(
                "delete from " + TableUtilities.getTableName(Role.class)
                        + " where " + Role.ID + " = ?", role.getId());
    }



    public static final Role createJdbcRole(DataSource dataSource, String name,
            String description, boolean disable) {
        SimpleJdbcInsert insertRole = new SimpleJdbcInsert(dataSource);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        insertRole.withTableName(TableUtilities.getTableName(Role.class));
        insertRole.usingGeneratedKeyColumns(Role.ID);
        Map<String, Object> fields = new HashMap<String, Object>();
        Role role = null;
        Long roleId = null;
        try {
            role = jdbcTemplate.queryForObject("select * from "
                            + TableUtilities.getTableName(Role.class) + " where "
                            + Role.NAME + " = ?",
                    new RoleMapper(), name);
        } catch (EmptyResultDataAccessException e) {
        }
        if (role == null) {
            fields.put(Role.NAME, name);
            fields.put(Role.DESCRIPTION, description);
            fields.put(Role.DISABLE, disable);
            roleId = insertRole.executeAndReturnKey(fields).longValue();
            role = new Role();
            role.setName(name);
            role.setDescription(description);
            role.setId(roleId);
        }
        return role;
    }


}
