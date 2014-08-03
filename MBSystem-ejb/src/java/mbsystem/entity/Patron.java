/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Di
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Patron.findAll", query = "SELECT p FROM Patron p"),
    @NamedQuery(name = "Patron.findById", query = "SELECT p FROM Patron p WHERE p.id = :id"),
    @NamedQuery(name = "Patron.findByEmail", query = "SELECT p FROM Patron p WHERE p.email = :email")})

public class Patron extends Users {

    private static final long serialVersionUID = 1L;

    @ManyToMany
    @JoinTable(name = "BOOKMARK",
            joinColumns = @JoinColumn(name = "patron_fk"),
            inverseJoinColumns = @JoinColumn(name = "book_fk"))
    private List<Book> bookmarkList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patron")
    private List<Loan> loanList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patron")
    private List<Comment> commentList = new ArrayList<>();

    /**
     * Constructors
     */
    public Patron() {
    }

    public Patron(String email, String name, String mobile) {
        super(email, name, mobile);
    }

    // Getters and setters

    public List<Book> getBookmarkList() {
        return bookmarkList;
    }

    public void setBookmarkList(List<Book> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    public List<Loan> getLoanList() {
        return loanList;
    }

    public void setLoanList(List<Loan> loanList) {
        this.loanList = loanList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Patron)) {
            return false;
        }
        Patron other = (Patron) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }

}
