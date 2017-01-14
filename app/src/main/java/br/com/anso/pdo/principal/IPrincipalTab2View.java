package br.com.anso.pdo.principal;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

public interface IPrincipalTab2View {

    void setListaPontosProximos(List<HashMap<String, String>> aList);
    void carregaPontosNoMapa(JSONArray listaDePontos);

    static interface IPrincipalTab2Presenter {
        void carregarPontosProximos(LatLng pos, Context context);
    }
}
