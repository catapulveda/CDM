package Dialogos;

import JButtonIntoJTable.BotonEnColumna;
import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import modelo.ConexionBD;
import modelo.CustomTableModel;

/**
 *
 * @author AUXPLANTA
 */
public class DialogoRegistrarHerramienta extends javax.swing.JDialog {

    CustomTableModel model;
    TableRowSorter rowSorter;
    int IDCOLUMN = 5;
    
    modelo.ConexionBD conex = new ConexionBD();
    
    TableColumnAdjuster ajustarColumna;
    
    public DialogoRegistrarHerramienta(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        ajustarColumna = new TableColumnAdjuster(tablaherramientas);
        
        Cargar();
        
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    if(cjherramienta.getText().isEmpty()){
                        modelo.Metodos.M("INGRESE EL NOMBRE DE LA HERRAMIENTA", "advertencia.png");
                        return;
                    }
                    String herramienta  = cjherramienta.getText().toUpperCase().trim();
                    String tipo = cjtipo.getText().toUpperCase().trim();
                    String codigo = cjcodigo.getText().trim();
                    conex.conectar();
                    if(conex.GUARDAR("INSERT INTO herramienta_consorcio "
                            + "(nombreherramienta,tipoherramienta,codigoherramienta) "
                            + "VALUES ( '"+herramienta+"' , '"+tipo+"' , '"+codigo+"' )")){
                        Cargar();
                        cjherramienta.setText(null);cjtipo.setText(null);
                        JOptionPane.showMessageDialog(null, "HERRAMIENTA GUARDA CON EXITO");
                        conex.CERRAR();
                    }
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(null, "ERROR AL GUARDAR\n"+e);
                }
            }
        });
        
        //EVENTO MIENTRAS SE ESCRIBE EN EL CAMPO DE TEXTO PARA BUSCAR LAS HERRAMIENTAS
        cjbuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt){
                rowSorter.setRowFilter(RowFilter.regexFilter(cjbuscar.getText().toUpperCase(), IDCOLUMN));
            }
        });
        
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               try{
                   int fila = tablaherramientas.getSelectedRow();                   
                   if(fila>=0){
                       int id = Integer.parseInt(tablaherramientas.getValueAt(fila, 1).toString());
                       conex.conectar();
                       if(conex.GUARDAR("DELETE FROM herramientaconsorcio WHERE idherramienta='"+id+"' ")){
                           Cargar();
                       }
                   }
               }catch(Exception ex){
                   JOptionPane.showMessageDialog(rootPane, "ERROR\n"+ex);
                   Logger.getLogger(DialogoRegistrarHerramienta.class.getName()).log(Level.SEVERE, null, ex);
               }
            }
        });
        
        tablaherramientas.setDefaultRenderer(JButton.class, new BotonEnColumna());
        
    }
    
    void Cargar(){
        try {            
            String data[][]={};
            String col[]={"ITEM","SELEC.","CANT.","TIPO","CODIGO","HERRAMIENTA"};
            model = new CustomTableModel(
                data, col, tablaherramientas,
                new Class[]{Integer.class,Boolean.class,Integer.class,String.class,String.class,String.class},
                new Boolean[]{false,true,true,true,true,true}
            ){
                @Override
                public boolean isCellEditable(int row, int col){
                    if(col==1)
                        if(Integer.parseInt(model.getValueAt(row, 2).toString())>0)
                            return true;
                    return col==2||col==3|col==4||col==5;                    
                }
            };

            conex.conectar();
            ResultSet rs = conex.CONSULTAR("SELECT * FROM herramientaconsorcio WHERE nombreherramienta<>'' AND nombreherramienta ILIKE '%"+cjbuscar.getText()+"%' ORDER BY nombreherramienta ");
            while(rs.next()){
                model.addRow(new Object[]{
                    rs.getInt("idherramienta"),
                    false,
                    0,
                    rs.getString("tipoherramienta"),
                    rs.getString("codigoherramienta"),
                    rs.getString("nombreherramienta")
                });                               
            }
            rowSorter = new TableRowSorter(model);
            tablaherramientas.setRowSorter(rowSorter);
            ajustarColumna.adjustColumns();
            
            model.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e){
                    if(e.getType() == TableModelEvent.UPDATE){
                        int id = Integer.parseInt(model.getValueAt(e.getFirstRow(), 0).toString());
                        String nombre = model.getValueAt(e.getFirstRow(), 4).toString();
                        String tipo = model.getValueAt(e.getFirstRow(), 2).toString();
                        String codigo = model.getValueAt(e.getFirstRow(), 3).toString();
                        conex.conectar();
                        switch(e.getColumn()){
                            case 1:
                                try {
                                    if(Integer.parseInt(model.getValueAt(e.getFirstRow(), 2).toString())>0){
                                        //{"N°", "ID", "NOMBRE HERRAMIENTA", "CANTIDAD", "CODIGO"};
                                        ( (view.REMISIONESCDM)getOwner()).agregarFila(new Object[]{
                                            "",
                                            model.getValueAt(e.getFirstRow(), 0),//id
                                            model.getValueAt(e.getFirstRow(), 5),
                                            model.getValueAt(e.getFirstRow(), 2),
                                            model.getValueAt(e.getFirstRow(), 3)
                                        });
                                    }else{
                                        modelo.Metodos.M("DIGITE UNA CANTIDAD MAYOR A 0(CERO)", "advertencia.png");                                        
                                    }
                                } catch (NumberFormatException ex) {
                                    modelo.Metodos.M("ERROR EN LA ESCRITURA DEL NUMERO\n"+ex, "error.png");
                                }catch(Exception ex){
                                        modelo.Metodos.M("ERORR DESCONOCIDO\n"+ex, "error.png");
                                }
                                break;
                            case 3:
                                if(conex.GUARDAR("UPDATE herramientaconsorcio SET nombreherramienta='"+nombre+"' WHERE idherramienta='"+id+"' ")){

                                }
                                break;
                            case 4:
                                if(conex.GUARDAR("UPDATE herramientaconsorcio SET tipoherramienta='"+tipo+"' WHERE idherramienta='"+id+"' ")){

                                }
                                break;
                            case 5:
                                if(conex.GUARDAR("UPDATE herramientaconsorcio SET codigoherramienta='"+codigo+"' WHERE idherramienta='"+id+"' ")){
                                    
                                }
                                break;
                            default: break;
                        }
                    }
                }
            });
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR AL CARGAR LA LISTA DE HERRAMIENTAS\n"+ex);
            Logger.getLogger(DialogoRegistrarHerramienta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cjherramienta = new CompuChiqui.JTextFieldPopup();
        cjtipo = new CompuChiqui.JTextFieldPopup();
        jLabel2 = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaherramientas = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        cjbuscar = new CompuChiqui.JTextFieldPopup();
        btnEliminar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cjcodigo = new CompuChiqui.JTextFieldPopup();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel1.setText("Herramienta:");

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel2.setText("Tipo:");

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Guardar.png"))); // NOI18N
        btnGuardar.setText("Guardar");

        tablaherramientas.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        tablaherramientas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablaherramientas);

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel3.setText("Buscar:");

        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/basura.png"))); // NOI18N
        btnEliminar.setText("Eliminar");

        jLabel4.setFont(new java.awt.Font("Enter Sansman", 0, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("HERRAMIENTAS CONSORCIO CDM");

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel5.setText("Codigo:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cjherramienta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cjcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGuardar)
                                .addGap(0, 221, Short.MAX_VALUE))
                            .addComponent(cjtipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cjbuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminar)
                        .addGap(2, 2, 2)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cjherramienta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cjtipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cjcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cjbuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DialogoRegistrarHerramienta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DialogoRegistrarHerramienta dialog = new DialogoRegistrarHerramienta(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private CompuChiqui.JTextFieldPopup cjbuscar;
    private CompuChiqui.JTextFieldPopup cjcodigo;
    private CompuChiqui.JTextFieldPopup cjherramienta;
    private CompuChiqui.JTextFieldPopup cjtipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaherramientas;
    // End of variables declaration//GEN-END:variables
}
