package com.curtisnewbie.util;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class PropertyConfig {

    @Inject
    @ConfigProperty(name = "path_to_media_directory", defaultValue = "media")
    protected String pathToMediaDir;

    @Inject
    @ConfigProperty(name = "default_media_directory", defaultValue = "media")
    protected String defaultMediaDir;

    @Inject
    @ConfigProperty(name = "init_change_detector", defaultValue = "true")
    protected boolean initDetector;

    /**
     * Get custom media directory
     * 
     * @return
     */
    public String getMediaDir() {
        return pathToMediaDir;
    }

    /**
     * Get default media directory
     * 
     * @return
     */
    public String getDefMediaDir() {
        return defaultMediaDir;
    }

    /**
     * Return whether change detectory should be used
     * 
     * @return
     */
    public boolean shouldInitChangeDetector() {
        return initDetector;
    }
}