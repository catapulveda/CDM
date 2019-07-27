package view;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collector;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.imageio.ImageIO;
import modelo.ConexionBD;

public class NewMain {

    public static void main(String[] args) throws IOException, SQLException {
        
        
        class Venta{
            private String nombre;
            private int cantidad;
            private int valor;

            public Venta(String nombre, int cantidad, int valor) {
                this.nombre = nombre;
                this.cantidad = cantidad;
                this.valor = valor;
            }

            public String getNombre() {
                return nombre;
            }

            public void setNombre(String nombre) {
                this.nombre = nombre;
            }

            public int getCantidad() {
                return cantidad;
            }

            public void setCantidad(int cantidad) {
                this.cantidad = cantidad;
            }

            public int getValor() {
                return valor;
            }

            public void setValor(int valor) {
                this.valor = valor;
            }
            
            
        }
        
        ObservableList<Venta> ventas = FXCollections.observableArrayList();
        
        ventas.add(new Venta("UNO", 2, 1000));
        ventas.add(new Venta("DOS", 2, 2000));
        ventas.add(new Venta("TRES", 2, 3000));
        ventas.add(new Venta("CUATRO", 2, 4000));
        
        double d = ventas.stream().mapToDouble(v -> v.cantidad*v.valor).sum();
        System.out.println(d);
        
        List<String> list = Arrays.asList("NELSON","EDUARDO","CASTIBLANCO","SEPULVEDA");        
        list.forEach(s->System.out.println(s.toLowerCase()));
        
//        String IP = "191.102.239.79";
//        String API_KEY = "at_54l2XewsBjdS1kLva4Ux5EiTdLDgg";
//        String API_URL = "https://geo.ipify.org/api/v1?apiKey=" + API_KEY + "&ipAddress=" + IP;
//        try (java.util.Scanner s = new java.util.Scanner(new java.net.URL(API_URL).openStream())) {
//            System.out.println(s.useDelimiter("\\A").next());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        
//        String dbLocation = "C:\\Users\\PROGRAMADOR\\Desktop\\GeoLite2-City_20190528\\GeoLite2-City.mmdb";
//
//        File database = new File(dbLocation);
//        DatabaseReader dbReader = new DatabaseReader.Builder(database).build();
//
//        InetAddress ipAddress = InetAddress.getByName(IP);
//        CityResponse response = dbReader.city(ipAddress);
//
//        String countryName = response.getCountry().getName();
//        String cityName = response.getCity().getName();
//        String postal = response.getPostal().getCode();
//        String state = response.getLeastSpecificSubdivision().getName();
//        File epm = new File("caribe.png");
//        BufferedImage bImage = ImageIO.read(epm);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ImageIO.write(bImage, "png", bos);
//        byte[] data = bos.toByteArray();
//        
//        String sql = "UPDATE cliente SET logo=? WHERE idcliente=2";
//        ConexionBD con = new ConexionBD();
//        con.conectar();
//        PreparedStatement ps = con.getConexion().prepareStatement(sql);
//        ps.setBytes(1, data);
//        System.out.println(ps.executeUpdate());
    }

}
