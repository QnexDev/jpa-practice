package com.qnex.jpa.practice.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class LibraryType {

    @Id
    private Long id;
    private String type;

//    @OneToMany(fetch = FetchType.EAGER)
//    @JoinColumn(name = "library_type_id")
    @ElementCollection
    private List<String> libraries;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<String> libraries) {
        this.libraries = libraries;
    }
}
