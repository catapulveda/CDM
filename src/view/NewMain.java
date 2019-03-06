package view;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import modelo.Conexion2;

public class NewMain {

    public static void main(String[] args) throws SQLException {
        
        String text = "one two three four five six seven eight nine ten ";
    JTextArea textArea = new JTextArea(text);
    textArea.setColumns(30);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.append(text);
    textArea.setSize(640, 480);
    JOptionPane.showMessageDialog(null, new JScrollPane( textArea), "Not Truncated!",
        JOptionPane.WARNING_MESSAGE);
    }

}
