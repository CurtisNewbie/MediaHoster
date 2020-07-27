package com.curtisnewbie.util;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Event for file being removed
 * </p>
 */
public class FileRemovedEvent extends EventWithMsg {

    public static final String TYPE = "removed";

    public FileRemovedEvent(String msg) {
        super(msg);
    }
}
