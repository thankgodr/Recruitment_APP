package com.rhicstech.crutra.crutra.auth.Social.CallBacks;

import com.jaychang.sa.SocialUser;

/**
 * Created by rhicstechii on 09/02/2018.
 */

public interface FacebookCallback {
    void onSuccess(SocialUser socialUser);
    void onError(Throwable error);
    void canceled(String msg);
}
