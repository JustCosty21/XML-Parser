package com.company;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XMLParser {

    public XMLParser() {

    }

    protected static void createXML(ListMultimap<String, Element> list, String orderNo) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//        Document newDoc = docBuilder.newDocument();
//
//
//        Element root = newDoc.createElement("plm");
//        newDoc.appendChild(root);

        //key = supplier name and file no
        list.asMap().forEach((key, collection) -> {
            Document newDoc = docBuilder.newDocument();


            Element root = newDoc.createElement("plm");
            newDoc.appendChild(root);
            for (Element e : collection) {
                Node copy = newDoc.importNode(e, true);
                root.insertBefore(copy, root.getNextSibling());

            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }
            DOMSource source = new DOMSource(newDoc);
            File file = new java.io.File("./output_folder/" + key + orderNo + ".xml");

            StreamResult result = new StreamResult(file);
            try {
                transformer.transform(source, result);
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        });

    }

    public void processFile(Path path) throws IOException, ParserConfigurationException, SAXException {
        createOutputFolder();
        MyEntry<String, Document> doc = getDocumentFromPath(path);

        MyEntry<String, ListMultimap<String, Element>> brands = getBrands(doc);

        createXML(brands.getValue(), brands.getKey());
    }

    public Boolean createOutputFolder() {
        boolean result = false;

        File directory = new File("./output_folder");

        if (!directory.exists()) {
            result = directory.mkdirs();
        }

        return result;
    }

    public List<Path> getOrderFiles() {
        List<Path> orderFiles = null;
        try (Stream<Path> paths = Files.walk(Paths.get("./input_files"))) {
            orderFiles = paths.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return orderFiles;
    }

    public MyEntry<String, Document> getDocumentFromPath(Path path) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(path.toFile());
        doc.getDocumentElement().normalize();

        String orderNo = getOrderNo(path);

        return new MyEntry<>(orderNo, doc);
    }

    private String getOrderNo(Path path) {

        return path.getFileName().toString()
                .replaceFirst("[.][^.]+$", "").replaceAll("\\D+", "");
    }

    public MyEntry<String, ListMultimap<String, Element>> getBrands(MyEntry<String, Document> doc) {
        ListMultimap<String, Element> musicianMap = ArrayListMultimap.create();

        NodeList nList = doc.getValue().getElementsByTagName("product");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                musicianMap.put(eElement
                        .getElementsByTagName("supplier")
                        .item(0)
                        .getTextContent(), eElement);
            }
        }

        try {
            return new MyEntry<>(doc.getKey(), musicianMap);
        } catch (NullPointerException e) {
            System.out.println("Something went wrong. Please contact me! :)");
            e.printStackTrace();

            return null;
        }
    }
}
