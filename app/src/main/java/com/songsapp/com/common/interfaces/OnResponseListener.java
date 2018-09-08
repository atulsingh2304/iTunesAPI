package com.songsapp.com.common.interfaces;

import org.json.JSONObject;

public interface OnResponseListener {
    void onSuccess(JSONObject response);
    void onError(String errorMessage);
}
