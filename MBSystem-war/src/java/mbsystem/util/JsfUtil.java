/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.util;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 *
 * @author Di
 */
public final class JsfUtil {

    private JsfUtil() {
    }

    /**
     * Get the list of options available when selecting one or multiple objects
     * (e.g. when using h:selectManyCheckbox to select categories for a book)
     *
     * @param entities
     * @param selectOne
     * @return the array of selectItem
     */
    public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "---");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }

    /**
     *
     * @param ex
     * @param defaultMsg
     */
    public static void addErrorMessage(Exception ex, String defaultMsg) {
        String msg = ex.getLocalizedMessage();
        if (msg != null && msg.length() > 0) {
            addErrorMessage(msg);
        } else {
            addErrorMessage(defaultMsg);
        }
    }

    /**
     *
     * @param messages
     */
    public static void addErrorMessages(List<String> messages) {
        for (String message : messages) {
            addErrorMessage(message);
        }
    }

    /**
     *
     * @param msg
     */
    public static void addErrorMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    /**
     *
     * @param context
     * @param msg
     */
    public static void addErrorMessage(FacesContext context, String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
        context.addMessage(null, facesMsg);
    }

    /**
     *
     * @param msg
     */
    public static void addSuccessMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
        // FacesContext.getCurrentInstance().getMessageList().clear();
        FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
    }

    /**
     *
     * @param key
     * @return
     */
//    public static String getRequestParameter(String key) {
//        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
//    }
//    
//    public static Object getObjectFromRequestParameter(String requestParameterName, Converter converter, UIComponent component) {
//        String theId = JsfUtil.getRequestParameter(requestParameterName);
//        return converter.getAsObject(FacesContext.getCurrentInstance(), component, theId);
//    }
}
