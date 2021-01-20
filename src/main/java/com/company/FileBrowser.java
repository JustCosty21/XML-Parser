package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;

public class FileBrowser {

    public static String createWindow() {
        JFrame frame = new JFrame("Swing Tester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String s = createUI(frame);

        frame.setSize(560, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return s;
    }


    private static String createUI(final JFrame frame) {
        JPanel panel = new JPanel();
        LayoutManager layout = new FlowLayout();
        panel.setLayout(layout);

        File file = new File("wrongpath");
        do {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int option = fileChooser.showOpenDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();

                if (!file.exists()) {
                    System.out.println("Folder does not exist.");
                    file = new File("wrongpath");
                } else {
                    if (file.isDirectory()) {
                        File[] files = file.listFiles((d, name) -> (name.contains("order") && name.endsWith(".xml")));

                        assert files != null;
                        if (files.length == 0) {
                            file = new File("wrongpath");
                        }
                    } else {
                        System.out.println("This is a file. Please provide a folder location.");

                        file = new File("wrongpath");
                    }
                }
            } else {
                System.exit(0);
            }

        }while(file.getAbsolutePath().contains("wrongpath"));

        frame.getContentPane().add(panel, BorderLayout.CENTER);

        return file.getAbsolutePath();
    }
}
