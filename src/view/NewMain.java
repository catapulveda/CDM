package view;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import javax.imageio.ImageIO;
import modelo.ConexionBD;

public class NewMain {

    public static void main(String[] args) throws IOException, SQLException {
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

        File epm = new File("caribe.png");
        BufferedImage bImage = ImageIO.read(epm);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", bos);
        byte[] data = bos.toByteArray();
        
        String sql = "UPDATE cliente SET logo=? WHERE idcliente=2";
        ConexionBD con = new ConexionBD();
        con.conectar();
        PreparedStatement ps = con.getConexion().prepareStatement(sql);
        ps.setBytes(1, data);
        System.out.println(ps.executeUpdate());
    }

}
