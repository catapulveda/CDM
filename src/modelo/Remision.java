package modelo;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Remision {        
    
    public static String[] getColumnNames(){
        return new String[]{
            "ID",
            "N° DESPACHO",
            "REMISION",
            "FECHA",
            "CLIENTE",
            "CIUDAD",
            "DESTINO",
            "CONDUCTOR",
            "CEDULA",
            "PLACA",
            "TELEFONO",
            "CONTRATO",
            "CENTRO DE COSTOS",            
            "TIPO",
            "EMPRESA",
            "SERVICIO",
            "FACTURA"
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
            false,//
            false,//
            false,//            
            false,//
            false,
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
            String.class,//"ESTADO
            String.class,//DESCRIPCION
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class
        };
    }        
    
    public static void cargarRemisiones(DefaultTableModel modelo, String tipo, String empresa){
        modelo.ConexionBD con = new ConexionBD();        
            con.conectar();
            String sql = " SELECT * FROM remision r "
                    + "LEFT JOIN despacho d USING (iddespacho) "
                    + "INNER JOIN cliente c ON c.idcliente=r.idcliente WHERE idremision>0  ";
            sql += (tipo.equals("TODAS"))?"":"AND tipo_remision='"+tipo+"' ";
            sql += (empresa.equals("TODAS"))?"":"AND empresa_remision='"+empresa+"' ";
            sql += " ORDER BY fechacreacion DESC ";
            try {
                ResultSet rs = con.CONSULTAR(sql);
                while(rs.next()){
                    modelo.addRow(new Object[]{
                        rs.getInt("idremision"),
                        rs.getString("nodespacho"),
                        rs.getString("numero_remision"),
                        new SimpleDateFormat("EEE, d MMM yyyy").format(rs.getDate("fecha_remision")),
                        rs.getString("nombrecliente"),
                        rs.getString("ciudad_remision"),
                        rs.getString("destino_remision"),
                        rs.getString("conductor_remision"),
                        rs.getString("cedula_remision"),
                        rs.getString("placa_remision"),                        
                        rs.getString("telefono_remision"),
                        rs.getString("contrato_remision"),
                        rs.getString("centrodecostos_remision"),                                    
                        rs.getString("tipo_remision"),
                        rs.getString("empresa_remision"),
                        rs.getString("servicio_remision"),
                        rs.getString("factura_numero")
                    });
                }
            } catch(Exception e){
            JOptionPane.showMessageDialog(null, "Ocurrio un error al cargar la lista de remisiones.\n"+e, "Error", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(Despacho.class.getResource("/recursos/images/error.png")));
            Logger.getLogger(Despacho.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            con.CERRAR();
        }
    }
    
}
