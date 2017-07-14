package br.com.anso.pdo.resultadoRotas;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.anso.pdo.R;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Consulta;
import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.util.PDOConsulta;
import br.com.anso.pdo.util.PDOConsultaCallback;


public class ResultadoRotasPresenter implements IResultadoRotasView.IResultadoRotasPresenter {

    private AppSingleton app = AppSingleton.getApp();
    private IResultadoRotasView view;

    public ResultadoRotasPresenter(IResultadoRotasView view){
        this.view = view;
    }

    @Override
    public void pesquisarRotas(Consulta c, Context context) {
        pesquisarRotasWebService(c, context);
    }

    public void carregarResultadoBuscaRotas(String jsonRotas, Context context){
        JSONObject rotas = null;
        try {
            rotas = new JSONObject(jsonRotas);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray listaJson;

        if(rotas == null || rotas.optBoolean("timeout")){
            view.setErroTimeout(context.getResources().getString(R.string.falha_de_conex_o));
            return;
        }


        listaJson = rotas.optJSONArray("listaRotaItem");

        app.setListaRotas(listaJson);

        List<HashMap<String, String>> aList = new ArrayList<>();

        for (int i = 0; i < listaJson.length(); i++){
            HashMap<String, String> map = new HashMap<>();
            JSONObject l = listaJson.optJSONObject(i);
            if(l!=null){
                JSONArray viagens = l.optJSONArray("viagens");

                if(viagens == null) viagens = new JSONArray();

                int embarque = 1;
                for(int j = 0; j < viagens.length(); j++){
                    JSONObject v = viagens.optJSONObject(j);

                    if(v!=null && !v.optString("tipoDeTransporte").toLowerCase().equals("caminhada")){
                        JSONObject linhaObj = v.optJSONObject("linha");

                        if(linhaObj!=null){
                            map.put("corconsorcio"+embarque, linhaObj.optString("corconsorcio"));
                            map.put("vista"+embarque, linhaObj.optString("vista"));
                            map.put("consorcio"+embarque, linhaObj.optString("empresa"));
                        }

                        double tempoViagem = v.optDouble("tempoViagem");
                        String tempoViagemStr = Util.converterMinutosParaTempoViagemCompleto(tempoViagem, context);

                        map.put("tempoviagem"+embarque, tempoViagemStr);
                        String numTransbordo = embarque > 1 ? context.getResources().getString(R.string.onibusPlural) : context.getResources().getString(R.string.onibus);
                        map.put("numeroRota", context.getResources().getString(R.string.rota)+" "+(i+1)+ " - "+" ("+embarque+" "+numTransbordo+")");
                        embarque++;
                    }
                }
                aList.add(map);
            }
        }
        view.setListaRotas(aList);
    }

    public void pesquisarRotasWebService(Consulta c, final Context context){
        String queryString = Util.construirQueryUrlConsultaRotas(c);

        String url = PDOConsulta.URL_BASE_WS + "obterrotas?token="+ PDOConsulta.TOKEN + queryString;

        //Log.v("TAG", url);

        new PDOConsulta(new PDOConsultaCallback() {
            @Override
            public void callback(String result) {
                carregarResultadoBuscaRotas(result, context);
            }
        }).execute(url);
    }
}
