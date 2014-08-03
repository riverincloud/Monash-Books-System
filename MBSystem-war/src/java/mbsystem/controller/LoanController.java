/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.controller;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import mbsystem.ejb.LoanBean;
import mbsystem.entity.Loan;
import mbsystem.entity.Users;
import mbsystem.qualifier.LoggedIn;
import mbsystem.util.JsfUtil;
import mbsystem.util.PageNavigation;
import mbsystem.util.PaginationHelper;

/**
 *
 * @author Di
 */
@Named(value = "loanController")
@SessionScoped
public class LoanController implements Serializable {

    private static final Logger LOG = Logger.getLogger(LoanController.class.getName());

    @EJB
    private LoanBean ejbFacade;

    @Inject
    @LoggedIn
    private Users user;
    private List<Loan> myLoans;
    private Loan currentLoan;
    private DataModel items = null;
    private PaginationHelper pagination;
    private String searchString;

    /**
     * Constructor
     */
    public LoanController() {
    }

    /**
     * @return the currentLoan Construct a new one if not already exists
     */
    public Loan getSelectedLoan() {
        if (currentLoan == null) {
            currentLoan = new Loan();
        }
        return currentLoan;
    }

    /**
     * @return the pagination
     */
    public PaginationHelper getPagination() {
        if (pagination == null) {
            // Diplay 10 items per page
            pagination = new PaginationHelper(10) {
                @Override
                public int getItemsCount() {
                    return ejbFacade.count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(ejbFacade.findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    /**
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
     * @return the View page
     */
    public PageNavigation prepareView() {
        // Call getItems() method to find the current comment
        currentLoan = (Loan) getItems().getRowData();
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
     * @return the Edit page
     */
    public PageNavigation prepareEdit() {
        currentLoan = (Loan) getItems().getRowData();
        return PageNavigation.EDIT;
    }

    /**
     * Update currentLoan
     *
     * @return the View page if successful or the Edit page if not
     */
    public PageNavigation update() {
        try {
            LOG.log(Level.INFO, "Updating loan ID:{0}", currentLoan.getId());
            // Call the isValidDate(String dateString) method to validate the input for dueDate
            if (isValidDate(currentLoan.getDueDate())) {
                ejbFacade.update(currentLoan);
                JsfUtil.addSuccessMessage("Loan Updated");
                return PageNavigation.VIEW;
            }
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error Occured");
            return null;
        }
    }

    // Validate date input before update
    private boolean isValidDate(String dateString) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy hh:mm:ss");
        try {
            DateTime date = DateTime.parse(dateString, dtf);
            return true;
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Invalid Date - enter dd/MM/yyyy hh:mm:ss.");
            return false;
        }
    }

    /**
     * Delete current loan (triggered on List page)
     *
     * @return the List page
     */
    public PageNavigation destroy() {
        // Call getItems() method to select the current loan
        currentLoan = (Loan) getItems().getRowData();
        // Call performDestroy() method to delete current loan
        performDestroy();
        // Call the recreateModel() method to deselect the item
        recreateModel();
        // Return the List page at last
        return PageNavigation.LIST;
    }

    /**
     * Delete current loan (triggered on View page)
     *
     * @return the List page
     */
    public PageNavigation destroyAndView() {
        performDestroy();
        recreateModel();
        return PageNavigation.LIST;
    }

    // the performDestroy() method - delete current loan and indicate the result
    private void performDestroy() {
        try {
            ejbFacade.delete(currentLoan);
            JsfUtil.addSuccessMessage("Loan Deleted");
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error Occured");
        }
    }

    /**
     * @return the user's loans
     */
    public List<Loan> getMyLoans() {
        if (user != null) {
            myLoans = ejbFacade.getLoanByPatronEmail(user.getEmail());
            if (myLoans.isEmpty()) {
                LOG.log(Level.FINEST, "Patron {0} has no loans to display.", user.getEmail());
                return null;
            } else {
                return myLoans;
            }
        } else {
            JsfUtil.addErrorMessage("Current user is not authenticated.");
            return null;
        }
    }

    /**
     * @return the searchString
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * @param searchString the searchString to set
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

}
