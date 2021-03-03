package com.wiremock.WiremockService;

import com.github.tomakehurst.wiremock.standalone.WireMockServerRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class WiremockServiceApplication {

    private final static Logger logger = Logger.getLogger(WiremockServiceApplication.class.getName());

    public static void main(String[] args) throws IOException {

        WireMockServerRunner runner = new WireMockServerRunner();

        String[] rootDirArgs = new String[]{"--root-dir", "./resources", "--local-response-templating"};
        String[] newArgs = Stream.concat(Arrays.stream(args), Arrays.stream(rootDirArgs)).toArray(String[]::new);

        FileHandler fileHandler = new FileHandler("Wiremock.log");
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] fileResources = resolver.getResources("classpath:__files/*.*");
        Resource[] mapResource = resolver.getResources("classpath:mappings/*.*");

        String filePathTxt = "./resources/__files";
        String mappingPathTxt = "./resources/mappings";

        Path resourcePath = Paths.get("./resources");
        Path _filePath = Paths.get(filePathTxt);
        Path mappingsPath = Paths.get(mappingPathTxt);

        if (!Files.exists(resourcePath))
            Files.createDirectory(resourcePath);
        if (!Files.exists(_filePath))
            Files.createDirectory(_filePath);
        if (!Files.exists(mappingsPath))
            Files.createDirectory(mappingsPath);

        copyFilesFromJar(filePathTxt, fileResources);
        copyFilesFromJar(mappingPathTxt, mapResource);

        runner.run(newArgs);
    }

    private static void copyFilesFromJar(String path, Resource[] resources) throws IOException {

        for (Resource resource : resources) {
            logger.info("Found Resource: " + resource);

            InputStream inStream = resource.getInputStream();
            Path targetFile = Paths.get(path + "/" + resource.getFilename());

            Files.copy(inStream, targetFile, REPLACE_EXISTING);
        }
    }
}
