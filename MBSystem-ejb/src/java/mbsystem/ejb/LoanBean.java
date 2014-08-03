/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.ejb;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mbsystem.entity.Loan;

/**
 *
 * @author Di
 */
@Stateless
@LocalBean
public class LoanBean extends AbstractFacade<Loan> {

    @PersistenceContext(unitName = "MBSystem-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LoanBean() {
        super(Loan.class);
    }

    /**
     * Find a loan with its id
     *
     * @param id
     * @return the loan
     */
    public Loan getLoanById(Integer id) {
        Query createNamedQuery = getEntityManager().createNamedQuery("Loan.findById");
        createNamedQuery.setParameter("id", id);
        return (Loan) createNamedQuery.getSingleResult();
    }

    /**
     * Find a patron's loan list with its email
     *
     * @param email
     * @return the loan list
     */
    public List<Loan> getLoanByPatronEmail(String email) {
        Query createNamedQuery = getEntityManager().createNamedQuery("Loan.findByPatronEmail");
        createNamedQuery.setParameter("email", email);
        return createNamedQuery.getResultList();
    }

}
