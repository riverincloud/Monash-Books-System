/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.ejb;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mbsystem.entity.Category;

/**
 *
 * @author Di
 */
@Stateless
@LocalBean
public class CategoryBean extends AbstractFacade<Category> {

    @PersistenceContext(unitName = "MBSystem-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoryBean() {
        super(Category.class);
    }

    /**
     * Find a category with its category (name)
     *
     * @param category
     * @return the category (object)
     */
    public Category getCategoryByCategory(String category) {
        Query createNamedQuery = getEntityManager().createNamedQuery("Category.findByCategory");
        createNamedQuery.setParameter("category", category);
        if (createNamedQuery.getResultList().size() > 0) {
            return (Category) createNamedQuery.getSingleResult();
        } else {
            return null;
        }
    }

}
