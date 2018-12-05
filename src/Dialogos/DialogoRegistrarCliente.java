package Dialogos;

import CompuChiqui.JTextFieldPopup;
import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import modelo.ConexionBD;
import modelo.CustomTableModel;

public final class DialogoRegistrarCliente extends javax.swing.JDialog {
    
    CustomTableModel modeloTabla;
    TableRowSorter rowSorter;
    
    private int idUsuario = 1;
    
    TableColumnAdjuster ajustarColumna;
    
    ConexionBD conexion = new ConexionBD();
    
    private DefaultTableModel modelociudad;
    int con = 0, conciu = 0;
    
    public Icon bien = new ImageIcon(getClass().getResource("/recursos/images/bien.png"));
    public Icon mal = new ImageIcon(getClass().getResource("/recursos/images/error.png"));
    
    public DialogoRegistrarCliente(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        ajustarColumna = new TableColumnAdjuster(TablaClientes);
        Cargar();
        cjNombre.grabFocus();        
    }
    
    public void CargarCiudad(int idcliente){
        try{
            String data[][]={};
            String col[]={"","CIUDAD","DESTINO","TELEFONO"};
            conexion.conectar();
            ResultSet rs = conexion.CONSULTAR("SELECT * FROM ciudad c INNER JOIN ciudadcliente ciu ON c.idciudad=ciu.idciudad WHERE idcliente="+idcliente+"  ");
            modelociudad = new DefaultTableModel(data, col);
            table.setModel(modelociudad);
            while(rs.next()){
                modelociudad.insertRow(conciu, new Object[]{});
                modelociudad.setValueAt(rs.getInt("idciudad"), conciu, 0);
                modelociudad.setValueAt(rs.getString("nombreciudad"), conciu, 1);
                modelociudad.setValueAt(rs.getString("direccionciudad"), conciu, 2);
                modelociudad.setValueAt(rs.getString("telefonociudad"), conciu, 3);
                conciu++;
            }
            
            table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            table.setCellSelectionEnabled(true);
            table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        
            modelociudad.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent tme) {
                    if(tme.getType() == TableModelEvent.UPDATE){
                        if(conexion.GUARDAR("UPDATE ciudad SET nombreciudad='"+modelociudad.getValueAt(tme.getFirstRow(), 1)+"' , direccionciudad='"+modelociudad.getValueAt(tme.getFirstRow(), 2)+"' , telefonociudad='"+modelociudad.getValueAt(tme.getFirstRow(), 3)+"' WHERE idciudad='"+modelociudad.getValueAt(tme.getFirstRow(), 0)+"' ")){
                            M("ITEM "+modelociudad.getValueAt(tme.getFirstRow(), 0)+" ACTUALIZADO",bien);
                        }
                    }
                }
            });        
            conciu = 0;
        }catch(Exception e){
            M("OCURRIO UN ERROR AL INTENTAR CARGAR LAS CIUDADES DEL CLIENTE SELECCIONADO\n"+e,mal);
        }finally{
            conexion.CERRAR();
        }
    }

    public void Cargar(){
        
        TablaClientes.setRowSorter(null);
        modeloTabla = new CustomTableModel(
                new String[][]{}, 
                modelo.Cliente.getColumnNames(), 
                TablaClientes, 
                modelo.Cliente.getColumnClass(), 
                modelo.Cliente.getColumnEditables());
        modelo.Cliente.cargarClientes(modeloTabla);
        
        modeloTabla.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if(e.getType() == TableModelEvent.UPDATE){
                    String nombre = modeloTabla.getValueAt(e.getFirstRow(), 1).toString();
                    String nit = modeloTabla.getValueAt(e.getFirstRow(), 2).toString();
                    String id = modeloTabla.getValueAt(e.getFirstRow(), 0).toString();
                    if(conexion.GUARDAR("UPDATE cliente SET nombrecliente='"+nombre+"' , nitcliente='"+nit+"' WHERE idcliente="+id+" ")){
                        
                    }
                }
            }
        });
        
        TablaClientes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        TablaClientes.setCellSelectionEnabled(true);
        TablaClientes.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");

        ajustarColumna.adjustColumns();

        TablaClientes.getColumnModel().getColumn(0).setCellRenderer(new JButtonIntoJTable.BotonEnColumna());

        rowSorter = new TableRowSorter(modeloTabla);
        TablaClientes.setRowSorter(rowSorter); 
    }
    
    public void M(String m, Icon i){
        JOptionPane.showMessageDialog(this, m, "Aviso", JOptionPane.INFORMATION_MESSAGE, i);
    }
    
    public void Guardar(){
        if(!cjNombre.getText().isEmpty()){
            String nombre = cjNombre.getText().trim().toUpperCase();
            String nit = cjCedula.getText().trim().toUpperCase();
            String GuardarNombre = "INSERT INTO cliente (nombrecliente,nitcliente,representante) VALUES ('"+nombre+"' , '"+nit+"' , '"+cjrepresentante.getText().trim()+"') ";
            if(JOptionPane.showConfirmDialog(this, "Seguro desea guardar el nuevo cliente ?") == JOptionPane.YES_OPTION){
                if(conexion.GUARDAR(GuardarNombre)){
                        Limpiar();
                        Cargar();
                        habilitarCajas(false);
                    }
            }
        }else{
            JOptionPane.showMessageDialog(this, "INGRESE EL NOMBRE DEL CLIENTE A REGISTRAR.", "FALTAN DATOS", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/recursos/images/advertencia.png")));
        }
    }
    
    public void Limpiar(){
        cjNombre.setText(null);
        cjCedula.setText(null);
        cjrepresentante.setText(null);
    }
    
    void habilitarCajas(boolean estado){
        cjNombre.setEnabled(estado);
        cjCedula.setEnabled(estado);
        cjrepresentante.setEnabled(estado);
        cjNombre.grabFocus();        
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        SubMenuTabla = new javax.swing.JPopupMenu();
        subMenuEliminarCliente = new javax.swing.JMenuItem();
        SubMenuItemAsociarCiudad = new javax.swing.JMenuItem();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        TablaClientes = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        cjbuscarmarcatransf = new javax.swing.JTextField();
        jToolBar1 = new javax.swing.JToolBar();
        btnNuevo = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnNuevo1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnGuardar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cjNombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cjCedula = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cjrepresentante = new javax.swing.JTextField();

        subMenuEliminarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        subMenuEliminarCliente.setText("Eliminar");
        subMenuEliminarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuEliminarClienteActionPerformed(evt);
            }
        });
        SubMenuTabla.add(subMenuEliminarCliente);

        SubMenuItemAsociarCiudad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/siguiente.png"))); // NOI18N
        SubMenuItemAsociarCiudad.setText("Asociar Ciudad");
        SubMenuItemAsociarCiudad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubMenuItemAsociarCiudadActionPerformed(evt);
            }
        });
        SubMenuTabla.add(SubMenuItemAsociarCiudad);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Clientes");

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        TablaClientes.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        TablaClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        TablaClientes.setRowHeight(25);
        TablaClientes.setShowHorizontalLines(false);
        TablaClientes.setShowVerticalLines(false);
        TablaClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaClientesMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(TablaClientes);

        table.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        table.setRowHeight(22);
        jScrollPane2.setViewportView(table);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Buscar:");

        cjbuscarmarcatransf.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjbuscarmarcatransfKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cjbuscarmarcatransf))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cjbuscarmarcatransf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
        );

        jToolBar1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar1.setRollover(true);

        btnNuevo.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/add.png"))); // NOI18N
        btnNuevo.setText("Nuevo");
        btnNuevo.setFocusable(false);
        btnNuevo.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnNuevo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNuevo);
        jToolBar1.add(jSeparator2);

        btnNuevo1.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnNuevo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        btnNuevo1.setText("Cancelar");
        btnNuevo1.setFocusable(false);
        btnNuevo1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnNuevo1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevo1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNuevo1);
        jToolBar1.add(jSeparator1);

        btnGuardar.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Guardar.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnGuardar);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Nombre:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(jLabel2, gridBagConstraints);

        cjNombre.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel1.add(cjNombre, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("NIT / CEDULA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanel1.add(jLabel3, gridBagConstraints);

        cjCedula.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        jPanel1.add(cjCedula, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Representante:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanel1.add(jLabel4, gridBagConstraints);

        cjrepresentante.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        jPanel1.add(cjrepresentante, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void TablaClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaClientesMouseClicked
        if(SwingUtilities.isRightMouseButton(evt)){
            TablaClientes.setRowSelectionInterval(TablaClientes.rowAtPoint( evt.getPoint() ), TablaClientes.rowAtPoint( evt.getPoint() ));
            TablaClientes.setColumnSelectionInterval(0, TablaClientes.getColumnCount()-1);
            SubMenuTabla.show(TablaClientes, evt.getPoint().x, evt.getPoint().y); 
        }
        if(SwingUtilities.isLeftMouseButton(evt)){
            CargarCiudad(Integer.parseInt(TablaClientes.getValueAt(TablaClientes.getSelectedRow(), 0).toString()));
        }    
    }//GEN-LAST:event_TablaClientesMouseClicked

    private void subMenuEliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuEliminarClienteActionPerformed
        if(TablaClientes.getSelectedRow() != -1)
        {
            String sql = "DELETE FROM cliente WHERE id_cliente='"+TablaClientes.getValueAt(TablaClientes.getSelectedRow(), 0)+"' ";
            if(JOptionPane.showConfirmDialog(this, "Desea eliminar el registro seleccionado","Cliente: "+TablaClientes.getValueAt(TablaClientes.getSelectedRow(), 1),JOptionPane.DEFAULT_OPTION)== JOptionPane.YES_OPTION)
            {
                if(conexion.GUARDAR(sql)){M("Cliente eliminado",bien);}Cargar();
            }
        }
    }//GEN-LAST:event_subMenuEliminarClienteActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        Guardar();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void SubMenuItemAsociarCiudadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubMenuItemAsociarCiudadActionPerformed
    try{
        if(TablaClientes.getSelectedRow() != -1){
            JTextFieldPopup ciudad = new JTextFieldPopup("Ingrese la ciudad");
            JTextFieldPopup destino = new JTextFieldPopup("Ingrese el Destino");
            JTextFieldPopup telefono = new JTextFieldPopup("Ingrese el telefono");
            JPanel panel = new JPanel();
            panel.add(ciudad);panel.add(destino);panel.add(telefono);
            String[] options = new String[]{"Cancelar", "Registrar"};
            int opcion = JOptionPane.showOptionDialog(
                    this, 
                    panel, 
                    "Iniciar Sesion",
                    JOptionPane.NO_OPTION, 
                    JOptionPane.PLAIN_MESSAGE,
                    null, 
                    options, 
                    options[1]);
            if(opcion == 1){
                if(conexion.GUARDAR("INSERT INTO ciudad (nombreciudad,direccionciudad,telefonociudad) VALUES ('"+ciudad.getText().trim()+"','"+destino.getText().trim()+"','"+telefono.getText().trim()+"') ")){                    
                    if(conexion.GUARDAR("INSERT INTO ciudadcliente VALUES("+TablaClientes.getValueAt(TablaClientes.getSelectedRow(), 0)+","+modelo.Metodos.getUltimoID("ciudad", "idciudad")+") ")){
                        
                    }
                }
            }            
        }
    }catch(Exception e){M("OCURRIO UN ERROR AL INTENTAR ASOCIAR LAS CIUDADES AL CLIENTE\n"+e,mal);}
    }//GEN-LAST:event_SubMenuItemAsociarCiudadActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        habilitarCajas(true);
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void cjbuscarmarcatransfKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjbuscarmarcatransfKeyReleased
        rowSorter.setRowFilter(RowFilter.regexFilter(cjbuscarmarcatransf.getText().toUpperCase(), 1));
    }//GEN-LAST:event_cjbuscarmarcatransfKeyReleased

    private void btnNuevo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevo1ActionPerformed
        habilitarCajas(false);
    }//GEN-LAST:event_btnNuevo1ActionPerformed

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
            java.util.logging.Logger.getLogger(DialogoRegistrarCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DialogoRegistrarCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DialogoRegistrarCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DialogoRegistrarCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogoRegistrarCliente dialog = new DialogoRegistrarCliente(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem SubMenuItemAsociarCiudad;
    private javax.swing.JPopupMenu SubMenuTabla;
    private javax.swing.JTable TablaClientes;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnNuevo1;
    private javax.swing.JTextField cjCedula;
    private javax.swing.JTextField cjNombre;
    private javax.swing.JTextField cjbuscarmarcatransf;
    private javax.swing.JTextField cjrepresentante;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem subMenuEliminarCliente;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
