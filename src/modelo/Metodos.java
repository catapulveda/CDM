package modelo;
 
import ds.desktop.notify.DesktopNotify;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.ProgressMonitor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

/**
 *
 * @author AUXPLANTA
 */
public class Metodos {    
    
    public static void ERROR(Exception e, String mensaje){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        JOptionPane.showMessageDialog(null, mensaje+"\n"+sw.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        escribirFichero(e);
    }
    
    public static void JTableToExcel(JTable table, JButton btn){
        ProgressMonitor pm = new ProgressMonitor(table.getParent(), "Exportando a excel...", "", 0, table.getRowCount());        
        (new Thread(){
            @Override
            public void run(){
                FileOutputStream fileOut = null; 
                try{
                    btn.setEnabled(false);
                    btn.setIcon(new ImageIcon(getClass().getResource("/recursos/images/gif.gif")));
                    XSSFWorkbook wb = new XSSFWorkbook();
                    fileOut = new FileOutputStream(table.getName()+".xlsx");
                    XSSFSheet hoja = wb.createSheet(table.getName()+".xlsx");
                    XSSFRow fila;
                    for(int i=0; i<=table.getRowCount(); i++){
                        if(pm.isCanceled()){
                            break;
                        }
                        pm.setNote("Exportando "+i);
                        pm.setProgress(i);
                        fila = hoja.createRow(i);
                        
                        for(int j = 0; j < table.getColumnCount(); j++){
                            if(i==0){
                                fila.createCell(j).setCellValue(table.getColumnName(j));
                            }else{
                                fila.createCell(j).setCellValue((table.getValueAt(i-1, j)==null)?"0":table.getValueAt(i-1, j).toString());
                            }                            
                        }
                    }                    
                    if(!pm.isCanceled()){
                        for(int j = 0; j < table.getColumnCount(); j++) {
                            wb.getSheetAt(0).autoSizeColumn(j);
                        }
                        wb.write(fileOut);
                        fileOut.close();
                        Desktop.getDesktop().open(new File(table.getName()+".xlsx"));
                    }else{
                        System.err.println("CANCELADO");
                    }
                } catch (Exception ex) {
                    ERROR(ex, "ERROR AL EXPORTAR EL ARCHIVO EXCEL.");
                    Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
                }finally{
                    try {
                        fileOut.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
                    }finally{
                        btn.setEnabled(true);
                        btn.setIcon(new ImageIcon(getClass().getResource("/recursos/images/excel.png")));
                    }
                    btn.setEnabled(true);
                    btn.setIcon(new ImageIcon(getClass().getResource("/recursos/images/excel.png")));
                }
            }
        }).start(); 
    }
    
    public static void M(String m, String i) {
        JOptionPane.showMessageDialog(null, m, "Aviso", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(Metodos.class.getClass().getResource("/recursos/images/"+i)));
    }
    
    public static int getConsecutivoRemision(String empresa, boolean aumentar){
        int ID_REMISION = 0;
        ConexionBD conexion = new ConexionBD();
        try {
            conexion.conectar();
            if(aumentar){
                ResultSet rs = conexion.CONSULTAR("SELECT nextval('"+empresa+"') ");
                rs.next();
                ID_REMISION = rs.getInt("nextval");
            }else{
                ResultSet rs = conexion.CONSULTAR(" SELECT last_value FROM "+empresa);
                rs.next();
                ID_REMISION = rs.getInt("last_value");
            }
        }catch(Exception ex){
            Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
            modelo.Metodos.ERROR(ex, "ERROR AL GENERAR EL CONSECUTIVO "+empresa);
        }finally{
            conexion.CERRAR();
        }
        return ID_REMISION;
    }
    
    public static int getUltimoID(String tabla, String col){
        ConexionBD conexion = new ConexionBD();
        try {            
            conexion.conectar();
            ResultSet rs = conexion.CONSULTAR("SELECT MAX("+col+") FROM "+tabla+" ");
            rs.next();
            return rs.getInt("max");
        } catch (SQLException ex) {
            Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            conexion.CERRAR();
        }
        return -1;
    }
    
    public static void cargarCombo(String sql, JComboBox combo){
        ConexionBD conexion = new ConexionBD();
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(sql);
        try {
            while(rs.next()){
                if(rs.getString(1) != null && !rs.getString(1).isEmpty()){
                    combo.addItem(rs.getString(1));
                }              
            }
        } catch (SQLException ex) {
            Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static BufferedImage byteToBufferedImage(byte[] bytes){
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException ex) {
            Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static byte[] BufferedImageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream os =  new  ByteArrayOutputStream (); 
        ImageIO.write(image,  "png" , os ); 
        os.flush();
        return os.toByteArray();
    }
    
    public static BufferedImage imageToBufferedImage(Image img){
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null),
        BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();    
        return bufferedImage;
    }
    
    public static ArrayList getFirma(String cargo){
        ConexionBD con = new ConexionBD();
        con.conectar();
        ArrayList list = new ArrayList();
        ResultSet rs = con.CONSULTAR("SELECT firma, nombre FROM personal WHERE cargo='"+cargo+"' ");
        try {
            rs.next();        
            list.add(0, rs.getBytes("firma"));
            list.add(1, rs.getString("nombre"));
        } catch (SQLException ex) {
            Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            con.CERRAR();
        }
        return list;
    }
    
    public static Icon getIcon(String icono){
        return new ImageIcon(Metodos.class.getResource("/recursos/images/"+icono));
    }
    
    public static void escribirFichero(Exception e){
        File archivo = new File("ERORR.txt");
        BufferedWriter bw = null;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        if(archivo.exists()) {
            try {
                bw = new BufferedWriter(new FileWriter(archivo, true));
                bw.write(sw.toString()+"\n\n\n");
            } catch (IOException ex) {
                Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                bw = new BufferedWriter(new FileWriter(archivo));
                bw.write(sw.toString()+"\n\n\n");
            } catch (IOException ex) {
                Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void generarGrafica(DefaultCategoryDataset dataSet, String arriba, String abajo, String valores, JPanel panel){
        JFreeChart chart = ChartFactory.createBarChart3D(arriba, abajo, valores, dataSet, PlotOrientation.VERTICAL,true,true,true);
        ChartPanel chartPanelPlanesUsuario = new ChartPanel(chart);
        chartPanelPlanesUsuario.setFont(new Font("Ebrima", Font.BOLD, 12));
        
        CategoryItemRenderer renderer = chart.getCategoryPlot().getRenderer();
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.BOTTOM_CENTER  ));
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(new Font("Ebrima", Font.BOLD, 12));
        
        CategoryPlot plot = chart.getCategoryPlot();
//        plot.setBackgroundImage(new ImageIcon(Metodos.class.getClass().getResource("/recursos/images/logo.png")).getImage());
//        plot.setBackgroundImageAlignment(JLabel.CENTER);

        panel.removeAll();
        panel.add(chartPanelPlanesUsuario, BorderLayout.CENTER);
    }
    
    public static String convertirAMoneda(Object texto){
        DecimalFormatSymbols separadores=new DecimalFormatSymbols();
        separadores.setDecimalSeparator('.');
        separadores.setGroupingSeparator(',');
        DecimalFormat formateadorPagos = new DecimalFormat("###,###,###,###.##",separadores);
        return formateadorPagos.format(Float.parseFloat(texto.toString()));
    }
    
    public static void borrarFilasDeTabla(JTable tabla, String tablaBD, String col, String and){
        int[] filas = tabla.getSelectedRows();
        if(JOptionPane.showConfirmDialog(null, "Desea eliminar "+filas.length+" las filas seleccionadas?")==JOptionPane.YES_OPTION){
            java.sql.Connection cc = new ConexionBD().conectar();
            try {
                java.sql.Statement st = cc.createStatement();
                for (int i = filas.length-1; i >= 0; i--) {
                    String query = "DELETE FROM "+tablaBD+" WHERE "+col+"="+tabla.getValueAt(filas[i], 0)+" "+and;
                    if(st.executeUpdate(query)>0){
                        ((CustomTableModel)tabla.getModel()).removeRow(filas[i]);
                        System.out.println("BIEN -> "+query);
                    }
                }
                DesktopNotify.showDesktopMessage(
                        "Registros eliminados!", 
                        "Se han eliminado "+filas.length+" registros.", DesktopNotify.SUCCESS, 5000);
            }catch(org.postgresql.util.PSQLException ex){
                M("PSQLException: "+ex.getMessage(), "error.png");
                Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
            }catch (SQLException ex) {                
                M("SQLException: "+ex.getMessage(), "error.png");
                Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                try {
                    cc.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
