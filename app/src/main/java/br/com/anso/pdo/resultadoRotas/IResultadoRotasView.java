package br.com.anso.pdo.resultadoRotas;

import android.content.Context;

import java.util.HashMap;
import java.util.List;

import br.com.anso.pdo.util.Consulta;


public interface IResultadoRotasView {

    void setListaRotas(List<HashMap<String, String>> aList);
    void setErroTimeout(String msg);

    interface IResultadoRotasPresenter{
        void pesquisarRotas(Consulta c, Context context);
    }
}
