package modelo;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Despacho {
    
    private int iddespacho;
    private String nodespacho;       

    public Despacho(int iddespacho, String nodespacho) {
        this.iddespacho = iddespacho;
        this.nodespacho = nodespacho;
    }
    
    public static String[] getColumnNames(){
        return new String[]{
            "ID",
            "N° DESPACHO",
            "FECHA CREACION",
            "CLIENTE",
            "ESTADO",
            "DESCRIPCION",
            "PREPARADOR"
        };
    }
    
    public static Boolean[] getColumnEditables(){
        return new Boolean[]{
            true,//
            false,//
            false,//
            false,//            
            false,//
            false,
            false
        };
    }
    
    public static Class[] getColumnClass(){
        return new Class[]{
            Integer.class,//"ID°",
            String.class,//"Nº DESPACHO",
            String.class,//"FECHA",
            String.class,//"CLIENT",            
            Boolean.class,//"ESTADO
            String.class,//DESCRIPCION
            String.class//CREADOR
        };
    }
    
    public static void cargarDespachos(DefaultTableModel modelo){
        modelo.ConexionBD con = new ConexionBD();
        try {            
            con.conectar();
            ResultSet rs = con.CONSULTAR("SELECT * FROM despacho d "
                    + "INNER JOIN cliente c ON d.idcliente=c.idcliente "
                    + "INNER JOIN usuario u USING(idusuario) "
                    + "ORDER BY fecha_despacho DESC");
            while(rs.next()){
                modelo.addRow(new Object[]{
                    rs.getInt("iddespacho"),
                    rs.getString("nodespacho"),
                    new SimpleDateFormat("EEE, d MMM yyyy hh:mm aa").format(rs.getTimestamp("fecha_despacho")),
                    rs.getString("nombrecliente"),
                    rs.getBoolean("estado_despacho"),
                    rs.getString("descripcion_despacho"),
                    rs.getString("nombreusuario")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ocurrio un error al cargar la lista de gastos.\n"+e, "Error", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(Despacho.class.getResource("/recursos/images/error.png")));
            Logger.getLogger(Despacho.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            con.CERRAR();
        }
    }
    
    @Override
    public String toString(){
        return nodespacho;
    }

    public int getIddespacho() {
        return iddespacho;
    }

    public void setIddespacho(int iddespacho) {
        this.iddespacho = iddespacho;
    }

    public String getNodespacho() {
        return nodespacho;
    }

    public void setNodespacho(String nodespacho) {
        this.nodespacho = nodespacho;
    }
    
}
