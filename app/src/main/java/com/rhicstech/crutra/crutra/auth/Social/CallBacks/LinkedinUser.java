package com.rhicstech.crutra.crutra.auth.Social.CallBacks;

import com.jaychang.sa.SocialUser;

/**
 * Created by rhicstechii on 13/02/2018.
 */

public class LinkedinUser extends SocialUser {

    public void setToken(String token){
        this.accessToken = token;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }

    public  void setPicUrl(String url){
        this.profilePictureUrl = url;
    }

    public  void setUserId(String userId1){
        this.userId = userId1;
    }
    public void setUsername(String username1){
        this.username = username1;
    }
    public void setProfileUrl(String profileUrl){
        this.pageLink = profileUrl;
    }
}
