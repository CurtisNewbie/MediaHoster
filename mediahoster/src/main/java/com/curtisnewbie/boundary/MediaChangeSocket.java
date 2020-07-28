package com.curtisnewbie.boundary;

import java.util.concurrent.*;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import com.curtisnewbie.util.FileAddedEvent;
import com.curtisnewbie.util.FileRemovedEvent;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * WebSocket endpoint for sending messages to client, notifying them the change of files in backend
 * (media file being deleted or found)
 * </p>
 */
@ServerEndpoint("/event/change")
@ApplicationScoped
public class MediaChangeSocket {

    private final ConcurrentMap<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.getId());
    }

    protected void notifyFileRemoved(@ObservesAsync FileRemovedEvent e) {
        notifyChange(String.format("%s:%s", e.getMsg(), FileRemovedEvent.TYPE));
    }

    protected void notifyFileAdded(@ObservesAsync FileAddedEvent e) {
        notifyChange(String.format("%s:%s", e.getMsg(), FileAddedEvent.TYPE));
    }

    private void notifyChange(String eventMsg) {
        if (eventMsg != null)
            sessions.values().forEach((v) -> {
                v.getAsyncRemote().sendText(eventMsg);
            });
    }
}
