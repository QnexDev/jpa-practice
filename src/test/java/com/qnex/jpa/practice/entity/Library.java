package com.qnex.jpa.practice.entity;

import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Library {

    @Id
    private Long id;

    //    @Version
    private Long version;

    private String name;

    private Date created;

    @ManyToOne
    @JoinColumn(name = "library_type_id")
    private LibraryType libraryType;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "Library_Book",
            joinColumns = @JoinColumn(name = "library_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    @BatchSize(size = 100)
    private List<Book> books = new ArrayList<>();



    public Library() {
    }

    public Library(Long id, String name, Long version, LibraryType libraryType) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.libraryType = libraryType;
    }

    public Library(Long id, String name, Long version) {
        this(id, name, version, null);
    }

    public Library(Long id, String name) {
        this(id, name, 1L);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LibraryType getLibraryType() {
        return libraryType;
    }

    public void setLibraryType(LibraryType libraryType) {
        this.libraryType = libraryType;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "Library{" +
                "id=" + id +
                ", version=" + version +
                ", name='" + name + '\'' +
                ", libraryType=" + libraryType +
                '}';
    }
}


