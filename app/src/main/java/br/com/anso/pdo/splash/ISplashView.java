package br.com.anso.pdo.splash;

import android.content.Context;

import org.json.JSONException;


public interface ISplashView {

    void abrirProximaView();
    void abrirDialogoAtualizarApp(boolean deprecated);

    interface ISplashPresenter{
        void iniciarApp(Context context) throws JSONException;
        void carregarFavoritos(String path, Context context) throws JSONException;
        void verificarVersaoApp(Context context);

        boolean carregaPreferencias(Context baseContext);
    }

}
