/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.ejb;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mbsystem.entity.Comment;

/**
 *
 * @author Di
 */
@Stateless
@LocalBean
public class CommentBean extends AbstractFacade<Comment> {

    @PersistenceContext(unitName = "MBSystem-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CommentBean() {
        super(Comment.class);
    }

    /**
     * Find a comment with its id
     *
     * @param id
     * @return the comment
     */
    public Comment getCommentById(Integer id) {
        Query createNamedQuery = getEntityManager().createNamedQuery("Comment.findById");
        createNamedQuery.setParameter("id", id);
        return (Comment) createNamedQuery.getSingleResult();
    }

    /**
     * Find a patron's comment list with its email
     *
     * @param email
     * @return the comment list
     */
    public List<Comment> getCommentByPatronEmail(String email) {
        Query createNamedQuery = getEntityManager().createNamedQuery("Comment.findByPatronEmail");
        createNamedQuery.setParameter("email", email);
        return createNamedQuery.getResultList();
    }

}
