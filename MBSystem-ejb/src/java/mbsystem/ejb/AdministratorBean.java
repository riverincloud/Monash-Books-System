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
import mbsystem.entity.Administrator;
import mbsystem.entity.Groups;

/**
 *
 * @author Di
 */
@Stateless
@LocalBean
public class AdministratorBean extends AbstractFacade<Administrator> {

    @PersistenceContext(unitName = "MBSystem-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AdministratorBean() {
        super(Administrator.class);
    }

    @Override
    public void create(Administrator admin) {
        Groups adminGroup = (Groups) em.createNamedQuery("Groups.findByName")
                .setParameter("groupname", "ADMINS")
                .getSingleResult();
        admin.getGroupList().add(adminGroup);
        adminGroup.getUserList().add(admin);
        em.persist(admin);
        em.merge(adminGroup);
    }

    @Override
    public void delete(Administrator admin) {
        Groups adminGroup = (Groups) em.createNamedQuery("Groups.findByName")
                .setParameter("groupname", "ADMINS")
                .getSingleResult();
        adminGroup.getUserList().remove(admin);
        em.remove(em.merge(admin));
        em.merge(adminGroup);
    }

}
