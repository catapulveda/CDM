package view2;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import modelo.Ciudad;
import modelo.Cliente;
import modelo.Conductor;
import modelo.ConexionBD;
import modelo.Metodos;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

 public class DialogoImprimirRemisionTrafos1 extends javax.swing.JDialog{

   private int IDDESPACHO = -1, ID_REMISION = -1;
   
   private boolean ACTUALIZANDO = false;
   
   ConexionBD conexion = new ConexionBD();
   
   modelo.Sesion sesion = modelo.Sesion.getConfigurador(null, -1);
   
   private String contrato = "";
   private String centrodecostos = "";
   private String cliente = "";
   private String SERVICIO = "";
   
    public DialogoImprimirRemisionTrafos1(java.awt.Frame parent, boolean modal){
        super(parent, modal);
        initComponents();
        
        cjRemision.setText(""+modelo.Metodos.getConsecutivoRemision("cdmsinretorno", false));        
        
        cjFecha.setDate(new java.util.Date());
    }
    
    public void cargarEncabezado(ResultSet rs){
       try{
           cjRemision.setText(rs.getString("numero_remision"));
           cjConductor.setText(rs.getString("conductor_remision"));
           cjCedula.setText(rs.getString("cedula_remision"));
           cjPlaca.setText(rs.getString("placa_remision"));
           cjCiudad.setText(rs.getString("ciudad_remision"));
           cjDireccion.setText(rs.getString("destino_remision"));
           cjTelefono.setText(rs.getString("telefono_remision"));
           cjFactura.setText(rs.getString("factura_numero"));
           cjFecha.setDate(rs.getDate("fecha_remision"));
           cjObservaciones.setText(rs.getString("descripcion_remision"));
           contrato = rs.getString("contrato");
           centrodecostos = rs.getString("centrodecostos");
           cliente = rs.getString("nombrecliente");
       }catch(SQLException ex){
           Logger.getLogger(DialogoImprimirRemisionTrafos1.class.getName()).log(Level.SEVERE, null, ex);
       }
    }
    
    public void cargarObservaciones(ResultSet rs){
       try {
           if(getSERVICIO().equals("DADO DE BAJA")){
               cjObservaciones.setText("* "+rs.getInt("count")+" TRANSFORMADORES ENTREGADOS.\n");
           }else{
               cjObservaciones.setText("* "+rs.getInt("count")+" TRANSFORMADORES ENTREGADOS EN PERFECTAS CONDICIONES.\n");
           }
           cjObservaciones.setText(cjObservaciones.getText()+"* DESPACHO Nº "+rs.getString("nodespacho")+".");
           lblServicios.setText("SERVICIO: "+getSERVICIO());
       } catch (SQLException ex) {
           Logger.getLogger(DialogoImprimirRemisionTrafos1.class.getName()).log(Level.SEVERE, null, ex);
       }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cjRemision = new CompuChiqui.JTextFieldPopup();
        jLabel3 = new javax.swing.JLabel();
        cjConductor = new CompuChiqui.JTextFieldPopup();
        jLabel4 = new javax.swing.JLabel();
        cjCedula = new CompuChiqui.JTextFieldPopup();
        jLabel12 = new javax.swing.JLabel();
        cjPlaca = new CompuChiqui.JTextFieldPopup();
        jLabel6 = new javax.swing.JLabel();
        cjCiudad = new CompuChiqui.JTextFieldPopup();
        jLabel8 = new javax.swing.JLabel();
        cjTelefono = new CompuChiqui.JTextFieldPopup();
        jLabel7 = new javax.swing.JLabel();
        cjDireccion = new CompuChiqui.JTextFieldPopup();
        jLabel9 = new javax.swing.JLabel();
        cjFactura = new CompuChiqui.JTextFieldPopup();
        jLabel10 = new javax.swing.JLabel();
        cjFecha = new com.toedter.calendar.JDateChooser();
        btnAgregarCiudad = new javax.swing.JButton();
        btnActualizarConsecutivo = new javax.swing.JButton();
        btnAgregarConductor = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        cjObservaciones = new javax.swing.JTextPane();
        btnImprimir1 = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        lblServicios = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        cjcontrato = new CompuChiqui.JTextFieldPopup();
        jLabel15 = new javax.swing.JLabel();
        cjcentrodecostos = new CompuChiqui.JTextFieldPopup();
        jLabel13 = new javax.swing.JLabel();
        comboCliente = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        cjresponsable = new CompuChiqui.JTextFieldPopup();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Numero Remision:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(jLabel2, gridBagConstraints);

        cjRemision.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 0);
        jPanel3.add(cjRemision, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Conductor:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel3.add(jLabel3, gridBagConstraints);

        cjConductor.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        jPanel3.add(cjConductor, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Responsale:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel4, gridBagConstraints);

        cjCedula.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        jPanel3.add(cjCedula, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Placa vehiculo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(jLabel12, gridBagConstraints);

        cjPlaca.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        jPanel3.add(cjPlaca, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Ciudad:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel6, gridBagConstraints);

        cjCiudad.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        jPanel3.add(cjCiudad, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Telefono:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel8, gridBagConstraints);

        cjTelefono.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        jPanel3.add(cjTelefono, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Direccion:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel7, gridBagConstraints);

        cjDireccion.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        jPanel3.add(cjDireccion, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Factura:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel9, gridBagConstraints);

        cjFactura.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        jPanel3.add(cjFactura, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Fecha:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel10, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.ipadx = 70;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        jPanel3.add(cjFecha, gridBagConstraints);

        btnAgregarCiudad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/ver.png"))); // NOI18N
        btnAgregarCiudad.setToolTipText("Actualizar consecutivo");
        btnAgregarCiudad.setFocusable(false);
        btnAgregarCiudad.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAgregarCiudad.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAgregarCiudad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarCiudadActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipadx = -20;
        jPanel3.add(btnAgregarCiudad, gridBagConstraints);

        btnActualizarConsecutivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/actualizar.png"))); // NOI18N
        btnActualizarConsecutivo.setToolTipText("Actualizar consecutivo");
        btnActualizarConsecutivo.setFocusable(false);
        btnActualizarConsecutivo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnActualizarConsecutivo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnActualizarConsecutivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarConsecutivoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = -20;
        jPanel3.add(btnActualizarConsecutivo, gridBagConstraints);

        btnAgregarConductor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/ver.png"))); // NOI18N
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = -20;
        jPanel3.add(btnAgregarConductor, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Observaciones");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(jLabel11, gridBagConstraints);

        jScrollPane2.setViewportView(cjObservaciones);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        jPanel3.add(jScrollPane2, gridBagConstraints);

        btnImprimir1.setBackground(new java.awt.Color(255, 255, 255));
        btnImprimir1.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnImprimir1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        btnImprimir1.setText("Salir");
        btnImprimir1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimir1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(btnImprimir1, gridBagConstraints);

        btnImprimir.setBackground(new java.awt.Color(255, 255, 255));
        btnImprimir.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/imprimir.png"))); // NOI18N
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        jPanel3.add(btnImprimir, gridBagConstraints);

        lblServicios.setFont(new java.awt.Font("Enter Sansman", 0, 14)); // NOI18N
        lblServicios.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblServicios.setText("ddd");
        lblServicios.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        jPanel3.add(lblServicios, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Contrato:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel5, gridBagConstraints);

        jLabel14.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Centro de Costos:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel14, gridBagConstraints);

        cjcontrato.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        jPanel3.add(cjcontrato, gridBagConstraints);

        jLabel15.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Cedula:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel15, gridBagConstraints);

        cjcentrodecostos.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        jPanel3.add(cjcentrodecostos, gridBagConstraints);

        jLabel13.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Cliente:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel13, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        jPanel3.add(comboCliente, gridBagConstraints);

        jLabel16.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Cliente:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel16, gridBagConstraints);

        cjresponsable.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        jPanel3.add(cjresponsable, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        
//        (new Thread(){
//            public void run(){
                try{
                    btnImprimir.setIcon(new ImageIcon(getClass().getResource("/recursos/images/gif.gif")));
                    btnImprimir.setEnabled(false);
                    String remision = cjRemision.getText().trim();
                    String Cliente = comboCliente.getSelectedItem().toString();
                    int idcliente = ((Cliente)comboCliente.getModel().getSelectedItem()).getIdCliente();
                    String Centrodecostos = cjcentrodecostos.getText().trim();
                    String Contrato = cjcontrato.getText().trim();
                    String conductor = cjConductor.getText().trim();
                    String cedula = cjCedula.getText().trim();
                    String placa = cjPlaca.getText().trim();
                    String ciudad = cjCiudad.getText().trim();
                    String direccion = cjDireccion.getText().trim();
                    String telefono = cjTelefono.getText().trim();
                    String factura = cjFactura.getText().trim();
                    String responsable = cjresponsable.getText().trim();
                    java.util.Date fecha = cjFecha.getDate();
                    String observaciones = cjObservaciones.getText();

                    ResultSet rs = null;
                    String sql = "";
                            if(isACTUALIZANDO()){
                                sql = " UPDATE remision SET ";
                                sql += " cliente_remision='"+Cliente+"' , ciudad_remision='"+ciudad+"' , ";
                                sql += " destino_remision='"+direccion+"' , telefono_remision='"+telefono+"' , ";
                                sql += " contrato_remision='"+Contrato+"' , centrodecostos_remision='"+Centrodecostos+"' , ";
                                sql += " conductor_remision='"+conductor+"' , cedula_remision='"+cedula+"' , ";
                                sql += " placa_remision='"+placa+"' , fecha_remision='"+fecha+"' , tipo_remision='SIN RETORNO' , ";
                                sql += " servicio_remision='"+getSERVICIO()+"' , descripcion_remision='"+observaciones+"' , ";
                                sql += " factura_numero='"+factura+"' , idusuario="+sesion.getIdUsuario()+", idcliente="+idcliente+" , ";
                                sql += " responsable='"+responsable+"' ";
                                sql += " WHERE idremision="+getIDREMISION()+" ";
                            }else{
                                sql = " INSERT INTO remision (iddespacho, numero_remision, cliente_remision, ";
                                sql += "  ciudad_remision, destino_remision, telefono_remision, contrato_remision,  ";
                                sql += " centrodecostos_remision, conductor_remision, cedula_remision, placa_remision, ";
                                sql += " fecha_remision, tipo_remision, servicio_remision, descripcion_remision,  ";
                                sql += " factura_numero, empresa_remision, estado, fechacreacion, idusuario, idcliente, responsable ) ";
                                sql += " VALUES ( "+getIDDESPACHO()+" , '"+remision+"' , '"+Cliente+"' , '"+ciudad+"' , ";
                                sql += " '"+direccion+"' , '"+telefono+"' , '"+Contrato+"' , '"+Centrodecostos+"' , ";
                                sql += " '"+conductor+"' , '"+cedula+"' , '"+placa+"' , '"+fecha+"' , 'SIN RETORNO' , ";
                                sql += " '"+getSERVICIO()+"' , '"+observaciones+"' , '"+factura+"' ,  ";
                                sql += " 'CDM' , 'TRUE' , '"+new java.util.Date()+"' , "+sesion.getIdUsuario()+" , ";
                                sql += " "+idcliente+" , '"+responsable+"' ) ";
                            }

                            if(conexion.GUARDAR(sql)){

                                int IDREMISION = (isACTUALIZANDO())?getIDREMISION():modelo.Metodos.getUltimoID("remision", "idremision");

                                if(!isACTUALIZANDO()){
                                    int nremision = modelo.Metodos.getConsecutivoRemision("cdmsinretorno",true);
                                    cjRemision.setText(""+nremision);
                                    remision = ""+nremision;                            
                                }                        

                                Map<String, Object> p = new HashMap<>();
                                p.put("IDREMISION", IDREMISION);
                                JasperReport reporte = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/REMISIONES_TRAFOS.jasper").toString()));
                                JasperPrint reporte_view = JasperFillManager.fillReport(reporte, p, conexion.conectar());
                                JasperViewer jviewer = new JasperViewer(reporte_view, false);
                                jviewer.setTitle("Remision No " + remision);
                                jviewer.setVisible(true);                                
                            }
                }catch(Exception e){
                    Logger.getLogger(DialogoImprimirRemisionTrafos1.class.getName()).log(Level.SEVERE, null, e);
                    Metodos.ERROR(e, "ERROR AL INTENTAR GENERAR LA REMISION DE LOS TRANSFORMADORES.");
                }finally{
                    btnImprimir.setIcon(new ImageIcon(getClass().getResource("/recursos/images/imprimir.png")));
                    btnImprimir.setEnabled(true);
                    hide();
                }
//            }
//        }).start();
        
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnActualizarConsecutivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarConsecutivoActionPerformed
        cjRemision.setText(""+modelo.Metodos.getConsecutivoRemision("cdmsinretorno", false));        
    }//GEN-LAST:event_btnActualizarConsecutivoActionPerformed

    private void btnImprimir1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimir1ActionPerformed
        dispose();
    }//GEN-LAST:event_btnImprimir1ActionPerformed

    private void btnAgregarConductorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarConductorActionPerformed
        JComboBox<Conductor> conductores = new JComboBox<>();
        conductores.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        conductores.setUI(JComboBoxColor.JComboBoxColor.createUI(conductores));
        conductores.setMaximumRowCount(20);
        conductores.addItem(new Conductor(-1, "SELECCIONE...", "SIN CEDULA"));
        modelo.Conductor.llenarComboConductores(conductores);
        JOptionPane.showMessageDialog(this, conductores, "Seleccione un conductor", JOptionPane.DEFAULT_OPTION, new ImageIcon(getClass().getResource("/recursos/images/conductor.png")));
        if(conductores.getSelectedIndex()>0){
            Conductor c = conductores.getItemAt(conductores.getSelectedIndex());
            cjConductor.setText(c.getNombreConductor());
            cjCedula.setText(c.getCedulaConductor());
        }        
    }//GEN-LAST:event_btnAgregarConductorActionPerformed

    private void btnAgregarCiudadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarCiudadActionPerformed
        JComboBox<Ciudad> ciudades = new JComboBox<>();
        ciudades.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        ciudades.setUI(JComboBoxColor.JComboBoxColor.createUI(ciudades));
        ciudades.setMaximumRowCount(20);
        ciudades.addItem(new Ciudad(-1, "Seleccione...", "", ""));
        modelo.Ciudad.cargarComboNombreCiudades(ciudades);
        JOptionPane.showMessageDialog(this, ciudades, "Seleccione una ciudad", JOptionPane.DEFAULT_OPTION, new ImageIcon(getClass().getResource("/recursos/images/conductor.png")));
        if(ciudades.getSelectedIndex()>0){
            Ciudad c = ciudades.getItemAt(ciudades.getSelectedIndex());
            cjCiudad.setText(c.getNombreCiudad());
            cjDireccion.setText(c.getDireccionCiudad());
            cjTelefono.setText(c.getTelefonoCiudad());
        }
    }//GEN-LAST:event_btnAgregarCiudadActionPerformed

//    public static void main(String args[]){
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()){
//                if ("Windows".equals(info.getName())){
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(DialogoImprimirRemisionTrafos1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        java.awt.EventQueue.invokeLater(new Runnable(){
//            @Override
//            public void run(){
//                DialogoImprimirRemisionTrafos1 dialog = new DialogoImprimirRemisionTrafos1(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter(){
//                    @Override
//                    public void windowClosing(java.awt.event.WindowEvent e){
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnActualizarConsecutivo;
    public javax.swing.JButton btnAgregarCiudad;
    public javax.swing.JButton btnAgregarConductor;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnImprimir1;
    private CompuChiqui.JTextFieldPopup cjCedula;
    private CompuChiqui.JTextFieldPopup cjCiudad;
    private CompuChiqui.JTextFieldPopup cjConductor;
    private CompuChiqui.JTextFieldPopup cjDireccion;
    private CompuChiqui.JTextFieldPopup cjFactura;
    private com.toedter.calendar.JDateChooser cjFecha;
    private javax.swing.JTextPane cjObservaciones;
    private CompuChiqui.JTextFieldPopup cjPlaca;
    private CompuChiqui.JTextFieldPopup cjRemision;
    private CompuChiqui.JTextFieldPopup cjTelefono;
    private CompuChiqui.JTextFieldPopup cjcentrodecostos;
    private CompuChiqui.JTextFieldPopup cjcontrato;
    private CompuChiqui.JTextFieldPopup cjresponsable;
    private javax.swing.JComboBox<Cliente> comboCliente;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblServicios;
    // End of variables declaration//GEN-END:variables

    public int getIDDESPACHO() {
        return IDDESPACHO;
    }

    public void setIDDESPACHO(int IDDESPACHO) {
        this.IDDESPACHO = IDDESPACHO;
        String sql = "SELECT e.centrodecostos, e.contrato, c.nombrecliente, c.idcliente, c.nitcliente FROM transformador t\n" +
        "INNER JOIN despacho d ON d.iddespacho=t.iddespacho\n" +
        "INNER JOIN entrada e ON e.identrada=t.identrada\n" +
        "INNER JOIN cliente c ON c.idcliente=d.idcliente\n" +
        "WHERE d.iddespacho="+this.IDDESPACHO+" LIMIT 1; ";
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(sql);
        try {
           if(rs.next()){
               cjcentrodecostos.setText(rs.getString("centrodecostos"));
               cjcontrato.setText(rs.getString("contrato"));
               //cjcliente.setText(rs.getString("nombrecliente"));
               comboCliente.addItem(new Cliente(rs.getInt("idcliente"), rs.getString("nombrecliente"), rs.getString("nitcliente")));
           }
        } catch (SQLException ex) {
           Logger.getLogger(DialogoImprimirRemisionTrafos1.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
           conexion.CERRAR();
        }
    }

    public boolean isACTUALIZANDO() {
        return ACTUALIZANDO;
    }

    public void setACTUALIZANDO(boolean ACTUALIZANDO) {
        this.ACTUALIZANDO = ACTUALIZANDO;
    }

    public int getIDREMISION() {
        return ID_REMISION;
    }

    public void setIDREMISION(int IDREMISION) {
        this.ID_REMISION = IDREMISION;
        String sql = "SELECT * FROM remision r INNER JOIN cliente c ON c.idcliente=r.idcliente WHERE idremision="+this.ID_REMISION;
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(sql);
       try {
           if(rs.next()){
               cjRemision.setText(rs.getString("numero_remision"));
               //comboCliente.getModel().setSelectedItem(new Cliente(rs.getInt("idcliente"), rs.getString("nombrecliente"), rs.getString("nitcliente")));
               comboCliente.addItem(new Cliente(rs.getInt("idcliente"), rs.getString("nombrecliente"), rs.getString("nitcliente")));
               cjresponsable.setText(rs.getString("responsable"));
               //cjcliente.setText(rs.getString("cliente_remision"));
               cjcontrato.setText(rs.getString("contrato_remision"));
               cjcentrodecostos.setText(rs.getString("centrodecostos_remision"));
               cjConductor.setText(rs.getString("conductor_remision"));
               cjCedula.setText(rs.getString("cedula_remision"));
               cjPlaca.setText(rs.getString("placa_remision"));
               cjCiudad.setText(rs.getString("ciudad_remision"));
               cjDireccion.setText(rs.getString("destino_remision"));
               cjTelefono.setText(rs.getString("telefono_remision"));
               cjFactura.setText(rs.getString("factura_numero"));
               cjFecha.setDate(rs.getDate("fecha_remision"));
               cjObservaciones.setText(rs.getString("descripcion_remision"));
               lblServicios.setText(rs.getString("servicio_remision"));
               System.out.println(rs.getString("servicio_remision"));
           }
       } catch (SQLException ex) {
           Logger.getLogger(DialogoImprimirRemisionTrafos1.class.getName()).log(Level.SEVERE, null, ex);
           Metodos.M("ERROR AL ASIGNAR EL ID DE LA REMISION", "error.png");
       }finally{
           conexion.CERRAR();
       }       
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
        cjcontrato.setText(contrato);
    }

    public String getCentrodecostos() {
        return centrodecostos;
    }

    public void setCentrodecostos(String centrodecostos) {
        this.centrodecostos = centrodecostos;
        cjcentrodecostos.setText(this.centrodecostos);
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
//        cjcliente.setText(cliente);
    }

    public String getSERVICIO() {
        return SERVICIO;
    }

    public void setSERVICIO(String SERVICIO) {
        this.SERVICIO = SERVICIO;
        lblServicios.setText("SERVICIO: "+this.SERVICIO);
        String sql = "SELECT count(t.*), d.nodespacho from despacho d "
                + "INNER JOIN transformador t USING(iddespacho) "
                + "WHERE d.iddespacho="+getIDDESPACHO()
                +" AND "+((this.SERVICIO.equals("REPARACION")||this.SERVICIO.equals("MANTENIMIENTO"))?"(t.serviciosalida='REPARACION' OR t.serviciosalida='MANTENIMIENTO')":"t.serviciosalida='"+this.SERVICIO+"' ")
                + " GROUP BY d.nodespacho";
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(sql);
        try {
            if(rs.next()){
                if(this.SERVICIO.equals("DADO DE BAJA")){
                   cjObservaciones.setText("* "+rs.getInt("count")+" TRANSFORMADORES ENTREGADOS.\n");
                }else{
                   cjObservaciones.setText("* "+rs.getInt("count")+" TRANSFORMADORES ENTREGADOS EN PERFECTAS CONDICIONES.\n");
                }
                cjObservaciones.setText(cjObservaciones.getText()+"* DESPACHO Nº "+rs.getString("nodespacho")+".");
           }
        } catch (SQLException ex) {
           Metodos.ERROR(ex, "NO SE PUDO CARGAR LAS OBSERVACIONES");
        }
    }
}
