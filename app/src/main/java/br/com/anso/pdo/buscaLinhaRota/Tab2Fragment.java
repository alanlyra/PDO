package br.com.anso.pdo.buscaLinhaRota;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import br.com.anso.pdo.R;
import br.com.anso.pdo.resultadoRotas.ResultadoRotasActivity;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Util;

public class Tab2Fragment extends Fragment {

    private CharSequence text;
    int duration = Toast.LENGTH_SHORT;
    private AppSingleton app = AppSingleton.getApp();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.tab2_fragment, container, false);

        TextView localDestinoLinhasRotas = (TextView) inflatedView.findViewById(R.id.localDestinoLinhasRotas);
        TextView localPartidaLinhasRotas = (TextView) inflatedView.findViewById(R.id.localPartidaLinhasRotas);

        text = getResources().getString(R.string.definir_locais_partidas_destino);

        if(Util.nonBlank(app.getEnderecoOrigemExibicao())) {
            String municipioOrigem = " - " + app.getMunicipios().get(app.getIndexMunicipioOrigem());
            localPartidaLinhasRotas.setText(app.getEnderecoOrigemExibicao().concat(municipioOrigem));
        }

        if(Util.nonBlank(app.getEnderecoDestinoExibicao())) {
            String municipioDestino = " - " + app.getMunicipios().get(app.getIndexMunicipioDestino());
            localDestinoLinhasRotas.setText(app.getEnderecoDestinoExibicao().concat(municipioDestino));
        }

        if(app.getAbaDefault()==1)
            pesquisarRotas(inflatedView);

        return inflatedView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void pesquisarRotas(View v) {
        if(Util.nonBlank(app.getEnderecoOrigemExibicao()) && Util.nonBlank(app.getEnderecoDestinoExibicao())){
            Intent i = new Intent(getActivity(), ResultadoRotasActivity.class);
            startActivity(i);
        }
        else{
            Toast.makeText(getActivity(),
                    text, duration).show();
        }
    }
}