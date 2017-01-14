package br.com.anso.pdo.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Rota {
    private ArrayList<Viagem> viagens;
    private PontoGeometrico pontoOrigem;
    private double totalDinheiro;
    private double totalCartao;

    public ArrayList<Viagem> getViagens() {
        return viagens;
    }

    public void setViagens(ArrayList<Viagem> viagens) {
        this.viagens = viagens;
    }

    public PontoGeometrico getPontoOrigem() {
        return pontoOrigem;
    }

    public void setPontoOrigem(PontoGeometrico pontoOrigem) {
        this.pontoOrigem = pontoOrigem;
    }

    public double getTotalDinheiro() {
        return totalDinheiro;
    }

    public void setTotalDinheiro(double totalDinheiro) {
        this.totalDinheiro = totalDinheiro;
    }

    public double getTotalCartao() {
        return totalCartao;
    }

    public void setTotalCartao(double totalCartao) {
        this.totalCartao = totalCartao;
    }

    public Rota(JSONObject rotaJSON) {
        JSONObject pontoOrigem = rotaJSON.optJSONObject("pontoOrigem");

        if (pontoOrigem != null)
            this.pontoOrigem = new PontoGeometrico(pontoOrigem);

        this.totalCartao = rotaJSON.optDouble("totalCartao");
        this.totalDinheiro = rotaJSON.optDouble("totalDinheiro");
        this.viagens = new ArrayList<>();

        JSONArray viagens = rotaJSON.optJSONArray("viagens");

        if(viagens != null){
            for (int i = 0; i < viagens.length(); i++){
                Viagem v = new Viagem();
                JSONObject vJson = viagens.optJSONObject(i);

                // Comum à todas as viagens
                v.setTipoTransporte(vJson.optString("tipoDeTransporte"));
                v.setDistancia(vJson.optDouble("distancia"));
                v.setPontoDestino(new PontoGeometrico(vJson.optJSONObject("pontoDestino")));
                v.setTempoViagem(vJson.optDouble("tempoViagem"));

                JSONArray polylinesArray = vJson.optJSONArray("polyline");
                ArrayList<String> polylines = new ArrayList<String>();

                if(polylinesArray != null){
                    for(int j = 0; j < polylinesArray.length(); j++){
                        polylines.add(polylinesArray.optString(j));
                    }
                }
                v.setPolylines(polylines);

                // Viagens do tipo diferente de caminhada tem estes atributos também
                if(!v.getTipoTransporte().equals("Caminhada")){
                    v.setAvisos(vJson.optString("avisos"));
                    JSONObject linhaJSON = vJson.optJSONObject("linha");

                    if(linhaJSON != null) {
                        v.setLinha(new Linha(
                                        linhaJSON.optString("routeName"),
                                        linhaJSON.optString("servico"),
                                        linhaJSON.optString("empresa"),
                                        linhaJSON.optString("corconsorcio"),
                                        linhaJSON.optString("numero"),
                                        linhaJSON.optString("vista")
                                )
                        );
                    }

                    JSONArray paradasJson = vJson.optJSONArray("paradas");
                    ArrayList<Ponto> paradas = new ArrayList<>();
                    if(paradasJson != null){
                        for(int j = 0; j < paradasJson.length(); j++){
                            Ponto p = new Ponto(paradasJson.optJSONObject(j));
                            paradas.add(p);
                        }
                    }
                    v.setParadas(paradas);
                }

                this.viagens.add(v);
            }
        }
    }
}
