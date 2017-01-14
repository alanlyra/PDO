package br.com.anso.pdo.buscaPontosEstacoes;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import br.com.anso.pdo.util.Ponto;

public interface IBuscaPontosEstacoesView {

    void carregaPontosNoMapa(List<Ponto> listaDePontos, int raio);
    void setErroTimeout(String msg);

    static interface IBuscaPontosEstacoesPresenter{
        void consultaPontosEstacoesWebservice(LatLng pos, Context context);
    }

}
