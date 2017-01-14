package br.com.anso.pdo.linhasdoponto;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.anso.pdo.R;
import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.util.VDOConsulta;
import br.com.anso.pdo.util.VDOConsultaCallback;


public class LinhasdopontoPresenter implements ILinhasdopontoView.ILinhasdopontoPresenter {

    ILinhasdopontoView view;

    public LinhasdopontoPresenter(ILinhasdopontoView view){
        this.view = view;
    }

    public void carregarLinhasDoPonto(Context context) {
        consultaLinhasWebservice(view.getPonto(), context);
    }

    public void carregarLinhasDoPonto(String resultadoJson, Context context) {
        JSONObject linhas = null;
        try {
            linhas = new JSONObject(resultadoJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray listaJson;

        if(linhas == null || linhas.optBoolean("timeout")){
            view.setErroTimeout(context.getResources().getString(R.string.falha_de_conex_o));
            return;
        }


        listaJson = linhas.optJSONArray("linhas");

        int qtdResultLinhas = listaJson.length();

        int[] options = new int[qtdResultLinhas];
        for(int i = 0; i < qtdResultLinhas; i++)
            options[i] = R.drawable.more;

        List<HashMap<String, String>> aList = new ArrayList<>();

        for (int i = 0; i < listaJson.length(); i++){
            HashMap<String, String> listaLinhasJSON = new HashMap<>();
            JSONObject l = listaJson.optJSONObject(i);

            if(l!=null){
                String consorcio = l.optString("consorcio").toLowerCase().replaceAll("consórcio", "").trim();
                String tempo_chegada = "N/D";
                if(l.optDouble("tempo_chegada") > 0)
                    tempo_chegada = Util.converterMinutosParaTempoViagem(l.optDouble("tempo_chegada"), context).replace("de viagem", "");
                listaLinhasJSON.put("servico", l.optString("servico"));
                listaLinhasJSON.put("route_name", l.optString("route_name"));
                listaLinhasJSON.put("corconsorcio", l.optString("corconsorcio"));
                listaLinhasJSON.put("txt", l.optString("numero_linha"));
                listaLinhasJSON.put("cur", l.optString("vista") + "(SERVIÇO: " + l.optString("servico") + ")");
                listaLinhasJSON.put("route", l.optString("route_name"));
                listaLinhasJSON.put("consorcio", consorcio);
                listaLinhasJSON.put("timetext", context.getResources().getString(R.string.tempo_chegada));
                listaLinhasJSON.put("tempochegada", tempo_chegada);
                listaLinhasJSON.put("options", Integer.toString(options[i]));

                aList.add(listaLinhasJSON);
            }
        }
        view.setListaLinhas(aList);
        view.exibirPontoNoMapa(listaJson);
    }

    public void consultaLinhasWebservice(String ponto, final Context context) {
        String url = VDOConsulta.URL_BASE_WS + "obterlinhasdoponto?token="+ VDOConsulta.TOKEN + "&ponto_id="+ponto;

        new VDOConsulta(new VDOConsultaCallback() {
            @Override
            public void callback(String result) {
                carregarLinhasDoPonto(result, context);
            }
        }).execute(url);
    }

}
