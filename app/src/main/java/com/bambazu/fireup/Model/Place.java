package com.bambazu.fireup.Model;

/**
 * Created by blackxcorpio on 10/15/14.
 */
public class Place {

    private String placeCode;
    private String placeName;
    private String placeIcon;
    private String placeIcon2;
    private String placeIcon3;
    private String placeIcon4;
    private String placeIcon5;
    private String placeCategory;
    private Number placeRanking;
    private double latitude;
    private double longitude;
    private int rooms;
    private boolean visible;
    private String address;
    private String city;
    private String depto;
    private String country;
    private String description;
    private Number lowprice;
    private Number highprice;
    private String phone;

    public Place(String placeCode, String placeName, String placeIcon, String placeIcon2, String placeIcon3, String placeIcon4, String placeIcon5, String placeCategory, Number placeRanking, double latitude, double longitude, int rooms, boolean visible, String address, String city, String depto, String country, String description, Number lowprice, Number highprice, String phone){
        this.placeCode = placeCode;
        this.placeName = placeName;
        this.placeIcon = placeIcon;
        this.placeIcon2 = placeIcon2;
        this.placeIcon3 = placeIcon3;
        this.placeIcon4 = placeIcon4;
        this.placeIcon5 = placeIcon5;
        this.placeCategory = placeCategory;
        this.placeRanking = placeRanking;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rooms = rooms;
        this.visible = visible;
        this.address = address;
        this.city = city;
        this.depto = depto;
        this.country = country;
        this.description = description;
        this.lowprice = lowprice;
        this.highprice = highprice;
        this.phone = phone;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceCode() {
        return placeCode;
    }

    public void setPlaceCode(String placeCode) {
        this.placeCode = placeCode;
    }

    public String getPlaceIcon() {
        return placeIcon;
    }

    public void setPlaceIcon(String placeIcon) {
        this.placeIcon = placeIcon;
    }

    public String getPlaceIcon2() {
        return placeIcon2;
    }

    public void setPlaceIcon2(String placeIcon2) {
        this.placeIcon2 = placeIcon2;
    }

    public String getPlaceIcon3() {
        return placeIcon3;
    }

    public void setPlaceIcon3(String placeIcon3) {
        this.placeIcon3 = placeIcon3;
    }

    public String getPlaceIcon4() {
        return placeIcon4;
    }

    public void setPlaceIcon4(String placeIcon4) {
        this.placeIcon4 = placeIcon4;
    }

    public String getPlaceIcon5() {
        return placeIcon5;
    }

    public void setPlaceIcon5(String placeIcon5) {
        this.placeIcon5 = placeIcon5;
    }

    public String getPlaceCategory() {
        return placeCategory;
    }

    public void setPlaceCategory(String placeCategory) {
        this.placeCategory = placeCategory;
    }

    public Number getPlaceRanking() {
        return placeRanking;
    }

    public void setPlaceRanking(Number placeRanking) {
        this.placeRanking = placeRanking;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String adress) {
        this.address = adress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public boolean getVisible(){
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Number getHighprice() {
        return highprice;
    }

    public void setHighprice(Number highprice) {
        this.highprice = highprice;
    }

    public Number getLowprice() {
        return lowprice;
    }

    public void setLowprice(Number lowprice) {
        this.lowprice = lowprice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDepto() {
        return depto;
    }

    public void setDepto(String depto) {
        this.depto = depto;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
