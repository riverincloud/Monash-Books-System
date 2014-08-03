/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Di
 */
@Entity
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BOOK_ID")
    private Integer id;
    @NotNull(message = "Title field required.")
    private String title;
    @NotNull(message = "Author field required.")
    private String author;
    @NotNull(message = "Publisher field required.")
    private String publisher;
    @NotNull(message = "Publish Date field required.")
    private String publishDate;
    @NotNull(message = "ISBN field required.")
    @Size(min = 9, message = "ISBN must be at least 9 digits long.")
    private String isbn;
    @NotNull(message = "Description field required.")
    @Size(min = 10, max = 2000, message = "Description must be 10 to 2000 characters long.")
    private String description;
    @NotNull(message = "Image Url field required.")
    private String imageUrl;
    @NotNull(message = "Available field required.")
    private boolean available;

    @ManyToMany(mappedBy = "bookList")
    private List<Category> categoryList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book")
    private List<Loan> loanList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book")
    private List<Comment> commentList = new ArrayList<>();

    /**
     * Constructors
     */
    public Book() {
    }

    public Book(String title, String author, String publisher, String publishDate,
            String isbn, String description, String imageUrl, boolean available) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.isbn = isbn;
        this.description = description;
        this.imageUrl = imageUrl;
        this.available = available;
    }

    public Book(String title, String author, String publisher, String publishDate, String isbn,
            String description, String imageUrl, boolean available, List<Category> categoryList) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.isbn = isbn;
        this.description = description;
        this.imageUrl = imageUrl;
        this.available = available;
        this.categoryList = categoryList;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
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
        if (!(object instanceof Book)) {
            return false;
        }
        Book other = (Book) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return title + " (" + isbn + ")";
    }

    /**
     * For displaying its category list in String
     *
     * @return the string
     */
    public String categoryListToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < categoryList.size(); i++) {
            sb.append(categoryList.get(i).getCategory()).append(". ");
        }
        return sb.toString();
    }

}
