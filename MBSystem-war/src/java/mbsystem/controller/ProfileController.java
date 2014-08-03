/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import mbsystem.ejb.BookBean;
import mbsystem.ejb.CommentBean;
import mbsystem.ejb.LoanBean;

import mbsystem.ejb.UsersBean;
import mbsystem.entity.Book;
import mbsystem.entity.Comment;
import mbsystem.entity.Loan;
import mbsystem.entity.Patron;
import mbsystem.entity.Users;
import mbsystem.util.JsfUtil;

/**
 *
 * @author Di
 */
@Named(value = "profileController")
@SessionScoped
public class ProfileController implements Serializable {

    @EJB
    BookBean bb;
    @EJB
    CommentBean cb;
    @EJB
    LoanBean lb;
    @EJB
    UsersBean ub;

    @Inject
    private UserController userController;
    @Inject
    private LoanController loanController;
    @Inject
    private CommentController commentController;

    private Patron currentPatron;
    private List<Book> myBookmarks;
    private List<Loan> myLoans;
    private List<Comment> myComments;
    private String bookmarkMessage;
    private String loanMessage;
    private String commentMessage;

    /**
     * Constructor
     */
    public ProfileController() {
    }

    // Getters and setters
    public Patron getCurrentPatron() {
        return currentPatron;
    }

    public void setCurrentPatron(Patron currentPatron) {
        this.currentPatron = currentPatron;
    }

    public List<Book> getMyBookmarks() {
        return myBookmarks;
    }

    public void setMyBookmarks(List<Book> myBookmarks) {
        this.myBookmarks = myBookmarks;
    }

    public List<Loan> getMyLoans() {
        return myLoans;
    }

    public void setMyLoans(List<Loan> myLoans) {
        this.myLoans = myLoans;
    }

    public List<Comment> getMyComments() {
        return myComments;
    }

    public void setMyComments(List<Comment> myComments) {
        this.myComments = myComments;
    }

    public String getBookmarkMessage() {
        return bookmarkMessage;
    }

    public void setBookmarkMessage(String bookmarkMessage) {
        this.bookmarkMessage = bookmarkMessage;
    }

    public String getLoanMessage() {
        return loanMessage;
    }

    public void setLoanMessage(String loanMessage) {
        this.loanMessage = loanMessage;
    }

    public String getCommentMessage() {
        return commentMessage;
    }

    public void setCommentMessage(String commentMessage) {
        this.commentMessage = commentMessage;
    }

    /**
     * Display the current patron's profile
     *
     * @return the profile page
     */
    public String displayProfile() {
        // check if the user is a patron
        if (userController.isPatron()) {
            // get the authenticated user on the userController
            Users authedUser = userController.getAuthedUser();
            // retreive the current patron from database, with the user's email
            currentPatron = ub.getPatronByEmail(authedUser.getEmail());
        }

        // check if the current patron has any loans
        if (currentPatron.getLoanList().isEmpty()) {
            loanMessage = "You do not have any current loans.";
        } else {
            myLoans = new ArrayList<>();
            // important - retrive the patron's loans with loanController instead of "currentPatron.getLoanList"
            // this ensures that any changes made at the back (e.g. administrator deleted a loan) would be reflected immediately here
            myLoans = loanController.getMyLoans();
        }

        myBookmarks = new ArrayList<>();
        myBookmarks = currentPatron.getBookmarkList();
        // check if the current patron has any bookmarks
        if (myBookmarks.isEmpty()) {
            bookmarkMessage = "You do not have any bookmarks.";
        }

        // check if the current patron has any comments
        if (currentPatron.getCommentList().isEmpty()) {
            commentMessage = "You do not have any current comments.";
        } else {
            myComments = new ArrayList<>();
            // important - retrive the patron's comments with commentController instead of "currentPatron.getCommentList"
            // this ensures that any changes made at the back (e.g. administrator deleted a comment) would be reflected immediately here
            myComments = commentController.getMyComments();
        }

        // at last return the page
        return "/profile";
    }

    /**
     * Return the selected book
     */
    public void returnBook() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        String id = params.get("id");
        for (int i = 0; i < myLoans.size(); i++) {
            if (myLoans.get(i).getId().toString().equals(id)) {
                // get the selected loan
                Loan selectedLoan = myLoans.get(i);
                // get the associated book
                Book selectedBook = selectedLoan.getBook();
                // set book available
                selectedBook.setAvailable(true);
                // set loan returned
                selectedLoan.setReturned(true);
                // write to database
                try {
                    bb.update(selectedBook);
                    lb.update(selectedLoan);
                    JsfUtil.addSuccessMessage("Loan and Book Updated");
                } catch (Exception e) {
                    JsfUtil.addErrorMessage(e, "Persistence Error Occured");
                }
            }
        }
    }

    /**
     * Remove the selected bookmark
     */
    public void removeBookmark() {
        // Find current book
        Book selectedBook = findSelectedBook();
        // If the current patron is able to borrow the current book
        if (selectedBook != null) {
            // remove the book form bookmark list
            myBookmarks.remove(selectedBook);
            currentPatron.setBookmarkList(myBookmarks);
            // Write to database
            try {
                ub.update(currentPatron);
                JsfUtil.addSuccessMessage("Patron Updated");
            } catch (Exception e) {
                JsfUtil.addErrorMessage(e, "Persistence Error Occured");
            }
        }
    }

    /**
     * Remove selected comment
     */
    public void removeComment() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        String id = params.get("id");
        for (int i = 0; i < myComments.size(); i++) {
            if (myComments.get(i).getId().toString().equals(id)) {
                // get the selected comment
                Comment selectedComment = myComments.get(i);
                // remove it from my comments
                myComments.remove(selectedComment);
                // get the associated book
                Book selectedBook = selectedComment.getBook();
                // remove this comment from the book
                selectedBook.getCommentList().remove(selectedComment);
                // update in database
                try {
                    cb.delete(selectedComment);
                    bb.update(selectedBook);
                    JsfUtil.addSuccessMessage("Comment Deleted");
                } catch (Exception e) {
                    JsfUtil.addErrorMessage(e, "Persistence Error Occured");
                }
            }
        }
    }

    // Find current book
    private Book findSelectedBook() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        String id = params.get("id");
        List<Book> allBooks = bb.findAll();
        for (int i = 0; i < allBooks.size(); i++) {
            if (allBooks.get(i).getId().toString().equals(id)) {
                Book book = allBooks.get(i);
                return book;
            }
        }
        return null;
    }

}
