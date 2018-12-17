package joelespinal.com.greenprodiver.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Calendar;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity(
        nameInDb = "BOOKS",
        generateConstructors = true,
        generateGettersSetters = true
)
public class Book {
    @Property(nameInDb = "_id")
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "TITLE")
    private String title;
    @Property(nameInDb = "AUTHOR")
    private String author;
    @Property(nameInDb = "PUBLICATION_DATE")
    private Date publicationDate = Calendar.getInstance().getTime();
@Generated(hash = 743716992)
public Book(Long id, String title, String author, Date publicationDate) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.publicationDate = publicationDate;
}
@Generated(hash = 1839243756)
public Book() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getTitle() {
    return this.title;
}
public void setTitle(String title) {
    this.title = title;
}
public String getAuthor() {
    return this.author;
}
public void setAuthor(String author) {
    this.author = author;
}
public Date getPublicationDate() {
    return this.publicationDate;
}
public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
}



}
