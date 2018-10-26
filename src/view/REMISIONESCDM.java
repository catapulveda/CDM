package view;

import CopyPasteJTable.ExcelAdapter;
import Dialogos.DialogoConfigurarConsecutivosRemision;
import Dialogos.DialogoRegistrarHerramienta;
import JButtonIntoJTable.BotonEnColumna;
import JTableAutoResizeColumn.TableColumnAdjuster;
import com.mxrck.autocompleter.TextAutoCompleter;
import static java.awt.Frame.MAXIMIZED_VERT;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import modelo.Ciudad;
import modelo.Cliente;
import modelo.Conductor;
import modelo.ConexionBD;
import modelo.HerramientaConsorcio;
import modelo.Metodos;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public final class REMISIONESCDM extends javax.swing.JFrame {

    private final String ruta = System.getProperties().getProperty("user.dir");
    private DefaultTableModel modeloHerramientas;
    private boolean ACTUALIZANDO = false;
    private int IDREMISION = -1;
    public String NUMERO_REMISION = null;

    private String TIPO = "", CONSECUTIVO_EMPRESA = null;
    private String REPORTE = null;

    TableColumnAdjuster ajustarColumna;

    ConexionBD conexion = new ConexionBD();

    private TextAutoCompleter cjHerramienta;
    private JTextComponent cjTablaHerramientas = new JTextField();

    TableRowSorter rowSorter;

    ArrayList<Integer> lista = new ArrayList();
    
    modelo.Sesion sesion = modelo.Sesion.getConfigurador(null, -1);

    public REMISIONESCDM() {
        initComponents();

        ajustarColumna = new TableColumnAdjuster(tablaHerramientas);

        Enter(cjciudad, cjdestino);
        Enter(cjdestino, cjtelefono);
        Enter(cjtelefono, cjcontrato);
        Enter(cjcontrato, cjcentrodecostos);
        Enter(cjcentrodecostos, cjconductor);
        Enter(cjconductor, cjplaca);
        Enter(cjplaca, cjfactura);
        Enter(cjfactura, cjcedula);

        cjfecha.setDate(new Date());
        
        modelo.Cliente.cargarComboNombreClientes(comboCliente);
        comboCliente.setUI(JComboBoxColor.JComboBoxColor.createUI(comboCliente));
    }

    public void AbrirEncabezadoRemision(ResultSet rs) {
        try {
//            setIDREMISION(idremision);
//            conexion.conectar();
//            ResultSet rs = conexion.CONSULTAR("SELECT * FROM remision WHERE idremision='" + idremision + "' ");
//            if (rs.next()) {
                NUMERO_REMISION = rs.getString("numero_remision");
                setTitle("REMISION N° " + NUMERO_REMISION);
                comboCliente.getModel().setSelectedItem(new Cliente(rs.getInt("idcliente"), rs.getString("nombrecliente"), rs.getString("nitcliente")));
                cjresponsable.setText(rs.getString("responsable"));
                //cjcliente.setText(rs.getString("cliente_remision"));
                cjciudad.setText(rs.getString("ciudad_remision"));
                cjdestino.setText(rs.getString("destino_remision"));
                cjtelefono.setText(rs.getString("telefono_remision"));
                cjconductor.setText(rs.getString("conductor_remision"));
                cjcedula.setText(rs.getString("cedula_remision"));
                cjplaca.setText(rs.getString("placa_remision"));
                cjfactura.setText(rs.getString("factura_numero"));
                cjcontrato.setText(rs.getString("contrato_remision"));
                cjcentrodecostos.setText(rs.getString("centrodecostos_remision"));
                comboempresa.setSelectedItem(rs.getString("empresa_remision"));
                cjnoremision.setText(rs.getString("numero_remision"));
                areadeTexto.setText(rs.getString("descripcion_remision"));
                cjfecha.setDate(rs.getDate("fecha_remision"));
                cjnitcedulacliente.setText(rs.getString("nitcliente"));
                cjempresatransportadora.setText(rs.getString("empresatransportadora"));
//            }
        } catch (Exception e) {
            Logger.getLogger(REMISIONESCDM.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, "ERROR AL ABRIR LA REMISION\n" + e + "\n");
        } finally {
//            conexion.CERRAR();
        }
    }

    void mostrarConsecutivoActual() {
        cjnoremision.setText(String.valueOf(modelo.Metodos.getConsecutivoRemision(CONSECUTIVO_EMPRESA, false)));
    }

    void cargarListaHerramientas() {
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR("SELECT * FROM herramientaconsorcio");
        ArrayList<Object> herramientas = new ArrayList<>();
        try {
            while (rs.next()) {
                herramientas.add(new HerramientaConsorcio(
                        rs.getInt("idherramienta"),
                        rs.getString("nombreherramienta"),
                        rs.getString("codigoherramienta")
                ));
            }
            cjHerramienta = new TextAutoCompleter(cjTablaHerramientas, herramientas);
            cjHerramienta.setCaseSensitive(true);
            cjHerramienta.setClearOnIncorrect(true);
        } catch (SQLException ex) {
            Logger.getLogger(REMISIONESCDM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cargarTablaHerramientas() {
        
        String data[][] = {};
        String columnas[] = {"N°", "ID", "NOMBRE HERRAMIENTA", "CANTIDAD", "CODIGO"};
        modeloHerramientas = new DefaultTableModel(data, columnas) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return JButton.class;
                }
                if (columnIndex == 1 || columnIndex == 3) {
                    return Integer.class;
                }
                if (columnIndex == 5) {
                    return Boolean.class;
                }
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0 || column == 1 || column == 4) {
                    return false;
                }
                return true;
            }
        };

        tablaHerramientas.setModel(modeloHerramientas);

        modeloHerramientas.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    try {
                        int fila = e.getFirstRow();
                        int cantidad = (int) modeloHerramientas.getValueAt(fila, 3);
                        int idherramienta = (int) modeloHerramientas.getValueAt(fila, 1);
                        if (lista.contains(idherramienta)) {
                            if (getIDREMISION() > 0) {
                                if (conexion.GUARDAR("UPDATE datosremision_consorcio SET cantidad=" + cantidad + " WHERE idherramienta=" + idherramienta + " AND idremision=" + getIDREMISION() + " ")) {

                                }
                            }
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        });

        rowSorter = new TableRowSorter(modeloHerramientas);
        tablaHerramientas.setRowSorter(rowSorter);

        tablaHerramientas.setDefaultRenderer(Object.class, new ColorRowJTable.ColorRowInJTable());
        tablaHerramientas.setDefaultRenderer(Integer.class, new ColorRowJTable.ColorRowInJTable());
        tablaHerramientas.setDefaultRenderer(Boolean.class, new ColorRowJTable.ColorRowInJTable());

        cargarListaHerramientas();
        cjTablaHerramientas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                if (evt.getKeyChar() == 10) {
                    ajustarColumna.adjustColumns();
                    HerramientaConsorcio herr = (HerramientaConsorcio) cjHerramienta.getItemSelected();
                    int fila = tablaHerramientas.getSelectedRow();
                    tablaHerramientas.setValueAt(herr.getIdHerramienta(), fila, 1);
                    tablaHerramientas.setValueAt(herr.getCodigoHerramienta(), fila, 4);
                    tablaHerramientas.setValueAt(1, fila, 3);
                }
            }
        });
        tablaHerramientas.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor((JTextField) cjTablaHerramientas));

        tablaHerramientas.setDefaultRenderer(JButton.class, new BotonEnColumna());
        tablaHerramientas.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablaHerramientas.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tablaHerramientas.setCellSelectionEnabled(true);

        ExcelAdapter copypaste = new ExcelAdapter(tablaHerramientas);

        ajustarColumna.adjustColumns();
        comboempresa.setSelectedItem("CONSORCIO");
    }

    public void agregarFila(Object[] obj){
        modeloHerramientas.addRow(obj);
        ajustarColumna.adjustColumns();
    }
    
    public void cargarResultadoHerramientas() {
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR("SELECT * FROM datosremision_consorcio d\n"
                + " RIGHT JOIN herramientaconsorcio h USING(idherramienta)\n"
                + " WHERE d.idremision=" + getIDREMISION() + " ORDER BY nombreherramienta");
        try {
            while (rs.next()) {
                lista.add(rs.getInt("idherramienta"));
                modeloHerramientas.addRow(new Object[]{
                    rs.getRow(),
                    rs.getString("idherramienta"),
                    rs.getString("nombreherramienta"),
                    rs.getString("cantidad"),
                    rs.getString("codigoherramienta")
                });
            }
            ajustarColumna.adjustColumns();
        } catch (SQLException ex) {
            Logger.getLogger(REMISIONESCDM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    Object getT(int row, int col) {
        return tablaHerramientas.getValueAt(row, col);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        SubMenuTexto = new javax.swing.JPopupMenu();
        SubMenuConvertirAMayusculas = new javax.swing.JMenuItem();
        SubMenuConvertirMinusculas = new javax.swing.JMenuItem();
        SubMenuCopiar = new javax.swing.JMenuItem();
        SubMenuPegar = new javax.swing.JMenuItem();
        SubMenuCortar = new javax.swing.JMenuItem();
        SubMenuSeleecionarTodo = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btn_generar = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btn_guardar = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jLabel8 = new javax.swing.JLabel();
        cjnoremision = new javax.swing.JTextField();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        checkremisionnueva = new javax.swing.JCheckBox();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        contenedor = new javax.swing.JTabbedPane();
        paneldatosremision = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        areadeTexto = new CompuChiqui.JCTextArea();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaHerramientas = new javax.swing.JTable();
        jToolBar2 = new javax.swing.JToolBar();
        jButton2 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnAgregarFila = new javax.swing.JButton();
        jToolBar3 = new javax.swing.JToolBar();
        jLabel2 = new javax.swing.JLabel();
        cjBuscarNombreHerramienta = new CompuChiqui.JTextFieldPopup();
        btnBuscarNombreHerramienta = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        comboempresa = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        btnAgregarCliente = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        cjciudad = new javax.swing.JTextField();
        btnAgregarConductor1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        cjdestino = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cjtelefono = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        cjconductor = new javax.swing.JTextField();
        btnAgregarConductor = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        cjcedula = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        cjplaca = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cjfactura = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cjcontrato = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cjcentrodecostos = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cjfecha = new com.toedter.calendar.JDateChooser();
        jLabel16 = new javax.swing.JLabel();
        cjempresatransportadora = new javax.swing.JTextField();
        cjnitcedulacliente = new CompuChiqui.JTextFieldPopup();
        jLabel17 = new javax.swing.JLabel();
        comboCliente = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        cjresponsable = new CompuChiqui.JTextFieldPopup();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();

        SubMenuConvertirAMayusculas.setText("Convertir a Mayusculas");
        SubMenuConvertirAMayusculas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                SubMenuConvertirAMayusculasMouseEntered(evt);
            }
        });
        SubMenuConvertirAMayusculas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubMenuConvertirAMayusculasActionPerformed(evt);
            }
        });
        SubMenuTexto.add(SubMenuConvertirAMayusculas);

        SubMenuConvertirMinusculas.setText("Convertir a Minusculas");
        SubMenuConvertirMinusculas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubMenuConvertirMinusculasActionPerformed(evt);
            }
        });
        SubMenuTexto.add(SubMenuConvertirMinusculas);

        SubMenuCopiar.setText("Copiar");
        SubMenuCopiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubMenuCopiarActionPerformed(evt);
            }
        });
        SubMenuTexto.add(SubMenuCopiar);

        SubMenuPegar.setText("Pegar");
        SubMenuPegar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubMenuPegarActionPerformed(evt);
            }
        });
        SubMenuTexto.add(SubMenuPegar);

        SubMenuCortar.setText("Cortar");
        SubMenuCortar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubMenuCortarActionPerformed(evt);
            }
        });
        SubMenuTexto.add(SubMenuCortar);

        SubMenuSeleecionarTodo.setText("Seleccionar Todo");
        SubMenuSeleecionarTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubMenuSeleecionarTodoActionPerformed(evt);
            }
        });
        SubMenuTexto.add(SubMenuSeleecionarTodo);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.add(jSeparator2);

        btn_generar.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btn_generar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/reporte2.png"))); // NOI18N
        btn_generar.setText("Generar");
        btn_generar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_generarActionPerformed(evt);
            }
        });
        jToolBar1.add(btn_generar);
        jToolBar1.add(jSeparator3);

        btn_guardar.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btn_guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Guardar.png"))); // NOI18N
        btn_guardar.setText("Guardar");
        btn_guardar.setFocusable(false);
        btn_guardar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btn_guardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_guardarActionPerformed(evt);
            }
        });
        jToolBar1.add(btn_guardar);
        jToolBar1.add(jSeparator6);

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel8.setText("Remision N°:");
        jToolBar1.add(jLabel8);

        cjnoremision.setEditable(false);
        cjnoremision.setBackground(new java.awt.Color(255, 255, 255));
        cjnoremision.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        cjnoremision.setForeground(new java.awt.Color(255, 0, 0));
        cjnoremision.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jToolBar1.add(cjnoremision);
        jToolBar1.add(jSeparator4);

        checkremisionnueva.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        checkremisionnueva.setText("REMISION NUEVA");
        checkremisionnueva.setFocusable(false);
        checkremisionnueva.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        checkremisionnueva.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        checkremisionnueva.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkremisionnuevaItemStateChanged(evt);
            }
        });
        checkremisionnueva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkremisionnuevaActionPerformed(evt);
            }
        });
        jToolBar1.add(checkremisionnueva);
        jToolBar1.add(jSeparator5);

        paneldatosremision.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        paneldatosremision.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        paneldatosremision.setLayout(new java.awt.CardLayout());

        areadeTexto.setColumns(20);
        areadeTexto.setRows(5);
        areadeTexto.setComponentPopupMenu(SubMenuTexto);
        areadeTexto.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jScrollPane1.setViewportView(areadeTexto);

        paneldatosremision.add(jScrollPane1, "card2");

        contenedor.addTab("Descripcion", paneldatosremision);

        tablaHerramientas.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tablaHerramientas.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaHerramientas.setRowHeight(25);
        tablaHerramientas.getTableHeader().setReorderingAllowed(false);
        tablaHerramientas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tablaHerramientasKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(tablaHerramientas);

        jToolBar2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar2.setFloatable(false);
        jToolBar2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton2);
        jToolBar2.add(jSeparator1);

        btnAgregarFila.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/add.png"))); // NOI18N
        btnAgregarFila.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAgregarFila.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarFilaActionPerformed(evt);
            }
        });
        jToolBar2.add(btnAgregarFila);

        jToolBar3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar3.setFloatable(false);

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel2.setText("Buscar:");
        jToolBar3.add(jLabel2);

        cjBuscarNombreHerramienta.setPlaceholder("Ingrese el nombre de la herramienta");
        cjBuscarNombreHerramienta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjBuscarNombreHerramientaKeyReleased(evt);
            }
        });
        jToolBar3.add(cjBuscarNombreHerramienta);

        btnBuscarNombreHerramienta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/diferencias.png"))); // NOI18N
        btnBuscarNombreHerramienta.setFocusable(false);
        btnBuscarNombreHerramienta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBuscarNombreHerramienta.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(btnBuscarNombreHerramienta);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        contenedor.addTab("Herramientas", jPanel2);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos Cliente:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 1, 10))); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel9.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("EMPRESA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel9, gridBagConstraints);

        comboempresa.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CDM", "MEDIDORES", "CONSORCIO" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(comboempresa, gridBagConstraints);

        jLabel15.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("CLIENTE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel15, gridBagConstraints);

        btnAgregarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/add.png"))); // NOI18N
        btnAgregarCliente.setToolTipText("Actualizar consecutivo");
        btnAgregarCliente.setEnabled(false);
        btnAgregarCliente.setFocusable(false);
        btnAgregarCliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAgregarCliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAgregarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarClienteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(btnAgregarCliente, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("CIUDAD:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel6, gridBagConstraints);

        cjciudad.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cjciudad, gridBagConstraints);

        btnAgregarConductor1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/add.png"))); // NOI18N
        btnAgregarConductor1.setToolTipText("Actualizar consecutivo");
        btnAgregarConductor1.setFocusable(false);
        btnAgregarConductor1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAgregarConductor1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAgregarConductor1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarConductor1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(btnAgregarConductor1, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("DESTINO:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel4, gridBagConstraints);

        cjdestino.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cjdestino, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("TELEFONO:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel5, gridBagConstraints);

        cjtelefono.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cjtelefono, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("CONDUCTOR:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel12, gridBagConstraints);

        cjconductor.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cjconductor, gridBagConstraints);

        btnAgregarConductor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/add.png"))); // NOI18N
        btnAgregarConductor.setToolTipText("Actualizar consecutivo");
        btnAgregarConductor.setFocusable(false);
        btnAgregarConductor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAgregarConductor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAgregarConductor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarConductorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(btnAgregarConductor, gridBagConstraints);

        jLabel13.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("CEDULA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel13, gridBagConstraints);

        cjcedula.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cjcedula, gridBagConstraints);

        jLabel14.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("PLACA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel14, gridBagConstraints);

        cjplaca.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cjplaca, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("FACTURA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel11, gridBagConstraints);

        cjfactura.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cjfactura, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("CONTRATO:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel10, gridBagConstraints);

        cjcontrato.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cjcontrato, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("C. DE COSTOS:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel3, gridBagConstraints);

        cjcentrodecostos.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cjcentrodecostos, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("FECHA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cjfecha, gridBagConstraints);

        jLabel16.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("EMPRESA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel16, gridBagConstraints);

        cjempresatransportadora.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cjempresatransportadora, gridBagConstraints);

        cjnitcedulacliente.setPlaceholder("NIT / CEDULA");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cjnitcedulacliente, gridBagConstraints);

        jLabel17.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("RES`PONSABLE:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel17, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(comboCliente, gridBagConstraints);

        jLabel18.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("NIT / CEDULA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel18, gridBagConstraints);

        cjresponsable.setPlaceholder("Responsable / Contacto");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cjresponsable, gridBagConstraints);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        contenedor.addTab("Encabezado", jPanel1);

        jMenu1.setText("Archivo");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Configuracion");

        jMenuItem3.setText("Consecutivos de remision");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contenedor)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contenedor)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_generarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_generarActionPerformed
//        (new Thread(){
//            public void run(){
        try {
            btn_generar.setIcon(new ImageIcon(getClass().getResource("/recursos/images/gif.gif")));
            btn_generar.setEnabled(false);
            Map<String, Object> p = new HashMap<>();
            JasperPrint reporte_view;
            JasperReport reporte;          
            reporte = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/" + getREPORTE() + ".jasper").toString()));
            p.put("IDREMISION", IDREMISION);
            reporte_view = JasperFillManager.fillReport(reporte, p, conexion.conectar());
            JasperViewer jviewer = new JasperViewer(reporte_view, false);
            jviewer.setTitle(getTIPO());
            jviewer.setExtendedState(MAXIMIZED_VERT);
            jviewer.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERROR AL GENERAR LA REMISION INTENTE NUEVAMENTE O CONTACTE CON EL ADMNINISTRADOR DEL SISTEMA\nDETALLES DEL ERROR:\n" + e);
            java.util.logging.Logger.getLogger(REMISIONESCDM.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
        } finally {
            btn_generar.setIcon(new ImageIcon(getClass().getResource("/recursos/images/reporte2.png")));
            btn_generar.setEnabled(true);
        }
//            }
//        }).start();
    }//GEN-LAST:event_btn_generarActionPerformed

    private void btn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_guardarActionPerformed
        try {
//            boolean CONTINUAR = false;
            String pregunta = (!isACTUALIZANDO()) ? "SE GUARDARÁ LA REMISION " + cjnoremision.getText() + " " + TIPO + " DE " + comboempresa.getSelectedItem() + " AL CLIENTE " + comboCliente.getSelectedItem().toString() + ", DESEA CONTINUAR ? " : "Desea Actualizar la Remision ?";
            if (checkremisionnueva.isSelected()) {
                pregunta = "HAZ SELECCIONADO LA OPCION NUEVA REMISION, POR LO TANTO SE REGISTRARÁ UNA NUEVA REMISION CON UN NUEVO CONSECUTIVO.";
                setACTUALIZANDO(false);
            } else {
                pregunta = (isACTUALIZANDO()) ? " Desea continuar ACTUALIZANDO la remision ?" : " Desea continuar REGISTRANDO la remision ";
            }
//                if(JOptionPane.showConfirmDialog(this, pregunta, "CONFIRMACION", 0, JOptionPane.QUESTION_MESSAGE, new ImageIcon(getClass().getResource("/recursos/images/advertencia.png")))==JOptionPane.YES_OPTION){
            String GUARDA_REMISION = null;
            if (isACTUALIZANDO()) {
                GUARDA_REMISION = " UPDATE remision SET ";
                GUARDA_REMISION += " cliente_remision='" + comboCliente.getSelectedItem().toString() + "' , ";
                GUARDA_REMISION += " idcliente="+((Cliente)comboCliente.getModel().getSelectedItem()).getIdCliente()+" , ";
                GUARDA_REMISION += " ciudad_remision='" + cjciudad.getText() + "' , destino_remision='" + cjdestino.getText() + "' , ";
                GUARDA_REMISION += " telefono_remision='" + cjtelefono.getText() + "' , contrato_remision='" + cjcontrato.getText() + "' , ";
                GUARDA_REMISION += " centrodecostos_remision='" + cjcentrodecostos.getText() + "' , conductor_remision='" + cjconductor.getText() + "' , ";
                GUARDA_REMISION += " cedula_remision='" + cjcedula.getText() + "' , placa_remision='" + cjplaca.getText() + "' , ";
                GUARDA_REMISION += " fecha_remision='" + cjfecha.getDate() + "' , ";
                GUARDA_REMISION += " tipo_remision='" + TIPO + "' , ";
                GUARDA_REMISION += " descripcion_remision='" + areadeTexto.getText() + "' , ";
                GUARDA_REMISION += " factura_numero='" + cjfactura.getText() + "' , ";
                GUARDA_REMISION += " empresa_remision='"+comboempresa.getSelectedItem()+"' , ";
                GUARDA_REMISION += " idusuario="+sesion.getIdUsuario()+" , ";
                GUARDA_REMISION += " nitcliente='"+cjnitcedulacliente.getText().trim()+"' , ";
                GUARDA_REMISION += " empresatransportadora='"+cjempresatransportadora.getText().trim()+"' , ";
                GUARDA_REMISION += " responsable='"+cjresponsable.getText().trim()+"'  ";
                GUARDA_REMISION += " WHERE idremision='" + getIDREMISION() + "' ";
            } else {
                GUARDA_REMISION = " INSERT INTO remision ( numero_remision , cliente_remision , ";
                GUARDA_REMISION += "ciudad_remision , destino_remision , telefono_remision , ";
                GUARDA_REMISION += " contrato_remision , centrodecostos_remision , conductor_remision , ";
                GUARDA_REMISION += " cedula_remision , placa_remision,  ";
                GUARDA_REMISION += " fecha_remision, tipo_remision, descripcion_remision, factura_numero, ";
                GUARDA_REMISION += " empresa_remision, estado, fechacreacion, idusuario , nitcliente, ";
                GUARDA_REMISION += " empresatransportadora, idcliente, responsable, iddespacho ) VALUES ( ";
                GUARDA_REMISION += " '" + cjnoremision.getText() + "' , '" + comboCliente.getSelectedItem().toString() + "' , ";
                GUARDA_REMISION += " '" + cjciudad.getText() + "' , '" + cjdestino.getText() + "' , ";
                GUARDA_REMISION += " '" + cjtelefono.getText() + "' , '" + cjcontrato.getText() + "' , ";
                GUARDA_REMISION += " '" + cjcentrodecostos.getText() + "' , '" + cjconductor.getText() + "' , ";
                GUARDA_REMISION += " '" + cjcedula.getText() + "' , '" + cjplaca.getText() + "' , ";
                GUARDA_REMISION += " '" + new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(cjfecha.getDate()) + "' , ";
                GUARDA_REMISION += "'" + TIPO + "' , ";
                GUARDA_REMISION += " '" + areadeTexto.getText() + "' , ";
                GUARDA_REMISION += " '" + cjfactura.getText() + "' , ";
                GUARDA_REMISION += " '" + comboempresa.getSelectedItem() + "' , ";
                GUARDA_REMISION += " 'TRUE' , '" + new java.util.Date() + "' , ";
                GUARDA_REMISION += " "+sesion.getIdUsuario()+" , ";
                GUARDA_REMISION += " '"+cjnitcedulacliente.getText().trim()+"' , ";
                GUARDA_REMISION += " '"+cjempresatransportadora.getText().trim()+"' ,  ";
                GUARDA_REMISION += " "+((Cliente)comboCliente.getModel().getSelectedItem()).getIdCliente()+" , ";
                GUARDA_REMISION += " '"+cjresponsable.getText().trim()+"' , ";
                GUARDA_REMISION += " 0 ";
                GUARDA_REMISION += " ) ";
            }

            if (conexion.GUARDAR(GUARDA_REMISION)){
                if (checkremisionnueva.isSelected()){
                    checkremisionnueva.setSelected(false);
                    lista.clear();
                }
                if (isACTUALIZANDO()) {
//                            JOptionPane.showMessageDialog(this, "LA REMISION SE HA ACTUALIZADO CON ÉXITO.", "REMISION ACTUALIZADA", JOptionPane.OK_OPTION, new ImageIcon(getClass().getResource("/recursos/images/bien.png")));
                } else {
                    NUMERO_REMISION = cjnoremision.getText();
                    modelo.Metodos.getConsecutivoRemision(CONSECUTIVO_EMPRESA, true);
//                            JOptionPane.showMessageDialog(this, "LA REMISION SE HA REGISTRADO CON ÉXITO.", "REMISION GUARDADA", JOptionPane.OK_OPTION, new ImageIcon(getClass().getResource("/recursos/images/bien.png")));
                    IDREMISION = (isACTUALIZANDO()) ? getIDREMISION() : modelo.Metodos.getUltimoID("remision", "idremision");
                    setACTUALIZANDO(true);
                }

                if (comboempresa.getSelectedItem().equals("CONSORCIO") && TIPO.equals("CON RETORNO")) {
                    String GUARDA_DATOS = " INSERT INTO datosremision_consorcio VALUES \n";
                    boolean GUARDADATOS = false;
                    for (int i = 0; i < tablaHerramientas.getRowCount(); i++) {
                        System.out.println("RECORRIENDO TABLA");
                        tablaHerramientas.scrollRectToVisible(tablaHerramientas.getCellRect(i, 0, true));
                        tablaHerramientas.setRowSelectionInterval(i, i);
                        Object datos[] = {getT(i, 1), getT(i, 2), getT(i, 3)};
                        try{
                            if(!lista.contains(Integer.parseInt(datos[0].toString()))){
                                System.out.println(" !lista.isEmpty() && !lista.contains(Integer.parseInt(datos[0].toString()) ");
                                GUARDADATOS = true;
                                GUARDA_DATOS += "  ('" + (i + 1) + "' , '" + getIDREMISION() + "'  , '" + datos[0] + "' , '" + datos[2] + "') ,\n";
                                lista.add(Integer.parseInt(datos[0].toString()));
                            }else{
                                System.out.println("NADA");
                            }
                        }catch(NullPointerException e){
                        }
                    }
                    //GUARDA LOS DATOS DE LAS HERRAMIENTAS ASOCIADAS A LA REMISION
                    GUARDA_DATOS = GUARDA_DATOS.substring(0, GUARDA_DATOS.length() - 2);
                    if (GUARDADATOS && conexion.GUARDAR(GUARDA_DATOS)) {                        
                    }
                    if(!GUARDADATOS){
                        cargarTablaHerramientas();
                    }
                }
                btn_generar.doClick();
            } else {
                modelo.Metodos.M("NO SE PUDO GUARDAR LA REMISION.", "advertencia.png");
            }
//                }//PREGUNTA SI DESEA GUARDAR        
        } catch (Exception ex) {
            modelo.Metodos.ERROR(ex, "ERROR");
            Logger.getLogger(REMISIONESCDM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_guardarActionPerformed

    private void SubMenuConvertirAMayusculasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubMenuConvertirAMayusculasActionPerformed
        try {
            if (!areadeTexto.getSelectedText().isEmpty()) {
                areadeTexto.replaceSelection(areadeTexto.getSelectedText().toUpperCase());
            }
        } catch(Exception e){
            modelo.Metodos.M("ERROR AL CONVERTIR A MAYUSCULAS: " + e, "error.png");
        }
    }//GEN-LAST:event_SubMenuConvertirAMayusculasActionPerformed

    private void SubMenuConvertirAMayusculasMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SubMenuConvertirAMayusculasMouseEntered
        this.SubMenuConvertirAMayusculas.setEnabled((null != areadeTexto.getSelectedText()));
    }//GEN-LAST:event_SubMenuConvertirAMayusculasMouseEntered

    private void SubMenuCopiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubMenuCopiarActionPerformed
        areadeTexto.copy();
    }//GEN-LAST:event_SubMenuCopiarActionPerformed

    private void SubMenuPegarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubMenuPegarActionPerformed
        areadeTexto.paste();
    }//GEN-LAST:event_SubMenuPegarActionPerformed

    private void SubMenuCortarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubMenuCortarActionPerformed
        areadeTexto.cut();
    }//GEN-LAST:event_SubMenuCortarActionPerformed

    private void SubMenuSeleecionarTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubMenuSeleecionarTodoActionPerformed
        areadeTexto.setSelectionStart(0);
        areadeTexto.setSelectionEnd(areadeTexto.getText().length());
    }//GEN-LAST:event_SubMenuSeleecionarTodoActionPerformed

    private void SubMenuConvertirMinusculasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubMenuConvertirMinusculasActionPerformed
        if (!areadeTexto.getSelectedText().isEmpty()) {
            areadeTexto.replaceSelection(areadeTexto.getSelectedText().toLowerCase());
        }
    }//GEN-LAST:event_SubMenuConvertirMinusculasActionPerformed

    private void checkremisionnuevaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkremisionnuevaActionPerformed
        
    }//GEN-LAST:event_checkremisionnuevaActionPerformed

    DialogoConfigurarConsecutivosRemision dccr;
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        if (dccr == null) {
            dccr = new DialogoConfigurarConsecutivosRemision(this, rootPaneCheckingEnabled);
        }
        dccr.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (JOptionPane.showConfirmDialog(this, "Seguro que desea salir de la aplicacion ?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    private void checkremisionnuevaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkremisionnuevaItemStateChanged
        if(!NUMERO_REMISION.isEmpty()){
            if(evt.getStateChange() == ItemEvent.SELECTED){
                cjnoremision.setText(""+Metodos.getConsecutivoRemision(CONSECUTIVO_EMPRESA, false));
                setACTUALIZANDO(true);
            }else if(evt.getStateChange() == ItemEvent.DESELECTED){
                cjnoremision.setText(NUMERO_REMISION);
                setACTUALIZANDO(false);
            }
        }            
    }//GEN-LAST:event_checkremisionnuevaItemStateChanged

    private void cjBuscarNombreHerramientaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjBuscarNombreHerramientaKeyReleased
        rowSorter.setRowFilter(RowFilter.regexFilter(cjBuscarNombreHerramienta.getText().toUpperCase(), 2));
    }//GEN-LAST:event_cjBuscarNombreHerramientaKeyReleased

    private void btnAgregarFilaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarFilaActionPerformed
        DialogoRegistrarHerramienta drh = new DialogoRegistrarHerramienta(this, false);
        drh.setVisible(true);
    }//GEN-LAST:event_btnAgregarFilaActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            if (!checkremisionnueva.isSelected()) {
                int filas[] = tablaHerramientas.getSelectedRows();
                for (int i = filas.length - 1; i >= 0; i--) {
                    if (getIDREMISION() > 0) {
                        if (conexion.GUARDAR("DELETE FROM datosremision_consorcio WHERE idremision=" + getIDREMISION() + " "
                            + "AND idherramienta=" + tablaHerramientas.getValueAt(filas[i], 1) + " ")) {
                        modeloHerramientas.removeRow(filas[i]);
                        }else{
                            modeloHerramientas.removeRow(filas[i]);
                        }
                    }
                }
        } else {
            modelo.Metodos.M("NO SE PUEDEN ELIMINAR HERRAMIENTA DE LA TABLA HASTA DESACTIVAR LA CASILLA DE 'REMISION NUEVA'.", "advertencia.png");
        }
        } catch (Exception e) {
            System.err.println("ERROR AL ELIMINAR LAS FILAS: " + e);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void tablaHerramientasKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaHerramientasKeyTyped
        if (evt.getKeyChar() == 10) {
            if (tablaHerramientas.getSelectedRow() == 0 && tablaHerramientas.getSelectedColumn() == 0) {
                btnAgregarFila.doClick();
                tablaHerramientas.setRowSelectionInterval(tablaHerramientas.getRowCount() - 1, tablaHerramientas.getRowCount() - 1);
                tablaHerramientas.setColumnSelectionInterval(2, 2);
            }
        }
    }//GEN-LAST:event_tablaHerramientasKeyTyped

    private void btnAgregarConductorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarConductorActionPerformed
        JComboBox<Conductor> conductores = new JComboBox<>();
        conductores.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        conductores.setUI(JComboBoxColor.JComboBoxColor.createUI(conductores));
        conductores.setMaximumRowCount(20);
        conductores.addItem(new Conductor(-1, "SIN CEDULA", "SELECCIONE..."));
        modelo.Conductor.llenarComboConductores(conductores);
        JOptionPane.showMessageDialog(this, conductores, "Seleccione un conductor", JOptionPane.DEFAULT_OPTION, new ImageIcon(getClass().getResource("/recursos/images/conductor.png")));
        if (conductores.getSelectedIndex() > 0) {
            Conductor c = conductores.getItemAt(conductores.getSelectedIndex());
            cjconductor.setText(c.getNombreConductor());
            cjcedula.setText(c.getCedulaConductor());
        }
    }//GEN-LAST:event_btnAgregarConductorActionPerformed

    private void btnAgregarConductor1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarConductor1ActionPerformed
        JComboBox<Ciudad> ciudades = new JComboBox<>();
        ciudades.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        ciudades.setUI(JComboBoxColor.JComboBoxColor.createUI(ciudades));
        ciudades.setMaximumRowCount(20);
        ciudades.addItem(new Ciudad(-1, "Seleccione...", "", ""));
        modelo.Ciudad.cargarComboNombreCiudades(ciudades);
        JOptionPane.showMessageDialog(this, ciudades, "Seleccione una ciudad", JOptionPane.DEFAULT_OPTION, new ImageIcon(getClass().getResource("/recursos/images/conductor.png")));
        if (ciudades.getSelectedIndex() > 0) {
            Ciudad c = ciudades.getItemAt(ciudades.getSelectedIndex());
            cjciudad.setText(c.getNombreCiudad());
            cjdestino.setText(c.getDireccionCiudad());
            cjtelefono.setText(c.getTelefonoCiudad());
        }
    }//GEN-LAST:event_btnAgregarConductor1ActionPerformed

    private void btnAgregarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarClienteActionPerformed
        JComboBox<modelo.Cliente> clientes = new JComboBox<>();
        clientes.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        clientes.setUI(JComboBoxColor.JComboBoxColor.createUI(clientes));
        clientes.setMaximumRowCount(20);
        clientes.addItem(new modelo.Cliente(-1, "SELECCIONE...", "SIN NIT"));
        modelo.Cliente.cargarComboNombreClientes(clientes);
        JOptionPane.showMessageDialog(this, clientes, "Seleccione un cliente", JOptionPane.DEFAULT_OPTION, new ImageIcon(getClass().getResource("/recursos/images/conductor.png")));
        if (clientes.getSelectedIndex() > 0) {
            modelo.Cliente c = clientes.getItemAt(clientes.getSelectedIndex());
//            cjcliente.setText(c.getNombreCliente());
        }
    }//GEN-LAST:event_btnAgregarClienteActionPerformed

    public void Enter(JTextField uno, final JTextField dos) {
        uno.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == 10) {
                    dos.grabFocus();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

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
            java.util.logging.Logger.getLogger(REMISIONESCDM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new REMISIONESCDM().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JMenuItem SubMenuConvertirAMayusculas;
    public static javax.swing.JMenuItem SubMenuConvertirMinusculas;
    public static javax.swing.JMenuItem SubMenuCopiar;
    public static javax.swing.JMenuItem SubMenuCortar;
    public static javax.swing.JMenuItem SubMenuPegar;
    public static javax.swing.JMenuItem SubMenuSeleecionarTodo;
    public static javax.swing.JPopupMenu SubMenuTexto;
    private CompuChiqui.JCTextArea areadeTexto;
    public javax.swing.JButton btnAgregarCliente;
    public javax.swing.JButton btnAgregarConductor;
    public javax.swing.JButton btnAgregarConductor1;
    private javax.swing.JButton btnAgregarFila;
    private javax.swing.JButton btnBuscarNombreHerramienta;
    private javax.swing.JButton btn_generar;
    private javax.swing.JButton btn_guardar;
    private javax.swing.JCheckBox checkremisionnueva;
    private CompuChiqui.JTextFieldPopup cjBuscarNombreHerramienta;
    private javax.swing.JTextField cjcedula;
    private javax.swing.JTextField cjcentrodecostos;
    private javax.swing.JTextField cjciudad;
    private javax.swing.JTextField cjconductor;
    private javax.swing.JTextField cjcontrato;
    private javax.swing.JTextField cjdestino;
    private javax.swing.JTextField cjempresatransportadora;
    private javax.swing.JTextField cjfactura;
    private com.toedter.calendar.JDateChooser cjfecha;
    private CompuChiqui.JTextFieldPopup cjnitcedulacliente;
    private javax.swing.JTextField cjnoremision;
    private javax.swing.JTextField cjplaca;
    private CompuChiqui.JTextFieldPopup cjresponsable;
    private javax.swing.JTextField cjtelefono;
    public javax.swing.JComboBox<Cliente> comboCliente;
    public javax.swing.JComboBox comboempresa;
    private javax.swing.JTabbedPane contenedor;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JPanel paneldatosremision;
    private javax.swing.JTable tablaHerramientas;
    // End of variables declaration//GEN-END:variables

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

    public String getTIPO() {
        return TIPO;
    }

    public void setTIPO(String TIPO) {
        this.TIPO = TIPO;
    }

    public String getCONSECUTIVO_EMPRESA() {
        return CONSECUTIVO_EMPRESA;
    }

    public void setCONSECUTIVO_EMPRESA(String CONSECUTIVO_EMPRESA) {
        this.CONSECUTIVO_EMPRESA = CONSECUTIVO_EMPRESA;
    }

    public String getREPORTE() {
        return REPORTE;
    }

    public void setREPORTE(String REPORTE) {
        this.REPORTE = REPORTE;
    }

}
