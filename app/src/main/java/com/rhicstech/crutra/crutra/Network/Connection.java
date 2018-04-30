package com.rhicstech.crutra.crutra.Network;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by rhicstechii on 16/01/2018.
 */

public class Connection {
    static OkHttpClient client = new OkHttpClient();
    static String  baseUrl = "http://13.250.43.203/api/";

    public static Response getconnect (String path) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .build();

        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response postconnect(String path, String jsonPostData) throws IOException {
         final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, jsonPostData);
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();


        //return response.body().string();
        return response;
    }

    public static Response getconnectWithToken (String path, String token) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .addHeader("authorization", "Bearer " + token)
                .build();

        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response postconnectWithToken (String path, String jsonPostData, String token) throws IOException {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, jsonPostData);
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .addHeader("authorization", "Bearer " + token)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();


        //return response.body().string();
        return response;
    }

    public static Response postconnectCustom (String path, String jsonPostData, String datatype) throws IOException {
        final MediaType JSON = MediaType.parse(datatype);

        RequestBody body = RequestBody.create(JSON, jsonPostData);
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();


        //return response.body().string();
        return response;
    }
    public static Response postconnectCustomwithToken (String path, String jsonPostData, String datatype, String token) throws IOException {
        final MediaType JSON = MediaType.parse(datatype);

        RequestBody body = RequestBody.create(JSON, jsonPostData);
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .addHeader("authorization", "Bearer " + token)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();


        //return response.body().string();
        return response;
    }


}
