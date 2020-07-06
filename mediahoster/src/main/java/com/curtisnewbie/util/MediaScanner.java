package com.curtisnewbie.util;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;

/**
 * ------------------------------------
 * 
 * Author: Yongjie Zhuang
 * 
 * ------------------------------------
 * <p>
 * MediaScanner that is reponsible for initialising path the media directory and
 * scanning all available media files in the specified directory.
 * </p>
 * <p>
 * The path to the media directory is loaded from {@code application.properties}
 * file, if the specified one is incorrect, it will use the default one instead.
 * </p>
 * <p>
 * It provides functionality to continuosly watch for changes in the media
 * directory using {@code java.nio.file.WatchService;}. If files in this
 * directory are modified, removed and so on, it will scan the whole directory
 * and update its {@code Map<String, File> mediaMap}.
 * </p>
 * <p>
 * Notice that {@link MediaScanner#initPath()} is a point where the program
 * might exit, when it fails to find a usable directory.
 * </p>
 * 
 * @see {@link MediaScanner#init()}
 * @see {@link MediaScanner#initChangeDetector()}
 */
@ApplicationScoped
public class MediaScanner {

    @Inject
    protected Logger logger;

    @Inject
    protected ManagedExecutor managedExecutor;

    /** Path to the media directory */
    private String mediaDir;

    /** Indicate whether the change detector has started */
    private volatile boolean changeDetectorStarted = false;

    /** Concurrent Hash map for media files */
    private Map<String, File> mediaMap = new ConcurrentHashMap<>();

    private final CLIConfig cliConfig;
    private final PropertyConfig propertyConfig;

    public MediaScanner(CLIConfig cliConfig, PropertyConfig propertyConfig) {
        this.cliConfig = cliConfig;
        this.propertyConfig = propertyConfig;
    }

    void onStart(@Observes StartupEvent startup) {
        initPath();
        logIp();
        managedExecutor.execute(() -> {
            scanMediaDir(); /* scan for the first time */
        });
        if (propertyConfig.shouldInitChangeDetector())
            initChangeDetector();
    }

    /**
     * Log the ip address of this server
     */
    private void logIp() {
        try {
            InetAddress inet = InetAddress.getLocalHost();
            logger.info(String.format("IP: '%s'", inet.getHostAddress()));
        } catch (Exception e) {
            logger.error("Unable to display MediaHoster's IP Address.");
        }
    }

    /**
     * <p>
     * Init path to the media directory
     * </p>
     * <p>
     * When the configured path is incorrect, and it fails to use the default media
     * directory. It will abort and exit the program.
     * </p>
     */
    protected void initPath() {
        // use directory specified in CLI if possible
        String dir = cliConfig.dir();
        // use directory specified in property file as second option
        if (dir == null)
            dir = propertyConfig.getMediaDir();

        if (isValidMediaDir(dir)) {
            logger.info(String.format("MediaScanner Successfully initialised, Media Directory:'%s'", dir));
        } else {
            logger.error(String.format("Media directory: '%s' illegal. Changing to the default directory.", dir));
            dir = propertyConfig.getDefMediaDir();
            if (mkdir(dir)) {
                logger.info(String.format("Using default media directory: '%s'. Please place your media files in it.",
                        dir));
            } else {
                logger.fatal(String.format("Failed to use default media directory: '%s'. Aborting...", dir));
                dir = null;
                System.exit(1);
            }
        }
        mediaDir = dir;
    }

    /**
     * Check if the configured path to the media directory is valid
     * 
     * @return whether configured path to the media directory is valid
     */
    private boolean isValidMediaDir(String mediaDir) {
        if (mediaDir == null || mediaDir.isEmpty())
            return false;

        File file = new File(mediaDir);
        if (file.exists() && file.isDirectory())
            return true;
        else
            return false;
    }

    /**
     * Create directory
     * 
     * @return whether the directory is created
     */
    private boolean mkdir(String dir) {
        File file = new File(dir);
        if (file.exists() && file.isDirectory())
            return true;
        return file.mkdir();
    }

    /**
     * <p>
     * Init a Watch service to watch for changes in the media directory. It will
     * only start for once. Once it's started, the proceeding calls will simply do
     * nothing.
     * </p>
     * <p>
     * Once the change detector has started, it will watch for changes in every 1
     * sec.
     * </p>
     */
    protected void initChangeDetector() {
        if (!changeDetectorStarted && mediaDir != null) {
            changeDetectorStarted = true;
            logger.info("Change detector initialised.");

            new Thread(() -> {
                Path dir = new File(mediaDir).toPath();
                try {
                    WatchService watcher = FileSystems.getDefault().newWatchService();
                    dir.register(watcher, ENTRY_MODIFY, ENTRY_DELETE, ENTRY_CREATE);
                    while (true) {
                        WatchKey key = watcher.poll(1, TimeUnit.SECONDS);
                        if (key != null) {
                            for (var e : key.pollEvents()) {
                                logger.info("Detected changes in media directory.");
                                var kind = e.kind();
                                if (kind == ENTRY_MODIFY || kind == ENTRY_CREATE || kind == ENTRY_DELETE)
                                    scanMediaDir();
                            }
                            key.reset();
                        }
                    }
                } catch (Exception e) {
                    logger.fatal(e);
                }
            }).start();
        }
    }

    protected void clearMediaMap() {
        this.mediaMap.clear();
    }

    /**
     * <p>
     * Scanning media directory and updating the {@code Map mediaMap}
     * </p>
     * 
     * @see MediaScanner#scan(Map, File)
     */
    protected void scanMediaDir() {
        if (mediaDir != null) {
            logger.info(String.format("Scanning Media Directory:'%s'", mediaDir));
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
        String fileExt;
        for (File f : list) {
            if (f.isDirectory()) {
                scan(tempMap, f);
            } else {
                String path = convertSlash(f.getPath());
                if (!tempMap.containsKey(path) && (fileExt = extractFileExt(path)) != null
                        && VideoType.contains(fileExt)) {
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

    /**
     * Extract file extension, return NULL if not found
     * 
     * @param path absolute or relative path
     * @return file extension or NULL if not found
     */
    public String extractFileExt(String path) {
        String fileExt = null;
        int len = path.length();
        int i = path.lastIndexOf('.');
        if (i > 0 && i != len - 1)
            fileExt = path.substring(i + 1);
        return fileExt;
    }
}