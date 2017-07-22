package br.com.anso.pdo.selecionarEndereco;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import br.com.anso.pdo.R;
import br.com.anso.pdo.buscaLinhaRota.BuscaLinhaRotaActivity;
import br.com.anso.pdo.principal.ExibeOnibusProximosFragment;
import br.com.anso.pdo.principal.PrincipalActivity;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Usuario;
import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.vdoactivity.VDOAppCompatActivity;


public class SelecionarEnderecoActivity extends VDOAppCompatActivity implements ISelecionarEnderecoView {

    private AutoCompleteEnderecoWidget acTextView;
    private SuggestionAdapter adapter;
    private String endereco = "";
    private String[] selection = {""};
    private Spinner municipioSpinner;
    private AppSingleton app = AppSingleton.getApp();
    private boolean partida;
    private boolean atual = false;
    private String enderecoAtual;
    private LinearLayout clear;
    private ImageView back;
    private ImageView home;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_partida_destino);

        enderecoAtual = getResources().getString(R.string.localizacao_atual);
        clear = (LinearLayout)findViewById(R.id.clear);

        Bundle b = getIntent().getExtras();
        String titulo = b.getString("titulo");


        home = (ImageView) findViewById(R.id.home);
        TextView textLocal = (TextView) findViewById(R.id.textLocal);
        if(textLocal!=null) textLocal.setText(titulo);

        partida = titulo != null && titulo.toUpperCase().contains("PARTIDA");

        back = (ImageView) findViewById(R.id.backToPrincipal);
        acTextView = (AutoCompleteEnderecoWidget) findViewById(R.id.autoComplete);
        if(acTextView!=null) acTextView.setThreshold(2);//busca a partir da terceira letra

        adapter = new SuggestionAdapter(this);
        acTextView.setAdapter(adapter);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(SelecionarEnderecoActivity.this, PrincipalActivity.class);
                startActivity(i);
            }
        });

        acTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                adapter.getResultList().clear();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        acTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                selection[0] = (String) parent.getItemAtPosition(position);
            }
        });

        if(clear!=null){
            clear.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    acTextView.setText("");
                }
            });
        }


        Button ok = (Button) findViewById(R.id.confirmarEndereco);
        if(ok!=null){
            ok.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String campo = String.valueOf(acTextView.getText()).trim();
                    String num = "";

                    if(campo.contains(",")){
                        String tmp = campo.split(",")[1].trim();
                        num = tmp.matches("\\d+") ? ", "+tmp : num;
                    }

                    if(adapter.getResultList() != null && adapter.getCount() > 0) {
                        if (!adapter.getResultList().get(0).equals("---")) {
                            acTextView.setText(adapter.getResultList().get(0).concat(num));
                        }
                        else if(adapter.getCount() > 1) {
                            acTextView.setText(adapter.getResultList().get(1));
                        }
                    }

                    enderecoSelecionado(acTextView.getEditableText().toString());
                }
            });
        }

        back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i;
                        i = new Intent(SelecionarEnderecoActivity.this, PrincipalActivity.class);
                        startActivity(i);
                    }
        });


        //time.restartUserTime();

        constroiListaMunicipios(this.partida);
    }


    private void constroiListaMunicipios(final boolean isPartida) {
        // Verifico Lista de municípios pois quando há uma Uncaught Exception o ArrayList fica nulo
        if(app.getMunicipios() == null || app.getMunicipios().size() == 0){
            Util.carregarListaMunicipiosAtendidos();
        }

        this.municipioSpinner = (Spinner) findViewById(R.id.listaMunicipios);

        String[] itens = app.getMunicipios().toArray(new String[app.getMunicipios().size()]);
        ArrayAdapter<String> municipioAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_layout, itens);
        this.municipioSpinner.setAdapter(municipioAdapter);

        if(isPartida)
            this.municipioSpinner.setSelection(app.getIndexMunicipioOrigem());
        else
            this.municipioSpinner.setSelection(app.getIndexMunicipioDestino());

        this.municipioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isPartida)
                    app.setIndexMunicipioOrigem(position);
                else
                    app.setIndexMunicipioDestino(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void enderecoSelecionado(String selecionado) {


        endereco = selecionado;
        setEnderecos(endereco,endereco);

        Util.executaCallback("enderecoSelecionado", this);

        Intent i;
        i = new Intent(SelecionarEnderecoActivity.this, BuscaLinhaRotaActivity.class);
        startActivity(i);

        //super.onBackPressed();
    }

    public void setEnderecos(String endereco, String enderecoWS){
        if(partida) {
            app.setEnderecoOrigem(endereco, enderecoWS);
        }
        else{
            app.setEnderecoDestino(endereco, enderecoWS);
        }
    }

    /*public void voltar(View view){
        super.onBackPressed();
    }*/

    @Override
    public String getEndereco() {

        if (atual) {
            return enderecoAtual;
        } else {
            return endereco;

        }
    }


    public LatLng getPosicao(){
        return Usuario.getInstance().getPosicao();
    }

    public void definirPosicaoAtual(View view){
        atual = true;
        String pontoWS = "POINT(";
        double lat = getPosicao().latitude;
        double lon = getPosicao().longitude;
        pontoWS=pontoWS+lon+" "+lat+")";
        enderecoSelecionado(pontoWS);
        if(partida) {
            app.setEnderecoOrigem(enderecoAtual, pontoWS);
        }
        else{
            app.setEnderecoDestino(enderecoAtual, pontoWS);
        }
    }

    public String getMunicipioSelecionado(){
            return this.municipioSpinner.getSelectedItem().toString();
    }
}
