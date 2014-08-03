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

/**
 *
 * @author Di
 */
@Stateless
@LocalBean
public class GroupsBean extends AbstractFacade<Groups> {

    @PersistenceContext(unitName = "MBSystem-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GroupsBean() {
        super(Groups.class);
    }

    /**
     * Find a group with its group name
     *
     * @param groupname
     * @return the group
     */
    public Groups getGroupByName(String groupname) {
        Query createNamedQuery = getEntityManager().createNamedQuery("Groups.findByName");
        createNamedQuery.setParameter("groupname", groupname);
        if (createNamedQuery.getResultList().size() > 0) {
            return (Groups) createNamedQuery.getSingleResult();
        } else {
            return null;
        }
    }

}
