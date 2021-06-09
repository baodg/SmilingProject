package com.example.firebaseapplication.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UserResponse {

    private String id;

    private String name;

    private String email;

    private String password;

    private String avatar;

    private String emotion;

    public UserResponse() {
        this.id = "";
        this.name = "";
        this.email = "";
        this.password = "";
        this.avatar = "";
        this.emotion = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public static UserResponse getResponseFromObject(JSONObject object) throws JSONException {
        UserResponse response = new UserResponse();
        response.setName(object.getString("name"));
        response.setEmail(object.getString("email"));
        response.setAvatar(object.getString("avatar"));
        response.setId(object.getString("id"));
        response.setPassword(object.getString("password"));
        response.setEmotion(object.getString("emotion"));
        return response;
    }

    public HashMap<String, String> toHashMap() {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("id", id);
        result.put("name", name);
        result.put("email", email);
        result.put("password", password);
        result.put("avatar", avatar);
        result.put("emotion", emotion);
        return result;
    }
}
