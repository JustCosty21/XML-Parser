package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        List<Path> orderFiles = getOrderFiles();

        for(Path path : orderFiles) {
            Main.processFile(path);
        }
    }

    private static void processFile(Path path) throws IOException {
        Document doc = Jsoup.parse(Files.readString(path), "", Parser.xmlParser());
        for (Element e : doc.select("product")) {
            System.out.println(e);
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
