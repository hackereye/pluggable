package com.itrustcambodia.pluggable.error;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.request.http.WebResponse;

import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.layout.ErrorLayout;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;

@Mount("/a")
public class AccessDeniedPage extends AbstractErrorPage {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public AccessDeniedPage() {
        ErrorLayout layout = new ErrorLayout("layout");
        add(layout);
        layout.add(homePageLink("homePageLink"));
    }

    @Override
    protected void setHeaders(final WebResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Override
    public String getPageTitle() {
        return "Access Denied";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return null;
    }
}