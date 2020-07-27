package com.curtisnewbie.util;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Event for file added/found
 * </p>
 */
public class FileAddedEvent extends EventWithMsg {

    public static final String TYPE = "added";

    public FileAddedEvent(String msg) {
        super(msg);
    }
}
