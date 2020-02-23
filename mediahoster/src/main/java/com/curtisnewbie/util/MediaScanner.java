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

    private void init(@Observes StartupEvent startup) {
        if (isValidMediaDir()) {
            logger.info("MediaScanner Successfully init, Media Directory:\"" + pathToMediaDir + "\"");
        } else {
            logger.error(
                    new IllegalMediaDirectoryException().getMessage() + " Media Directory:\"" + pathToMediaDir + "\"");
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
}