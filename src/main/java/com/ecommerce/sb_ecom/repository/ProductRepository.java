package com.ecommerce.sb_ecom.repository;

import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pagedetails);


    List<Product> findByCategory(Category category);

    List<Product> findByProductNameLikeIgnoreCase(String keyword);
}

