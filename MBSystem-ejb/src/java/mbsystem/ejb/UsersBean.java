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
import mbsystem.entity.Groups;
import mbsystem.entity.Patron;
import mbsystem.entity.Users;

/**
 *
 * @author Di
 */
@Stateless
@LocalBean
public class UsersBean extends AbstractFacade<Patron> {

    @PersistenceContext(unitName = "MBSystem-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsersBean() {
        super(Patron.class);
    }

    @Override
    public void create(Patron patron) {
        if (createUser(patron)) {
            Groups userGroup = (Groups) em.createNamedQuery("Groups.findByName")
                    .setParameter("groupname", "USERS")
                    .getSingleResult();
            patron.getGroupList().add(userGroup);
            userGroup.getUserList().add(patron);
            em.persist(patron);
            em.merge(userGroup);
        }
    }

    @Override
    public void delete(Patron patron) {
        Groups userGroup = (Groups) em.createNamedQuery("Groups.findByName")
                .setParameter("groupname", "USERS")
                .getSingleResult();
        userGroup.getUserList().remove(patron);
        em.remove(em.merge(patron));
        em.merge(userGroup);
    }

    /**
     * Create a new user verifying if the user already exists
     *
     * @param patron
     * @return
     */
    public boolean createUser(Patron patron) {
        return getUserByEmail(patron.getEmail()) == null;
    }

    /**
     * Find a user with its email
     *
     * @param email
     * @return the user
     */
    public Users getUserByEmail(String email) {
        Query createNamedQuery = getEntityManager().createNamedQuery("Users.findByEmail");
        createNamedQuery.setParameter("email", email);
        if (createNamedQuery.getResultList().size() > 0) {
            return (Users) createNamedQuery.getSingleResult();
        } else {
            return null;
        }
    }

    /**
     * Find a patron with its email
     *
     * @param email
     * @return the patron
     */
    public Patron getPatronByEmail(String email) {
        Query createNamedQuery = getEntityManager().createNamedQuery("Patron.findByEmail");
        createNamedQuery.setParameter("email", email);
        if (createNamedQuery.getResultList().size() > 0) {
            return (Patron) createNamedQuery.getSingleResult();
        } else {
            return null;
        }
    }
}
