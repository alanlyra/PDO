package br.com.anso.pdo.util;


import android.content.res.Resources;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.anso.pdo.R;

public class Caminhada {
    private LatLng posicao;
    private String polilinha;
    private String rua;
    private double distancia;
    private String instrucao;
    private MarkerOptions markerOptionsParada;

    public String getPolilinha() {
        return polilinha;
    }

    public String getRua() {
        return rua;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setPolilinha(String polilinha) {
        this.polilinha = polilinha;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public String getInstrucao() {
        return instrucao;
    }

    public void setInstrucao(String instrucao) {
        this.instrucao = instrucao;
    }

    public LatLng getPosicao() {
        return posicao;
    }

    public void setPosicao(LatLng posicao) {
        this.posicao = posicao;
    }

    public void setMarkerOptionsParadas(Resources resources){
        this.markerOptionsParada = new MarkerOptions()
                .position(posicao)
                .icon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(resources, R.drawable.circle1, 30, 30)))
                .title(rua)
                .anchor((float) 0.5, (float) 0.5);
    }

    public MarkerOptions getMarkerOptionsParada(){
        return markerOptionsParada;
    }
}
