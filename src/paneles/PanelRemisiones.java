package paneles;

import CopyPasteJTable.ExcelAdapter;
import Dialogos.DialogoConfigurarConsecutivosRemision;
import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.net.Inet4Address;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.TableRowSorter;
import modelo.ConexionBD;
import modelo.CustomTableModel;
import view.DespachoARemision;
import view.REMISIONESCDM;
import view2.DespachoARemision2;

public class PanelRemisiones extends javax.swing.JPanel {

    TableColumnAdjuster ajustarColumna;
    
    CustomTableModel modeloTablaRemisiones;
    TableRowSorter rowSorter;
    
    int ID_BUSQUEDA = 2;
    int ID_REMISION = -1;
    
    ConexionBD conexion = new ConexionBD();
    
    public PanelRemisiones() {
        initComponents();
        
        ajustarColumna = new TableColumnAdjuster(tablaRemisiones);
        ExcelAdapter excelAdapter = new CopyPasteJTable.ExcelAdapter(tablaRemisiones);
        
//        cargarTablaRemisiones();
        
        comboTipo.setUI(JComboBoxColor.JComboBoxColor.createUI(comboTipo));
        comboEmpresa.setUI(JComboBoxColor.JComboBoxColor.createUI(comboEmpresa));
        
        comboTipo.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        comboEmpresa.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
    }
    
    public void cargarTablaRemisiones(){
        tablaRemisiones.setRowSorter(null);
        modeloTablaRemisiones = new CustomTableModel(
            new String[][]{}, 
            modelo.Remision.getColumnNames(), 
            tablaRemisiones, 
            modelo.Remision.getColumnClass(), 
            modelo.Remision.getColumnEditables()
        );
       
        modelo.Remision.cargarRemisiones(modeloTablaRemisiones, comboTipo.getSelectedItem().toString(), comboEmpresa.getSelectedItem().toString());
        
        tablaRemisiones.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaRemisiones.setCellSelectionEnabled(true);
        tablaRemisiones.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        
        ajustarColumna.adjustColumns();
        
        tablaRemisiones.getColumnModel().getColumn(0).setCellRenderer(new JButtonIntoJTable.BotonEnColumna());
            
        rowSorter = new TableRowSorter(modeloTablaRemisiones);
        tablaRemisiones.setRowSorter(rowSorter);                         
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SubMenuEliminarRemision = new javax.swing.JPopupMenu();
        submenuItemEliminarRemision = new javax.swing.JMenuItem();
        SubMenuItemAnularRemision = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaRemisiones = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel4 = new javax.swing.JLabel();
        cjBuscar = new CompuChiqui.JTextFieldPopup();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        comboTipo = new javax.swing.JComboBox<>();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        comboEmpresa = new javax.swing.JComboBox<>();
        btnCargarRemisiones = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        generarExcelDespachos = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jProgressBar1 = new javax.swing.JProgressBar();
        lblFilasSeleccionadas = new javax.swing.JLabel();

        submenuItemEliminarRemision.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/basura.png"))); // NOI18N
        submenuItemEliminarRemision.setText("Eliminar Remision");
        submenuItemEliminarRemision.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submenuItemEliminarRemisionActionPerformed(evt);
            }
        });
        SubMenuEliminarRemision.add(submenuItemEliminarRemision);

        SubMenuItemAnularRemision.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        SubMenuItemAnularRemision.setText("Anular Remision");
        SubMenuItemAnularRemision.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubMenuItemAnularRemisionActionPerformed(evt);
            }
        });
        SubMenuEliminarRemision.add(SubMenuItemAnularRemision);

        tablaRemisiones.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        tablaRemisiones.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaRemisiones.setRowHeight(25);
        tablaRemisiones.getTableHeader().setReorderingAllowed(false);
        tablaRemisiones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaRemisionesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaRemisiones);

        jToolBar1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setToolTipText("");

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel4.setText("Buscar:");
        jToolBar1.add(jLabel4);

        cjBuscar.setToolTipText("");
        cjBuscar.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        cjBuscar.setPlaceholder("Ingrese el numero de remision");
        cjBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjBuscarKeyReleased(evt);
            }
        });
        jToolBar1.add(cjBuscar);
        jToolBar1.add(jSeparator3);
        jToolBar1.add(jSeparator1);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel1.setText("Tipo:");
        jToolBar1.add(jLabel1);

        comboTipo.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        comboTipo.setForeground(new java.awt.Color(255, 255, 255));
        comboTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODAS", "SIN RETORNO", "CON RETORNO" }));
        comboTipo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboTipoItemStateChanged(evt);
            }
        });
        jToolBar1.add(comboTipo);
        jToolBar1.add(jSeparator2);

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel2.setText("Empresa:");
        jToolBar1.add(jLabel2);

        comboEmpresa.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        comboEmpresa.setForeground(new java.awt.Color(255, 255, 255));
        comboEmpresa.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODAS", "CDM", "MEDIDORES", "CONSORCIO" }));
        comboEmpresa.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboEmpresaItemStateChanged(evt);
            }
        });
        jToolBar1.add(comboEmpresa);

        btnCargarRemisiones.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/actualizar.png"))); // NOI18N
        btnCargarRemisiones.setToolTipText("Actualizar lista de lotes");
        btnCargarRemisiones.setFocusable(false);
        btnCargarRemisiones.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCargarRemisiones.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCargarRemisiones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarRemisionesActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCargarRemisiones);
        jToolBar1.add(jSeparator4);

        generarExcelDespachos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/excel.png"))); // NOI18N
        generarExcelDespachos.setToolTipText("Exportar a excel");
        generarExcelDespachos.setFocusable(false);
        generarExcelDespachos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        generarExcelDespachos.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        generarExcelDespachos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generarExcelDespachosActionPerformed(evt);
            }
        });
        jToolBar1.add(generarExcelDespachos);

        jToolBar2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar2.setFloatable(false);

        jProgressBar1.setStringPainted(true);
        jToolBar2.add(jProgressBar1);

        lblFilasSeleccionadas.setFont(new java.awt.Font("Enter Sansman", 1, 12)); // NOI18N
        jToolBar2.add(lblFilasSeleccionadas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jToolBar2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addGap(25, 25, 25))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 307, Short.MAX_VALUE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cjBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjBuscarKeyReleased
        rowSorter.setRowFilter(RowFilter.regexFilter(cjBuscar.getText().toUpperCase()));
    }//GEN-LAST:event_cjBuscarKeyReleased

    private void comboTipoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboTipoItemStateChanged
        if(evt.getStateChange()==ItemEvent.SELECTED){
            cargarTablaRemisiones();
        }
    }//GEN-LAST:event_comboTipoItemStateChanged

    private void comboEmpresaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboEmpresaItemStateChanged
        if(evt.getStateChange()==ItemEvent.SELECTED){
            cargarTablaRemisiones();
        }
    }//GEN-LAST:event_comboEmpresaItemStateChanged

    private void submenuItemEliminarRemisionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submenuItemEliminarRemisionActionPerformed
        try{
            int fila[] = tablaRemisiones.getSelectedRows();
            if((Inet4Address.getLocalHost().getHostName().equals("ALMACEN") || Inet4Address.getLocalHost().getHostName().equals("PROGRAMADOR"))){
                if(JOptionPane.showConfirmDialog(null, "Desea continuar eliminado las remisiones seleccionadas?")== JOptionPane.YES_OPTION){
                    for (int i = fila.length-1; i >= 0; i--) {
                        conexion.conectar();                    
                        if(conexion.GUARDAR("DELETE FROM remision WHERE idremision='"+tablaRemisiones.getValueAt(fila[i], 0)+"' ")){                            
                            conexion.CERRAR();
                        }
                    }
                    cargarTablaRemisiones();
                    if(JOptionPane.showConfirmDialog(this, "Deberá configurar el consecutivo de la remisiones. Desea realizarlo ahora mismo?")==JOptionPane.YES_OPTION){
                        new DialogoConfigurarConsecutivosRemision(null, true).setVisible(true);
                    }
                }                
            }else{
                modelo.Metodos.M("SOLO EL PERSONAL DE ALMACEN PUEDE ELIMINAR REMISIONES", "advertencia.png");
            }
        }catch(Exception e){
            modelo.Metodos.ERROR(e, "OCURRIO UN ERROR AL INTENTAR ELIMINAR LA REMISION");
        }
    }//GEN-LAST:event_submenuItemEliminarRemisionActionPerformed

    private void SubMenuItemAnularRemisionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubMenuItemAnularRemisionActionPerformed
        try{
            int fila = tablaRemisiones.getSelectedRow();
            if(fila>=0){
                if(JOptionPane.showConfirmDialog(this, "Seguro que desea anular la remision "+tablaRemisiones.getValueAt(fila, 2))==JOptionPane.YES_OPTION){
                    conexion.conectar();
                    if(conexion.GUARDAR("UPDATE remision SET estado='FALSE' WHERE idremision='"+tablaRemisiones.getValueAt(fila, 0)+"' ")){
                        cargarTablaRemisiones();
                    }
                }
            }
        }catch(Exception e){
            modelo.Metodos.M("OCURRIO UN ERROR AL INTENTAR ANULAR LA REMISION\n"+e, "error.png");
        }
    }//GEN-LAST:event_SubMenuItemAnularRemisionActionPerformed

    private void tablaRemisionesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaRemisionesMouseClicked
        if(SwingUtilities.isRightMouseButton(evt)){
            if(tablaRemisiones.getSelectedRows().length==1){
                tablaRemisiones.setRowSelectionInterval(tablaRemisiones.rowAtPoint( evt.getPoint() ), tablaRemisiones.rowAtPoint( evt.getPoint() ));
                tablaRemisiones.setColumnSelectionInterval(0, tablaRemisiones.getColumnCount()-1);
            }                                    
            ID_REMISION = (int) tablaRemisiones.getValueAt(tablaRemisiones.getSelectedRow(), 0);
            submenuItemEliminarRemision.setText("Eliminar Remision ("+tablaRemisiones.getSelectedRows().length+")");
            SubMenuEliminarRemision.show(tablaRemisiones, evt.getPoint().x, evt.getPoint().y); 
        }
        if(evt.getClickCount()==2 && SwingUtilities.isLeftMouseButton(evt)){
            int fila = tablaRemisiones.getSelectedRow();
            int idremision = (int)tablaRemisiones.getValueAt(fila, 0);
            conexion.conectar();
            ResultSet rs = conexion.CONSULTAR("SELECT r.*, c.idcliente, c.nombrecliente, c.nitcliente as nit FROM remision r "
                    + "INNER JOIN cliente c ON c.idcliente=r.idcliente "
                    + "WHERE idremision="+idremision);
            try {
                rs.next();
                if(rs.getInt("iddespacho")==0){
                    
                    REMISIONESCDM remisiones = new REMISIONESCDM();
                    remisiones.setIDREMISION(idremision);
                    remisiones.AbrirEncabezadoRemision(rs);
                    remisiones.setACTUALIZANDO(true);
                    
                    if(rs.getString("tipo_remision").equals("CON RETORNO")){
                        remisiones.setTIPO("CON RETORNO");
                        if(rs.getString("empresa_remision").equals("CDM") || rs.getString("empresa_remision").equals("MEDIDORES")){
                            remisiones.setCONSECUTIVO_EMPRESA("cdmretorno");
                            remisiones.setREPORTE("REMISIONCDM");
                        }else if(rs.getString("empresa_remision").equals("CONSORCIO")){
                            remisiones.setCONSECUTIVO_EMPRESA("consorcioretorno");
                            remisiones.setREPORTE("CONSORCIO_HERRAMIENTAS");
                            remisiones.cargarTablaHerramientas();
                            remisiones.cargarResultadoHerramientas();
                        }
                    }else if(rs.getString("tipo_remision").equals("SIN RETORNO")){
                        remisiones.setTIPO("SIN RETORNO");
                        if(rs.getString("empresa_remision").equals("CDM")||rs.getString("empresa_remision").equals("MEDIDORES")){
                            remisiones.setREPORTE("REMISIONCDM");
                            remisiones.setCONSECUTIVO_EMPRESA("cdmsinretorno");
                        }else{
                            remisiones.setREPORTE("CONSORCIO_HERRAMIENTAS");
                            remisiones.setCONSECUTIVO_EMPRESA("consorciosinretorno");
                        }                        
                    }
                    remisiones.setVisible(true);
                    
                }else if(rs.getInt("iddespacho")>0){
                    DespachoARemision2 dar = new DespachoARemision2();
                    dar.setACTUALIZANDO(true);
                    dar.setIDREMISION(idremision);
                    dar.cargarTabla();
                    dar.cargarServicios();
                    dar.setExtendedState(Frame.MAXIMIZED_BOTH);
                    dar.setTitle("Remision "+tablaRemisiones.getValueAt(fila, 2)+" DE "+tablaRemisiones.getValueAt(fila, 4));            
                    dar.setVisible(true);
                }
            } catch (SQLException ex) {
                Logger.getLogger(PanelRemisiones.class.getName()).log(Level.SEVERE, null, ex);
                modelo.Metodos.M("ERROR AL ABRIR LA REMISION\n"+ex, "error.png");
            }                                    
        }
    }//GEN-LAST:event_tablaRemisionesMouseClicked

    private void btnCargarRemisionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarRemisionesActionPerformed
        cargarTablaRemisiones();
    }//GEN-LAST:event_btnCargarRemisionesActionPerformed

    private void generarExcelDespachosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarExcelDespachosActionPerformed
        modelo.Metodos.JTableToExcel(tablaRemisiones, generarExcelDespachos);
    }//GEN-LAST:event_generarExcelDespachosActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu SubMenuEliminarRemision;
    private javax.swing.JMenuItem SubMenuItemAnularRemision;
    public javax.swing.JButton btnCargarRemisiones;
    private CompuChiqui.JTextFieldPopup cjBuscar;
    private javax.swing.JComboBox<String> comboEmpresa;
    private javax.swing.JComboBox<String> comboTipo;
    public javax.swing.JButton generarExcelDespachos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lblFilasSeleccionadas;
    private javax.swing.JMenuItem submenuItemEliminarRemision;
    private javax.swing.JTable tablaRemisiones;
    // End of variables declaration//GEN-END:variables
}



