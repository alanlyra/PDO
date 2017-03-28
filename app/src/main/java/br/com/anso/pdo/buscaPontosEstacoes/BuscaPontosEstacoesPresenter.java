package br.com.anso.pdo.buscaPontosEstacoes;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.anso.pdo.R;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Ponto;
import br.com.anso.pdo.util.PDOConsulta;
import br.com.anso.pdo.util.PDOConsultaCallback;

public class BuscaPontosEstacoesPresenter implements IBuscaPontosEstacoesView.IBuscaPontosEstacoesPresenter {

    AppSingleton appPresenter = AppSingleton.getApp();
    IBuscaPontosEstacoesView view;

    public BuscaPontosEstacoesPresenter(IBuscaPontosEstacoesView view){
        this.view = view;
    }

    public BuscaPontosEstacoesPresenter() {    }

    public void consultaPontosEstacoesWebservice(LatLng posicao, final Context context) {

        String url = PDOConsulta.URL_BASE_WS + "obterpedproximogps?token="+ PDOConsulta.TOKEN +"&lat=" + posicao.latitude + "&lon=" + posicao.longitude+"&raio=2000";

        new PDOConsulta(new PDOConsultaCallback() {
            @Override
            public void callback(String result) {
                carregarPontosProximos(result, context);
            }
        }).execute(url);
    }

    public void carregarPontosProximos(String resultadoJson, Context context) {
        JSONArray pontosJson;
        JSONObject pontos = null;
        try{
            pontos = new JSONObject(resultadoJson);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(pontos == null || pontos.optBoolean("timeout")){
            view.setErroTimeout(context.getResources().getString(R.string.falha_de_conex_o));
            return;
        }

        pontosJson = pontos.optJSONArray("pontos");

        List<Ponto> listaDePontos = new ArrayList<>();

        if(pontosJson!=null){
            for (int i = 0; i < pontosJson.length(); i++){
                JSONObject l = pontosJson.optJSONObject(i);

                if(l!=null){
                    Ponto ponto = new Ponto();
                    ponto.setIdPonto(l.optString("id").trim());
                    ponto.setEndereco(l.optString("referencia").trim());
                    ponto.setPosicao(new LatLng(Double.parseDouble(l.optString("latitude").trim()), Double.parseDouble(l.optString("longitude").trim())));
                    ponto.setDistancia(Double.parseDouble(l.optString("distancia").trim()));

                    listaDePontos.add(ponto);
                }
            }
            int raioInicial = 1000;
            view.carregaPontosNoMapa(listaDePontos, raioInicial);
            appPresenter.setListaDePontosPorRaio(listaDePontos);
        }
    }
}
