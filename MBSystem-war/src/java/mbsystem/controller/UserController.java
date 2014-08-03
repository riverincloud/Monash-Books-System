/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import mbsystem.ejb.UsersBean;
import mbsystem.entity.Groups;
import mbsystem.entity.Users;
import mbsystem.qualifier.LoggedIn;
import mbsystem.util.JsfUtil;

/**
 *
 * @author Di
 */
@Named(value = "userController")
@SessionScoped
public class UserController implements Serializable {

    private static final Logger LOG = Logger.getLogger(UserController.class.getName());

    @EJB
    UsersBean ejbFacade;

//    @Inject
//    PatronController patronController;
    Users user;
    private String username; // user's email
    private String password;

    /**
     * Constructor
     */
    public UserController() {
    }

    /**
     * @return the ejbFacade
     */
    public UsersBean getEjbFacade() {
        return ejbFacade;
    }

    /**
     *
     * @return the user (regardless authenticated or not)
     */
    public Users getUser() {
        return user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Login method based on <code>HttpServletRequest</code> and security realm
     *
     * @return
     */
    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            // try login with username (email) and password input
            request.login(this.getUsername(), this.getPassword());
            JsfUtil.addSuccessMessage("Login Success");
            // read the user from database
            this.user = ejbFacade.getUserByEmail(getUsername());
            // call the getAuthedUser() method
            this.getAuthedUser();
            // stay on current page
            return null;
        } catch (ServletException ex) {
            LOG.log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("Login Failed - Invalid email or password");
            // redirect to the login page if the current login failed
            return "/login";
        }
    }

    /**
     * Log out method
     *
     * @return
     */
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            // reset the user to null
            this.user = null;
            request.logout();
            // clear the session
            ((HttpSession) context.getExternalContext().getSession(false)).invalidate();
            JsfUtil.addSuccessMessage("Logout Success");
            // clear the username field
            username = null;
            // redirect to home page
            return "index";
        } catch (ServletException ex) {
            LOG.log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("Logout Failed");
            // redirect to the login page if log out failed
            // the login page would render a "Log out" button in this case
            return "login";
        }
    }

    /**
     *
     * @return the user (authenticated user)
     */
    public @Produces
    @LoggedIn
    Users getAuthedUser() {
        return user;
    }

    /**
     * Check whether the user is logged in
     *
     * @return
     */
    public boolean isLogged() {
        return (getUser() != null);
    }

    /**
     * Check whether the user is an administrator
     *
     * @return
     */
    public boolean isAdmin() {
        for (Groups g : user.getGroupList()) {
            if (g.getGroupname().equals("ADMINS")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the user is a patron
     *
     * @return
     */
    public boolean isPatron() {
        for (Groups g : user.getGroupList()) {
            if (g.getGroupname().equals("USERS")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Enter the Administration Site
     *
     * @return
     */
    public String enterAdmin() {
        if (isAdmin()) {
            // direct to Administration Site home page for adminstrator
            return "/admin/index";
        } else {
            // redirect to Main Site home page if not an adminstrator
            // A non-administrator shold not be able to see the button on the page that calls this method, but double check here just in case.
            return "/index";
        }
    }

}
