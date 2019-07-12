package modelo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

public class ColorDespachoARemision extends DefaultTableCellRenderer{

    Color celda_error = new Color(185,63,51);
    Color vigente = new Color(138,224,132);
    Color vencido = new Color(145,175,238);
    int cc=0;  
    Component renderer = null;
    public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,  boolean isSelected, boolean hasFocus,  int row, int column) {
       
    JLabel c = null;
    try{                
        setForeground(vencido);
        c = new JLabel( (value==null)?"":value.toString() );
        c.setToolTipText((value==null)?"":value.toString());
        c.setFont(new Font("Ebrima", 1, 12));
        
        if ( (row%2)!=0 ){            
            c.setBackground(new Color(231,243,253,255));            
        }else{
            c.setBackground(Color.white);
        }
        
        if(column==0){
            c.setOpaque(true);
            c.setBackground(new Color(255, 248, 220));
        }
        
        if(null != table.getModel().getValueAt(row, 2)){
            c.setOpaque(true);
            c.setBackground(vigente);
            c.setForeground(Color.black);
        }

        if(column==8 || column == 9){
            if(Double.parseDouble(""+table.getModel().getValueAt(row, 8)) != Double.parseDouble(""+table.getModel().getValueAt(row, 9))){
                c.setOpaque(true);
                c.setBackground(new Color(210, 71, 38));
                c.setForeground(Color.white);
            }else{
                if ( (row%2)!=0 ){c.setBackground(new Color(231,243,253,255));}else{c.setBackground(Color.white);}
            }
        }

        if(column == 10 || column == 11){
            if( !(table.getModel().getValueAt(row, 10).toString().equals(table.getModel().getValueAt(row, 11).toString()))){
                c.setOpaque(true);
                c.setBackground(new Color(210, 71, 38));
                c.setForeground(Color.white);
            }            
        }        

        if(column == 12 || column == 13){
            if( !(table.getModel().getValueAt(row, 12).toString().equals(table.getModel().getValueAt(row, 13).toString()))){
                c.setOpaque(true);
                c.setBackground(new Color(210, 71, 38));
                c.setForeground(Color.white);
            } 
        }

        if(column == 14 || column == 15){
            if( !(table.getModel().getValueAt(row, 14).toString().equals(table.getModel().getValueAt(row, 15).toString()))){
                c.setOpaque(true);
                c.setBackground(new Color(210, 71, 38));
                c.setForeground(Color.white);
            }                
        }

        
            if(isSelected){//SE APLICARAN CAMBIOS CUANDO SELECCIONE LA FILA
                c.setOpaque(true);

                c.setBackground(new Color(198,198,198));
                ((JLabel)c).setBorder(new LineBorder(new Color(33,115,70), 2, false));
                
                c.setForeground(isSelected ?UIManager.getColor("Table.selectionForeground") :UIManager.getColor("Table.foreground"));
                c.setBackground(isSelected ?UIManager.getColor("Table.selectionBackground") :UIManager.getColor("Table.background"));
                c.setBorder(hasFocus ?BorderFactory.createLineBorder(UIManager.getColor("Table.selectionForeground"), 1) :BorderFactory.createEmptyBorder(2, 2, 2, 2));

                if(column==0){c.setOpaque(true);c.setBackground(new Color(210, 105, 30));}

                if(column==8||column==9){
                    if(Double.parseDouble(""+table.getModel().getValueAt(row, 8)) != Double.parseDouble(""+table.getModel().getValueAt(row, 9))){
                        c.setOpaque(true);
                        c.setBackground(new Color(210, 71, 38));
                    }
                }

                if( !(table.getModel().getValueAt(row, 10).toString().equals(table.getModel().getValueAt(row, 11).toString()))){
                    c.setOpaque(true);
                    if(column == 10 || column == 11){
                        c.setBackground(new Color(210, 71, 38));
                        c.setForeground(Color.white);
                    }
                }
                if( !(table.getModel().getValueAt(row, 12).toString().equals(table.getModel().getValueAt(row, 13).toString()))){
                    c.setOpaque(true);
                    if(column == 12 || column == 13){
                        c.setBackground(new Color(210, 71, 38));
                        c.setForeground(Color.white);
                    }
                }
                if( !(table.getModel().getValueAt(row, 14).toString().equals(table.getModel().getValueAt(row, 15).toString()))){
                    c.setOpaque(true);
                    if(column == 14 || column == 15){
                        c.setBackground(new Color(210, 71, 38));
                        c.setForeground(Color.white);
                    }
                }
            }
        }catch(Exception e){
//            System.err.println("ERROR AL RENDEREIZAR LA TABLA\n"+e);        
//            if(column == 5){
//                c = new JLabel("###");
//                c.setOpaque(true);
//                c.setBackground(new Color(210, 71, 38));
//                c.setForeground(Color.white);
//                c.setToolTipText(""+e);
//            }
        }
    return c;
    }

    public ColorDespachoARemision() {        
        setOpaque(true);
    }
    
}