package br.com.anso.pdo.itinerario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.util.VDOConsulta;
import br.com.anso.pdo.util.VDOConsultaCallback;


public class ItinerarioPresenter implements IItinerarioView.IItinerarioPresenter{
    IItinerarioView view;
    private JSONArray listaItinerariosJson = null;
    private int tempo;

    public ItinerarioPresenter(IItinerarioView view) {
        this.view = view;
    }

    public void carregarItinerariosWebservice() {
        if( Util.nonBlank( view.getRouteName() ) && Util.nonBlank( view.getServico() ) ) {
            String url = VDOConsulta.URL_BASE_WS + "obteritinerarioslinha?token=" + VDOConsulta.TOKEN + "&routeName=" + view.getRouteName() + "&servico=" + view.getServico();

            //Log.d( "URL: ", url);
            new VDOConsulta(new VDOConsultaCallback() {
                @Override
                public void callback(String result) {
                    carregarListaDeItinerarios(result);
                }
            }).execute(url);
        }
    }

    public void carregarListaDeItinerarios(String result) {
        //Log.d( "carregarItinerarios", "\n" + result );

        JSONObject linhas = null;
        try {
            linhas = new JSONObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (linhas != null) {
            listaItinerariosJson = linhas.optJSONArray("itinerarios");
        } else
            listaItinerariosJson = new JSONArray();



        int createNewList = 1;
        int sentidoInicial = 0;
        carregarItinerarios(sentidoInicial, createNewList);
    }



    public void carregarItinerarios(int sentido, int createNewList){
        boolean mostrarBotaoInverter = true;

        if(listaItinerariosJson.length() < 2){
            mostrarBotaoInverter = false;
        }

        if(listaItinerariosJson!=null){
            JSONArray listaJson = listaItinerariosJson.optJSONObject(sentido).optJSONArray("paradas");

            List<HashMap<String, String>> aList = new ArrayList<>();


            try {
                tempo = listaItinerariosJson.optJSONObject(sentido).getInt("tempo_viagem");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if(listaJson!=null) {
                for (int i = 0; i < listaJson.length(); i++) {
                    HashMap<String, String> listaItinerariosJSON = new HashMap<>();
                    JSONObject l = listaJson.optJSONObject(i);

                    if(l!=null){
                        listaItinerariosJSON.put("referencia", l.optString("referencia"));
                        listaItinerariosJSON.put("ponto_id", l.optString("ponto_id"));

                        aList.add(listaItinerariosJSON);
                    }
                }
            }

            if(tempo>0) {
                if (createNewList == 1)
                    view.setListaItinerarios(aList, Util.converterMinutosParaTempoViagemSimples(tempo));
            }
            else{
                if (createNewList == 1)
                    view.setListaItinerarios(aList, "N/D");
            }
        }

        JSONObject lista = listaItinerariosJson.optJSONObject(sentido);
        if(lista!=null)
            view.exibirItinerariosNoMapa(lista, mostrarBotaoInverter);
    }
}
