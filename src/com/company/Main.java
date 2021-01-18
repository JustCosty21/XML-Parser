package com.company;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        List<Path> orderFiles = getOrderFiles();
        try (Stream<Path> paths = Files.walk(Paths.get("./input_files"))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static List<Path> getOrderFiles() {
        List<Path> orderFiles = null;
        try (Stream<Path> paths = Files.walk(Paths.get("./input_files"))) {
            orderFiles = paths.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return orderFiles;
    }
}
