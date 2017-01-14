package br.com.anso.pdo.principal;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

import br.com.anso.pdo.util.Linha;

public interface IPrincipalTab1View {

    void setListaLinhas(List<HashMap<String, String>> aList);
    void exibirLinhasNoMapa(List<Linha> linhas);

    static interface IPrincipalTab1Presenter {
        void carregarLinhasProximas(LatLng pos, Context context);
    }
}
