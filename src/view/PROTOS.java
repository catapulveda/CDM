package view;

import Dialogos.DialogoTrafosRepetidos;
import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import modelo.Cliente;
import modelo.ConexionBD;
import modelo.CustomTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PROTOS extends javax.swing.JFrame{

    private String ESTADO_TRAFO = null;
    private boolean ACTUALIZANDO = false;
    ConexionBD conex = new ConexionBD();
    private int IDTRAFO = -1, IDPROTOCOLO = -1;
    TableModelListener listenerTablaUno;
    Hilofases alertas;
    modelo.Sesion sesion = modelo.Sesion.getConfigurador(null, -1);
    
    CustomTableModel modeloTabla;
    TableRowSorter rowSorter;
    RowFilter<TableModel, Object>  compoundRowFilter;
    private int IDBUSQUEDA = 3;
    
    private int idUsuario = 1;
    
    TableColumnAdjuster ajustarColumna;
    
    public PROTOS() {
        initComponents();
              
        setExtendedState(Frame.MAXIMIZED_BOTH);
        cjfechasalida.setDate(new java.util.Date());
        cjprotocolo.setText("A-"+modelo.Metodos.getConsecutivoRemision("protocolo", false)+"-"+new SimpleDateFormat("yy").format(new java.util.Date()));
        habilitarCampos((comboFase.getSelectedIndex()==0));
        
        ajustarColumna = new TableColumnAdjuster(tablaProtocolos);
        cargarProtocolos(); 
        
        comboCliente.addItem(new Cliente(-1, "Seleccione...", null));
        modelo.Cliente.cargarComboNombreClientes(comboCliente);
        comboCliente.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
    }
    
    void HallarTensionSerie(){
        String tensionserie = "", nba = "";
        tensionserie = (7000 <= cjvp.getInt() && cjvp.getInt() <= 15000)?"15":(16000 <= cjvp.getInt() && cjvp.getInt() <= 25000)?"25":(26000 <= cjvp.getInt() && cjvp.getInt() <= 38000)?"38":(39000 <= cjvp.getInt() && cjvp.getInt() <= 52000)?"52":(cjvp.getInt() <= 1200)?"1.2":"0";
        nba = (7000 <= cjvp.getInt() && cjvp.getInt() <= 15000)?"95":(16000 <= cjvp.getInt() && cjvp.getInt() <= 25000)?"125":(26000 <= cjvp.getInt() && cjvp.getInt() <= 38000)?"200":(39000 <= cjvp.getInt() && cjvp.getInt() <= 52000)?"250":(cjvp.getInt() <= 1200)?"30":"0";
        tensionserie += (cjvs.getInt()>1200)?"/15":"/1.2";
        nba += (cjvs.getInt()<=1200)?"/30":(7000 <= cjvs.getInt() && cjvs.getInt() <= 15000)?"/95":"0";
        cjtensionSerie.setText(tensionserie);
        cjnba.setText(nba);
    }
    
    public void HallarConexionYPolaridad() {
        if (comboFase.getSelectedIndex() == 0) {
            comboGrupoConexion .setSelectedItem((cjvp.getInt()<=8000)?"Ii6":"Ii0");
            comboPolaridad.setSelectedIndex((cjvp.getInt()<=8000)?1:0);
        } else if (comboFase.getSelectedIndex() == 1) {
            comboGrupoConexion .setSelectedItem("DYn5");
            comboPolaridad.setSelectedIndex(0);
        }
    }
    
    void HallarCorrientes(){
        try{
            cji1.setText(String.valueOf(QD(((cjkva.getDouble() * 1000) / ((comboFase.getSelectedIndex()==0)?1:Math.sqrt(3)) ) / cjvp.getInt(), 2)));
            cji2.setText(String.valueOf(QD(((cjkva.getDouble() * 1000) / ((comboFase.getSelectedIndex()==0)?1:Math.sqrt(3)) ) / cjvs.getInt(), 2)));
        }catch(Exception e){
            Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    void HallarPromedioResistencias(){
        if(comboFase.getSelectedIndex()==0){
            cjproresalta.setText(""+cjuv.getDouble());
            cjproresbaja.setText(""+cjxy.getDouble());
        }else{
            cjproresalta.setText(""+QD((cjuv.getDouble()+cjwu.getDouble()+cjvw.getDouble())/3, 2));
            cjproresbaja.setText(""+QD((cjxy.getDouble()+cjyz.getDouble()+cjzx.getDouble())/3, 2));
        }        
    }
    
    void HallarPromedioCorrientes(){
        if(comboFase.getSelectedIndex()==0){
            cjpromedioi.setText(String.valueOf(QD((cjiu.getDouble() / cji2.getDouble()) * 100, 2)));
        }else{
            cjpromedioi.setText(String.valueOf(QD((((cjiu.getDouble() + cjiv.getDouble() + cjiw.getDouble()) / 3) / cji2.getDouble()) * 100, 2)));
        }        
    }
    
    double I2R(){
        if(comboFase.getSelectedIndex()==0){
            cji2r.setText(""+QD(((Math.pow(cji1.getDouble(), 2) * cjproresalta.getDouble()) + (Math.pow(cji2.getDouble(), 2) * (cjproresbaja.getDouble() / 1000))), 2));
            return ((Math.pow(cji1.getDouble(), 2) * cjproresalta.getDouble()) + (Math.pow(cji2.getDouble(), 2) * (cjproresbaja.getDouble() / 1000)));
        }else{
            cji2r.setText(""+QD(1.5 * ((Math.pow(cji1.getDouble(), 2) * cjproresalta.getDouble()) + (Math.pow(cji2.getDouble(), 2) * (cjproresbaja.getDouble() / 1000))), 2));
            return 1.5 * ((Math.pow(cji1.getDouble(), 2) * cjproresalta.getDouble()) + (Math.pow(cji2.getDouble(), 2) * (cjproresbaja.getDouble() / 1000)));
        }                
    }
    
    double I2R85(){
        cji2ra85.setText(String.valueOf(QD(I2R() * K(), 1)));
        return I2R() * K();
    }
    
    double R() {
        return cjpcumedido.getDouble() / (10 * cjkva.getDouble());
    }
    
    double R85() {
        return R() * K();
    }
    
    double Z() {
        cjimpedancia.setText(""+QD((cjvcc.getDouble() / cjvp.getDouble()) * 100, 3));
        return (cjvcc.getDouble() / cjvp.getDouble()) * 100;
    }
    
    public double Z85() {
        double Z85 = Math.sqrt((Math.pow(R85(), 2)) + (Math.pow(X(), 2)));
        cjimpedancia85.setText(String.valueOf(QD(Z85, 2)));
        return Z85;
    }
    
    double X() {
        return Math.sqrt((Math.pow(Z(), 2)) - (Math.pow(R(), 2)));        
    }
    
    double getkc(){
        return (comboMaterialAlta.getSelectedItem().toString().equalsIgnoreCase("COBRE")&& comboMaterialBaja.getSelectedItem().equals("COBRE"))?234.5:(comboMaterialAlta.getSelectedItem().toString().equalsIgnoreCase("ALUMINIO")&& comboMaterialBaja.getSelectedItem().equals("ALUMINIO"))?225:229;        
    }
    
    double K(){
        double K = 0;
        if(comboRefrigeracion.getSelectedIndex() < 2){
            K = (getkc() + 85) / (getkc() + Double.parseDouble(cjtemperatura.getText()));
        }else if (comboRefrigeracion.getSelectedIndex() == 2){
            switch (comboClaseAislamiento.getSelectedIndex()){
                case 1:
                    K = (getkc() + 75) / (getkc() + Double.parseDouble(cjtemperatura.getText()));
                    break;
                case 2:
                    K = (getkc() + 85) / (getkc() + Double.parseDouble(cjtemperatura.getText()));
                    break;
                case 3:
                    K = (getkc() + 100) / (getkc() + Double.parseDouble(cjtemperatura.getText()));
                    break;
                case 4:
                    K = (getkc() + 120) / (getkc() + Double.parseDouble(cjtemperatura.getText()));
                    break;
                case 5:
                    K = (getkc() + 145) / (getkc() + Double.parseDouble(cjtemperatura.getText()));
                    break;
            }
        }
        return K;
    }
    
    public void CargarTablas() {
        try {
            tablaUno.getModel().removeTableModelListener(listenerTablaUno);
            for (int i = 0; i < 5; i++){
                tablaUno.setValueAt("Posic: "+(i+1), i, 0);
                tablaUno.setValueAt((ACTUALIZANDO)?tablaUno.getValueAt(i, 2):0, i, 2);
                tablaUno.setValueAt((ACTUALIZANDO)?tablaUno.getValueAt(i, 3):0, i, 3);
                tablaUno.setValueAt((ACTUALIZANDO)?tablaUno.getValueAt(i, 4):0, i, 4);
            }
            
            double factor = 1.0;
            tablaUno.setValueAt(Math.round(cjvp.getInt() * factor), conmutador.getSelectedIndex(), 1);
            for (int i = conmutador.getSelectedIndex()+1; i < conmutador.getItemCount(); i++) {//ME PARO UNA FILA DESPUES DE DONDE VA LA POSICION DEL CONMUTADOR
                factor = factor-(factor*0.025);
                System.out.println("HACIA ABAJO FACTOR ES: "+factor);
                tablaUno.setValueAt(Math.round( cjvp.getInt() * factor ), i, 1);
            }
            factor = 1.0;
            for (int i = conmutador.getSelectedIndex()-1; i >= 0; i--) {//ME PARO UNA FILA ANTES DE DONDE VA LA POSICION DEL CONMUTADOR
                factor = factor+(factor*0.025);
                System.out.println("HACIA ARRIBA FACTOR ES: "+factor);
                tablaUno.setValueAt(Math.round( cjvp.getInt() * factor ), i, 1);
            }            
            
            tablaUno.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tablaUno.setCellSelectionEnabled(true);

            for(int i = 0; i < tablaUno.getRowCount(); i++){
                tablaDos.setValueAt(QD((Integer.parseInt(tablaUno.getValueAt(i, 1).toString()) * ((comboFase.getSelectedIndex()==0)?1:Math.sqrt(3)) ) / cjvs.getInt(), 3), i, 0);
                tablaDos.setValueAt(QD((Integer.parseInt(tablaUno.getValueAt(i, 1).toString()) * ((comboFase.getSelectedIndex()==0)?1:Math.sqrt(3)) ) / cjvs.getInt(), 3) * 0.995, i, 1);
                tablaDos.setValueAt(QD((Integer.parseInt(tablaUno.getValueAt(i, 1).toString()) * ((comboFase.getSelectedIndex()==0)?1:Math.sqrt(3)) ) / cjvs.getInt(), 3) * 1.005, i, 2);
            }
            tablaDos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tablaDos.setCellSelectionEnabled(true);
            listenerTablaUno = new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    if(e.getType() == TableModelEvent.UPDATE){
                        if(e.getColumn()>1){
                            if(comboFase.getSelectedIndex()==0 && e.getColumn()==2 || comboFase.getSelectedIndex()==1 && e.getColumn()>2){
                                if( alertas==null || alertas.getState()==java.lang.Thread.State.TERMINATED){
                                    alertas = new Hilofases(e.getFirstRow(), e.getColumn());
                                    alertas.start();
                                }
                            }                            
                        }
                    }
                }
            };
            tablaUno.getModel().addTableModelListener(listenerTablaUno);
        } catch (Exception e) {
            Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, e);
        }      
    }
    
    double QD(double n, double d){
        return Math.round(n * Math.pow(10, d)) / Math.pow(10, d);
    }
    
    void habilitarCampos(boolean ver){
        cjwu.setEnabled(ver);
        cjvw.setEnabled(ver);
        cjyz.setEnabled(ver);
        cjzx.setEnabled(ver);
        cjiv.setEnabled(ver);
        cjiw.setEnabled(ver);
    }
    
    double PCU85() {
        cjpcua85.setText(String.valueOf(QD( ((cjpcumedido.getDouble() - I2R()) / K()) + I2R85(), 1)));
        return QD( ((cjpcumedido.getDouble() - I2R()) / K()) + I2R85(), 1);
    }
    
    public void HallarReg() {                                   
        double REG = QD(Math.sqrt(R85() + Math.pow(X(), 2) + (200 * R85() * 0.8) + (200 * X() * 0.6) + 10000) - 100, 2);
        REG = Math.pow(R85(), 2) + Math.pow(X(), 2) + 200 * R85() * 0.8 + 200 * X() * 0.6 + 10000;
        REG = Math.sqrt(REG);
        REG = REG - 100;
        REG = QD(REG, 2);
        cjreg.setText(String.valueOf(REG));       
    }
    
    void HallarEf(){
        cjef.setText(String.valueOf(QD((0.8 * cjkva.getDouble() * Math.pow(10, 5)) / (0.8 * cjkva.getDouble() * Math.pow(10, 3) + cjpomedido.getDouble() + PCU85()), 2)));
    }
    
    void cargarValores(){
        String servicio = comboServicio.getSelectedItem().toString(), tabla = null;
        int ano = cjano.getInt();
        int vp = cjvp.getInt();
        int fase = Integer.parseInt(comboFase.getSelectedItem().toString());
        if(comboAceite.getSelectedItem().equals("SECO")){
            tabla = (vp<=15000)?"trifasicosecoserie1212":"trifasicosecoserie1512";
        }else if("NUEVO".equals(servicio)||"RECONSTRUIDO".equals(servicio) || cjcliente.getText().equals("EMPRESAS PUBLICAS DE MEDELLIN S.A E.S.P")){
            tabla = (vp<=15000)?(fase==1)?"monofasiconuevo":"trifasiconuevo":(vp > 15000 && vp <= 35000)?(fase==1)?"monofasiconuevoserie35":"trifasiconuevoserie35":null;
        }else if(servicio.equals("REPARACION")){
            if(vp <= 15000){
                tabla = (fase==1)?(ano < 1996)?"monofasicoantesde1996":"monofasicodespuesde1996":(ano < 1996)?"trifasicoantesde1996":"trifasicodespuesde1996";
            }else if (vp > 15000 && vp <= 35000){
                tabla = (fase==1)?(ano >= 1996)?"monofasicoantesde1996serie35":"monofasicodespuesde1996serie35":(ano < 1996)?"trifasicoantesde1996serie35":"trifasicodespuesde1996serie46";
            }else if(vp > 35000 && vp <= 46000 && fase==3 ){
                tabla = "trifasicodespuesde1996serie46";
            }
        }else if(servicio.equals("MANTENIMIENTO")){
            tabla = ESTADO_TRAFO.equals("REPARADO")?(ano < 1996)?(fase == 1)?"monofasicoantesde1996":"trifasicoantesde1996":(fase == 1)?"monofasicodespuesde1996":"trifasicodespuesde1996":(fase == 1)?"monofasiconuevo":"trifasiconuevo";
        }
        if(comboRefrigeracion.getSelectedIndex() == 2){
            tabla = (fase==3&&(vp > 1200 && vp <= 15000) )?"trifasicosecoserie1512":"trifasicosecoserie1212";
        }        
        double kva = getKva(tabla, cjkva.getDouble());
        String sql = "SELECT * FROM " + tabla + " WHERE kva=" + kva;
        conex.conectar();
        ResultSet rs = conex.CONSULTAR(sql);
        try{
            if(rs.next()){
                cjiogarantizado.setText(""+rs.getDouble("io"));
                cjpogarantizado.setText(""+rs.getDouble("po"));
                switch(comboClaseAislamiento.getSelectedIndex()){
                    case 0:cjpcugarantizado.setText(""+rs.getInt("pc"));
                        break;
                    case 1:cjpcugarantizado.setText(""+rs.getInt("pc75"));
                        break;
                    case 2:cjpcugarantizado.setText(""+rs.getInt("pc85"));
                        break;
                    case 3:cjpcugarantizado.setText(""+rs.getInt("pc100"));
                        break;
                    case 4:cjpcugarantizado.setText(""+rs.getInt("pc120"));
                        break;
                    case 5:cjpcugarantizado.setText(""+rs.getInt("pc145"));
                        break;
                }
                
                cjimpedanciagarantizado.setText(""+rs.getDouble("uz"));
            }
        }catch(SQLException ex){
            Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            conex.CERRAR();
        }
    }
    
    public static double getKva(String tabla, double KVAdigitada){
        double kva = 0;
        String sql = "select * from " + tabla + " ORDER BY kva ASC";
        ConexionBD conex = new ConexionBD();
        conex.conectar();
        ResultSet rs = conex.CONSULTAR(sql);
        boolean esta = false;
        try {
            double BD, auxiliar = 0;
            while (rs.next()) {
                System.out.println(rs.getDouble(1));
                BD = rs.getDouble("kva");
                if (BD == KVAdigitada) {
                    kva = BD;
                    esta = true;
                    break;
                } else if (BD < KVAdigitada) {
                    auxiliar = BD;
                } else if(KVAdigitada < ((auxiliar + BD) / 2)) {
                    kva = auxiliar;
                    esta = true;
                    break;
                } else {
                    kva = BD;
                    esta = true;
                    break;
                }
            }
            if (!esta) {
                JOptionPane.showMessageDialog(null, "NO SE ENCONTRO LA POTENCIA DE " + KVAdigitada + " EN LA TABLA " + tabla);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error al buscar el kva mas adecuado\n" + ex);
        } finally {
            conex.CERRAR();
        }
        return kva;
    }
    
    void cargarMedidas() {
        double kva = cjkva.getDouble();        
        cjancho.setText((comboFase.getSelectedIndex()==0)?((kva==3)?"250":(kva==5)?"280":(kva==10)?"320":(kva==15)?"350":(kva==25)?"380":(kva==37.5)?"420":(kva==50)?"450":(kva==75)?"480":"0"):((kva==15)?"350":(kva==30)?"500":(kva==45)?"600":(kva==75)?"620":(kva==112.5)?"700":(kva==150)?"750":(kva==225)?"800":"0"));
        cjlargo.setText((comboFase.getSelectedIndex()==0)?((kva==3)?"250":(kva==5)?"280":(kva==10)?"320":(kva==15)?"350":(kva==25)?"380":(kva==37.5)?"420":(kva==50)?"450":(kva==75)?"480":"0"):((kva==15)?"280":(kva==30)?"320":(kva==45)?"350":(kva==75)?"380":(kva==112.5)?"430":(kva==150)?"480":(kva==225)?"520":"0"));
        cjalto.setText((comboFase.getSelectedIndex()==0)?((kva==3)?"450":(kva==5)?"500":(kva==10)?"550":(kva==15)?"550":(kva==25)?"550":(kva==37.5)?"600":(kva==50)?"650":(kva==75)?"700":"0"):((kva==15)?"500":(kva==30)?"500":(kva==45)?"550":(kva==75)?"600":(kva==112.5)?"650":(kva==150)?"700":(kva==225)?"750":"0"));
        cjelementos.setText((comboFase.getSelectedIndex()==0)?((kva==50)?"6":(kva==75)?"8":"0"):((kva==75)?"6":(kva==112.5)?"10":(kva==150)?"14":(kva==225)?"18":"0"));
        cjlargoelemento.setText((comboFase.getSelectedIndex()==0)?((kva==50||kva==75)?"300":"0"):((kva==75)?"300":(kva==112.5)?"380":(kva==150)?"300":(kva==225)?"300":"0"));
        cjaltoelemento.setText((comboFase.getSelectedIndex()==0)?((kva==50)?"480":(kva==75)?"480":"0"):((kva==75)?"480":(kva==112.5)?"380":(kva==150)?"480":(kva==225)?"480":"0"));
        cjcolor.setText((cjcliente.getText().equals("EMPRESAS PUBLICAS DE MEDELLIN S.A E.S.P"))?"VERDE":"GRIS");
    }
    
    Object getT(int r, int col){return "'"+tablaUno.getValueAt(r, col)+"'";}
    
    void guardarProtocolo(){
        (new Thread(){
            @Override
            public void run(){
                String GUARDAR = null;
                if(!ACTUALIZANDO){
                    GUARDAR = "INSERT INTO public.protocolos(\n" +
            "            idtransformador, codigo, frecuencia, refrigeracion, \n" +
            "            tensionserie, nba, calentamientodevanado, claseaislamiento, alturadiseno, \n" +
            "            derivacionprimaria, i1, i2, temperaturadeensayo, conmutador, \n" +
            "            liquidoaislante, referenciadeaceite, tensionderuptura, metodo, \n" +
            "            tiemporalt, tensiondeprueba, atcontrabt, \n" +
            "            atcontratierra, btcontratierra, grupodeconexion, polaridad, punou, \n" +
            "            punov, punow, pdosu, pdosv, pdosw, ptresu, ptresv, ptresw, pcuatrou, \n" +
            "            pcuatrov, pcuatrow, pcincou, pcincov, pcincow, resuv, resvw, \n" +
            "            reswu, proresuno, materialconductoralta, resxy, resyz, reszx, \n" +
            "            proresdos, materialconductorbaja, iu, iv, iw, promedioi, iogarantizado, \n" +
            "            pomedido, pogarantizado, vcc, pcu, pcua85, pcugarantizado, i2r, \n" +
            "            i2ra85, impedancia, impedancia85, impedanciagarantizada, reg, \n" +
            "            ef, largotanque, anchotanque, altotanque, color, espesor, radiadores, \n" +
            "            largoradiador, altoradiador, observaciones, fechalaboratorio, \n" +
            "            fechaderegistro, estadoservicio, garantia, idusuario)\n" +
            "    VALUES ("+IDTRAFO+", '"+cjprotocolo.getText()+"', '"+comboFrecuencia.getSelectedItem()+"', '"+comboRefrigeracion.getSelectedItem()+"', \n" +
            "            '"+cjtensionSerie.getText()+"', '"+cjnba.getText()+"', '"+cjcalentamientodevanado.getText()+"', '"+comboClaseAislamiento.getSelectedItem()+"', '"+cjaltdiseno.getText()+"', \n" +
            "            '"+comboDerivacion.getSelectedItem()+"', '"+cji1.getText()+"', '"+cji2.getText()+"', '"+cjtemperatura.getText()+"', '"+conmutador.getSelectedItem()+"', \n" +
            "            '"+comboAceite.getSelectedItem()+"', '"+comboReferenciaAceite.getSelectedItem()+"', '"+cjRuptura.getText()+"', '"+cjmetodo.getText()+"', \n" +
            "            '"+cjtiemporalt.getText()+"', '"+comboTensionPrueba.getSelectedItem()+"', '"+cjATcontraBT.getText()+"', \n" +
            "            '"+cjATcontraTierra.getText()+"', '"+cjBTcontraTierra.getText()+"', '"+comboGrupoConexion.getSelectedItem()+"', '"+comboPolaridad.getSelectedItem()+"', \n" +
            "            "+getT(0,2)+", "+getT(0,3)+", "+getT(0,4)+",  \n" +
            "            "+getT(1,2)+", "+getT(1,3)+", "+getT(1,4)+",  \n" +
            "            "+getT(2,2)+", "+getT(2,3)+", "+getT(2,4)+",  \n" +
            "            "+getT(3,2)+", "+getT(3,3)+", "+getT(3,4)+",  \n" +
            "            "+getT(4,2)+", "+getT(4,3)+", "+getT(4,4)+",  \n" +
            "            '"+cjuv.getDouble()+"', '"+cjvw.getDouble()+"','"+cjwu.getDouble()+"', '"+cjproresalta.getText()+"',  \n" +
            "            '"+comboMaterialAlta.getSelectedItem()+"', '"+cjxy.getDouble()+"', '"+cjyz.getDouble()+"', '"+cjzx.getDouble()+"','"+cjproresbaja.getText()+"', '"+comboMaterialBaja.getSelectedItem()+"', "
                     + " '"+cjiu.getDouble()+"', '"+cjiv.getDouble()+"', '"+cjiw.getDouble()+"', '"+cjpromedioi.getText()+"', '"+cjiogarantizado.getText()+"','"+cjpomedido.getText()+"', '"+cjpogarantizado.getText()+"', "
                     + " '"+cjvcc.getText()+"', '"+cjpcumedido.getText()+"', '"+cjpcua85.getText()+"', '"+cjpcugarantizado.getText()+"', '"+cji2r.getText()+"','"+cji2ra85.getText()+"', '"+cjimpedancia.getText()+"', "
                     + " '"+cjimpedancia85.getText()+"', '"+cjimpedanciagarantizado.getText()+"', '"+cjreg.getText()+"', \n" +
            "            '"+cjef.getText()+"', '"+cjlargo.getText()+"', '"+cjancho.getText()+"', '"+cjalto.getText()+"', '"+cjcolor.getText()+"', '"+cjespesor.getText()+"', '"+cjelementos.getText()+"', \n" +
            "            '"+cjlargoelemento.getText()+"', '"+cjaltoelemento.getText()+"', '"+cjobservaciones.getText()+"', '"+cjfechasalida.getDate()+"', \n" +
            "            '"+new java.util.Date()+"', '"+ESTADO_TRAFO+"' , '"+checkGarantia.isSelected()+"' , "+sesion.getIdUsuario()+")";
                }else{
                    GUARDAR = "UPDATE public.protocolos SET\n" +
            "            frecuencia='"+comboFrecuencia.getSelectedItem()+"', refrigeracion='"+comboRefrigeracion.getSelectedItem()+"', \n" +
            "            tensionserie='"+cjtensionSerie.getText()+"', nba='"+cjnba.getText()+"', calentamientodevanado='"+cjcalentamientodevanado.getText()+"', claseaislamiento='"+comboClaseAislamiento.getSelectedItem()+"', alturadiseno='"+cjaltdiseno.getText()+"', \n" +
            "            derivacionprimaria='"+comboDerivacion.getSelectedItem()+"', i1='"+cji1.getText()+"', i2='"+cji2.getText()+"', temperaturadeensayo='"+cjtemperatura.getText()+"', conmutador='"+conmutador.getSelectedItem()+"', \n" +
            "            liquidoaislante='"+comboAceite.getSelectedItem()+"', referenciadeaceite='"+comboReferenciaAceite.getSelectedItem()+"', tensionderuptura='"+cjRuptura.getText()+"', metodo='"+cjmetodo.getText()+"', \n" +
            "            tiemporalt='"+cjtiemporalt.getText()+"', tensiondeprueba='"+comboTensionPrueba.getSelectedItem()+"', atcontrabt='"+cjATcontraBT.getText()+"', \n" +
            "            atcontratierra='"+cjATcontraTierra.getText()+"', btcontratierra='"+cjBTcontraTierra.getText()+"', grupodeconexion='"+comboGrupoConexion.getSelectedItem()+"', polaridad='"+comboPolaridad.getSelectedItem()+"', punou="+getT(0,2)+", \n" +
            "            punov="+getT(0,3)+", punow="+getT(0,4)+", pdosu="+getT(1,2)+", pdosv="+getT(1,3)+", pdosw="+getT(1,4)+", ptresu="+getT(2,2)+", ptresv="+getT(2,3)+", ptresw="+getT(2,4)+", pcuatrou="+getT(3,2)+", \n" +
            "            pcuatrov="+getT(3,3)+", pcuatrow="+getT(3,4)+", pcincou="+getT(4,2)+", pcincov="+getT(4,3)+", pcincow="+getT(4,4)+", resuv='"+cjuv.getDouble()+"', resvw='"+cjvw.getDouble()+"', \n" +
            "            reswu='"+cjwu.getDouble()+"', proresuno='"+cjproresalta.getText()+"', materialconductoralta='"+comboMaterialAlta.getSelectedItem()+"', resxy='"+cjxy.getDouble()+"', resyz='"+cjyz.getDouble()+"', reszx='"+cjzx.getDouble()+"', \n" +
            "            proresdos='"+cjproresbaja.getText()+"', materialconductorbaja='"+comboMaterialBaja.getSelectedItem()+"', iu='"+cjiu.getDouble()+"', iv='"+cjiv.getDouble()+"', iw='"+cjiw.getDouble()+"', promedioi='"+cjpromedioi.getText()+"', iogarantizado='"+cjiogarantizado.getText()+"', \n" +
            "            pomedido='"+cjpomedido.getText()+"', pogarantizado='"+cjpogarantizado.getText()+"', vcc='"+cjvcc.getText()+"', pcu='"+cjpcumedido.getText()+"', pcua85='"+cjpcua85.getText()+"', pcugarantizado='"+cjpcugarantizado.getText()+"', i2r='"+cji2r.getText()+"', \n" +
            "            i2ra85='"+cji2ra85.getText()+"', impedancia='"+cjimpedancia.getText()+"', impedancia85='"+cjimpedancia85.getText()+"', impedanciagarantizada='"+cjimpedanciagarantizado.getText()+"', reg='"+cjreg.getText()+"', \n" +
            "            ef='"+cjef.getText()+"', largotanque='"+cjlargo.getText()+"', anchotanque='"+cjancho.getText()+"', altotanque='"+cjalto.getText()+"', color='"+cjcolor.getText()+"', espesor='"+cjespesor.getText()+"', radiadores='"+cjelementos.getText()+"', \n" +
            "            largoradiador='"+cjlargoelemento.getText()+"', altoradiador='"+cjaltoelemento.getText()+"', observaciones='"+cjobservaciones.getText()+"', fechalaboratorio='"+cjfechasalida.getDate()+"', \n" +
            "            estadoservicio='"+ESTADO_TRAFO+"' , garantia='"+checkGarantia.isSelected()+"' , idusuario="+sesion.getIdUsuario()+" WHERE idprotocolo="+IDPROTOCOLO+" ";
                }
                if(conex.GUARDAR(GUARDAR)){
                    modelo.Metodos.M("PROTOCOLO "+((ACTUALIZANDO)?"ACTUALIZADO":"REGISTRADO"), "bien.png");                    
                    try{                        
                        btnGuardar.setEnabled(false);
                        btnGuardar.setIcon(new ImageIcon(getClass().getResource("/recursos/images/gif.gif")));
                        JasperReport reporte = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PROTOCOLO.jasper").toString()));
                        Map<String, Object> p = new HashMap<String, Object>();
                        p.put("IDPROTOCOLO", (ACTUALIZANDO)?IDPROTOCOLO:modelo.Metodos.getUltimoID("protocolos", "idprotocolo"));
                        p.put("BTcontraAT", cjBTcontraATyTierra.getText());
                        p.put("ATcontraBT", cjATcontraBTyTierra.getText());
                        p.put("tension1", Integer.parseInt(tablaUno.getValueAt(0, 1).toString()));
                        p.put("tension2", Integer.parseInt(tablaUno.getValueAt(1, 1).toString()));
                        p.put("tension3", Integer.parseInt(tablaUno.getValueAt(2, 1).toString()));
                        p.put("tension4", Integer.parseInt(tablaUno.getValueAt(3, 1).toString()));
                        p.put("tension5", Integer.parseInt(tablaUno.getValueAt(4, 1).toString()));
                        JasperPrint jasperprint = JasperFillManager.fillReport(reporte, p, conex.conectar());
                        if(mostrarProtocolo.isSelected()){
                            JasperViewer.viewReport(jasperprint, false);
                        }
                                                
                        String protocolo = cjprotocolo.getText();
                        String cliente = cjcliente.getText().replace("/", "");
                        limpiar();
                        cjprotocolo.setText("A-"+modelo.Metodos.getConsecutivoRemision("protocolo", false)+"-"+new SimpleDateFormat("yy").format(new java.util.Date()));
                        JasperExportManager.exportReportToPdfFile(
                                jasperprint, 
                                System.getProperties().getProperty("user.dir")+"\\PROTOCOLOS PDF\\"+protocolo+"_"+cliente+".pdf");
                                               
//                        if(!ACTUALIZANDO){
//                            cjprotocolo.setText("A-"+modelo.Metodos.getConsecutivoRemision("protocolo", true)+"-"+new SimpleDateFormat("yy").format(new java.util.Date()));
//                        }else{
                            
//                        }
                    }catch(MalformedURLException | JRException | NumberFormatException ex){
                        Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
                        modelo.Metodos.escribirFichero(ex);
                        modelo.Metodos.ERROR(ex, "ERROR AL GENERAR EL PROTOCOLO");
                    }catch(Exception ex){
                        modelo.Metodos.ERROR(ex, "ERROR AL EXPORTAR Y GUARDAR EL ARCHIVO PDF EN LA CARPTEA 'PROTOCOLOS PDF'\nVERIFIQUE QUE EL NOMBRE DEL CLIENTE NO CONTENGA PUNTOS NI CARACTERES ESPECIALES.");
                    }finally{
                        btnGuardar.setIcon(new ImageIcon(getClass().getResource("/recursos/images/guardar.png")));
                        btnGuardar.setEnabled(true);
                    }
                }
            }
        }).start();
    }
    
    void abrirProtocolo(){
        conex.conectar();
        ResultSet rs = conex.CONSULTAR("SELECT * FROM protocolos INNER JOIN transformador t USING(idtransformador) INNER JOIN entrada e ON t.identrada=e.identrada INNER JOIN cliente c ON c.idcliente=e.idcliente WHERE idprotocolo="+IDPROTOCOLO);
        try {
            if(rs.next()){
                ACTUALIZANDO = true;
                cjserie.setText(rs.getString("numeroserie"));
                cjprotocolo.setText(rs.getString("codigo"));
                cjcliente.setText(rs.getString("nombrecliente"));
                cjlote.setText(rs.getString("lote"));
                cjempresa.setText(rs.getString("numeroempresa"));
                cjmarca.setText(rs.getString("marca"));
                cjkva.setText(rs.getString("kvasalida"));
                comboFase.setSelectedItem(rs.getString("fase"));
                cjano.setText(rs.getString("ano"));
                cjvp.setText(rs.getString("tps"));
                cjvs.setText(rs.getString("tss"));
                cjtensionBT.setText(String.valueOf(rs.getInt("tss")*2));
                cjTensionBT2.setText(rs.getString("tss"));
                comboServicio.setSelectedItem(rs.getString("serviciosalida"));
                ESTADO_TRAFO = rs.getString("estadoservicio");
                cjmasa.setText(rs.getString("peso"));
                cjaceite.setText(rs.getString("aceite"));
                CargarTablas();
                habilitarCampos(rs.getString("fase").equals("3"));
                cjcliente.setCaretPosition(0);
                comboFrecuencia.setSelectedItem(rs.getString("frecuencia"));
                comboRefrigeracion.setSelectedItem(rs.getString("refrigeracion"));
                cjtensionSerie.setText(rs.getString("tensionserie"));
                cjnba.setText(rs.getString("nba"));
                cjcalentamientodevanado.setText(rs.getString("calentamientodevanado"));
                comboClaseAislamiento.setSelectedItem(rs.getString("claseaislamiento"));
                cjaltdiseno.setText(rs.getString("alturadiseno"));
                cji1.setText(""+rs.getDouble("i1"));
                cji2.setText(rs.getString("i2"));
                comboDerivacion.setSelectedItem(rs.getString("derivacionprimaria"));
                cjtemperatura.setText(rs.getString("temperaturadeensayo"));
                conmutador.setSelectedItem(rs.getString("conmutador"));
                comboAceite.setSelectedItem(rs.getString("liquidoaislante"));
                comboReferenciaAceite.setSelectedItem(rs.getString("referenciadeaceite"));
                cjRuptura.setText(rs.getString("tensionderuptura"));
                cjmetodo.setText(rs.getString("metodo"));
                cjtiemporalt.setText(rs.getString("tiemporalt"));
                comboTensionPrueba.setSelectedItem(rs.getString("tensiondeprueba"));
                cjATcontraBT.setText(rs.getString("atcontrabt"));
                cjATcontraTierra.setText(rs.getString("atcontratierra"));
                cjBTcontraTierra.setText(rs.getString("btcontratierra"));
                comboGrupoConexion.setSelectedItem(rs.getString("grupodeconexion"));
                comboPolaridad.setSelectedItem(rs.getString("polaridad"));
                tablaUno.setValueAt(rs.getDouble("punou"), 0, 2);tablaUno.setValueAt(rs.getDouble("punov"), 0, 3);tablaUno.setValueAt(rs.getDouble("punow"), 0, 4);
                tablaUno.setValueAt(rs.getDouble("pdosu"), 1, 2);tablaUno.setValueAt(rs.getDouble("pdosv"), 1, 3);tablaUno.setValueAt(rs.getDouble("pdosw"), 1, 4);
                tablaUno.setValueAt(rs.getDouble("ptresu"), 2, 2);tablaUno.setValueAt(rs.getDouble("ptresv"), 2, 3);tablaUno.setValueAt(rs.getDouble("ptresw"), 2, 4);
                tablaUno.setValueAt(rs.getDouble("pcuatrou"), 3, 2);tablaUno.setValueAt(rs.getDouble("pcuatrov"), 3, 3);tablaUno.setValueAt(rs.getDouble("pcuatrow"), 3, 4);
                tablaUno.setValueAt(rs.getDouble("pcincou"), 4, 2);tablaUno.setValueAt(rs.getDouble("pcincov"), 4, 3);tablaUno.setValueAt(rs.getDouble("pcincow"), 4, 4);
                cjuv.setText(""+rs.getDouble("resuv"));
                cjvw.setText(""+rs.getDouble("resvw"));
                cjwu.setText(""+rs.getDouble("reswu"));
                cjproresalta.setText(""+rs.getDouble("proresuno"));
                comboMaterialAlta.setSelectedItem(rs.getString("materialconductoralta"));
                cjxy.setText(""+rs.getDouble("resxy"));
                cjyz.setText(""+rs.getDouble("resyz"));
                cjzx.setText(""+rs.getDouble("reszx"));
                cjproresbaja.setText(""+rs.getDouble("proresdos"));
                comboMaterialBaja.setSelectedItem(rs.getString("materialconductorbaja"));
                cjtensionBT.setText(rs.getString("tss"));
                cjiu.setText(""+rs.getDouble("iu"));
                cjiv.setText(""+rs.getDouble("iv"));
                cjiw.setText(""+rs.getDouble("iw"));
                cjpromedioi.setText(""+rs.getDouble("promedioi"));
                cjiogarantizado.setText(""+rs.getDouble("iogarantizado"));
                cjpomedido.setText(rs.getString("pomedido"));
                cjpogarantizado.setText(""+rs.getDouble("pogarantizado"));
                cjvcc.setText(rs.getString("vcc"));
                cjpcumedido.setText(rs.getString("pcu"));
                cjpcua85.setText(""+rs.getDouble("pcua85"));
                cjpcugarantizado.setText(rs.getString("pcugarantizado"));
                cji2r.setText(""+rs.getDouble("i2r"));
                cji2ra85.setText(""+rs.getDouble("i2ra85"));
                cjimpedancia.setText(""+rs.getDouble("impedancia"));
                cjimpedancia85.setText(""+rs.getDouble("impedancia85"));
                cjimpedanciagarantizado.setText(""+rs.getDouble("impedanciagarantizada"));
                cjreg.setText(rs.getString("reg"));
                cjef.setText(""+rs.getDouble("ef"));
                cjobservaciones.setText(rs.getString("observaciones"));
                cjfechasalida.setDate(rs.getDate("fechalaboratorio"));
                cjlargo.setText(rs.getString("largotanque"));
                cjancho.setText(rs.getString("anchotanque"));
                cjalto.setText(rs.getString("altotanque"));
                cjcolor.setText(rs.getString("color"));
                cjespesor.setText(rs.getString("espesor"));
                cjelementos.setText(rs.getString("radiadores"));
                cjlargoelemento.setText(rs.getString("largoradiador"));
                cjaltoelemento.setText(rs.getString("altoradiador"));
                jTabbedPane1.setSelectedIndex(0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void limpiar(){
        System.out.println(jTabbedPane1.getComponentCount());
        Component c = jTabbedPane1.getComponentAt(0);
        JPanel p = (JPanel) c;
        for (Component com : p.getComponents()){
            if(com instanceof JPanel){
                JPanel panel = (JPanel) com;
                for (Component txt : panel.getComponents()){
                    if(txt instanceof JTextField){
                        ((JTextField) txt).setText("");
                    }
                }
            }
        }
        cjcalentamientodevanado.setText("65");cjtemperatura.setText("30");cjRuptura.setText("40");
        cjmetodo.setText("ASTM 877");cjBTcontraATyTierra.setText("10");cjATcontraBTyTierra.setText("34.5");
        cjtiempoaplicado.setText("60");cjFrecuenciaInducida.setText("414");cjtiempoInducido.setText("17");
        cjespesor.setText("110");cjobservaciones.setText("");cjtiemporalt.setText("60");
        btnGuardar.setIcon(new ImageIcon(getClass().getResource("/recursos/images/Guardar.png")));
        btnGuardar.setEnabled(true);checkGarantia.setSelected(false);        
    }
    
    void cargarProtocolos(){
        modeloTabla = new CustomTableModel(
                new Object[][]{}, 
                modelo.PROTOCOLO.getColumnNames(), 
                tablaProtocolos, 
                modelo.PROTOCOLO.getColumnClass(), 
                modelo.PROTOCOLO.getColumnEditables());
        modelo.PROTOCOLO.cargarProtocolos(modeloTabla);
        
        tablaProtocolos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaProtocolos.setCellSelectionEnabled(true);
        tablaProtocolos.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        
        ajustarColumna.adjustColumns();            
        tablaProtocolos.getColumnModel().getColumn(0).setCellRenderer(new JButtonIntoJTable.BotonEnColumna());                  
        
        rowSorter = new TableRowSorter(modeloTabla);
        tablaProtocolos.setRowSorter(rowSorter); 
    }
    
    void buscarProtocolo(){
        RowFilter<TableModel, Object> serie = RowFilter.regexFilter(cjbuscarPorSerie.getText().toUpperCase(), 3);
        RowFilter<TableModel, Object> cliente = RowFilter.regexFilter((comboCliente.getSelectedIndex()>0)?comboCliente.getSelectedItem().toString():"", 2);
        RowFilter<TableModel, Object> lote = RowFilter.regexFilter(cjBuscarPorLote.getText(), 9);
        RowFilter<TableModel, Object> marca = RowFilter.regexFilter(cjBuscarPorMarca.getText().toUpperCase(), 5);        
        List<RowFilter<TableModel, Object>> filters = new ArrayList<>();
        filters.add(serie);
        filters.add(cliente);
        filters.add(lote);
        filters.add(marca);        
        compoundRowFilter = RowFilter.andFilter(filters);
        rowSorter.setRowFilter(compoundRowFilter);
    }
       
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuProtocolos = new javax.swing.JPopupMenu();
        subMenuAbrirProtocolo = new javax.swing.JMenuItem();
        subMenuEliminar = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cjprotocolo = new CompuChiqui.JTextFieldPopup();
        jLabel80 = new javax.swing.JLabel();
        cjserie = new CompuChiqui.JTextFieldPopup();
        jLabel4 = new javax.swing.JLabel();
        cjempresa = new CompuChiqui.JTextFieldPopup();
        jLabel5 = new javax.swing.JLabel();
        cjmarca = new CompuChiqui.JTextFieldPopup();
        jLabel6 = new javax.swing.JLabel();
        cjkva = new CompuChiqui.JTextFieldPopup();
        jLabel8 = new javax.swing.JLabel();
        comboFase = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cjano = new CompuChiqui.JTextFieldPopup();
        jLabel9 = new javax.swing.JLabel();
        cjvp = new CompuChiqui.JTextFieldPopup();
        jLabel10 = new javax.swing.JLabel();
        cjvs = new CompuChiqui.JTextFieldPopup();
        jLabel1 = new javax.swing.JLabel();
        comboServicio = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        comboFrecuencia = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        comboRefrigeracion = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        cjtensionSerie = new CompuChiqui.JTextFieldPopup();
        jLabel13 = new javax.swing.JLabel();
        cjnba = new CompuChiqui.JTextFieldPopup();
        jLabel14 = new javax.swing.JLabel();
        cjcalentamientodevanado = new CompuChiqui.JTextFieldPopup();
        jLabel15 = new javax.swing.JLabel();
        comboClaseAislamiento = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        cjaltdiseno = new CompuChiqui.JTextFieldPopup();
        jLabel17 = new javax.swing.JLabel();
        cji1 = new CompuChiqui.JTextFieldPopup();
        jLabel18 = new javax.swing.JLabel();
        cji2 = new CompuChiqui.JTextFieldPopup();
        jLabel19 = new javax.swing.JLabel();
        comboDerivacion = new javax.swing.JComboBox<>();
        jLabel29 = new javax.swing.JLabel();
        cjtemperatura = new CompuChiqui.JTextFieldPopup();
        jLabel30 = new javax.swing.JLabel();
        conmutador = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        comboAceite = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        comboReferenciaAceite = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        cjRuptura = new CompuChiqui.JTextFieldPopup();
        jLabel23 = new javax.swing.JLabel();
        cjmetodo = new CompuChiqui.JTextFieldPopup();
        jPanel4 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        cjtiemporalt = new CompuChiqui.JTextFieldPopup();
        jLabel25 = new javax.swing.JLabel();
        comboTensionPrueba = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        cjATcontraBT = new CompuChiqui.JTextFieldPopup();
        jLabel27 = new javax.swing.JLabel();
        cjATcontraTierra = new CompuChiqui.JTextFieldPopup();
        jLabel28 = new javax.swing.JLabel();
        cjBTcontraTierra = new CompuChiqui.JTextFieldPopup();
        jPanel5 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        comboGrupoConexion = new javax.swing.JComboBox<>();
        jLabel32 = new javax.swing.JLabel();
        comboPolaridad = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        cjuv = new CompuChiqui.JTextFieldPopup();
        jLabel36 = new javax.swing.JLabel();
        cjxy = new CompuChiqui.JTextFieldPopup();
        jLabel37 = new javax.swing.JLabel();
        cjwu = new CompuChiqui.JTextFieldPopup();
        jLabel38 = new javax.swing.JLabel();
        cjyz = new CompuChiqui.JTextFieldPopup();
        jLabel39 = new javax.swing.JLabel();
        cjvw = new CompuChiqui.JTextFieldPopup();
        jLabel40 = new javax.swing.JLabel();
        cjzx = new CompuChiqui.JTextFieldPopup();
        jLabel41 = new javax.swing.JLabel();
        cjproresalta = new CompuChiqui.JTextFieldPopup();
        jLabel42 = new javax.swing.JLabel();
        cjproresbaja = new CompuChiqui.JTextFieldPopup();
        jLabel43 = new javax.swing.JLabel();
        comboMaterialAlta = new javax.swing.JComboBox<>();
        jLabel44 = new javax.swing.JLabel();
        comboMaterialBaja = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        cjBTcontraATyTierra = new CompuChiqui.JTextFieldPopup();
        jLabel46 = new javax.swing.JLabel();
        cjATcontraBTyTierra = new CompuChiqui.JTextFieldPopup();
        jLabel47 = new javax.swing.JLabel();
        cjtiempoaplicado = new CompuChiqui.JTextFieldPopup();
        jLabel48 = new javax.swing.JLabel();
        cjtensionBT = new CompuChiqui.JTextFieldPopup();
        jLabel49 = new javax.swing.JLabel();
        cjFrecuenciaInducida = new CompuChiqui.JTextFieldPopup();
        jLabel50 = new javax.swing.JLabel();
        cjtiempoInducido = new CompuChiqui.JTextFieldPopup();
        jPanel8 = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        cjTensionBT2 = new CompuChiqui.JTextFieldPopup();
        jLabel52 = new javax.swing.JLabel();
        cjiu = new CompuChiqui.JTextFieldPopup();
        jLabel53 = new javax.swing.JLabel();
        cjiv = new CompuChiqui.JTextFieldPopup();
        jLabel54 = new javax.swing.JLabel();
        cjiw = new CompuChiqui.JTextFieldPopup();
        jLabel55 = new javax.swing.JLabel();
        cjpromedioi = new CompuChiqui.JTextFieldPopup();
        jLabel56 = new javax.swing.JLabel();
        cjiogarantizado = new CompuChiqui.JTextFieldPopup();
        jLabel57 = new javax.swing.JLabel();
        cjpomedido = new CompuChiqui.JTextFieldPopup();
        jLabel58 = new javax.swing.JLabel();
        cjpogarantizado = new CompuChiqui.JTextFieldPopup();
        jPanel9 = new javax.swing.JPanel();
        jLabel59 = new javax.swing.JLabel();
        cjvcc = new CompuChiqui.JTextFieldPopup();
        jLabel60 = new javax.swing.JLabel();
        cjpcumedido = new CompuChiqui.JTextFieldPopup();
        jLabel61 = new javax.swing.JLabel();
        cjpcua85 = new CompuChiqui.JTextFieldPopup();
        jLabel62 = new javax.swing.JLabel();
        cjpcugarantizado = new CompuChiqui.JTextFieldPopup();
        jLabel63 = new javax.swing.JLabel();
        cji2r = new CompuChiqui.JTextFieldPopup();
        jLabel64 = new javax.swing.JLabel();
        cji2ra85 = new CompuChiqui.JTextFieldPopup();
        jLabel65 = new javax.swing.JLabel();
        cjimpedancia = new CompuChiqui.JTextFieldPopup();
        jLabel66 = new javax.swing.JLabel();
        cjimpedancia85 = new CompuChiqui.JTextFieldPopup();
        jLabel67 = new javax.swing.JLabel();
        cjimpedanciagarantizado = new CompuChiqui.JTextFieldPopup();
        jPanel10 = new javax.swing.JPanel();
        jLabel78 = new javax.swing.JLabel();
        cjreg = new CompuChiqui.JTextFieldPopup();
        jLabel79 = new javax.swing.JLabel();
        cjef = new CompuChiqui.JTextFieldPopup();
        jPanel12 = new javax.swing.JPanel();
        jLabel68 = new javax.swing.JLabel();
        cjmasa = new CompuChiqui.JTextFieldPopup();
        jLabel69 = new javax.swing.JLabel();
        cjaceite = new CompuChiqui.JTextFieldPopup();
        jLabel70 = new javax.swing.JLabel();
        cjlargo = new CompuChiqui.JTextFieldPopup();
        jLabel71 = new javax.swing.JLabel();
        cjancho = new CompuChiqui.JTextFieldPopup();
        jLabel72 = new javax.swing.JLabel();
        cjalto = new CompuChiqui.JTextFieldPopup();
        jLabel73 = new javax.swing.JLabel();
        cjcolor = new CompuChiqui.JTextFieldPopup();
        jLabel74 = new javax.swing.JLabel();
        cjespesor = new CompuChiqui.JTextFieldPopup();
        jLabel75 = new javax.swing.JLabel();
        cjelementos = new CompuChiqui.JTextFieldPopup();
        jLabel76 = new javax.swing.JLabel();
        cjlargoelemento = new CompuChiqui.JTextFieldPopup();
        jLabel77 = new javax.swing.JLabel();
        cjaltoelemento = new CompuChiqui.JTextFieldPopup();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaUno = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaDos = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        cjobservaciones = new CompuChiqui.JCTextArea();
        cjcliente = new CompuChiqui.JTextFieldPopup();
        cjlote = new CompuChiqui.JTextFieldPopup();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jToolBar2 = new javax.swing.JToolBar();
        checkGarantia = new javax.swing.JCheckBox();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnGuardar = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        cjfechasalida = new com.toedter.calendar.JDateChooser();
        jPanel14 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel81 = new javax.swing.JLabel();
        cjbuscarPorSerie = new CompuChiqui.JTextFieldPopup();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel82 = new javax.swing.JLabel();
        comboCliente = new javax.swing.JComboBox<>();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jLabel83 = new javax.swing.JLabel();
        cjBuscarPorLote = new CompuChiqui.JTextFieldPopup();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jLabel84 = new javax.swing.JLabel();
        cjBuscarPorMarca = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnGenerarExcel = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablaProtocolos = new javax.swing.JTable();
        jProgressBar1 = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        subMenuItemRecalcular = new javax.swing.JMenuItem();
        mostrarProtocolo = new javax.swing.JCheckBoxMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        subMenuAbrirProtocolo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/ver.png"))); // NOI18N
        subMenuAbrirProtocolo.setText("Abrir");
        subMenuAbrirProtocolo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuAbrirProtocoloActionPerformed(evt);
            }
        });
        menuProtocolos.add(subMenuAbrirProtocolo);

        subMenuEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        subMenuEliminar.setText("Eliminar");
        subMenuEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuEliminarActionPerformed(evt);
            }
        });
        menuProtocolos.add(subMenuEliminar);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setFont(new java.awt.Font("Ebrima", 0, 12)); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Informacion General", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel2.setLayout(new java.awt.GridLayout(22, 2, 0, 2));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Protocolo N°:");
        jPanel2.add(jLabel3);

        cjprotocolo.setEditable(false);
        cjprotocolo.setBackground(new java.awt.Color(255, 255, 255));
        cjprotocolo.setForeground(new java.awt.Color(0, 102, 255));
        cjprotocolo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjprotocolo.setCampodetexto(cjATcontraBT);
        cjprotocolo.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        cjprotocolo.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel2.add(cjprotocolo);

        jLabel80.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel80.setText("Serie N°:");
        jPanel2.add(jLabel80);

        cjserie.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjserie.setCampodetexto(cjATcontraBT);
        cjserie.setPreferredSize(new java.awt.Dimension(100, 20));
        cjserie.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cjserieKeyPressed(evt);
            }
        });
        jPanel2.add(cjserie);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("N° Empresa:");
        jPanel2.add(jLabel4);

        cjempresa.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjempresa.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel2.add(cjempresa);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Marca:");
        jPanel2.add(jLabel5);

        cjmarca.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjmarca.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel2.add(cjmarca);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("KVA:");
        jPanel2.add(jLabel6);

        cjkva.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjkva.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel2.add(cjkva);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Fases:");
        jPanel2.add(jLabel8);

        comboFase.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "3" }));
        comboFase.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboFaseItemStateChanged(evt);
            }
        });
        jPanel2.add(comboFase);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Año:");
        jPanel2.add(jLabel7);

        cjano.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjano.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel2.add(cjano);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Volt. Primario(V):");
        jPanel2.add(jLabel9);

        cjvp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjvp.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel2.add(cjvp);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Volt. Secund.(V):");
        jPanel2.add(jLabel10);

        cjvs.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjvs.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel2.add(cjvs);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Servicio:");
        jPanel2.add(jLabel1);

        comboServicio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NUEVO", "RECONSTRUIDO", "REPARACION", "MANTENIMIENTO" }));
        comboServicio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboServicioItemStateChanged(evt);
            }
        });
        jPanel2.add(comboServicio);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Frecuencia(Hz):");
        jPanel2.add(jLabel2);

        comboFrecuencia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "60", "50" }));
        jPanel2.add(comboFrecuencia);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Refrigeracion:");
        jPanel2.add(jLabel11);

        comboRefrigeracion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ONAN", "ONAF", "AN" }));
        jPanel2.add(comboRefrigeracion);

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Tension Serie:");
        jPanel2.add(jLabel12);

        cjtensionSerie.setEditable(false);
        cjtensionSerie.setBackground(new java.awt.Color(255, 255, 255));
        cjtensionSerie.setForeground(new java.awt.Color(0, 102, 255));
        cjtensionSerie.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel2.add(cjtensionSerie);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("NBA AT/BT:");
        jPanel2.add(jLabel13);

        cjnba.setEditable(false);
        cjnba.setBackground(new java.awt.Color(255, 255, 255));
        cjnba.setForeground(new java.awt.Color(0, 102, 255));
        cjnba.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel2.add(cjnba);

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Calent. Devn:");
        jPanel2.add(jLabel14);

        cjcalentamientodevanado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjcalentamientodevanado.setText("65");
        jPanel2.add(cjcalentamientodevanado);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Clase Aislam.:");
        jPanel2.add(jLabel15);

        comboClaseAislamiento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ao", "A:(75°C)", "E:(85°C)", "B:(100°C)", "F:(120°C)", "H:(145°C)" }));
        comboClaseAislamiento.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboClaseAislamientoItemStateChanged(evt);
            }
        });
        jPanel2.add(comboClaseAislamiento);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Alt Diseño:");
        jPanel2.add(jLabel16);

        cjaltdiseno.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjaltdiseno.setText("1000 MSNM");
        jPanel2.add(cjaltdiseno);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("I1:");
        jPanel2.add(jLabel17);

        cji1.setEditable(false);
        cji1.setBackground(new java.awt.Color(255, 255, 255));
        cji1.setForeground(new java.awt.Color(0, 102, 255));
        cji1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel2.add(cji1);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("I2:");
        jPanel2.add(jLabel18);

        cji2.setEditable(false);
        cji2.setBackground(new java.awt.Color(255, 255, 255));
        cji2.setForeground(new java.awt.Color(0, 102, 255));
        cji2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel2.add(cji2);

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Deriv. Prim:");
        jPanel2.add(jLabel19);

        comboDerivacion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "(-4)*2.5%", "(+1-3)*2.5%", "(+2-2)*2.5%", "(+3-1)*2.5%", "(+4)*2.5%" }));
        comboDerivacion.setSelectedIndex(1);
        jPanel2.add(comboDerivacion);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel29.setText("Temperatura:");
        jPanel2.add(jLabel29);

        cjtemperatura.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjtemperatura.setText("30");
        cjtemperatura.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel2.add(cjtemperatura);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel30.setText("Conmutador:");
        jPanel2.add(jLabel30);

        conmutador.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5" }));
        conmutador.setSelectedIndex(1);
        conmutador.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                conmutadorItemStateChanged(evt);
            }
        });
        jPanel2.add(conmutador);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "1) Liquido Aislante", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel3.setToolTipText("Liquido Aislante");
        jPanel3.setLayout(new java.awt.GridLayout(4, 2, 0, 2));

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Aceite:");
        jPanel3.add(jLabel20);

        comboAceite.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MINERAL", "VEGETAL", "REGENERADO", "SECO" }));
        jPanel3.add(comboAceite);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Referencia:");
        jPanel3.add(jLabel21);

        comboReferenciaAceite.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "HYVOLT", "LUB TROIL TIPO II", "FR3", "EPM", "INHIBIDO TIPO II", "NYTRO IZAR II", "N/A" }));
        jPanel3.add(comboReferenciaAceite);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Ruptura(kv):");
        jPanel3.add(jLabel22);

        cjRuptura.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjRuptura.setText("40");
        cjRuptura.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel3.add(cjRuptura);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Metodo:");
        jPanel3.add(jLabel23);

        cjmetodo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjmetodo.setText("ASTM 877");
        cjmetodo.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel3.add(cjmetodo);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "2) Resist. Aislamiento:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel4.setToolTipText("Resistencia de Aislamiento");
        jPanel4.setLayout(new java.awt.GridLayout(5, 2, 0, 2));

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Tiempo:");
        jPanel4.add(jLabel24);

        cjtiemporalt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjtiemporalt.setText("60");
        cjtiemporalt.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel4.add(cjtiemporalt);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Tens. Prueba:");
        jPanel4.add(jLabel25);

        comboTensionPrueba.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "5000/500", "5000/5000", "500/500" }));
        jPanel4.add(comboTensionPrueba);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("AT contra BT(GΩ):");
        jPanel4.add(jLabel26);

        cjATcontraBT.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjATcontraBT.setAbajo(cjATcontraBTyTierra);
        cjATcontraBT.setArriba(comboTensionPrueba);
        cjATcontraBT.setCampodetexto(cjATcontraTierra);
        cjATcontraBT.setPreferredSize(new java.awt.Dimension(100, 20));
        cjATcontraBT.setValidar(true);
        jPanel4.add(cjATcontraBT);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel27.setText("AT contra Tierra(GΩ):");
        jPanel4.add(jLabel27);

        cjATcontraTierra.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjATcontraTierra.setAbajo(null);
        cjATcontraTierra.setArriba(cjATcontraBT);
        cjATcontraTierra.setCampodetexto(cjBTcontraTierra);
        cjATcontraTierra.setPreferredSize(new java.awt.Dimension(100, 20));
        cjATcontraTierra.setValidar(true);
        jPanel4.add(cjATcontraTierra);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel28.setText("BT contra Tierra(GΩ):");
        jPanel4.add(jLabel28);

        cjBTcontraTierra.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjBTcontraTierra.setCampodetexto(null);
        cjBTcontraTierra.setPreferredSize(new java.awt.Dimension(100, 20));
        cjBTcontraTierra.setValidar(true);
        cjBTcontraTierra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cjBTcontraTierraKeyPressed(evt);
            }
        });
        jPanel4.add(cjBTcontraTierra);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "3) Relacion de Transformacion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel5.setToolTipText("Relacion de Transformacion");
        jPanel5.setLayout(new java.awt.GridLayout(2, 2, 0, 2));

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel31.setText("Grupo de Conexion:");
        jPanel5.add(jLabel31);

        comboGrupoConexion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ii6", "Ii0", "DYn5", "DYn11", "Yy0", "Yy6" }));
        jPanel5.add(comboGrupoConexion);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel32.setText("Polaridad:");
        jPanel5.add(jLabel32);

        comboPolaridad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SUSTRACTIVA", "ADITIVA", "NO APLICA" }));
        jPanel5.add(comboPolaridad);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "4) Resistencia Entre Terminales", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel6.setToolTipText("Resistencia Entre Terminales");
        jPanel6.setLayout(new java.awt.GridLayout(5, 4, 0, 2));

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel35.setText("U-V(Ω):");
        jPanel6.add(jLabel35);

        cjuv.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjuv.setAbajo(cjwu);
        cjuv.setCampodetexto(null);
        cjuv.setPreferredSize(new java.awt.Dimension(100, 20));
        cjuv.setValidar(true);
        cjuv.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cjuvKeyPressed(evt);
            }
        });
        jPanel6.add(cjuv);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel36.setText("X-Y(mΩ):");
        jPanel6.add(jLabel36);

        cjxy.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjxy.setAbajo(cjyz);
        cjxy.setCampodetexto(null);
        cjxy.setPreferredSize(new java.awt.Dimension(100, 20));
        cjxy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjxyKeyReleased(evt);
            }
        });
        jPanel6.add(cjxy);

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel37.setText("V-W(Ω):");
        jPanel6.add(jLabel37);

        cjwu.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjwu.setAbajo(cjvw);
        cjwu.setArriba(cjuv);
        cjwu.setCampodetexto(cjvw);
        cjwu.setPreferredSize(new java.awt.Dimension(100, 20));
        cjwu.setValidar(true);
        jPanel6.add(cjwu);

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel38.setText("Y-Z(mΩ):");
        jPanel6.add(jLabel38);

        cjyz.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjyz.setAbajo(cjvcc);
        cjyz.setArriba(cjxy);
        cjyz.setCampodetexto(cjzx);
        cjyz.setPreferredSize(new java.awt.Dimension(100, 20));
        cjyz.setValidar(true);
        jPanel6.add(cjyz);

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel39.setText("W-U(Ω):");
        jPanel6.add(jLabel39);

        cjvw.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjvw.setArriba(cjwu);
        cjvw.setCampodetexto(cjxy);
        cjvw.setPreferredSize(new java.awt.Dimension(100, 20));
        cjvw.setValidar(true);
        jPanel6.add(cjvw);

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel40.setText("Z-X(mΩ):");
        jPanel6.add(jLabel40);

        cjzx.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjzx.setArriba(cjyz);
        cjzx.setCampodetexto(cjiu);
        cjzx.setPreferredSize(new java.awt.Dimension(100, 20));
        cjzx.setValidar(true);
        jPanel6.add(cjzx);

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel41.setText("Promedio:");
        jPanel6.add(jLabel41);

        cjproresalta.setForeground(new java.awt.Color(0, 102, 255));
        cjproresalta.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjproresalta.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel6.add(cjproresalta);

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel42.setText("Promedio:");
        jPanel6.add(jLabel42);

        cjproresbaja.setForeground(new java.awt.Color(0, 102, 255));
        cjproresbaja.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjproresbaja.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel6.add(cjproresbaja);

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel43.setText("Material:");
        jPanel6.add(jLabel43);

        comboMaterialAlta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "COBRE", "ALUMINIO" }));
        jPanel6.add(comboMaterialAlta);

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel44.setText("Material:");
        jPanel6.add(jLabel44);

        comboMaterialBaja.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "COBRE", "ALUMINIO" }));
        jPanel6.add(comboMaterialBaja);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "5) Ensayo de Aislamiento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel7.setToolTipText("Resistencia Entre Terminales");
        jPanel7.setLayout(new java.awt.GridLayout(6, 2, 0, 2));

        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel45.setText("BT contra AT y Tierra:");
        jPanel7.add(jLabel45);

        cjBTcontraATyTierra.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjBTcontraATyTierra.setText("10");
        cjBTcontraATyTierra.setCampodetexto(cjATcontraBTyTierra);
        cjBTcontraATyTierra.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel7.add(cjBTcontraATyTierra);

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel46.setText("AT contra BT y TierraKv:");
        jPanel7.add(jLabel46);

        cjATcontraBTyTierra.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjATcontraBTyTierra.setText("34.5");
        cjATcontraBTyTierra.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel7.add(cjATcontraBTyTierra);

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel47.setText("Tiempo de Prueba(s):");
        jPanel7.add(jLabel47);

        cjtiempoaplicado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjtiempoaplicado.setText("60");
        cjtiempoaplicado.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel7.add(cjtiempoaplicado);

        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel48.setText("Tension BT:");
        jPanel7.add(jLabel48);

        cjtensionBT.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjtensionBT.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel7.add(cjtensionBT);

        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel49.setText("Frecuencia (Hz):");
        jPanel7.add(jLabel49);

        cjFrecuenciaInducida.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjFrecuenciaInducida.setText("414");
        cjFrecuenciaInducida.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel7.add(cjFrecuenciaInducida);

        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel50.setText("Timpo de Prueba:");
        jPanel7.add(jLabel50);

        cjtiempoInducido.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjtiempoInducido.setText("17");
        cjtiempoInducido.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel7.add(cjtiempoInducido);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "6) Ensayo Sin Carga", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel8.setToolTipText("Resistencia Entre Terminales");
        jPanel8.setLayout(new java.awt.GridLayout(8, 2, 0, 2));

        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel51.setText("Tension BT:");
        jPanel8.add(jLabel51);

        cjTensionBT2.setForeground(new java.awt.Color(0, 102, 255));
        cjTensionBT2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjTensionBT2.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel8.add(cjTensionBT2);

        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel52.setText("Iu(Amp):");
        jPanel8.add(jLabel52);

        cjiu.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjiu.setAbajo(cjiv);
        cjiu.setCampodetexto(null);
        cjiu.setPreferredSize(new java.awt.Dimension(100, 20));
        cjiu.setValidar(true);
        cjiu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cjiuKeyPressed(evt);
            }
        });
        jPanel8.add(cjiu);

        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel53.setText("Iv(Amp):");
        jPanel8.add(jLabel53);

        cjiv.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjiv.setAbajo(cji2ra85);
        cjiv.setArriba(cjiu);
        cjiv.setCampodetexto(cjiw);
        cjiv.setPreferredSize(new java.awt.Dimension(100, 20));
        cjiv.setValidar(true);
        jPanel8.add(cjiv);

        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel54.setText("Iw(Amp):");
        jPanel8.add(jLabel54);

        cjiw.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjiw.setAbajo(cjpomedido);
        cjiw.setArriba(cjiv);
        cjiw.setCampodetexto(cjpomedido);
        cjiw.setPreferredSize(new java.awt.Dimension(100, 20));
        cjiw.setValidar(true);
        jPanel8.add(cjiw);

        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel55.setText("Promedio I(%):");
        jPanel8.add(jLabel55);

        cjpromedioi.setForeground(new java.awt.Color(0, 102, 255));
        cjpromedioi.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjpromedioi.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel8.add(cjpromedioi);

        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel56.setText("Io Garantizado:");
        jPanel8.add(jLabel56);

        cjiogarantizado.setForeground(new java.awt.Color(0, 102, 255));
        cjiogarantizado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjiogarantizado.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel8.add(cjiogarantizado);

        jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel57.setText("Po Medido:");
        jPanel8.add(jLabel57);

        cjpomedido.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjpomedido.setCampodetexto(null);
        cjpomedido.setPreferredSize(new java.awt.Dimension(100, 20));
        cjpomedido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cjpomedidoKeyTyped(evt);
            }
        });
        jPanel8.add(cjpomedido);

        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel58.setText("Po Garantizado:");
        jPanel8.add(jLabel58);

        cjpogarantizado.setForeground(new java.awt.Color(0, 102, 255));
        cjpogarantizado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjpogarantizado.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel8.add(cjpogarantizado);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "7) Ensayo De Corto Circuito", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel9.setToolTipText("Resistencia Entre Terminales");
        jPanel9.setLayout(new java.awt.GridLayout(9, 2, 0, 2));

        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel59.setText("Vcc(V):");
        jPanel9.add(jLabel59);

        cjvcc.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjvcc.setArriba(cjpomedido);
        cjvcc.setCampodetexto(cjpcumedido);
        cjvcc.setPreferredSize(new java.awt.Dimension(100, 20));
        cjvcc.setValidar(true);
        jPanel9.add(cjvcc);

        jLabel60.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel60.setText("Pcc Medido(W):");
        jPanel9.add(jLabel60);

        cjpcumedido.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjpcumedido.setCampodetexto(null);
        cjpcumedido.setPreferredSize(new java.awt.Dimension(100, 20));
        cjpcumedido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cjpcumedidoKeyTyped(evt);
            }
        });
        jPanel9.add(cjpcumedido);

        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel61.setText("Pcc a 85°(W):");
        jPanel9.add(jLabel61);

        cjpcua85.setForeground(new java.awt.Color(0, 102, 255));
        cjpcua85.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjpcua85.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel9.add(cjpcua85);

        jLabel62.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel62.setText("Pcc Garantizado:");
        jPanel9.add(jLabel62);

        cjpcugarantizado.setForeground(new java.awt.Color(0, 102, 255));
        cjpcugarantizado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjpcugarantizado.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel9.add(cjpcugarantizado);

        jLabel63.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel63.setText("I2r(W):");
        jPanel9.add(jLabel63);

        cji2r.setForeground(new java.awt.Color(0, 102, 255));
        cji2r.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cji2r.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel9.add(cji2r);

        jLabel64.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel64.setText("I2r a 85°(W):");
        jPanel9.add(jLabel64);

        cji2ra85.setForeground(new java.awt.Color(0, 102, 255));
        cji2ra85.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cji2ra85.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel9.add(cji2ra85);

        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel65.setText("Impedancia Z (%):");
        jPanel9.add(jLabel65);

        cjimpedancia.setForeground(new java.awt.Color(0, 102, 255));
        cjimpedancia.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjimpedancia.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel9.add(cjimpedancia);

        jLabel66.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel66.setText("Impedancia 85° Z (%):");
        jPanel9.add(jLabel66);

        cjimpedancia85.setForeground(new java.awt.Color(0, 102, 255));
        cjimpedancia85.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjimpedancia85.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel9.add(cjimpedancia85);

        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel67.setText("Impendancia Garantizado:");
        jPanel9.add(jLabel67);

        cjimpedanciagarantizado.setForeground(new java.awt.Color(0, 102, 255));
        cjimpedanciagarantizado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjimpedanciagarantizado.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel9.add(cjimpedanciagarantizado);

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel10.setToolTipText("Resistencia Entre Terminales");
        jPanel10.setLayout(new java.awt.GridLayout(1, 4, 0, 2));

        jLabel78.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel78.setText("8) Regulacion:");
        jPanel10.add(jLabel78);

        cjreg.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjreg.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel10.add(cjreg);

        jLabel79.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel79.setText("9) Eficiencia:");
        jPanel10.add(jLabel79);

        cjef.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjef.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel10.add(cjef);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "10) Caracteristicas Mecanicas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel12.setToolTipText("Resistencia Entre Terminales");
        jPanel12.setLayout(new java.awt.GridLayout(10, 2, 0, 2));

        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel68.setText("Masa(Kg):");
        jPanel12.add(jLabel68);

        cjmasa.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjmasa.setCampodetexto(cjaceite);
        cjmasa.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjmasa);

        jLabel69.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel69.setText("Aceite(L):");
        jPanel12.add(jLabel69);

        cjaceite.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjaceite.setCampodetexto(cjlargo);
        cjaceite.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjaceite);

        jLabel70.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel70.setText("Largo(mm):");
        jPanel12.add(jLabel70);

        cjlargo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjlargo.setCampodetexto(cjancho);
        cjlargo.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjlargo);

        jLabel71.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel71.setText("Ancho(mm):");
        jPanel12.add(jLabel71);

        cjancho.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjancho.setCampodetexto(cjalto);
        cjancho.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjancho);

        jLabel72.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel72.setText("Alto(mm):");
        jPanel12.add(jLabel72);

        cjalto.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjalto.setCampodetexto(cjcolor);
        cjalto.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjalto);

        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel73.setText("Color:");
        jPanel12.add(jLabel73);

        cjcolor.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjcolor.setCampodetexto(cjespesor);
        cjcolor.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjcolor);

        jLabel74.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel74.setText("Espesor(µ):");
        jPanel12.add(jLabel74);

        cjespesor.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjespesor.setText("110");
        cjespesor.setCampodetexto(cjelementos);
        cjespesor.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjespesor);

        jLabel75.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel75.setText("N° Elementos:");
        jPanel12.add(jLabel75);

        cjelementos.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjelementos.setCampodetexto(cjlargoelemento);
        cjelementos.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjelementos);

        jLabel76.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel76.setText("Largo Elemento(mm):");
        jPanel12.add(jLabel76);

        cjlargoelemento.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjlargoelemento.setCampodetexto(cjaltoelemento);
        cjlargoelemento.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjlargoelemento);

        jLabel77.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel77.setText("Ancho Elemento(mm):");
        jPanel12.add(jLabel77);

        cjaltoelemento.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjaltoelemento.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjaltoelemento);

        tablaUno.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "POSICION", "TENSION", "FASE U", "FASE V", "FASE W"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tablaUno.setGridColor(new java.awt.Color(227, 227, 227));
        tablaUno.setRowHeight(20);
        tablaUno.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tablaUnoKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(tablaUno);

        tablaDos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NOMINAL", "MINIMA", "MAXIMA"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaDos.setGridColor(new java.awt.Color(227, 227, 227));
        tablaDos.setRowHeight(20);
        jScrollPane2.setViewportView(tablaDos);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Observaciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel11.setToolTipText("Resistencia Entre Terminales");

        cjobservaciones.setColumns(20);
        cjobservaciones.setRows(5);
        cjobservaciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjobservacionesKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(cjobservaciones);

        cjcliente.setEditable(false);
        cjcliente.setBackground(new java.awt.Color(255, 255, 255));
        cjcliente.setForeground(new java.awt.Color(0, 102, 255));
        cjcliente.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjcliente.setPreferredSize(new java.awt.Dimension(100, 20));

        cjlote.setEditable(false);
        cjlote.setBackground(new java.awt.Color(255, 255, 255));
        cjlote.setForeground(new java.awt.Color(0, 102, 255));
        cjlote.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjlote.setPreferredSize(new java.awt.Dimension(100, 20));

        jLabel33.setText("Cliente:");

        jLabel34.setText("Lote:");

        jToolBar2.setFloatable(false);

        checkGarantia.setText("Garantia");
        checkGarantia.setFocusable(false);
        checkGarantia.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        checkGarantia.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(checkGarantia);
        jToolBar2.add(jSeparator6);

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Guardar.png"))); // NOI18N
        btnGuardar.setToolTipText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        jToolBar2.add(btnGuardar);
        jToolBar2.add(jSeparator5);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/nuevodocumento.png"))); // NOI18N
        jButton2.setToolTipText("Nuevo");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton2);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cjcliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addGap(30, 30, 30)
                        .addComponent(cjlote, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cjcliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cjlote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fecha Salida Laboratorio", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel13.setToolTipText("Resistencia Entre Terminales");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cjfechasalida, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cjfechasalida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(6, 6, 6)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Resultados y Ensayos", jPanel1);

        jToolBar1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar1.setFloatable(false);

        jLabel81.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel81.setText("Serie:");
        jToolBar1.add(jLabel81);

        cjbuscarPorSerie.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjbuscarPorSerieKeyReleased(evt);
            }
        });
        jToolBar1.add(cjbuscarPorSerie);
        jToolBar1.add(jSeparator2);

        jLabel82.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel82.setText("Cliente:");
        jToolBar1.add(jLabel82);

        comboCliente.setMaximumRowCount(20);
        comboCliente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboClienteItemStateChanged(evt);
            }
        });
        jToolBar1.add(comboCliente);
        jToolBar1.add(jSeparator3);

        jLabel83.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel83.setText("Lote:");
        jToolBar1.add(jLabel83);

        cjBuscarPorLote.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjBuscarPorLoteKeyReleased(evt);
            }
        });
        jToolBar1.add(cjBuscarPorLote);
        jToolBar1.add(jSeparator4);

        jLabel84.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel84.setText("Marca:");
        jToolBar1.add(jLabel84);

        cjBuscarPorMarca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjBuscarPorMarcaKeyReleased(evt);
            }
        });
        jToolBar1.add(cjBuscarPorMarca);
        jToolBar1.add(jSeparator1);

        btnGenerarExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/excel.png"))); // NOI18N
        btnGenerarExcel.setFocusable(false);
        btnGenerarExcel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGenerarExcel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGenerarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarExcelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnGenerarExcel);

        tablaProtocolos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PROTOCOLO N°", "CLIENTE", "SERIE", "N° EMPRESA", "MARCA", "POTENCIA", "FASE", "TENSION", "LOTE", "REMISION", "DESPACHO"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tablaProtocolos.setGridColor(new java.awt.Color(227, 227, 227));
        tablaProtocolos.setRowHeight(25);
        tablaProtocolos.getTableHeader().setReorderingAllowed(false);
        tablaProtocolos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaProtocolosMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tablaProtocolos);

        jProgressBar1.setStringPainted(true);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1259, Short.MAX_VALUE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Protocolos", jPanel14);

        jMenu1.setText("Archivo");
        jMenu1.setFont(new java.awt.Font("Ebrima", 1, 12)); // NOI18N
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Editar");
        jMenu2.setFont(new java.awt.Font("Ebrima", 1, 12)); // NOI18N

        subMenuItemRecalcular.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
        subMenuItemRecalcular.setFont(new java.awt.Font("Ebrima", 0, 12)); // NOI18N
        subMenuItemRecalcular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/calculadora.png"))); // NOI18N
        subMenuItemRecalcular.setText("Recalcular");
        subMenuItemRecalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuItemRecalcularActionPerformed(evt);
            }
        });
        jMenu2.add(subMenuItemRecalcular);

        mostrarProtocolo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        mostrarProtocolo.setFont(new java.awt.Font("Ebrima", 0, 12)); // NOI18N
        mostrarProtocolo.setSelected(true);
        mostrarProtocolo.setText("Mostrar protocolo al imprimir");
        jMenu2.add(mostrarProtocolo);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Exportar");
        jMenu3.setFont(new java.awt.Font("Ebrima", 1, 12)); // NOI18N

        jMenuItem1.setFont(new java.awt.Font("Ebrima", 0, 12)); // NOI18N
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/excel.png"))); // NOI18N
        jMenuItem1.setText("Detalles de transformador");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cjserieKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjserieKeyPressed
        if(evt.getKeyCode()==10){
            try{
                boolean mostrar = false;
                ResultSet rs = null;
                conex.conectar();
                rs = conex.CONSULTAR("SELECT numeroserie, count(*) FROM transformador t WHERE t.numeroserie='"+cjserie.getText()+"' GROUP BY numeroserie ");
                int total = 0;
                if(rs.next()){
                    total = rs.getInt("count");
                }else{
                    modelo.Metodos.M("NO SE ENCONTRÒ EL NUMERO DE SERIE DIGITADO", "advertencia.png");
                    return;
                }
                rs = conex.CONSULTAR("SELECT * FROM entrada e INNER JOIN transformador t USING(identrada) INNER JOIN cliente c USING (idcliente) WHERE t.numeroserie='"+cjserie.getText()+"'");
                if(total == 1){                    
                    mostrar = rs.next();
                }else if(total > 1){
                    DialogoTrafosRepetidos trafos = new DialogoTrafosRepetidos(this, rootPaneCheckingEnabled);
                    trafos.CargarDatos(rs);
                    trafos.setVisible(true);
                    IDTRAFO = trafos.getIDTRAFO();
                    rs = conex.CONSULTAR("SELECT * FROM entrada e INNER JOIN transformador t USING(identrada) INNER JOIN cliente c USING (idcliente) WHERE t.idtransformador='"+trafos.getIDTRAFO()+"'");
                    mostrar = rs.next();
                }
                if(mostrar){
                    IDTRAFO = rs.getInt("idtransformador");
                    cjcliente.setText(rs.getString("nombrecliente"));
                    cjlote.setText(rs.getString("lote"));
                    cjempresa.setText(rs.getString("numeroempresa"));
                    cjmarca.setText(rs.getString("marca"));
                    cjkva.setText(rs.getString("kvasalida"));
                    comboFase.setSelectedItem(rs.getString("fase"));
                    cjano.setText(rs.getString("ano"));
                    cjvp.setText(rs.getString("tps"));
                    cjvs.setText(rs.getString("tss"));
                    cjtensionBT.setText(String.valueOf(rs.getInt("tss")*2));
                    cjTensionBT2.setText(rs.getString("tss"));
                    comboServicio.setSelectedItem(rs.getString("serviciosalida"));
                    cjmasa.setText(rs.getString("peso"));
                    cjaceite.setText(rs.getString("aceite"));
                    CargarTablas();
                    habilitarCampos(rs.getString("fase").equals("3"));
                    cjcliente.setCaretPosition(0);
                    subMenuItemRecalcular.doClick();
                }
            }catch(SQLException ex){
                modelo.Metodos.M("ERROR AL BUSCAR EL NUMERO DE SERIE\n"+ex, "error.png");
                Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                conex.CERRAR();
            }
        }
    }//GEN-LAST:event_cjserieKeyPressed

    private void subMenuItemRecalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuItemRecalcularActionPerformed
        cargarValores();
        HallarTensionSerie();
        HallarConexionYPolaridad();
        HallarCorrientes();
//        CargarTablas();
        HallarPromedioCorrientes();
        HallarPromedioResistencias();
        I2R();
        I2R85();
        Z85();
        HallarReg();
        HallarEf();        
        cargarMedidas();
    }//GEN-LAST:event_subMenuItemRecalcularActionPerformed

    private void tablaUnoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaUnoKeyTyped
        if(evt.getKeyChar()==10){
            if(comboFase.getSelectedIndex()==0){
                if(tablaUno.getSelectedRow()==0&&tablaUno.getSelectedColumn()==3){
                    cjuv.grabFocus();
                }
            }else{
                if(tablaUno.getSelectedRow()==0&&tablaUno.getSelectedColumn()==0){
                    cjuv.grabFocus();
                }
            }
        }
    }//GEN-LAST:event_tablaUnoKeyTyped

    private void cjuvKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjuvKeyPressed
        if(evt.getKeyCode()==10 && !cjuv.getText().isEmpty()){
            if(comboFase.getSelectedIndex()==0){
                cjxy.grabFocus();
            }else{
                cjwu.grabFocus();
            }
        }
    }//GEN-LAST:event_cjuvKeyPressed

    private void cjxyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjxyKeyReleased
        if(evt.getKeyCode()==10 && !cjxy.getText().isEmpty()){
            if(comboFase.getSelectedIndex()==0){
                cjiu.grabFocus();
            }else{
                cjyz.grabFocus();
            }
        }
    }//GEN-LAST:event_cjxyKeyReleased

    private void comboFaseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboFaseItemStateChanged
        if(evt.getStateChange()==ItemEvent.DESELECTED){
            habilitarCampos((comboFase.getSelectedIndex()==1));
            subMenuItemRecalcular.doClick();
        }
    }//GEN-LAST:event_comboFaseItemStateChanged

    private void cjBTcontraTierraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjBTcontraTierraKeyPressed
        if(evt.getKeyCode()==10){
            tablaUno.setRowSelectionInterval(0, 0);
            tablaUno.setColumnSelectionInterval(2, 2);
            tablaUno.grabFocus();
        }
    }//GEN-LAST:event_cjBTcontraTierraKeyPressed

    private void cjiuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjiuKeyPressed
        if(evt.getKeyCode()==10){
            if(comboFase.getSelectedIndex()==0){
                cjpomedido.grabFocus();
            }else{
                cjiv.grabFocus();
            }
        }
    }//GEN-LAST:event_cjiuKeyPressed

    private void comboServicioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboServicioItemStateChanged
        if(evt.getStateChange() == ItemEvent.DESELECTED){
            if(!ACTUALIZANDO && "MANTENIMIENTO".equals(comboServicio.getSelectedItem().toString())){
                while(true){
                    int n = JOptionPane.showOptionDialog(this, "SELECCIONE EL ESTADO DEL TRANSFORMADOR", "Seleccione una opcion", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, modelo.Metodos.getIcon("advertencia.png"), new Object[]{"ORIGINAL","REPARADO"}, "ORIGINAL");
                    if(n>=0){
                        ESTADO_TRAFO = (n==0)?"ORIGINAL":"REPARADO";
                        break;
                    }
                }
            }else{
                ESTADO_TRAFO = comboServicio.getSelectedItem().toString();
            }
        }
    }//GEN-LAST:event_comboServicioItemStateChanged

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardarProtocolo();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnGenerarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarExcelActionPerformed
        modelo.Metodos.JTableToExcel(tablaProtocolos, btnGenerarExcel);
    }//GEN-LAST:event_btnGenerarExcelActionPerformed

    private void cjbuscarPorSerieKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjbuscarPorSerieKeyReleased
        buscarProtocolo();
    }//GEN-LAST:event_cjbuscarPorSerieKeyReleased

    private void tablaProtocolosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaProtocolosMouseClicked
        if(SwingUtilities.isRightMouseButton(evt)){
            tablaProtocolos.setRowSelectionInterval(tablaProtocolos.rowAtPoint( evt.getPoint() ), tablaProtocolos.rowAtPoint( evt.getPoint() ));
            tablaProtocolos.setColumnSelectionInterval(tablaProtocolos.columnAtPoint(evt.getPoint()), tablaProtocolos.columnAtPoint(evt.getPoint()));           
            menuProtocolos.show(tablaProtocolos, evt.getPoint().x, evt.getPoint().y); 
            IDPROTOCOLO = (int) tablaProtocolos.getValueAt(tablaProtocolos.getSelectedRow(), 0);
            IDBUSQUEDA = tablaProtocolos.columnAtPoint(evt.getPoint());
        }
        if(evt.getClickCount()==2){
            IDPROTOCOLO = (int) tablaProtocolos.getValueAt(tablaProtocolos.getSelectedRow(), 0);
            abrirProtocolo();
        }
    }//GEN-LAST:event_tablaProtocolosMouseClicked
        
    private void cjBuscarPorMarcaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjBuscarPorMarcaKeyReleased
        buscarProtocolo();
    }//GEN-LAST:event_cjBuscarPorMarcaKeyReleased

    private void cjBuscarPorLoteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjBuscarPorLoteKeyReleased
        buscarProtocolo();
    }//GEN-LAST:event_cjBuscarPorLoteKeyReleased

    private void subMenuAbrirProtocoloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuAbrirProtocoloActionPerformed
        abrirProtocolo();
    }//GEN-LAST:event_subMenuAbrirProtocoloActionPerformed

    private void subMenuEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuEliminarActionPerformed
        if(JOptionPane.showConfirmDialog(rootPane, "Desea eliminar el protocolo "+tablaProtocolos.getValueAt(tablaProtocolos.getSelectedRow(), 1)+"?")==JOptionPane.YES_OPTION){
            if(conex.GUARDAR("DELETE FROM protocolos WHERE idprotocolo="+IDPROTOCOLO)){
                modeloTabla.removeRow(tablaProtocolos.getSelectedRow());
                //modelo.Metodos.M("EL PROTOCOLO HA SIDO ELIMINADO", "bien.png");                
            }
        }        
    }//GEN-LAST:event_subMenuEliminarActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        limpiar();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void conmutadorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_conmutadorItemStateChanged
        if(evt.getStateChange() == ItemEvent.DESELECTED ){
            CargarTablas();
            comboDerivacion.setSelectedIndex(conmutador.getSelectedIndex());
        }
    }//GEN-LAST:event_conmutadorItemStateChanged

    private void cjobservacionesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjobservacionesKeyReleased
        if(evt.getKeyChar()==10){
            cjfechasalida.grabFocus();
        }
    }//GEN-LAST:event_cjobservacionesKeyReleased

    private void cjpcumedidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjpcumedidoKeyTyped
        if(evt.getKeyChar()==10){
            subMenuItemRecalcular.doClick();
            if(cjpcumedido.getDouble() < cji2r.getDouble()){
                modelo.Metodos.M("Las perdidas en el cobre son menores a las I²R.!", "advertencia.png");
                cjpcumedido.setBorder(new LineBorder(new Color(209,72,54), 2));
                cji2r.setBorder(new LineBorder(new Color(209,72,54), 2));
            }else{
                cjobservaciones.grabFocus();                
            }
        }
    }//GEN-LAST:event_cjpcumedidoKeyTyped

    private void cjpomedidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjpomedidoKeyTyped
        if(evt.getKeyChar()== 10){
            if(cjpomedido.getDouble() > cjpogarantizado.getDouble()){
                modelo.Metodos.M("Las Po Medidas con mayores a las garantizadas.!!", "advertencia.png");
                cjpogarantizado.setBorder(new LineBorder(new Color(209,72,54), 2));
                cjpomedido.setBorder(new LineBorder(new Color(209,72,54), 2));
            }else{
                cjvcc.grabFocus();
            }
        }
    }//GEN-LAST:event_cjpomedidoKeyTyped

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        ProgressMonitor pm = new ProgressMonitor(this, "Generando excel", "", 0, 0);
        int idcliente = comboCliente.getItemAt(comboCliente.getSelectedIndex()).getIdCliente();
        String sql = " SELECT count(*)\n" +
        "FROM entrada e \n" +
        "INNER JOIN transformador t ON e.identrada=t.identrada\n" +
        "LEFT JOIN despacho d ON d.iddespacho=t.iddespacho\n" +
        "LEFT JOIN remision r ON r.idremision=t.idremision\n" +
        "LEFT JOIN protocolos p ON p.idtransformador=t.idtransformador\n" +
        "WHERE e.idcliente="+idcliente+" ";
        conex.conectar();
        ResultSet rs1 = conex.CONSULTAR(sql);
        
        try {
            rs1.next();
            pm.setMaximum(rs1.getInt("count"));
        } catch (SQLException ex) {
            Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        conex.conectar();
        (new Thread(){
            @Override
            public void run(){
                try {
                    String sql1 = " SELECT p.codigo, t.numeroserie, t.marca, t.kvasalida, t.fase, t.ano, t.tps, t.tss, p.i1, p.i2, p.proresuno, p.proresdos, p.pomedido, p.iu, p.iv, p.iw, \n" +
                    "p.promedioi, p.iogarantizado, p.pcu, p.vcc, p.temperaturadeensayo, p.i2r, p.i2ra85, p.pcua85, p.impedancia85, p.impedanciagarantizada, p.reg, p.atcontrabt, \n" +
                    "p.atcontratierra, p.btcontratierra, p.grupodeconexion, punou, pdosu, ptresu, pcuatrou, pcincou, punov, pdosv, ptresv, pcuatrov, pcincov, punow, pdosw, ptresw, pcuatrow, pcincow,\n" +
                    "p.anchotanque, p.largotanque, altotanque, t.serviciosalida, to_char(p.fechaderegistro, 'DD Mon YYYY'), ('') as vencegarantia, p.liquidoaislante, t.aceite, p.color, t.peso, \n" +
                    "e.lote, e.op, t.numeroempresa\n" +
                    "FROM entrada e \n" +
                    "INNER JOIN transformador t ON e.identrada=t.identrada\n" +
                    "LEFT JOIN despacho d ON d.iddespacho=t.iddespacho\n" +
                    "LEFT JOIN remision r ON r.idremision=t.idremision\n" +
                    "LEFT JOIN protocolos p ON p.idtransformador=t.idtransformador\n" +
                    "WHERE e.idcliente="+idcliente+"\n" +
                    "ORDER BY e.identrada, fase, kvaentrada, marca ";
                    ResultSet rs = conex.CONSULTAR(sql1);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(new File("CARACTERISTICAS DE PROTOCOLOS.xlsx")));
                    XSSFSheet hoja = wb.getSheetAt(0);
                    XSSFRow fila;
                    int filas = 4;
                    while(rs.next()){
                        pm.setProgress(rs.getRow());
                        fila = hoja.createRow(filas);
                        for (int i = 0; i < rsmd.getColumnCount(); i++) {
                            if(rs.getObject((i+1)) instanceof Double){
                                fila.createCell(i, XSSFCell.CELL_TYPE_STRING).setCellValue(QD(rs.getDouble((i+1)), 3));
                            }else if(rs.getObject((i+1)) instanceof Integer){
                                fila.createCell(i, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(rs.getInt( (i+1) ));
                            }else{
                                fila.createCell(i, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString( (i+1) ));
                            }
                        }
                        filas++;
                    }
                    for(int j = 0; j < rsmd.getColumnCount(); j++) {
                        wb.getSheetAt(0).autoSizeColumn(j);
                    }
                    OutputStream out = new FileOutputStream(new File("PROTOCOLOS.xlsx"));
                    wb.write(out);
                    out.close();
                    Desktop.getDesktop().open(new File("PROTOCOLOS.xlsx"));
                } catch (Exception ex){
                    Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
                    modelo.Metodos.ERROR(ex, "ERROR AL GENERAR EL REPORTE");
                }
            }
        }).start();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void comboClienteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboClienteItemStateChanged
        if(evt.getStateChange() == ItemEvent.DESELECTED){
            buscarProtocolo();
        }        
    }//GEN-LAST:event_comboClienteItemStateChanged

    private void comboClaseAislamientoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboClaseAislamientoItemStateChanged
        switch(comboClaseAislamiento.getSelectedIndex()){
            case 0:jLabel61.setText("Pcc A°(W):");
                break;
            case 1:jLabel61.setText("Pcc a 75°(W):");
                break;
            case 2:jLabel61.setText("Pcc a 85°(W):");
                break;
            case 3:jLabel61.setText("Pcc a 100°(W):");
                break;
            case 4:jLabel61.setText("Pcc a 120°(W):");
                break;
            case 5:jLabel61.setText("Pcc a 145°(W):");
                break;
        }
    }//GEN-LAST:event_comboClaseAislamientoItemStateChanged

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PROTOS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PROTOS().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerarExcel;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JCheckBox checkGarantia;
    private CompuChiqui.JTextFieldPopup cjATcontraBT;
    private CompuChiqui.JTextFieldPopup cjATcontraBTyTierra;
    private CompuChiqui.JTextFieldPopup cjATcontraTierra;
    private CompuChiqui.JTextFieldPopup cjBTcontraATyTierra;
    private CompuChiqui.JTextFieldPopup cjBTcontraTierra;
    private CompuChiqui.JTextFieldPopup cjBuscarPorLote;
    private javax.swing.JTextField cjBuscarPorMarca;
    private CompuChiqui.JTextFieldPopup cjFrecuenciaInducida;
    private CompuChiqui.JTextFieldPopup cjRuptura;
    private CompuChiqui.JTextFieldPopup cjTensionBT2;
    private CompuChiqui.JTextFieldPopup cjaceite;
    private CompuChiqui.JTextFieldPopup cjaltdiseno;
    private CompuChiqui.JTextFieldPopup cjalto;
    private CompuChiqui.JTextFieldPopup cjaltoelemento;
    private CompuChiqui.JTextFieldPopup cjancho;
    private CompuChiqui.JTextFieldPopup cjano;
    private CompuChiqui.JTextFieldPopup cjbuscarPorSerie;
    private CompuChiqui.JTextFieldPopup cjcalentamientodevanado;
    private CompuChiqui.JTextFieldPopup cjcliente;
    private CompuChiqui.JTextFieldPopup cjcolor;
    private CompuChiqui.JTextFieldPopup cjef;
    private CompuChiqui.JTextFieldPopup cjelementos;
    private CompuChiqui.JTextFieldPopup cjempresa;
    private CompuChiqui.JTextFieldPopup cjespesor;
    private com.toedter.calendar.JDateChooser cjfechasalida;
    private CompuChiqui.JTextFieldPopup cji1;
    private CompuChiqui.JTextFieldPopup cji2;
    private CompuChiqui.JTextFieldPopup cji2r;
    private CompuChiqui.JTextFieldPopup cji2ra85;
    private CompuChiqui.JTextFieldPopup cjimpedancia;
    private CompuChiqui.JTextFieldPopup cjimpedancia85;
    private CompuChiqui.JTextFieldPopup cjimpedanciagarantizado;
    private CompuChiqui.JTextFieldPopup cjiogarantizado;
    private CompuChiqui.JTextFieldPopup cjiu;
    private CompuChiqui.JTextFieldPopup cjiv;
    private CompuChiqui.JTextFieldPopup cjiw;
    private CompuChiqui.JTextFieldPopup cjkva;
    private CompuChiqui.JTextFieldPopup cjlargo;
    private CompuChiqui.JTextFieldPopup cjlargoelemento;
    private CompuChiqui.JTextFieldPopup cjlote;
    private CompuChiqui.JTextFieldPopup cjmarca;
    private CompuChiqui.JTextFieldPopup cjmasa;
    private CompuChiqui.JTextFieldPopup cjmetodo;
    private CompuChiqui.JTextFieldPopup cjnba;
    private CompuChiqui.JCTextArea cjobservaciones;
    private CompuChiqui.JTextFieldPopup cjpcua85;
    private CompuChiqui.JTextFieldPopup cjpcugarantizado;
    private CompuChiqui.JTextFieldPopup cjpcumedido;
    private CompuChiqui.JTextFieldPopup cjpogarantizado;
    private CompuChiqui.JTextFieldPopup cjpomedido;
    private CompuChiqui.JTextFieldPopup cjpromedioi;
    private CompuChiqui.JTextFieldPopup cjproresalta;
    private CompuChiqui.JTextFieldPopup cjproresbaja;
    private CompuChiqui.JTextFieldPopup cjprotocolo;
    private CompuChiqui.JTextFieldPopup cjreg;
    private CompuChiqui.JTextFieldPopup cjserie;
    private CompuChiqui.JTextFieldPopup cjtemperatura;
    private CompuChiqui.JTextFieldPopup cjtensionBT;
    private CompuChiqui.JTextFieldPopup cjtensionSerie;
    private CompuChiqui.JTextFieldPopup cjtiempoInducido;
    private CompuChiqui.JTextFieldPopup cjtiempoaplicado;
    private CompuChiqui.JTextFieldPopup cjtiemporalt;
    private CompuChiqui.JTextFieldPopup cjuv;
    private CompuChiqui.JTextFieldPopup cjvcc;
    private CompuChiqui.JTextFieldPopup cjvp;
    private CompuChiqui.JTextFieldPopup cjvs;
    private CompuChiqui.JTextFieldPopup cjvw;
    private CompuChiqui.JTextFieldPopup cjwu;
    private CompuChiqui.JTextFieldPopup cjxy;
    private CompuChiqui.JTextFieldPopup cjyz;
    private CompuChiqui.JTextFieldPopup cjzx;
    private javax.swing.JComboBox<String> comboAceite;
    private javax.swing.JComboBox<String> comboClaseAislamiento;
    public javax.swing.JComboBox<Cliente> comboCliente;
    private javax.swing.JComboBox<String> comboDerivacion;
    private javax.swing.JComboBox<String> comboFase;
    private javax.swing.JComboBox<String> comboFrecuencia;
    private javax.swing.JComboBox<String> comboGrupoConexion;
    private javax.swing.JComboBox<String> comboMaterialAlta;
    private javax.swing.JComboBox<String> comboMaterialBaja;
    private javax.swing.JComboBox<String> comboPolaridad;
    private javax.swing.JComboBox<String> comboReferenciaAceite;
    private javax.swing.JComboBox<String> comboRefrigeracion;
    private javax.swing.JComboBox<String> comboServicio;
    private javax.swing.JComboBox<String> comboTensionPrueba;
    private javax.swing.JComboBox<String> conmutador;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JPopupMenu menuProtocolos;
    private javax.swing.JCheckBoxMenuItem mostrarProtocolo;
    private javax.swing.JMenuItem subMenuAbrirProtocolo;
    private javax.swing.JMenuItem subMenuEliminar;
    private javax.swing.JMenuItem subMenuItemRecalcular;
    private javax.swing.JTable tablaDos;
    private javax.swing.JTable tablaProtocolos;
    private javax.swing.JTable tablaUno;
    // End of variables declaration//GEN-END:variables

    private class Hilofases extends Thread{
        
        private int fila = -1;
        private int col = -1;
        
        public Hilofases(int row, int col) {
            this.fila = row;
            this.col = col;
        }
        
        @Override
        public void run(){
            while(true){
                System.out.println("CORRIENDO HILO");
                double fase = Double.parseDouble(tablaUno.getValueAt(getFila(), getCol()).toString());
                double minimo = Double.parseDouble(tablaDos.getValueAt(getFila(), 1).toString());
                double maximo = Double.parseDouble(tablaDos.getValueAt(getFila(), 2).toString());
                if(fase < minimo || fase > maximo){
                    try {
                        JTextField fileName = new JTextField(String.valueOf(fase));
                        Object[] message = {"INGRESE UN NUEVO VALOR.\n(MIN = "+minimo+" MAX = "+maximo+")", fileName};
                        int n =JOptionPane.showOptionDialog(rootPane, message, "VALOR FUERA DE RANGO", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,modelo.Metodos.getIcon("advertencia.png"), new Object[]{"ACEPTAR","OMITIR ERROR"}, "ACEPTAR");
                        if(n==1){
                            break;
                        }else if(n==0){
                            if(Double.parseDouble(fileName.getText()) >= minimo && Double.parseDouble(fileName.getText()) <= maximo){
                                tablaUno.setValueAt(Double.parseDouble(fileName.getText()), getFila(), getCol());
                                break;
                            }
                        }
                    } catch (HeadlessException | NumberFormatException ex){
                        modelo.Metodos.M("ERROR\n"+ex, "error.png");
                    }                                
                }else{
                    break;
                }
            }
        }       

        public int getFila() {
            return fila;
        }

        public void setFila(int fila) {
            this.fila = fila;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }
        
    }

}
