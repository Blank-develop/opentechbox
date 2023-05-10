package com.blank.opentechbox.entity;

public class PurchaseResponse {
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
