package com.curtisnewbie.util;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Event with msg
 * </p>
 */
public abstract class EventWithMsg {

    private final String msg;

    EventWithMsg(String msg) {
        this.msg = msg;
    }

    /**
     * Get message in this event
     * 
     * @return msg
     */
    public String getMsg() {
        return this.msg;
    }
}
