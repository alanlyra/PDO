package br.com.anso.pdo.splash;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Linha;
import br.com.anso.pdo.util.Ponto;
import br.com.anso.pdo.util.Util;



public class SplashPresenter implements ISplashView.ISplashPresenter {

    ISplashView view;
    AppSingleton app = AppSingleton.getApp();

    public SplashPresenter(ISplashView view){
        this.view = view;
    }

    @Override
    public void iniciarApp(Context context) throws JSONException {
        Util.carregarListaMunicipiosAtendidos();
        Util.carregarInformacoesVersaoApp(context);
        carregarFavoritos("config.json", context);
    }

    @Override
    public void carregarFavoritos(String path, Context context) throws JSONException {
        //por enquanto carregando favoritos em separado (linhas.json e pontos.json).
        JSONArray array = Util.readJsonData("linhas.json", context);
        JSONArray arrayPontos = Util.readJsonData("pontos.json", context);

        for(int i=0; i<array.length(); i++){
            Linha linha = new Linha();
            JSONObject arr = array.optJSONObject(i);
            if(arr!=null){
                linha.setNumero(arr.optString("numero"));
                linha.setCorConsorcio(arr.optString("corConsorcio"));
                linha.setVista(arr.optString("vista"));
                linha.setRouteName(arr.optString("routeName"));
                linha.setServico(arr.optString("servico"));

                app.setLinhaFavorita(linha);
            }
        }

        for(int i=0; i<arrayPontos.length(); i++){
            Ponto ponto = new Ponto();
            JSONObject arr = arrayPontos.optJSONObject(i);
            if(arr!=null){
                ponto.setEndereco(arr.optString("endereco"));
                JSONObject posicao = arr.optJSONObject("posicao");
                if(posicao!=null){
                    ponto.setPosicao(new LatLng(Double.parseDouble(posicao.optString("latitude")), Double.parseDouble(posicao.optString("longitude"))));
                    ponto.setIdPonto(arrayPontos.getJSONObject(i).optString("idPonto"));

                    app.setPontoFavorito(ponto);
                }
            }
        }
    }

    public void verificarVersaoApp(Context context){
        if(app.appVersionCheckJson == null){
            String jsonSavedData = Util.readFileData("version.json", context);
            try{
                app.appVersionCheckJson = new JSONObject(jsonSavedData);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        if(app.appVersionCheckJson != null && !app.appVersionCheckJson.optString("updated").equals("t")){
            view.abrirDialogoAtualizarApp(app.appVersionCheckJson.optBoolean("deprecated"));
        }
        else{
            view.abrirProximaView();
        }
    }

    public boolean carregaPreferencias(Context context){
        String file = "user.json";

        String preferences = Util.readFileData(file, context);
        //Log.d("__USER", preferences);
        if(preferences.equals("")){
            Util.createAndSaveFile(file, "isFirstTime: false", context);

            return true;
        }
        //Util.createAndSaveFile(file, "", context); //para zerar o arquivo basta descomentar e recarregar o app
        return false;
    }
}
