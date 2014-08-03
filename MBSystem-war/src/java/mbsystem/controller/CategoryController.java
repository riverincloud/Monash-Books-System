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
import javax.faces.model.SelectItem;

import mbsystem.ejb.CategoryBean;
import mbsystem.entity.Category;
import mbsystem.util.JsfUtil;
import mbsystem.util.PageNavigation;
import mbsystem.util.PaginationHelper;

/**
 *
 * @author Di
 */
@Named(value = "categoryController")
@SessionScoped
public class CategoryController implements Serializable {

    private static final Logger LOG = Logger.getLogger(CategoryController.class.getName());

    @EJB
    private CategoryBean ejbFacade;

    private Category currentCategory;
    private DataModel items = null;
    private PaginationHelper pagination;

    /**
     * Constructor
     */
    public CategoryController() {
    }

    /**
     *
     * @return the ejbFacade;
     */
    public CategoryBean getFacade() {
        return ejbFacade;
    }

    /**
     * Get the current category, construct a new one if not already exists
     *
     * @return the current category
     */
    public Category getSelectedCategory() {
        if (currentCategory == null) {
            currentCategory = new Category();
        }
        return currentCategory;
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
        currentCategory = (Category) getItems().getRowData();
        return PageNavigation.VIEW; //.getText();
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
        currentCategory = new Category();
        return PageNavigation.CREATE;
    }

    /**
     *
     * @return the Edit page
     */
    public PageNavigation prepareEdit() {
        currentCategory = (Category) getItems().getRowData();
        return PageNavigation.EDIT;
    }

    /**
     * Create a new category
     *
     * @return the View page if successful, otherwise null
     */
    public PageNavigation create() {
        try {
            // confirm the category is not duplicated
            if (!isCategoryDuplicated(currentCategory)) {
                ejbFacade.create(currentCategory);
                JsfUtil.addSuccessMessage("Category Created");
                return PageNavigation.VIEW;
            }
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error Occured");
            return null;
        }
    }

    // Check if the category already exsists
    // Each category should have a unique name (note: a Category object's "category" property holds this value)
    private boolean isCategoryDuplicated(Category c) {
        if (ejbFacade.getCategoryByCategory(c.getCategory()) != null) {
            JsfUtil.addErrorMessage("Duplicated Category Error");
            return true;
        }
        return false;
    }

    /**
     * update current category
     *
     * @return the View page if successful or the Edit page if not
     */
    public PageNavigation update() {
        try {
            LOG.log(Level.INFO, "Updating category ID:{0}", currentCategory.getId());
            ejbFacade.update(currentCategory);
            JsfUtil.addSuccessMessage("Category Updated");
            return PageNavigation.VIEW;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error Occured");
            return null;
        }
    }

    /**
     * Delete current category (triggered on List page)
     *
     * @return the List page
     */
    public PageNavigation destroy() {
        // Call getItems() method to select the current category
        currentCategory = (Category) getItems().getRowData();
        // Call performDestroy() method to delete current category
        performDestroy();
        // Call the recreateModel() method to deselect the item
        recreateModel();
        // Return the List page at last
        return PageNavigation.LIST;
    }

    /**
     * Delete current category (triggered on View page)
     *
     * @return the List page
     */
    public PageNavigation destroyAndView() {
        performDestroy();
        recreateModel();
        return PageNavigation.LIST;
    }

    // the performDestroy() method - delete current category and indicate the result
    private void performDestroy() {
        try {
            ejbFacade.delete(currentCategory);
            JsfUtil.addSuccessMessage("Category Deleted");
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error Occured");
        }
    }

    /**
     * Get an array of SelectItem object, using when selecting categories for a
     * book
     *
     * @return
     */
    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

}
