package modelo;

public class PrepararDespacho {
    
    public static String[] getColumnNames(){
        return new String[]{
            "ITEM",//0
            "DESPACHO",//1
            "REMISION",//2
            "N° EMPRESA",//3
            "SELECC.",//4
            "PROTO.",//5
            "N° SEIRE",//6
            "MARCA",//7
            "FASE",//8
            "KVA ENT.",//9
            "KVA SAL.",//10         
            "TENSION ENT.",//11
            "TENSION SAL.",//12
            "SERV. ENT.",//13
            "SERV. SAL.",//14
            "TIPO ENT.",//15
            "TIPO SAL.",//16
            "OBSERV. ENT.",//17
            "OBSERV. SAL.",//18        
            "PESO",//19
            "ACEITE"//20
        };
    }
    
    public static Boolean[] getColumnEditablesS(){
        return new Boolean[]{
            false,//"ITEM°",
            true,//"DESPACHO",1
            false,//"REMISION",2
            false,//"N° EMPRESA",3
            true,//SELECCIONE 4
            false,//PROTOCOLO 5
            false,//"N° SEIRE",5
            false,//"MARCA",6
            false,//"FASE.",7
            false,//"KVA ENT"9
            true,//"KVA SAL",10
            false,//"TENSION ENT",11
            true,//"TENSION SAL",12
            false,//"SERV. ENT",13
            true,//"SERV. SAL",14
            false,//"TIPO ENT",15
            true,//"TIPO SAL",16
            false,//"OBSERV. ENT",17
            true,//"OBSERV. SAL",18
            false,//"PESO",19
            false,//"ACEITE",20
        };
    }
    
    public static Class[] getColumnClass(){
        return new Class[]{
            Integer.class,//"ITEM",
            Object.class,//"DESPACHO",
            Object.class,//"REMISION",
            Object.class,//"N° EMPRESA",
            Boolean.class,//"N° EMPRESA",
            Object.class,//"N° SEIRE",
            Object.class,//"MARCA",
            Integer.class,//"FASE",
            Double.class,//"KVA ENT",
            Double.class,//"KVA SAL",            
            String.class,//"TENSION ENT",
            String.class,//"TENSION SAL",
            String.class,//"SERV. ENT",
            String.class,//"SERV. SAL",
            String.class,//"TIPO ENT",
            String.class,//"TIPO SAL",
            String.class,//"OBSERV. ENT",
            String.class,//"OBSERV. SAL",            
            Integer.class,//"PESO",
            Integer.class,//"ACEITE",            
        };
    }    
    
}