package br.com.anso.pdo.rota;

import org.json.JSONException;

import br.com.anso.pdo.util.Rota;

public interface IRotaView {
    void exibirRotaNoMapa(Rota rota);

    static interface IRotaPresenter{
        void carregarRotas() throws JSONException;
    }
}
