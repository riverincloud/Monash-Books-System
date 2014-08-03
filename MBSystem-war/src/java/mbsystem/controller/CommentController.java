/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;

import mbsystem.ejb.CommentBean;
import mbsystem.entity.Comment;
import mbsystem.entity.Users;
import mbsystem.qualifier.LoggedIn;
import mbsystem.util.JsfUtil;
import mbsystem.util.PageNavigation;
import mbsystem.util.PaginationHelper;

/**
 *
 * @author Di
 */
@Named(value = "commentController")
@SessionScoped
public class CommentController implements Serializable {

    private static final Logger LOG = Logger.getLogger(CommentController.class.getName());

    @EJB
    private CommentBean ejbFacade;

    @Inject
    @LoggedIn
    private Users user;
    private List<Comment> myComments;
    private Comment currentComment;
    private DataModel items = null;
    private PaginationHelper pagination;
    private String searchString;

    /**
     * Constructor
     */
    public CommentController() {
    }

    /**
     * @return the currentComment Construct a new one if not already exists
     */
    public Comment getSelectedComment() {
        if (currentComment == null) {
            currentComment = new Comment();
        }
        return currentComment;
    }

    /**
     * @return the pagination
     */
    public PaginationHelper getPagination() {
        if (pagination == null) {
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
        currentComment = (Comment) getItems().getRowData();
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
     * Delete current comment (triggered on List page)
     *
     * @return the List page
     */
    public PageNavigation destroy() {
        // Call getItems() method to select the current comment
        currentComment = (Comment) getItems().getRowData();
        // Call performDestroy() method to delete current comment
        performDestroy();
        // Call the recreateModel() method to deselect the item
        recreateModel();
        // Return the List page at last
        return PageNavigation.LIST;
    }

    /**
     * Delete current comment (triggered on View page)
     *
     * @return the List page
     */
    public PageNavigation destroyAndView() {
        performDestroy();
        recreateModel();
        return PageNavigation.LIST;
    }

    // the performDestroy() method - delete current comment and indicate the result
    private void performDestroy() {
        try {
            ejbFacade.delete(currentComment);
            JsfUtil.addSuccessMessage("Comment Deleted");
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error Occured");
        }
    }

    /**
     * @return the user's comments
     */
    public List<Comment> getMyComments() {
        if (user != null) {
            myComments = ejbFacade.getCommentByPatronEmail(user.getEmail());
            if (myComments.isEmpty()) {
                LOG.log(Level.FINEST, "Patron {0} has no comments to display.", user.getEmail());
                return null;
            } else {
                return myComments;
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
