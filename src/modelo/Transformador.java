package modelo;

import modelo.Lote;

public class Transformador {
    
    private int idtransformador;
    private int item;
    private String numeroempresa;
    private String numeroserie;
    private String marca;
    private double kvaentrada;
    private double kvasalida;
    private int fase;
    private int tpe;
    private int tse;
    private int tte;
    private int tps;
    private int tss;
    private int tts;
    private String aat;
    private String abt;
    private String hat;
    private String hbt;
    private boolean ci;
    private boolean ce;
    private String herraje;
    private int ano;
    private int peso;
    private int aceite;
    private String observacionentrada;
    private String observacionsalida;
    private String servicioentrada;
    private String serviciosalida;
    private String tipotradoentrada;
    private String tipotrafosalida;
    private String estado;
    private Lote entrada;
    private String causadefalla;

    public Transformador() {
    }

    public Transformador(int idtransformador, String numeroserie) {
        this.idtransformador = idtransformador;
        this.numeroserie = numeroserie;
    }
    
    public Transformador(int idtransformador) {
        this.idtransformador = idtransformador;
    }

    public int getIdtransformador() {
        return idtransformador;
    }

    public void setIdtransformador(int idtransformador) {
        this.idtransformador = idtransformador;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public String getNumeroempresa() {
        return numeroempresa;
    }

    public void setNumeroempresa(String numeroempresa) {
        this.numeroempresa = numeroempresa;
    }

    public String getNumeroserie() {
        return numeroserie;
    }

    public void setNumeroserie(String numeroserie) {
        this.numeroserie = numeroserie;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getKvaentrada() {
        return kvaentrada;
    }

    public void setKvaentrada(double kvaentrada) {
        this.kvaentrada = kvaentrada;
    }

    public double getKvasalida() {
        return kvasalida;
    }

    public void setKvasalida(double kvasalida) {
        this.kvasalida = kvasalida;
    }

    public int getFase() {
        return fase;
    }

    public void setFase(int fase) {
        this.fase = fase;
    }

    public int getTpe() {
        return tpe;
    }

    public void setTpe(int tpe) {
        this.tpe = tpe;
    }

    public int getTse() {
        return tse;
    }

    public void setTse(int tse) {
        this.tse = tse;
    }

    public int getTte() {
        return tte;
    }

    public void setTte(int tte) {
        this.tte = tte;
    }

    public int getTps() {
        return tps;
    }

    public void setTps(int tps) {
        this.tps = tps;
    }

    public int getTss() {
        return tss;
    }

    public void setTss(int tss) {
        this.tss = tss;
    }

    public int getTts() {
        return tts;
    }

    public void setTts(int tts) {
        this.tts = tts;
    }

    public String getAat() {
        return aat;
    }

    public void setAat(String aat) {
        this.aat = aat;
    }

    public String getAbt() {
        return abt;
    }

    public void setAbt(String abt) {
        this.abt = abt;
    }

    public String getHat() {
        return hat;
    }

    public void setHat(String hat) {
        this.hat = hat;
    }

    public String getHbt() {
        return hbt;
    }

    public void setHbt(String hbt) {
        this.hbt = hbt;
    }

    public boolean isCi() {
        return ci;
    }

    public void setCi(boolean ci) {
        this.ci = ci;
    }

    public boolean isCe() {
        return ce;
    }

    public void setCe(boolean ce) {
        this.ce = ce;
    }

    public String getHerraje() {
        return herraje;
    }

    public void setHerraje(String herraje) {
        this.herraje = herraje;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public int getAceite() {
        return aceite;
    }

    public void setAceite(int aceite) {
        this.aceite = aceite;
    }

    public String getObservacionentrada() {
        return observacionentrada;
    }

    public void setObservacionentrada(String observacionentrada) {
        this.observacionentrada = observacionentrada;
    }

    public String getObservacionsalida() {
        return observacionsalida;
    }

    public void setObservacionsalida(String observacionsalida) {
        this.observacionsalida = observacionsalida;
    }

    public String getServicioentrada() {
        return servicioentrada;
    }

    public void setServicioentrada(String servicioentrada) {
        this.servicioentrada = servicioentrada;
    }

    public String getServiciosalida() {
        return serviciosalida;
    }

    public void setServiciosalida(String serviciosalida) {
        this.serviciosalida = serviciosalida;
    }

    public String getTipotradoentrada() {
        return tipotradoentrada;
    }

    public void setTipotradoentrada(String tipotradoentrada) {
        this.tipotradoentrada = tipotradoentrada;
    }

    public String getTipotrafosalida() {
        return tipotrafosalida;
    }

    public void setTipotrafosalida(String tipotrafosalida) {
        this.tipotrafosalida = tipotrafosalida;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Lote getEntrada() {
        return entrada;
    }

    public void setEntrada(Lote entrada) {
        this.entrada = entrada;
    }

    public String getCausadefalla() {
        return causadefalla;
    }

    public void setCausadefalla(String causadefalla) {
        this.causadefalla = causadefalla;
    }

    
    
}
