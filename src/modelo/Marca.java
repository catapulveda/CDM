package modelo;

import com.mxrck.autocompleter.AutoCompleterCallback;
import com.mxrck.autocompleter.TextAutoCompleter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author AUXPLANTA
 */
public class Marca {

    private int idMarca;
    private String nombreMarca;
    
    static final ConexionBD conexion = new ConexionBD();
    
    public Marca() {
    }   
    
    public Marca(int idMarca, String nombreMarca) {
        this.idMarca = idMarca;
        this.nombreMarca = nombreMarca;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public String getNombreMarca() {
        return nombreMarca;
    }

    public void setNombreMarca(String nombreMarca) {
        this.nombreMarca = nombreMarca;
    }
    
    @Override
    public String toString(){
        return nombreMarca;
    }
    
    public static JTextField getJTextFieldMarcas(){
        JTextField txt = new JTextField();
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR("SELECT * FROM marca");
        ArrayList<Object> marcas = new ArrayList<>();
        try {
            while(rs.next()){
                marcas.add(
                    new Marca(
                        rs.getInt("idmarca"), 
                        rs.getString("nombremarca")
                    )
                );
            }
            conexion.CERRAR();
            TextAutoCompleter cjHerramienta = new TextAutoCompleter(txt, marcas);
            cjHerramienta.setCaseSensitive(true);
            cjHerramienta.setClearOnIncorrect(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERROR AL CARGAR LA LISTA DE MARCAS EN EL AUTOCOMPLETADO\n"+ex);
            Logger.getLogger(Marca.class.getName()).log(Level.SEVERE, null, ex);
        }
        return txt;
    }
    
    public static JTextField getTextFieldHerrajes(){
        JTextField txt = new JTextField();
        ArrayList<Object> herrajes = new ArrayList<>();
        herrajes.add("AUTO");
        herrajes.add("NORMAL");
        TextAutoCompleter cjHerramienta = new TextAutoCompleter(txt, herrajes);
        cjHerramienta.setCaseSensitive(true);
        cjHerramienta.setClearOnIncorrect(true);
        return txt;
    }
    
    public static ArrayList<String> getMarcas(){
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR("SELECT * FROM marca");
        ArrayList<String> marcas = new ArrayList<>();
        try {
            while(rs.next()){
                marcas.add(rs.getString("nombremarca"));
            }
            Collections.sort(marcas);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR AL CARGAR LA LISTA DE MARCAS\n"+ex);
            Logger.getLogger(Marca.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            conexion.CERRAR();
        }
        return marcas;
    }
    
}
