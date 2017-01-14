package br.com.anso.pdo.chegarAoPonto;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.anso.pdo.util.Caminhada;
import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.util.VDOConsulta;
import br.com.anso.pdo.util.VDOConsultaCallback;

public class ChegarAoPontoPresenter implements IChegarAoPontoView.IChegarAoPontoPresenter {

    IChegarAoPontoView view;

    public ChegarAoPontoPresenter(IChegarAoPontoView view) {
        this.view = view;
    }

    public void carregarComoChegarAoPontoWebservice() {
        if(view.getOrigem() != null && view.getDestino() != null) {
            String url = VDOConsulta.URL_BASE_WS + "obtercomochegarcaminhada?token=" + VDOConsulta.TOKEN +
                    "&origemX=" + view.getOrigem().longitude + "&origemY=" + view.getOrigem().latitude +
                    "&destinoX=" + view.getDestino().longitude + "&destinoY=" + view.getDestino().latitude;

            new VDOConsulta(new VDOConsultaCallback() {
                @Override
                public void callback(String result) {
                    carregarListaDeCaminhadas(result);
                }
            }).execute(url);
        }
    }

    private void carregarListaDeCaminhadas(String result) {
        JSONObject linhas = null;
        try {
            linhas = new JSONObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray listaCaminhadasJson;
        if (linhas != null) {
            listaCaminhadasJson = linhas.optJSONArray("trajeto");
        } else
            listaCaminhadasJson = new JSONArray();

        carregarCaminhadas(listaCaminhadasJson);
    }

    public void carregarCaminhadas(JSONArray listaCaminhadasJson){
        List<Caminhada> aList = new ArrayList<>();

        for (int i = 0; i < listaCaminhadasJson.length(); i++){
            Caminhada caminhada = new Caminhada();
            JSONObject l = listaCaminhadasJson.optJSONObject(i);

            if(l!=null){
                caminhada.setPolilinha(Util.optString(l, "polyline"));
                caminhada.setRua(Util.optString(l, "rua"));
                caminhada.setDistancia(Double.parseDouble(Util.optString(l, "distancia")));
                caminhada.setInstrucao(Util.optString(l, "instrucao"));

                aList.add(caminhada);
            }
        }

        view.setListaCaminhada(aList);
    }
}
