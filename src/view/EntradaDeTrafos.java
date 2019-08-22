package view;

import CopyPasteJTable.ExcelAdapter;
import Dialogos.DialogoImprimirAceite;
import Dialogos.DialogoRegistrarDiferencias;
import Dialogos.RegistrarDiferencia;
import Dialogos.Tensiones;
import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import modelo.Ciudad;
import modelo.Cliente;
import modelo.Conductor;
import modelo.ConexionBD;
import modelo.CustomTableModel;
import modelo.Metodos;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class EntradaDeTrafos extends javax.swing.JFrame {

    CustomTableModel modeloTabla;

    String SERVICIOS[] = {"REPARACION", "FABRICACION", "MANTENIMIENTO", "DADO DE BAJA", "GARANTIA", "DEVOLUCION", "REVISION", "RECONSTRUIDO", "NUEVO"};
    String TIPOS[] = {"CONVENCIONAL", "CONV. - REPOT.", "AUTOPROTEGIDO", "SECO", "PAD MOUNTED", "POTENCIA"};

//    String secciones[] = {"ENCUBE","CONEXIONES","ENSAMBLE","BOBINADO","DESARME Y DESENCUBE","PINTURA","METALMECANICA","NUCLEO"};
    String secciones[] = {"CONEXIONES", "ENSAMBLE", "BOBINADO", "DESARME Y DESENCUBE", "PINTURA", "METALMECANICA", "NUCLEO"};

    TableColumnAdjuster ajustarColumna;
    boolean LOTE_ABIERTO = false;
    private int IDENTRADA;
    private String CLIENTE = "";

    ArrayList<String> tensiones = new ArrayList<>();
    ArrayList<String> listaSeries = new ArrayList<>();

    TableRowSorter rowSorter;
    private int IDBUSQUEDA = 4;

    boolean YACARGO = false;

    ConexionBD conexion = new ConexionBD();

    modelo.Sesion sesion = modelo.Sesion.getConfigurador(null, -1);
    ExcelAdapter copypaste;

    private int COL_PLACA = 4;

    private int INDEX_COL_SERVICES = 20;
    private int INDEX_COL_TIPO_TRAFOS = 21;
    private int INDEX_COL_MARCAS = 5;
    private int INDEX_COL_HERRAJES = 15;
    private int INDEX_COL_TENSIONES = 8;

    public EntradaDeTrafos() {
        initComponents();

//        String sql = "SELECT kva, po, pc, io, uz FROM trifasiconuevoserie35";
//        ResultSet rs = model.Conexion2.CONSULTAR(sql);
//        try {
//            while(rs.next()){
//                if(conexion.GUARDAR("INSERT INTO trifasiconuevoserie35 VALUES ( "+rs.getString("kva")+" , "+rs.getString("po")+" , "+rs.getString("pc")+" , "+rs.getString("io")+" , "+rs.getString("uz")+" )")){
//                    
//                }
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        setIDENTRADA(322);
//        setIDENTRADA(282);
        this.setIconImage(new ImageIcon(getClass().getResource("/recursos/images/trafo.jpg")).getImage());

        ajustarColumna = new TableColumnAdjuster(tablaTrafos);
        ajustarColumna.adjustColumns();

        //CARGAMOS LAS TENSIONES QUE SE AUTOCOMPLETARAN EN LA COLUMNA TENSION
        getTensiones();

        //CARGAMOS EL MODELO DE LA TABLA ENTRADA DE TRANSFORMADORES
        cargarTablaDeTransformadores(checkOrdenar.isSelected());
        //ExcelAdapter excelAdapter = new CopyPasteJTable.ExcelAdapter(tablaTrafos);
        copypaste = new ExcelAdapter(tablaTrafos);

        tablaTrafos.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                lblFilasSeleccionadas.setText("Columnas: " + tablaTrafos.getSelectedColumnCount() + " Filas: " + tablaTrafos.getSelectedRowCount() + " Total filas: " + tablaTrafos.getRowCount());
            }
        });

        //CARGAR Y LLENAR EL COMBOBOX DE LOS CLIENTES        
        modelo.Cliente.cargarComboNombreClientes(comboCliente);
        comboCliente.setUI(JComboBoxColor.JComboBoxColor.createUI(comboCliente));
//        model.Ciudad.cargarComboNombreCiudades(this.entradaDeTrafos.comboCiudad);

        //CARGAR Y LLENAR EL COMBOBOX DE LOS CONDUCTORES
        modelo.Conductor.llenarComboConductores(comboConductor);
        comboConductor.setUI(JComboBoxColor.JComboBoxColor.createUI(comboConductor));

        //MUESTRA EL CONTENIDO DEL COMBOBOX AL MAXIMO ANCHO DEL ITEM MAS LARGO
        comboCliente.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        comboCiudad.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        comboConductor.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));

//        subMenuImprimirTodosLosFormatos.doClick();
    }

    /*
    SELECT * FROM entrada e INNER JOIN transformador t ON e.identrada=t.identrada WHERE  e.identrada=$P{IDENTRADA_TRAFOS} ORDER BY fase ASC, kvaentrada ASC, marca ASC, item ASC
     */
    public void cargarTablaDeTransformadores(boolean ordenar) {
        try {
            modeloTabla = new CustomTableModel(
                    new String[][]{},
                    modelo.EntradaDeTrafo.getColumnNames(),
                    this.tablaTrafos,
                    modelo.EntradaDeTrafo.getColumnClass(),
                    modelo.EntradaDeTrafo.getColumnEditables()
            );
            modeloTabla.setMenu(subMenuEntradaDeTrafos);
//        modeloTabla.setMenuItem(subMenuFiltrar);
            tablaTrafos.setSurrendersFocusOnKeystroke(true);

            rowSorter = new TableRowSorter(modeloTabla);
            tablaTrafos.setRowSorter(rowSorter);
            rowSorter.setRowFilter(RowFilter.regexFilter(cjBuscarSerie.getText().toUpperCase(), 3));

            tablaTrafos.getColumnModel().getColumn(0).setCellRenderer(new JButtonIntoJTable.BotonEnColumna());

            //COLUMNA SERVICIOS
            tablaTrafos.getColumnModel().getColumn(INDEX_COL_SERVICES).setCellEditor(new DefaultCellEditor(new JComboBox(SERVICIOS)));
            tablaTrafos.getColumnModel().getColumn(INDEX_COL_SERVICES).setCellRenderer(new JComboBoxIntoJTable.JComboBoxEnColumnaJTable(SERVICIOS));

            //COLUMNA TIPO DE TRANSFORMADOR
            tablaTrafos.getColumnModel().getColumn(INDEX_COL_TIPO_TRAFOS).setCellEditor(new DefaultCellEditor(new JComboBox(TIPOS)));
//        tablaTrafos.getColumnModel().getColumn(21).setCellRenderer(new JComboBoxIntoJTable.JComboBoxEnColumnaJTable(TIPOS));                

            //COLUMNA MARCAS
//        tablaTrafos.getColumnModel().getColumn(5).setCellEditor(new JTextFieldIntoJTable.JTextField_DefaultCellEditor(modelo.Marca.getJTextFieldMarcas()));
            tablaTrafos.getColumnModel().getColumn(INDEX_COL_MARCAS).setCellEditor(new JTextFieldIntoJTable.JTextField_DefaultCellEditor(new modelo.JTextFieldAutoComplete(modelo.Marca.getMarcas())));
//        entradaDeTrafos.tablaTrafos.getColumnModel().getColumn(4).setCellRenderer(new JTextFieldIntoJTable.JTextField_TableCellRenderer());

            //COLUMNA HERRAJE
            tablaTrafos.getColumnModel().getColumn(INDEX_COL_HERRAJES).setCellEditor(new JTextFieldIntoJTable.JTextField_DefaultCellEditor(modelo.Marca.getTextFieldHerrajes()));

            //COLUMNA TENSIONES       
            JTextComponent txt = new JTextField();
            AutoCompleteDecorator.decorate(txt, tensiones, true);
            tablaTrafos.getColumnModel().getColumn(INDEX_COL_TENSIONES).setCellEditor(new DefaultCellEditor((JTextField) txt));

            modeloTabla.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    if (e.getType() == TableModelEvent.UPDATE) {
//                        System.out.println("NO ESTA ENL ARRAY "+listaSeries.contains(modeloTabla.getValueAt(e.getFirstRow(), 0)));
//                         listaSeries.forEach((a)->System.out.println(a));
                        if (listaSeries.contains(modeloTabla.getValueAt(e.getFirstRow(), 0).toString())) {
                            switch (e.getColumn()) {
                                case 1:
                                    if (conexion.GUARDAR("UPDATE transformador SET item='" + modeloTabla.getValueAt(e.getFirstRow(), e.getColumn()) + "' WHERE idtransformador=" + modeloTabla.getValueAt(e.getFirstRow(), 0) + " AND identrada=" + IDENTRADA + " ")) {

                                    }
                                    break;
                                case 3:
                                    if (actualizaTrafo("numeroempresa", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 4:
                                    if (conexion.GUARDAR("UPDATE transformador SET numeroserie='" + modeloTabla.getValueAt(e.getFirstRow(), e.getColumn()) + "' WHERE idtransformador=" + modeloTabla.getValueAt(e.getFirstRow(), 0) + " AND identrada=" + IDENTRADA + " ")) {
                                    }
                                    break;
                                case 5:
                                    if (actualizaTrafo("marca", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 6:
                                    if (actualizaTrafo("kvaentrada", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 7:
                                    if (actualizaTrafo("fase", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 8:
                                    String GUARDAR = "";
                                    String t[] = modeloTabla.getValueAt(e.getFirstRow(), 8).toString().split("/");
                                    if (t.length == 3) {
                                        if (new ConexionBD().GUARDAR("UPDATE transformador SET tpe='" + t[0] + "' , tse='" + t[1] + "' , tte='" + t[2] + "' , tps='" + t[0] + "' , tss='" + t[1] + "' , tts='" + t[2] + "' WHERE idtransformador='" + modeloTabla.getValueAt(e.getFirstRow(), 0) + "' AND identrada='" + IDENTRADA + "' ")) {
                                        }
                                    } else {
                                        if (new ConexionBD().GUARDAR("UPDATE transformador SET tpe='0' , tse='0' , tte='0' , tps='0' , tss='0' , tts='0' WHERE idtransformador='" + modeloTabla.getValueAt(e.getFirstRow(), 0) + "' AND identrada='" + IDENTRADA + "' ")) {
                                        }
                                    }
                                    break;
                                case 9:
                                    if (actualizaTrafo("aat", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 10:
                                    if (actualizaTrafo("abt", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 11:
                                    if (actualizaTrafo("hat", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 12:
                                    if (actualizaTrafo("hbt", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 13:
                                    if (actualizaTrafo("ci", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 14:
                                    if (actualizaTrafo("ce", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 15:
                                    if (actualizaTrafo("herraje", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 16:
                                    if (actualizaTrafo("ano", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 17:
                                    if (actualizaTrafo("peso", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 18:
                                    if (actualizaTrafo("aceite", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 19:
                                    if (actualizaTrafo("observacionentrada", e.getFirstRow(), e.getColumn())) {
                                    }
                                    break;
                                case 20:
                                    if (actualizaTrafo("servicioentrada", e.getFirstRow(), e.getColumn())) {
                                        if (actualizaTrafo("serviciosalida", e.getFirstRow(), e.getColumn())) {
                                        }
                                    }
                                    break;
                                case 21:
                                    if (actualizaTrafo("tipotrafoentrada", e.getFirstRow(), e.getColumn())) {
                                        if (actualizaTrafo("tipotrafosalida", e.getFirstRow(), e.getColumn())) {
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            });

            String sql = " SELECT e.*, t.*, r.numero_remision FROM entrada e ";
            sql += " INNER JOIN transformador t ON t.identrada=e.identrada  ";
            sql += " LEFT JOIN remision r ON t.idremision=r.idremision ";
            sql += " INNER JOIN cliente c ON c.idcliente=e.idcliente  ";
            sql += " INNER JOIN conductor co ON co.idconductor=e.idconductor  ";
            sql += " WHERE e.identrada='" + IDENTRADA + "' ";
            sql += " " + ((ordenar) ? "ORDER BY fase ASC, kvaentrada ASC, marca" : "ORDER BY item") + " ";

            conexion.conectar();
            ResultSet rs = conexion.CONSULTAR(sql);
            while (rs.next()) {
                if (!listaSeries.contains(rs.getString("idtransformador"))) {
                    listaSeries.add(rs.getString("idtransformador"));
                }
                modeloTabla.addRow(new Object[]{
                    rs.getInt("idtransformador"),
                    rs.getInt("item"),//"N°",
                    rs.getString("numero_remision"),//"N° REMISION",
                    rs.getString("numeroempresa"),//"N° EMPRESA",
                    rs.getString("numeroserie"),//"N° SEIRE",
                    rs.getString("marca"),//"MARCA",
                    rs.getDouble("kvaentrada"),//"KVA",
                    rs.getInt("fase"),//"FASE",
                    rs.getString("tpe") + "/" + rs.getString("tse") + "/" + rs.getString("tte"),//"TENSION P",
                    rs.getString("aat"),//"A.T",
                    rs.getString("abt"),//"B.T",
                    rs.getString("hat"),//"H.A",
                    rs.getString("hbt"),//"H.B",
                    rs.getBoolean("ci"),//"INT",
                    rs.getBoolean("ce"),//"EXT",
                    rs.getString("herraje"),//"HERRAJE",
                    rs.getInt("ano"),//"AÑO",
                    rs.getInt("peso"),//"PESO",
                    rs.getInt("aceite"),//"ACEITE",
                    rs.getString("observacionentrada"),//"OBSERVACION",
                    rs.getString("serviciosalida"),//"SERVICIO",
                    rs.getString("tipotrafoentrada"),//"TIPO", 
                });
            }
            new TableColumnAdjuster(tablaTrafos).adjustColumns();
            lblFilasSeleccionadas.setText("Columnas: " + tablaTrafos.getSelectedColumnCount() + " Filas: " + tablaTrafos.getSelectedRowCount() + " Total filas: " + tablaTrafos.getRowCount());
            conexion.CERRAR();

            tablaTrafos.setDropTarget(new DropTarget(tablaTrafos, new DropTargetListener() {
                @Override
                public void dragEnter(DropTargetDragEvent dtde) {
                    System.out.println("Enter");
                }

                @Override
                public void dragOver(DropTargetDragEvent dtde) {
                    tablaTrafos.setRowSelectionInterval(tablaTrafos.rowAtPoint(dtde.getLocation()), tablaTrafos.rowAtPoint(dtde.getLocation()));
                    tablaTrafos.setColumnSelectionInterval(0, tablaTrafos.getColumnCount() - 1);
                }

                @Override
                public void dropActionChanged(DropTargetDragEvent dtde) {
                    System.out.println("ActionChanged");
                }

                @Override
                public void dragExit(DropTargetEvent dte) {
                    System.out.println("Exit");
                }

                @Override
                public void drop(DropTargetDropEvent dtde) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    if (dtde.getDropAction() == DnDConstants.ACTION_MOVE) {
                        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            Transferable t = dtde.getTransferable();
                            try {
                                List fileList = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
                                if (cjLote.getText().isEmpty()) {
                                    Metodos.M("EL NOMBRE DEL LOTE NO PUEDE ESTAR VACIO", "advertencia.png");
                                    return;
                                }
                                String noserie = (String) tablaTrafos.getValueAt(tablaTrafos.rowAtPoint(dtde.getLocation()), 4);
                                File rutaFotos = new File("FOTOS TRAFOS", ((Cliente) comboCliente.getModel().getSelectedItem()).getNombreCliente() + "/(" + getIDENTRADA() + ") " + cjLote.getText() + "/");
                                rutaFotos.mkdirs();
                                for (int i = 0; i < fileList.size(); i++) {
                                    File f = (File) fileList.get(i);
                                    String extension = f.getName().substring(f.getName().lastIndexOf("."), f.getName().length());
                                    if (f.getName().endsWith("jpg") || f.getName().endsWith("png")) {
                                        Files.copy(f.toPath(), new File(rutaFotos.toPath().toString() + "/" + noserie + "_" + f.getName() + extension).toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    } else {
                                        Metodos.M("FORMATO DE IMAGEN NO VALIDO PARA " + f.getName(), "advertencia.png");
                                    }
                                }
                            } catch (UnsupportedFlavorException | IOException ex) {
                                Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }));

        } catch (SQLException ex) {
            Metodos.ERROR(ex, "ERROR AL CARGAR EL LISTADO DE TRANSFORMADORES.");
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean actualizaTrafo(String columna, int fila, int col) {
        return new ConexionBD().GUARDAR(" UPDATE transformador SET " + columna + "='" + modeloTabla.getValueAt(fila, col).toString().trim() + "' "
                + " WHERE idtransformador='" + modeloTabla.getValueAt(fila, 0).toString().trim() + "' AND "
                + " identrada='" + IDENTRADA + "' ");
    }

    public void cargarEncabezadoEntrada() {
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR("SELECT * FROM entrada e \n"
                + "INNER JOIN cliente cli ON e.idcliente=cli.idcliente \n"
                + "INNER JOIN conductor con ON e.idconductor=con.idconductor\n"
                + "INNER JOIN ciudad ciu ON e.idciudad=ciu.idciudad\n"
                + "WHERE e.identrada=" + IDENTRADA + " ");
        try {
            if (rs.next()) {
                LOTE_ABIERTO = true;
                CLIENTE = rs.getString("nombrecliente");
                comboCliente.getModel().setSelectedItem(new Cliente(rs.getInt("idcliente"), rs.getString("nombrecliente"), rs.getString("nitcliente")));
                comboCiudad.getModel().setSelectedItem(new Ciudad(rs.getInt("idciudad"), rs.getString("nombreciudad"), rs.getString("direccionciudad"), rs.getString("telefonociudad")));
                comboConductor.getModel().setSelectedItem(new Conductor(rs.getInt("idconductor"), rs.getString("cedulaconductor"), rs.getString("nombreconductor")));
                cjIdEntradaAlmacen.setText(rs.getString("identradaalmacen"));
                cjLote.setText(rs.getString("lote"));
                cjContrato.setText(rs.getString("contrato"));
                cjOp.setText(rs.getString("op"));
                cjCentroDeCostos.setText(rs.getString("centrodecostos"));
                cjFechaRecepcion.setDate(rs.getDate("fecharecepcion"));
                cjPlacaVehiculo.setText(rs.getString("placavehiculo"));
                cjObservaciones.setText(rs.getString("observacion"));
                checkNuevos.setSelected(rs.getBoolean("nuevo"));
            }
            conexion.CERRAR();
            YACARGO = true;
        } catch (SQLException ex) {
            Metodos.ERROR(ex, "ERROR AL CARGAR EL ENCABEZADO DEL LOTE.");
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getTensiones() {
        tensiones.clear();
        try {
            conexion.conectar();
            ResultSet rs = conexion.CONSULTAR("SELECT * FROM tensiones ORDER BY id ASC");
            while (rs.next()) {
                tensiones.add(rs.getString("primario") + "/" + rs.getString("secundario") + "/" + rs.getString("terciario"));
            }
            conexion.CERRAR();
        } catch (SQLException ex) {
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        subMenuEntradaDeTrafos = new javax.swing.JPopupMenu();
        subMenuItemEliminarTrafo = new javax.swing.JMenuItem();
        subMenuExportarExcel = new javax.swing.JMenuItem();
        subMenuRegistrarObservacion = new javax.swing.JMenuItem();
        subMenuImprimirFormatos = new javax.swing.JPopupMenu();
        subMenuImprimirEntradaDeAlmacen = new javax.swing.JMenuItem();
        subMenuImprimirEntradaDeTrafos = new javax.swing.JMenuItem();
        subMenuImprimirOrdenDeProduccion = new javax.swing.JMenuItem();
        subMenuImprimirTodosLosFormatos = new javax.swing.JMenuItem();
        subMenuImprimirMuestrasDeAceite = new javax.swing.JMenuItem();
        imprimirHojasDeRuta = new javax.swing.JMenuItem();
        tarjetaDeAprobacion = new javax.swing.JMenuItem();
        consumoDeAceite = new javax.swing.JMenuItem();
        jSplitPane1 = new javax.swing.JSplitPane();
        panelTabla = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaTrafos = new javax.swing.JTable();
        jToolBar2 = new javax.swing.JToolBar();
        lblFilasSeleccionadas = new javax.swing.JLabel();
        barra = new javax.swing.JProgressBar();
        jToolBar3 = new javax.swing.JToolBar();
        cjBuscarSerie = new CompuChiqui.JTextFieldPopup();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        checkOrdenar = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        comboCliente = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        comboCiudad = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        comboConductor = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        cjIdEntradaAlmacen = new CompuChiqui.JTextFieldPopup();
        jLabel7 = new javax.swing.JLabel();
        cjLote = new CompuChiqui.JTextFieldPopup();
        jLabel8 = new javax.swing.JLabel();
        cjContrato = new CompuChiqui.JTextFieldPopup();
        cjOp = new CompuChiqui.JTextFieldPopup();
        jLabel10 = new javax.swing.JLabel();
        cjCentroDeCostos = new CompuChiqui.JTextFieldPopup();
        jLabel1 = new javax.swing.JLabel();
        cjFechaRecepcion = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        cjPlacaVehiculo = new CompuChiqui.JTextFieldPopup();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        cjObservaciones = new CompuChiqui.JCTextArea();
        checkNuevos = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        cjrepresentante = new CompuChiqui.JTextFieldPopup();
        jToolBar1 = new javax.swing.JToolBar();
        btnAgregarFila = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnBorrarFila = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnGuardar = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnImprimrFormatos = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnInsertar = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuEditar = new javax.swing.JMenu();
        menuItemTensiones = new javax.swing.JMenuItem();
        menuItemRegistrarDiferencias = new javax.swing.JMenuItem();

        subMenuItemEliminarTrafo.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        subMenuItemEliminarTrafo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        subMenuItemEliminarTrafo.setText("Eliminar del lote");
        subMenuItemEliminarTrafo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuItemEliminarTrafoActionPerformed(evt);
            }
        });
        subMenuEntradaDeTrafos.add(subMenuItemEliminarTrafo);

        subMenuExportarExcel.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        subMenuExportarExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/excel.png"))); // NOI18N
        subMenuExportarExcel.setText("Exportar a excel");
        subMenuExportarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuExportarExcelActionPerformed(evt);
            }
        });
        subMenuEntradaDeTrafos.add(subMenuExportarExcel);

        subMenuRegistrarObservacion.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        subMenuRegistrarObservacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/check.png"))); // NOI18N
        subMenuRegistrarObservacion.setText("Registrar Observación");
        subMenuRegistrarObservacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuRegistrarObservacionActionPerformed(evt);
            }
        });
        subMenuEntradaDeTrafos.add(subMenuRegistrarObservacion);

        subMenuImprimirEntradaDeAlmacen.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        subMenuImprimirEntradaDeAlmacen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/reporte2.png"))); // NOI18N
        subMenuImprimirEntradaDeAlmacen.setText("Entrada De Almacen");
        subMenuImprimirEntradaDeAlmacen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuImprimirEntradaDeAlmacenActionPerformed(evt);
            }
        });
        subMenuImprimirFormatos.add(subMenuImprimirEntradaDeAlmacen);

        subMenuImprimirEntradaDeTrafos.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        subMenuImprimirEntradaDeTrafos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/reporte2.png"))); // NOI18N
        subMenuImprimirEntradaDeTrafos.setText("Entrada De Transformadores");
        subMenuImprimirEntradaDeTrafos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuImprimirEntradaDeTrafosActionPerformed(evt);
            }
        });
        subMenuImprimirFormatos.add(subMenuImprimirEntradaDeTrafos);

        subMenuImprimirOrdenDeProduccion.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        subMenuImprimirOrdenDeProduccion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/control.png"))); // NOI18N
        subMenuImprimirOrdenDeProduccion.setText("Orden de Produccion");
        subMenuImprimirOrdenDeProduccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuImprimirOrdenDeProduccionActionPerformed(evt);
            }
        });
        subMenuImprimirFormatos.add(subMenuImprimirOrdenDeProduccion);

        subMenuImprimirTodosLosFormatos.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        subMenuImprimirTodosLosFormatos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/multiple.png"))); // NOI18N
        subMenuImprimirTodosLosFormatos.setText("Formatos de Produccion");
        subMenuImprimirTodosLosFormatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuImprimirTodosLosFormatosActionPerformed(evt);
            }
        });
        subMenuImprimirFormatos.add(subMenuImprimirTodosLosFormatos);

        subMenuImprimirMuestrasDeAceite.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        subMenuImprimirMuestrasDeAceite.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/peligro.png"))); // NOI18N
        subMenuImprimirMuestrasDeAceite.setText("Muestras de Aceite");
        subMenuImprimirMuestrasDeAceite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuImprimirMuestrasDeAceiteActionPerformed(evt);
            }
        });
        subMenuImprimirFormatos.add(subMenuImprimirMuestrasDeAceite);

        imprimirHojasDeRuta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/hojasderuta2.png"))); // NOI18N
        imprimirHojasDeRuta.setText("Hojas de Ruta");
        imprimirHojasDeRuta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imprimirHojasDeRutaActionPerformed(evt);
            }
        });
        subMenuImprimirFormatos.add(imprimirHojasDeRuta);

        tarjetaDeAprobacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/hojasderuta2.png"))); // NOI18N
        tarjetaDeAprobacion.setText("Tarjeta de Aprobación");
        tarjetaDeAprobacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tarjetaDeAprobacionActionPerformed(evt);
            }
        });
        subMenuImprimirFormatos.add(tarjetaDeAprobacion);

        consumoDeAceite.setText("Consumo de aceite");
        consumoDeAceite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                consumoDeAceiteActionPerformed(evt);
            }
        });
        subMenuImprimirFormatos.add(consumoDeAceite);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Entrada de transformadores");

        jSplitPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jSplitPane1.setDividerLocation(180);
        jSplitPane1.setDividerSize(8);
        jSplitPane1.setResizeWeight(0.2);
        jSplitPane1.setToolTipText("");
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setOneTouchExpandable(true);

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
        tablaTrafos.setGridColor(new java.awt.Color(227, 227, 227));
        tablaTrafos.setRowHeight(25);
        tablaTrafos.getTableHeader().setReorderingAllowed(false);
        tablaTrafos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaTrafosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaTrafos);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        lblFilasSeleccionadas.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jToolBar2.add(lblFilasSeleccionadas);
        jToolBar2.add(barra);

        jToolBar3.setRollover(true);

        cjBuscarSerie.setPlaceholder("Ingrese el numero de serie");
        cjBuscarSerie.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjBuscarSerieKeyReleased(evt);
            }
        });
        jToolBar3.add(cjBuscarSerie);
        jToolBar3.add(jSeparator6);

        checkOrdenar.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        checkOrdenar.setSelected(true);
        checkOrdenar.setText("Ordenar");
        checkOrdenar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkOrdenarActionPerformed(evt);
            }
        });
        jToolBar3.add(checkOrdenar);

        javax.swing.GroupLayout panelTablaLayout = new javax.swing.GroupLayout(panelTabla);
        panelTabla.setLayout(panelTablaLayout);
        panelTablaLayout.setHorizontalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTablaLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE))
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)))
        );
        panelTablaLayout.setVerticalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTablaLayout.createSequentialGroup()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setRightComponent(panelTabla);

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel3.setText("Cliente:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel3.add(jLabel3, gridBagConstraints);

        comboCliente.setMaximumRowCount(20);
        comboCliente.setAutoscrolls(true);
        comboCliente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboClienteItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel3.add(comboCliente, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel4.setText("Ciudad:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel3.add(jLabel4, gridBagConstraints);

        comboCiudad.setMaximumRowCount(20);
        comboCiudad.setAutoscrolls(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel3.add(comboCiudad, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel6.setText("Conductor:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel3.add(jLabel6, gridBagConstraints);

        comboConductor.setMaximumRowCount(20);
        comboConductor.setAutoscrolls(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel3.add(comboConductor, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel5.setText("ID Entrada:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel3.add(jLabel5, gridBagConstraints);

        cjIdEntradaAlmacen.setPlaceholder("N° Entrada Almacen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel3.add(cjIdEntradaAlmacen, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel7.setText("Lote:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel3.add(jLabel7, gridBagConstraints);

        cjLote.setPlaceholder("Lote");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel3.add(cjLote, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel8.setText("Contrato:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel3.add(jLabel8, gridBagConstraints);

        cjContrato.setPlaceholder("Contrato");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel3.add(cjContrato, gridBagConstraints);

        cjOp.setPlaceholder("Orden De Produccion");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel3.add(cjOp, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel10.setText("Cent. costos:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel3.add(jLabel10, gridBagConstraints);

        cjCentroDeCostos.setPlaceholder("Centro de Costos");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel3.add(cjCentroDeCostos, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel1.setText("Fecha Recepcion:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel3.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel3.add(cjFechaRecepcion, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel11.setText("Placa Vehiculo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel3.add(jLabel11, gridBagConstraints);

        cjPlacaVehiculo.setPlaceholder("Placa vehiculo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel3.add(cjPlacaVehiculo, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel2.setText("Observaciones:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        jPanel3.add(jLabel2, gridBagConstraints);

        cjObservaciones.setColumns(20);
        cjObservaciones.setRows(5);
        cjObservaciones.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jScrollPane2.setViewportView(cjObservaciones);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
        jPanel3.add(jScrollPane2, gridBagConstraints);

        checkNuevos.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        checkNuevos.setText("Orden de Produccion: ( Nuevos ? )");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel3.add(checkNuevos, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel9.setText("Representante:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel3.add(jLabel9, gridBagConstraints);

        cjrepresentante.setPlaceholder("N° Entrada Almacen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel3.add(cjrepresentante, gridBagConstraints);

        jScrollPane3.setViewportView(jPanel3);

        jSplitPane1.setLeftComponent(jScrollPane3);

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnAgregarFila.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnAgregarFila.setForeground(new java.awt.Color(0, 204, 0));
        btnAgregarFila.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/row.png"))); // NOI18N
        btnAgregarFila.setText("Agregar fla");
        btnAgregarFila.setToolTipText("Añadir fila");
        btnAgregarFila.setFocusable(false);
        btnAgregarFila.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnAgregarFila.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarFilaActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAgregarFila);
        jToolBar1.add(jSeparator1);

        btnBorrarFila.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnBorrarFila.setForeground(new java.awt.Color(255, 0, 0));
        btnBorrarFila.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/borrar_fila.png"))); // NOI18N
        btnBorrarFila.setText("Borrar fila");
        btnBorrarFila.setToolTipText("Borrar fila");
        btnBorrarFila.setFocusable(false);
        btnBorrarFila.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnBorrarFila.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarFilaActionPerformed(evt);
            }
        });
        jToolBar1.add(btnBorrarFila);
        jToolBar1.add(jSeparator2);

        btnGuardar.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Guardar.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.setToolTipText("Guardar registros");
        btnGuardar.setFocusable(false);
        btnGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnGuardar);
        jToolBar1.add(jSeparator3);

        btnImprimrFormatos.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnImprimrFormatos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/imprimir.png"))); // NOI18N
        btnImprimrFormatos.setText("Imprimir");
        btnImprimrFormatos.setToolTipText("Generar entrada de almacen");
        btnImprimrFormatos.setFocusable(false);
        btnImprimrFormatos.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnImprimrFormatos.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimrFormatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimrFormatosActionPerformed(evt);
            }
        });
        jToolBar1.add(btnImprimrFormatos);
        jToolBar1.add(jSeparator4);

        btnInsertar.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnInsertar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/check.png"))); // NOI18N
        btnInsertar.setText("Insertar");
        btnInsertar.setToolTipText("Generar entrada de almacen");
        btnInsertar.setFocusable(false);
        btnInsertar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnInsertar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnInsertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnInsertar);

        jMenu1.setText("Archivo");
        jMenuBar1.add(jMenu1);

        menuEditar.setText("Editar");
        menuEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditarActionPerformed(evt);
            }
        });

        menuItemTensiones.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/voltage.png"))); // NOI18N
        menuItemTensiones.setText("Tensiones");
        menuItemTensiones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemTensionesActionPerformed(evt);
            }
        });
        menuEditar.add(menuItemTensiones);

        menuItemRegistrarDiferencias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/add.png"))); // NOI18N
        menuItemRegistrarDiferencias.setText("Registrar diferencias");
        menuItemRegistrarDiferencias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRegistrarDiferenciasActionPerformed(evt);
            }
        });
        menuEditar.add(menuItemRegistrarDiferencias);

        jMenuBar1.add(menuEditar);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void menuEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditarActionPerformed
        try {
            DialogoRegistrarDiferencias drd = new DialogoRegistrarDiferencias(this, true);
            drd.CargarDatos(IDENTRADA);
            drd.setTitle(comboCliente.getSelectedItem().toString());
            drd.setVisible(true);
        } catch (Exception ex) {
            Metodos.ERROR(ex, "ERROR AL INTENTAR ABRIR LA VENTANA DE REGISTRO DE DIFERENCIAS.");
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_menuEditarActionPerformed

    private void btnImprimrFormatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimrFormatosActionPerformed
        subMenuImprimirFormatos.show(btnImprimrFormatos,
                (int) (btnImprimrFormatos.getAlignmentX() + btnImprimrFormatos.getWidth() / 2),
                (int) (btnImprimrFormatos.getAlignmentY() + btnImprimrFormatos.getHeight()));
    }//GEN-LAST:event_btnImprimrFormatosActionPerformed

    private void btnAgregarFilaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarFilaActionPerformed
        modeloTabla.addRow(new Object[]{
            "",
            0,//"N°",
            "",//"REMISION",
            "",//"N° EMPRESA",
            "",//"N° SEIRE",
            "",//"MARCA",
            0,//"KVA",
            0,//"FASE",
            "",//"TENSION P",
            0,//"A.T",
            0,//"B.T",
            0,//"H.A",
            0,//"H.B",
            false,//"INT",
            false,//"EXT",
            "",//"HERRAJE",
            0,//"AÑO",
            0,//"PESO",
            0,//"ACEITE",
            "",//"OBSERVACION",
            "REPARACION",//"SERVICIO",
            "CONVENCIONAL",//"TIPO", 
        });
        modeloTabla.setValueAt(tablaTrafos.getRowCount(), tablaTrafos.getRowCount() - 1, 1);
        ajustarColumna.adjustColumns();
    }//GEN-LAST:event_btnAgregarFilaActionPerformed

    private void btnBorrarFilaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarFilaActionPerformed
        int filas[] = tablaTrafos.getSelectedRows();
        for (int i = filas.length - 1; i >= 0; i--) {
            modeloTabla.removeRow(filas[i]);
        }
    }//GEN-LAST:event_btnBorrarFilaActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        try {
            int idCliente = ((Cliente) comboCliente.getModel().getSelectedItem()).getIdCliente();
            int idCiudad = 0;
            //int idConductor = entradaDeTrafos.comboConductor.getItemAt(entradaDeTrafos.comboConductor.getSelectedIndex()).getIdConductor();
            int idConductor = ((Conductor) comboConductor.getModel().getSelectedItem()).getIdConductor();
            //idCiudad = entradaDeTrafos.comboCiudad.getItemAt(entradaDeTrafos.comboCiudad.getSelectedIndex()).getIdCiudad();
            idCiudad = ((Ciudad) comboCiudad.getModel().getSelectedItem()).getIdCiudad();

            String idEntradaAlmacen = cjIdEntradaAlmacen.getText().trim();
            String lote = cjLote.getText().trim();
            String contrato = cjContrato.getText().trim();
            String op = cjOp.getText().trim();
            String placa = cjPlacaVehiculo.getText().trim();
            String centrodecostos = cjCentroDeCostos.getText().trim();
            java.util.Date fecharecepcion = cjFechaRecepcion.getDate();
            java.util.Date fechaRegistro = new java.util.Date();
            boolean estado = false;
            String observaciones = cjObservaciones.getText();

            if (Inet4Address.getLocalHost().getHostName().equals("ALMACEN") || Inet4Address.getLocalHost().getHostName().equals("PROGRAMADOR")) {
                String ACTUALIZA_LOTE = "";
                if (LOTE_ABIERTO) {//SI EL LOTE ESTA ABIERTO, ACTUALIZO.
                    ACTUALIZA_LOTE = " UPDATE entrada SET ";
                    ACTUALIZA_LOTE += " idcliente='" + idCliente + "' , ";
                    ACTUALIZA_LOTE += " idciudad='" + idCiudad + "' , ";
                    ACTUALIZA_LOTE += " idconductor='" + idConductor + "' , ";
                    ACTUALIZA_LOTE += " idusuario='" + sesion.getIdUsuario() + "' , ";
                    ACTUALIZA_LOTE += " identradaalmacen='" + idEntradaAlmacen + "' ,  ";
                    ACTUALIZA_LOTE += " lote='" + lote + "' , ";
                    ACTUALIZA_LOTE += " contrato='" + contrato + "' , ";
                    ACTUALIZA_LOTE += " op='" + op + "' , ";
                    ACTUALIZA_LOTE += " centrodecostos='" + centrodecostos + "' , ";
                    ACTUALIZA_LOTE += " fecharecepcion='" + fecharecepcion + "' , ";
                    ACTUALIZA_LOTE += " fechaactualizado='" + fechaRegistro + "' ,  ";
                    ACTUALIZA_LOTE += " observacion='" + observaciones + "' , ";
                    ACTUALIZA_LOTE += " placavehiculo='" + placa + "' , ";
                    ACTUALIZA_LOTE += " nuevo='" + checkNuevos.isSelected() + "' , ";
                    ACTUALIZA_LOTE += " representante='" + cjrepresentante.getText().trim() + "' ";
                    ACTUALIZA_LOTE += " WHERE identrada='" + IDENTRADA + "' ";
                } else {//DE LO CONTRARIO 
                    ACTUALIZA_LOTE = " INSERT INTO entrada (idcliente,idciudad,idconductor,idusuario,identradaalmacen,nombrepc,lote, ";
                    ACTUALIZA_LOTE += " contrato,op,centrodecostos,fecharecepcion,fecharegistrado,estado,observacion,placavehiculo,nuevo,representante) VALUES \n( ";
                    ACTUALIZA_LOTE += " '" + idCliente + "' , ";
                    ACTUALIZA_LOTE += " '" + idCiudad + "' , ";
                    ACTUALIZA_LOTE += " '" + idConductor + "' , ";
                    ACTUALIZA_LOTE += " '" + sesion.getIdUsuario() + "' , ";
                    ACTUALIZA_LOTE += " '" + idEntradaAlmacen + "' , ";
                    ACTUALIZA_LOTE += " '" + Inet4Address.getLocalHost().getHostName() + "' , ";
                    ACTUALIZA_LOTE += " '" + lote + "' , ";
                    ACTUALIZA_LOTE += " '" + contrato + "' , ";
                    ACTUALIZA_LOTE += " '" + op + "' , ";
                    ACTUALIZA_LOTE += " '" + centrodecostos + "' , ";
                    ACTUALIZA_LOTE += " '" + fecharecepcion + "' , ";
                    ACTUALIZA_LOTE += " '" + fechaRegistro + "' , ";
                    ACTUALIZA_LOTE += " '" + false + "' , ";
                    ACTUALIZA_LOTE += " '" + observaciones + "' , ";
                    ACTUALIZA_LOTE += " '" + placa + "' , ";
                    ACTUALIZA_LOTE += " '" + checkNuevos.isSelected() + "' , ";
                    ACTUALIZA_LOTE += " '" + cjrepresentante.getText().trim() + "' ";
                    ACTUALIZA_LOTE += " ) ";
                }

                if (new ConexionBD().GUARDAR(ACTUALIZA_LOTE)) {
                    if (!LOTE_ABIERTO) {
                        conexion.conectar();
                        ResultSet rs = conexion.CONSULTAR("SELECT last_value FROM entrada_identrada_seq");
                        rs.next();
                        IDENTRADA = rs.getInt("last_value");
                        LOTE_ABIERTO = true;
                        conexion.CERRAR();
                    }
                    String GUARDAR = " INSERT INTO transformador ( ";
                    GUARDAR += " item, numeroempresa, numeroserie, marca, kvaentrada, kvasalida, fase, tpe, tse, ";
                    GUARDAR += " tte, tps, tss, tts, aat, abt, hat, hbt, ci, ce, herraje, ano, ";
                    GUARDAR += " peso, aceite, observacionentrada, servicioentrada, serviciosalida, ";
                    GUARDAR += " tipotrafoentrada, tipotrafosalida, estado, identrada ";
                    GUARDAR += " ) VALUES ";
                    int cantidadGuardar = 0;
//                        barraProgresoEntrada.setMaximum(modelo.getRowCount());
                    for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                        tablaTrafos.setRowSelectionInterval(i, i);
                        tablaTrafos.setColumnSelectionInterval(COL_PLACA, COL_PLACA);
                        if (modeloTabla.getValueAt(i, COL_PLACA).equals("")) {
                            modeloTabla.setValueAt("SIN PLACA " + (i + 1), i, COL_PLACA);
                            //JOptionPane.showMessageDialog(this, "EL ITEM "+modeloTabla.getValueAt(i, 3)+" NO TIENE NUMERO DE SERIE, POR LO TANTO NO SE GUARDARA EN LA BASE DE DATOS.\nSI NO TIENE NUMERO DE SERIE ASIGENELO EL VALOR '0' Y HAGA CLICK NUEVAMENTE EN EL BOTON GUARDAR.", "ITEM SIN NUMERO DE SERIE", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/recursos/images/advertencia.png")));
                        }
                        System.out.println("ID ES *" + modeloTabla.getValueAt(i, 0) == null + "*");
                        if (modeloTabla.getValueAt(i, 0).toString().isEmpty()) {
                            System.out.println(" EL ID ES NULO " + modeloTabla.getValueAt(i, 0));
                            //System.out.println(modeloTabla.getValueAt(i, COL_PLACA)+" NO ESTA EN ");
//                            listaSeries.add(modeloTabla.getValueAt(i, COL_PLACA).toString());
                            cantidadGuardar++;
                            GUARDAR += "( '" + modeloTabla.getValueAt(i, 1) + "' , ";//ITEM
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 3) + "' , ";//EMPRESA
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 4).toString().trim() + "' , ";//SERIE
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 5) + "' , ";//MARCA
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 6) + "' , ";//KVA ENTRADA
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 6) + "' , ";//KVA SALIDA
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 7) + "' , ";//FASE

                            String tension[] = modeloTabla.getValueAt(i, 8).toString().split("/");
                            if (tension.length == 3) {
                                GUARDAR += " '" + tension[0] + "' , ";//TENSION PRIMARIA ENTRADA
                                GUARDAR += " '" + tension[1] + "' , ";//TENSION SECUNDARIA ENTRADA
                                GUARDAR += " '" + tension[2] + "' , ";//TENSION TERCIARIA ENTRADA
                                //**********************
                                GUARDAR += " '" + tension[0] + "' , ";//TENSION PRIMARIA SALIDA
                                GUARDAR += " '" + tension[1] + "' , ";//TENSION SECUNDARIA SALIDA
                                GUARDAR += " '" + tension[2] + "' , ";//TENSION TERCIARIA SALIDA
                            } else {
                                GUARDAR += " '0' , ";//TENSION PRIMARIA ENTRADA
                                GUARDAR += " '0' , ";//TENSION SECUNDARIA ENTRADA
                                GUARDAR += " '0' , ";//TENSION TERCIARIA ENTRADA
                                //**********************
                                GUARDAR += " '0' , ";//TENSION PRIMARIA SALIDA
                                GUARDAR += " '0' , ";//TENSION SECUNDARIA SALIDA
                                GUARDAR += " '0' , ";//TENSION TERCIARIA SALIDA
                            }

                            GUARDAR += " '" + modeloTabla.getValueAt(i, 9) + "' , ";//AAT
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 10) + "' , ";//ABT
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 11) + "' , ";//HAT
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 12) + "' , ";//HBT
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 13) + "' , ";//CI
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 14) + "' , ";//CE
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 15) + "' , ";//HERRAJE
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 16) + "' , ";//ANO
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 17) + "' , ";//PESO
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 18) + "' , ";//ACEITE
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 19) + "' , ";//OBSERV. ENTRA.
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 20) + "' , ";//SERV. ENTRA.
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 20) + "' , ";//SERV. SALI.
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 21) + "' , ";//TIPO. TRAFO. ENTRA.
                            GUARDAR += " '" + modeloTabla.getValueAt(i, 21) + "' , ";//TIPO. TRAFO. SALI.
                            GUARDAR += " 'EN PLANTA' , ";//ESTADO
                            GUARDAR += " '" + IDENTRADA + "' ),\n";//ID ENTRADA
//                                GUARDAR += " '0' , ";//ID DESPACHO
//                                GUARDAR += " '0' ) ,\n";//ID SALIDA
                        }
                    }
                    GUARDAR = GUARDAR.substring(0, GUARDAR.length() - 2);
                    if (cantidadGuardar > 0 && new ConexionBD().GUARDAR(GUARDAR)) {
                        cargarTablaDeTransformadores(checkOrdenar.isSelected());
                        LOTE_ABIERTO = true;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "SOLO EL PERSONAL DE ALMACEN PUEDE REALIZAR CAMBIOS", "Debe completar todos los campos", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/recursos/images/advertencia.png")));
            }

        } catch (UnknownHostException | HeadlessException ex) {
            Metodos.ERROR(ex, "ERROR AL VERIFICAR EL NOMBRE DE EQUIPO(PC) QUE REALIZARA LOS CAMBIOS.");
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            Metodos.ERROR(ex, "SELECCIONE UNA CIUDAD");
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Metodos.ERROR(ex, "ERROR AL EJECUTAR LA CONSULTA A LA BASE DE DATOS");
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.lang.ClassCastException ex) {
            Metodos.ERROR(ex, "OCURRIO UN ERROR AL PARECER EN LA SELECCION DEL CLIENTE, CIUDAD O CONDUCTOR. SELECCIONELO NUEVAMENTE.");
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void menuItemTensionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemTensionesActionPerformed
        Tensiones t = new Tensiones(this, true);
        t.setVisible(true);
        getTensiones();
    }//GEN-LAST:event_menuItemTensionesActionPerformed

    private void subMenuImprimirEntradaDeAlmacenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuImprimirEntradaDeAlmacenActionPerformed
        (new Thread() {
            @Override
            public void run() {
                try {
                    btnImprimrFormatos.setEnabled(false);
                    btnImprimrFormatos.setIcon(new ImageIcon(getClass().getResource("/recursos/images/gif.gif")));
                    JasperReport reporte = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/ENTRADADEALMACEN_NUEVA.jasper").toString()));
                    Map<String, Object> p = new HashMap<String, Object>();
                    p.put("IDLOTE", IDENTRADA);
                    JasperPrint jasperprint = JasperFillManager.fillReport(reporte, p, conexion.conectar());
                    JasperViewer.viewReport(jasperprint, false);
                    JasperViewer visor = new JasperViewer(jasperprint, false);
                } catch (JRException | MalformedURLException ex) {
                    Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    btnImprimrFormatos.setIcon(new ImageIcon(getClass().getResource("/recursos/images/imprimir.png")));
                    btnImprimrFormatos.setEnabled(true);
                }
            }
        }).start();
    }//GEN-LAST:event_subMenuImprimirEntradaDeAlmacenActionPerformed

    private void subMenuItemEliminarTrafoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuItemEliminarTrafoActionPerformed
        int filas[] = tablaTrafos.getSelectedRows();
        for (int i = filas.length - 1; i >= 0; i--) {
            if (new ConexionBD().GUARDAR("DELETE FROM transformador WHERE idtransformador='" + tablaTrafos.getValueAt(filas[i], 0) + "' AND identrada='" + IDENTRADA + "' ")) {
                listaSeries.remove(tablaTrafos.getValueAt(filas[i], 4).toString());
                modeloTabla.removeRow(filas[i]);
            }
        }
    }//GEN-LAST:event_subMenuItemEliminarTrafoActionPerformed

    private void checkOrdenarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkOrdenarActionPerformed
        cargarTablaDeTransformadores(checkOrdenar.isSelected());
    }//GEN-LAST:event_checkOrdenarActionPerformed

    private void tablaTrafosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaTrafosMouseClicked
//        if(SwingUtilities.isRightMouseButton(evt)){
//            int col = tablaTrafos.columnAtPoint(evt.getPoint());
//            int row = tablaTrafos.rowAtPoint(evt.getPoint());            
//            subMenuFiltrar.setText("Buscar por: "+tablaTrafos.getColumnName(col));
//            subMenuEntradaDeTrafos.show(tablaTrafos, evt.getX(), evt.getY());
//            
//            if(tablaTrafos.getSelectedRowCount()==1){
//                tablaTrafos.setRowSelectionInterval(row, row);
//                tablaTrafos.setColumnSelectionInterval(0, tablaTrafos.getColumnCount()-1);
//            }
//        }
    }//GEN-LAST:event_tablaTrafosMouseClicked

    private void cjBuscarSerieKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjBuscarSerieKeyReleased
        rowSorter.setRowFilter(RowFilter.regexFilter(cjBuscarSerie.getText().toUpperCase()));
    }//GEN-LAST:event_cjBuscarSerieKeyReleased

    private void comboClienteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboClienteItemStateChanged
        if (YACARGO || !LOTE_ABIERTO) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                modelo.Ciudad.cargarComboCiudadesPorCliente(((Cliente) comboCliente.getModel().getSelectedItem()).getIdCliente(), comboCiudad);
            }
        }
    }//GEN-LAST:event_comboClienteItemStateChanged

    private void menuItemRegistrarDiferenciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRegistrarDiferenciasActionPerformed
        try {
            DialogoRegistrarDiferencias drd = new DialogoRegistrarDiferencias(this, rootPaneCheckingEnabled);
            drd.CargarDatos(IDENTRADA);
            drd.setTitle("Diferencias " + CLIENTE + " Lote " + cjLote.getText());
            drd.setVisible(true);
        } catch (Exception e) {
            Metodos.ERROR(e, "ERROR");
        }
    }//GEN-LAST:event_menuItemRegistrarDiferenciasActionPerformed

    private void subMenuImprimirEntradaDeTrafosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuImprimirEntradaDeTrafosActionPerformed
        try {
            if (LOTE_ABIERTO) {
                conexion.conectar();
                ResultSet rs = conexion.CONSULTAR("SELECT estado FROM entrada WHERE identrada=" + IDENTRADA + "  ");
                if (rs.next()) {
                    if (!rs.getBoolean("estado")) {
                        modelo.Metodos.M("EL PERSONAL DE ALMACEN AUN NO A TERMINADO DE VERIFICAR ESTE LOTE\n", "advertencia.png");
                        return;
                    }
                    String reporte = "ENTRADADETRAFOS";
                    if (checkNuevos.isSelected()) {
                        reporte = "ENTRADADETRAFOS_NUEVOS";
                    }
                    JasperReport jasper = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/" + reporte + ".jasper").toString()));
                    Map<String, Object> p = new HashMap<String, Object>();
                    JasperPrint jasperprint = null;
                    p.put("IDENTRADA", IDENTRADA);
                    p.put("ORDEN", checkOrdenar.isSelected());
                    jasperprint = JasperFillManager.fillReport(jasper, p, conexion.conectar());
                    JasperViewer.viewReport(jasperprint, false);
                }
            } else {
                modelo.Metodos.M("NO HAY NINGUN LOTE ABIERTO PARA GENERAR LOS FORMATOS\n", "advertencia.png");
            }
        } catch (Exception ex) {
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
            Metodos.ERROR(ex, "ERROR");
        }
    }//GEN-LAST:event_subMenuImprimirEntradaDeTrafosActionPerformed

    private void subMenuExportarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuExportarExcelActionPerformed
        modelo.Metodos.JTableToExcel(tablaTrafos, btnImprimrFormatos);
    }//GEN-LAST:event_subMenuExportarExcelActionPerformed

    private void subMenuImprimirMuestrasDeAceiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuImprimirMuestrasDeAceiteActionPerformed
        try {
            DialogoImprimirAceite dia = new DialogoImprimirAceite(this, rootPaneCheckingEnabled);
            dia.setIdentrada(getIDENTRADA());
            this.setExtendedState(ICONIFIED);
            dia.setVisible(true);
        } catch (Exception e) {
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, e);
            Metodos.ERROR(e, "ERROR");
        }
    }//GEN-LAST:event_subMenuImprimirMuestrasDeAceiteActionPerformed

    private void subMenuImprimirOrdenDeProduccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuImprimirOrdenDeProduccionActionPerformed
        try {
            JasperReport reporte = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/ORDENDEPRODUCCION.jasper").toString()));
            Map<String, Object> p = new HashMap<>();

            p.put("IDLOTE", IDENTRADA);

            ArrayList lista = new ArrayList();

            lista = modelo.Metodos.getFirma("GERENTE TECNICO");
            p.put("FIRMA_GERENTE", modelo.Metodos.byteToBufferedImage((byte[]) lista.get(0)));
            p.put("NOMBRE_GERENTE", lista.get(1));

            lista = modelo.Metodos.getFirma("DIRECTOR DE PRODUCCION");
            p.put("FIRMA_PRODUCCION", modelo.Metodos.byteToBufferedImage((byte[]) lista.get(0)));
            p.put("NOMBRE_PRODUCCION", lista.get(1));

            lista = modelo.Metodos.getFirma("COORDINADORA ADMINISTRATIVA");
            p.put("FIRMA_ADMINISTRATIVA", modelo.Metodos.byteToBufferedImage((byte[]) lista.get(0)));
            p.put("NOMBRE_ADMINISTRATIVA", lista.get(1));

            lista = modelo.Metodos.getFirma("ALMACEN");
            p.put("NOMBRE_ALMACEN", lista.get(1));

            lista = modelo.Metodos.getFirma("COORDINADORA DEL SIG");
            p.put("FIRMA_SIG", modelo.Metodos.byteToBufferedImage((byte[]) lista.get(0)));
            p.put("NOMBRE_SIG", lista.get(1));

            JasperPrint jasperprint = null;
            jasperprint = JasperFillManager.fillReport(reporte, p, conexion.conectar());
            JasperViewer.viewReport(jasperprint, false);
        } catch (JRException | MalformedURLException ex) {
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
            modelo.Metodos.M("ERROR AL GENERAR LA ORDEN DE PRODUCCION\n" + ex, "error.png");
        }
    }//GEN-LAST:event_subMenuImprimirOrdenDeProduccionActionPerformed

    private void subMenuImprimirTodosLosFormatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuImprimirTodosLosFormatosActionPerformed
        try {
            JasperReport VALIDACION = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PRODUCCION/VALIDACION.jasper").toString()));
            JasperReport VERIFICACION = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PRODUCCION/VERIFICACION.jasper").toString()));
            JasperReport NUCLEOS = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PRODUCCION/PRUEBA DE NUCLEO.jasper").toString()));
            JasperReport BOBINA = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PRODUCCION/CALCULO DE BOBINA.jasper").toString()));
            JasperReport ENCUBE = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PRODUCCION/ENCUBE.jasper").toString()));
            JasperReport SECCIONES = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PRODUCCION/SECCIONES.jasper").toString()));
            JasperReport SEGUIMIENTO = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PRODUCCION/SEGUIMIENTO.jasper").toString()));
            JasperReport HERMETICIDAD = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PRODUCCION/HERMETICIDAD.jasper").toString()));
            JasperReport PINTURA = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PRODUCCION/PINTURA.jasper").toString()));

            Map<String, Object> p = new HashMap<>();
            p.put("IDENTRADA", getIDENTRADA());
            p.put("ORDEN", checkOrdenar.isSelected());

            Connection con = conexion.conectar();

            JasperPrint jp = JasperFillManager.fillReport(VALIDACION, p, con);

            unirPaginas(JasperFillManager.fillReport(VERIFICACION, p, con), jp);

            unirPaginas(JasperFillManager.fillReport(NUCLEOS, p, con), jp);

            unirPaginas(JasperFillManager.fillReport(BOBINA, p, con), jp);

            for (String seccione : secciones) {
                p.put("SECCION", seccione);
                unirPaginas(JasperFillManager.fillReport(SECCIONES, p, con), jp);
            }

            unirPaginas(JasperFillManager.fillReport(HERMETICIDAD, p, con), jp);
            unirPaginas(JasperFillManager.fillReport(PINTURA, p, con), jp);

            int ancho = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
            int alto = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

            Rectangle Barra = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

            JasperViewer jv1 = new JasperViewer(jp, false);
            jv1.setSize(ancho / 2, alto - (alto - Barra.height));
            jv1.setLocation(0, 0);
            jv1.setVisible(true);

            JasperPrint jp2 = JasperFillManager.fillReport(SEGUIMIENTO, p, con);
            unirPaginas(JasperFillManager.fillReport(ENCUBE, p, con), jp2);
            jv1 = new JasperViewer(jp2, false);
            jv1.setSize(ancho / 2, alto - (alto - Barra.height));
            jv1.setLocation(ancho / 2, 0);
            jv1.setVisible(true);
//            JasperViewer.viewReport(jp, false);
//            JasperViewer.viewReport(JasperFillManager.fillReport(SEGUIMIENTO, p, conexion.conectar()), false);

//            FileOutputStream fileOut = new FileOutputStream("MergeReportTemplate.pdf");
//            JRExporter exporter = new JRPdfExporter();
//            exporter.setParameter(JRPdfExporterParameter.FORCE_LINEBREAK_POLICY, Boolean.TRUE);
//            exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM,  fileOut);
//            exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT, jasperPrint);
//            exporter.exportReport();
//            fileOut.flush();
//            fileOut.close();
            //JRSaver.saveObject(dst, new java.io.File("hola.jasper"));
//            JasperExportManager.
//            byte[] pdf = generateReport(jasperPrintList);
//            OutputStream out = new FileOutputStream("SECCIONES.pdf");
//            out.write(pdf);
//            out.close();
//            Desktop.getDesktop ().open(new java.io.File("SECCIONES.pdf"));
        } catch (Exception ex) {
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
            modelo.Metodos.M("ERROR AL GENERAR EL REPORTE\n" + ex, "error.png");
        }
    }//GEN-LAST:event_subMenuImprimirTodosLosFormatosActionPerformed

    private void btnInsertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertarActionPerformed
        try {
            if (tablaTrafos.getRowCount() == 1) {
                String serie = modeloTabla.getValueAt(0, 4).toString();
                int veces = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el numero de item a insertar:", 0));
                if (veces > 0 && !serie.isEmpty()) {
                    int serien = Integer.parseInt(serie);
                    for (int i = 2; i <= veces; i++) {
                        modeloTabla.addRow(new Object[]{
                            "",
                            (i),//"N°",
                            "",//"REMISION",
                            "",//"N° EMPRESA",
                            (serien += 1),//"N° SEIRE",
                            modeloTabla.getValueAt(0, 5),//"MARCA",
                            modeloTabla.getValueAt(0, 6),//"KVA",
                            modeloTabla.getValueAt(0, 7),//"FASE",
                            modeloTabla.getValueAt(0, 8),//"TENSION P",
                            0,//"A.T",
                            0,//"B.T",
                            0,//"H.A",
                            0,//"H.B",
                            false,//"INT",
                            false,//"EXT",
                            "",//"HERRAJE",
                            modeloTabla.getValueAt(0, 16),//"AÑO",
                            modeloTabla.getValueAt(0, 17),//"PESO",
                            modeloTabla.getValueAt(0, 18),//"ACEITE",
                            "",//"OBSERVACION",
                            modeloTabla.getValueAt(0, 20),//"SERVICIO",
                            modeloTabla.getValueAt(0, 21),//"TIPO", 
                        });
                    }
                }
            } else {
                modelo.Metodos.M("Inserte sólo una fila con los datos principales del transformador.", "advertencia.png");
            }
        } catch (Exception e) {
            modelo.Metodos.M("ERROR\n" + e, "error.png");
        }
    }//GEN-LAST:event_btnInsertarActionPerformed

    private void imprimirHojasDeRutaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imprimirHojasDeRutaActionPerformed
        try {
            JasperReport reporte = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/HOJADERUTA_FRENTE.jasper").toString()));
            Map<String, Object> p = new HashMap<>();
            p.put("IDENTRADA", getIDENTRADA());
            JasperPrint jp = JasperFillManager.fillReport(reporte, p, conexion.conectar());

            int ancho = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
            int alto = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

            Rectangle Barra = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

            JasperViewer jv1 = new JasperViewer(jp, false);
            jv1.setSize(ancho / 2, alto - (alto - Barra.height));
            jv1.setLocation(0, 0);
            jv1.setVisible(true);

            jv1 = new JasperViewer(JasperFillManager.fillReport(
                    (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/HOJADERUTA_REVES.jasper").toString())), null, new JREmptyDataSource()), false);
            jv1.setSize(ancho / 2, alto - (alto - Barra.height));
            jv1.setLocation(ancho / 2, 0);
            jv1.setVisible(true);

        } catch (MalformedURLException | JRException ex) {
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_imprimirHojasDeRutaActionPerformed

    private void subMenuRegistrarObservacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuRegistrarObservacionActionPerformed

        Dialogos.RegistrarDiferencia rd = new RegistrarDiferencia(this, true);
        try {
            rd.setIdtransformador((int) modeloTabla.getValueAt(rowSorter.convertRowIndexToModel(tablaTrafos.getSelectedRow()), 0));
            rd.cargarDiferencia();
        } catch (SQLException ex) {
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
        }
        rd.setVisible(true);
    }//GEN-LAST:event_subMenuRegistrarObservacionActionPerformed

    private void tarjetaDeAprobacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tarjetaDeAprobacionActionPerformed
        try {
            JasperReport reporte = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/TARJETA_ALMACEN.jasper").toString()));
            Map<String, Object> p = new HashMap<>();
            p.put("IDENTRADA", getIDENTRADA());
            JasperPrint jp = JasperFillManager.fillReport(reporte, p, conexion.conectar());

            JasperViewer jv1 = new JasperViewer(jp, false);
            jv1.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tarjetaDeAprobacionActionPerformed

    private void consumoDeAceiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_consumoDeAceiteActionPerformed
        String sql = "select * from transformador \n"
                + "where identrada=" + getIDENTRADA() + " and serviciosalida!='DADO DE BAJA';";
        conexion = new ConexionBD();
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(sql);
                
        try {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(new File("PLANTILLAS EXCEL\\CONSUMO DE ACEITE.xlsx")));
            XSSFSheet hoja = wb.getSheetAt(0);
            XSSFRow fila = null;
            File f = File.createTempFile("CONSUMO DE ACEITE",".xlsx");
            FileOutputStream fileOut = new FileOutputStream(f);
            int posicionFila = 9;
            while(rs.next()){
                fila = hoja.createRow(posicionFila);
                
                fila.createCell(0).setCellValue(rs.getRow());
                fila.createCell(1).setCellValue(rs.getInt("fase"));
                fila.createCell(2).setCellValue(rs.getString("numeroempresa"));
                fila.createCell(3).setCellValue(rs.getString("numeroserie"));
                fila.createCell(4).setCellValue(rs.getString("marca"));
                fila.createCell(5).setCellValue(rs.getDouble("kvasalida"));
                fila.createCell(6).setCellValue(rs.getString("tipotrafosalida"));
                fila.createCell(7).setCellValue(rs.getString("serviciosalida"));
                fila.createCell(8).setCellValue( new BigDecimal((rs.getInt("aceite")/3.785)).setScale(1, RoundingMode.HALF_EVEN).doubleValue() );
                
                posicionFila++;
            }            
            
            wb.write(fileOut);
            fileOut.close();
            Desktop.getDesktop().open(f);
        } catch (IOException | SQLException ex) {
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            conexion.CERRAR();
        }
    }//GEN-LAST:event_consumoDeAceiteActionPerformed

    private void unirPaginas(JasperPrint source, JasperPrint dst) {
        List<JRPrintPage> pages = source.getPages();
        pages.stream().forEach((page) -> {
            dst.addPage(page);
        });
    }

    public byte[] generateReport(List<JasperPrint> jasperPrintList) {
        //throw the JasperPrint Objects in a list
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        //Add the list as a Parameter
        exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
        //this will make a bookmark in the exported PDF for each of the reports
        exporter.setParameter(JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS, Boolean.TRUE);
//      exporter.setParameter(JRPdfExporterParameter.IS_COMPRESSED, Boolean.TRUE);      
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE, new java.io.File("ya.jasper"));
        try {
            exporter.exportReport();
        } catch (JRException ex) {
            Logger.getLogger(EntradaDeTrafos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return baos.toByteArray();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EntradaDeTrafos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> {
            new EntradaDeTrafos().setVisible(true);
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    public javax.swing.JButton btnAgregarFila;
    public javax.swing.JButton btnBorrarFila;
    public javax.swing.JButton btnGuardar;
    public javax.swing.JButton btnImprimrFormatos;
    public javax.swing.JButton btnInsertar;
    private javax.swing.JCheckBox checkNuevos;
    public javax.swing.JCheckBox checkOrdenar;
    public CompuChiqui.JTextFieldPopup cjBuscarSerie;
    public CompuChiqui.JTextFieldPopup cjCentroDeCostos;
    public CompuChiqui.JTextFieldPopup cjContrato;
    public com.toedter.calendar.JDateChooser cjFechaRecepcion;
    public CompuChiqui.JTextFieldPopup cjIdEntradaAlmacen;
    public CompuChiqui.JTextFieldPopup cjLote;
    public CompuChiqui.JCTextArea cjObservaciones;
    public CompuChiqui.JTextFieldPopup cjOp;
    public CompuChiqui.JTextFieldPopup cjPlacaVehiculo;
    public CompuChiqui.JTextFieldPopup cjrepresentante;
    public javax.swing.JComboBox<Ciudad> comboCiudad;
    public javax.swing.JComboBox<Cliente> comboCliente;
    public javax.swing.JComboBox<Conductor> comboConductor;
    private javax.swing.JMenuItem consumoDeAceite;
    private javax.swing.JMenuItem imprimirHojasDeRuta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    public javax.swing.JLabel lblFilasSeleccionadas;
    public javax.swing.JMenu menuEditar;
    public javax.swing.JMenuItem menuItemRegistrarDiferencias;
    public static javax.swing.JMenuItem menuItemTensiones;
    private javax.swing.JPanel panelTabla;
    public javax.swing.JPopupMenu subMenuEntradaDeTrafos;
    public javax.swing.JMenuItem subMenuExportarExcel;
    public javax.swing.JMenuItem subMenuImprimirEntradaDeAlmacen;
    public javax.swing.JMenuItem subMenuImprimirEntradaDeTrafos;
    public javax.swing.JPopupMenu subMenuImprimirFormatos;
    public javax.swing.JMenuItem subMenuImprimirMuestrasDeAceite;
    public javax.swing.JMenuItem subMenuImprimirOrdenDeProduccion;
    public javax.swing.JMenuItem subMenuImprimirTodosLosFormatos;
    public javax.swing.JMenuItem subMenuItemEliminarTrafo;
    private javax.swing.JMenuItem subMenuRegistrarObservacion;
    public javax.swing.JTable tablaTrafos;
    private javax.swing.JMenuItem tarjetaDeAprobacion;
    // End of variables declaration//GEN-END:variables

    public int getIDENTRADA() {
        return IDENTRADA;
    }

    public void setIDENTRADA(int IDENTRADA) {
        this.IDENTRADA = IDENTRADA;
    }

}
