package com.curtisnewbie.util;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class VideoType {

    /** MediaType supported by HTML Video */
    private static final Set<String> videoType;
    /** Max length of extension name among supported media type */
    private static final int MAX_EXT_LEN;
    static {
        List<String> l = Arrays.asList("ogg", "mp4", "webm");
        int max = 0;
        for (String ext : l)
            max = ext.length() > max ? ext.length() : max;
        videoType = new TreeSet<>(l);
        MAX_EXT_LEN = max;
    }

    private VideoType() {
    }

    /**
     * Extract file extension, return NULL if not found
     * 
     * @param path absolute or relative path
     * @return file extension or NULL if not found
     */
    public static String extractFileExt(String path) {
        String fileExt = null;
        int len = path.length();
        int i = path.lastIndexOf('.');
        if (i > 0 && i < len - 1)
            fileExt = path.substring(i + 1);
        return fileExt;
    }

    /**
     * Return wehther the file is a valid/supported video file.
     * <p>
     * This is verified by checking file extension name
     * 
     * @param path file absolute path
     * @return whether the file is a valid/supported video file
     */
    public static boolean isValid(String path) {
        int len = path.length();
        if (len > MAX_EXT_LEN) // avoid checking the whole path
            path = path.substring(len - MAX_EXT_LEN - 2);
        String ext = extractFileExt(path);
        return ext != null && contains(ext);
    }

    public static boolean contains(String type) {
        return videoType.contains(type);
    }
}