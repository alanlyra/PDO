package br.com.anso.pdo.util;


import android.content.res.Resources;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import br.com.anso.pdo.R;

public class Ponto {
    private LatLng posicao;
    private String endereco;
    private String idPonto;
    private double distancia;
    private MarkerOptions markerOptions;
    private MarkerOptions markerOptionsParada;
    private MarkerOptions markerOptionsParadaHighlight;

    public Ponto(){

    }

    public Ponto(LatLng posicao, String endereco, String idPonto){
        this.posicao = posicao;
        this.endereco = endereco;
        this.idPonto = idPonto;
    }

    public Ponto(JSONObject json) {
        this.posicao = new LatLng(json.optJSONObject("geometrico").optDouble("y"),json.optJSONObject("geometrico").optDouble("x"));
        this.endereco = json.optString("referencia");
    }

    public LatLng getPosicao() {
        return posicao;
    }

    public void setPosicao(LatLng posicao) {
        this.posicao = posicao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getIdPonto() {
        return idPonto;
    }

    public void setIdPonto(String idPonto) {
        this.idPonto = idPonto;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public MarkerOptions getMarker() {
        return markerOptions;
    }

    public void setMarkerOptions(Resources resources){
        this.markerOptions = new MarkerOptions()
                .position(posicao)
                .icon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(resources, R.drawable.pinperson, 50, 65)))
                .title(endereco)
                .anchor((float) 0.5, (float) 1);
    }

    public void setMarkerOptionsParadas(Resources resources){
        this.markerOptionsParada = new MarkerOptions()
                .position(posicao)
                .icon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(resources, R.drawable.circle1, 30, 30)))
                .title(endereco)
                .snippet("")
                .anchor((float) 0.5, (float) 0.5);
    }

    public MarkerOptions getMarkerOptionsParada(){
        return markerOptionsParada;
    }

    public void setMarkerOptionsParadaHighlight(Resources resources){
        this.markerOptionsParadaHighlight = new MarkerOptions()
                .position(posicao)
                .icon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(resources, R.drawable.circle3, 36, 36)))
                .title(endereco)
                .snippet("")
                .anchor((float) 0.5, (float) 0.5);
    }
    public MarkerOptions getMarkerOptionsParadaHighlight() {
        return markerOptionsParadaHighlight;
    }
}
