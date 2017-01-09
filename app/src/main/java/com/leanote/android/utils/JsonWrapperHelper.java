package com.leanote.android.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonWrapperHelper {
    public static final String TAG = "JsonWrapperHelper";

    public static String getWrapperJson(String string) {
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(string).getAsJsonObject();
        if (object.has("Ok") && object.has("Msg")) {
            return string;
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("{\"Ok\":false,\"Msg\":null,\"data\":").append(string).append("}");
            return builder.toString();
        }
    }
}
