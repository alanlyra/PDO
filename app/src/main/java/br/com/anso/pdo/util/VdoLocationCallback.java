package br.com.anso.pdo.util;

import com.google.android.gms.maps.model.LatLng;


public interface VdoLocationCallback {

    void tratarPosicaoGPS(LatLng pos);
}
