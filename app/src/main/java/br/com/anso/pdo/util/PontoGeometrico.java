package br.com.anso.pdo.util;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;


public class PontoGeometrico {
    private LatLng coordenadas;
    private String logradouro;
    private String numero;
    private String referencia;
    private String municipio;

    public PontoGeometrico(){}
    public PontoGeometrico(JSONObject p){
        this.referencia = p.optString("referencia");
        this.coordenadas = new LatLng(Double.parseDouble(p.optString("lat")), Double.parseDouble(p.optString("lon")));
        this.logradouro = p.optString("logradouro");
        this.municipio = p.optString("municipio");
        this.numero = p.optString("numero");
    }

    public LatLng getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(LatLng coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }
}
