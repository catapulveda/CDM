package view;

import Dialogos.BuscarEnDespacho;
import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import modelo.ConexionBD;
import modelo.CustomTableModel;
import modelo.Metodos;

public class DespachoARemision extends javax.swing.JFrame {

    TableColumnAdjuster ajustarColumna;
    
//    CustomTableModel modeloTabla;
    DefaultTableModel modeloTabla;
    
    TableRowSorter rowSorter;
    
    private int IDDESPACHO = -1, IDREMISION = -1;
    
    private boolean ACTUALIZANDO = false;
    
    ConexionBD conexion = new ConexionBD();        
    
    String SERVICIOS[] = {"REPARACION", "FABRICACION", "RECONSTRUCCION", "MANTENIMIENTO", "DADO DE BAJA", "GARANTIA", "DEVOLUCION", "REVISION","RECONSTRUIDO"};
    String TIPOS[] = {"CONVENCIONAL", "CONV. - REPOT.", "AUTOPROTEGIDO", "SECO", "PAD MOUNTED", "POTENCIA"};   
    String DANOS[] = {"","CORTOCIRCUITO EN DEVANADO PRIMARIO",
        "CORTOCIRCUITO EN DEVANADO SECUNDARIO","DISEÑO DEFECTUOSO",
        "FALLA DE AISLAMIENTO","FALLA POR MANIPULACION",
        "HUMEDAD","PUNTO CALIENTE EN FASE",
        "SOBRECARGA","SOBRETENSION DE ORIGEN ARTMOSFERICO O MANIOBRA"};
    
    public DespachoARemision(){
        initComponents();
        
        ajustarColumna = new TableColumnAdjuster(tabla);
        
        tabla.getSelectionModel().addListSelectionListener((ListSelectionEvent e)->{             
            if (e.getValueIsAdjusting()){
                double suma = 0;
                for (int row : tabla.getSelectedRows()){
                    for (int col : tabla.getSelectedColumns()){
                        try{
                            suma += Double.parseDouble(tabla.getValueAt(row, col).toString());
                        }catch(java.lang.NumberFormatException | java.lang.NullPointerException ex){suma += 0;}
                    }
                }
                lblFilasSeleccionadas.setText("Columnas: " + tabla.getSelectedColumnCount() + " Filas: " + tabla.getSelectedRowCount()+" Total filas: "+tabla.getRowCount()+" Suma: "+suma);
                suma = 0;
            }
        });
        
    }
    
    public void cargarTabla(){
//        modeloTabla = new CustomTableModel(
//            new Object[][]{},
//            new String[]{
//                "ITEM","LOTE","REMISION","O.P","No EMPRESA","No SERIE","MARCA",
//                "FASE","KVA ENT.","KVA. SAL.","TENS. ENT.","TENS. SAL.","SERV. ENT.",
//                "SERV. SALIDA","TIPO TRAF. ENT.","TIPO TRAF. SAL.","OBSERV. ENT.","OBSERV. SAL.",
//                "AÑO","PESO","ACEITE","CIUDAD","FECHA DE RECEPCION","CAUSA DE FALLA"
//            }, 
//            tabla, 
//            new Class[]{
//                Integer.class,Object.class,Object.class,Object.class,Object.class,Object.class,Object.class,
//                Integer.class,Double.class,Double.class,Object.class,Object.class,Object.class,
//                Object.class,Object.class,Object.class,Object.class,Object.class,
//                Integer.class,Integer.class,Integer.class,Object.class,Object.class,Object.class
//            },
//            new Boolean[]{
//                false,false,false,false,true,false,false,
//                false,false,true,false,true,false,
//                true,false,true,false,true,
//                false,false,false,false,false,false
//            }
//        );
        String[] cols = {"ITEM","LOTE","REMISION","O.P","No EMPRESA","No SERIE","MARCA",
            "FASE","KVA ENT.","KVA. SAL.","TENS. ENT.","TENS. SAL.","SERV. ENT.",
            "SERV. SALIDA","TIPO TRAF. ENT.","TIPO TRAF. SAL.","OBSERV. ENT.","OBSERV. SAL.",
            "AÑO","PESO","ACEITE","CIUDAD","FECHA DE RECEPCION","CAUSA DE FALLA"};
        modeloTabla = new DefaultTableModel(new Object[][]{}, cols){
            @Override
            public boolean isCellEditable(int row, int column){
                return column==4||column==9||column==11||column==13||column==15||column==17||column==23;
            }
        };
        
        tabla.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tabla.setCellSelectionEnabled(true);
        tabla.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tabla.setSurrendersFocusOnKeystroke(true);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
        
        tabla.setModel(modeloTabla);
        
        rowSorter = new TableRowSorter(modeloTabla);
        tabla.setRowSorter(rowSorter);
        rowSorter.setRowFilter(RowFilter.regexFilter(cjBuscar.getText().toUpperCase(), 5));
        
        //COLUMNA SERVICIOS
        JComboBox combo = new JComboBox(SERVICIOS);
        combo.setMaximumRowCount(10);
        combo.setUI(JComboBoxColor.JComboBoxColor.createUI(combo));
        combo.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        tabla.getColumnModel().getColumn(13).setCellEditor(new DefaultCellEditor(combo));
//        tablaTrafos.getColumnModel().getColumn(13).setCellRenderer(new JComboBoxIntoJTable.JComboBoxEnColumnaJTable(SERVICIOS));
        
        //COLUMNA TIPO TRAFOS
        combo = new JComboBox(TIPOS);
        combo.setMaximumRowCount(10);
        combo.setUI(JComboBoxColor.JComboBoxColor.createUI(combo));
        combo.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        tabla.getColumnModel().getColumn(15).setCellEditor(new DefaultCellEditor(combo));
//        tablaTrafos.getColumnModel().getColumn(15).setCellRenderer(new JComboBoxIntoJTable.JComboBoxEnColumnaJTable(TIPOS));                
        
        combo = new JComboBox(DANOS);
        combo.setMaximumRowCount(10);
        combo.setUI(JComboBoxColor.JComboBoxColor.createUI(combo));
        combo.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        tabla.getColumnModel().getColumn(23).setCellEditor(new DefaultCellEditor(combo));

        String SQL = "SELECT e.lote, e.op, e.fecharecepcion, c.nombreciudad, r.numero_remision, t.* FROM entrada e\n";
        SQL += "INNER JOIN transformador t USING(identrada)\n";
        SQL += "LEFT JOIN remision r USING(idremision)\n";
        SQL += "INNER JOIN ciudad c USING(idciudad) WHERE\n";
        SQL += (ACTUALIZANDO)?" t.idremision="+getIDREMISION()+" ":" t.iddespacho="+getIDDESPACHO();
        SQL += " "+((comboServicio.getSelectedIndex()>0)?" AND t.serviciosalida='"+comboServicio.getSelectedItem()+"' ":"")+" ";
        SQL += "ORDER BY lote DESC, fase ASC, kvasalida ASC, marca ASC, item ASC";
        
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(SQL);
        try{
            while(rs.next()){
                modeloTabla.addRow(new Object[]{
                    rs.getInt("item"),
                    rs.getString("lote"),
                    rs.getString("numero_remision"),
                    rs.getString("op"),                    
                    rs.getString("numeroempresa"),
                    rs.getString("numeroserie"),
                    rs.getString("marca"),
                    rs.getInt("fase"),
                    rs.getDouble("kvaentrada"),
                    rs.getDouble("kvasalida"),
                    rs.getInt("tpe")+"/"+rs.getInt("tse")+"/"+rs.getInt("tte"),
                    rs.getInt("tps")+"/"+rs.getInt("tss")+"/"+rs.getInt("tts"),
                    rs.getString("servicioentrada"),
                    rs.getString("serviciosalida"),
                    rs.getString("tipotrafoentrada"),
                    rs.getString("tipotrafosalida"),
                    rs.getString("observacionentrada"),
                    rs.getString("observacionsalida"),
                    rs.getInt("ano"),
                    rs.getInt("peso"),
                    rs.getInt("aceite"),
                    rs.getString("nombreciudad"),
                    new SimpleDateFormat("EEE, d MMM yyyy").format(rs.getDate("fecharecepcion")),
                    rs.getString("causadefalla")
                });
            }
            
            tabla.setDefaultRenderer(Object.class, new modelo.ColorPrepararDespacho());
            
            ajustarColumna.adjustColumns();
            
            lblFilasSeleccionadas.setText("Columnas: " + tabla.getSelectedColumnCount() + " Filas: " + tabla.getSelectedRowCount()+" Total filas: "+tabla.getRowCount());
            
            modeloTabla.addTableModelListener(new TableModelListener(){
                @Override
                public void tableChanged(TableModelEvent e) {
                    if(e.getType() == TableModelEvent.UPDATE){
                        try {
                            String val = modeloTabla.getValueAt(e.getFirstRow(), e.getColumn()).toString();
                            String item = modeloTabla.getValueAt(e.getFirstRow(), 0).toString();
                            String serie = modeloTabla.getValueAt(e.getFirstRow(), 5).toString();

                            if(e.getColumn() == 4){
                                actualizarSalidas("numeroempresa", val, item, serie);
                            }

                            if(e.getColumn() == 9){
                                actualizarSalidas("kvasalida", val, item, serie);
                            }

                            if(e.getColumn() == 11){
                                String GUARDAR = "";
                                String t[] = modeloTabla.getValueAt(e.getFirstRow(), 11).toString().split("/");
                                if(t.length==3){
                                    if(new ConexionBD().GUARDAR("UPDATE transformador SET tps='"+t[0]+"' , tss='"+t[1]+"' , tts='"+t[2]+"' WHERE item='"+modeloTabla.getValueAt(e.getFirstRow(), 0)+"' AND iddespacho='"+getIDDESPACHO()+"' AND numeroserie='"+modeloTabla.getValueAt(e.getFirstRow(), 5)+"' ")){}
                                }else{
                                    if(new ConexionBD().GUARDAR("UPDATE transformador SET tps='0' , tss='0' , tts='0' WHERE item='"+modeloTabla.getValueAt(e.getFirstRow(), 0)+"' AND identrada='"+getIDDESPACHO()+"' AND numeroserie='"+modeloTabla.getValueAt(e.getFirstRow(), 5)+"' ")){
                                        JOptionPane.showMessageDialog(null, "EL FORMATO DE LA TENSION DEBE COMPONERSE DE 3 TENSIONES SEPARADAS POR EL SIMBOLO /, RELLENAR CON 0(cero), EN CASO DE TENER LAS TRES.", "TENSION NO VÁLIDA", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/recursos/images/advertencia.png")));
                                        modeloTabla.setValueAt("0/0/0", e.getFirstRow(), 11);
                                    }                                
                                }    
                            }

                            if(e.getColumn() == 13){
                                actualizarSalidas("serviciosalida", val, item, serie);
                            }

                            if(e.getColumn() == 15){
                                actualizarSalidas("tipotrafosalida", val, item, serie);
                            }

                            if(e.getColumn() == 17){
                                actualizarSalidas("observacionsalida", val, item, serie);
                            }

                            if(e.getColumn() == 23){
                                actualizarSalidas("causadefalla", val, item, serie);
                            }
                        } catch (NullPointerException ex) {
                        }                                                
                    }
                }
            });
            
        } catch (SQLException ex) {
            Logger.getLogger(DespachoARemision.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            conexion.CERRAR();
        }
    }
    
    public void actualizarSalidas(String col, String val, String item, String serie){
        conexion.conectar();
        if(conexion.GUARDAR(" UPDATE transformador SET "+col+"='"+val+"' WHERE iddespacho="+getIDDESPACHO()+" AND item="+item+" AND numeroserie='"+serie+"' ")){
            
        }
    }
    
    public void cargarServicios(){
        String sql = " SELECT DISTINCT(t.serviciosalida) FROM transformador t WHERE ";
        sql += (isACTUALIZANDO())?"t.idremision="+getIDREMISION():"t.iddespacho="+getIDDESPACHO();
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(sql);
        try {
            if(!isACTUALIZANDO()){
                comboServicio.addItem("TODOS");
            }            
            while(rs.next()){
                comboServicio.addItem(rs.getString("serviciosalida"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DespachoARemision.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            conexion.CERRAR();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        subMenuDevolverAPlanta = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        cjBuscar = new CompuChiqui.JTextFieldPopup();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        comboServicio = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnRefrescar3 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnDevolver = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnImprimirRemision = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btnBuscar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        jToolBar2 = new javax.swing.JToolBar();
        jProgressBar1 = new javax.swing.JProgressBar();
        lblFilasSeleccionadas = new javax.swing.JLabel();

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/izquierda.png"))); // NOI18N
        jMenuItem1.setText("Devolver a planta");
        jMenuItem1.setToolTipText("Devolver a planta");
        subMenuDevolverAPlanta.add(jMenuItem1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jToolBar1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar1.setFloatable(false);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel1.setText("Buscar:");
        jToolBar1.add(jLabel1);

        cjBuscar.setPlaceholder("Buscar No Serie:");
        cjBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjBuscarKeyReleased(evt);
            }
        });
        jToolBar1.add(cjBuscar);
        jToolBar1.add(jSeparator4);

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel2.setText("Servicio:");
        jToolBar1.add(jLabel2);

        comboServicio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboServicioItemStateChanged(evt);
            }
        });
        jToolBar1.add(comboServicio);
        jToolBar1.add(jSeparator1);

        btnRefrescar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/excel.png"))); // NOI18N
        btnRefrescar3.setToolTipText("Exportar a excel");
        btnRefrescar3.setFocusable(false);
        btnRefrescar3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefrescar3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefrescar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefrescar3ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRefrescar3);
        jToolBar1.add(jSeparator2);

        btnDevolver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/izquierda.png"))); // NOI18N
        btnDevolver.setToolTipText("Devolver a planta");
        btnDevolver.setFocusable(false);
        btnDevolver.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDevolver.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDevolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDevolverActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDevolver);
        jToolBar1.add(jSeparator3);

        btnImprimirRemision.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/imprimir.png"))); // NOI18N
        btnImprimirRemision.setToolTipText("Imprimir Remision");
        btnImprimirRemision.setFocusable(false);
        btnImprimirRemision.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimirRemision.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirRemision.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirRemisionActionPerformed(evt);
            }
        });
        jToolBar1.add(btnImprimirRemision);
        jToolBar1.add(jSeparator5);

        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/buscar.png"))); // NOI18N
        btnBuscar.setToolTipText("Imprimir Remision");
        btnBuscar.setEnabled(false);
        btnBuscar.setFocusable(false);
        btnBuscar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBuscar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnBuscar);

        tabla.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabla.setDragEnabled(true);
        tabla.setName(""); // NOI18N
        tabla.setRowHeight(25);
        jScrollPane1.setViewportView(tabla);

        jToolBar2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar2.setFloatable(false);

        jProgressBar1.setStringPainted(true);
        jToolBar2.add(jProgressBar1);

        lblFilasSeleccionadas.setFont(new java.awt.Font("Enter Sansman", 1, 12)); // NOI18N
        jToolBar2.add(lblFilasSeleccionadas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cjBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjBuscarKeyReleased
        rowSorter.setRowFilter(RowFilter.regexFilter(cjBuscar.getText().toUpperCase(), 5));
    }//GEN-LAST:event_cjBuscarKeyReleased

    private void btnRefrescar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescar3ActionPerformed
        modelo.Metodos.JTableToExcel(tabla, btnRefrescar3);
    }//GEN-LAST:event_btnRefrescar3ActionPerformed

    private void btnDevolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDevolverActionPerformed
        (new Thread(){
            @Override
            public void run(){
                try {
                    btnDevolver.setEnabled(false);
                    btnDevolver.setIcon(modelo.Metodos.getIcon("gif.gif"));
                    int filas[] = tabla.getSelectedRows();
                    for (int i = filas.length - 1; i >= 0; i--){
                        String sql = " UPDATE transformador SET iddespacho=null, idremision=null, estado='EN PLANTA' ";
                        sql += " WHERE iddespacho="+getIDDESPACHO()+" AND item="+tabla.getValueAt(filas[i], 0)+" AND ";
                        sql += " numeroserie='"+tabla.getValueAt(filas[i], 5)+"' ";
                        if(new ConexionBD().GUARDAR(sql)){
        //                    modeloTabla.removeRow(filas[i]);
                        }
                    }
                    cjBuscar.setText("");
                    cargarTabla();
                } catch(Exception e){
                    Logger.getLogger(DespachoARemision.class.getName()).log(Level.SEVERE, null, e);
                    Metodos.ERROR(e, "ERROR AL INTENTAR DEVOLVER LOS TRANSFORMADORES SELECCIONADOS A PLANTA.");
                }finally{
                    btnDevolver.setEnabled(true);
                    btnDevolver.setIcon(modelo.Metodos.getIcon("izquierda.png"));
                }
            }
        }).start();        
    }//GEN-LAST:event_btnDevolverActionPerformed

    private void comboServicioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboServicioItemStateChanged
        if(evt.getStateChange() == ItemEvent.DESELECTED){
            cargarTabla();
        }
    }//GEN-LAST:event_comboServicioItemStateChanged

    private void btnImprimirRemisionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirRemisionActionPerformed
        
        ResultSet rs = null;
        Dialogos.DialogoImprimirRemisionTrafos dirt = new Dialogos.DialogoImprimirRemisionTrafos(this, rootPaneCheckingEnabled);
        boolean MOSTRAR = false;
        
        if(isACTUALIZANDO()){
            conexion.conectar();            
            rs = conexion.CONSULTAR("SELECT * FROM remision r \n" +
                                    "INNER JOIN transformador t USING(idremision)\n" +
                                    "INNER JOIN entrada e ON t.identrada=e.identrada\n" +
                                    "INNER JOIN cliente c ON e.idcliente=c.idcliente\n" +
                                    "WHERE r.idremision="+getIDREMISION()+" LIMIT 1");
            try{
                if(rs.next()){
                    MOSTRAR = true;
                    dirt.setACTUALIZANDO(true);
                    dirt.setIDREMISION(getIDREMISION());
                    dirt.cargarEncabezado(rs);
                }
            }catch(SQLException ex){
                Logger.getLogger(DespachoARemision.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
           String sql = "";
            if(comboServicio.getSelectedIndex()==0){
                sql = "SELECT * FROM remision WHERE iddespacho="+getIDDESPACHO()+" ";
            }else if(comboServicio.getSelectedIndex()>0){
                String servicio = (comboServicio.getSelectedItem().toString().equals("MANTENIMIENTO"))?"REPARACION":comboServicio.getSelectedItem().toString();            
                sql = "SELECT * FROM remision WHERE iddespacho="+getIDDESPACHO()+" AND servicio_remision='"+servicio+"' ";
            }

            conexion.conectar();
            rs = conexion.CONSULTAR(sql);
            try{
                if(rs.next()){
                    JOptionPane.showMessageDialog(this, "YA SE ENCUENTRA GENERADA UNA REMISION PARA ÉSTE DESPACHO.\nVERIFIQUE LA REMISION QUE DESEA GENERAR O DIRIJASE A LA TABLA REMISIONES.", "FALTA INFORMACION", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/recursos/images/advertencia.png")));
                    return;
                }else{
                    rs = conexion.CONSULTAR("SELECT e.contrato, e.centrodecostos, c.nombrecliente FROM transformador t \n" +
                                            "INNER JOIN entrada e ON t.identrada=e.identrada\n" +
                                            "INNER JOIN cliente c ON e.idcliente=c.idcliente\n" +
                                            "WHERE t.iddespacho="+getIDDESPACHO()+" LIMIT 1");
                    if(rs.next()){
                        MOSTRAR = true;
                        dirt.setIDDESPACHO(getIDDESPACHO());
                        dirt.setCentrodecostos(rs.getString("centrodecostos"));
                        dirt.setContrato(rs.getString("contrato"));
                        dirt.setCliente(rs.getString("nombrecliente"));
                    }else{
                        JOptionPane.showMessageDialog(this, "NO SE ENCONTRO EL CENTRO DE COSTOS, CONTRATO NI CLIENTE PARA PODER GENERAR LOS DATOS DE ENCABEZADO DE LA REMISION.", "FALTA INFORMACION", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/recursos/images/advertencia.png")));
                    }
                }
            }catch(SQLException ex){
                Logger.getLogger(DespachoARemision.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        
        if(MOSTRAR){
//            this.setExtendedState(ICONIFIED);
            int SERVICIOS = 0;
            for (int i = 0; i < comboServicio.getItemCount() ; i++){
                
                String SERVICIO = (comboServicio.getSelectedIndex()==0)?comboServicio.getItemAt(i):comboServicio.getSelectedItem().toString();
                if(!SERVICIO.equals("TODOS")){
                    //SUMARA 1 SI ES REPARACION O MANTENIMIENTO LLEGANDO A 2, SÓLO SE GENERA CUANDO SEA 1.
                    if(SERVICIO.equals("REPARACION") || SERVICIO.equals("MANTENIMIENTO")){
                        SERVICIOS++;
                        SERVICIO = "REPARACION";
                    }else{
                        SERVICIOS = 1;
                    }
//                    SERVICIOS += (SERVICIO.equals("REPARACION") || SERVICIO.equals("MANTENIMIENTO"))?(SERVICIOS++):1;
//                    SERVICIO = (SERVICIO.equals("REPARACION") || SERVICIO.equals("MANTENIMIENTO"))?"REPARACION":SERVICIO;                                                                                
                    SERVICIO = (comboServicio.getSelectedIndex()>0)?comboServicio.getSelectedItem().toString():SERVICIO;
                    //SI SE HA SELECCIONADO UN SERVICIO LA VARIABLE TOMARA ESE VALOR DEL SERVICIO SELECCIONADO
                                                            
                    dirt.setSERVICIO(SERVICIO);
                    
                    String sql = "SELECT COUNT(*), d.nodespacho FROM transformador t INNER JOIN despacho d USING(iddespacho) WHERE t.iddespacho="+getIDDESPACHO()+" AND ";
                    sql += (SERVICIO.equals("REPARACION"))?"(serviciosalida='REPARACION' OR serviciosalida='MANTENIMIENTO')":"serviciosalida='"+SERVICIO+"' ";
                    sql += " GROUP BY d.iddespacho ";
                    conexion.conectar();
                    rs = conexion.CONSULTAR(sql);                    
                    try {if(rs.next()){dirt.cargarObservaciones(rs);}} catch (SQLException ex) {Logger.getLogger(DespachoARemision.class.getName()).log(Level.SEVERE, null, ex);}
                                                            
                    dirt.setVisible((SERVICIOS <= 1));                  
                    
                    if(comboServicio.getSelectedIndex()>0){
                        break;
                    }
                }
            }           
        }
        cargarTabla();        
    }//GEN-LAST:event_btnImprimirRemisionActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        BuscarEnDespacho buscar = new BuscarEnDespacho(this, false);
        buscar.setTabla(tabla);
        buscar.setVisible(true);
    }//GEN-LAST:event_btnBuscarActionPerformed

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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DespachoARemision.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DespachoARemision().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnBuscar;
    public javax.swing.JButton btnDevolver;
    public javax.swing.JButton btnImprimirRemision;
    public javax.swing.JButton btnRefrescar3;
    public CompuChiqui.JTextFieldPopup cjBuscar;
    public javax.swing.JComboBox<String> comboServicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lblFilasSeleccionadas;
    private javax.swing.JPopupMenu subMenuDevolverAPlanta;
    public javax.swing.JTable tabla;
    // End of variables declaration//GEN-END:variables

    public int getIDDESPACHO() {
        return IDDESPACHO;
    }

    public void setIDDESPACHO(int IDDESPACHO) {
        this.IDDESPACHO = IDDESPACHO;
    }

    public int getIDREMISION() {
        return IDREMISION;
    }

    public void setIDREMISION(int IDREMISION) {
        this.IDREMISION = IDREMISION;
    }

    public boolean isACTUALIZANDO() {
        return ACTUALIZANDO;
    }

    public void setACTUALIZANDO(boolean ACTUALIZANDO) {
        this.ACTUALIZANDO = ACTUALIZANDO;
    }
}
