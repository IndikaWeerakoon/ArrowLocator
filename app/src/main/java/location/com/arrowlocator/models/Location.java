package location.com.arrowlocator.models;

public class Location {
    private int id;
    private String name;
    private double lat;
    private double lon;
    private char flag;
    private String discription;
    private String imageUrl;

    public Location(int id, String name, double lat, double lon, char flag, String discription, String imageUrl) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.flag = flag;
        this.discription = discription;
        this.imageUrl = imageUrl;
    }

    public Location() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public char getFlag() {
        return flag;
    }

    public String getDiscription() {
        return discription;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
