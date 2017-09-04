package br.com.anso.pdo.resultadoListaItinerario;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import br.com.anso.pdo.R;
import br.com.anso.pdo.itinerario.ItinerarioActivity;
import br.com.anso.pdo.principal.PrincipalActivity;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Linha;

public class ResultadoListaItinerarioActivity extends Activity implements IResultadoListaItinerarioView {

    private IResultadoListaItinerarioView.IResultadoListaItinerarioPresenter presenter;
    private AppSingleton app = AppSingleton.getApp();
    private ListView listView;
    private LinearLayout emptyView;
    private LinearLayout bar;
    private LinearLayout optionsLayout;
    private ImageView home;
    private LinearLayout flag5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_lista_itinerario);

        TextView tituloTextView = (TextView) findViewById(R.id.tituloResultadoBuscaLinha);
        listView = (ListView) findViewById(R.id.list);

        emptyView = (LinearLayout) findViewById(R.id.nenhumaLinhaEncontrada);

        home = (ImageView) findViewById(R.id.home);
        bar = (LinearLayout) this.findViewById(R.id.barLinear);
        bar.setVisibility(View.VISIBLE);

        tituloTextView.setText(getResources().getString(R.string.resultado_busca_por).concat(app.getTextoBuscaLinha().concat("\"")));

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(ResultadoListaItinerarioActivity.this, PrincipalActivity.class);
                startActivity(i);
            }
        });

        exibirLoadingListaResultado();
        presenter = new ResultadoListaItinerarioPresenter(this);
        presenter.pesquisarLinhas(app.getTextoBuscaLinha(), this.getBaseContext());
    }

    public void voltar(View view){
        super.onBackPressed();
    }

    @Override
    public void setErroTimeout(String msg){
        bar.setVisibility(View.GONE);
        ((TextView) emptyView.findViewById(R.id.nenhumaLinhaEncontradaText)).setText(msg);
        emptyView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    @Override
    public void setListaLinhas(final List<HashMap<String, String>> aList) {
        bar.setVisibility(View.GONE);

        if(aList.size() == 0){
            ((TextView) emptyView.findViewById(R.id.nenhumaLinhaEncontradaText)).setText(getResources().getString(R.string.nenhuma_linha_encontrada));
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        else{
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        String[] from = {"vista", "consorcio", "options"};
        int[] to = {R.id.vista, R.id.consorcio, R.id.options};

        SimpleAdapter adapter = new SimpleAdapter(this.getBaseContext(), aList, R.layout.listview_resultadolinhas_layout, from, to) {

            public View getView(int position, View convertView, ViewGroup parent) {
                Linha linha = new Linha();

                linha.setRouteName(aList.get(position).get("route_name"));
                linha.setServico(aList.get(position).get("servico"));
                linha.setCorConsorcio(aList.get(position).get("corconsorcio"));
                linha.setNumero(aList.get(position).get("linha"));
                linha.setVista(aList.get(position).get("vista"));
                linha.setConsorcio(aList.get(position).get("consorcio"));

                View view = super.getView(position, convertView, parent);

                LinearLayout corconsorcioLayout = (LinearLayout) view.findViewById(R.id.flag);
                optionsLayout = (LinearLayout) view.findViewById(R.id.optionsLayout);
                flag5 = (LinearLayout) view.findViewById(R.id.flag5);

                corconsorcioLayout.setBackgroundColor(Color.parseColor(aList.get(position).get("corconsorcio")));


                attachPopupHandler(linha, optionsLayout);
                attachClickItemHandler(linha.getRouteName(), linha.getServico(), (LinearLayout) view.findViewById(R.id.itemLista));

                if (position % 2 == 1) {
                    view.setBackgroundResource(R.color.color_primary2);
                    flag5.setBackgroundResource(R.color.color_primary2);
                }
                else if (position % 2 == 0) {
                    view.setBackgroundResource(R.color.color_primary4);
                    flag5.setBackgroundResource(R.color.color_primary4);
                }

                return view;
            }
        };

        listView.setAdapter(adapter);
    }

    public Activity getActivity(){
        return this;
    }

    private void attachClickItemHandler(final String routeName, final String servico, LinearLayout l){
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.setRouteNameExibirIitnerario(routeName );
                app.setServicoExibirIitnerario( servico );
                Intent i = new Intent(getActivity(), ItinerarioActivity.class);
                startActivity(i);

            }
        });
    }

    private void attachPopupHandler(final Linha linha, final LinearLayout popUp_btn) {
        optionsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                app.setRouteNameExibirIitnerario(linha.getRouteName());
                app.setServicoExibirIitnerario(linha.getServico());
                app.setCorConsorcio(linha.getCorConsorcio());
                Intent i;
                i = new Intent(getActivity(), ItinerarioActivity.class);
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
        presenter.pesquisarLinhas(app.getTextoBuscaLinha(), this.getBaseContext());
    }

}
