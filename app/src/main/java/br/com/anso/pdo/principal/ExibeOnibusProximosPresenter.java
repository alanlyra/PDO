package br.com.anso.pdo.principal;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.anso.pdo.R;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Linha;
import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.util.PDOConsulta;
import br.com.anso.pdo.util.PDOConsultaCallback;


public class ExibeOnibusProximosPresenter implements IPrincipalTab1View.IPrincipalTab1Presenter {

    IPrincipalTab1View view;
    private AppSingleton appPresenter = AppSingleton.getApp();

    public ExibeOnibusProximosPresenter(IPrincipalTab1View view){
        this.view = view;
    }

    public void carregarLinhasProximas(LatLng posicao, final Context context) {
        String url = PDOConsulta.URL_BASE_WS + "buscarlinhasproximasgps?token="+ PDOConsulta.TOKEN +"&lat=" + posicao.latitude + "&lon=" + posicao.longitude;

        new PDOConsulta(new PDOConsultaCallback() {
            @Override
            public void callback(String result) {
                carregarLinhasProximas(result, context);
            }
        }).execute(url);
    }

    private void carregarLinhasProximas(String resultadoJson, Context context) {

        JSONObject linhas = null;
        try {
            linhas = new JSONObject(resultadoJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray listaJson;

        if(linhas != null){
            try {
                listaJson = linhas.getJSONArray("linhas");
            } catch (JSONException e) {
                listaJson = new JSONArray();
                e.printStackTrace();
            }
        }
        else
            listaJson = new JSONArray();

        appPresenter.setListaLinhas(listaJson);

        int qtdResultLinhas = listaJson.length();

        int[] options = new int[qtdResultLinhas];

        for(int i = 0; i < qtdResultLinhas; i++)
            options[i] = R.drawable.more;


        List<HashMap<String, String>> aList = new ArrayList<>();
        ArrayList<Linha> listaDeLinhas = new ArrayList<>();

        for (int i = 0; i < listaJson.length(); i++){
            HashMap<String, String> map = new HashMap<>();
            JSONObject l = listaJson.optJSONObject(i);

            if(l!=null){
                String consorcio = l.optString("consorcio").toLowerCase().replaceAll("consórcio", "").trim();
                String tempo_chegada = "N/D";
                if(l.optDouble("tempo_chegada") > 0)
                    tempo_chegada = Util.converterMinutosParaTempoViagem(l.optDouble("tempo_chegada"), context).replace("de viagem", "");
                map.put("corconsorcio", l.optString("corconsorcio"));
                map.put("txt", l.optString("numero_linha"));
                map.put("cur", l.optString("vista") + "(SERVIÇO: " + l.optString("servico") + ")");
                map.put("route_name", l.optString("route_name"));
                map.put("servico", l.optString("servico"));
                map.put("consorcio", consorcio);
                map.put("timetext", context.getResources().getString(R.string.tempo_chegada));
                map.put("tempochegada", tempo_chegada);
                map.put("options", Integer.toString(options[i]));

                aList.add(map);

                JSONArray posicoes = l.optJSONArray("posicoes");
                if(posicoes!=null){
                    for (int j = 0; j < posicoes.length(); j++) {
                        Linha linhaObject = new Linha();
                        JSONObject posicao = posicoes.optJSONObject(j);

                        if(posicao!=null){
                            linhaObject.setPosicao(new LatLng(posicao.optDouble("latitude"), posicao.optDouble("longitude")));
                            linhaObject.setNumero(l.optString("numero_linha"));
                            linhaObject.setDataHora(Util.optString(posicao, "datahora"));
                            linhaObject.setOrdem(Util.optString(posicao, "ordem"));
                            linhaObject.setSentido(Util.optString(posicao, "sentido"));

                            listaDeLinhas.add(linhaObject);
                        }

                    }
                }
            }
        }
        view.setListaLinhas(aList);
        view.exibirLinhasNoMapa(listaDeLinhas);
    }
}
