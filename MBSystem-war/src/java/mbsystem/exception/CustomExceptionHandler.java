/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import mbsystem.util.JsfUtil;

/**
 *
 * @author Di
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {

    private static final Logger log = Logger.getLogger(CustomExceptionHandler.class.getCanonicalName());
    private final ExceptionHandler wrapped;

    CustomExceptionHandler(ExceptionHandler exception) {
        this.wrapped = exception;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    @Override
    public void handle() throws FacesException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
        while (i.hasNext()) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context
                    = (ExceptionQueuedEventContext) event.getSource();

            // Get the exception from context
            Throwable t = context.getException();
            final FacesContext fc = FacesContext.getCurrentInstance();
            final Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
            final NavigationHandler nav = fc.getApplication().getNavigationHandler();

            // Do something with the exception
            try {
                // log error
                log.log(Level.INFO, "Custom Exception Handler", t);

                // redirect to error page
                requestMap.put("exceptionMessage", t.getMessage());
                t.printStackTrace(pw);
                requestMap.put("exceptionTrace", sw.toString());
                nav.handleNavigation(fc, null, "/error");
                fc.renderResponse();

                // report the error in a jsf error message
                JsfUtil.addErrorMessage(t.getMessage());

            } finally {
                //remove it from queue
                i.remove();
            }
        }
        //parent hanle
        getWrapped().handle();
    }
}
