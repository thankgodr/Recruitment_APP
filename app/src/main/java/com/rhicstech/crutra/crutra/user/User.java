package com.rhicstech.crutra.crutra.user;

import com.rhicstech.crutra.crutra.Utils.UserBase;

/**
 * Created by rhicstechii on 16/01/2018.
 */

public class User extends UserBase {

    private String firstname;
    private String lastname;
    private String middlename;

    public User(){

    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }







}
