package com.ecommerce.microcommerce.DAO;

import com.ecommerce.microcommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ProductDao extends JpaRepository<Product,Integer> {


    Product findById(int id);
    List<Product> findByPrixGreaterThan(int id);


}
