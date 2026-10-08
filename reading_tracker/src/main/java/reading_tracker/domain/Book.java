package reading_tracker.domain;

import jakarta.persistence.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private Integer publicationYear;
    private Integer pages;
    private Integer rating;
    private boolean favorite;
    @Enumerated (EnumType.STRING)
    private BookStatus status;


    public Book(String title, String author, Integer publicationYear, Integer pages,
        Integer rating, boolean favorite, BookStatus status){

            this.title = title;
            this.author = author;
            this.publicationYear = publicationYear;
            this.pages = pages;
            this.rating = rating;
            this.favorite = favorite;
            this.status = status;
    }

    //create a null object so Spring can create an empty form 
    //before the user enters data
    public Book(){
    }

    // getter: returns the field value  

    public Long getId(){
        return id;
    }
    public String getTitle(){
        return title;
    }
    public String getAuthor(){
        return author;
    }
    public Integer getPublicationYear(){
        return publicationYear;
    }
    public Integer getPages(){
        return pages;
    }
    public Integer getRating(){
        return rating;
    }
    public boolean getFavorite(){
        return favorite;
    }
    public BookStatus getStatus(){
        return status;
    }
    //setter: updates the field value
    public void setId(Long id){
        this.id = id;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setAuthor(String author){
        this.author = author;
    }
    public void setPublicationYear(Integer publicationYear){
        this.publicationYear = publicationYear;
    }
    public void setPages(Integer pages){
        this.pages = pages;
    }
    public void setRating(Integer rating){
        this.rating = rating;
    }
    public void setFavorite(boolean favorite){
        this.favorite = favorite;
    }
    public void setStatus(BookStatus status){
        this.status = status;
    }
}