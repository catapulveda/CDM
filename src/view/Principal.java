package view;

import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import javax.swing.ImageIcon;
import modelo.ConexionBD;

/**
 *
 * @author AUXPLANTA
 */
public class Principal extends javax.swing.JFrame {

    private PopupMenu popup = new PopupMenu();
    private Image image =new ImageIcon(getClass().getResource("/recursos/images/logo.png")).getImage() ;
    private final TrayIcon trayIcon = new TrayIcon(image, "CDM SOFTWARE", popup);
    
    static final ConexionBD conexion = new ConexionBD();    
    
    public Principal(){
        initComponents();
                
        System.out.println(new BigDecimal((24/3.785)).setScale(1, RoundingMode.HALF_EVEN).doubleValue());
            //REGISTRA LAS ENTRADAS O LOTES
//        ResultSet rs1 = modelo.Conexion2.CONSULTAR("SELECT * FROM entrada e, conductor c WHERE e.conductor=c.nombre_conductor ORDER BY e.identrada");
//        try {
//            String sql = " INSERT INTO entrada (identrada,idcliente,idciudad,idconductor, \n";
//            sql += " idusuario,identradaalmacen,nombrepc,lote,contrato,op,centrodecostos, \n";
//            sql += " fecharecepcion,fecharegistrado,fechaactualizado,estado,observacion,placavehiculo) VALUES \n ";
//            while(rs1.next()){
//                sql += " ('"+rs1.getString("identrada")+"' , '"+rs1.getString("idcliente")+"', ";
//                sql += " '"+rs1.getString("idciudad_entrada")+"' , '"+rs1.getString("id_conductor")+"' , ";
//                sql += " '1' , '"+rs1.getString("identradaalmacen")+"' , 'ALMACEN' , '"+rs1.getString("lote")+"' , ";
//                sql += " '"+rs1.getString("contratono")+"' , '"+rs1.getString("op")+"' , ";
//                sql += " '"+rs1.getString("centrodecosto")+"' , '"+rs1.getDate("fechaderecepcion")+"' , ";
//                sql += " '"+rs1.getDate("fechaderecepcion")+"' , '"+rs1.getDate("fechaderecepcion")+"' , ";
//                sql += " '"+rs1.getString("estadolote")+"' , '"+rs1.getString("observacion_entradaalmacen")+"' , ";
//                sql += " '"+rs1.getString("placa")+"' ),\n";
//            }
//            sql = sql.substring(0, sql.length()-2);
//            Clipboard system;
//            StringSelection stsel;
//            stsel  = new StringSelection(sql.toString());
//            system = Toolkit.getDefaultToolkit().getSystemClipboard();
//            system.setContents(stsel,stsel);
//            System.out.println(sql);
//            if(new modelo.ConexionBD().GUARDAR(sql)){
//                
//            }
//        }catch(SQLException ex){
//            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
//        }

//REGISTRA LOS TRANSFORMADORES
//    (new Thread(){
//            public void run(){
//                ResultSet rs = modelo.Conexion2.CONSULTAR("SELECT * FROM datosentrada");
//                try {
//                    String GUARDAR = " INSERT INTO transformador ( ";
//                    GUARDAR += " idtransformador, item, numeroempresa, numeroserie, marca, kvaentrada, kvasalida, fase, tpe, tse, ";
//                    GUARDAR += " tte, tps, tss, tts, aat, abt, hat, hbt, ci, ce, herraje, ano, ";
//                    GUARDAR += " peso, aceite, observacionentrada, observacionsalida, servicioentrada, serviciosalida, ";
//                    GUARDAR += " tipotrafoentrada, tipotrafosalida, estado, identrada, iddespacho, idremision ";
//                    GUARDAR += " ) VALUES ";
//                    while(rs.next()){
//                        //System.out.println(rs.getRow()+" "+rs.getString("tension_salida")+" "+rs.getString("noplaca"));
//                        area.setText(rs.getRow()+" "+rs.getString("tension_salida")+" "+rs.getString("noplaca"));
//                        GUARDAR += " ( "+rs.getInt("idtrafo")+" , ";
//                        GUARDAR += " '"+rs.getString("no")+"' , '"+rs.getString("noempresa")+"' , '"+rs.getString("noplaca")+"' , ";
//                        GUARDAR += " '"+rs.getString("marca")+"' , '"+rs.getString("kva")+"' , '"+rs.getString("kva_salida")+"' , ";
//                        GUARDAR += " '"+rs.getString("fase")+"' , '"+rs.getString("tension").split("/")[0]+"' , '"+rs.getString("tension").split("/")[1]+"' , ";
//                        GUARDAR += " '"+rs.getString("tension").split("/")[2]+"' , '"+rs.getString("tension_salida").split("/")[0]+"' ,  ";
//                        GUARDAR += " '"+rs.getString("tension_salida").split("/")[1]+"' , '"+rs.getString("tension_salida").split("/")[2]+"' , ";
//                        GUARDAR += " '"+rs.getString("aat")+"' , '"+rs.getString("abt")+"' , '"+rs.getString("hat")+"' , '"+rs.getString("hbt")+"' ,";
//                        GUARDAR += " '"+rs.getBoolean("ci")+"' , '"+rs.getBoolean("ce")+"' , '"+rs.getString("herraje")+"' , ";
//                        GUARDAR += " '"+rs.getString("ano")+"' , '"+rs.getString("peso")+"' , '"+rs.getString("aceite")+"' , ";
//                        GUARDAR += " '"+rs.getString("observacion")+"' , '"+rs.getString("observacion_salida")+"' , '"+rs.getString("servicio")+"' , ";
//                        GUARDAR += " '"+rs.getString("servicio_salida")+"' , '"+rs.getString("tipoentrada")+"' , '"+rs.getString("tipotrafo_salida")+"' , ";
//                        GUARDAR += " '"+rs.getString("estado")+"' , '"+rs.getString("identrada_datos")+"' , '"+rs.getString("iddespacho_salida")+"' , ";
//                        GUARDAR += ((rs.getString("idremision_datosentrada")==null)?0:rs.getString("idremision_datosentrada"));
//                        GUARDAR += "),\n";
//                    }
//        //            System.out.println(GUARDAR);
//                    GUARDAR = GUARDAR.substring(0, GUARDAR.length()-2);
//        //            System.out.println(GUARDAR);
//                    Clipboard system;
//                    StringSelection stsel;
//                    stsel  = new StringSelection(GUARDAR);
//                    system = Toolkit.getDefaultToolkit().getSystemClipboard();
//                    system.setContents(stsel,stsel);
//                    if(new modelo.ConexionBD().GUARDAR(GUARDAR)){
//
//                    }
//                } catch (SQLException ex) {
//                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
//                    area.setText(""+ex);
//               }
//            }
//        }).start();
       
//REGISTRA LAS DIFERENCIAS DE LAS ENTRADAS REGISTRADAS
//        ResultSet rs = modelo.Conexion2.CONSULTAR("SELECT * FROM diferencia_entrada");
//        String GUARDAR = " INSERT INTO diferenciasentrada (iddiferencia, identrada, descripcion) VALUES ";        
//        try {
//            while(rs.next()){
//                GUARDAR += " ( '"+rs.getString("id_dif")+"' , '"+rs.getString("identrada_dif")+"' , '"+rs.getString("diferencia_dif")+"' ),\n";
//            }
//            GUARDAR = GUARDAR.substring(0, GUARDAR.length()-2);
//            if(new modelo.ConexionBD().GUARDAR(GUARDAR)){
//
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
//        }


//REGISTRA LOS DESPACHOS
//        ResultSet rs = modelo.Conexion2.CONSULTAR("SELECT * FROM despacho");
//        String GUARDAR = " INSERT INTO despacho VALUES ";
//        try {
//            while(rs.next()){
//                GUARDAR += " ( '"+rs.getString("iddespacho")+"' , '"+rs.getString("nodespacho")+"' , '"+rs.getString("fecha_despacho")+"' , '"+rs.getString("idcliente")+"' , '"+((rs.getString("peso_despacho")==null)?0:rs.getInt("peso_despacho"))+"' , '"+rs.getString("estado_despacho")+"' , '"+rs.getString("descripcion_despacho")+"' , 1) ,\n";
//            }
//            System.out.println(GUARDAR);
//            GUARDAR = GUARDAR.substring(0, GUARDAR.length()-2);
//            if(new modelo.ConexionBD().GUARDAR(GUARDAR)){
//                
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
//REGISTRA LAS REMISIONES
//        ResultSet rs = modelo.Conexion2.CONSULTAR("SELECT * FROM remision");
//        String GUARDAR = " INSERT INTO remision VALUES ";
//        try {
//            while(rs.next()){
//                GUARDAR += " ( '"+rs.getString("idremision")+"' , '"+rs.getString("iddespacho_remision")+"' , ";
//                GUARDAR += " '"+rs.getString("numero_remision")+"' , '"+rs.getString("cliente_remision")+"' , ";
//                GUARDAR += " '"+rs.getString("ciudad_remision")+"' , '"+rs.getString("destino_remision")+"' , ";
//                GUARDAR += " '"+rs.getString("telefono_remision")+"' , '"+rs.getString("contrato_remision")+"' , ";
//                GUARDAR += " '"+rs.getString("centrodecostos_remision")+"' , '"+rs.getString("conductor_remision")+"' , ";
//                GUARDAR += " '"+rs.getString("cedula_remision")+"' , '"+rs.getString("placa_remision")+"' , ";
//                GUARDAR += " '"+rs.getDate("fecha_remision")+"' , '"+rs.getString("tipo_remision")+"' , ";
//                GUARDAR += " '"+rs.getString("cedula_remision")+"' , '"+rs.getString("placa_remision")+"' , ";
//                GUARDAR += " '"+rs.getString("servicio_remision")+"' , '"+rs.getString("descripcion_remision")+"' , ";
//                GUARDAR += " '"+rs.getString("factura_numero")+"' , '"+rs.getString("empresa_remision")+"' , ";
//                GUARDAR += " '"+rs.getBoolean("estado")+"', 1 ) ,\n";
//                
//            }
//            System.out.println(GUARDAR);
//            Clipboard system;
//            StringSelection stsel;
//            stsel  = new StringSelection(GUARDAR);
//            system = Toolkit.getDefaultToolkit().getSystemClipboard();
//            system.setContents(stsel,stsel);
//            GUARDAR = GUARDAR.substring(0, GUARDAR.length()-2);
//            if(new modelo.ConexionBD().GUARDAR(GUARDAR)){
//                
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        try {
//            ServerSocket serverSocket = new ServerSocket(55557);
//        } catch (IOException ex) {
//            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
//            Metodos.ERROR(ex, "LA APLICACION YA SE HA INICIADO, VERIFIQUE LOS PROCESOS EJECUTADOS E INTENTE NUEVAMENTE");
//            System.exit(0);
//        }
        
//        jPanel1.setLayout (new JScollPanelAjustable.WrapLayout());
        
        
//        if (SystemTray.isSupported()){
//            try {
//                SystemTray systemtray = SystemTray.getSystemTray();
//                                
//                trayIcon.setToolTip("CDM SOFTWARE");
//                trayIcon.setImageAutoSize(true);
//                trayIcon.addMouseListener(new MouseAdapter(){
//                    @Override
//                    public void mouseReleased(MouseEvent e){
//                        if(SwingUtilities.isLeftMouseButton(e)){
//                            setVisible(true);
//                            setExtendedState(JFrame.NORMAL);
//                        }
//                    }
//                });
//                systemtray.add(trayIcon);
//            } catch (Exception ex) {
//                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        
//        addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowIconified(WindowEvent e) {
//                (new Thread(){
//                    @Override
//                    public void run(){
//                        setVisible(false);
//                        while(true){
//                            if(getExtendedState()==JFrame.ICONIFIED){
//                                trayIcon.displayMessage("CDM SOFTWARE:", "Aplicacion iniciada en segundo plano.", TrayIcon.MessageType.INFO);
//                                try {
//                                    Thread.sleep(60000);
//                                } catch (InterruptedException ex) {
//                                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
//                                }
//                            }
//                        }
//                    }
//                }).start();                
//            }
//        });          
    }
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnLotes = new javax.swing.JButton();
        btnProtocolo = new javax.swing.JButton();
        btnInformes = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menu principal");
        setIconImage(new ImageIcon(getClass().getResource("/recursos/images/logo.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setLayout(new java.awt.GridLayout(3, 1, 0, 5));

        btnLotes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/lotes.png"))); // NOI18N
        btnLotes.setBorderPainted(false);
        btnLotes.setPreferredSize(new java.awt.Dimension(130, 130));
        btnLotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLotesActionPerformed(evt);
            }
        });
        jPanel1.add(btnLotes);

        btnProtocolo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/protocolo.png"))); // NOI18N
        btnProtocolo.setBorderPainted(false);
        btnProtocolo.setPreferredSize(new java.awt.Dimension(130, 130));
        btnProtocolo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProtocoloActionPerformed(evt);
            }
        });
        jPanel1.add(btnProtocolo);

        btnInformes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/calendario.png"))); // NOI18N
        btnInformes.setBorderPainted(false);
        btnInformes.setPreferredSize(new java.awt.Dimension(130, 130));
        btnInformes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInformesActionPerformed(evt);
            }
        });
        jPanel1.add(btnInformes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(232, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnProtocoloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProtocoloActionPerformed
        (new Thread(){
            @Override
            public void run(){
                btnProtocolo.setEnabled(false);
                view.PROTOS proto = new view.PROTOS();
                modelo.Sesion sesion = modelo.Sesion.getConfigurador(null, -1);
                proto.setTitle("PROTOCOLO DE TRANSFORMADORES - Usuario: "+sesion.getNombre());
                proto.setVisible(true);
                dispose();
            }
        }).start();          
    }//GEN-LAST:event_btnProtocoloActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            Dialogos.Login login = new Dialogos.Login(this, rootPaneCheckingEnabled);
            login.setVisible(true);
            modelo.Sesion sesion = modelo.Sesion.getConfigurador(null, -1);
            setTitle("Bienvenido "+sesion.getNombre());
        } catch (Exception e){
            modelo.Metodos.ERROR(e, "ERROR AL ABRIR EL FORMULARIO PARA EL INICIO DE SESION.");
        }        
    }//GEN-LAST:event_formWindowOpened

    private void btnLotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLotesActionPerformed
        (new Thread(){
            @Override
            public void run(){
                btnLotes.setEnabled(false);
                view.MENU_NUEVO menu = new view.MENU_NUEVO();
                modelo.Sesion sesion = modelo.Sesion.getConfigurador(null, -1);
                menu.setTitle("CONTROL DE TRASFORMADORES - Usuario: "+sesion.getNombre());
                menu.setVisible(true);
                dispose();
            }
        }).start();        
    }//GEN-LAST:event_btnLotesActionPerformed

    private void btnInformesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInformesActionPerformed
        (new Thread(){
            @Override
            public void run(){
                btnInformes.setEnabled(false);
                view.InformesDeProduccion menu = new view.InformesDeProduccion();
                modelo.Sesion sesion = modelo.Sesion.getConfigurador(null, -1);
                menu.setTitle("INFORME DE PRODUCCION - Usuario: "+sesion.getNombre());
                menu.setVisible(true);
                dispose();
            }
        }).start(); 
    }//GEN-LAST:event_btnInformesActionPerformed
    
    
    
    public static void main(String args[]){     
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new Principal().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInformes;
    private javax.swing.JButton btnLotes;
    private javax.swing.JButton btnProtocolo;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
