/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mbsystem.entity.Book;
import mbsystem.entity.Category;

/**
 *
 * @author Di
 */
@Stateless
@LocalBean
public class BookBean extends AbstractFacade<Book> {

    private static final Logger logger
            = Logger.getLogger(BookBean.class.getCanonicalName());

    @PersistenceContext(unitName = "MBSystem-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BookBean() {
        super(Book.class);
    }

}
