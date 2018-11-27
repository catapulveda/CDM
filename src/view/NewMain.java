package view;

import java.sql.ResultSet;
import java.sql.SQLException;
import modelo.Conexion2;

public class NewMain {

    public static void main(String[] args) throws SQLException {
        
        Conexion2 con2 = new Conexion2();        
        ResultSet rs = con2.CONSULTAR("SELECT  * FROM entrada;");
        while(rs.next()){
            System.out.println(rs.getObject(1));
        }
    }

}
