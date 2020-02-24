package com.curtisnewbie.util;

import java.io.File;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;

/**
 * Scan all available media files
 */
@ApplicationScoped
public class MediaScanner {

    @Inject
    private Logger logger;

    @Inject
    @ConfigProperty(name = "path_to_media_directory")
    public String pathToMediaDir;

    @Inject
    @ConfigProperty(name = "default_media_directory")
    public String defaultMediaDir;

    private String mediaDir;

    private void init(@Observes StartupEvent startup) {
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
        if (file.isDirectory() && file.exists())
            return true;

        return file.mkdir();
    }
}