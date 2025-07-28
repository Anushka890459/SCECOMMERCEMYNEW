package com.ecommerce.sb_ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@NotBlank

    private String categoryName;


    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;


    // Default constructor
    public Category() {}

    // Getter for id
    public Long getId() {
        return id;
    }

    // Getter and Setter for categoryName
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCategoryId(Long categoryId) {
    }


    public List<Product> getProducts() {
        return products;
    }
}

