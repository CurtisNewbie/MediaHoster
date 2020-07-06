package com.curtisnewbie.boundary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

/**
 * ------------------------------------
 * 
 * Author: Yongjie Zhuang
 * 
 * ------------------------------------
 * <p>
 * A StreamingOutput specifically for byte-range requests
 * </p>
 */
public class MediaStreaming implements StreamingOutput {

    private long from;
    private long to;
    private File file;

    public MediaStreaming(File file, long from, long to) {
        this.file = file;
        this.from = from;
        this.to = to;
    }

    @Override
    public void write(OutputStream output) throws IOException, WebApplicationException {
        try (FileChannel inChannel = new FileInputStream(file).getChannel();
                WritableByteChannel outChannel = Channels.newChannel(output)) {
            inChannel.transferTo(from, to - from + 1, outChannel);
        } catch (IOException e) {
        }
    }
}