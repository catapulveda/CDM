package paneles;

import CopyPasteJTable.ExcelAdapter;
import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableRowSorter;
import modelo.CustomTableModel;

public class PanelControl extends javax.swing.JPanel {

    TableColumnAdjuster ajustarColumna;
    
    CustomTableModel modeloTablaControl;
        
    LinkedList<RowFilter<Object, Object>> filtros = new LinkedList<>();
    List<String> nombreFiltros = new ArrayList<>();
    TableRowSorter rowSorter;
    int IDBUSQUEDA = 3;
    
    public PanelControl() {
        initComponents();
        
        ajustarColumna = new TableColumnAdjuster(tablaControl);
        ExcelAdapter excelAdapter = new CopyPasteJTable.ExcelAdapter(tablaControl);
        
        comboColumnas.setUI(JComboBoxColor.JComboBoxColor.createUI(comboColumnas));
        comboColumnas.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        
        combo.setUI(JComboBoxColor.JComboBoxColor.createUI(combo));
        combo.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        
        tablaControl.getSelectionModel().addListSelectionListener((ListSelectionEvent e)->{
            if (e.getValueIsAdjusting()){
                lblFilasSeleccionadas.setText("Columnas: " + tablaControl.getSelectedColumnCount() + " Filas: " + tablaControl.getSelectedRowCount()+" Total filas: "+tablaControl.getRowCount());
            }
        });
        
        
        
    }
    
    public void cargarTablaControl(){
        
        tablaControl.setRowSorter(null);
        modeloTablaControl = new CustomTableModel(
            new String[][]{}, 
            modelo.ControlTotal.getColumnNames(), 
            tablaControl, 
            modelo.ControlTotal.getColumnClass(), 
            modelo.ControlTotal.getColumnEditables()
        );
        modeloTablaControl.setMenu(menuTabla);
        modeloTablaControl.setMenuItem(subMenuFiltrar);
        modelo.ControlTotal.cargarTrafos(modeloTablaControl, barraControl);
        
        tablaControl.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaControl.setCellSelectionEnabled(true);
        tablaControl.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
               
        ajustarColumna.adjustColumns();        
        
        tablaControl.getColumnModel().getColumn(0).setCellRenderer(new JButtonIntoJTable.BotonEnColumna());
            
        rowSorter = new TableRowSorter(modeloTablaControl);
        tablaControl.setRowSorter(rowSorter);
        
        comboColumnas.removeAllItems();
        comboColumnas.addItem("TODOS");
        for(String col : modelo.ControlTotal.getColumnNames()){
            comboColumnas.addItem(col);
        }        
    }
    
    void distintos(int col){
        List<String> datos = new ArrayList<>();
        for(int i=0; i<tablaControl.getRowCount(); i++){
            datos.add(""+tablaControl.getValueAt(i, col));
        }
        datos = datos.stream().distinct().collect(Collectors.toList());
        combo.removeAllItems();
        datos.stream().forEach(d->{combo.addItem(d);});        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuTabla = new javax.swing.JPopupMenu();
        subMenuFiltrar = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaControl = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        comboColumnas = new javax.swing.JComboBox<>();
        combo = new javax.swing.JComboBox<>();
        cjBuscar = new CompuChiqui.JTextFieldPopup();
        btnAplicarFiltro = new javax.swing.JButton();
        btnAplicarFiltro1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnGenerarExcel = new javax.swing.JButton();
        btnRefrescar = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        lblFilasSeleccionadas = new javax.swing.JLabel();
        barraControl = new javax.swing.JProgressBar();

        subMenuFiltrar.setFont(new java.awt.Font("Ebrima", 1, 12)); // NOI18N
        subMenuFiltrar.setText("jMenuItem1");
        subMenuFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuFiltrarActionPerformed(evt);
            }
        });
        menuTabla.add(subMenuFiltrar);

        tablaControl.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        tablaControl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "CONTROL TOTAL"
            }
        ));
        tablaControl.setName("Control total"); // NOI18N
        tablaControl.setRowHeight(25);
        tablaControl.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tablaControl);

        jToolBar1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar1.setFloatable(false);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel1.setText("Buscar por: ");
        jToolBar1.add(jLabel1);

        comboColumnas.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        comboColumnas.setForeground(new java.awt.Color(255, 255, 255));
        comboColumnas.setMaximumRowCount(16);
        comboColumnas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboColumnasItemStateChanged(evt);
            }
        });
        jToolBar1.add(comboColumnas);

        combo.setMaximumRowCount(16);
        combo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboItemStateChanged(evt);
            }
        });
        jToolBar1.add(combo);

        cjBuscar.setPlaceholder("Ingrese numero de serie");
        cjBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjBuscarKeyReleased(evt);
            }
        });
        jToolBar1.add(cjBuscar);

        btnAplicarFiltro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/filtro.png"))); // NOI18N
        btnAplicarFiltro.setFocusable(false);
        btnAplicarFiltro.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnAplicarFiltro.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAplicarFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAplicarFiltroActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAplicarFiltro);

        btnAplicarFiltro1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/filtromenos.png"))); // NOI18N
        btnAplicarFiltro1.setToolTipText("Quitar filtros");
        btnAplicarFiltro1.setFocusable(false);
        btnAplicarFiltro1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAplicarFiltro1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAplicarFiltro1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAplicarFiltro1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAplicarFiltro1);

        jSeparator1.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jToolBar1.add(jSeparator1);

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

        btnRefrescar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/actualizar.png"))); // NOI18N
        btnRefrescar.setToolTipText("Actualizar lista de lotes");
        btnRefrescar.setFocusable(false);
        btnRefrescar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefrescar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefrescarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRefrescar);

        jToolBar2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar2.setFloatable(false);

        lblFilasSeleccionadas.setFont(new java.awt.Font("Enter Sansman", 1, 12)); // NOI18N
        jToolBar2.add(lblFilasSeleccionadas);

        barraControl.setStringPainted(true);
        jToolBar2.add(barraControl);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                .addGap(23, 23, 23))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 381, Short.MAX_VALUE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        cargarTablaControl();        
    }//GEN-LAST:event_btnRefrescarActionPerformed

    private void cjBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjBuscarKeyReleased
        rowSorter.setRowFilter(RowFilter.regexFilter(cjBuscar.getText().toUpperCase(), IDBUSQUEDA));
    }//GEN-LAST:event_cjBuscarKeyReleased

    private void comboColumnasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboColumnasItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED){
            if(comboColumnas.getSelectedIndex()==0){
                combo.removeAllItems();
                filtros.clear();
            }else{
                distintos( (comboColumnas.getSelectedIndex()-1) );
            }            
//            switch(comboColumnas.getSelectedIndex()){
//                case 0:IDBUSQUEDA = 3;cjBuscar.setPlaceholder("Ingrese el numero de serie");break;
//                case 1:IDBUSQUEDA = 4;cjBuscar.setPlaceholder("Ingrese el numero de empresa");break;
//                case 2:IDBUSQUEDA = 1;cjBuscar.setPlaceholder("Ingrese el nombre del cliente");break;               
//            }
            repaint();
        }
    }//GEN-LAST:event_comboColumnasItemStateChanged

    private void btnGenerarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarExcelActionPerformed
        modelo.Metodos.JTableToExcel(tablaControl, btnGenerarExcel);
    }//GEN-LAST:event_btnGenerarExcelActionPerformed

    private void subMenuFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuFiltrarActionPerformed
        IDBUSQUEDA = tablaControl.getSelectedColumn();
    }//GEN-LAST:event_subMenuFiltrarActionPerformed

    private void btnAplicarFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAplicarFiltroActionPerformed
        if(comboColumnas.getSelectedIndex()>0){            
            filtros.add(RowFilter.regexFilter(cjBuscar.getText().toUpperCase(), (comboColumnas.getSelectedIndex()-1) ));
            nombreFiltros.add(cjBuscar.getText().toUpperCase());
        }
        rowSorter.setRowFilter(RowFilter.andFilter(filtros));
        btnAplicarFiltro.setText(""+filtros.size());
    }//GEN-LAST:event_btnAplicarFiltroActionPerformed

    private void comboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboItemStateChanged
        if(evt.getStateChange()==ItemEvent.SELECTED){
            cjBuscar.setText(combo.getSelectedItem().toString());
        }
    }//GEN-LAST:event_comboItemStateChanged

    private void btnAplicarFiltro1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAplicarFiltro1ActionPerformed
        if(comboColumnas.getSelectedIndex()==0){
            filtros.clear();
            rowSorter.setRowFilter(RowFilter.regexFilter(""));
            btnAplicarFiltro.setText("");
            cjBuscar.setText("");
        }else{
            String[] buttons = new String[nombreFiltros.size()];
            for(int i=0; i<nombreFiltros.size(); i++){
                buttons[i] = nombreFiltros.get(i);
            }
            int r = JOptionPane.showOptionDialog(this, "Seleccione", "Mensaje", 1, 1, null, buttons, null);
            if(r>-1){
                filtros.remove(r);
                nombreFiltros.remove(r);
                btnAplicarFiltro.setText(""+filtros.size());
                rowSorter.setRowFilter(RowFilter.andFilter(filtros));
            }
        }  
        
        
    }//GEN-LAST:event_btnAplicarFiltro1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barraControl;
    private javax.swing.JButton btnAplicarFiltro;
    private javax.swing.JButton btnAplicarFiltro1;
    public javax.swing.JButton btnGenerarExcel;
    public javax.swing.JButton btnRefrescar;
    private CompuChiqui.JTextFieldPopup cjBuscar;
    private javax.swing.JComboBox<String> combo;
    private javax.swing.JComboBox<String> comboColumnas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lblFilasSeleccionadas;
    private javax.swing.JPopupMenu menuTabla;
    private javax.swing.JMenuItem subMenuFiltrar;
    private javax.swing.JTable tablaControl;
    // End of variables declaration//GEN-END:variables
}
