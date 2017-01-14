package br.com.anso.pdo.resultadoRotas;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import br.com.anso.pdo.R;
import br.com.anso.pdo.rota.RotaActivity;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Consulta;
import br.com.anso.pdo.util.ConsultaFactory;

public class ResultadoRotasActivity extends Activity implements IResultadoRotasView {

    private IResultadoRotasView.IResultadoRotasPresenter presenter;
    private AppSingleton app = AppSingleton.getApp();

    private ListView listView;
    private LinearLayout emptyView;
    private LinearLayout bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_rotas);

        presenter = new ResultadoRotasPresenter(this);


        TextView tituloTextView = (TextView) findViewById(R.id.tituloResultadoBuscaRotas);
        listView = (ListView) findViewById(R.id.list);

        emptyView = (LinearLayout) findViewById(R.id.nenhumaRotaEncontrada);

        bar = (LinearLayout) this.findViewById(R.id.barLinear);
        bar.setVisibility(View.VISIBLE);

        Consulta c = ConsultaFactory.construirConsulta();

        tituloTextView.setText(getResources().getString(R.string.origem).concat(" ").concat(app.getEnderecoOrigemExibicao()).concat(" - ").concat(app.getMunicipios().get(app.getIndexMunicipioOrigem())).concat("\n").
                               concat(getResources().getString(R.string.destino)).concat(" ").concat(app.getEnderecoDestinoExibicao()).concat(" - ").concat(app.getMunicipios().get(app.getIndexMunicipioDestino())));

        presenter.pesquisarRotas(c, this.getBaseContext());
    }

    public void voltar(View view){
        super.onBackPressed();
    }

    @Override
    public void setErroTimeout(String msg){
        bar.setVisibility(View.GONE);
        ((TextView) emptyView.findViewById(R.id.nenhumaRotaEncontradaText)).setText(msg);
        emptyView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    @Override
    public void setListaRotas(final List<HashMap<String, String>> aList) {
        bar.setVisibility(View.GONE);


        if(aList.size() == 0){
            ((TextView) emptyView.findViewById(R.id.nenhumaRotaEncontradaText)).setText(getResources().getString(R.string.nenhuma_rota_encontrada));
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        else{
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        String[] from = {"consorcio1", "vista1", "tempoviagem1", "consorcio2", "numeroRota", "vista2", "tempoviagem2", "consorcio3", "vista3", "tempoviagem3"};
        int[] to = {R.id.consorcio1, R.id.vista1, R.id.tempoviagem1, R.id.consorcio2, R.id.numeroRota, R.id.vista2, R.id.tempoviagem2, R.id.consorcio3, R.id.vista3, R.id.tempoviagem3};

        SimpleAdapter adapter = new SimpleAdapter(this.getBaseContext(), aList, R.layout.listview_resultadorotas_layout, from, to) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                HashMap<String, String> map = aList.get(position);

                TextView tempoviagem1 = (TextView) view.findViewById(R.id.tempoviagem1);
                TextView tempoviagem2 = (TextView) view.findViewById(R.id.tempoviagem2);
                TextView tempoviagem3 = (TextView) view.findViewById(R.id.tempoviagem3);

                LinearLayout corconsorcioLayout;
                if(map.containsKey("corconsorcio1")){
                    corconsorcioLayout = (LinearLayout) view.findViewById(R.id.flag1);
                    corconsorcioLayout.setBackgroundColor(Color.parseColor(aList.get(position).get("corconsorcio1")));
                    view.findViewById(R.id.rotaitem1).setVisibility(LinearLayout.VISIBLE);
                    String tempo1 =  aList.get(position).get("tempoviagem1");
                    if(tempo1.contains("N/D"))
                        tempoviagem1.setVisibility(View.GONE);
                    else
                        tempoviagem1.setVisibility(View.VISIBLE);
                }else{
                    view.findViewById(R.id.rotaitem1).setVisibility(LinearLayout.GONE);
                }
                if(map.containsKey("corconsorcio2")){
                    corconsorcioLayout = (LinearLayout) view.findViewById(R.id.flag2);
                    corconsorcioLayout.setBackgroundColor(Color.parseColor(aList.get(position).get("corconsorcio2")));
                    view.findViewById(R.id.rotaitem2).setVisibility(LinearLayout.VISIBLE);
                    String tempo2 =  aList.get(position).get("tempoviagem2");
                    if(tempo2.contains("N/D"))
                        tempoviagem2.setVisibility(View.GONE);
                    else
                        tempoviagem2.setVisibility(View.VISIBLE);
                }
                else{
                    view.findViewById(R.id.rotaitem2).setVisibility(LinearLayout.GONE);
                }
                if(map.containsKey("corconsorcio3")){
                    corconsorcioLayout = (LinearLayout) view.findViewById(R.id.flag3);
                    corconsorcioLayout.setBackgroundColor(Color.parseColor(aList.get(position).get("corconsorcio3")));
                    view.findViewById(R.id.rotaitem3).setVisibility(LinearLayout.VISIBLE);
                    String tempo3 =  aList.get(position).get("tempoviagem3");
                    if(tempo3.contains("N/D"))
                        tempoviagem3.setVisibility(View.GONE);
                    else
                        tempoviagem3.setVisibility(View.VISIBLE);
                }
                else{
                    view.findViewById(R.id.rotaitem3).setVisibility(LinearLayout.GONE);
                }

                attachClickItemHandler(position, (LinearLayout) view.findViewById(R.id.itemLista));

                if (position % 2 == 1) {
                    view.setBackgroundResource(R.color.listview2);
                }
                else if (position % 2 == 0) {
                    view.setBackgroundResource(R.color.listview1);

                }

                return view;
            }
        };

        listView.setAdapter(adapter);
    }

    public Activity getActivity(){
        return this;
    }

    private void attachClickItemHandler(final int position, LinearLayout l){
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.setPositionOpcaoRota(position);
                Intent i = new Intent(getActivity(), RotaActivity.class);
                startActivity(i);

            }
        });
    }

    private void exibirLoadingListaResultado(){
        listView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        bar.setVisibility(View.VISIBLE);
    }

    public void tentarNovamente(View v){
        exibirLoadingListaResultado();
        Consulta c = ConsultaFactory.construirConsulta();
        presenter.pesquisarRotas(c, this.getBaseContext());
    }
}