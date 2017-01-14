package br.com.anso.pdo.resultadoListaItinerario;

import android.content.Context;

import java.util.HashMap;
import java.util.List;


public interface IResultadoListaItinerarioView {

    void setListaLinhas(List<HashMap<String, String>> aList);
    void setErroTimeout(String msg);

    static interface IResultadoListaItinerarioPresenter {
        void pesquisarLinhas(String textoBusca, Context context);
    }
}
