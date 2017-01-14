package br.com.anso.pdo.buscaLinhaRota;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import br.com.anso.pdo.R;
import br.com.anso.pdo.resultadoListaItinerario.ResultadoListaItinerarioActivity;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Util;

public class Tab1Fragment extends Fragment {

    private EditText buscaEditText;
    private CharSequence text;
    private LinearLayout clear;
    int duration = Toast.LENGTH_SHORT;
    private AppSingleton app = AppSingleton.getApp();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        text = getResources().getString(R.string.definir_nome_numero_linha);
        return inflater.inflate(R.layout.tab1_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();

        buscaEditText = (EditText) getActivity().findViewById(R.id.nomeOuNumeroLinha);
        if(app.getTextoBuscaLinha() != null && app.getTextoBuscaLinha().length() > 0)
            buscaEditText.setText(app.getTextoBuscaLinha());

        clear = (LinearLayout)getActivity().findViewById(R.id.clear);

        if(clear!=null){
            clear.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    buscaEditText.setText("");
                }
            });
        }
    }

    public void pesquisarLinhas(View v){

        if(buscaEditText!=null) {
            String busca = buscaEditText.getText().toString();
            if (Util.nonBlank(busca)) {
                app.setTextoBuscaLinha(busca);
                Intent i = new Intent(getActivity(), ResultadoListaItinerarioActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(getActivity(),
                        text, duration).show();
            }
        }
    }
}