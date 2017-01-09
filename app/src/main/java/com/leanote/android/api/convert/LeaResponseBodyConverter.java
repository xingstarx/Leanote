package com.leanote.android.api.convert;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.leanote.android.utils.JsonWrapperHelper;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class LeaResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    LeaResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String str = value.string();
        String jsonBean = JsonWrapperHelper.getWrapperJson(str);
        Log.e("TEST", "jsonBean == " + jsonBean);
        Reader reader = new StringReader(jsonBean);
        JsonReader jsonReader = gson.newJsonReader(reader);
        try {
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}