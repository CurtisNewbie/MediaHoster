package com.curtisnewbie.util;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.runtime.annotations.CommandLineArguments;

@ApplicationScoped
public class CLIConfig {

    private static final String DIRECTORY_PREFIX = "-DDir=";
    private String dir = null;

    public CLIConfig(@CommandLineArguments String[] args) {
        for (String s : args) {
            int i;
            if ((i = s.indexOf(s)) >= 0)
                this.dir = s.substring(i + DIRECTORY_PREFIX.length());
        }
    }

    /**
     * Return directory path specified as CLI arguments, or NULL if not found.
     * 
     * @return directory path
     */
    public String dir() {
        return this.dir;
    }
}