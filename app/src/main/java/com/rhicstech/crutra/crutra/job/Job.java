package com.rhicstech.crutra.crutra.job;

/**
 * Created by rhicstechii on 16/01/2018.
 */

public class Job {
    private int id;
    private String title;
    private String shotDes;
    private String londDes;
    private Company company;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShotDes() {
        return shotDes;
    }

    public void setShotDes(String shotDes) {
        this.shotDes = shotDes;
    }

    public String getLondDes() {
        return londDes;
    }

    public void setLondDes(String londDes) {
        this.londDes = londDes;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
