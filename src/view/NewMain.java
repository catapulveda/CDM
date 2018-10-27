package view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.JOptionPane;

public class NewMain {

    public static void main(String[] args) {
        File miDir = new File("H:\\RECUVA");
        verContenidoFolder(miDir);
    }

    public static void verContenidoFolder(File dir) {
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                
                if (file.isDirectory()) {
                    System.out.println("directorio:" + file.getCanonicalPath());
                    verContenidoFolder(file);
                } else {
                    if(file.getName().endsWith("java")){
//                        FileReader fileR = new FileReader(file);
//                        BufferedReader file2 = new BufferedReader(fileR);
                        Files.move(file.toPath(), new File("REC//"+file.getName()).toPath());
                        System.out.println(file.toPath());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
