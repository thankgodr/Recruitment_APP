package com.rhicstech.crutra.crutra.job;

/**
 * Created by rhicstechii on 16/01/2018.
 */

public class Company {
    private int id;
    private String name;
    private String city;
    private String state;
    private String country;
    private  String about;
    private String shotDes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShotDes() {
        return shotDes;
    }

    public void setShotDes(String shotDes) {
        this.shotDes = shotDes;
    }
}
