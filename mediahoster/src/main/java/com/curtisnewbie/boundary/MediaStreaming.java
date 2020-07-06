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

import com.curtisnewbie.Main;

/**
 * ------------------------------------
 * 
 * Author: Yongjie Zhuang
 * 
 * ------------------------------------
 * <p>
 * A StreamingOutput for streaming with byte-range requests. It's expected that
 * a MediaStreaming is ran within separate threads, thus it checks whether the
 * app is currently running through {@link Main#isRunning()}. When the
 * application is expected to be terminated (isRunning() returns false), it
 * stops the current task immediately, such that it won't block the termination.
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
            while (from < to && Main.isRunning()) {
                if (to - from + 1 > 5000) {
                    inChannel.transferTo(from, 5000, outChannel);
                    from += 5000;
                } else {
                    inChannel.transferTo(from, to - from + 1, outChannel);
                }
            }
        } catch (IOException e) {
        }
    }
}