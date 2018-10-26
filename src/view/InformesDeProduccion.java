package view;

import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import modelo.ConexionBD;
import modelo.CustomTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jfree.data.category.DefaultCategoryDataset;

public class InformesDeProduccion extends javax.swing.JFrame{

    CustomTableModel modeloTabla;
    TableColumnAdjuster ajustarColumna;
    ConexionBD conexion = new ConexionBD();
    
    List<RowFilter<TableModel, Object>> filtros = new ArrayList<>();
    TableRowSorter rowSorter;
    RowFilter<TableModel, Object>  compoundRowFilter;
    
    DefaultCategoryDataset dataSetUnidades = new DefaultCategoryDataset();
    DefaultCategoryDataset dataSetKva = new DefaultCategoryDataset();
    DefaultCategoryDataset dataSetServicios = new DefaultCategoryDataset();
    DefaultCategoryDataset dataSetFases = new DefaultCategoryDataset();
    DefaultCategoryDataset dataSetServiciosTotales = new DefaultCategoryDataset();
    
    public InformesDeProduccion(){
        initComponents();                
        panelCantidades.setLayout(new java.awt.BorderLayout());
        panelFases.setLayout(new java.awt.BorderLayout());
        panelKva.setLayout(new java.awt.BorderLayout());
        panelServicios.setLayout(new java.awt.BorderLayout());
        jpanelServiciosTotales.setLayout(new java.awt.BorderLayout());
        
        ajustarColumna = new TableColumnAdjuster(tablaDatos);
        cargarDatosTabla();
        comboCliente.addItem(new modelo.Cliente(-1, "SELECCIONE...", ""));
        modelo.Cliente.cargarComboNombreClientes(comboCliente);
        comboCliente.setUI(JComboBoxColor.JComboBoxColor.createUI(comboCliente));
        comboCliente.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        
        setExtendedState(MAXIMIZED_BOTH);
    }
    
    void cargarDatosTabla(){
                
//        (new Thread(){
//            @Override
//            public void run(){                
        
                modeloTabla = new CustomTableModel(
                new Object[][]{}, 
                new String[]{"ITEM","ORDEN","Nº SERIE","FECHA RECEPCION","FECHA ENTREGADO","CLIENTE","FASE","KVA","SERVICIO","LOTE"}, 
                tablaDatos, 
                new Class[]{Integer.class,String.class,String.class,String.class,String.class,String.class,Integer.class,Double.class,String.class,String.class}, 
                new Boolean[]{true,false,false,false,false,false,false,false,false,false});
                tablaDatos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                tablaDatos.setCellSelectionEnabled(true);
                tablaDatos.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
                tablaDatos.getColumnModel().getColumn(0).setCellRenderer(new JButtonIntoJTable.BotonEnColumna());
                
                if(cjfechainicio.getDate()==null||cjfechafin.getDate()==null){
                    return;
                }

                String fechainicio = new SimpleDateFormat("dd-MM-yyyy").format(cjfechainicio.getDate());
                String fechafin = new SimpleDateFormat("dd-MM-yyyy").format(cjfechafin.getDate());
                int totalintervenidos = 0;
                int index = comboCliente.getSelectedIndex();
                String sql = null;
                
                tablaDatos.setRowSorter(null);
                conexion.conectar();
                sql = "SELECT e.op, t.numeroserie, e.fecharecepcion, p.fechalaboratorio, c.nombrecliente, \n";
                sql += " t.fase, t.kvasalida, t.serviciosalida, e.lote FROM protocolos p\n";
                sql += " INNER JOIN transformador t USING(idtransformador)\n";
                sql += " INNER JOIN entrada e USING(identrada)\n ";
                sql += " INNER JOIN cliente c USING(idcliente)\n ";
                sql += " WHERE p.fechalaboratorio::date BETWEEN '"+fechainicio+"' AND '"+fechafin+"' \n";
                sql += (index>0)?"AND c.idcliente="+comboCliente.getItemAt(index).getIdCliente():"";
                sql += " ORDER BY p.fechalaboratorio, t.numeroserie ASC ";
                
                ResultSet rs = conexion.CONSULTAR(sql);
                try {
                    while(rs.next()){
                        modeloTabla.addRow(new Object[]{
                            rs.getRow(),
                            rs.getString("op"),
                            rs.getString("numeroserie"),
                            new SimpleDateFormat("dd-MMM-yy").format(rs.getDate("fecharecepcion")),
                            new SimpleDateFormat("dd-MMM-yy").format(rs.getDate("fechalaboratorio")),
                            rs.getString("nombrecliente"),
                            rs.getInt("fase"),
                            rs.getString("kvasalida"),
                            rs.getString("serviciosalida"),
                            rs.getString("lote")
                        });
                    }
                    rowSorter = new TableRowSorter(modeloTabla);
                    tablaDatos.setRowSorter(rowSorter);
                    ajustarColumna.adjustColumns();
                } catch (Exception ex) {
                    modelo.Metodos.ERROR(ex, "Error al cargar la tabla.");
                    Logger.getLogger(InformesDeProduccion.class.getName()).log(Level.SEVERE, null, ex);
                }
                /******************************/
                sql = " SELECT count(*) FROM protocolos ";
                sql += " INNER JOIN transformador t USING(idtransformador)\n" +
                       " INNER JOIN entrada e USING(identrada)\n" +
                       " INNER JOIN cliente c USING(idcliente) ";
                sql += " WHERE fechalaboratorio BETWEEN '"+fechainicio+"' AND '"+fechafin+"' ";
                sql += (index>0)?"AND c.idcliente="+comboCliente.getItemAt(index).getIdCliente():"";
                rs = conexion.CONSULTAR(sql);
                try {
                    rs.next();
                    totalintervenidos = rs.getInt("count");
                    sql = " SELECT count(*), extract(year from fechalaboratorio) AS ano, extract(month from fechalaboratorio), to_char(fechalaboratorio, 'TMMonth') AS mes \n ";
                    sql += " FROM protocolos ";
                    sql += " INNER JOIN transformador t USING(idtransformador)\n" +
                           " INNER JOIN entrada e USING(identrada)\n" +
                           " INNER JOIN cliente c USING(idcliente) ";
                    sql += " WHERE fechalaboratorio BETWEEN '"+fechainicio+"' AND '"+fechafin+"' ";
                    sql += (index>0)?"AND c.idcliente="+comboCliente.getItemAt(index).getIdCliente():"";
                    sql += " GROUP BY extract(year from fechalaboratorio), extract(month from fechalaboratorio), to_char(fechalaboratorio, 'TMMonth')\n ";
                    sql += " ORDER BY extract(month from fechalaboratorio) ASC ";
                    rs = conexion.CONSULTAR(sql);                
                    dataSetUnidades.clear();
                    while(rs.next()){
                        dataSetUnidades.addValue(rs.getInt("count"), "AÑO: "+rs.getString("ano"), rs.getString("mes"));
                    }            
                    modelo.Metodos.generarGrafica(dataSetUnidades, "TOTAL INTERVENIDOS: "+modelo.Metodos.convertirAMoneda(totalintervenidos), "MES", "CANTIDAD", panelCantidades);
                    validate();
                } catch (Exception ex) {
                    modelo.Metodos.ERROR(ex, "ERROR AL CARGAR LA GRAFICA DE CANTIDADES.");
                    Logger.getLogger(InformesDeProduccion.class.getName()).log(Level.SEVERE, null, ex);
                }
                /******************************/
                sql = " SELECT sum(t.kvasalida) FROM protocolos p ";
                sql += " INNER JOIN transformador t USING(idtransformador)\n" +
                       " INNER JOIN entrada e USING(identrada)\n" +
                       " INNER JOIN cliente c USING(idcliente) ";
                sql += " WHERE fechalaboratorio BETWEEN '"+fechainicio+"' AND '"+fechafin+"' ";
                sql += (index>0)?"AND c.idcliente="+comboCliente.getItemAt(index).getIdCliente():"";
                rs = conexion.CONSULTAR(sql);
                try {
                    rs.next();
                    totalintervenidos = rs.getInt("sum");
                    sql = " SELECT sum(t.kvasalida), extract(year from fechalaboratorio) AS ano, extract(month from fechalaboratorio), to_char(fechalaboratorio, 'TMMonth') AS mes \n ";
                    sql += " FROM protocolos p INNER JOIN transformador t USING(idtransformador)\n" +
                    "INNER JOIN entrada e USING(identrada)\n" +
                    "INNER JOIN cliente c USING(idcliente) ";
                    sql += " WHERE fechalaboratorio BETWEEN '"+fechainicio+"' AND '"+fechafin+"' ";
                    sql += (index>0)?"AND c.idcliente="+comboCliente.getItemAt(index).getIdCliente():"";
                    sql += " GROUP BY extract(year from fechalaboratorio), extract(month from fechalaboratorio), to_char(fechalaboratorio, 'TMMonth')\n ";
                    sql += " ORDER BY extract(month from fechalaboratorio) ASC ";
                    rs = conexion.CONSULTAR(sql);                
                    dataSetKva.clear();
                    while(rs.next()){
                        dataSetKva.addValue(rs.getInt("sum"), "AÑO: "+rs.getString("ano"), rs.getString("mes"));
                    }
                    modelo.Metodos.generarGrafica(dataSetKva, "TOTAL KVA PRODUCIDOS: "+modelo.Metodos.convertirAMoneda(totalintervenidos), "MES", "KVA", panelKva);
                    validate();
                } catch (Exception ex) {
                    modelo.Metodos.ERROR(ex, "ERROR AL CARGAR LA GRAFICA DE KVA TOTALES.");
                    Logger.getLogger(InformesDeProduccion.class.getName()).log(Level.SEVERE, null, ex);
                }
                /******************************/
                sql = " SELECT to_char(fechalaboratorio, 'TMMonth') AS mes, extract(year from fechalaboratorio), t.serviciosalida, count(*) \n ";
                sql += " FROM protocolos p INNER JOIN transformador t USING(idtransformador)\n" +
                    "INNER JOIN entrada e USING(identrada)\n" +
                    "INNER JOIN cliente c USING(idcliente) ";
                sql += " WHERE fechalaboratorio BETWEEN '"+fechainicio+"' AND '"+fechafin+"' ";
                sql += (index>0)?"AND c.idcliente="+comboCliente.getItemAt(index).getIdCliente():"";
                sql += " GROUP BY to_char(fechalaboratorio, 'TMMonth'), extract(year from fechalaboratorio), ";
                sql += " t.serviciosalida, extract(month from fechalaboratorio) ";
                sql += " ORDER BY extract(month from fechalaboratorio) ASC ";
                rs = conexion.CONSULTAR(sql);
                try {
                    dataSetServicios.clear();
                    while(rs.next()){
                        dataSetServicios.addValue(rs.getInt("count"), rs.getString("serviciosalida")+" - "+rs.getInt("date_part"), rs.getString("mes"));
                    }
                    modelo.Metodos.generarGrafica(dataSetServicios, "SERVICIOS REALIZADOS", "MES", "CANTIDAD", panelServicios);
                    validate();
                } catch (Exception ex) {
                    modelo.Metodos.ERROR(ex, "ERROR AL CARGAR LA GRAFICA DE SERVICIOS.");
                    Logger.getLogger(InformesDeProduccion.class.getName()).log(Level.SEVERE, null, ex);
                }
                /******************************/                
                try{                
                    sql = " SELECT to_char(fechalaboratorio, 'TMMonth') AS mes, extract(year from fechalaboratorio), t.fase, count(*)\n ";
                    sql += " FROM protocolos p INNER JOIN transformador t USING(idtransformador)\n" +
                    "INNER JOIN entrada e USING(identrada)\n" +
                    "INNER JOIN cliente c USING(idcliente) ";
                    sql += " WHERE fechalaboratorio BETWEEN '"+fechainicio+"' AND '"+fechafin+"'  ";
                    sql += (index>0)?"AND c.idcliente="+comboCliente.getItemAt(index).getIdCliente():"";
                    sql += " GROUP BY to_char(fechalaboratorio, 'TMMonth'), extract(year from fechalaboratorio), t.fase, extract(month from fechalaboratorio) \n ";
                    sql += " ORDER BY extract(month from fechalaboratorio) ASC ";
                    
                    rs = conexion.CONSULTAR(sql);
                
                    dataSetFases.clear();
                    while(rs.next()){
                        dataSetFases.addValue(rs.getInt("count"), ((rs.getString("fase").equals("1"))?"MONOFASICO":"TRIFASICO")+" - "+rs.getInt("date_part"), rs.getString("mes"));
                    }
                    
                    sql = " SELECT t.fase, count(*) FROM protocolos p ";
                    sql += " INNER JOIN transformador t USING(idtransformador)\n" +
                       " INNER JOIN entrada e USING(identrada)\n" +
                       " INNER JOIN cliente c USING(idcliente) ";
                    sql += " WHERE fechalaboratorio BETWEEN '"+fechainicio+"' AND '"+fechafin+"' ";
                    sql += (index>0)?"AND c.idcliente="+comboCliente.getItemAt(index).getIdCliente():"";
                    sql += " GROUP BY t.fase ";
                    rs = conexion.CONSULTAR(sql);
                    while(rs.next()){
                        dataSetFases.addValue(
                                rs.getInt("count"), 
                                "TOTALES", 
                                (rs.getString("fase").equals("1"))?"MONOFASICOS":"TRIFASICOS");
                    }                    
                    modelo.Metodos.generarGrafica(dataSetFases, "FASES", "MES", "FASE", panelFases);
                    validate();
                }catch (Exception ex){
                    modelo.Metodos.ERROR(ex, "ERROR AL CARGAR LA GRAFICA DE SERVICIOS.");
                    Logger.getLogger(InformesDeProduccion.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try{
                    sql = " SELECT  t.serviciosalida, count(*) FROM protocolos p ";
                    sql += " INNER JOIN transformador t USING(idtransformador)\n" +
                    " INNER JOIN entrada e USING(identrada)\n" +
                    " INNER JOIN cliente c USING(idcliente) ";
                    sql += " WHERE fechalaboratorio BETWEEN '"+fechainicio+"' AND '"+fechafin+"' ";
                    sql += (index>0)?"AND c.idcliente="+comboCliente.getItemAt(index).getIdCliente():"";
                    sql += " GROUP BY t.serviciosalida ORDER BY t.serviciosalida ASC";
                    
                    rs = conexion.CONSULTAR(sql);
                    dataSetServiciosTotales.clear();
                    while(rs.next()){
                        dataSetServiciosTotales.addValue(rs.getInt("count"), rs.getString("serviciosalida"), rs.getString("serviciosalida"));
                    }
                        modelo.Metodos.generarGrafica(dataSetServiciosTotales, "SERVICIOS", "SERVICIO", "CANTIDAD", jpanelServiciosTotales);
                    validate();
                    
                } catch (Exception e) {
                    modelo.Metodos.ERROR(e, "ERROR AL CARGAR LA GRAFICA DE SERVICIOS TOTALES.");
                    Logger.getLogger(InformesDeProduccion.class.getName()).log(Level.SEVERE, null, e);
                }
//            }
//        }).start();        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuTabla = new javax.swing.JPopupMenu();
        subMenuFiltrar = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jpanelReportes = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaDatos = new javax.swing.JTable();
        barraProgreso = new javax.swing.JProgressBar();
        jToolBar2 = new javax.swing.JToolBar();
        btnImprimir = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnExcel = new javax.swing.JButton();
        jpanelCantidades = new javax.swing.JPanel();
        panelCantidades = new javax.swing.JPanel();
        jpanelKva = new javax.swing.JPanel();
        panelKva = new javax.swing.JPanel();
        jpanelServicios = new javax.swing.JPanel();
        panelServicios = new javax.swing.JPanel();
        jpanelFases = new javax.swing.JPanel();
        panelFases = new javax.swing.JPanel();
        jpanelServiciosTotales = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        cjfechainicio = new com.toedter.calendar.JDateChooser();
        cjfechafin = new com.toedter.calendar.JDateChooser();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        comboMes = new com.toedter.calendar.JMonthChooser();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        comboCliente = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        subMenuFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/buscar.png"))); // NOI18N
        subMenuFiltrar.setText("jMenuItem1");
        subMenuFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuFiltrarActionPerformed(evt);
            }
        });
        menuTabla.add(subMenuFiltrar);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tablaDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablaDatos.setGridColor(new java.awt.Color(227, 227, 227));
        tablaDatos.setName("Informe de Produccion"); // NOI18N
        tablaDatos.setRowHeight(25);
        tablaDatos.getTableHeader().setReorderingAllowed(false);
        tablaDatos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaDatosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaDatos);

        barraProgreso.setStringPainted(true);

        jToolBar2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar2.setFloatable(false);
        jToolBar2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/imprimir.png"))); // NOI18N
        btnImprimir.setFocusable(false);
        btnImprimir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });
        jToolBar2.add(btnImprimir);
        jToolBar2.add(jSeparator3);

        btnExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/excel.png"))); // NOI18N
        btnExcel.setFocusable(false);
        btnExcel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExcel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelActionPerformed(evt);
            }
        });
        jToolBar2.add(btnExcel);

        javax.swing.GroupLayout jpanelReportesLayout = new javax.swing.GroupLayout(jpanelReportes);
        jpanelReportes.setLayout(jpanelReportesLayout);
        jpanelReportesLayout.setHorizontalGroup(
            jpanelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpanelReportesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpanelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(barraProgreso, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                    .addGroup(jpanelReportesLayout.createSequentialGroup()
                        .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpanelReportesLayout.setVerticalGroup(
            jpanelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpanelReportesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpanelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barraProgreso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("REPORTES", jpanelReportes);

        javax.swing.GroupLayout panelCantidadesLayout = new javax.swing.GroupLayout(panelCantidades);
        panelCantidades.setLayout(panelCantidadesLayout);
        panelCantidadesLayout.setHorizontalGroup(
            panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );
        panelCantidadesLayout.setVerticalGroup(
            panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 229, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jpanelCantidadesLayout = new javax.swing.GroupLayout(jpanelCantidades);
        jpanelCantidades.setLayout(jpanelCantidadesLayout);
        jpanelCantidadesLayout.setHorizontalGroup(
            jpanelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpanelCantidadesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCantidades, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jpanelCantidadesLayout.setVerticalGroup(
            jpanelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpanelCantidadesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCantidades, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("CANTIDADES", jpanelCantidades);

        javax.swing.GroupLayout panelKvaLayout = new javax.swing.GroupLayout(panelKva);
        panelKva.setLayout(panelKvaLayout);
        panelKvaLayout.setHorizontalGroup(
            panelKvaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );
        panelKvaLayout.setVerticalGroup(
            panelKvaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 229, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jpanelKvaLayout = new javax.swing.GroupLayout(jpanelKva);
        jpanelKva.setLayout(jpanelKvaLayout);
        jpanelKvaLayout.setHorizontalGroup(
            jpanelKvaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpanelKvaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKva, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jpanelKvaLayout.setVerticalGroup(
            jpanelKvaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpanelKvaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKva, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("KVA", jpanelKva);

        javax.swing.GroupLayout panelServiciosLayout = new javax.swing.GroupLayout(panelServicios);
        panelServicios.setLayout(panelServiciosLayout);
        panelServiciosLayout.setHorizontalGroup(
            panelServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );
        panelServiciosLayout.setVerticalGroup(
            panelServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 229, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jpanelServiciosLayout = new javax.swing.GroupLayout(jpanelServicios);
        jpanelServicios.setLayout(jpanelServiciosLayout);
        jpanelServiciosLayout.setHorizontalGroup(
            jpanelServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpanelServiciosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelServicios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jpanelServiciosLayout.setVerticalGroup(
            jpanelServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpanelServiciosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelServicios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("SERVICIOS", jpanelServicios);

        javax.swing.GroupLayout panelFasesLayout = new javax.swing.GroupLayout(panelFases);
        panelFases.setLayout(panelFasesLayout);
        panelFasesLayout.setHorizontalGroup(
            panelFasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );
        panelFasesLayout.setVerticalGroup(
            panelFasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 229, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jpanelFasesLayout = new javax.swing.GroupLayout(jpanelFases);
        jpanelFases.setLayout(jpanelFasesLayout);
        jpanelFasesLayout.setHorizontalGroup(
            jpanelFasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpanelFasesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelFases, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jpanelFasesLayout.setVerticalGroup(
            jpanelFasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpanelFasesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelFases, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("FASES", jpanelFases);

        javax.swing.GroupLayout jpanelServiciosTotalesLayout = new javax.swing.GroupLayout(jpanelServiciosTotales);
        jpanelServiciosTotales.setLayout(jpanelServiciosTotalesLayout);
        jpanelServiciosTotalesLayout.setHorizontalGroup(
            jpanelServiciosTotalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 375, Short.MAX_VALUE)
        );
        jpanelServiciosTotalesLayout.setVerticalGroup(
            jpanelServiciosTotalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 251, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("TOTAL SERVICIOS", jpanelServiciosTotales);

        jToolBar1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar1.setFloatable(false);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel1.setText("Periodo:");
        jToolBar1.add(jLabel1);

        cjfechainicio.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cjfechainicioPropertyChange(evt);
            }
        });
        jToolBar1.add(cjfechainicio);

        cjfechafin.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cjfechafinPropertyChange(evt);
            }
        });
        jToolBar1.add(cjfechafin);
        jToolBar1.add(jSeparator1);
        jToolBar1.add(comboMes);
        jToolBar1.add(jSeparator2);

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel2.setText("Cliente:");
        jToolBar1.add(jLabel2);

        comboCliente.setEditable(true);
        comboCliente.setMaximumRowCount(20);
        comboCliente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboClienteItemStateChanged(evt);
            }
        });
        jToolBar1.add(comboCliente);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/buscar.png"))); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        jMenu2.setText("Editar");

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/basura.png"))); // NOI18N
        jMenuItem1.setText("Remover filtros");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cjfechainicioPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cjfechainicioPropertyChange
        if(cjfechainicio.getDate()!=null&&cjfechafin.getDate()!=null){
            cargarDatosTabla();
        }
    }//GEN-LAST:event_cjfechainicioPropertyChange

    private void cjfechafinPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cjfechafinPropertyChange
        if(cjfechainicio.getDate()!=null&&cjfechafin.getDate()!=null){
            cargarDatosTabla();
        }
    }//GEN-LAST:event_cjfechafinPropertyChange

    private void tablaDatosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaDatosMouseClicked
        if(SwingUtilities.isRightMouseButton(evt)){
            int col = tablaDatos.columnAtPoint(evt.getPoint());
            int row = tablaDatos.rowAtPoint(evt.getPoint());            
            tablaDatos.setColumnSelectionInterval(col, col);
            tablaDatos.setRowSelectionInterval(row, row);
            subMenuFiltrar.setText("Filtrar por: "+tablaDatos.getValueAt(row, col));
            menuTabla.show(tablaDatos, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tablaDatosMouseClicked

    private void subMenuFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuFiltrarActionPerformed
        filtros.add(RowFilter.regexFilter(tablaDatos.getValueAt(tablaDatos.getSelectedRow(), tablaDatos.getSelectedColumn()).toString(), tablaDatos.getSelectedColumn()));
        compoundRowFilter = RowFilter.andFilter(filtros);
        rowSorter.setRowFilter(compoundRowFilter);
    }//GEN-LAST:event_subMenuFiltrarActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        filtros.clear();
        compoundRowFilter = RowFilter.andFilter(filtros);
        rowSorter.setRowFilter(compoundRowFilter);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        modelo.Metodos.JTableToExcel(tablaDatos, btnExcel);
    }//GEN-LAST:event_btnExcelActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        (new Thread(){
            @Override
            public void run(){
                try{
                    btnImprimir.setEnabled(false);
                    btnImprimir.setIcon(new ImageIcon(getClass().getResource("/recursos/images/gif.gif")));
                    JasperReport reporte = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/INFORME_ANUAL.jasper").toString()));
                    Map<String, Object> p = new HashMap<String, Object>();
                    p.put("INICIO", cjfechainicio.getDate());
                    p.put("FIN", cjfechafin.getDate());
                    JasperPrint jasperprint = JasperFillManager.fillReport(reporte, p, conexion.conectar());                        
                    JasperViewer.viewReport(jasperprint, false);                        
                    JasperViewer visor = new JasperViewer(jasperprint, false);                        
                }catch(JRException | MalformedURLException ex){
                    Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
                }finally{
                    btnImprimir.setIcon(new ImageIcon(getClass().getResource("/recursos/images/imprimir.png")));
                    btnImprimir.setEnabled(true);
                }
            }
        }).start();
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, comboMes.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        cjfechainicio.setDate(cal.getTime());

        cal.set(Calendar.MONTH, comboMes.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        cjfechafin.setDate(cal.getTime());
        
        cargarDatosTabla();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void comboClienteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboClienteItemStateChanged
        if(evt.getStateChange()==ItemEvent.DESELECTED){
            if(comboCliente.getSelectedIndex()>0){
                cargarDatosTabla();
            }
        }
    }//GEN-LAST:event_comboClienteItemStateChanged

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(InformesDeProduccion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InformesDeProduccion().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barraProgreso;
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnImprimir;
    private com.toedter.calendar.JDateChooser cjfechafin;
    private com.toedter.calendar.JDateChooser cjfechainicio;
    private javax.swing.JComboBox<modelo.Cliente> comboCliente;
    private com.toedter.calendar.JMonthChooser comboMes;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JPanel jpanelCantidades;
    private javax.swing.JPanel jpanelFases;
    private javax.swing.JPanel jpanelKva;
    private javax.swing.JPanel jpanelReportes;
    private javax.swing.JPanel jpanelServicios;
    private javax.swing.JPanel jpanelServiciosTotales;
    private javax.swing.JPopupMenu menuTabla;
    private javax.swing.JPanel panelCantidades;
    private javax.swing.JPanel panelFases;
    private javax.swing.JPanel panelKva;
    private javax.swing.JPanel panelServicios;
    private javax.swing.JMenuItem subMenuFiltrar;
    private javax.swing.JTable tablaDatos;
    // End of variables declaration//GEN-END:variables
}
