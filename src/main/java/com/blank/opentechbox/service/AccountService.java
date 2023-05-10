package com.blank.opentechbox.service;

import com.blank.opentechbox.entity.Account;

import com.blank.opentechbox.repo.AccountRepo;
import com.blank.opentechbox.service.inbox.InboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private InboxService inboxService;
    public Account findByUsername(String username) {
        return accountRepo.findByUsername(username);
    }
    public void save(Account account){
        accountRepo.save(account);
    }
    public static class InvalidRequestException extends RuntimeException {
        public InvalidRequestException(String message) {
            super(message);
        }
    }
    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String message) {
            super(message);
        }
    }

    public List<Account> findAll() {
        return accountRepo.findAll();
    }

    public Optional<Account> findById(Long id) {
        return accountRepo.findById(id);
    }

    //remove
    public String deleteAccount(String username) throws AccountNotFoundException {
        Account account = accountRepo.findByUsername(username);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with username: " + username);
        }
        accountRepo.delete(account);
        return "Account deleted successfully";
    }

    //upgrade account level
    public void upgradeAccount(String username, String currentAccountType, String targetAccountType) {
        Account account = accountRepo.findByUsername(username);

        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }

        if (!account.getAccountType().equals(currentAccountType)) {
            throw new InvalidRequestException("Current account type is not correct");
        }

        switch (targetAccountType) {
            case "bronze" -> {
                account.setAccountExpirationDate(null);
                throw new InvalidRequestException("Cannot downgrade to bronze account");
            }
            case "silver" -> {
                if (account.getAccountType().equals("bronze")) {
                    account.setAccountExpirationDate(LocalDate.now().plusWeeks(1));
                    account.setAccountType("silver");
                    account.setBalance(account.getBalance() - 20.0);
                    accountRepo.save(account);
                } else {
                    throw new InvalidRequestException("Current account type is not eligible for upgrade to silver");
                }
            }
            case "gold" -> {
                if (account.getAccountType().equals("silver") || account.getAccountType().equals("bronze")) {
                    account.setAccountExpirationDate(LocalDate.now().plusWeeks(2));
                    account.setAccountType("gold");
                    account.setBalance(account.getBalance() - 35.0);
                    accountRepo.save(account);
                } else {
                    throw new InvalidRequestException("Current account type is not eligible for upgrade to gold");
                }
            }
            case "premium" -> {
                if (account.getAccountType().equals("gold") || account.getAccountType().equals("silver") || account.getAccountType().equals("bronze")) {
                    account.setAccountExpirationDate(LocalDate.now().plusWeeks(4));
                    account.setAccountType("premium");
                    account.setBalance(account.getBalance() - 50.0);
                    accountRepo.save(account);
                } else {
                    throw new InvalidRequestException("Current account type is not eligible for upgrade to premium");
                }
            }
            default -> throw new InvalidRequestException("Invalid target account type");
        }
    }

    //send Upgrade Message
    public void sendUpgradeMessage(String username, String newAccountType) {
        // find the user's account
        Account account = accountRepo.findByUsername(username);

        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }

        // create the inbox message
        String message = "Congratulations! Your account has been upgraded to " + newAccountType + ".";

        // add the message to the user's inbox
        inboxService.sendInbox(account.getUsername(), message);

        // save the account
        accountRepo.save(account);
    }
}
