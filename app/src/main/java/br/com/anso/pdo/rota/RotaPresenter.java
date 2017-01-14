package br.com.anso.pdo.rota;


import org.json.JSONException;
import org.json.JSONObject;

import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Rota;

public class RotaPresenter implements IRotaView.IRotaPresenter {
    IRotaView view;
    private AppSingleton app = AppSingleton.getApp();

    public RotaPresenter(IRotaView view){
        this.view = view;
    }

    @Override
    public void carregarRotas() throws JSONException {
        JSONObject rotaJSON = app.getListaRotas().optJSONObject(app.getPositionOpcaoRota());

        if(rotaJSON!=null){
            // TODO: calcular isso para as cinco rotas e manter no singleton ao inves de calcular sempre quando abre a tela de resultado ;)
            Rota r = new Rota(rotaJSON);

            view.exibirRotaNoMapa(r);
        }
    }
}
