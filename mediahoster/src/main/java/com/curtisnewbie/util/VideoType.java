package com.curtisnewbie.util;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class VideoType {

    /** MediaType supported by HTML Video */
    private static final Set<String> videoType = new TreeSet<>(Arrays.asList("ogg", "mp4", "webm"));

    private VideoType() {
    }

    public static boolean contains(String type) {
        return videoType.contains(type);
    }
}