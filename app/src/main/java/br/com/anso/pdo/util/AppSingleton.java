package br.com.anso.pdo.util;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.anso.pdo.buscaPontosEstacoes.BuscaPontosEstacoesActivity;
import br.com.anso.pdo.principal.ExibeOnibusProximosFragment;
import br.com.anso.pdo.principal.ExibePontosProximosFragment;
import br.com.anso.pdo.principal.IPrincipalView;

/**
 * Essa classe é um singleton e armazena as informações da aplicação.
 */
public class AppSingleton implements IPrincipalView.IPrincipalPresenter {

    private static AppSingleton app;

    private ExibePontosProximosFragment viewPontos;
    private ExibeOnibusProximosFragment viewLinhas;
    private BuscaPontosEstacoesActivity viewPontosEstacoes;
    private LatLng coordPonto;

    private int metodoOrdenacaoResultados = 3; // mínimo de transbordos
    private int raioBusca = 800; // raio máximo de busca por rotas
    private int numeroOnibusProximos=0;
    private int indexMunicipioOrigem = 0;
    private int indexMunicipioDestino = 0;
    private int positionOpcaoRota;
    private int abaDefault;

    private String enderecoOrigemExibicao = "";
    private String enderecoDestinoExibicao = "";
    private String enderecoOrigemWS="";
    private String enderecoDestinoWS="";
    private String routeNameExibirIitnerario = "";
    private String servicoExibirIitnerario = "";
    private String numOnibusExibirItinerario = "";
    private String sentidoExibirItinerario = "";
    private String consorcioExibirItinerario = "";
    private String textoBuscaLinha = "";
    private String idPonto="";
    private String enderecoPonto="";
    private String corConsorcio="";
    private String localDestinoRota="";

    private JSONArray listaLinhas = new JSONArray();
    private JSONArray listaRotas = new JSONArray();

    private List<Ponto> listaDePontosPorRaio = new ArrayList<>();
    private ArrayList<String> municipios = new ArrayList<>();
    public ArrayList<Linha> linhasFavoritas = new ArrayList<>();
    public ArrayList<Ponto> pontosFavoritos = new ArrayList<>();

    public String appVersionName;
    public int appVersionCode;
    public JSONObject appVersionCheckJson;

    /* Global methods */
    public void ativaTimer(int time){
        TimerApp timer = new TimerApp();
        timer.ativaTimer(time);
    }

    public static AppSingleton getApp() {
        if( app == null )
            app = new AppSingleton();

        return app;
    }

    private AppSingleton(){}

    /***          Getters and Setters            **/

    public ExibeOnibusProximosFragment getViewLinhas() {
        return viewLinhas;
    }

    public void setViewLinhas(ExibeOnibusProximosFragment viewLinhas) {
        this.viewLinhas = viewLinhas;
    }

    public ExibePontosProximosFragment getViewPontos() {
        return viewPontos;
    }

    public void setViewPontos(ExibePontosProximosFragment viewPontos) {
        this.viewPontos = viewPontos;
    }

    public BuscaPontosEstacoesActivity getViewPontosEstacoes(){
        return this.viewPontosEstacoes;
    }

    public void setViewPontosEstacoes(BuscaPontosEstacoesActivity viewPontosEstacoes){
        this.viewPontosEstacoes = viewPontosEstacoes;
    }

    public ArrayList<Ponto> getPontosFavoritos() {
        return pontosFavoritos;
    }

    public void setPontoFavorito(Ponto pontoFavorito) {
        this.pontosFavoritos.add(pontoFavorito);
        //Util.createAndSaveFile("config.json", null, context);
    }

    public ArrayList<Linha> getLinhasFavoritas() {
        return linhasFavoritas;
    }

    public void setLinhaFavorita(Linha linhaFavorita) {
        this.linhasFavoritas.add(linhaFavorita);
        //Util.createAndSaveFile("config.json", null, context);
    }

    public void setNumeroOnibusProximos(int numeroOnibusProximos){
        this.numeroOnibusProximos = numeroOnibusProximos;
    }

    public void atualizaUsuarioViews(LatLng pos){
        ExibePontosProximosFragment viewPontos = getViewPontos();
        if(viewPontos!=null){
            viewPontos.posicionarUsuarioMapaGPS(pos);
        }

        ExibeOnibusProximosFragment viewLinhas = getViewLinhas();
        if(viewLinhas!=null){
            viewLinhas.posicionarUsuarioMapaGPS(pos);
        }

        BuscaPontosEstacoesActivity viewPontosEstacoes = getViewPontosEstacoes();
        if(viewPontosEstacoes != null){
            viewPontosEstacoes.posicionarUsuarioMapaGPS(pos);
        }
    }

    public int getNumeroOnibusProximos(){
        return this.numeroOnibusProximos;
    }

    public void setEnderecoOrigem(String enderecoOrigemExibicao, String enderecoOrigemWS) {
        this.enderecoOrigemExibicao = enderecoOrigemExibicao;
        this.enderecoOrigemWS = enderecoOrigemWS;
    }

    public void setEnderecoDestino(String enderecoDestinoExibicao, String enderecoDestinoWS) {
        this.enderecoDestinoExibicao = enderecoDestinoExibicao;
        this.enderecoDestinoWS = enderecoDestinoWS;
    }

    public String getEnderecoOrigemExibicao(){
        return this.enderecoOrigemExibicao;
    }

    public String getEnderecoDestinoExibicao(){
        return this.enderecoDestinoExibicao;
    }

    public void setCorConsorcio(String corConsorcio) {
        this.corConsorcio = corConsorcio;
    }

    public String getCorConsorcio(){
        return this.corConsorcio;
    }

    public String getRouteNameExibirIitnerario() {
        return routeNameExibirIitnerario;
    }

    public void  setRouteNameExibirIitnerario( String value) {
         routeNameExibirIitnerario = value;
    }

    public String getServicoExibirIitnerario() {
        return servicoExibirIitnerario;
    }

    public void setServicoExibirIitnerario(String servicoExibirIitnerario) {
        this.servicoExibirIitnerario = servicoExibirIitnerario;
    }

    public String getNumOnibusExibirItinerario() {
        return numOnibusExibirItinerario;
    }

    public void setNumOnibusExibirItinerario(String numOnibusExibirItinerario) {
        this.numOnibusExibirItinerario = numOnibusExibirItinerario;
    }

    public String getSentidoExibirItinerario() {
        return sentidoExibirItinerario;
    }

    public void setSentidoExibirItinerario(String sentidoExibirItinerario) {
        this.sentidoExibirItinerario = sentidoExibirItinerario;
    }

    public String getConsorcioExibirItinerario() {
        return consorcioExibirItinerario;
    }

    public void setConsorcioExibirItinerario(String consorcioExibirItinerario) {
        this.consorcioExibirItinerario = consorcioExibirItinerario;
    }

    public void setListaLinhas(JSONArray listaLinhas){
        this.listaLinhas = listaLinhas;
    }

    public JSONArray getListaLinhas(){
        return this.listaLinhas;
    }

    public String getTextoBuscaLinha() {
        return textoBuscaLinha;
    }

    public void setTextoBuscaLinha(String textoBuscaLinha) {
        this.textoBuscaLinha = textoBuscaLinha;
    }

    public ArrayList<String> getMunicipios() {
        if(municipios == null || municipios.size() == 0){
            municipios = new ArrayList<String>();
            municipios.add("Rio de Janeiro");
        }

        return municipios;
    }

    public void setMunicipios(ArrayList<String> municipios) {
        this.municipios = municipios;
    }

    public int getIndexMunicipioOrigem() {
        return indexMunicipioOrigem;
    }

    public void setIndexMunicipioOrigem(int indexMunicipioOrigem) {
        this.indexMunicipioOrigem = indexMunicipioOrigem;
    }

    public int getIndexMunicipioDestino() {
        return indexMunicipioDestino;
    }

    public void setIndexMunicipioDestino(int indexMunicipioDestino) {
        this.indexMunicipioDestino = indexMunicipioDestino;
    }

    public String getIdPonto() {
        return idPonto;
    }

    public void setIdPonto(String idPonto) {
        this.idPonto = idPonto;
    }

    public String getEnderecoPonto() {
        return enderecoPonto;
    }
    public void setEnderecoPonto(String enderecoPonto){
        this.enderecoPonto = enderecoPonto;
    }

    public void setCoordPonto(LatLng coordPonto) {
        this.coordPonto = coordPonto;
    }

    public LatLng getCoordPonto() {
        return coordPonto;
    }

    public List<Ponto> getListaDePontosPorRaio() {
        return listaDePontosPorRaio;
    }

    public void setListaDePontosPorRaio(List<Ponto> listaDePontosPorRaio) {
        this.listaDePontosPorRaio = listaDePontosPorRaio;
    }

    public int getMetodoOrdenacaoResultados() {
        return metodoOrdenacaoResultados;
    }

    public void setMetodoOrdenacaoResultados(int metodoOrdenacaoResultados) {
        this.metodoOrdenacaoResultados = metodoOrdenacaoResultados;
    }

    public int getRaioBusca() {
        return raioBusca;
    }

    public void setRaioBusca(int raioBusca) {
        this.raioBusca = raioBusca;
    }

    public void setListaRotas(JSONArray listaRotas) {
        this.listaRotas = listaRotas;
    }

    public JSONArray getListaRotas() {
        return listaRotas;
    }

    public void setPositionOpcaoRota(int positionOpcaoRota) {
        this.positionOpcaoRota = positionOpcaoRota;
    }

    public int getPositionOpcaoRota() {
        return positionOpcaoRota;
    }

    public int getAbaDefault() {
        return abaDefault;
    }

    public void setAbaDefault(int abaDefault) {
        this.abaDefault = abaDefault;
    }

    public String getEnderecoOrigemWS(){
        return enderecoOrigemWS;
    }

    public String getEnderecoDestinoWS(){
        return enderecoDestinoWS;
    }

    public void setlocalDestinoRota (String localDestinoRota ) {
        this.localDestinoRota = localDestinoRota;
    }

    public String getLocalDestinoRota(){
        return this.localDestinoRota;
    }
}

