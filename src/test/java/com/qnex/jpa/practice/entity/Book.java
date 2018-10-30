package com.qnex.jpa.practice.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
public class Book {

    @Id
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Library> libraries;

    public Book() {
    }

    public Book(Long id, String name, List<Library> libraries) {
        this.id = id;
        this.name = name;
        this.libraries = libraries;
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

    public List<Library> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<Library> libraries) {
        this.libraries = libraries;
    }

}