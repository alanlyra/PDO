package br.com.anso.pdo.util;

import android.app.Activity;
import android.content.res.Resources;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.anso.pdo.R;

public class Usuario {
    private static Usuario ourInstance;
    private GPS gpsProvider;
    private LatLng posicao;
    private MarkerOptions marker;
    private boolean isFirstAccess;
    private boolean isManualPos;//posicao vindo de alteração manual (arrastar no mapa)?

    private Usuario() {
    //      Fundão
        this.posicao = new LatLng(-22.858065, -43.232082);
    //      Vila Militar
    //  this.posicao = new LatLng(-22.862682, -43.394937);
    }

    public static Usuario getInstance() {
        if( ourInstance == null )
            ourInstance = new Usuario();

        return ourInstance;
    }

    public LatLng getPosicao(){
        return posicao;
    }

    public void setPosicao(LatLng posicao){
        this.posicao = posicao;
    }

    public void setIsManualPos(boolean isManualPos){
        this.isManualPos = isManualPos;
    }

    public boolean getIsManualPos(){
        return isManualPos;
    }

    public MarkerOptions getMarker() {
        return marker;
    }

    public void setMarker(Resources resources) {

        this.marker = new MarkerOptions()
                .position(posicao)
                .icon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(resources, R.drawable.pinperson, 130, 200)))
                .anchor((float) 0.5, (float) 1);
    }

    public void carregaGPS(Activity activity){
        gpsProvider = new GPS(activity);
    }

    public void recentralizarGPS(Activity act){
        gpsProvider.recentralizarGPS(act);
    }

    public boolean isFirstAccess() {
        return isFirstAccess;
    }

    public void setFirstAccess(boolean firstAccess) {
        isFirstAccess = firstAccess;
    }
}
