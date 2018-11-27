package view2;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class MainProcesadorTexto2 {

    public static void main(String[] args) {
        FrameProcesadorTexto2 miFrame = new FrameProcesadorTexto2();
        miFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class FrameProcesadorTexto2 extends JFrame {

    public FrameProcesadorTexto2() {
        setBounds(450, 200, 550, 550);
        SheetProcesadorTexto2 mySheet = new SheetProcesadorTexto2();
        add(mySheet);
        setVisible(true);
    }
}

class SheetProcesadorTexto2 extends JPanel {

    public SheetProcesadorTexto2() {
        setLayout(new BorderLayout());
        JPanel laminaMenu = new JPanel();
        //************************************
        //menus
        JMenuBar miMenuBarra = new JMenuBar();
        fuente = new JMenu("Fuente");
        estilo = new JMenu("Estilo");
        tamano = new JMenu("Tamano");

        // menu de tipografia
        configurarMenu("Arial New", "fuente", "Arial New", 9, 10, "");
        configurarMenu("Courier New", "fuente", "Courier New", 9, 10, "");
        configurarMenu("Verdana", "fuente", "Verdana", 9, 10, "");

        //             ------------------------menu de estilo--------------------------------------------
        configurarMenu("negrita", "estilo", "", Font.BOLD, 1, "src/graficos/childCare.png");
        configurarMenu("cursiva", "estilo", "", Font.ITALIC, 1, "src/graficos/childCare.png");
        /*JCheckBoxMenuItem negrita = new JCheckBoxMenuItem("negrita", new ImageIcon("src/graficos/childCare"));
        JCheckBoxMenuItem cursiva = new JCheckBoxMenuItem("cursiva", new ImageIcon("src/graficos/childCare"));
        //responden a eventos
        negrita.addActionListener(new StyledEditorKit.BoldAction());
        cursiva.addActionListener(new StyledEditorKit.ItalicAction());*/

        //------------------------------------------ menu de tamano-----------------------------------------------
        ButtonGroup tamanoLetra = new ButtonGroup();

        JRadioButtonMenuItem doce = new JRadioButtonMenuItem("12");
        JRadioButtonMenuItem dieciseis = new JRadioButtonMenuItem("16");
        JRadioButtonMenuItem veinte = new JRadioButtonMenuItem("20");
        JRadioButtonMenuItem veinticuatro = new JRadioButtonMenuItem("24");
        //veinticuatro.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        //agregamos al grupo
        tamanoLetra.add(doce);
        tamanoLetra.add(dieciseis);
        tamanoLetra.add(veinte);
        tamanoLetra.add(veinticuatro);

        doce.addActionListener(new StyledEditorKit.FontSizeAction("cambia tamano", 12));
        dieciseis.addActionListener(new StyledEditorKit.FontSizeAction("cambia tamano", 16));
        veinte.addActionListener(new StyledEditorKit.FontSizeAction("cambia tamano", 20));
        veinticuatro.addActionListener(new StyledEditorKit.FontSizeAction("cambia tamano", 24));

        tamano.add(doce);
        tamano.add(dieciseis);
        tamano.add(veinte);
        tamano.add(veinticuatro);
        //************************agregamos los menu a la barra**************************
        miMenuBarra.add(fuente);
        miMenuBarra.add(estilo);
        miMenuBarra.add(tamano);

        //------------------------------------
        /*   estilo.add(negrita);
        estilo.add(cursiva);*/
        //-------------lamina para el menu------------
        laminaMenu.add(miMenuBarra);
        add(laminaMenu, BorderLayout.NORTH);

        //---------------area o panel de texto--------------
        miArea = new JTextPane();
        //JScrollPane miAreaScroll = new JScrollPane(miArea);
        add(miArea, BorderLayout.CENTER);

        //      ------ Menu Emergente ------
        JPopupMenu miMenuEmergente = new JPopupMenu();
        JMenuItem negritaE = new JMenuItem("negrita");
        JMenuItem cursivaE = new JMenuItem("cursiva");

        //Agregando Funcionalidad
        negritaE.addActionListener(new StyledEditorKit.BoldAction());
        cursivaE.addActionListener(new StyledEditorKit.ItalicAction());

        miMenuEmergente.add(negritaE);
        miMenuEmergente.add(cursivaE);

        miArea.setComponentPopupMenu(miMenuEmergente);

        //----------------Creación de Barra de Herramientas-----------
        miBarra = new JToolBar();
        getConfiguraBarra("src/graficos/icons/format-bold.png").addActionListener(new StyledEditorKit.BoldAction());
        getConfiguraBarra("src/graficos/icons/italic_icon.png").addActionListener(new StyledEditorKit.ItalicAction());
        getConfiguraBarra("src/graficos/icons/underline.png").addActionListener(new StyledEditorKit.UnderlineAction());
        miBarra.addSeparator();
        getConfiguraBarra("src/graficos/icons/blue.png").addActionListener(new StyledEditorKit.ForegroundAction("colorAzul", Color.BLUE));
        getConfiguraBarra("src/graficos/icons/green.png").addActionListener(new StyledEditorKit.ForegroundAction("colorVerde", Color.GREEN));
        getConfiguraBarra("src/graficos/icons/red.png").addActionListener(new StyledEditorKit.ForegroundAction("colorRojo", Color.RED));
        miBarra.addSeparator();
        getConfiguraBarra("src/graficos/icons/left-side-alignment.png").addActionListener(new StyledEditorKit.AlignmentAction("izquierda", StyleConstants.ALIGN_LEFT));
        getConfiguraBarra("src/graficos/icons/right-alignment.png").addActionListener(new StyledEditorKit.AlignmentAction("derecha", StyleConstants.ALIGN_RIGHT));
        getConfiguraBarra("src/graficos/icons/center-alignment.png").addActionListener(new StyledEditorKit.AlignmentAction("centrado", StyleConstants.ALIGN_CENTER));
        getConfiguraBarra("src/graficos/icons/justified.png").addActionListener(new StyledEditorKit.AlignmentAction("justificado", StyleConstants.ALIGN_JUSTIFIED));
        miBarra.setOrientation(1);

        add(miBarra, BorderLayout.WEST);
    }

    public JButton getConfiguraBarra(String ruta) {
        JButton boton = new JButton(new ImageIcon(ruta));
        miBarra.add(boton);
        return boton;
    }

    public void configurarMenu(String rotulo, String Menu, String tipoLetra, int estilos, int tamFuente, String rutaIcono) {
        JMenuItem elemMenu = new JMenuItem(rotulo, new ImageIcon(rutaIcono));
        if (Menu == "fuente") {
            fuente.add(elemMenu);
            if (tipoLetra == "Arial New") {
                elemMenu.addActionListener(new StyledEditorKit.FontFamilyAction("cambiaLetra", "Arial New"));
            } else if (tipoLetra == "Courier New") {
                elemMenu.addActionListener(new StyledEditorKit.FontFamilyAction("cambioLetra", "Courier New"));
            } else if (tipoLetra == "Verdana") {
                elemMenu.addActionListener(new StyledEditorKit.FontFamilyAction("cambiamoLetra", "Verdana"));
            }
        } else if (Menu == "estilo") {
            estilo.add(elemMenu);
            if (estilos == Font.BOLD) {
                elemMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
                elemMenu.addActionListener(new StyledEditorKit.BoldAction());
            } else if (estilos == Font.ITALIC) {
                elemMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK));
                elemMenu.addActionListener(new StyledEditorKit.ItalicAction());
            }
        } else if (Menu == "tamano") {
            tamano.add(elemMenu);
            elemMenu.addActionListener(new StyledEditorKit.FontSizeAction("cambiaTamaño", tamFuente));
        }
    }

    //                      ------------variables globales----------
    JTextPane miArea;
    JMenu fuente, estilo, tamano;
    Font letras;
    JToolBar miBarra;
}
