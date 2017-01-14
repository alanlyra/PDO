package br.com.anso.pdo.itinerario;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public interface IItinerarioView {

    String getRouteName();
    String getServico();
    String getNumOnibusExibirItinerario();
    String getSentidoExibirItinerario();
    String getConsorcioExibirItinerario();

    void setListaItinerarios(List<HashMap<String, String>> aList, String tempo);

    void exibirItinerariosNoMapa(JSONObject listaJson, boolean mostrarBotaoInverter);

    interface IItinerarioPresenter {
        void carregarItinerarios(int sentido, int createNewList);
        void carregarItinerariosWebservice();


    }
}
