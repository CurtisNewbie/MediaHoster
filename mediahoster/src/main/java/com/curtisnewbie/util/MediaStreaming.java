package com.curtisnewbie.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

public class MediaStreaming implements StreamingOutput {

    // 5mb buffer
    public static final int BUFFER_SIZE = 1024 * 1024 * 5;

    private long from;
    private long to;
    private InputStream in;

    public MediaStreaming(InputStream in, long from, long to) {
        this.in = in;
        this.from = from;
        this.to = to;
    }

    @Override
    public void write(OutputStream output) throws IOException, WebApplicationException {
        long range = to - from + 1;
        byte[] buffer = new byte[range < BUFFER_SIZE ? (int) range : BUFFER_SIZE];
        while (in.read(buffer) != -1) {
            output.write(buffer);
            output.flush();
            range -= buffer.length;
            if (range <= 0)
                break;
        }
    }

}