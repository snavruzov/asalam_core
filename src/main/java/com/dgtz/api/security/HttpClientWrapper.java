package com.dgtz.api.security;

import com.dgtz.api.beans.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;

/**
 * Created by Intellij IDEA.
 * User: Sardor Navruzov
 * Date: 1/30/13
 * Time: 1:15 PM
 */
public abstract class HttpClientWrapper {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientWrapper.class);

    protected HttpClientWrapper() {
    }


    protected void doRequestGet(String url)
    {
        try {
            logger.debug("REQUEST URL {}", url);
            com.mashape.unirest.http.HttpResponse<String> body = Unirest.get(url)
                    .asString();
            logger.debug("GET RESPONSE FROM JMAIL: {}, {}", body.getBody());

        } catch (Exception e){
            logger.error("ERROR IN SENDING NOTE", e);
        }
    }

    protected Object doRequestGet(Class clazz, String url)
    {
        Object ret = null;
        try {
            logger.debug("REQUEST URL {}", url);
            com.mashape.unirest.http.HttpResponse<String> body = Unirest.get(url)
                    .asString();
            ret= new Gson().fromJson(body.getBody(), clazz);

        } catch (Exception e){
            logger.error("ERROR IN MAIN", e);
        }

        return ret;

    }

    protected FBInfo doFBRequestGet(String url)
    {
        FBInfo ret = null;
        try {
            logger.debug("REQUEST URL {}", url);
            com.mashape.unirest.http.HttpResponse<JsonNode> body = Unirest.get(url)
                    .asJson();
            ret= new Gson().fromJson(body.getBody().toString(), FBInfo.class);

        } catch (Exception e){
            logger.error("ERROR IN SENDING NOTE", e);
        }

        return ret;
    }

    protected VKInfo doVKRequestGet(String url)
    {
        VKInfo ret = null;
        try {
            logger.debug("REQUEST VK URL {}", url);
            com.mashape.unirest.http.HttpResponse<JsonNode> body = Unirest.get(url)
                    .asJson();
            ret= new Gson().fromJson(body.getBody().toString(), VKInfo.class);

        } catch (Exception e){
            logger.error("ERROR IN SENDING NOTE", e);
        }

        return ret;
    }

    protected GoogleInfo doGoogleAuthRequestGet(String url)
    {
        GoogleInfo ret = null;
        try {
            logger.debug("REQUEST GOOGLE URL {}", url);
            com.mashape.unirest.http.HttpResponse<JsonNode> body = Unirest.get(url)
                    .asJson();
            ret= new Gson().fromJson(body.getBody().toString(), GoogleInfo.class);

        } catch (Exception e){
            logger.error("ERROR IN SENDING NOTE", e);
        }

        return ret;
    }

    protected GOOGInfo doGoogleRequestGet(String url, String token)
    {
        GOOGInfo ret = null;
        try {
            logger.debug("REQUEST URL {}", url);
            com.mashape.unirest.http.HttpResponse<JsonNode> body = Unirest.get(url)
                    .header("Authorization", "Bearer " + token)
                    .asJson();
            ret= new Gson().fromJson(body.getBody().toString(), GOOGInfo.class);

        } catch (Exception e){
            logger.error("ERROR IN SENDING NOTE", e);
        }

        return ret;
    }

    protected TwitterInfo doTwitterRequestGet(String url, String header)
    {
        TwitterInfo ret = null;
        try {
            logger.debug("REQUEST URL {}:::HEADER:::>> {} ", url, header);
            com.mashape.unirest.http.HttpResponse<JsonNode> body = Unirest.get(url)
                    .header("Authorization", header)
                    .asJson();
            ret= new Gson().fromJson(body.getBody().toString(), TwitterInfo.class);

        } catch (Exception e){
            logger.error("ERROR IN SENDING NOTE", e);
        }

        return ret;
    }

    @SuppressWarnings("rawtypes")
    private Object getObjFromResp(HttpResponse response, Class clazz, String subUrl) {
        Object ret = null;

        try {
            if (response != null) {
                JsonReader reader = new JsonReader(new InputStreamReader(response.getEntity().getContent()));

                logger.debug(reader.peek().toString());
                ret = new Gson().fromJson(reader, clazz);
            } else {
                logger.debug("Response from {} is null", subUrl);
            }
        } catch (Exception e) {
            logger.error("ERROR IN MAIN API ", e);
        }

        return ret;
    }


}
