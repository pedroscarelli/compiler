package com.compiler.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

class FileManagerTest {

    private final FileManager fileManager = new FileManager(null);

    private static String path(String... parts) {
        return String.join(File.separator, parts);
    }

    @Test
    void removesTheExtension() {
        assertEquals(path("", "home", "me", "program"), fileManager.removeFileExtension(path("", "home", "me", "program.txt")));
    }

    @Test
    void keepsDotsInFolderNames() {
        assertEquals(path("", "home", "my.project", "program"), fileManager.removeFileExtension(path("", "home", "my.project", "program")));
    }

    @Test
    void keepsHiddenFileNames() {
        assertEquals(path("", "home", ".program"), fileManager.removeFileExtension(path("", "home", ".program")));
    }
}
