package modelo;
 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;

/**
 *
 * @author AUXPLANTA
 */
public class Ciudad {

    private int idCiudad;
    private String nombreCiudad;
    private String direccionCiudad;
    private String telefonoCiudad;
    
    static final ConexionBD conexion = new ConexionBD();

    public Ciudad() {
    }
    
    public Ciudad(int idCiudad, String nombreCiudad, String direccionCiudad, String telefonoCiudad){
        this.idCiudad = idCiudad;
        this.nombreCiudad = nombreCiudad;
        this.direccionCiudad = direccionCiudad;
        this.telefonoCiudad = telefonoCiudad;
    }
    
    public int getIdCiudad(){
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public String getNombreCiudad() {
        return nombreCiudad;
    }

    public void setNombreCiudad(String nombreCiudad) {
        this.nombreCiudad = nombreCiudad;
    }

    public String getDireccionCiudad() {
        return direccionCiudad;
    }

    public void setDireccionCiudad(String direccionCiudad) {
        this.direccionCiudad = direccionCiudad;
    }

    public String getTelefonoCiudad() {
        return telefonoCiudad;
    }

    public void setTelefonoCiudad(String telefonoCiudad) {
        this.telefonoCiudad = telefonoCiudad;
    }   
    
    @Override
    public String toString(){
        return nombreCiudad+" "+direccionCiudad;
    }
     
    static ResultSet cargarCiudades(){
        conexion.conectar();
        return conexion.CONSULTAR("SELECT idciudad, nombreciudad, direccionciudad, telefonociudad FROM ciudad ORDER BY nombreciudad ASC");
    }
    
    public static void cargarComboNombreCiudades(JComboBox<Ciudad> combo){
        try {
            ResultSet rs = cargarCiudades();
            while(rs.next()){
                combo.addItem(new Ciudad(rs.getInt("idciudad"), rs.getString("nombreciudad"), rs.getString("direccionciudad"), rs.getString("telefonociudad")));
            }
            conexion.CERRAR();
        } catch (SQLException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);            
        }
    }
    
    public static void cargarComboCiudadesPorCliente(int idCliente, JComboBox<Ciudad> comboCiudad){
        try {
            conexion.conectar();
            ResultSet rs = conexion.CONSULTAR("SELECT ciu.idciudad, ciu.nombreciudad, ciu.direccionciudad, ciu.telefonociudad FROM ciudad ciu JOIN ciudadcliente cc ON ciu.idciudad=cc.idciudad JOIN cliente c ON cc.idcliente=c.idcliente WHERE c.idcliente="+idCliente+" ");
            comboCiudad.removeAllItems();
            while(rs.next()){
                comboCiudad.addItem(new Ciudad(rs.getInt("idciudad"), rs.getString("nombreciudad"), rs.getString("direccionciudad"), rs.getString("telefonociudad")));
            }
            conexion.CERRAR();
        } catch (Exception ex) {
            Logger.getLogger(Ciudad.class.getName()).log(Level.SEVERE, null, ex);
            Metodos.ERROR(ex, "OCURRIO UN ERROR AL INTENTAR CARGAR LAS CIUDADES ASOCIADAS AL CLIENTE SELECCIONADO");
        }
        
    }
    
}
