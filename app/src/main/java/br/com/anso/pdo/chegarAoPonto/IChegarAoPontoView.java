package br.com.anso.pdo.chegarAoPonto;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import br.com.anso.pdo.util.Caminhada;


public interface IChegarAoPontoView {

    LatLng getOrigem();

    LatLng getDestino();

    void setListaCaminhada(List<Caminhada> aList);

    interface IChegarAoPontoPresenter {

        void carregarComoChegarAoPontoWebservice();
    }
}
