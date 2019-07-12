package view;

import CopyPasteJTable.ExcelAdapter;
import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
import modelo.Cliente;
import modelo.ConexionBD;
import modelo.Despacho;
import modelo.Metodos;

public class PrepararDespacho extends javax.swing.JFrame {

//    CustomTableModel modeloTabla;
    DefaultTableModel modeloTabla;
    
    String SERVICIOS[] = {"REPARACION", "FABRICACION", "RECONSTRUCCION", "MANTENIMIENTO", "DADO DE BAJA", "GARANTIA", "DEVOLUCION", "REVISION","RECONSTRUIDO"};
    String TIPOS[] = {"CONVENCIONAL", "CONV. - REPOT.", "AUTOPROTEGIDO", "SECO", "PAD MOUNTED", "POTENCIA", "SUMERGIBLE"};
    
    TableColumnAdjuster ajustarColumna;
    
    TableRowSorter rowSorter;
    
    private int IDENTRADA = -1, IDCLIENTE = -1, PESO = 0;
    
    ArrayList<String> tensiones = new ArrayList<>();
    
    ConexionBD conexion = new ConexionBD();
    
    modelo.Sesion sesion = modelo.Sesion.getConfigurador(null, -1);
    
    public PrepararDespacho() {
        initComponents();                
        
        ajustarColumna = new TableColumnAdjuster(tablaTrafos);
        ajustarColumna.adjustColumns();
        
        ExcelAdapter excelAdapter = new CopyPasteJTable.ExcelAdapter(tablaTrafos);
        
        comboListaDespachos.setUI(JComboBoxColor.JComboBoxColor.createUI(comboListaDespachos));
        
        comboListaDespachos.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        
        tablaTrafos.getSelectionModel().addListSelectionListener((ListSelectionEvent e)->{
            if (e.getValueIsAdjusting()){
                lblFilasSeleccionadas.setText("Columnas: " + tablaTrafos.getSelectedColumnCount() + " Filas: " + tablaTrafos.getSelectedRowCount()+" Total filas: "+tablaTrafos.getRowCount());
            }
        });
               
        modelo.Cliente.cargarComboNombreClientes(comboClientes);
        comboClientes.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        comboClientes.setUI(JComboBoxColor.JComboBoxColor.createUI(comboClientes));
    }
    
    public void cargarIntefazTabla(){
        tablaTrafos.setRowSorter(null);
        modeloTabla =  new DefaultTableModel(new Object[][]{}, modelo.PrepararDespacho.getColumnNames()){
            @Override
            public Class<?> getColumnClass(int column){
                if(column==4)
                    return Boolean.class;                
                return Object.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int col){
                if(col==4)
                    return null == modeloTabla.getValueAt(row, 1);
                return col==10||col==12||col==14||col==16||col==18;
            }
            
        };
        tablaTrafos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaTrafos.setCellSelectionEnabled(true);
        tablaTrafos.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tablaTrafos.setSurrendersFocusOnKeystroke(true);
        tablaTrafos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);        
        tablaTrafos.setModel(modeloTabla);       
        
        tablaTrafos.setDefaultRenderer(Object.class, new modelo.ColorPrepararDespacho());
        
        rowSorter = new TableRowSorter(modeloTabla);
        tablaTrafos.setRowSorter(rowSorter);
        rowSorter.setRowFilter(RowFilter.regexFilter(cjBuscar.getText().toUpperCase(), 3));
        
        //COLUMNA SERVICIOS
        JComboBox comboServicios = new JComboBox(SERVICIOS);
        comboServicios.setUI(JComboBoxColor.JComboBoxColor.createUI(comboServicios));
        comboServicios.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        tablaTrafos.getColumnModel().getColumn(14).setCellEditor(new DefaultCellEditor(comboServicios));
//        tablaTrafos.getColumnModel().getColumn(13).setCellRenderer(new JComboBoxIntoJTable.JComboBoxEnColumnaJTable(SERVICIOS));
        
        //COLUMNA TIPO TRAFOS
        comboServicios = new JComboBox(TIPOS);
        comboServicios.setUI(JComboBoxColor.JComboBoxColor.createUI(comboServicios));
        comboServicios.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        tablaTrafos.getColumnModel().getColumn(16).setCellEditor(new DefaultCellEditor(comboServicios));
//        tablaTrafos.getColumnModel().getColumn(15).setCellRenderer(new JComboBoxIntoJTable.JComboBoxEnColumnaJTable(TIPOS));
        
        String sql = " SELECT p.codigo, d.nodespacho, r.numero_remision, t.idtransformador, t.numeroempresa, t.numeroserie, t.marca, t.fase, t.kvaentrada, t.kvasalida, t.tpe, t.tse, tte, t.tps, t.tss, tts, t.servicioentrada, t.serviciosalida, t.observacionentrada, t.observacionsalida, t.tipotrafoentrada, t.tipotrafosalida, t.peso, t.aceite FROM transformador t \n" +
                    "LEFT JOIN despacho d USING(iddespacho)\n" +
                    "LEFT JOIN remision r USING(idremision)\n" +
                    "left join protocolos p on t.idtransformador=p.idtransformador\n" +
                    "WHERE t.identrada="+getIDENTRADA()+" ORDER BY fase, kvaentrada, marca, item;";
        
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(sql);
        try {
            while(rs.next()){
                modeloTabla.addRow(new Object[]{
                    rs.getInt("idtransformador"),//"ITEM°",
                    rs.getString("nodespacho"),
                    rs.getString("numero_remision"),
                    rs.getString("numeroempresa"),
                    false,
                    rs.getString("codigo"),
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
                    rs.getInt("peso"),
                    rs.getInt("aceite")
                });
            }
            
            modeloTabla.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    if(e.getType() == TableModelEvent.UPDATE){
                        
                        String val = modeloTabla.getValueAt(e.getFirstRow(), e.getColumn()).toString();
                        String item = modeloTabla.getValueAt(e.getFirstRow(), 0).toString();
                        String serie = modeloTabla.getValueAt(e.getFirstRow(), 6).toString();
                        
                        if(e.getColumn() == 4){
                            if(Boolean.parseBoolean(modeloTabla.getValueAt(e.getFirstRow(), 4).toString())){
                                PESO += (int)modeloTabla.getValueAt(e.getFirstRow(), 19);
                            }else{
                                PESO -= (int)modeloTabla.getValueAt(e.getFirstRow(), 19);
                            }
                            lblPeso.setText("Peso Total: "+PESO);
                        }
                        
                        if(e.getColumn() == 10){
                            actualizarSalidas("kvasalida", val, item, serie);
                        }
                        
                        if(e.getColumn() == 12){
                            String GUARDAR = "";
                            String t[] = modeloTabla.getValueAt(e.getFirstRow(), 12).toString().split("/");
                            if(t.length==3){
                                if(new ConexionBD().GUARDAR("UPDATE transformador SET tps='"+t[0]+"' , tss='"+t[1]+"' , tts='"+t[2]+"' WHERE item='"+modeloTabla.getValueAt(e.getFirstRow(), 0)+"' AND identrada='"+getIDENTRADA()+"' AND numeroserie='"+modeloTabla.getValueAt(e.getFirstRow(), 6)+"' ")){}
                            }else{
                                if(new ConexionBD().GUARDAR("UPDATE transformador SET tps='0' , tss='0' , tts='0' WHERE item='"+modeloTabla.getValueAt(e.getFirstRow(), 0)+"' AND identrada='"+IDENTRADA+"' AND numeroserie='"+modeloTabla.getValueAt(e.getFirstRow(), 6)+"' ")){
                                    JOptionPane.showMessageDialog(null, "EL FORMATO DE LA TENSION DEBE COMPONERSE DE 3 TENSIONES SEPARADAS POR EL SIMBOLO /, RELLENAR CON 0(cero), EN CASO DE TENER LAS TRES.", "TENSION NO VÁLIDA", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/recursos/images/advertencia.png")));
                                    modeloTabla.setValueAt("0/0/0", e.getFirstRow(), 11);
                                }                                
                            }    
                        }
                        
                        if(e.getColumn() == 14){
                            actualizarSalidas("serviciosalida", val, item, serie);
                        }
                        
                        if(e.getColumn() == 16){
                            actualizarSalidas("tipotrafosalida", val, item, serie);
                        }
                        
                        if(e.getColumn() == 18){
                            actualizarSalidas("observacionsalida", val, item, serie);
                        }
                    }
                }
            });
            
            //new TableColumnAdjuster(tablaTrafos).adjustColumns();
            ajustarColumna.adjustColumns();
            lblFilasSeleccionadas.setText("Columnas: " + tablaTrafos.getSelectedColumnCount() + " Filas: " + tablaTrafos.getSelectedRowCount()+" Total filas: "+tablaTrafos.getRowCount());
            conexion.CERRAR();
        }catch(SQLException ex){
            Logger.getLogger(PrepararDespacho.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void actualizarSalidas(String col, String val, String item, String serie){
        if(conexion.GUARDAR(" UPDATE transformador SET "+col+"='"+val+"' WHERE identrada="+getIDENTRADA()+" AND item="+item+" AND numeroserie='"+serie+"' ")){
            
        }
    }
    
//     public void cargarTablaDeTransformadores(){
//        modeloTabla = new CustomTableModel(
//            new String[][]{}, 
//            model.PrepararDespacho.getColumnNames(), 
//            this.tablaTrafos, 
//            model.PrepararDespacho.getColumnClass(),
//            model.PrepararDespacho.getColumnEditables()
//        ){
//            @Override
//            public boolean isCellEditable(int row, int col) {
//                if(col==4){
//                    if(null != tablaTrafos.getValueAt(row, 1)){
//                        return false;
//                    }else{
//                        return true;
//                    }                           
//                }
//                return super.isCellEditable(row, col);
//            }            
//        };                
//        
//        tablaTrafos.setDefaultRenderer(String.class, new model.ColorPrepararDespacho());
//            
//        rowSorter = new TableRowSorter(modeloTabla);
//        tablaTrafos.setRowSorter(rowSorter);
//        rowSorter.setRowFilter(RowFilter.regexFilter(cjBuscar.getText().toUpperCase(), 3));
//
//        //COLUMNA SERVICIOS
//        JComboBox combo_servicios = new JComboBox(SERVICIOS);
//        tablaTrafos.getColumnModel().getColumn(13).setCellEditor(new DefaultCellEditor(combo_servicios));
////        tablaTrafos.getColumnModel().getColumn(13).setCellRenderer(new JComboBoxIntoJTable.JComboBoxEnColumnaJTable(SERVICIOS));
//        
//        //COLUMNA SERVICIOS
//        JComboBox combo_tipos = new JComboBox(TIPOS);
//        tablaTrafos.getColumnModel().getColumn(15).setCellEditor(new DefaultCellEditor(combo_tipos));        
////        tablaTrafos.getColumnModel().getColumn(15).setCellRenderer(new JComboBoxIntoJTable.JComboBoxEnColumnaJTable(TIPOS));
//        
//        modeloTabla.addTableModelListener(new TableModelListener() {
//            @Override
//            public void tableChanged(TableModelEvent e) {
//                if(e.getType() == TableModelEvent.UPDATE){
//                    if(e.getColumn() == 4){
//                        if(Boolean.parseBoolean(modeloTabla.getValueAt(e.getFirstRow(), 4).toString())){
//                            PESO += (int)modeloTabla.getValueAt(e.getFirstRow(), 18);
//                        }else{
//                            PESO -= (int)modeloTabla.getValueAt(e.getFirstRow(), 18);
//                        }
//                        lblPeso.setText("Peso Total: "+PESO);
//                    }
//                }
//            }
//        });
//        
//        String sql = " SELECT d.nodespacho, r.numero_remision, t.item, t.numeroempresa, t.numeroserie, t.marca, t.fase, t.kvaentrada, t.kvasalida, t.tpe, t.tse, tte, t.tps, t.tss, tts, t.servicioentrada, t.serviciosalida, t.observacionentrada, t.observacionsalida, t.tipotrafoentrada, t.tipotrafosalida, t.peso, t.aceite FROM transformador t \n" +
//                    "LEFT JOIN despacho d USING(iddespacho)\n" +
//                    "LEFT JOIN remision r USING(idremision)\n" +
//                    "WHERE t.identrada="+getIDENTRADA()+" ORDER BY fase, kvaentrada, marca, item";
//        
//        conexion.conectar();
//        ResultSet rs = conexion.CONSULTAR(sql);
//        try {
//            while(rs.next()){
//                modeloTabla.addRow(new Object[]{
//                    rs.getInt("item"),//"ITEM°",
//                    rs.getString("nodespacho"),
//                    rs.getString("numero_remision"),
//                    rs.getString("numeroempresa"),
//                    false,
//                    rs.getString("numeroserie"),
//                    rs.getString("marca"),
//                    rs.getInt("fase"),
//                    rs.getDouble("kvaentrada"),
//                    rs.getDouble("kvasalida"),
//                    rs.getInt("tpe")+"/"+rs.getInt("tse")+"/"+rs.getInt("tte"),
//                    rs.getInt("tps")+"/"+rs.getInt("tss")+"/"+rs.getInt("tts"),
//                    rs.getString("servicioentrada"),
//                    rs.getString("serviciosalida"),
//                    rs.getString("tipotrafoentrada"),
//                    rs.getString("tipotrafosalida"),
//                    rs.getString("observacionentrada"),
//                    rs.getString("observacionsalida"),                    
//                    rs.getInt("peso"),
//                    rs.getInt("aceite")
//                });
//            }
//            new TableColumnAdjuster(tablaTrafos).adjustColumns();
//            lblFilasSeleccionadas.setText("Columnas: " + tablaTrafos.getSelectedColumnCount() + " Filas: " + tablaTrafos.getSelectedRowCount()+" Total filas: "+tablaTrafos.getRowCount());
//            conexion.CERRAR();
//        } catch (SQLException ex) {
//            Logger.getLogger(PrepararDespacho.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//    }
     
    public void cargarComboDespachos(){
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(" SELECT * FROM despacho WHERE idcliente="+IDCLIENTE+" "
                + "ORDER BY iddespacho DESC LIMIT 10 ");
        try {
            comboListaDespachos.addItem(new Despacho(0, "NUEVO"));
            while(rs.next()){
                comboListaDespachos.addItem(new Despacho(rs.getInt("iddespacho"), rs.getString("nodespacho")));
            }
        } catch (SQLException ex) {
            Metodos.ERROR(ex, "OCURRIO UN ERROR AL CARGAR LA LISTA DESPLEGABLE CON LOS DESPACHOS RECIENTES.");
            Logger.getLogger(PrepararDespacho.class.getName()).log(Level.SEVERE, null, ex);
        }
    }       

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tablaTrafos = new javax.swing.JTable();
        barra = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        cjBuscar = new CompuChiqui.JTextFieldPopup();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jCheckBox1 = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel3 = new javax.swing.JLabel();
        comboClientes = new javax.swing.JComboBox<>();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        comboListaDespachos = new javax.swing.JComboBox<>();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btnGuardarDespacho = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnRefrescar3 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        lblPeso = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        lblFilasSeleccionadas = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tablaTrafos.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        tablaTrafos.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaTrafos.setRowHeight(25);
        jScrollPane1.setViewportView(tablaTrafos);

        barra.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        barra.setFloatable(false);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel1.setText("Buscar:");
        barra.add(jLabel1);

        cjBuscar.setPlaceholder("Buscar No Serie:");
        cjBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjBuscarKeyReleased(evt);
            }
        });
        barra.add(cjBuscar);
        barra.add(jSeparator3);

        jCheckBox1.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jCheckBox1.setText("Todos");
        jCheckBox1.setFocusable(false);
        jCheckBox1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jCheckBox1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });
        barra.add(jCheckBox1);
        barra.add(jSeparator2);

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel3.setText("Cliente:");
        barra.add(jLabel3);

        comboClientes.setMaximumRowCount(12);
        barra.add(comboClientes);
        barra.add(jSeparator6);

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel2.setText("Seleccione despacho:");
        barra.add(jLabel2);

        comboListaDespachos.setMaximumRowCount(12);
        barra.add(comboListaDespachos);
        barra.add(jSeparator5);

        btnGuardarDespacho.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnGuardarDespacho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Guardar.png"))); // NOI18N
        btnGuardarDespacho.setText("Guardar");
        btnGuardarDespacho.setToolTipText("Guardar registros");
        btnGuardarDespacho.setFocusable(false);
        btnGuardarDespacho.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnGuardarDespacho.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardarDespacho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarDespachoActionPerformed(evt);
            }
        });
        barra.add(btnGuardarDespacho);
        barra.add(jSeparator4);

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
        barra.add(btnRefrescar3);
        barra.add(jSeparator1);

        lblPeso.setFont(new java.awt.Font("Enter Sansman", 1, 12)); // NOI18N
        lblPeso.setText("Peso total:");
        barra.add(lblPeso);

        jToolBar1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar1.setFloatable(false);

        lblFilasSeleccionadas.setFont(new java.awt.Font("Enter Sansman", 1, 12)); // NOI18N
        jToolBar1.add(lblFilasSeleccionadas);

        jProgressBar1.setStringPainted(true);
        jToolBar1.add(jProgressBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(barra, javax.swing.GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cjBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjBuscarKeyReleased
        rowSorter.setRowFilter(RowFilter.regexFilter(cjBuscar.getText().toUpperCase()));
    }//GEN-LAST:event_cjBuscarKeyReleased

    private void btnGuardarDespachoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarDespachoActionPerformed
        (new Thread(){
            @Override
            public void run(){
                try{            
                    int IDDESPACHO = -1;
                    String NUMERO_DESPACHO = "";
                    conexion = new ConexionBD();
                    conexion.conectar();
                    if(comboListaDespachos.getSelectedIndex()==0){
                        if((null!= (NUMERO_DESPACHO=JOptionPane.showInputDialog(null, "Ingrese el numero del despacho:", "Ingrese un despacho:", JOptionPane.INFORMATION_MESSAGE)) )){
                            if(null != NUMERO_DESPACHO && !NUMERO_DESPACHO.isEmpty()){
                                conexion.getConexion().setAutoCommit(false);
                                String sql = " INSERT INTO despacho (nodespacho, fecha_despacho, idcliente, peso_despacho, ";
                                sql += " estado_despacho, descripcion_despacho, idusuario) ";
                                sql += " VALUES ( '"+NUMERO_DESPACHO+"', '"+new java.util.Date()+"' , "+((Cliente)comboClientes.getModel().getSelectedItem()).getIdCliente()+", "+PESO+", ";
                                sql += " 'false' , '', "+sesion.getIdUsuario()+" ) ";                                                                
                                PreparedStatement pst = conexion.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                                if(pst.executeUpdate()>0){
                                    ResultSet rs = pst.getGeneratedKeys();
                                    rs.next();
                                    IDDESPACHO = rs.getInt(1);
                                }
//                                if(conexion.GUARDAR(sql)){
//                                    conexion.conectar();
//                                    ResultSet rs = conexion.CONSULTAR("SELECT last_value FROM despacho_iddespacho_seq");
//                                    rs.next();
//                                    IDDESPACHO = rs.getInt("last_value");
//                                    conexion.CERRAR();
//                                }
                            }
                        }
                    }else if(comboListaDespachos.getSelectedIndex()>0){
                        IDDESPACHO = comboListaDespachos.getItemAt(comboListaDespachos.getSelectedIndex()).getIddespacho();
                    }

                    if(IDDESPACHO>0){
                        if(JOptionPane.showConfirmDialog(null, "Desea continuar ? ", "Confirmar", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                            String update = "";
                            for (int i = 0; i < tablaTrafos.getRowCount(); i++) {                                
                                if(Boolean.parseBoolean(tablaTrafos.getValueAt(i, 4).toString())){
                                    tablaTrafos.setRowSelectionInterval(i, i);

                                    update += "UPDATE transformador SET iddespacho="+IDDESPACHO+" , estado='A DESPACHAR'";
                                    update += "WHERE identrada="+getIDENTRADA()+" AND idtransformador="+tablaTrafos.getValueAt(i, 0)+";\n";
//                                    String sql = " UPDATE transformador SET iddespacho="+IDDESPACHO+" , estado='A DESPACHAR' ";
//                                    sql += " WHERE identrada="+getIDENTRADA()+" AND item="+tablaTrafos.getValueAt(i, 0)+" ";
//                                    if(conexion.GUARDAR(sql)){
//                                        
//                                    }
                                }
//                                if(i==tablaTrafos.getRowCount()-1){
//                                    cargarIntefazTabla();
//                                }
                            }
                            if(update.isEmpty()){
                               Metodos.M("NO SE HA SELECCIONADO NINGUN TRANSFORMADOR", "advertencia.png");
                               return;
                            }
                            PreparedStatement pst = conexion.getConexion().prepareCall(update);
                            if(pst.executeUpdate()>0){
                                if(!conexion.getConexion().getAutoCommit()){
                                    conexion.getConexion().commit();                                    
                                }
                                cargarIntefazTabla();
                            }
                        }
                    }            

                }catch(HeadlessException | SQLException e){
                    Metodos.ERROR(e, "ERROR AL INTENTAR GUARDAR LOS TRANSFORMADORES AL DESPACHO.");
                    Logger.getLogger(PrepararDespacho.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }).start();        
    }//GEN-LAST:event_btnGuardarDespachoActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        for (int i = 0; i < tablaTrafos.getRowCount(); i++) {
            if(null==tablaTrafos.getValueAt(i, 1)){
                tablaTrafos.setValueAt((jCheckBox1.isSelected())?true:false, i, 4);
            }                
        }        
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void btnRefrescar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescar3ActionPerformed
        modelo.Metodos.JTableToExcel(tablaTrafos, btnRefrescar3);
    }//GEN-LAST:event_btnRefrescar3ActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PrepararDespacho.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PrepararDespacho.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PrepararDespacho.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PrepararDespacho.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrepararDespacho().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar barra;
    public javax.swing.JButton btnGuardarDespacho;
    public javax.swing.JButton btnRefrescar3;
    public CompuChiqui.JTextFieldPopup cjBuscar;
    public javax.swing.JComboBox<modelo.Cliente> comboClientes;
    public javax.swing.JComboBox<modelo.Despacho> comboListaDespachos;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblFilasSeleccionadas;
    private javax.swing.JLabel lblPeso;
    public javax.swing.JTable tablaTrafos;
    // End of variables declaration//GEN-END:variables

    public int getIDENTRADA() {
        return IDENTRADA;
    }

    public void setIDENTRADA(int IDENTRADA) {
        this.IDENTRADA = IDENTRADA;
    }

    public int getIDCLIENTE() {
        return IDCLIENTE;
    }

    public void setIDCLIENTE(int IDCLIENTE) {
        this.IDCLIENTE = IDCLIENTE;        
    }
}
