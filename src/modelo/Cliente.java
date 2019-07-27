package modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

public class Cliente {

    private int idCliente;
    private String nombrecliente;
    private String nitCliente;
    
    static final ConexionBD conexion = new ConexionBD();

    public Cliente() {
    }
    
    public Cliente(int idCliente, String nombreCliente, String nitCliente) {
        this.idCliente = idCliente;
        this.nombrecliente = nombreCliente;
        this.nitCliente = nitCliente;
    }
    
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombrecliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombrecliente = nombreCliente;
    }

    public String getNitCliente() {
        return nitCliente;
    }

    public void setNitCliente(String nitCliente) {
        this.nitCliente = nitCliente;
    }
    
    @Override
    public String toString(){
        return nombrecliente;
    }
        
    public static String[] getColumnNames(){
        return new String[]{
            "ID",
            "NOMBRE",
            "NIT"
        };
    }
    
    public static Boolean[] getColumnEditables(){
        return new Boolean[]{
            false,//"ID",
            true,//"CLIENTE",
            true//"LOTE",
        };
    }
    
    public static Class[] getColumnClass(){
        return new Class[]{
            Integer.class,//ID
            String.class,//NOMBRE
            String.class//NIT
        };
    }
    
    public static void cargarClientes(DefaultTableModel model){
        String sql = "SELECT * FROM cliente ORDER  BY nombrecliente";
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(sql);
        try {
            while(rs.next()){
                model.addRow(new Object[]{
                    rs.getInt("idcliente"),
                    rs.getString("nombrecliente"),
                    rs.getString("nitcliente")
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            conexion.CERRAR();
        }
    }
    
    public static void cargarComboNombreClientes(JComboBox<Cliente> combo){
        try {
//            combo.removeAllItems();
            conexion.conectar();
            ResultSet rs = conexion.CONSULTAR("SELECT idcliente, nombrecliente, nitcliente FROM cliente ORDER BY nombrecliente ASC");           
            while(rs.next()){
                combo.addItem(new Cliente(rs.getInt("idcliente"), rs.getString("nombrecliente"), rs.getString("nitcliente")));
            }
            conexion.CERRAR();
        } catch (SQLException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);            
        }
    }
    
    
    
}
