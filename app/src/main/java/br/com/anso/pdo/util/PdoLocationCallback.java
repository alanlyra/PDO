package br.com.anso.pdo.util;

import com.google.android.gms.maps.model.LatLng;


public interface PdoLocationCallback {

    void tratarPosicaoGPS(LatLng pos);
}
