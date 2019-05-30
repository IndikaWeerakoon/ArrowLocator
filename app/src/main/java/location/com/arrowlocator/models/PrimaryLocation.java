package location.com.arrowlocator.models;

import java.io.Serializable;

public class PrimaryLocation implements Serializable {
    private int id;
    private String name;
    private double lat;
    private double lon;
    private char flag;

    public PrimaryLocation(int id, String name, double lat, double lon, char flag) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.flag = flag;
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
}
