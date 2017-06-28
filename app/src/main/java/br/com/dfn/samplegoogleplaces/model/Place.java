package br.com.dfn.samplegoogleplaces.model;


public class Place {
    private String name;
    private String type;
    private String address;
    private double lat;
    private double lng;

    public static final String TYPE_SHOPPING = "shopping_mall";
    public static final String TYPE_NIGHT_CLUB = "night_club";
    public static final String TYPE_RESTAURANT = "restaurant";
    public static final String TYPE_AIRPORT = "airport";
    public static final String TYPE_SUPERMARKET = "grocery_or_supermarket";

    public Place(String name, String type, String address, double lat, double lng) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
