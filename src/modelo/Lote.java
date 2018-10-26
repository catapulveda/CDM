package modelo;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
 
/**
 *
 * @author AUXPLANTA
 */
public class Lote {
    
    private int idEntrada;
    private Cliente cliente;
    private Ciudad ciudad;
    private Conductor conductor;
    private String idEntradaAlmacen;
    private String lote;
    private String contrato;
    private String op;
    private String centroDeCostos;
    private Date fechaRecepcion;
    private Date fechaRegistro;
    private Date fechaActualizado;
    private Date fechaLiberado;
    private boolean estado;
    private String observacion;
    
    private static int totalLotes = 0;
    
    static final ConexionBD conexion = new ConexionBD();     
    
    public static String[] getColumnNames(){
        return new String[]{"ID",
            "CLIENTE",
            "LOTE",
            "FECHA RECEPCION",
            "CIUDAD",
            "CONDUCTOR",
            "N° ENTRADA",            
            "CONTRATO",
            "OP",
            "CENTRO DE COSTOS",            
            "FECHA REGISTRO",
            "FECHA ULT. ACTUALIZACION",
            "FECHA LIBERADO",
            "ESTADO",
            "ELABORADO POR"
        };
    }
    
    public static Boolean[] getColumnEditables(){
        return new Boolean[]{
            false,//"ID",
            false,//"CLIENTE",
            false,//"LOTE",
            false,//"FECHA RECEPCION",
            false,//"CIUDAD",
            false,//"CONDUCTOR",
            false,//"N° ENTRADA",            
            false,//"CONTRATO",
            false,//"OP",
            false,//"CENTRO DE COSTOS",            
            false,//"FECHA REGISTRO",
            false,//"FECHA REGISTRO",
            false,//"FECHA LIBERADO",
            false,//"ESTADO"
            false//"ELABORADO POR NOMBRE ALMACEN"
        };
    }
    
    public static Class[] getColumnClass(){
        return new Class[]{
            Integer.class,//ID
            Cliente.class,//CLIENTE
            String.class,//LOTE
            Date.class,//FECHA RECEPCION
            Ciudad.class,//CIUDAD
            Conductor.class,//CONDUCTOR
            String.class,//N ENTRADA            
            String.class,//CONTRATO
            String.class,//OP
            String.class,//CENTRO DE COSTOS            
            Date.class,//FECHA REGISTRO
            Date.class,//FECHA ACTUALIZACION
            Date.class,//FECHA LIBERADO
            Boolean.class,//ESTADO
            String.class//NOMBRE USUARIO ALMACEN
        };
    }
    
    public static void cargarLotes(DefaultTableModel modelo, 
            int indiceComboTipoContrato, 
            int idcliente, 
            JComboBox comboBuscarLotePorContrato, 
            JComboBox comboBuscarLotePorLote){
        try {
            String sql = " SELECT e.identrada, e.idcliente, e.idciudad, e.idconductor, e.identradaAlmacen, e.lote, \n";
            sql += " e.contrato, e.op, e.centrodecostos, e.fecharecepcion, e.fecharegistrado, e.fechaactualizado, \n";
            sql += " e.fechaliberado, e.estado, e.observacion, ciu.nombreciudad, ciu.direccionciudad, ciu.telefonociudad, \n";
            sql += " cli.nombrecliente, cli.nitcliente, con.cedulaconductor, con.nombreconductor, usu.nombreusuario \n";
            sql += " FROM entrada e  ";
            sql += " INNER JOIN ciudad ciu ON (e.idciudad = ciu.idciudad)\n ";
            sql += " INNER JOIN cliente cli ON (e.idcliente = cli.idcliente)\n ";
            sql += " INNER JOIN conductor con ON (e.idconductor = con.idconductor)\n ";
            sql += " INNER JOIN usuario usu ON (e.idusuario = usu.idusuario)\n ";
            sql += (idcliente==-1)?"WHERE e.idcliente>"+idcliente+" ":" WHERE e.idcliente="+idcliente+" ";
            sql += (indiceComboTipoContrato==1)?"AND contrato!='PARTICULAR' \n":(indiceComboTipoContrato==2)?"AND contrato='PARTICULAR' \n":"";
            sql += (comboBuscarLotePorContrato.getSelectedIndex()>0)?"AND contrato='"+comboBuscarLotePorContrato.getSelectedItem()+"' ":"";
            sql += (comboBuscarLotePorLote.getSelectedIndex()>0)?" AND lote='"+comboBuscarLotePorLote.getSelectedItem()+"' ":"";
            sql += " ORDER BY fecharecepcion DESC";
            
            conexion.conectar();
            ResultSet rs = conexion.CONSULTAR(sql);
            totalLotes = 0;
            while(rs.next()){
                totalLotes++;
                modelo.addRow(
                    new Object[]{rs.getInt("identrada"), 
                        new Cliente(rs.getInt("idcliente"), rs.getString("nombrecliente"), rs.getString("nitcliente")), 
                        rs.getString("lote"), 
                        new SimpleDateFormat("EEE, d MMM yyyy").format(rs.getDate("fecharecepcion")), 
                        new Ciudad(rs.getInt("idciudad"), rs.getString("nombreciudad"), rs.getString("direccionciudad"), rs.getString("telefonociudad")), 
                        new Conductor(rs.getInt("idconductor"), rs.getString("cedulaconductor"), rs.getString("nombreconductor")), 
                        rs.getString("identradaAlmacen"),                        
                        rs.getString("contrato"),
                        rs.getString("op"), 
                        rs.getString("centrodecostos"),                        
                        new SimpleDateFormat("EEE, d MMM yyyy").format(rs.getDate("fecharegistrado")),
                        (null!=rs.getDate("fechaactualizado"))?new SimpleDateFormat("EEE, d MMM yyyy").format(rs.getDate("fechaactualizado")):"SIN ACTUALIZAR",
                        rs.getDate("fechaliberado"),
                        rs.getBoolean("estado"), 
                        rs.getString("nombreusuario")
                    }
                );                        
            }
            conexion.CERRAR();
        } catch (SQLException ex){
            Logger.getLogger(Lote.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static int getTotalLotes(){
        return totalLotes;
    }
    
}
