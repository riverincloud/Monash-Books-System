/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import mbsystem.controller.CategoryController;
import mbsystem.entity.Category;

/**
 *
 * @author Di
 */
@FacesConverter(value = "categoryConverter")
public class CategoryConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        CategoryController controller = (CategoryController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "categoryController");

        return controller.getFacade().getCategoryByCategory(value);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Category) {
            return ((Category) object).getCategory();
        } else {
            throw new IllegalArgumentException("object " + object
                    + " is of type " + object.getClass().getName()
                    + "; expected type: " + CategoryController.class.getName());
        }
    }
}
