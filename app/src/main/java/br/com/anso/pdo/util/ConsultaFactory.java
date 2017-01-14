package br.com.anso.pdo.util;

/**
 * Created by christian on 5/12/16.
 *
 * Retorna um objeto Consulta com os dados de endereços atuais guardados nas variáveis globais
 */

public class ConsultaFactory {

    private static final AppSingleton app = AppSingleton.getApp();

    private ConsultaFactory(){ }

    public static Consulta construirConsulta(){

        Consulta c = new ConsultaImpl();

        c.raioBusca = app.getRaioBusca();
        c.metodoOrdenacao = app.getMetodoOrdenacaoResultados();

        if(Util.nonBlank(app.getEnderecoOrigemWS())){
            String[] endereco = app.getEnderecoOrigemWS().split(",");
            String logradouro = endereco[0].trim();
            String numero = (endereco.length > 1 ? endereco[1].trim() : "1");
            String municipio = app.getMunicipios().get(app.getIndexMunicipioOrigem());

            c.logradouroOrigem = logradouro;
            c.numeroOrigem = numero;
            c.municipioOrigem = municipio;
        }

        if(Util.nonBlank(app.getEnderecoDestinoWS())){
            String[] endereco = app.getEnderecoDestinoWS().split(",");
            String logradouro = endereco[0].trim();
            String numero = (endereco.length > 1 ? endereco[1].trim() : "1");
            String municipio = app.getMunicipios().get(app.getIndexMunicipioDestino());

            c.logradouroDestino = logradouro;
            c.numeroDestino = numero;
            c.municipioDestino = municipio;
        }

        return c;
    }
}

class ConsultaImpl extends Consulta{ }