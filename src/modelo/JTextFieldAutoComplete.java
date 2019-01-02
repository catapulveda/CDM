package modelo;

import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JTextField;

/**
 *
 * @author PROGRAMADOR
 */
public class JTextFieldAutoComplete extends JTextField{

    private ArrayList<String> lista;    

    public JTextFieldAutoComplete(ArrayList<String> lista) {
        super();
        this.lista = lista;
        
        addKeyListener(new KeyAdapter() {            
            @Override
            public void keyPressed(KeyEvent evt){
                switch(evt.getKeyCode()){
                    case KeyEvent.VK_BACK_SPACE:
                        break;
                    case KeyEvent.VK_ENTER:
                        setText(getText());
                    default:
                        EventQueue.invokeLater(() -> {
                            lista.stream().filter(m-> m.startsWith(getText())).findFirst().ifPresent(a->{
                                System.out.println("VOY EN "+a);
                                String complete = "";
                                int start = getText().length();
                                int last = getText().length();                                
                                complete = a;
                                last = complete.length();
                                if(last>start){
                                    setText(complete);
                                    setCaretPosition(last);
                                    moveCaretPosition(start);
                                }
                            });
                        });
                }
            }
        });
    }
    
}
