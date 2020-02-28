package com.curtisnewbie.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;

/**
 * Scan all available media files
 */
@ApplicationScoped
public class MediaScanner {

    @Inject
    Logger logger;

    @Inject
    @ConfigProperty(name = "path_to_media_directory")
    String pathToMediaDir;

    @Inject
    @ConfigProperty(name = "default_media_directory")
    String defaultMediaDir;

    @Inject
    ManagedExecutor managedExecutor;

    private String mediaDir;

    /** Concurrent Hash map for media files */
    private Map<String, File> mediaMap = new ConcurrentHashMap<>();

    void init(@Observes StartupEvent startup) {
        if (isValidMediaDir()) {
            logger.info("MediaScanner Successfully init, Media Directory:\"" + pathToMediaDir + "\"");
            mediaDir = pathToMediaDir;
        } else {
            logger.error(
                    "Configured path to your media directory is illegal. It must be an absolute path, and it must be a directory/folder. Configured Media Directory:\""
                            + pathToMediaDir + "\"");
            if (createDefaultMediaDir()) {
                logger.info("Default media directory has been created: Media Directory:\"" + defaultMediaDir
                        + "\". Please place your media files in it.");
                mediaDir = defaultMediaDir;
            } else {
                mediaDir = null;
            }
        }
    }

    /**
     * Check if the configured path to the media directory is valid
     * 
     * @return whether configured path to the media directory is valid
     */
    public boolean isValidMediaDir() {
        if (pathToMediaDir == null || pathToMediaDir.isEmpty())
            return false;

        File file = new File(pathToMediaDir);
        if (file.exists() && file.isDirectory())
            return true;
        else
            return false;
    }

    /**
     * Create default media directory
     * 
     * @return if the default media directory is created
     */
    public boolean createDefaultMediaDir() {
        File file = new File(defaultMediaDir);
        if (file.exists() && file.isDirectory())
            return true;

        return file.mkdir();
    }

    /**
     * <p>
     * Scanning media directory and updating the {@code Map mediaMap} in every 1
     * sec.
     * </p>
     * 
     * @see MediaScanner#scan(Map, File)
     */
    @Scheduled(every = "1s")
    public void scanMediaDir() {
        if (mediaDir != null) {
            Map<String, File> tempMap = new HashMap<>();
            scan(tempMap, new File(mediaDir));
            for (var pair : mediaMap.entrySet()) {
                if (!pair.getValue().exists())
                    mediaMap.remove(pair.getKey());
            }
            // new media files added to the dir
            mediaMap.putAll(tempMap);
        }
    }

    /**
     * Helper method that scans files recursively
     */
    private void scan(Map<String, File> tempMap, File dir) {
        var list = dir.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                scan(tempMap, f);
            } else {
                String path = convertSlash(f.getPath());
                if (!tempMap.containsKey(path)) {
                    tempMap.put(path, f);
                }
            }
        }
    }

    /**
     * Get list of media file paths in string
     * 
     * @return Abstract path name of all media files in a list
     */
    public List<String> getMediaDirList() {
        ArrayList<String> list = new ArrayList<>();
        for (String s : mediaMap.keySet()) {
            list.add(s);
        }
        return list;
    }

    /**
     * Get Size of the {@code Map mediaMap}
     * 
     * @return size of {@code Map mediaMap}
     */
    public int getMediaMapSize() {
        return mediaMap.size();
    }

    /**
     * Get media file inputstream by filename
     * 
     * @param fileName
     * @return the media file, or {@code NULL} if file doesn't exist
     */
    public File getMediaByName(String fileName) {
        fileName = convertSlash(fileName);
        if (mediaMap.containsKey(fileName)) {
            File file = mediaMap.get(fileName);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    /**
     * Check whether the media file exists
     * 
     * @param filename
     * @return whether the media file exists
     */
    public boolean hasMediaFile(String filename) {
        filename = convertSlash(filename);
        if (mediaMap.containsKey(filename))
            return true;
        else
            return false;
    }

    /**
     * Get media file size in bytes by filename
     * 
     * @param fileName
     * @return media file size in bytes, or 0 if file doesn't exist.
     */
    public long getMediaSizeByName(String fileName) {
        fileName = convertSlash(fileName);
        if (mediaMap.containsKey(fileName)) {
            File file = mediaMap.get(fileName);
            if (file.exists()) {
                return file.length();
            }
        }
        return 0;
    }

    /**
     * Get last modified date of media file by filename
     * 
     * @param fileName
     * @return last modified date, or {@code NULL} if file doesn't exist.
     */
    public Date getMediaLastModifiedByName(String fileName) {
        fileName = convertSlash(fileName);
        if (mediaMap.containsKey(fileName)) {
            File file = mediaMap.get(fileName);
            if (file.exists()) {
                return new Date(file.lastModified());
            }
        }
        return null;
    }

    /** Convert all "\" to "/", this is for Windows OS compatibility */
    public String convertSlash(String str) {
        return str.replace("\\", "/");
    }
}