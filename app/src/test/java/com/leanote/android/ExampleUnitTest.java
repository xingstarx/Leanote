package com.leanote.android;

import com.google.gson.Gson;

import org.junit.Test;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    static class Note{
        String noteId;
        String userId;

        public Note(String noteId, String userId) {
            this.noteId = noteId;
            this.userId = userId;
        }
    }
    @Test
    public void gsonTest() {
        Map map = new HashMap();
        map.put("Ok", false);
        map.put("Msg", null);
        List<Note> list = new ArrayList<>();
        list.add(new Note("10000", "10000"));
        list.add(new Note("10001", "100001"));
        map.put("data", list);
        System.out.println(new Gson().toJson(map));
    }
}