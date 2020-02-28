package com.curtisnewbie.boundary;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.curtisnewbie.util.MediaStreaming;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.logging.Logger;

@Path("media")
public class MediaResources {

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

    @HEAD
    @Produces("video/mp4")
    public Response header(@QueryParam("filename") String filename) {
        logger.info("HEAD - Retrieving " + filename);
        if (filename == null)
            return Response.noContent().build();

        long len = scanner.getMediaSizeByName(filename);
        if (len > 0)
            return Response.ok().status(Status.PARTIAL_CONTENT).header("Accept-Ranges", "bytes").build();
        else
            return Response.noContent().build();
    }

    @GET
    @Produces("video/mp4")
    public void getMediaByName(@Suspended AsyncResponse asyncResponse, @QueryParam("filename") String filename,
            @HeaderParam("Range") String rangeHeader) {
        managedExecutor.runAsync(() -> {
            var fileIn = scanner.getMediaByName(filename);
            if (fileIn == null) {
                asyncResponse.resume(Response.noContent().build());
                return;
            }
            try (final BufferedInputStream in = new BufferedInputStream(fileIn)) {
                final long length = scanner.getMediaSizeByName(filename);
                final Date lastModified = scanner.getMediaLastModifiedByName(filename);

                long from = 0;
                long to = length - 1;
                if (rangeHeader != null) {
                    // partial content, range specfied, skipped to specific byte
                    // e.g., bytes = 123-124
                    String fromTo = rangeHeader.split("=")[1];
                    Pattern pattern = Pattern.compile("^(\\d*)\\-(\\d*)$");
                    Matcher matcher = pattern.matcher(fromTo);
                    if (matcher.find()) {
                        if (!matcher.group(1).isEmpty()) {
                            from = Long.parseLong(matcher.group(1));
                            in.skip(from);
                        }
                        if (!matcher.group(2).isEmpty()) {
                            to = Long.parseLong(matcher.group(2));
                        }
                    }
                }
                logger.info("GET - Retrieving Media File - \"" + filename + "\""
                        + (rangeHeader != null ? String.format(" Requested Byte Range %d-%d", from, to) : ""));
                // 216 not satisfiable range
                if (from < 0 || to >= length) {
                    asyncResponse.resume(Response.status(Status.REQUESTED_RANGE_NOT_SATISFIABLE)
                            .header("Content-Range", "*/" + length).build());
                    return;
                } else {
                    StreamingOutput streamOut = new MediaStreaming(in, from, to);
                    ResponseBuilder resp = Response.ok(streamOut);
                    if (rangeHeader != null)
                        // partial content 206
                        resp = resp.status(Status.PARTIAL_CONTENT);

                    resp = resp.lastModified(lastModified)
                            .header("Content-Range", String.format("bytes %d-%d/%d", from, to, length))
                            .header("Accept-Ranges", "bytes").header(HttpHeaders.CONTENT_LENGTH, to - from + 1);
                    asyncResponse.resume(resp.build());
                }
            } catch (IOException e) {
                logger.error(e);
            }
        });
    }

    @GET
    @Path("amount")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getNumberOfMedia() {
        logger.info("GET - Retrieving Amount of Media Files.");
        return Response.ok(scanner.getMediaMapSize()).build();
    }
}