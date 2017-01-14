package br.com.anso.pdo.principal;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.anso.pdo.R;
import br.com.anso.pdo.util.VDOConsulta;
import br.com.anso.pdo.util.VDOConsultaCallback;


public class ExibePontosProximosPresenter implements IPrincipalTab2View.IPrincipalTab2Presenter {

    IPrincipalTab2View view;
    int raio = 500;

    public ExibePontosProximosPresenter(IPrincipalTab2View view){
        this.view = view;
    }

    public void carregarPontosProximos(LatLng posicao, final Context context){
        String url = VDOConsulta.URL_BASE_WS + "obterpedproximogps?token="+ VDOConsulta.TOKEN +"&lat=" + posicao.latitude + "&lon=" + posicao.longitude+"&raio="+raio;

        new VDOConsulta(new VDOConsultaCallback() {
            @Override
            public void callback(String result) {
                carregarPontosProximos(result, context);
            }
        }).execute(url);
    }

    private void carregarPontosProximos(String resultadoJson, Context context) {
        JSONObject pontos = null;
        try{
            pontos = new JSONObject(resultadoJson);
        }catch (Exception e){
            e.printStackTrace();
        }

        JSONArray pontosJson;

        if(pontos != null) {
            pontosJson = pontos.optJSONArray("pontos");
            if(pontosJson == null)
                pontosJson = new JSONArray();
        }
        else
            pontosJson = new JSONArray();

        int qtdPontos = pontosJson.length();

        int[] options = new int[qtdPontos];

        for(int i = 0; i < qtdPontos; i++)
            options[i] = R.drawable.more;

        List<HashMap<String, String>> aList = new ArrayList<>();

        for (int i = 0; i < pontosJson.length(); i++){
            HashMap<String, String> listaPontosJSON = new HashMap<>();
            JSONObject l = pontosJson.optJSONObject(i);

            if(l!=null){
                listaPontosJSON.put("id", l.optString("id").trim());
                listaPontosJSON.put("txt", l.optString("referencia").trim());
                listaPontosJSON.put("longitude", l.optString("longitude").trim());
                listaPontosJSON.put("latitude", l.optString("latitude").trim());
                listaPontosJSON.put("distancia", l.optString("distancia").trim());
                listaPontosJSON.put("timetext", context.getResources().getString(R.string.a_pe));
                //listaPontosJSON.put("tempochegada", "N/D"); //Acertar com o tempo de chegada real
                listaPontosJSON.put("options", Integer.toString(options[i]));

                aList.add(listaPontosJSON);
            }
        }
        view.setListaPontosProximos(aList);
        view.carregaPontosNoMapa(pontosJson);
    }
}
