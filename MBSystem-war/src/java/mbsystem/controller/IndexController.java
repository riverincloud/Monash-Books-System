/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import mbsystem.ejb.AdministratorBean;
import mbsystem.ejb.BookBean;
import mbsystem.ejb.CategoryBean;
import mbsystem.ejb.GroupsBean;
import mbsystem.ejb.UsersBean;
import mbsystem.entity.Administrator;
import mbsystem.entity.Book;
import mbsystem.entity.Category;
import mbsystem.entity.Groups;
import mbsystem.entity.Patron;

/**
 *
 * @author Di
 */
@Named(value = "indexController")
@ApplicationScoped
public class IndexController {

    @EJB
    AdministratorBean ab;
    @EJB
    BookBean bb;
    @EJB
    CategoryBean cb;
    @EJB
    GroupsBean gb;
    @EJB
    UsersBean ub;

    private String pageTitle;

    @PostConstruct
    public void init() {
        pageTitle = "Monash Books | Home";

        // Create dummy admin user
        Administrator admin1 = new Administrator();
        admin1.setEmail("dzha73@student.monash.edu");
        admin1.setName("Di Zhang");
        admin1.setMobile("0434581450");
        admin1.setPassword("25f43b1486ad95a1398e3eeb3d83bc4010015fcc9bedb35b432e00298d5021f7"); // password is "admin1"

        // Create dummy patron
        Patron patron1 = new Patron();
        patron1.setEmail("di.zhang.com@gmail.com");
        patron1.setName("Di Zhang");
        patron1.setMobile("0434581450");
        patron1.setPassword("5329c37bed7f7d5d806b8c8270e4823b7697ccebc0e0c1e880af0268385ee7c3"); // password is "patron1"

        // Create the ADMINS group and USERS group
        Groups adminGroup = new Groups();
        adminGroup.setGroupname("ADMINS");
        Groups userGroup = new Groups();
        userGroup.setGroupname("USERS");

        // Write groups to database
        gb.create(adminGroup);
        gb.create(userGroup);

        // Ensure entities are stored in each side of the relationship
        admin1.getGroupList().add(adminGroup);
        patron1.getGroupList().add(userGroup);

        // Write users to database
        ab.create(admin1);
        ub.create(patron1);

        // Create dummy books
        // 1
        Book book1 = new Book();
        book1.setTitle("Who Will Run The Frog Hospital");
        book1.setAuthor("Lorrie Moore");
        book1.setPublisher("Vintage");
        String pubDate1 = "13/04/2004";
        book1.setPublishDate(pubDate1);
        book1.setIsbn("9787403180669");
        book1.setDescription("The novel addresses both adolescence and middle age through the eyes of Berie, "
                + "a girl from upstate New York. Moore uses memory as a narrative tool, "
                + "inviting the reader to follow Berie's recollections.");
        book1.setImageUrl("/MBSystem-war/resources/img/b1.jpg");
        book1.setAvailable(true);

        // 2
        Book book2 = new Book();
        book2.setTitle("The Art Of Immersion");
        book2.setAuthor("Frank Rose");
        book2.setPublisher("W. W. Norton & Company");
        String pubDate2 = "11/02/2014";
        book2.setPublishDate(pubDate2);
        book2.setIsbn("9781288218821");
        book2.setDescription("Frank Rose reminds us that stories are culturally significant and "
                + "that they way we tell them says a great deal about our societies and ourselves.");
        book2.setImageUrl("/MBSystem-war/resources/img/b2.jpg");
        book2.setAvailable(true);

        // 3
        Book book3 = new Book();
        book3.setTitle("Wall Street");
        book3.setAuthor("Steve Fraser");
        book3.setPublisher("Yale University Press");
        String pubDate3 = "14/04/2009";
        book3.setPublishDate(pubDate3);
        book3.setIsbn("9780877357735");
        book3.setDescription("Wall Street: no other place on earth is so singularly identified with money and the power of money. "
                + "And no other American institution has inspired such deep moral, cultural, and political ambivalence.");
        book3.setImageUrl("/MBSystem-war/resources/img/b3.jpg");
        book3.setAvailable(true);

        // 4
        Book book4 = new Book();
        book4.setTitle("Wide Awake");
        book4.setAuthor("Patricia Morrisroe");
        book4.setPublisher("Spiegel & Grau");
        String pubDate4 = "04/05/2014";
        book4.setPublishDate(pubDate4);
        book4.setIsbn("9780406024060");
        book4.setDescription("Wide Awake is the eye-opening account of Morrisroe’s quest — "
                + "a compelling memoir that blends science, culture, and business to tell the story of "
                + "why she — and 40 million other Americans — can’t sleep at night.");
        book4.setImageUrl("/MBSystem-war/resources/img/b4.jpg");
        book4.setAvailable(true);

        // 5
        Book book5 = new Book();
        book5.setTitle("In The Place Of Justice");
        book5.setAuthor("Wilbert Rideau");
        book5.setPublisher("Knopf");
        String pubDate5 = "27/04/2014";
        book5.setPublishDate(pubDate5);
        book5.setIsbn("9781474757653");
        book5.setDescription("Wilbert Rideau's painfully candid memoir describes his decades in Louisiana's notorious Angola prison.");
        book5.setImageUrl("/MBSystem-war/resources/img/b5.jpg");
        book5.setAvailable(true);

        // 6
        Book book6 = new Book();
        book6.setTitle("A Tenth Of A Second");
        book6.setAuthor("Jimena Canales");
        book6.setPublisher("University Of Chicago Press");
        String pubDate6 = "15/01/2010";
        book6.setPublishDate(pubDate6);
        book6.setIsbn("9780062594839");
        book6.setDescription("A Tenth of a Second sheds new light on modernity and illuminates the work "
                + "of important thinkers of the last two centuries.");
        book6.setImageUrl("/MBSystem-war/resources/img/b6.jpg");
        book6.setAvailable(true);

        // 7
        Book book7 = new Book();
        book7.setTitle("Eight Weeks Of Bruce");
        book7.setAuthor("Maya Contreras");
        book7.setPublisher("Vic & Olivier Publishing");
        String pubDate7 = "18/11/2009";
        book7.setPublishDate(pubDate7);
        book7.setIsbn("9780311765801");
        book7.setDescription("Eight Weeks of Bruce is the second book by New York writer Maya Contreras, "
                + "exploring her relationship with a full-time alcoholic.");
        book7.setImageUrl("/MBSystem-war/resources/img/b7.jpg");
        book7.setAvailable(true);

        // 8
        Book book8 = new Book();
        book8.setTitle("An Experiment In Love");
        book8.setAuthor("Hilary Mantel");
        book8.setPublisher("Picador");
        String pubDate8 = "06/12/2007";
        book8.setPublishDate(pubDate8);
        book8.setIsbn("9780069080118");
        book8.setDescription("Following 'A Change in Climate', this brilliant novel from the "
                + "double Man Booker prize-winning author of 'Wolf Hall' "
                + "is a coming-of-age tale set in Seventies London");
        book8.setImageUrl("/MBSystem-war/resources/img/b8.jpg");
        book8.setAvailable(true);

        // 9
        Book book9 = new Book();
        book9.setTitle("Spring, Heat, Rains");
        book9.setAuthor("David Shulman");
        book9.setPublisher("University Of Chicago Press");
        String pubDate9 = "15/11/2008";
        book9.setPublishDate(pubDate9);
        book9.setIsbn("9781490893390");
        book9.setDescription("“Rocks. Goats. Dry shrubs. Buffaloes. Thorns. A fallen tamarind tree.”");
        book9.setImageUrl("/MBSystem-war/resources/img/b9.jpg");
        book9.setAvailable(true);

        // 10
        Book book10 = new Book();
        book10.setTitle("A Mad Desire To Dance");
        book10.setAuthor("Elie Wiesel");
        book10.setPublisher("Picador");
        String pubDate10 = "13/04/2014";
        book10.setPublishDate(pubDate10);
        book10.setIsbn("9780087450397");
        book10.setDescription("From Elie Wiesel, a provocative and deeply thoughtful new novel "
                + "about a life shaped by the worst horrors of the twentieth century "
                + "and one man’s attempt to reclaim happiness.");
        book10.setImageUrl("/MBSystem-war/resources/img/b10.jpg");
        book10.setAvailable(true);

        // 11
        Book book11 = new Book();
        book11.setTitle("Mr. Peanut");
        book11.setAuthor("Adam Ross");
        book11.setPublisher("Knopf");
        String pubDate11 = "22/06/2010";
        book11.setPublishDate(pubDate11);
        book11.setIsbn("9781259800579");
        book11.setDescription("Adam Ross's daring first novel is a bleakly convincing portrayal of the eternal contest that often passes for a marriage.");
        book11.setImageUrl("/MBSystem-war/resources/img/b11.jpg");
        book11.setAvailable(true);

        // 12
        Book book12 = new Book();
        book12.setTitle("The War On Words");
        book12.setAuthor("Michael T. Gilmore");
        book12.setPublisher("University Of Chicago Press");
        String pubDate12 = "10/07/2010";
        book12.setPublishDate(pubDate12);
        book12.setIsbn("9780399876572");
        book12.setDescription("How did slavery and race impact American literature in the nineteenth century? "
                + "In this ambitious book, Michael T. Gilmore argues that ...");
        book12.setImageUrl("/MBSystem-war/resources/img/b12.jpg");
        book12.setAvailable(true);

        // Create dummy categories
        Category category1 = new Category();
        category1.setCategory("Fiction");
        Category category2 = new Category();
        category2.setCategory("Non-fiction");
        Category category3 = new Category();
        category3.setCategory("History");
        Category category4 = new Category();
        category4.setCategory("Biography");

        // Write the categories to the database
        cb.create(category1);
        cb.create(category2);
        cb.create(category3);
        cb.create(category4);

        // Read the categories from database
        Category category1Db = cb.findAll().get(0);
        Category category2Db = cb.findAll().get(1);
        Category category3Db = cb.findAll().get(2);
        Category category4Db = cb.findAll().get(3);

        // Add them to each book's category list
        book1.getCategoryList().add(category1Db);
        book1.getCategoryList().add(category2Db);
        book2.getCategoryList().add(category3Db);
        book2.getCategoryList().add(category4Db);
        book3.getCategoryList().add(category1Db);
        book4.getCategoryList().add(category2Db);
        book4.getCategoryList().add(category3Db);
        book5.getCategoryList().add(category4Db);
        book6.getCategoryList().add(category1Db);
        book6.getCategoryList().add(category2Db);
        book7.getCategoryList().add(category3Db);
        book8.getCategoryList().add(category4Db);
        book8.getCategoryList().add(category1Db);
        book9.getCategoryList().add(category2Db);
        book9.getCategoryList().add(category3Db);
        book10.getCategoryList().add(category4Db);
        book10.getCategoryList().add(category1Db);
        book11.getCategoryList().add(category2Db);
        book12.getCategoryList().add(category3Db);
        book12.getCategoryList().add(category4Db);

        // Write books to the database
        bb.create(book1);
        bb.create(book2);
        bb.create(book3);
        bb.create(book4);
        bb.create(book5);
        bb.create(book6);
        bb.create(book7);
        bb.create(book8);
        bb.create(book9);
        bb.create(book10);
        bb.create(book11);
        bb.create(book12);

        // Read the books from the database and add them to the book list of each category
        category1Db.getBookList().add(bb.findAll().get(0));
        category1Db.getBookList().add(bb.findAll().get(2));
        category1Db.getBookList().add(bb.findAll().get(5));
        category1Db.getBookList().add(bb.findAll().get(7));
        category1Db.getBookList().add(bb.findAll().get(9));

        category2Db.getBookList().add(bb.findAll().get(0));
        category2Db.getBookList().add(bb.findAll().get(3));
        category2Db.getBookList().add(bb.findAll().get(5));
        category2Db.getBookList().add(bb.findAll().get(8));
        category2Db.getBookList().add(bb.findAll().get(10));

        category3Db.getBookList().add(bb.findAll().get(1));
        category3Db.getBookList().add(bb.findAll().get(3));
        category3Db.getBookList().add(bb.findAll().get(6));
        category3Db.getBookList().add(bb.findAll().get(8));
        category3Db.getBookList().add(bb.findAll().get(11));

        category4Db.getBookList().add(bb.findAll().get(1));
        category4Db.getBookList().add(bb.findAll().get(4));
        category4Db.getBookList().add(bb.findAll().get(7));
        category4Db.getBookList().add(bb.findAll().get(9));
        category4Db.getBookList().add(bb.findAll().get(11));

        // Update the categories in the database
        cb.update(category1Db);
        cb.update(category2Db);
        cb.update(category3Db);
        cb.update(category4Db);
    }

    /**
     * @return the pageTitle
     */
    public String getPageTitle() {
        return pageTitle;
    }

}
