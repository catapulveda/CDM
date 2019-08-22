package paneles;

import Animacion.Fade;
import CopyPasteJTable.ExcelAdapter;
import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableRowSorter;
import modelo.ConexionBD;
import modelo.CustomTableModel;
import view.DespachoARemision;
import view2.DespachoARemision2;

public final class PanelDespachos extends javax.swing.JPanel {

    TableColumnAdjuster ajustarColumna;
    
    CustomTableModel modeloTablaDespachos;
    TableRowSorter rowSorter;
    
    int ID_BUSQUEDA = 3;
    int ID_DESPACHO = -1;
    
    ConexionBD conexion = new ConexionBD();
   
    public PanelDespachos() {
        initComponents();
        
        ajustarColumna = new TableColumnAdjuster(tabla);
        ExcelAdapter excelAdapter = new CopyPasteJTable.ExcelAdapter(tabla);
        
        
        tabla.getSelectionModel().addListSelectionListener((ListSelectionEvent e)->{
            if (e.getValueIsAdjusting()){
                lblFilasSeleccionadas.setText("Columnas: " + tabla.getSelectedColumnCount() + " Filas: " + tabla.getSelectedRowCount()+" Total filas: "+tabla.getRowCount());
            }
        });
        
    }

    public void cargarTablaDespachos(){
        tabla.setRowSorter(null);
        modeloTablaDespachos = new CustomTableModel(
            new String[][]{}, 
            modelo.Despacho.getColumnNames(), 
            tabla, 
            modelo.Despacho.getColumnClass(), 
            modelo.Despacho.getColumnEditables()
        );
        
        modelo.Despacho.cargarDespachos(modeloTablaDespachos);
        
        tabla.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tabla.setCellSelectionEnabled(true);
        tabla.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        
        ajustarColumna.adjustColumns();
        
        tabla.getColumnModel().getColumn(0).setCellRenderer(new JButtonIntoJTable.BotonEnColumna());
            
        rowSorter = new TableRowSorter(modeloTablaDespachos);
        tabla.setRowSorter(rowSorter); 
        
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SubMenuTablaListasDespacho = new javax.swing.JPopupMenu();
        SubMenuItemEliminarDespacho = new javax.swing.JMenuItem();
        SubMenuItemTerminarDespacho = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        cjBuscarDespacho = new CompuChiqui.JTextFieldPopup();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel3 = new javax.swing.JLabel();
        comboBuscar = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnCargarDespachos = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        generarExcelDespachos = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jProgressBar1 = new javax.swing.JProgressBar();
        lblFilasSeleccionadas = new javax.swing.JLabel();

        SubMenuItemEliminarDespacho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/basura.png"))); // NOI18N
        SubMenuItemEliminarDespacho.setText("Eliminar despacho");
        SubMenuItemEliminarDespacho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubMenuItemEliminarDespachoActionPerformed(evt);
            }
        });
        SubMenuTablaListasDespacho.add(SubMenuItemEliminarDespacho);

        SubMenuItemTerminarDespacho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/bien_pequeno.png"))); // NOI18N
        SubMenuItemTerminarDespacho.setText("Dar por terminado");
        SubMenuItemTerminarDespacho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubMenuItemTerminarDespachoActionPerformed(evt);
            }
        });
        SubMenuTablaListasDespacho.add(SubMenuItemTerminarDespacho);

        tabla.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "DESPACHOS"
            }
        ));
        tabla.setRowHeight(25);
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabla);

        jToolBar1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel1.setText("Buscar:");
        jToolBar1.add(jLabel1);

        cjBuscarDespacho.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjBuscarDespachoKeyReleased(evt);
            }
        });
        jToolBar1.add(cjBuscarDespacho);
        jToolBar1.add(jSeparator2);

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel3.setText("Buscar por:");
        jToolBar1.add(jLabel3);

        comboBuscar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CLIENTE", "DESPACHO" }));
        comboBuscar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBuscarItemStateChanged(evt);
            }
        });
        jToolBar1.add(comboBuscar);
        jToolBar1.add(jSeparator1);

        btnCargarDespachos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/actualizar.png"))); // NOI18N
        btnCargarDespachos.setToolTipText("Actualizar lista de lotes");
        btnCargarDespachos.setFocusable(false);
        btnCargarDespachos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCargarDespachos.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCargarDespachos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarDespachosActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCargarDespachos);
        jToolBar1.add(jSeparator3);

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
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jToolBar2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addGap(19, 19, 19))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 292, Short.MAX_VALUE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCargarDespachosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarDespachosActionPerformed
        cargarTablaDespachos();
    }//GEN-LAST:event_btnCargarDespachosActionPerformed

    private void cjBuscarDespachoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjBuscarDespachoKeyReleased
        rowSorter.setRowFilter(RowFilter.regexFilter(cjBuscarDespacho.getText().toUpperCase(), ID_BUSQUEDA));
    }//GEN-LAST:event_cjBuscarDespachoKeyReleased

    private void comboBuscarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboBuscarItemStateChanged
        if(evt.getStateChange() == ItemEvent.DESELECTED){
            switch(comboBuscar.getSelectedIndex()){
                case 0: ID_BUSQUEDA= 3;break;
                case 1: ID_BUSQUEDA= 1;break;
            }
        }
    }//GEN-LAST:event_comboBuscarItemStateChanged

    private void SubMenuItemEliminarDespachoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubMenuItemEliminarDespachoActionPerformed
        try {
            if (tabla.getSelectedRow() != -1){
                if (JOptionPane.showConfirmDialog(this, "Seguro que desea eliminar el despacho N° " + tabla.getValueAt(tabla.getSelectedRow(), 0), "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    (new Thread(){
                        public void run(){
                            try {                               
                                String mensajes = "";
                                conexion.conectar();
                                ResultSet rs = conexion.CONSULTAR("SELECT * FROM transformador WHERE iddespacho='"+ID_DESPACHO+"' ");
                                while (rs.next()){
                                    if (conexion.GUARDAR(" UPDATE transformador SET estado='EN PLANTA' , iddespacho=null , idremision=null "
                                        + " WHERE identrada='"+rs.getString("identrada")+"' AND item='"+rs.getInt("item")+"' AND numeroserie='"+rs.getString("numeroserie") + "' ")) {
                                        mensajes += "TRANSFORMADOR " + rs.getString("numeroserie") + " EN PLANTA\n";
                                }
                            }
                            if(conexion.GUARDAR(" DELETE FROM despacho WHERE iddespacho='" + ID_DESPACHO + "' ")){
                                modelo.Metodos.M(mensajes + "\nEL DESPACHO No " + tabla.getValueAt(tabla.getSelectedRow(), 1) + " SELECCIONADO HA SIDO ELIMINADO", "bien.png");
                                cargarTablaDespachos();
                            }

                        }catch(Exception ex){
                            Logger.getLogger(PanelDespachos.class.getName()).log(Level.SEVERE, null, ex);
                            modelo.Metodos.M("OCURRIO UN ERROR AL EJECUTAR EL PROCESO DE ELIMINACION DE DESPACHO Y DEVOLUCION DE TRANSFORMADORES A PLANTA\n" + ex, "error.png");
                        } finally {}
                    }
                }).start();
            }
        } else {
            modelo.Metodos.M("SELECCIONE EL DESPACHO QUE DESEA ELIMINAR", "advertencia.png");
        }
        } catch (Exception e) {
            modelo.Metodos.M("OCURRIO UN ERROR AL INTENTAR ELIMINAR EL DESPACHO\n" + e, "error.png");
            Logger.getLogger(PanelDespachos.class.getName()).log(Level.SEVERE, null, e);
        } finally {}
    }//GEN-LAST:event_SubMenuItemEliminarDespachoActionPerformed

    private void SubMenuItemTerminarDespachoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubMenuItemTerminarDespachoActionPerformed
        try{
            if(tabla.getSelectedRow() != -1){
                int fila = tabla.getSelectedRow();
                if(JOptionPane.showConfirmDialog(this, "Desea continuar ? ", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    if (!Boolean.parseBoolean(tabla.getValueAt(fila, 4).toString())) {
                        conexion.conectar();
                        if(conexion.GUARDAR(" UPDATE despacho SET estado_despacho='"+true+"' , fecha_despacho='"+new Date()+"' WHERE iddespacho="+Integer.parseInt(tabla.getValueAt(fila, 0).toString())+" ")){
                            cargarTablaDespachos();
                        }
                    }else{
                        modelo.Metodos.M("ESTE DESPACHO YA ESTA POR TERMINADO", "advertencia.png");
                    }
                }
            } else {
                modelo.Metodos.M("SELECCIONE EL DESPACHO QUE DESEA DAR POR TERMINADO", "advertencia.png");
            }
        }catch(HeadlessException | NumberFormatException e) {
            modelo.Metodos.M("OCURRIO UN ERROR EL INTENTAR DAR POR TERMIANDO AL DESPACHO SELECCIONADO\n"+e, "error.png");
        }
    }//GEN-LAST:event_SubMenuItemTerminarDespachoActionPerformed

    private void tablaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMouseClicked
        if(SwingUtilities.isRightMouseButton(evt)){           
            tabla.setRowSelectionInterval(tabla.rowAtPoint( evt.getPoint() ), tabla.rowAtPoint( evt.getPoint() ));
            tabla.setColumnSelectionInterval(0, tabla.getColumnCount()-1);
            SubMenuTablaListasDespacho.show(tabla, evt.getPoint().x, evt.getPoint().y); 
            ID_DESPACHO = (int) tabla.getValueAt(tabla.getSelectedRow(), 0);
        }

        if(evt.getClickCount()==2){
            int fila = tabla.getSelectedRow();
            DespachoARemision2 dar = new DespachoARemision2();
            dar.setIDDESPACHO((int) tabla.getValueAt(fila, 0));            
            dar.cargarTabla();
            dar.cargarServicios();
            dar.setExtendedState(Frame.MAXIMIZED_BOTH);
            dar.setNodespacho(tabla.getValueAt(fila, 1).toString());
            dar.setCliente(tabla.getValueAt(fila, 3).toString());
            dar.setTitle("Despacho "+tabla.getValueAt(fila, 1)+" DE "+tabla.getValueAt(fila, 3));
            dar.setVisible(true);
        }
    }//GEN-LAST:event_tablaMouseClicked

    private void generarExcelDespachosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarExcelDespachosActionPerformed
        modelo.Metodos.JTableToExcel(tabla, generarExcelDespachos);
    }//GEN-LAST:event_generarExcelDespachosActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem SubMenuItemEliminarDespacho;
    private javax.swing.JMenuItem SubMenuItemTerminarDespacho;
    private javax.swing.JPopupMenu SubMenuTablaListasDespacho;
    public javax.swing.JButton btnCargarDespachos;
    private CompuChiqui.JTextFieldPopup cjBuscarDespacho;
    private javax.swing.JComboBox<String> comboBuscar;
    public javax.swing.JButton generarExcelDespachos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lblFilasSeleccionadas;
    private javax.swing.JTable tabla;
    // End of variables declaration//GEN-END:variables
}
