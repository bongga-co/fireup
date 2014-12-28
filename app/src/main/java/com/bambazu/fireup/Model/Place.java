package com.bambazu.fireup.Model;

/**
 * Created by blackxcorpio on 10/15/14.
 */
public class Place {

    private String placeCode;
    private String placeName;
    private String placeIcon;
    private String placeCategory;
    private float placeRanking;
    private double latitude;
    private double longitude;
    private int rooms;
    private boolean visible;

    public Place(String placeCode, String placeName, String placeIcon, String placeCategory, float placeRanking, double latitude, double longitude, int rooms, boolean visible){
        this.placeCode = placeCode;
        this.placeName = placeName;
        this.placeIcon = placeIcon;
        this.placeCategory = placeCategory;
        this.placeRanking = placeRanking;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rooms = rooms;
        this.visible = visible;
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

    public String getPlaceCategory() {
        return placeCategory;
    }

    public void setPlaceCategory(String placeCategory) {
        this.placeCategory = placeCategory;
    }

    public float getPlaceRanking() {
        return placeRanking;
    }

    public void setPlaceRanking(float placeRanking) {
        this.placeRanking = placeRanking;
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
}
