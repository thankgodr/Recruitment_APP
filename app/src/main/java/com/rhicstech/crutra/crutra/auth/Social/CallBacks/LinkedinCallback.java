package com.rhicstech.crutra.crutra.auth.Social.CallBacks;

import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiResponse;

/**
 * Created by rhicstechii on 13/02/2018.
 */

public interface LinkedinCallback {
    void onstart();
    void onSuccuss(ApiResponse response);
    void onError(LIApiError error);
}
