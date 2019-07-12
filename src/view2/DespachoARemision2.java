package view2;

import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.Desktop;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import modelo.Metodos;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DespachoARemision2 extends javax.swing.JFrame {

    TableColumnAdjuster ajustarColumna;

//    CustomTableModel modeloTabla;
    DefaultTableModel modeloTabla;

    TableRowSorter rowSorter;

    private int IDDESPACHO = -1, IDREMISION = -1;

    private boolean ACTUALIZANDO = false;

    ConexionBD conexion = new ConexionBD();

    String SERVICIOS[] = {"REPARACION", "FABRICACION", "RECONSTRUCCION", "MANTENIMIENTO", "DADO DE BAJA", "GARANTIA", "DEVOLUCION", "REVISION", "RECONSTRUIDO"};
    String TIPOS[] = {"CONVENCIONAL", "CONV. - REPOT.", "AUTOPROTEGIDO", "SECO", "PAD MOUNTED", "POTENCIA"};
    String DANOS[] = {"", "CORTOCIRCUITO EN DEVANADO PRIMARIO",
        "CORTOCIRCUITO EN DEVANADO SECUNDARIO", "DISEÑO DEFECTUOSO",
        "FALLA DE AISLAMIENTO", "FALLA POR MANIPULACION",
        "HUMEDAD", "PUNTO CALIENTE EN FASE",
        "SOBRECARGA", "SOBRETENSION DE ORIGEN ARTMOSFERICO O MANIOBRA"};

    public DespachoARemision2() {
        initComponents();

        ajustarColumna = new TableColumnAdjuster(tabla);

        tabla.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                double suma = 0;
                for (int row : tabla.getSelectedRows()) {
                    for (int col : tabla.getSelectedColumns()) {
                        try {
                            suma += Double.parseDouble(tabla.getValueAt(row, col).toString());
                        } catch (java.lang.NumberFormatException | java.lang.NullPointerException ex) {
                            suma += 0;
                        }
                    }
                }
                lblFilasSeleccionadas.setText("Columnas: " + tabla.getSelectedColumnCount() + " Filas: " + tabla.getSelectedRowCount() + " Total filas: " + tabla.getRowCount() + " Suma: " + suma);
                suma = 0;
            }
        });

    }

    public void cargarTabla() {
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
        String[] cols = {"ITEM", "LOTE", "REMISION", "O.P", "No EMPRESA", "No SERIE", "MARCA",
            "FASE", "KVA ENT.", "KVA. SAL.", "TENS. ENT.", "TENS. SAL.", "SERV. ENT.",
            "SERV. SALIDA", "TIPO TRAF. ENT.", "TIPO TRAF. SAL.", "OBSERV. ENT.", "OBSERV. SAL.",
            "AÑO", "PESO", "ACEITE", "CIUDAD", "FECHA DE RECEPCION", "CAUSA DE FALLA"};
        modeloTabla = new DefaultTableModel(new Object[][]{}, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 9 || column == 11 || column == 13 || column == 15 || column == 17 || column == 23;
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
        SQL += (ACTUALIZANDO) ? " t.idremision=" + getIDREMISION() + " " : " t.iddespacho=" + getIDDESPACHO();
        //SQL += " "+((comboServicio.getSelectedIndex()>0)?" AND t.serviciosalida='"+comboServicio.getSelectedItem()+"' ":"")+" ";
        SQL += " ORDER BY lote DESC, fase ASC, kvasalida ASC, marca ASC, item ASC";

        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(SQL);
        try {
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("idtransformador"),
                    rs.getString("lote"),
                    rs.getString("numero_remision"),
                    rs.getString("op"),
                    rs.getString("numeroempresa"),
                    rs.getString("numeroserie"),
                    rs.getString("marca"),
                    rs.getInt("fase"),
                    rs.getDouble("kvaentrada"),
                    rs.getDouble("kvasalida"),
                    rs.getInt("tpe") + "/" + rs.getInt("tse") + "/" + rs.getInt("tte"),
                    rs.getInt("tps") + "/" + rs.getInt("tss") + "/" + rs.getInt("tts"),
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

            tabla.setDefaultRenderer(Object.class, new modelo.ColorDespachoARemision());

            ajustarColumna.adjustColumns();

            lblFilasSeleccionadas.setText("Columnas: " + tabla.getSelectedColumnCount() + " Filas: " + tabla.getSelectedRowCount() + " Total filas: " + tabla.getRowCount());

            modeloTabla.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        try {
                            String val = modeloTabla.getValueAt(e.getFirstRow(), e.getColumn()).toString();
                            String idtransformador = modeloTabla.getValueAt(e.getFirstRow(), 0).toString();
                            String serie = modeloTabla.getValueAt(e.getFirstRow(), 5).toString();

                            if (e.getColumn() == 4) {
                                actualizarSalidas("numeroempresa", val, idtransformador, serie);
                            }

                            if (e.getColumn() == 9) {
                                actualizarSalidas("kvasalida", val, idtransformador, serie);
                            }

                            if (e.getColumn() == 11) {
                                String GUARDAR = "";
                                String t[] = modeloTabla.getValueAt(e.getFirstRow(), 11).toString().split("/");
                                if (t.length == 3) {
                                    if (new ConexionBD().GUARDAR("UPDATE transformador SET tps='" + t[0] + "' , tss='" + t[1] + "' , tts='" + t[2] + "' WHERE idtransformador='" + modeloTabla.getValueAt(e.getFirstRow(), 0) + "' AND iddespacho='" + getIDDESPACHO() + "' AND numeroserie='" + modeloTabla.getValueAt(e.getFirstRow(), 5) + "' ")) {
                                    }
                                } else {
                                    if (new ConexionBD().GUARDAR("UPDATE transformador SET tps='0' , tss='0' , tts='0' WHERE idtransformador='" + modeloTabla.getValueAt(e.getFirstRow(), 0) + "' AND iddespacho='" + getIDDESPACHO() + "' AND numeroserie='" + modeloTabla.getValueAt(e.getFirstRow(), 5) + "' ")) {
                                        JOptionPane.showMessageDialog(null, "EL FORMATO DE LA TENSION DEBE COMPONERSE DE 3 TENSIONES SEPARADAS POR EL SIMBOLO /, RELLENAR CON 0(cero), EN CASO DE TENER LAS TRES.", "TENSION NO VÁLIDA", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/recursos/images/advertencia.png")));
                                        modeloTabla.setValueAt("0/0/0", e.getFirstRow(), 11);
                                    }
                                }
                            }

                            if (e.getColumn() == 13) {
                                actualizarSalidas("serviciosalida", val, idtransformador, serie);
                            }

                            if (e.getColumn() == 15) {
                                actualizarSalidas("tipotrafosalida", val, idtransformador, serie);
                            }

                            if (e.getColumn() == 17) {
                                actualizarSalidas("observacionsalida", val, idtransformador, serie);
                            }

                            if (e.getColumn() == 23) {
                                actualizarSalidas("causadefalla", val, idtransformador, serie);
                            }
                        } catch (NullPointerException ex) {
                        }
                    }
                }
            });

        } catch (SQLException ex) {
            Logger.getLogger(DespachoARemision2.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            conexion.CERRAR();
        }
    }

    public void actualizarSalidas(String col, String val, String idtransformador, String serie) {
        conexion.conectar();
        if (conexion.GUARDAR(" UPDATE transformador SET " + col + "='" + val + "' WHERE iddespacho=" + getIDDESPACHO() + " AND idtransformador=" + idtransformador + " AND numeroserie='" + serie + "' ")) {

        }
    }

    public void cargarServicios() {
        String sql = " SELECT DISTINCT(t.serviciosalida) FROM transformador t WHERE ";
        sql += (isACTUALIZANDO()) ? "t.idremision=" + getIDREMISION() : "t.iddespacho=" + getIDDESPACHO();
        sql += "ORDER BY 1 DESC";
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(sql);
        try {
            if (!isACTUALIZANDO()) {
                comboServicio.addItem("TODOS");
            }
            while (rs.next()) {
                comboServicio.addItem(rs.getString("serviciosalida"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DespachoARemision2.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
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
        btnImprimirRemision = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btnBuscar = new javax.swing.JButton();
        btnRefrescar3 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnDevolver = new javax.swing.JButton();
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

        btnImprimirRemision.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/print.png"))); // NOI18N
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

        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/multiple2.png"))); // NOI18N
        btnBuscar.setToolTipText("Datos para placas");
        btnBuscar.setFocusable(false);
        btnBuscar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBuscar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnBuscar);

        btnRefrescar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/excel2.png"))); // NOI18N
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
        jToolBar1.add(jSeparator3);

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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cjBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjBuscarKeyReleased
        rowSorter.setRowFilter(RowFilter.regexFilter(cjBuscar.getText().toUpperCase()));
    }//GEN-LAST:event_cjBuscarKeyReleased

    private void btnRefrescar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescar3ActionPerformed
        modelo.Metodos.JTableToExcel(tabla, btnRefrescar3);
    }//GEN-LAST:event_btnRefrescar3ActionPerformed

    private void btnDevolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDevolverActionPerformed
        (new Thread() {
            @Override
            public void run() {
                try {
                    btnDevolver.setEnabled(false);
                    btnDevolver.setIcon(modelo.Metodos.getIcon("gif.gif"));
                    int filas[] = tabla.getSelectedRows();
                    for (int i = filas.length - 1; i >= 0; i--) {
                        String sql = " UPDATE transformador SET iddespacho=null, idremision=null, estado='EN PLANTA' ";
                        sql += " WHERE iddespacho=" + getIDDESPACHO() + " AND idtransformador=" + tabla.getValueAt(filas[i], 0) + " AND ";
                        sql += " numeroserie='" + tabla.getValueAt(filas[i], 5) + "' ";
                        if (new ConexionBD().GUARDAR(sql)) {
                            //                    modeloTabla.removeRow(filas[i]);
                        }
                    }
                    cjBuscar.setText("");
                    cargarTabla();
                } catch (Exception e) {
                    Logger.getLogger(DespachoARemision2.class.getName()).log(Level.SEVERE, null, e);
                    Metodos.ERROR(e, "ERROR AL INTENTAR DEVOLVER LOS TRANSFORMADORES SELECCIONADOS A PLANTA.");
                } finally {
                    btnDevolver.setEnabled(true);
                    btnDevolver.setIcon(modelo.Metodos.getIcon("izquierda.png"));
                }
            }
        }).start();
    }//GEN-LAST:event_btnDevolverActionPerformed

    private void comboServicioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboServicioItemStateChanged
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
//            cargarTabla();
            if (comboServicio.getSelectedIndex() == 0) {
                rowSorter.setRowFilter(RowFilter.regexFilter("", 13));
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter(comboServicio.getSelectedItem().toString().toUpperCase(), 13));
            }
        }
    }//GEN-LAST:event_comboServicioItemStateChanged

    private void btnImprimirRemisionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirRemisionActionPerformed
        DialogoImprimirRemisionTrafos1 diag = new DialogoImprimirRemisionTrafos1(this, true);

        if (isACTUALIZANDO()) {

            diag.setIDREMISION(getIDREMISION());
            diag.setACTUALIZANDO(true);
            diag.setVisible(true);
        } else {
            String SERVICIO = comboServicio.getSelectedItem().toString();
            String sql = (comboServicio.getSelectedIndex() > 0)
                    ? "SELECT * FROM remision WHERE iddespacho=" + getIDDESPACHO() + " AND " + (SERVICIO.equals("REPARACION") || SERVICIO.equals("MANTENIMIENTO") ? "(servicio_remision='REPARACION' OR servicio_remision='MANTENIMIENTO')" : "servicio_remision='" + SERVICIO + "'") + " "
                    : "SELECT * FROM remision WHERE iddespacho=" + getIDDESPACHO();
            try {
                conexion.conectar();
                ResultSet rs = conexion.CONSULTAR(sql);
                if (rs.next()) {
                    Metodos.M("YA EXISTE UNA REMISION PARA " + ((comboServicio.getSelectedIndex() > 0) ? "EL SERVICIO (" + comboServicio.getSelectedItem() + ") " : "EL DESPACHO") + " SELECCIONADO", "error.png");
                    return;
                }
            } catch (SQLException ex) {
                Metodos.ERROR(ex, "ERROR AL BUSCAR LA REMISION");
            }

            int SERVICIOS = 0;
            for (int i = 0; i < comboServicio.getItemCount(); i++) {
                SERVICIO = comboServicio.getItemAt(i);
                if (SERVICIO.equals("TODOS")) {
                    continue;
                }
                if (SERVICIO.equals("REPARACION") || SERVICIO.equals("MANTENIMIENTO")) {
                    SERVICIOS++;
                } else {
                    SERVICIOS = 1;
                }
                SERVICIO = (comboServicio.getSelectedIndex() > 0) ? comboServicio.getSelectedItem().toString() : SERVICIO;

//                diag = new DialogoImprimirRemisionTrafos1(this, true);
                diag.setIDDESPACHO(getIDDESPACHO());
                diag.setSERVICIO(SERVICIO);
                diag.setVisible(SERVICIOS == 1);

                if (comboServicio.getSelectedIndex() > 0) {
                    break;
                }
            }
        }

        cargarTabla();
    }//GEN-LAST:event_btnImprimirRemisionActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
//        BuscarEnDespacho buscar = new BuscarEnDespacho(this, false);
//        buscar.setTabla(tabla);
//        buscar.setVisible(true);     
        try {
            XSSFWorkbook MONO = new XSSFWorkbook(new FileInputStream(new File("PLANTILLA PLACAS.xlsx")));
            XSSFWorkbook TRIFA = new XSSFWorkbook(new FileInputStream(new File("PLANTILLA PLACAS.xlsx")));

            XSSFSheet hojaMONO = MONO.getSheetAt(0);
            XSSFSheet hojaTRIF = TRIFA.getSheetAt(0);

            int celdas[] = {24, 25, 26, 27, 28};

            String sql = "SELECT cl.nombrecliente, e.contrato, e.lote, t.numeroempresa, t.numeroserie, t.fase, t.kvasalida, t.tps, t.tss, t.tts,\n"
                    + "t.serviciosalida, t.ano, t.peso, t.aceite, t.tipotrafosalida, pt.codigo, pt.conmutador, pt.vcc, pt.promedioi, pt.derivacionprimaria, pt.i1, pt.i2, \n"
                    + "pt.fechalaboratorio, pt.frecuencia, pt.refrigeracion, pt.materialconductoralta, pt.liquidoaislante, pt.tensionserie, pt.nba, "
                    + "pt.claseaislamiento, pt.liquidoaislante, pt.materialconductorbaja, pt.grupodeconexion, pt.derivacionprimaria, pt.fechaderegistro, \n"
                    + "(pt.vcc/t.tps)*100 as uz, ((cast((1/(pt.impedanciagarantizada/100)) as numeric)*pt.i2)/1000) as icc, \n"
                    + "(1250/(POWER((1/(pt.impedanciagarantizada/100)), 2))) as tcc,\n"
                    + "CASE \n"
                    + "	WHEN pt.grupodeconexion='Ii0' THEN (SELECT 'U'::text) \n"
                    + "	ELSE \n"
                    + "		CASE WHEN pt.grupodeconexion='Ii6' THEN (SELECT 'X'::text) \n"
                    + "		END\n"
                    + "END as U,\n"
                    + "CASE \n"
                    + "	WHEN pt.grupodeconexion='Ii0' THEN (SELECT 'X'::text) \n"
                    + "	ELSE \n"
                    + "		CASE WHEN pt.grupodeconexion='Ii6' THEN (SELECT 'U'::text) \n"
                    + "		END\n"
                    + "END as X FROM entrada e "
                    + "INNER JOIN cliente cl ON cl.idcliente=e.idcliente\n"
                    + "INNER JOIN transformador t USING(identrada)\n"
                    + "LEFT JOIN protocolos pt USING(idtransformador)\n"
                    + "LEFT JOIN remision r USING(idremision)\n"
                    + "INNER JOIN ciudad c USING(idciudad) WHERE\n"
                    + " t.iddespacho=" + getIDDESPACHO() + " ORDER BY fase ASC, e.identrada ASC, kvasalida ASC, marca ASC, item ASC";

            conexion.conectar();
            ResultSet rs = conexion.CONSULTAR(sql);
            int row1 = 1, row2 = 1;
            while (rs.next()) {
                if (!rs.getString("serviciosalida").equals("DADO DE BAJA")) {
                    XSSFRow r = null;
                    if (rs.getInt("fase") == 1) {
                        r = hojaMONO.createRow(row1);
                        row1++;
                    } else if (rs.getInt("fase") == 3) {
                        r = hojaTRIF.createRow(row2);
                        row2++;
                    }

                    r.createCell(0, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("numeroempresa"));
                    r.createCell(1, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("numeroserie"));
                    if (rs.getString("codigo") != null) {
                        r.createCell(2, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(rs.getDate("fechalaboratorio").toLocalDate().getYear());
                        r.createCell(3, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(rs.getDate("fechalaboratorio").toLocalDate().getMonthValue());
                        r.createCell(4, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(rs.getString("tipotrafosalida"));
                        r.createCell(5, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(rs.getString("kvasalida"));
                        r.createCell(6, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(rs.getInt("frecuencia"));
                        r.createCell(7, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(rs.getInt("tps"));
                        r.createCell(8, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(rs.getInt("tss"));
                        r.createCell(9, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("refrigeracion"));
                        r.createCell(10, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(rs.getDouble("i1"));
                        r.createCell(11, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(rs.getDouble("i2"));
                        r.createCell(12, XSSFCell.CELL_TYPE_STRING).setCellValue((rs.getString("materialconductoralta").equals("COBRE") ? "Cu" : "Al") + "-" + (rs.getString("materialconductorbaja").equals("COBRE") ? "Cu" : "Al"));
                        r.createCell(13, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(Math.round(rs.getDouble("icc") * 100d) / 100d);
                        r.createCell(14, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(Math.round(rs.getDouble("tcc") * 100d) / 100d);
                        r.createCell(15, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("aceite"));
                        r.createCell(16, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(Math.round(rs.getDouble("uz") * 100d) / 100d);
                        r.createCell(17, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("tensionserie"));
                        r.createCell(18, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(rs.getInt("peso"));
                        r.createCell(19, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(rs.getDouble("promedioi"));
                        r.createCell(20, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("nba"));
                        r.createCell(21, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("claseaislamiento"));
                        r.createCell(22, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("liquidoaislante"));
                        r.createCell(23, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("grupodeconexion"));

                        int pos = rs.getInt("conmutador");
                        int veces_atras = (pos - 1);
                        double factor = 1.0;
                        if (veces_atras > 0) {
                            factor = factor + (veces_atras * 2.5 / 100);
                        }

                        for (int i = 0; i < celdas.length; i++) {
                            r.createCell(celdas[i], XSSFCell.CELL_TYPE_NUMERIC).setCellValue(Math.round(rs.getInt("tps") * factor) + (((i + 1) == pos) ? "   X" : ""));
                            factor -= 0.025;
                        }

                        r.createCell(29, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("derivacionprimaria"));
                        r.createCell(30, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("lote"));
                        r.createCell(31, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("nombrecliente"));
                        r.createCell(32, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("contrato"));
                        r.createCell(33, XSSFCell.CELL_TYPE_STRING).setCellValue((rs.getString("serviciosalida").equals("REPARACION")) ? "REPARADO" : rs.getString("serviciosalida"));
                        r.createCell(34, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("u"));
                        r.createCell(35, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("x"));
                        r.createCell(36, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString("codigo"));
                    }
                }
            }

            for (int i = 0; i < MONO.getSheetAt(0).getRow(0).getLastCellNum(); i++) {
                MONO.getSheetAt(0).autoSizeColumn(i);
                TRIFA.getSheetAt(0).autoSizeColumn(i);
            }

            try {
                File f1 = File.createTempFile("MONOFASICOS", ".xlsx");
                File f2 = File.createTempFile("TRIFASICOS", ".xlsx");

                MONO.write(new FileOutputStream(f1));
                TRIFA.write(new FileOutputStream(f2));

                Desktop.getDesktop().open(f1);
                Desktop.getDesktop().open(f2);

                System.gc();
            } catch (IOException ex) {
                Logger.getLogger(DespachoARemision2.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception e) {
            modelo.Metodos.M(e.getMessage(), "error.png");
        }
    }//GEN-LAST:event_btnBuscarActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DespachoARemision2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> {
            new DespachoARemision2().setVisible(true);
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
