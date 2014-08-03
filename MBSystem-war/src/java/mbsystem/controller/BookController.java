/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import mbsystem.ejb.BookBean;
import mbsystem.entity.Book;
import mbsystem.entity.Category;
import mbsystem.util.JsfUtil;
import mbsystem.util.PageNavigation;
import mbsystem.util.PaginationHelper;

/**
 *
 * @author Di
 */
@Named(value = "bookController")
@SessionScoped
public class BookController implements Serializable {

    private final static Logger LOG = Logger.getLogger(BookController.class.getName());

    @EJB
    private BookBean ejbFacade;

    private Book currentBook;
    private DataModel items = null;
    private PaginationHelper pagination;
    private Category[] categoryArray; // holds the value for the <h:selectManyDropbox /> for selecting categories on Creat and Edit page

    /**
     * Constructor
     */
    public BookController() {
    }

    /**
     * Get the current book, construct a new one if not already exists
     *
     * @return the current book
     */
    public Book getSelectedBook() {
        if (currentBook == null) {
            currentBook = new Book();
        }
        return currentBook;
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

    public Category[] getCategoryArray() {
        return categoryArray;
    }

    public void setCategoryArray(Category[] categoryArray) {
        this.categoryArray = categoryArray;
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
        currentBook = (Book) getItems().getRowData();
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
        currentBook = new Book();
        return PageNavigation.CREATE;
    }

    /**
     *
     * @return the Edit page
     */
    public PageNavigation prepareEdit() {
        currentBook = (Book) getItems().getRowData();
        return PageNavigation.EDIT;
    }

    /**
     * Create a new book
     *
     * @return the View page if successful, otherwise null
     */
    public PageNavigation create() {
        try {
            // confirm the publishDate input is valid
            if (isValidDate(currentBook.getPublishDate())) {
                ArrayList<Category> categoryList = new ArrayList<>(Arrays.asList(categoryArray));
                currentBook.setCategoryList(categoryList);
                ejbFacade.create(currentBook);
                JsfUtil.addSuccessMessage("Book Created");
                return PageNavigation.VIEW;
            }
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error Occured");
            return null;
        }
    }

    /**
     * update current book
     *
     * @return the View page if successful or the Edit page if not
     */
    public PageNavigation update() {
        try {
            LOG.log(Level.INFO, "Updating book ID:{0}", currentBook.getId());
            // confirm the publishDate input is valid
            if (isValidDate(currentBook.getPublishDate())) {
                ArrayList<Category> categoryList = new ArrayList<>(Arrays.asList(categoryArray));
                currentBook.setCategoryList(categoryList);
                ejbFacade.update(currentBook);
                JsfUtil.addSuccessMessage("Book Updated");
                return PageNavigation.VIEW;
            }
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error Occured");
            return null;
        }
    }

    // Validate the input for publish date
    private boolean isValidDate(String dateString) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
        try {
            DateTime date = DateTime.parse(dateString, dtf);
            if (date.isAfterNow()) {
                JsfUtil.addErrorMessage("Publish Date must be a past date.");
                return false;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Invalid Date - enter dd/mm/yyyy.");
            return false;
        }
        return true;
    }

    /**
     * Delete current book (triggered on List page)
     *
     * @return the List page
     */
    public PageNavigation destroy() {
        // Call getItems() method to select the current book
        currentBook = (Book) getItems().getRowData();
        // Call performDestroy() method to delete current book
        performDestroy();
        // Call the recreateModel() method to deselect the item
        recreateModel();
        // Return the List page at last
        return PageNavigation.LIST;
    }

    /**
     * Delete current book (triggered on View page)
     *
     * @return the List page
     */
    public PageNavigation destroyAndView() {
        performDestroy();
        recreateModel();
        return PageNavigation.LIST;
    }

    // the performDestroy() method - delete current book and indicate the result
    private void performDestroy() {
        try {
            ejbFacade.delete(currentBook);
            JsfUtil.addSuccessMessage("Book Deleted");
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error Occured");
        }
    }

}
