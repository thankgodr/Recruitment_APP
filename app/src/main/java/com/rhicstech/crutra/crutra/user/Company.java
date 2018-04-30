package com.rhicstech.crutra.crutra.user;

import com.rhicstech.crutra.crutra.Utils.UserBase;

/**
 * Created by rhicstechii on 07/02/2018.
 */

public class Company extends UserBase {
    private String companyName;
    private int companySize;
    private int job_volume;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getCompanySize() {
        return companySize;
    }

    public void setCompanySize(int companySize) {
        this.companySize = companySize;
    }

    public int getJob_volume() {
        return job_volume;
    }

    public void setJob_volume(int job_volume) {
        this.job_volume = job_volume;
    }
}
