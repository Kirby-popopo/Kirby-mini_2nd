package com.example.Kirby_mini_2nd.config;

import com.google.gson.Gson;

public class GsonConfig {
    private static final Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }

}
