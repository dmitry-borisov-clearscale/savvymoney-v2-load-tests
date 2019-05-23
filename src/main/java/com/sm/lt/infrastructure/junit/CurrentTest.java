package com.sm.lt.infrastructure.junit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import lombok.extern.slf4j.Slf4j;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.rometools.utils.Strings;

@Slf4j
public class CurrentTest implements TestRule {

    private static final String TEST_FOLDER = "test";
    private static final String RESULT_FOLDER = "result";

    private Path baseDir = null;

    @Override
    public Statement apply(Statement base, Description description) {
        if (description.getMethodName() == null) {
            log.error("Test method required.Rule should be instance field");
            throw new IllegalStateException("Test method required");
        }
        String folderName = description.getTestClass().getSimpleName() + "-" + description.getMethodName();
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                try {
                    Path path = Paths.get(getRootFolder(), folderName);
                    createFolder(path);
                    createFolder(path.resolve(TEST_FOLDER));
                    createFolder(path.resolve(RESULT_FOLDER));
                    baseDir = path;
                    base.evaluate();
                } finally {
                    baseDir = null;
                }
            }
        };
    }

    private static void createFolder(Path folder) throws IOException {
        File file = folder.toFile();
        if (file.exists()) {
            FileUtils.deleteDirectory(file);
            log.info("Folder {} deleted", folder);
        }
        boolean mkdirs = file.mkdirs();
        log.info("Folder {} created: {}", folder, mkdirs);
        if (!mkdirs) {
            log.error("Folder {} not created", folder);
            throw new IllegalStateException("Dirs were not created");
        }
    }

    private static String getRootFolder() {
        String env = System.getProperty("SAVVY_JMETER_TESTS_FOLDER");
        return Strings.isNotEmpty(env)
                ? env
                : "target" + File.separator + "jmeter";
    }

    public void saveToTestFolder(String fileName, String fileBody) throws IOException {
        if (baseDir == null) {
            throw new IllegalStateException("baseDir was not initialized");
        }

        Path result = baseDir.resolve(TEST_FOLDER).resolve(fileName);
        File file = result.toFile();
        if (file.exists()) {
            boolean deleted = file.delete();
            log.info("File {} deleted: {}", result, deleted);
        }

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] strToBytes = fileBody.getBytes();
            outputStream.write(strToBytes);
        }
    }

    public Path copyToTestFolder(String fileName, String resourceName) throws URISyntaxException, IOException {
        URL resource = Resources.getResource(resourceName);
        File source = new File(resource.toURI());

        Path result = baseDir.resolve(TEST_FOLDER).resolve(fileName);
        File resultFile = result.toFile();
        if (resultFile.exists()) {
            boolean deleted = resultFile.delete();
            log.info("File {} deleted: {}", result, deleted);
        }
        Files.copy(source, resultFile);
        return result;
    }

    public Path getResultsFolder() {
        return baseDir.resolve(RESULT_FOLDER);
    }

    public Path getTestFolder() {
        return baseDir.resolve(TEST_FOLDER);
    }
}