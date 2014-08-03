/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.controller;

import javax.inject.Named;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import mbsystem.ejb.BookBean;
import mbsystem.ejb.CategoryBean;
import mbsystem.entity.Book;
import mbsystem.entity.Category;

/**
 *
 * @author Di
 */
@Named(value = "bookListController")
@SessionScoped
public class BookListController implements Serializable {

    @EJB
    BookBean bb;
    @EJB
    CategoryBean cb;

    private String pageTitle;
    private String listTitle;
    private List<Book> allBooks;
    private List<Category> categoryList;
    private Category selectedCategory;
    private String searchQuery;
    private List<Book> matchedBooks;

    @PostConstruct
    public void init() {
        pageTitle = "MonashBooks|Books";
        allBooks = bb.findAll();
        categoryList = new ArrayList<>();
        categoryList = cb.findAll();
    }

    // Getters and setters
    public String getPageTitle() {
        return pageTitle;
    }

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public List<Book> getAllBooks() {
        return allBooks;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public List<Book> getMatchedBooks() {
        return matchedBooks;
    }

    public void setMatchedBooks(List<Book> matchedBooks) {
        this.matchedBooks = matchedBooks;
    }

    /**
     * Display books in the selected category
     *
     * @return the book list page
     */
    public String displayCategorizedBooks() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        String category = params.get("category");
        listTitle = category;
        matchedBooks = new ArrayList<>();
        // If "All categories" selected, display all books regardless of categories
        if ("All categories".equals(category)) {
            matchedBooks = allBooks;
        } else {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getCategory().equals(category)) {
                    selectedCategory = categoryList.get(i);
                    matchedBooks = selectedCategory.getBookList();
                }
            }
        }
        return "/booklist";
    }

    /**
     * Display the result of a book search
     *
     * @return the book list page
     */
    public String displaySearchedBooks() {
        listTitle = "Matched Books";
        matchedBooks = new ArrayList<>();
        for (Book b : allBooks) {
            // Search by title
            if (b.getTitle().toLowerCase().trim().contains(searchQuery.toLowerCase().trim())) {
                matchedBooks.add(b);
            } // Then search by author
            else if (b.getAuthor().toLowerCase().trim().contains(searchQuery.toLowerCase().trim())) {
                matchedBooks.add(b);
            }
            if (matchedBooks.isEmpty()) {
                listTitle = "No book matched.";
            }
        }
        return "/booklist";
    }

    /**
     * Get a list of latest books
     *
     * @return latest books
     */
    public List<Book> getLatestBooks() {
        List<Book> latestBooks = new ArrayList<>();
        try {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date today = new Date();
            LocalDate localToday = LocalDate.fromDateFields(today);
            for (Book b : allBooks) {
                // the publishDate is stored as String in database
                String pubDateString = b.getPublishDate();
                // parse the string
                Date pubDate = df.parse(pubDateString);
                LocalDate localPubDate = LocalDate.fromDateFields(pubDate);
                // calculate the days since book published
                int publishedDays = Days.daysBetween(localPubDate, localToday).getDays();
                // if published within one year
                if (publishedDays < 365) {
                    latestBooks.add(b);
                }
            }
        } catch (ParseException e) {
            System.out.println("Invalid date format.");
        }
        return latestBooks;
    }

}
