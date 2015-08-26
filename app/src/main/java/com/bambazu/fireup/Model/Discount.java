package com.bambazu.fireup.Model;

/**
 * Created by pratechmobile on 8/14/15.
 */
public class Discount {
    private String offerID;
    private String offerIcon;
    private String offerDiscount;
    private String offerPlace;

    public Discount(String offerID, String offerIcon, String offerDiscount, String offerPlace){
        this.offerID = offerID;
        this.offerIcon = offerIcon;
        this.offerDiscount = offerDiscount;
        this.offerPlace = offerPlace;
    }

    public String getOfferID() {
        return offerID;
    }

    public void setOfferID(String offerID) {
        this.offerID = offerID;
    }

    public String getOfferIcon() {
        return offerIcon;
    }

    public void setOfferIcon(String offerIcon) {
        this.offerIcon = offerIcon;
    }

    public String getOfferDiscount() {
        return offerDiscount;
    }

    public void setOfferDiscount(String offerDiscount) {
        this.offerDiscount = offerDiscount;
    }

    public String getOfferPlace() {
        return offerPlace;
    }

    public void setOfferPlace(String offerPlace) {
        this.offerPlace = offerPlace;
    }
}