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
import com.curtisnewbie.util.AppLifeCycle;

/**
 * ------------------------------------
 * 
 * Author: Yongjie Zhuang
 * 
 * ------------------------------------
 * <p>
 * A StreamingOutput for streaming with byte-range requests. It's expected that
 * a MediaStreaming is ran within separate threads, thus it checks whether the
 * app is currently running through {@link AppLifeCycle#isRunning()}.
 * <p>
 * When the application is expected to be terminated (isRunning() returns
 * false), it stops the current task immediately (however, it depends on the
 * {@code BUFFER_SIZE}, it's more reactive if the {@code BUFFER_SIZE} is
 * smaller), such that it won't block the application shutdown.
 * </p>
 */
public class MediaStreaming implements StreamingOutput {

    private static final int BUFFER_SIZE = 1000;

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
            while (from < to && AppLifeCycle.isRunning()) {
                if (to - from + 1 > BUFFER_SIZE) {
                    inChannel.transferTo(from, BUFFER_SIZE, outChannel);
                    from += BUFFER_SIZE;
                } else {
                    inChannel.transferTo(from, to - from + 1, outChannel);
                }
            }
        } catch (IOException e) {
        }
    }
}