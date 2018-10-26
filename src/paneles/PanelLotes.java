package paneles;

import CopyPasteJTable.ExcelAdapter;
import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableRowSorter;
import modelo.Cliente;
import modelo.ConexionBD;
import modelo.CustomTableModel;
import modelo.Metodos;
import view.EntradaDeTrafos;
import view.PrepararDespacho;

public class PanelLotes extends javax.swing.JPanel {

    CustomTableModel modeloTabla;
    TableRowSorter rowSorter;    
    
    TableColumnAdjuster ajustarColumna;
    
    ConexionBD conexion = new ConexionBD();
    
    public PanelLotes() {
        initComponents();
        
        //CREA UNA INSTANCIA DE LA CLASE QUE AJUSTA EL ANCHO DE LAS COLUMNAS.
        ajustarColumna = new TableColumnAdjuster(tablaLotes);
        ExcelAdapter excelAdapter = new CopyPasteJTable.ExcelAdapter(tablaLotes);
        
        //CARGAR EL COMBO DE LOS NOMBRES DE LOS CLIENTES
        cargarComboClientes();
        
        //CARGAR EL COMBO DE LOS CONTRATOS SEGUN EL CLIENTE SELECCIONADO
        cargarComboContratos();
        
        //CREA EL MODODELO CON LAS COLUMNAS Y LO ASIGNA A LA TABLA
//        cargarListaLotes();                                     
        
        //AGREGA LA PERSONALIZACION DEL COMBOBOX
        comboBuscarLotePorCliente.setUI(JComboBoxColor.JComboBoxColor.createUI(comboBuscarLotePorCliente));
        comboBuscarLotePorTipoDeContrato.setUI(JComboBoxColor.JComboBoxColor.createUI(comboBuscarLotePorTipoDeContrato));
        comboBuscarLotePorContrato.setUI(JComboBoxColor.JComboBoxColor.createUI(comboBuscarLotePorContrato));
        comboBuscarLotePorLote.setUI(JComboBoxColor.JComboBoxColor.createUI(comboBuscarLotePorLote));
        
        //MUESTRA EL CONTENIDO DEL COMBOBOX AL MAXIMO ANCHO DEL ITEM MAS LARGO
        comboBuscarLotePorTipoDeContrato.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        comboBuscarLotePorCliente.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        comboBuscarLotePorContrato.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        comboBuscarLotePorLote.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
         
        tablaLotes.getSelectionModel().addListSelectionListener((ListSelectionEvent e)->{
            if (e.getValueIsAdjusting()){
                lblFilasSeleccionadas.setText("Columnas: " + tablaLotes.getSelectedColumnCount() + " Filas: " + tablaLotes.getSelectedRowCount()+" Total filas: "+tablaLotes.getRowCount());
            }
        });                
    }
    
    public void cargarListaLotes(){
        try{
            tablaLotes.setRowSorter(null);
            modeloTabla = new CustomTableModel(
                new String[][]{},                 
                modelo.Lote.getColumnNames(), 
                tablaLotes, 
                modelo.Lote.getColumnClass(),
                modelo.Lote.getColumnEditables()
            );
            
            modelo.Lote.cargarLotes(
                    modeloTabla, 
                    comboBuscarLotePorTipoDeContrato.getSelectedIndex(),
                    comboBuscarLotePorCliente.getItemAt(comboBuscarLotePorCliente.getSelectedIndex()).getIdCliente(),
                    comboBuscarLotePorContrato,
                    comboBuscarLotePorLote
            );
            
            tablaLotes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tablaLotes.setCellSelectionEnabled(true);
            tablaLotes.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
            
            ajustarColumna.adjustColumns();
            
//            frameLotes.tablaLotes.setDefaultRenderer(Object.class, new ColorRowJTable.ColorRowInJTable());
            tablaLotes.getColumnModel().getColumn(0).setCellRenderer(new JButtonIntoJTable.BotonEnColumna());
            
            rowSorter = new TableRowSorter(modeloTabla);
            tablaLotes.setRowSorter(rowSorter); 
            
            //setTitle("LOTES REGISTRADOS: "+model.Lote.getTotalLotes());
            lblFilasSeleccionadas.setText("Columnas: " + tablaLotes.getSelectedColumnCount() + " Filas: " + tablaLotes.getSelectedRowCount()+" Total filas: "+tablaLotes.getRowCount());
            
        }catch(Exception ex){
            Metodos.ERROR(ex, "OCURRIO UN ERROR AL CARGAR LA TABLA CON LA LISTA DE LOS LOTES");
            Logger.getLogger(PanelLotes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void cargarComboClientes(){
        String sql = " SELECT DISTINCT(c.nombrecliente), c.idcliente, c.nitcliente FROM cliente c, entrada e WHERE e.idcliente=c.idcliente ";
        sql += (comboBuscarLotePorTipoDeContrato.getSelectedIndex()==1)?" AND e.contrato!='PARTICULAR' ":"";
        sql += (comboBuscarLotePorTipoDeContrato.getSelectedIndex()==2)?" AND e.contrato='PARTICULAR' ":"";
        sql += " ORDER BY c.nombrecliente ASC ";
        conexion.conectar();
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(sql);
        comboBuscarLotePorCliente.removeAllItems();
        comboBuscarLotePorCliente.addItem(new Cliente(-1, "TODOS", "NIT CLIENTE"));
        try {
            while(rs.next()){
                comboBuscarLotePorCliente.addItem(
                        new Cliente(
                                rs.getInt("idcliente"),
                                rs.getString("nombrecliente"),
                                rs.getString("nitcliente"))
                );
            }
            conexion.CERRAR();
        } catch (SQLException ex) {
            Logger.getLogger(PanelLotes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void cargarComboContratos(){
        final int index = comboBuscarLotePorCliente.getSelectedIndex();
        conexion.conectar();
        ResultSet rscontrato = conexion.CONSULTAR("SELECT DISTINCT (contrato) FROM entrada e, cliente c WHERE e.idcliente=c.idcliente AND c.idcliente='"+comboBuscarLotePorCliente.getItemAt(index).getIdCliente()+"' ");
        try {
            comboBuscarLotePorContrato.removeAllItems();
            comboBuscarLotePorContrato.addItem("TODOS");
            while(rscontrato.next()){
                comboBuscarLotePorContrato.addItem(rscontrato.getString("contrato"));
            }
            conexion.CERRAR();
        } catch (SQLException ex) {
            Logger.getLogger(PanelLotes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void cargarComboLotes(){
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(" SELECT lote FROM entrada WHERE contrato='"+comboBuscarLotePorContrato.getSelectedItem()+"' ");
        try {
            comboBuscarLotePorLote.removeAllItems();
            comboBuscarLotePorLote.addItem("TODOS");
            while(rs.next()){
                comboBuscarLotePorLote.addItem(rs.getString("lote"));
            }
            conexion.CERRAR();
        } catch (SQLException ex) {
            Logger.getLogger(PanelLotes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        subMenuTablaLotes = new javax.swing.JPopupMenu();
        subMenuAbrirLote = new javax.swing.JMenuItem();
        subMenuDarPorTerminado = new javax.swing.JMenuItem();
        subMenuPrepararDespacho = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        comboBuscarLotePorTipoDeContrato = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        comboBuscarLotePorCliente = new javax.swing.JComboBox<>();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel3 = new javax.swing.JLabel();
        comboBuscarLotePorContrato = new javax.swing.JComboBox<>();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jLabel4 = new javax.swing.JLabel();
        comboBuscarLotePorLote = new javax.swing.JComboBox<>();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnCargarLotes = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btnGenerarExcel = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaLotes = new javax.swing.JTable();
        jToolBar2 = new javax.swing.JToolBar();
        barraProgreso = new javax.swing.JProgressBar();
        lblFilasSeleccionadas = new javax.swing.JLabel();

        subMenuAbrirLote.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        subMenuAbrirLote.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/ver.png"))); // NOI18N
        subMenuAbrirLote.setText("Abrir");
        subMenuAbrirLote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuAbrirLoteActionPerformed(evt);
            }
        });
        subMenuTablaLotes.add(subMenuAbrirLote);

        subMenuDarPorTerminado.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        subMenuDarPorTerminado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/finalizar.png"))); // NOI18N
        subMenuDarPorTerminado.setText("Dar por terminado");
        subMenuDarPorTerminado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuDarPorTerminadoActionPerformed(evt);
            }
        });
        subMenuTablaLotes.add(subMenuDarPorTerminado);

        subMenuPrepararDespacho.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        subMenuPrepararDespacho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/preparardespacho.png"))); // NOI18N
        subMenuPrepararDespacho.setText("Preparar despacho");
        subMenuPrepararDespacho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuPrepararDespachoActionPerformed(evt);
            }
        });
        subMenuTablaLotes.add(subMenuPrepararDespacho);

        jToolBar1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel1.setText("Tipo de cliente:");
        jToolBar1.add(jLabel1);

        comboBuscarLotePorTipoDeContrato.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        comboBuscarLotePorTipoDeContrato.setForeground(new java.awt.Color(255, 255, 255));
        comboBuscarLotePorTipoDeContrato.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS", "CON CONTRATO", "PARTICULARES" }));
        comboBuscarLotePorTipoDeContrato.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBuscarLotePorTipoDeContratoItemStateChanged(evt);
            }
        });
        jToolBar1.add(comboBuscarLotePorTipoDeContrato);
        jToolBar1.add(jSeparator1);

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel2.setText("Cliente:");
        jToolBar1.add(jLabel2);

        comboBuscarLotePorCliente.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        comboBuscarLotePorCliente.setMaximumRowCount(25);
        comboBuscarLotePorCliente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBuscarLotePorClienteItemStateChanged(evt);
            }
        });
        jToolBar1.add(comboBuscarLotePorCliente);
        jToolBar1.add(jSeparator2);

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel3.setText("Contrato:");
        jToolBar1.add(jLabel3);

        comboBuscarLotePorContrato.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        comboBuscarLotePorContrato.setForeground(new java.awt.Color(255, 255, 255));
        comboBuscarLotePorContrato.setMaximumRowCount(15);
        comboBuscarLotePorContrato.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBuscarLotePorContratoItemStateChanged(evt);
            }
        });
        jToolBar1.add(comboBuscarLotePorContrato);
        jToolBar1.add(jSeparator3);

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel4.setText("Lote:");
        jToolBar1.add(jLabel4);

        comboBuscarLotePorLote.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        comboBuscarLotePorLote.setForeground(new java.awt.Color(255, 255, 255));
        comboBuscarLotePorLote.setMaximumRowCount(15);
        comboBuscarLotePorLote.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBuscarLotePorLoteItemStateChanged(evt);
            }
        });
        jToolBar1.add(comboBuscarLotePorLote);
        jToolBar1.add(jSeparator4);

        btnCargarLotes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/actualizar.png"))); // NOI18N
        btnCargarLotes.setToolTipText("Actualizar lista de lotes");
        btnCargarLotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarLotesActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCargarLotes);
        jToolBar1.add(jSeparator5);

        btnGenerarExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/excel.png"))); // NOI18N
        btnGenerarExcel.setToolTipText("Exportar a excel");
        btnGenerarExcel.setFocusable(false);
        btnGenerarExcel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGenerarExcel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGenerarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarExcelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnGenerarExcel);

        tablaLotes.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        tablaLotes.setForeground(new java.awt.Color(70, 70, 70));
        tablaLotes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "LOTES"
            }
        ));
        tablaLotes.setGridColor(new java.awt.Color(227, 227, 227));
        tablaLotes.setOpaque(false);
        tablaLotes.setRowHeight(25);
        tablaLotes.setSelectionBackground(new java.awt.Color(51, 122, 183));
        tablaLotes.getTableHeader().setReorderingAllowed(false);
        tablaLotes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaLotesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaLotes);
        tablaLotes.getAccessibleContext().setAccessibleName("Lotes");

        jToolBar2.setFloatable(false);

        barraProgreso.setStringPainted(true);
        jToolBar2.add(barraProgreso);

        lblFilasSeleccionadas.setFont(new java.awt.Font("Enter Sansman", 1, 12)); // NOI18N
        lblFilasSeleccionadas.setToolTipText("");
        jToolBar2.add(lblFilasSeleccionadas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void subMenuAbrirLoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuAbrirLoteActionPerformed
        EntradaDeTrafos et = new EntradaDeTrafos();
        et.setTitle("LOTE "+tablaLotes.getValueAt(tablaLotes.getSelectedRow(), 2)+" "+tablaLotes.getValueAt(tablaLotes.getSelectedRow(), 1));
        et.setIDENTRADA(Integer.parseInt(tablaLotes.getValueAt(tablaLotes.getSelectedRow(), 0).toString()));
        et.cargarEncabezadoEntrada();
        et.cargarTablaDeTransformadores(et.checkOrdenar.isSelected());            
        et.setVisible(true);
    }//GEN-LAST:event_subMenuAbrirLoteActionPerformed

    private void subMenuDarPorTerminadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuDarPorTerminadoActionPerformed
        try {
            if(Inet4Address.getLocalHost().getHostName().equals("ALMACEN") || Inet4Address.getLocalHost().getHostName().equals("PROGRAMADOR")){
                String idEntrada = tablaLotes.getValueAt(tablaLotes.getSelectedRow(), 0).toString();
                boolean estado = Boolean.parseBoolean(tablaLotes.getValueAt(tablaLotes.getSelectedRow(), 13).toString());
                if(estado){
                    JOptionPane.showMessageDialog(this, "EL LOTE YA SE ENCUENTRA VERIFICADO.", "ITEM SIN NUMERO DE SERIE", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/recursos/images/advertencia.png")));
                }else{
                    if(JOptionPane.showConfirmDialog(this, "¿Confirma que desea dar por terminado el lote?")==JOptionPane.YES_OPTION){
                        if(new ConexionBD().GUARDAR("UPDATE entrada SET estado='TRUE' , fechaliberado='"+new java.util.Date()+"' WHERE identrada='"+idEntrada+"' ")){
                            btnCargarLotes.doClick();
                        }
                    }
                }
            }else{
                modelo.Metodos.M("SOLO EL PERSONAL DE ALMACEN PUEDE DAR POR TERMINADO EL LOTE.","advertencia.png");
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(PanelLotes.class.getName()).log(Level.SEVERE, null, ex);
            modelo.Metodos.ERROR(ex, "ERROR AL VERIFICAR EL NOMBRE DEL EQUIPO.");
        }
    }//GEN-LAST:event_subMenuDarPorTerminadoActionPerformed

    private void subMenuPrepararDespachoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuPrepararDespachoActionPerformed
        int IDENTRADA = (int) tablaLotes.getValueAt(tablaLotes.getSelectedRow(), 0);
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(" SELECT * FROM entrada INNER JOIN cliente c USING(idcliente) WHERE identrada="+IDENTRADA+"");
        try {
            if(rs.next()){
                PrepararDespacho pd = new PrepararDespacho();
                pd.setTitle("LOTE: "+rs.getString("lote")+" de "+rs.getString("nombrecliente"));
                pd.setIDENTRADA(rs.getInt("identrada"));
                pd.setIDCLIENTE(rs.getInt("idcliente"));
                pd.cargarIntefazTabla();
                pd.cargarComboDespachos();
                pd.comboClientes.getModel().setSelectedItem(new Cliente(rs.getInt("idcliente"), rs.getString("nombrecliente"), rs.getString("nitcliente")));
                pd.setVisible(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PanelLotes.class.getName()).log(Level.SEVERE, null, ex);
        }finally{conexion.CERRAR();}
    }//GEN-LAST:event_subMenuPrepararDespachoActionPerformed

    private void btnCargarLotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarLotesActionPerformed
        cargarListaLotes();
    }//GEN-LAST:event_btnCargarLotesActionPerformed

    private void comboBuscarLotePorTipoDeContratoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboBuscarLotePorTipoDeContratoItemStateChanged
        if(evt.getStateChange() == ItemEvent.DESELECTED){
//            cargarComboClientes();
            cargarListaLotes();
        }
    }//GEN-LAST:event_comboBuscarLotePorTipoDeContratoItemStateChanged

    private void comboBuscarLotePorClienteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboBuscarLotePorClienteItemStateChanged
        if(evt.getStateChange() == ItemEvent.DESELECTED){
            cargarComboContratos();
            cargarListaLotes();
        }
    }//GEN-LAST:event_comboBuscarLotePorClienteItemStateChanged

    private void comboBuscarLotePorContratoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboBuscarLotePorContratoItemStateChanged
        if(evt.getStateChange() == ItemEvent.DESELECTED){
            cargarComboLotes();
            cargarListaLotes();
        }
    }//GEN-LAST:event_comboBuscarLotePorContratoItemStateChanged

    private void comboBuscarLotePorLoteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboBuscarLotePorLoteItemStateChanged
        if(evt.getStateChange() == ItemEvent.DESELECTED){
            cargarListaLotes();
        }
    }//GEN-LAST:event_comboBuscarLotePorLoteItemStateChanged

    private void tablaLotesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaLotesMouseClicked
        if(SwingUtilities.isRightMouseButton(evt)){
            tablaLotes.setRowSelectionInterval(tablaLotes.rowAtPoint( evt.getPoint() ), tablaLotes.rowAtPoint( evt.getPoint() ));
            tablaLotes.setColumnSelectionInterval(0, tablaLotes.getColumnCount()-1);
            subMenuTablaLotes.show(tablaLotes, evt.getPoint().x, evt.getPoint().y); 
        }

        if(evt.getClickCount()==2){
            subMenuAbrirLote.doClick();                    
        }
    }//GEN-LAST:event_tablaLotesMouseClicked

    private void btnGenerarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarExcelActionPerformed
        modelo.Metodos.JTableToExcel(tablaLotes, btnGenerarExcel);
    }//GEN-LAST:event_btnGenerarExcelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JProgressBar barraProgreso;
    public javax.swing.JButton btnCargarLotes;
    public javax.swing.JButton btnGenerarExcel;
    public javax.swing.JComboBox<Cliente> comboBuscarLotePorCliente;
    public javax.swing.JComboBox<String> comboBuscarLotePorContrato;
    public javax.swing.JComboBox<String> comboBuscarLotePorLote;
    public javax.swing.JComboBox<String> comboBuscarLotePorTipoDeContrato;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lblFilasSeleccionadas;
    public javax.swing.JMenuItem subMenuAbrirLote;
    public javax.swing.JMenuItem subMenuDarPorTerminado;
    public javax.swing.JMenuItem subMenuPrepararDespacho;
    public javax.swing.JPopupMenu subMenuTablaLotes;
    public javax.swing.JTable tablaLotes;
    // End of variables declaration//GEN-END:variables

}
