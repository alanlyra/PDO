package br.com.anso.pdo.util;

import android.text.format.DateUtils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Viagem {

    private String tipoTransporte;
    private double distancia;
    private ArrayList<String> polylines;
    private PontoGeometrico pontoDestino;
    private String avisos;
    private double tempoViagem = 0.0;
    private ArrayList<Ponto> paradas;
    private Linha linha;

    public ArrayList<ArrayList<LatLng>> getDecodedPolyline(){
        ArrayList<ArrayList<LatLng>> list = new ArrayList<>();

        for(String p : polylines)
            list.add(Util.decodePoly(p));

        return list;
    }

    public String getTempoViagemFormatado(){
        if(tempoViagem != tempoViagem) tempoViagem = 0.0;

        if(!this.tipoTransporte.equals("Caminhada")){
            String[] tempTempo = DateUtils.formatElapsedTime((long) this.tempoViagem).split(":");
            String hora = !tempTempo[0].equals("00") ? tempTempo[0]+" h " :"";
            String min = !tempTempo[1].equals("00") ? tempTempo[1]+" min" : "";

            return (hora+min).equals("") ? "N/D" : hora+min;
        }
        else{
            double t = this.tempoViagem > 0.1 ? this.tempoViagem : (this.distancia)/60.0;

            String[] tempTempo = DateUtils.formatElapsedTime((long) t).split(":");
            String hora = !tempTempo[0].equals("00") ? tempTempo[0]+" h " :"";
            String min = !tempTempo[1].equals("00") ? tempTempo[1]+" min " : "";

            return (hora+min).equals("") ? "01 min " : hora+min; // Default de caminhada: 01 minuto
        }
    }

    public String getTipoTransporte() {
        return tipoTransporte;
    }

    public void setTipoTransporte(String tipoTransporte) {
        this.tipoTransporte = tipoTransporte;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public ArrayList<String> getPolylines() {
        return polylines;
    }

    public void setPolylines(ArrayList<String> polylines) {
        this.polylines = polylines;
    }

    public PontoGeometrico getPontoDestino() {
        return pontoDestino;
    }

    public void setPontoDestino(PontoGeometrico pontoDestino) {
        this.pontoDestino = pontoDestino;
    }

    public String getAvisos() {
        return avisos;
    }

    public void setAvisos(String avisos) {
        this.avisos = avisos;
    }

    public double getTempoViagem() {
        return tempoViagem;
    }

    public void setTempoViagem(double tempoViagem) {
        this.tempoViagem = tempoViagem;
    }

    public ArrayList<Ponto> getParadas() {
        return paradas;
    }

    public void setParadas(ArrayList<Ponto> paradas) {
        this.paradas = paradas;
    }

    public Linha getLinha() {
        return linha;
    }

    public void setLinha(Linha linha) {
        this.linha = linha;
    }
}
