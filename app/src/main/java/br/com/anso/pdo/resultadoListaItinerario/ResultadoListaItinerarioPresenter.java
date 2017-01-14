package br.com.anso.pdo.resultadoListaItinerario;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.anso.pdo.R;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.VDOConsulta;
import br.com.anso.pdo.util.VDOConsultaCallback;


public class ResultadoListaItinerarioPresenter implements IResultadoListaItinerarioView.IResultadoListaItinerarioPresenter {

    private IResultadoListaItinerarioView view;
    private AppSingleton app = AppSingleton.getApp();

    public ResultadoListaItinerarioPresenter(IResultadoListaItinerarioView view){
        this.view = view;
    }

    @Override
    public void pesquisarLinhas(String textoBusca, Context context) {
        buscarLinhasWebservice(textoBusca, context);
    }

    private void carregarResultadoBuscaLinhas(String resultadoJson, Context context){
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

        app.setListaLinhas(listaJson);

        int qtdResultLinhas = listaJson.length();

        int[] options = new int[qtdResultLinhas];
        for(int i = 0; i < qtdResultLinhas; i++)
            options[i] = R.drawable.more;

        List<HashMap<String, String>> aList = new ArrayList<>();

        for (int i = 0; i < listaJson.length(); i++){
            HashMap<String, String> map = new HashMap<>();
            JSONObject l = listaJson.optJSONObject(i);

            if(l!=null){
                map.put("corconsorcio", l.optString("corconsorcio"));
                map.put("linha", l.optString("numero_linha"));
                map.put("vista", l.optString("vista") + " (SERVIÇO: " + l.optString("servico") + ")");
                map.put("route_name", l.optString("route_name"));
                map.put("servico", l.optString("servico"));
                map.put("consorcio", l.optString("consorcio"));
                map.put("options", Integer.toString(options[i]));

                aList.add(map);
            }
        }
        view.setListaLinhas(aList);
    }

    public void buscarLinhasWebservice(String textoBusca, final Context context) {
        String url = VDOConsulta.URL_BASE_WS + "obterlistalinhas?token="+ VDOConsulta.TOKEN +"&consulta=" + textoBusca.replaceAll(" ", "+");

        new VDOConsulta(new VDOConsultaCallback() {
            @Override
            public void callback(String result) {
                carregarResultadoBuscaLinhas(result, context);
            }
        }).execute(url);
    }
}
