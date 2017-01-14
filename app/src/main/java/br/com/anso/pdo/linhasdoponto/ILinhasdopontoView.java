package br.com.anso.pdo.linhasdoponto;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

public interface ILinhasdopontoView {

    void setListaLinhas(List<HashMap<String, String>> aList);
    LatLng getPosicaoGPS();
    void exibirPontoNoMapa(JSONArray listaJson);

    String getPonto();
    String getEndereco();

    void setErroTimeout(String msg);

    interface ILinhasdopontoPresenter {
        void carregarLinhasDoPonto(Context context);
    }
}
