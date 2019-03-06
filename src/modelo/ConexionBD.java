package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AUXPLANTA
 */
public class ConexionBD {
    
    private Connection conexion;
    private Statement statement;
    private String IP = "192.168.10.5", PUERTO = "5432", BD = "CDM", USER = "postgres", PASS = "cdm";   
//    private String IP = "PRODUCCION", PUERTO = "5432", BD = "CDM2", USER = "postgres", PASS = "cdm";   

    public ConexionBD(){
        
    }
    
    public Connection conectar(){
        try {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection("jdbc:postgresql://"+IP+":"+PUERTO+"/"+BD, USER, PASS);
            statement = conexion.createStatement (ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);            
        } catch (ClassNotFoundException | SQLException ex){
            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
            Metodos.ERROR(ex, "ERROR AL CONECTARSE A LA BASE DE DATOS");
            System.exit(0);
        }
        return conexion;
    }
    
    public ResultSet CONSULTAR(String sql){
        ResultSet rs = null;
        try {     
            rs = statement.executeQuery(sql);
            System.out.println("CONSULTA EXITOSA: "+sql+"\n");
        }catch (SQLException ex) {
            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("QUERY ERRONE0: "+sql+"\n");
            Metodos.ERROR(ex, "ERROR AL REALIZAR LA CONSULTA A LA BASE DE DATOS");            
        }
        return rs;
    }
    
    public boolean GUARDAR(String sql){
        try {
            conectar();            
            if(statement.executeUpdate(sql)>0){
                System.out.println("SE EJECUTÓ EL QUERY -> "+sql);
                return true;
            }else{
                System.err.println("NO SE EJECUTÓ EL QUERY -> "+sql);
            }
        }catch (SQLException ex){
            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("NO SE EJECUTÓ EL QUERY -> "+sql);
            Metodos.ERROR(ex, "NO SE PUDO EJECUTAR EL QUERY CORRECTAMENTE\n"+sql);            
        }finally{
            CERRAR();
        }
        return false;
    }
    
    public void CERRAR(){
        try {
            System.out.println("CONEXION CERRADA.");
            conexion.close();
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConexion() {
        return conexion;
    }

    public void setConexion(Connection conexion) {
        this.conexion = conexion;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getPUERTO() {
        return PUERTO;
    }

    public void setPUERTO(String PUERTO) {
        this.PUERTO = PUERTO;
    }

    public String getBD() {
        return BD;
    }

    public void setBD(String BD) {
        this.BD = BD;
    }

    public String getUSER() {
        return USER;
    }

    public void setUSER(String USER) {
        this.USER = USER;
    }

    public String getPASS() {
        return PASS;
    }

    public void setPASS(String PASS) {
        this.PASS = PASS;
    }

}
