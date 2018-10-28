package com.qnex.jpa.practice.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class Library {

    @Id
    private Long id;

//    @Version
    private Long version;

    private String name;


    public Library() {
    }

    public Library(Long id, String name, Long version) {
        this.id = id;
        this.name = name;
        this.version = version;
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

    @Override
    public String toString() {
        return "Library{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}


