package com.curtisnewbie.util;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.enterprise.event.Observes;

import org.jboss.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;

/**
 * Class that observes {@code ShutdownEvent} and provides
 * {@link AppLifeCycle#isRunning()} method to notify insteresting parties that
 * the app is currently shutting down.
 */
public class AppLifeCycle {
    private static Logger logger = Logger.getLogger(AppLifeCycle.class);
    private static AtomicBoolean running = new AtomicBoolean(true);

    protected static void onClose(@Observes ShutdownEvent se) {
        logger.info("Application shutting down.");
        running.set(false);
    }

    /**
     * Return whether current application is running. This method returns false when
     * a CTRL+C is pressed.
     */
    public static boolean isRunning() {
        return running.get();
    }
}