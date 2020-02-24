package com.curtisnewbie.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;

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
    private Map<String, File> mediaMap;

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

        if (mediaDir != null) {
            mediaMap = new HashMap<>();
            scanMediaDir();
        } else {
            mediaMap = null;
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
     * Scanning media directory and updating the {@code Map mediaMap}.
     * </p>
     * <p>
     * This method obtains a lock of {@code Map mediaMap} and creates a new thread,
     * which is dedicated to scan the file trees under the media directory. This
     * thread is executed and managed by the container.
     * </p>
     * 
     * @see MediaScanner#scan(Map, File)
     */
    @Transactional
    public void scanMediaDir() {
        managedExecutor.runAsync(() -> {
            synchronized (mediaMap) {
                scan(mediaMap, new File(mediaDir));
            }
        });
    }

    /**
     * Helper method that scans files recursively
     */
    private void scan(Map<String, File> mediaMap, File dir) {
        var list = dir.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                scan(mediaMap, f);
            } else {
                String path = f.getPath();
                if (!mediaMap.containsKey(path)) {
                    mediaMap.put(path, f);
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
}