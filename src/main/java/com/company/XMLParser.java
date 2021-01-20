package com.company;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XMLParser {
    private final Scanner sc = new Scanner(System.in);

    public XMLParser() {
    }

    public void createXML(ListMultimap<String, Element> list, String orderNo) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        String howToSort = howToSortFiles();

        list.asMap().forEach((key, collection) -> {
            Document newDoc = docBuilder.newDocument();

            List<Element> elements = new ArrayList<>(collection);

            if (howToSort.equals("0"))
                elements.sort(Comparator.comparingDouble(o -> Double.parseDouble(o.getElementsByTagName("price").item(0).getTextContent())));
            else if (howToSort.equals("1")) {
                elements.sort(Comparator.comparingDouble(o -> (Double.parseDouble(o.getElementsByTagName("price").item(0).getTextContent()))));
                Collections.reverse(elements);
            }

            Element root = newDoc.createElement("products");
            newDoc.appendChild(root);
            for (Element e : elements) {
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
            File file = new File("./output_folder/" + key + orderNo + ".xml");

            StreamResult result = new StreamResult(file);
            try {
                assert transformer != null;
                transformer.transform(source, result);
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        });
    }

    private String howToSortFiles() {
        String[] options = new String[]{"Ascending sort by price", "Descending sort by price", "Random"};
        int response = JOptionPane.showOptionDialog(null, "How do you want this file?", "How to order",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        System.out.println(response);

        return String.valueOf(response);
    }

    /**
     * Method to process the file.
     *
     * @param path
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public void processFile(Path path) throws IOException, ParserConfigurationException, SAXException {
        createOutputFolder();
        MyEntry<String, Document> doc = getDocumentFromPath(path);

        MyEntry<String, ListMultimap<String, Element>> brands = getBrands(doc);

        assert brands != null;

        createXML(brands.getValue(), brands.getKey());
    }


    private void createOutputFolder() {
        File directory = new File("./output_folder");

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * @return list of order files
     */
    public List<Path> getOrderFiles() {
        String pathToDirectory = checkFolder();

        List<Path> orderFiles = null;
        try (Stream<Path> paths = Files.walk(Paths.get(pathToDirectory))) {
            orderFiles = paths.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e1) {
            System.out.println("Folder does not exist!");
        }

        return orderFiles;
    }

    private String checkFolder() {
        String pathToDirectory;
        pathToDirectory = FileBrowser.createWindow();

        return pathToDirectory;
    }

    /**
     * @param path
     * @return a Pair consisting of the order no and the document
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private MyEntry<String, Document> getDocumentFromPath(Path path) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(path.toFile());
        doc.getDocumentElement().normalize();

        String orderNo = getOrderNo(path);

        return new MyEntry<>(orderNo, doc);
    }

    /**
     * @param path
     * @return the order no based on the file name.
     */
    private String getOrderNo(Path path) {

        return path.getFileName().toString()
                .replaceFirst("[.][^.]+$", "").replaceAll("\\D+", "");
    }

    /**
     * @param doc
     * @return order no and a list of products for every supplier
     */
    private MyEntry<String, ListMultimap<String, Element>> getBrands(MyEntry<String, Document> doc) {
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
