package com.curtisnewbie.boundary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.curtisnewbie.util.MediaScanner;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.logging.Logger;

@Path("media")
public class MediaResources {
    // 15mb buffer
    final int BUFFER_SIZE = 1024 * 1024 * 15;

    @Inject
    Logger logger;

    @Inject
    ManagedExecutor managedExecutor;

    @Inject
    MediaScanner scanner;

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMediaList() {
        logger.info("GET - Retrieving Whole Media File List.");
        List<String> list = scanner.getMediaDirList();
        return Response.ok(list).header("Access-Control-Allow-Origin", "http://localhost:4200").build();
    }

    @HEAD
    @Produces("video/mp4")
    public Response header(@QueryParam("filename") String filename) {
        logger.info("HEAD - Retrieving " + filename);
        long len = scanner.getMediaSizeByName(filename);
        if (len > 0)
            return Response.noContent().build();
        else
            return Response.ok().status(Status.PARTIAL_CONTENT).header(HttpHeaders.CONTENT_LENGTH, len)
                    .header("Accept-Ranges", "bytes").header("Access-Control-Allow-Origin", "http://localhost:4200")
                    .build();

    }

    @GET
    @Produces("video/mp4")
    public void getExampleMedia(@Suspended AsyncResponse asyncResponse, @QueryParam("filename") String filename,
            @HeaderParam("Range") String rangeHeader) {
        logger.info("GET - Retrieving Media File - \"" + filename + "\".");
        managedExecutor.runAsync(() -> {
            try (final BufferedInputStream in = new BufferedInputStream(scanner.getMediaByName(filename));) {
                final long length = scanner.getMediaSizeByName(filename);
                final Date lastModified = scanner.getMediaLastModifiedByName(filename);

                // partial content, range specfied, skipped to specific byte
                long from = 0;
                if (rangeHeader != null) {
                    try {
                        // e.g., bytes = 0-123
                        from = Long.parseLong(rangeHeader.split("=")[1].split("-")[0]);
                        in.skip(from);
                    } catch (NumberFormatException e) {
                        // from = 0
                    }
                }
                StreamingOutput streamOut = out -> {
                    BufferedOutputStream bufOut = new BufferedOutputStream(out);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    // only send one chunk/buffer of data
                    if (in.read(buffer) != -1) {
                        bufOut.write(buffer);
                        bufOut.flush();
                    }
                };

                // build header for partial content 206
                ResponseBuilder resp;
                if (from > 0)
                    resp = Response.ok(streamOut).status(Status.PARTIAL_CONTENT);
                else
                    resp = Response.ok(streamOut);

                long to = from + BUFFER_SIZE;
                resp = resp.header("Access-Control-Allow-Origin", "*").lastModified(lastModified)
                        .header("Content-Range",
                                String.format("bytes %d-%d/%d", from, to > length ? length - 1 : to, length))
                        .header("Accept-Ranges", "bytes").header(HttpHeaders.CONTENT_LENGTH, length);
                asyncResponse.resume(resp.build());
            } catch (IOException e) {
                logger.error(e);
            }
        });
    }
}