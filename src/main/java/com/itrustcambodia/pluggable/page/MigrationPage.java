package com.itrustcambodia.pluggable.page;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import com.itrustcambodia.pluggable.PluggableConstants;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.core.Version;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.entity.ApplicationRegistry;
import com.itrustcambodia.pluggable.form.VersionForm;
import com.itrustcambodia.pluggable.layout.AbstractLayout;
import com.itrustcambodia.pluggable.migration.AbstractApplicationMigrator;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@Mount("/m")
@AuthorizeInstantiation(roles = { @com.itrustcambodia.pluggable.wicket.authroles.Role(name = "ROLE_PAGE_MIGRATION", description = "Access Migration Page") })
public class MigrationPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = 8329671858656695222L;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###,##0.00###");

    @Override
    public String getPageTitle() {
        return "Application Migration";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return null;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        AbstractLayout layout = requestLayout("layout");
        add(layout);
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        AbstractApplicationMigrator migrator = (AbstractApplicationMigrator) application.getBean(application.getMigrator().getName());

        Label newVersion1 = new Label("newVersion1", DECIMAL_FORMAT.format(migrator.getVersion()));
        layout.add(newVersion1);

        Label newVersion2 = new Label("newVersion2", DECIMAL_FORMAT.format(migrator.getVersion()));
        layout.add(newVersion2);

        ApplicationRegistry applicationRegistry = application.getJdbcTemplate().queryForObject("select * from " + TableUtilities.getTableName(ApplicationRegistry.class) + " order by " + ApplicationRegistry.VERSION + " desc limit 1", new EntityRowMapper<ApplicationRegistry>(ApplicationRegistry.class));
        Label oldVersion = new Label("oldVersion", DECIMAL_FORMAT.format(applicationRegistry.getVersion()));
        layout.add(oldVersion);

        Map<Double, String> versions = new TreeMap<Double, String>();
        for (Method method : migrator.getClass().getMethods()) {
            Version version = method.getAnnotation(Version.class);
            if (version != null) {
                if (version.value() > applicationRegistry.getVersion()) {
                    versions.put(version.value(), version.description());
                }
            }
        }
        List<VersionForm> versionInfos = new LinkedList<VersionForm>();
        for (Entry<Double, String> entry : versions.entrySet()) {
            VersionForm item = new VersionForm();
            item.setVersion(entry.getKey());
            item.setDescription(entry.getValue());
            versionInfos.add(0, item);
        }

        ListView<VersionForm> descriptions = new ListView<VersionForm>("descriptions", versionInfos) {

            private static final long serialVersionUID = -139639597325251874L;

            @Override
            protected void populateItem(ListItem<VersionForm> item) {
                Label version = new Label("version", DECIMAL_FORMAT.format(item.getModelObject().getVersion()));
                item.add(version);
                Label description = new Label("description", item.getModelObject().getDescription());
                item.add(description);
            }
        };
        layout.add(descriptions);

        Form<Void> upgradeForm = new Form<Void>("upgradeForm");

        Button upgradeButton = new Button("upgradeButton") {

            /**
             * 
             */
            private static final long serialVersionUID = -858249559970825568L;

            @Override
            public void onSubmit() {
                upgradeButtonClick();
            }

        };
        upgradeForm.add(upgradeButton);
        layout.add(upgradeForm);
    }

    public void upgradeButtonClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        application.update(PluggableConstants.DEBUG, true);
        application.update(PluggableConstants.DEPRECATED, true);
        AbstractApplicationMigrator migrator = (AbstractApplicationMigrator) application.getBean(application.getMigrator().getName());
        if (migrator.upgrade()) {
            setResponsePage(application.getSettingPage());
        } else {
            setResponsePage(MigrationPage.class);
        }
    }
}
