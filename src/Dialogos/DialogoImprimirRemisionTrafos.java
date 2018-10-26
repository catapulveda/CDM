package Dialogos;

import Animacion.Fade;
import java.awt.Frame;
import java.awt.event.ItemEvent;
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
import modelo.Conductor;
import modelo.ConexionBD;
import modelo.Metodos;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

 public class DialogoImprimirRemisionTrafos extends javax.swing.JDialog{

   private int IDDESPACHO = -1, ID_REMISION = -1;
   
   private boolean ACTUALIZANDO = false;
   
   ConexionBD conexion = new ConexionBD();
   
   modelo.Sesion sesion = modelo.Sesion.getConfigurador(null, -1);
   
   private String contrato = "";
   private String centrodecostos = "";
   private String cliente = "";
   private String SERVICIO = "";
   
    public DialogoImprimirRemisionTrafos(java.awt.Frame parent, boolean modal){
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
           Logger.getLogger(DialogoImprimirRemisionTrafos.class.getName()).log(Level.SEVERE, null, ex);
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
           Logger.getLogger(DialogoImprimirRemisionTrafos.class.getName()).log(Level.SEVERE, null, ex);
       }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cjRemision = new CompuChiqui.JTextFieldPopup();
        jPanel2 = new javax.swing.JPanel();
        btnImprimir = new javax.swing.JButton();
        btnImprimir1 = new javax.swing.JButton();
        lblServicios = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cjCiudad = new CompuChiqui.JTextFieldPopup();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cjFactura = new CompuChiqui.JTextFieldPopup();
        jLabel10 = new javax.swing.JLabel();
        cjFecha = new com.toedter.calendar.JDateChooser();
        btnActualizarConsecutivo = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        cjObservaciones = new javax.swing.JTextPane();
        jLabel7 = new javax.swing.JLabel();
        cjTelefono = new CompuChiqui.JTextFieldPopup();
        cjDireccion = new CompuChiqui.JTextFieldPopup();
        jLabel12 = new javax.swing.JLabel();
        cjPlaca = new CompuChiqui.JTextFieldPopup();
        cjConductor = new CompuChiqui.JTextFieldPopup();
        jLabel4 = new javax.swing.JLabel();
        cjCedula = new CompuChiqui.JTextFieldPopup();
        btnAgregarConductor = new javax.swing.JButton();
        btnAgregarCiudad = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Enter Sansman", 0, 14)); // NOI18N
        jLabel1.setText("Informacion de destino:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Numero Remision:");

        cjRemision.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnImprimir.setBackground(new java.awt.Color(255, 255, 255));
        btnImprimir.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/imprimir.png"))); // NOI18N
        btnImprimir.setText("Imprimir");
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnImprimir1.setBackground(new java.awt.Color(255, 255, 255));
        btnImprimir1.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnImprimir1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        btnImprimir1.setText("Salir");
        btnImprimir1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimir1ActionPerformed(evt);
            }
        });

        lblServicios.setFont(new java.awt.Font("Enter Sansman", 0, 14)); // NOI18N
        lblServicios.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnImprimir1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblServicios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImprimir)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnImprimir)
                        .addComponent(btnImprimir1))
                    .addComponent(lblServicios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Conductor:");

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Ciudad:");

        cjCiudad.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Telefono:");

        jLabel9.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Factura:");

        cjFactura.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Fecha:");

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

        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Observaciones");

        jScrollPane2.setViewportView(cjObservaciones);

        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Direccion:");

        cjTelefono.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N

        cjDireccion.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N

        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Placa vehiculo:");

        cjPlaca.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N

        cjConductor.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Cedula:");

        cjCedula.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N

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

        btnAgregarCiudad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/add.png"))); // NOI18N
        btnAgregarCiudad.setToolTipText("Actualizar consecutivo");
        btnAgregarCiudad.setFocusable(false);
        btnAgregarCiudad.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAgregarCiudad.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAgregarCiudad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarCiudadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cjRemision, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnActualizarConsecutivo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cjFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cjFecha, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                    .addComponent(cjDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cjCedula, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(cjCiudad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAgregarCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cjTelefono, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cjPlaca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cjConductor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAgregarConductor, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(cjRemision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnActualizarConsecutivo, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(cjConductor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAgregarConductor, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cjCedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cjPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(cjTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cjCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8))
                    .addComponent(btnAgregarCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(cjDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(cjFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10))
                    .addComponent(cjFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    String conductor = cjConductor.getText().trim();
                    String cedula = cjCedula.getText().trim();
                    String placa = cjPlaca.getText().trim();
                    String ciudad = cjCiudad.getText().trim();
                    String direccion = cjDireccion.getText().trim();
                    String telefono = cjTelefono.getText().trim();
                    String factura = cjFactura.getText().trim();
                    java.util.Date fecha = cjFecha.getDate();
                    String observaciones = cjObservaciones.getText();

                    ResultSet rs = null;
                    String sql = "";
                    if(isACTUALIZANDO()){
                        sql = " UPDATE remision SET ";
                        sql += " cliente_remision='"+getCliente()+"' , ciudad_remision='"+ciudad+"' , ";
                        sql += " destino_remision='"+direccion+"' , telefono_remision='"+telefono+"' , ";
                        sql += " contrato_remision='"+getContrato()+"' , centrodecostos_remision='"+getCentrodecostos()+"' , ";
                        sql += " conductor_remision='"+conductor+"' , cedula_remision='"+cedula+"' , ";
                        sql += " placa_remision='"+placa+"' , fecha_remision='"+fecha+"' , tipo_remision='SIN RETORNO' , ";
                        sql += " servicio_remision='"+getSERVICIO()+"' , descripcion_remision='"+observaciones+"' , ";
                        sql += " factura_numero='"+factura+"' , idusuario="+sesion.getIdUsuario()+" ";
                        sql += " WHERE idremision="+getIDREMISION()+" ";
                    }else{
                        sql = " INSERT INTO remision (iddespacho, numero_remision, cliente_remision, ";
                        sql += "  ciudad_remision, destino_remision, telefono_remision, contrato_remision,  ";
                        sql += " centrodecostos_remision, conductor_remision, cedula_remision, placa_remision, ";
                        sql += " fecha_remision, tipo_remision, servicio_remision, descripcion_remision,  ";
                        sql += " factura_numero, empresa_remision, estado, fechacreacion, idusuario ) ";
                        sql += " VALUES ( "+getIDDESPACHO()+" , '"+remision+"' , '"+getCliente()+"' , '"+ciudad+"' , ";
                        sql += " '"+direccion+"' , '"+telefono+"' , '"+getContrato()+"' , '"+getCentrodecostos()+"' , ";
                        sql += " '"+conductor+"' , '"+cedula+"' , '"+placa+"' , '"+fecha+"' , 'SIN RETORNO' , ";
                        sql += " '"+getSERVICIO()+"' , '"+observaciones+"' , '"+factura+"' ,  ";
                        sql += " 'CDM' , 'TRUE' , '"+new java.util.Date()+"' , "+sesion.getIdUsuario()+" ) ";
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
                    Logger.getLogger(DialogoImprimirRemisionTrafos.class.getName()).log(Level.SEVERE, null, e);
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

    public static void main(String args[]){
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()){
                if ("Windows".equals(info.getName())){
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DialogoImprimirRemisionTrafos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                DialogoImprimirRemisionTrafos dialog = new DialogoImprimirRemisionTrafos(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter(){
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e){
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblServicios;
    // End of variables declaration//GEN-END:variables

    public int getIDDESPACHO() {
        return IDDESPACHO;
    }

    public void setIDDESPACHO(int IDDESPACHO) {
        this.IDDESPACHO = IDDESPACHO;
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
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getCentrodecostos() {
        return centrodecostos;
    }

    public void setCentrodecostos(String centrodecostos) {
        this.centrodecostos = centrodecostos;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getSERVICIO() {
        return SERVICIO;
    }

    public void setSERVICIO(String SERVICIO) {
        this.SERVICIO = SERVICIO;
    }
}
