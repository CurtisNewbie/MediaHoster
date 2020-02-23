package com.curtisnewbie.util;

public class IllegalMediaDirectoryException extends Exception {

    private static final long serialVersionUID = 3974114552637122260L;

    public IllegalMediaDirectoryException() {
        super("Configured path to your media directory is illegal. It must be an absolute path, and it must be a directory/folder.");
    }
}