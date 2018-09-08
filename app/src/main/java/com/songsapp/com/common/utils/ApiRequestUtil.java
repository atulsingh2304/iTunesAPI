package com.songsapp.com.common.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.songsapp.com.R;
import com.songsapp.com.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ApiRequestUtil extends OkHttpClient {

    // Member variables.
    private Context mContext;
    private Request.Builder requestBuilder;
    private OnResponseListener mOnResponseListener;
    private static final long REQUEST_TIMEOUT = 30;


    /**
     * Public constructor to initialize the class with required params.
     *
     * @param context Context of calling class.
     */
    public ApiRequestUtil(Context context) {
        try {
            mContext = context;
            newBuilder().readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            // try the request
                            Response response = chain.proceed(request);
                            int tryCount = 0;
                            while (!response.isSuccessful() && tryCount < 3) {
                                tryCount++;
                                // retry the request
                                response = chain.proceed(request);
                            }

                            // otherwise just pass the original response on
                            return response;
                        }
                    }).build();
            requestBuilder = new Request.Builder();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    /**
     * Method to set response listener, which will return the success and error result on request completion.
     *
     * @param listener OnResponseListener instance which gets called on request execution.
     */
    public void setOnResponseListener(OnResponseListener listener) {
        mOnResponseListener = listener;
    }

    /**
     * Method to make the GET request.
     *
     * @param url      GET url which needs to be execute.
     * @param listener OnResponseListener instance which returns the response.
     */
    public void getResponse(String url, OnResponseListener listener) {
        try {
            mOnResponseListener = listener;
            requestBuilder.url(url);
            requestBuilder.get();
            makeRequestToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to make request call to the server.
     */
    private void makeRequestToServer() {
        newCall(requestBuilder.build()).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (mOnResponseListener != null && !call.isCanceled()) {
                    mOnResponseListener.onError(mContext.getResources().getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (mOnResponseListener != null) {
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject responseObject = getResponseObject(response.body().string());
                            if (responseObject != null) {
                                mOnResponseListener.onSuccess(responseObject);
                            } else {
                                mOnResponseListener.onSuccess(new JSONObject());
                            }
                            handleResponseBodyStream(response);
                        } else if (!response.isSuccessful()) {
                            handleResponseBodyStream(response);
                            mOnResponseListener.onError(response.message());
                        } else {
                            mOnResponseListener.onError(mContext.getResources().getString(R.string.something_went_wrong));
                        }
                    }
//                    Log.d(ApiRequestUtil.class.getSimpleName(), "onResponse: " + response);

                } catch (Exception e) {
                    if (mOnResponseListener != null) {
                        mOnResponseListener.onError(mContext.getResources().getString(R.string.something_went_wrong));
                    }
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Method to close stream when request is not successful.
     *
     * @param response Response instance.
     */
    private void handleResponseBodyStream(Response response) {
        if (response != null && response.body() != null) {
            response.body().close();
        }
    }

    /**
     * Method to get the JSONObject from the received response of OKHttp request.
     *
     * @param response Response string which is received in successful operation.
     * @return Returns the JSONObject of received response.
     */
    private JSONObject getResponseObject(String response) {
        try {
            Object object = new JSONTokener(response).nextValue();
            if (object instanceof JSONObject) {
                return new JSONObject(response);
            } else if (object instanceof JSONArray) {
                return convertToJsonObject(new JSONArray(response));
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to convert jsonArray in JsonObject
     *
     * @param jsonArray JsonArray to convert in JsonObject
     * @return Converted JsonObject
     */
    private JSONObject convertToJsonObject(JSONArray jsonArray) {
        JSONObject jsonObject = new JSONObject();
        if (jsonArray != null) {
            try {
                jsonObject.put("response", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        } else {
            return null;
        }
    }
}
