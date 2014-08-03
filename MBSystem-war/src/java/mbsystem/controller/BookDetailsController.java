/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mbsystem.controller;

import javax.inject.Named;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
@Named(value = "bookDetailsController")
@SessionScoped
public class BookDetailsController implements Serializable {

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
    
    private String pageTitle;
    private Book currentBook;
    private Patron currentPatron;
    private Comment newComment;
    private String newReview = "";
    
    @PostConstruct
    public void init(){
        pageTitle = "MonashBooks|Book Details";
    }
    
    // Getters and setters
    
    public String getPageTitle() {
        return pageTitle;
    }
    
    public Book getCurrentBook() {
        return currentBook;
    }

    public void setCurrentBook(Book currentBook) {
        this.currentBook = currentBook;
    }
    
    public Patron getCurrentPatron() {
        return currentPatron;
    }

    public void setCurrentPatron(Patron currentPatron) {
        this.currentPatron = currentPatron;
    }

    public Comment getNewComment() {
        return newComment;
    }

    public void setNewComment(Comment newComment) {
        this.newComment = newComment;
    }

    public String getNewReview() {
        return newReview;
    }

    public void setNewReview(String newReview) {
        this.newReview = newReview;
    }
    
    /**
     * Display details of a book
     * @return book details page if current book exists, otherwise null
     */
    public String displayBookDetails() {
        // Find current book
        currentBook = findCurrentBook();
        if (currentBook != null) {
            pageTitle = "Book|"+currentBook.getTitle();
            return "/bookdetails";
        }
        return null;
    }
    
    /**
     * Borrow the current book
     */
    public void borrowBook() {
        // Find current book
        currentBook = findCurrentBook();        
        // Find current patron
        currentPatron = findCurrentPatron();
        // If the current patron is able to borrow the current book
        if(currentBook!=null && currentPatron!=null) {
            // Calculate current date and due date    
            DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy hh:mm:ss");
            DateTime now = new DateTime();
            DateTime added = now.plusDays(14);
            String currentDate = now.toString(dtf);
            String dueDate = added.toString(dtf);
            // Setup a new loan
            Loan newLoan = new Loan();
            newLoan.setPatron(currentPatron);
            newLoan.setBook(currentBook);
            newLoan.setLoanDate(currentDate);
            newLoan.setDueDate(dueDate);
            // Write to database
            try {
                lb.create(newLoan);
                currentBook.setAvailable(false);
                currentBook.getLoanList().add(newLoan);
                currentPatron.getLoanList().add(newLoan);
                bb.update(currentBook);
                ub.update(currentPatron);                
                JsfUtil.addSuccessMessage("Loan Created");
            } catch (Exception e) {
                JsfUtil.addErrorMessage(e, "Persistence Error Occured");
            }
        }
    }
     
    /**
     * Book mark the current book
     */
    public void addBookmark() {
        // Find current book
        currentBook = findCurrentBook();        
        // Find current patron
        currentPatron = findCurrentPatron();
        // If the current patron is able to borrow the current book
        if(currentBook!=null && currentPatron!=null && !isBookmarked(currentBook)) {
            // Bookmark the book
            currentPatron.getBookmarkList().add(currentBook);
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
     * Post comment to current book
     */
    public void postComment() {
        // Find current book
        currentBook = findCurrentBook();        
        // Find current patron
        currentPatron = findCurrentPatron();
        // If able to post comment
        if(currentBook!=null && currentPatron!=null && newReview!="" ) {
            // Get current date time
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date date = new Date();
            String currentDate = df.format(date);
            // Setup a new comment            
            newComment = new Comment();
            newComment.setPatron(currentPatron);
            newComment.setBook(currentBook);
            newComment.setCommentDate(currentDate);
            newComment.setReview(newReview);       
            // Write to database
            try {
                cb.create(newComment);
                currentBook.getCommentList().add(newComment);
                currentPatron.getCommentList().add(newComment);
                bb.update(currentBook);
                ub.update(currentPatron);     
                JsfUtil.addSuccessMessage("Comment Created");
                // clear the review field
                newReview = "";
            } catch (Exception e) {
                JsfUtil.addErrorMessage(e, "Persistence Error Occured");
            }
        }
    }
    
    // Find current book
    private Book findCurrentBook() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        String id = params.get("id");
        List<Book> allBooks = bb.findAll();
        for(int i = 0; i < allBooks.size(); i++) {
            if(allBooks.get(i).getId().toString().equals(id)) {
                Book book = allBooks.get(i);
                return book;
            }
        }
        return null;
    }
    
    // Find current patron
    private Patron findCurrentPatron() {
        if (userController.isPatron()) {
            Users user = userController.getUser();
            Patron patron = ub.getPatronByEmail(user.getEmail());
            return patron;
        }
        return null;
    }    
    
    // Check wheather this book is already bookmarked by current patron
    // Although the patron can keep clicking the "Bookmark" button, it would be ignored if this method returns true.
    public boolean isBookmarked(Book book) {
        if (getCurrentPatron() != null) {
            for (Book b : currentPatron.getBookmarkList()) {
                if (b.getId().equals(book.getId())) {
                    return true;
                }
            }
        }
        return false;
    }
    
}