package br.com.anso.pdo.favoritos;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import br.com.anso.pdo.R;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Linha;
import br.com.anso.pdo.util.Ponto;
import br.com.anso.pdo.util.Util;

public class Favorito {
    private AppSingleton appSingleton = AppSingleton.getApp();
    private int duration = Toast.LENGTH_SHORT;

    public void atualizaFavoritos(Context context, Object object){
        boolean ok=true;

        if(object instanceof  Linha){
            for(int i = 0; i < appSingleton.getLinhasFavoritas().size(); i++){
                if (appSingleton.getLinhasFavoritas().get(i).getRouteName().equals(((Linha)object).getRouteName())){
                    Toast.makeText(context, context.getResources().getString(R.string.salvo_nos_favoritos_erro), duration).show();
                    ok=false;
                }
            }
            if(ok){
                appSingleton.setLinhaFavorita((Linha)object);
                salvaFavoritos(context, "linhas.json", appSingleton.getLinhasFavoritas());
                Toast.makeText(context, context.getResources().getString(R.string.salvo_nos_favoritos_resultado), duration).show();
            }
        }
        else{
            for(int i = 0; i < appSingleton.getPontosFavoritos().size(); i++){
                if (appSingleton.getPontosFavoritos().get(i).getIdPonto().equals(((Ponto)object).getIdPonto())){
                    Toast.makeText(context, context.getResources().getString(R.string.ponto_salvo_nos_favoritos_erro), duration).show();
                    ok=false;
                }
            }
            if(ok) {
                appSingleton.setPontoFavorito((Ponto) object);
                salvaFavoritos(context, "pontos.json", appSingleton.getPontosFavoritos());
                Toast.makeText(context, context.getResources().getString(R.string.ponto_salvo_nos_favoritos_resultado), duration).show();
            }
        }
    }

    private void salvaFavoritos(Context context, String file, List lista) {
        Gson jsonObject = new Gson();
        String jsonString = jsonObject.toJson(lista);
        //jsonString = "[]";//para limpar arquivo, basta descomentar =P
        Util.createAndSaveFile(file, jsonString, context);
    }
}
