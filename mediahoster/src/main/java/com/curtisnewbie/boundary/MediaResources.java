package com.curtisnewbie.boundary;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

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
        return Response.ok(list).build();
    }

    @GET
    @Produces("video/mp4")
    public void getExampleMedia(@Suspended AsyncResponse asyncResponse, @QueryParam("filename") String filename) {
        logger.info("GET - Retrieving Media File - \"" + filename + "\".");
        managedExecutor.runAsync(() -> {
            try (final BufferedInputStream in = new BufferedInputStream(scanner.getMediaByName(filename));) {
                final long length = scanner.getMediaSizeByName(filename);
                final Date lastModified = scanner.getMediaLastModifiedByName(filename);

                StreamingOutput streamOut = out -> {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while (in.read(buffer) != -1) {
                        out.write(buffer);
                        out.flush();
                    }
                };

                // .header("Range", String.format("bytes %d-%d/%d", 0, length - 1, length))
                asyncResponse.resume(Response.ok(streamOut).header(HttpHeaders.CONTENT_LENGTH, length)
                        .header("Access-Control-Allow-Origin", "*").header("max-age", "3600").lastModified(lastModified)
                        .build());
            } catch (IOException e) {
                logger.error(e);
            }
        });
    }
}