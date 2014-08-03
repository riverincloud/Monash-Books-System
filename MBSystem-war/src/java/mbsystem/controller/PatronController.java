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
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;

import mbsystem.ejb.UsersBean;
import mbsystem.entity.Patron;
import mbsystem.entity.Users;
import mbsystem.qualifier.LoggedIn;
import mbsystem.util.PaginationHelper;
import mbsystem.util.JsfUtil;
import mbsystem.util.DigestUtil;
import mbsystem.util.PageNavigation;

/**
 *
 * @author Di
 */
@Named(value = "patronController")
@SessionScoped
public class PatronController implements Serializable {

    private static final Logger LOG = Logger.getLogger(PatronController.class.getName());

    @EJB
    private UsersBean ejbFacade;

    @Inject
    @LoggedIn
    Users authedUser;

    private Patron currentPatron;
    private DataModel items = null;
    private PaginationHelper pagination;
    private String rePassword; // for the "re-enter password" field

    /**
     * Constructor
     */
    public PatronController() {
    }

    /**
     *
     * @return the authenticated user
     */
    public Users getAuthedUser() {
        return authedUser;
    }

    /**
     *
     * @param user
     */
    public void setAuthedUser(Users user) {
        this.authedUser = user;
    }

    /**
     * Set the patron to be the authenticated user
     *
     * @param patron
     */
    public void setPatron(Patron patron) {
        this.authedUser = patron;
    }

    /**
     * Get the current patron, construct a new one if not already exists
     *
     * @return the current patron
     */
    public Patron getSelectedPatron() {
        if (currentPatron == null) {
            currentPatron = new Patron();
        }
        return currentPatron;
    }

    /**
     *
     * @return the pagination
     */
    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(PaginationHelper.DEFAULT_SIZE) {
                @Override
                public int getItemsCount() {
                    return ejbFacade.count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(ejbFacade.findRange(new int[]{getPageFirstItem(),
                        getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }

    /**
     *
     * @return the List page
     */
    public PageNavigation prepareList() {
        // Call recreateModel() method
        recreateModel();
        return PageNavigation.LIST;
    }

    // the recreateModel() method - "deselect" any item if applicable
    private void recreateModel() {
        items = null;
    }

    // Go to next or previous page
    public PageNavigation next() {
        getPagination().nextPage();
        recreateModel();
        return PageNavigation.LIST;
    }

    public PageNavigation previous() {
        getPagination().previousPage();
        recreateModel();
        return PageNavigation.LIST;
    }

    /**
     *
     * @return the View page
     */
    public PageNavigation prepareView() {
        // Call getItems() method to find the current patron
        currentPatron = (Patron) getItems().getRowData();
        return PageNavigation.VIEW;
    }

    // the getItems() method - "select" an item
    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    /**
     *
     * @return the Create page
     */
    public PageNavigation prepareCreate() {
        currentPatron = new Patron();
        return PageNavigation.CREATE;
    }

    /**
     *
     * @return the Edit page
     */
    public PageNavigation prepareEdit() {
        currentPatron = (Patron) getItems().getRowData();
        return PageNavigation.EDIT;
    }

    /**
     * Create a new patron (created by administrator)
     *
     * @return the View page if successful, otherwise null
     */
    public PageNavigation create() {
        try {
            // confirm the patron is not duplicated and the password input is valid
            if (!isUserDuplicated(currentPatron) && isPasswordValid(currentPatron.getPassword(), rePassword)) {
                // password encrypt
                currentPatron.setPassword(DigestUtil.generate(currentPatron.getPassword()));
                ejbFacade.create(currentPatron);
                JsfUtil.addSuccessMessage("Patron Created");
                return PageNavigation.VIEW;
            }
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Patron Creation Error");
            return null;
        }
    }

    /**
     * Create a new patron (created by a patron through registration)
     *
     * @return the login page if successful, otherwise null
     */
    public String createAndView() {
        try {
            // confirm the patron is not duplicated and the password input is valid
            if (!isUserDuplicated(currentPatron) && isPasswordValid(currentPatron.getPassword(), rePassword)) {
                // password encrypt
                currentPatron.setPassword(DigestUtil.generate(currentPatron.getPassword()));
                ejbFacade.create(currentPatron);
                JsfUtil.addSuccessMessage("Registration successful, ready to login.");
                // redirect to the login page
                return "/login";
            }
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Registration Error");
            return null;
        }
    }

    // Check if user already exsists
    private boolean isUserDuplicated(Users u) {
        if (ejbFacade.getUserByEmail(u.getEmail()) != null) {
            JsfUtil.addErrorMessage("Duplicated Patron Error");
            return true;
        }
        return false;
    }

    // Check if the input for password is valid (before encrption)
    private boolean isPasswordValid(String password, String rePassword) {
        if (password.length() < 6 || password.length() > 20) {
            JsfUtil.addErrorMessage("Password must be 6 to 12 characters long.");
            return false;
        } else if (rePassword.length() == 0) {
            JsfUtil.addErrorMessage("Please re-enter password.");
            return false;
        } else if (!password.equals(rePassword)) {
            JsfUtil.addErrorMessage("Passwords do not match.");
            return false;
        }
        return true;
    }

    /**
     * update current patron
     *
     * @return the View page if successful or the Edit page if not
     */
    public PageNavigation update() {
        try {
            LOG.log(Level.INFO, "Updating patron ID:{0}", currentPatron.getId());
            // confirm the password input is valid
            if (isPasswordValid(currentPatron.getPassword(), rePassword)) {
                // password encrypt
                currentPatron.setPassword(DigestUtil.generate(currentPatron.getPassword()));
                ejbFacade.update(currentPatron);
                JsfUtil.addSuccessMessage("Patron Updated");
                return PageNavigation.VIEW;
            }
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error Occured");
            return null;
        }
    }

    /**
     * Delete current patron (triggered on List page)
     *
     * @return the List page
     */
    public PageNavigation destroy() {
        // Call getItems() method to select the current patron
        currentPatron = (Patron) getItems().getRowData();
        // Call performDestroy() method to delete current loan
        performDestroy();
        // Call the recreateModel() method to deselect the item
        recreateModel();
        // Return the List page at last
        return PageNavigation.LIST;
    }

    /**
     * Delete current patron (triggered on View page)
     *
     * @return the List page
     */
    public PageNavigation destroyAndView() {
        performDestroy();
        recreateModel();
        return PageNavigation.LIST;
    }

    // the performDestroy() method - delete current patron and indicate the result
    private void performDestroy() {
        try {
            ejbFacade.delete(currentPatron);
            JsfUtil.addSuccessMessage("Patron Deleted");
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error Occured");
        }
    }

}
