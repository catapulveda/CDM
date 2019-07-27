package DataSource;

import java.util.ArrayList;
import java.util.List;
import modelo.Transformador;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author nelson.castiblanco
 */
public class TrafosDataSource implements JRDataSource {

    private List<Transformador> lista = new ArrayList<Transformador>();
    private int index = -1;

    @Override
    public boolean next() throws JRException {
        return ++index < lista.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object valor = null;
        if (null != jrf.getName()) {
            switch (jrf.getName()) {
                case "nombrecliente":
                    valor = lista.get(index).getEntrada().getCliente().getNombreCliente();
                    break;
                case "lote":
                    valor = lista.get(index).getEntrada().getLote();
                    break;
                case "tps":
                    valor = lista.get(index).getTps();
                    break;
                case "tss":
                    valor = lista.get(index).getTss();
                    break;
                case "tts":
                    valor = lista.get(index).getTts();
                    break;
                case "kvasalida":
                    valor = lista.get(index).getKvasalida();
                    break;
                case "marca":
                    valor = lista.get(index).getMarca();
                    break;
                case "ano":
                    valor = lista.get(index).getAno();
                    break;
                case "tipotrafosalida":
                    valor = lista.get(index).getTipotrafosalida();
                    break;               
                default:
                    break;
            }
        }
        return valor;
    }
    
    public void add(Transformador t){
        this.lista.add(t);
    }

}
