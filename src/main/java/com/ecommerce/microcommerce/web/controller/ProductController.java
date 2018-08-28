package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.DAO.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitGratuitException;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
public class ProductController {


    @Autowired
    private ProductDao productDao;


    @RequestMapping(value="/Produits", method= RequestMethod.GET)
    public MappingJacksonValue  listeProduits(){

        List<Product> produits = productDao.findAll();




        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");

        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);

        produitsFiltres.setFilters(listDeNosFiltres);

        return  produitsFiltres;


    }

    @RequestMapping(value="/AdminProduits", method= RequestMethod.GET)
    public List<String> listeProduitMarge(){

        List<Product> produits = productDao.findAll();
        Collections.sort(produits);
        List<String> listeMargeTrie = new ArrayList<>();

        for(Product p : produits){
            int marge = p.getPrix()- p.getPrixAchat();
            listeMargeTrie.add(p.toString()+": "+marge);
        }
        return listeMargeTrie;
    }






    @GetMapping(value="/Produits/{id}")
    public Product afficherProduit(@PathVariable int id){
        Product produit = productDao.findById(id);
        if(produit==null) throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est INTROUVABLE. Écran Bleu si je pouvais.");


        return produit;
    }


    @GetMapping(value = "test/produits/{prixLimit}")
    public List<Product> testeDeRequetes(@PathVariable int prixLimit) {
        return productDao.findByPrixGreaterThan(350);
    }



    @PostMapping(value = "/Produits")
    public ResponseEntity< Void> ajouterProduit(@RequestBody Product product) throws ProduitGratuitException {

        Product productAdded =  productDao.save(product);


        if(productAdded.getPrix()<=0){
            throw new ProduitGratuitException("Erreur de prix...");
        }


        if (productAdded == null){
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(productAdded.getId()).toUri();


        return ResponseEntity.created(location).build();


    }


}
