package com.curtisnewbie;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.event.Observes;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main {

    private static AtomicBoolean running = new AtomicBoolean(true);

    public static void main(String... args) {
        Quarkus.run(args);
    }

    protected void onClose(@Observes ShutdownEvent se) {
        running.set(false);
    }

    /**
     * Return whether current application is running. This method returns false when
     * a CTRL+C is pressed.
     * 
     * @return
     */
    public static boolean isRunning() {
        return running.get();
    }
}