package com.blank.opentechbox.controller;


import com.blank.opentechbox.entity.Account;
import com.blank.opentechbox.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController {


    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String message) {
            super(message);
        }
    }
    @Autowired
    private AccountService accountService;

    //get all account info
    @GetMapping("")
    public List<Account> getAllAccounts() {
        return accountService.findAll();
    }
    //get account info by id
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) throws AccountNotFoundException {
        Optional<Account> optionalAccount = accountService.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            return ResponseEntity.ok(account);
        } else {
            throw new AccountNotFoundException("Account not found with id: " + id);
        }
    }
    //update account info
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account updatedAccount) throws AccountNotFoundException {
        Optional<Account> optionalAccount = accountService.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setUsername(updatedAccount.getUsername());
            account.setPassword(updatedAccount.getPassword());
            account.setEmail(updatedAccount.getEmail());
            account.setBalance(updatedAccount.getBalance());
            accountService.save(account);
            return ResponseEntity.ok().body(account);
        } else {
            throw new AccountNotFoundException("Account not found with id: " + id);
        }
    }
    //login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Account account) {
        Account existingAccount = accountService.findByUsername(account.getUsername());
        if (existingAccount == null || !existingAccount.getPassword().equals(account.getPassword())) {
            return ResponseEntity.badRequest().body("Account already exists!");
        }
        return ResponseEntity.ok().body("Login Successfully!");
    }
    //register
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Account account) {
        Account existingUser = accountService.findByUsername(account.getUsername());
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("Account already exists!");
        }
        accountService.save(account);
        return ResponseEntity.ok().body("Successfully registered!");
    }

    //remove
    @DeleteMapping("/{username}")
    public ResponseEntity<String> removeAccount(@PathVariable String username) {
        try {
            accountService.deleteAccount(username);
            return ResponseEntity.ok("Account with username " + username + " deleted successfully");
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found for "+username);
        }
    }


    @PostMapping("/upgrade")
    public ResponseEntity<String> upgradeAccount(@RequestParam String username,
                                            @RequestParam String currentAccountType,
                                            @RequestParam String targetAccountType) {
        try {
            accountService.upgradeAccount(username, currentAccountType, targetAccountType);
            String message = "Account type upgraded successfully to " + targetAccountType;
            accountService.sendUpgradeMessage(username, targetAccountType);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AccountService.InvalidRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}