package com.qnex.jpa.practice.entity;

import javax.persistence.*;

@Entity
public class Library {

    @Id
    private Long id;

    //    @Version
    private Long version;

    private String name;

    @ManyToOne
    private LibraryType libraryType;


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


