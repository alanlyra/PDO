package br.com.anso.pdo.util;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.anso.pdo.R;

public class Linha {
    private String numero;
    private String vista;
    private String consorcio;
    private LatLng posicao;
    private String corConsorcio;
    private String routeName;
    private String servico;
    private String dataHora;
    private String ordem;
    private String sentido;
    private MarkerOptions markerOptions;

    public LatLng getPosicao() {
        return posicao;
    }

    public void setPosicao(LatLng posicao) {
        this.posicao = posicao;
    }

    public Linha(String routeName, String servico, String consorcio, String corConsorcio, String numero, String vista){
        this.routeName = routeName;
        this.servico = servico;
        this.corConsorcio = corConsorcio;
        this.numero = numero;
        this.vista = vista;
        this.consorcio = consorcio;
    }
    public Linha (){

    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getConsorcio() {
        return consorcio;
    }

    public void setConsorcio(String consorcio) {
        this.consorcio = consorcio;
    }

    public String getVista() {
        return vista;
    }

    public void setVista(String vista) {
        this.vista = vista;
    }

    public String getCorConsorcio() {
        return corConsorcio;
    }

    public void setCorConsorcio(String corConsorcio) {
        this.corConsorcio = corConsorcio;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public void setOrdem(String ordem) {
        this.ordem = ordem;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    public String getDataHora() {
        return dataHora;
    }

    public String getOrdem() {
        return ordem;
    }

    public String getSentido() {
        return sentido;
    }

    public MarkerOptions getMarker() {
        return markerOptions;
    }

    public void setMarkerOptions(Resources resources, Context context){

        String tituloMarcador = Util.setStringNegrito(context.getString(R.string.linha), String.valueOf(Color.GRAY)) + " " + numero + "<br />" +
                                Util.setStringNegrito(context.getString(R.string.veiculo), String.valueOf(Color.GRAY)) + " " + ordem + "<br />"+
                                Util.setStringNegrito(context.getString(R.string.data_hora), String.valueOf(Color.GRAY))+ " " + dataHora + "<br />"+
                                Util.setStringNegrito(context.getString(R.string.sentido), String.valueOf(Color.GRAY)) + " " + sentido;
        this.markerOptions = new MarkerOptions()
                .position(posicao)
                .icon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(resources, R.drawable.pin_bus, 65, 65)))
                .title(context.getString(R.string.detalhes)).snippet(tituloMarcador)
                .anchor((float) 0.5, (float) 1);
    }
}
