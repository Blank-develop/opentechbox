package com.blank.opentechbox.service;

import com.blank.opentechbox.entity.Account;
import com.blank.opentechbox.entity.Product;

import com.blank.opentechbox.entity.Purchase;
import com.blank.opentechbox.repo.AccountRepo;
import com.blank.opentechbox.repo.ProductRepo;
import com.blank.opentechbox.repo.PurchaseRepository;
import com.blank.opentechbox.service.inbox.InboxService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;


@Service
@Transactional
public class ProductService {
    @Autowired
    private PurchaseRepository purchaseRepo;
    @Autowired
    private InboxService inboxService;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private AccountRepo accountRepo;

    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }
    public static class NotEnoughBalanceException extends RuntimeException {
        public NotEnoughBalanceException(String message) {
            super(message);
        }
    }

    public static class PurchaseResponse {
        private String message;
        private Purchase purchase;

        public PurchaseResponse(String message, Purchase purchase) {
            this.message = message;
            this.purchase = purchase;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Purchase getPurchase() {
            return purchase;
        }

        public void setPurchase(Purchase purchase) {
            this.purchase = purchase;
        }
    }
    //get all products
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }
    //post
    public Product createProduct(String username, Product product) {
        Account account = accountRepo.findByUsername(username);
        product.setAccount(account);
        return productRepo.save(product);
    }
    //product by username and id
    public List<Product> getProduct(String username, Long id) {
          Account account = accountRepo.findByUsername(username);
          return productRepo.findByAccountAndId(account,id);
    }
    //delete product by username and id
    public String deleteProduct(String username, Long id){
        Account account = accountRepo.findByUsername(username);
        List<Product> optionalProduct = productRepo.findByAccountAndId(account,id);
        if (optionalProduct.isEmpty()) {
            return "Product not found with id: " + id;
        } else {
            productRepo.deleteByAccountAndId(account, id);
            return "Product with id " + id + " has been deleted successfully.";
        }
    }

    //update
    public Product updateProductInfo(String username, Long productId, Product product) throws ProductNotFoundException {
        Account account = accountRepo.findByUsername(username);
        List<Product> optionalProduct = productRepo.findByAccountAndId(account, productId);
        if (!optionalProduct.isEmpty()) {
            Product existingProduct = optionalProduct.get(0);
            existingProduct.setProductName(product.getProductName());
            existingProduct.setPrice(product.getPrice());
            return productRepo.save(existingProduct);
        } else {
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }
    }

   //purchase
   public PurchaseResponse purchaseProduct(String buyerUsername, String sellerUsername, Long productId) throws ProductNotFoundException, NotEnoughBalanceException {
       Account buyer = accountRepo.findByUsername(buyerUsername);
       Account seller = accountRepo.findByUsername(sellerUsername);
       List<Product> optionalProduct = productRepo.findByAccountAndId(seller, productId);

       if (optionalProduct.isEmpty()) {
           throw new ProductNotFoundException("Product not found with id: " + productId);
       }

       Product product = optionalProduct.get(0);
       Double price = product.getPrice();
       double discount = 0; //set discount to 0

       // check if the buyer's account is premium, silver or gold and apply discount
       if (buyer.getAccountType().equals("silver")) {
           discount = price * 0.02; // discount 2%
       } else if (buyer.getAccountType().equals("gold")) {
           discount = price * 0.05; // discount 5%
       } else if (buyer.getAccountType().equals("premium")) {
           discount = price * 0.1; // discount 10%
       }

       if (buyer.getBalance() < price) {
           throw new NotEnoughBalanceException("Buyer does not have enough balance");
       }

       // update balances
       buyer.setBalance(buyer.getBalance() - price - discount);
       seller.setBalance(seller.getBalance() + price - discount);
       accountRepo.save(buyer);
       accountRepo.save(seller);

       // create purchase history
       Purchase purchase = new Purchase();
       purchase.setBuyer(buyer);
       purchase.setSeller(seller);
       purchase.setProduct(product);
       purchase.setPrice(BigDecimal.valueOf(price));
       purchaseRepo.save(purchase);
       String message = String.format("You have purchased %s for %.2f. The price after discount is %.2f", product.getProductName(), price, price - discount);
       inboxService.sendInbox(buyer.getUsername(), "You have successfully bought the product with ID " + product.getId() + " from " + seller.getUsername() + ". "+message);
       inboxService.sendInbox(seller.getUsername(), "Your product with ID " + product.getId() + " has been bought by " + buyer.getUsername() + ". "+message);

       return new PurchaseResponse("Purchase successful", purchase);
   }

   //ascending
    public List<Product> getAllProductsByPriceAsc() {
        return productRepo.findByOrderByPriceAsc();
    }
    //descending
    public List<Product> getAllProductsByPriceDesc() {
        return productRepo.findByOrderByPriceDesc();
    }

    //latest
    public List<Product> getAllProductsSortedByLatest() {
        return productRepo.findAllByOrderByProductDateDesc();
    }


}
