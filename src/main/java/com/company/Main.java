package com.company;


import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Main {
    private final static XMLParser writer;

    static {
        writer = new XMLParser();
    }

    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException, SAXException {
        List<Path> orderFiles = writer.getOrderFiles();

        for (Path path : orderFiles) {
            writer.processFile(path);
        }

        System.exit(0);
    }


}
