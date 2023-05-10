package com.blank.opentechbox.controller;

import com.blank.opentechbox.entity.Inbox;
import com.blank.opentechbox.entity.Product;
import com.blank.opentechbox.service.ProductService;
import com.blank.opentechbox.service.inbox.InboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private InboxService inboxService;
    @Autowired
    private ProductService productService;

    //get all products
    @GetMapping("")
    public List<Product> getAllProducts(@RequestParam(required = false) String sortBy) {
        List<Product> products;
        if (sortBy != null && sortBy.equalsIgnoreCase("asc")) {
            products = productService.getAllProductsByPriceAsc();
        } else if (sortBy != null && sortBy.equalsIgnoreCase("desc")) {
            products = productService.getAllProductsByPriceDesc();

        } else if (sortBy != null && sortBy.equalsIgnoreCase("latest")) {
            products = productService.getAllProductsSortedByLatest();
        }else {
            products = productService.getAllProducts();
        }
        return products;
    }

    @PostMapping("/{username}")
    public ResponseEntity<Product> createProduct(@PathVariable String username, @RequestBody Product product) {
        Product createdProduct = productService.createProduct(username, product);
        return ResponseEntity.created(URI.create("/api/products/" + createdProduct.getId())).body(createdProduct);
    }
    //get product by username and id
    @GetMapping("/{username}/{id}")
    public ResponseEntity<Object> getProduct(@PathVariable String username, @PathVariable Long id) {
        List<Product> product = productService.getProduct(username, id);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        } else {
            return ResponseEntity.ok(product);
        }
    }
    //delete product by username and id
    @DeleteMapping("/{username}/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable String username, @PathVariable Long id) {
        String message = productService.deleteProduct(username, id);
        return ResponseEntity.ok(message);
    }
    //update
    @PutMapping("/{username}/{productId}")
    public ResponseEntity<Object> updateProductInfo(@PathVariable String username, @PathVariable Long productId, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProductInfo(username, productId, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (ProductService.ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    //purchase
    @PostMapping("/{buyer}/{seller}/{productId}/purchase")
    public ResponseEntity<Object> purchaseProduct(@PathVariable String buyer, @PathVariable String seller, @PathVariable Long productId) {
        try {
            ProductService.PurchaseResponse response = productService.purchaseProduct(buyer, seller, productId);
            return ResponseEntity.ok(response);
        } catch (ProductService.ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ProductService.NotEnoughBalanceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    //inbox
    @GetMapping("/{username}/inbox")
    public List<Inbox> getInboxes(@PathVariable String username) {
        return inboxService.getInboxes(username);
    }

    @PutMapping("/{username}/inbox/{id}/read")
    public ResponseEntity<Object> markAsRead(@PathVariable String username, @PathVariable Long id) {
        inboxService.markAsRead(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //search product by name

}
