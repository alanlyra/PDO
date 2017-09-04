package br.com.anso.pdo.linhasdoponto;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import br.com.anso.pdo.R;
import br.com.anso.pdo.favoritos.Favorito;
import br.com.anso.pdo.itinerario.ItinerarioActivity;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Linha;
import br.com.anso.pdo.util.Ponto;
import br.com.anso.pdo.util.PdoLocationCallback;
import br.com.anso.pdo.pdoactivity.PDOAppCompatActivity;

public class LinhasdopontoActivity extends PDOAppCompatActivity implements ILinhasdopontoView, OnMapReadyCallback, PdoLocationCallback {

    private ILinhasdopontoView.ILinhasdopontoPresenter presenter;
    private GoogleMap googleMap;
    private ListView listView;
    private Marker userMaker;
    private FrameLayout frameoptions;
    private LinearLayout bar;
    private LinearLayout emptyView;

    private Favorito favorito;
    AppSingleton appSingleton = AppSingleton.getApp();


    public LinhasdopontoActivity() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.linhasdoponto);



        listView = (ListView) findViewById(R.id.listlinhasdoponto);
        emptyView = (LinearLayout) findViewById(R.id.listaLinhasNaoCarregada);

        bar = (LinearLayout) this.findViewById(R.id.barLinear);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextView tv = (TextView) findViewById(R.id.enderecoponto);
        if(tv!=null) tv.setText(getEndereco());

        exibirLoadingListaResultado();

        presenter = new LinhasdopontoPresenter(this);
    }

    public void voltarPrincipal(View view) {
        super.onBackPressed();
    }

    @Override
    public void setErroTimeout(String msg){
        bar.setVisibility(View.GONE);
        ((TextView) emptyView.findViewById(R.id.listaLinhasNaoCarregadaText)).setText(msg);
        emptyView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    @Override
    public void setListaLinhas(final List<HashMap<String, String>> aList) {

        bar.setVisibility(View.GONE);

        if(aList.size() == 0){
            ((TextView) emptyView.findViewById(R.id.listaLinhasNaoCarregadaText)).setText(getResources().getString(R.string.nao_ha_onibus_cadastrado));
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        else{
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        String[] from = {"txt", "cur", "timetext", "tempochegada", "options"};
        int[] to = {R.id.txt, R.id.cur, R.id.timetext, R.id.tempochegada, R.id.options};

        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_linhasdoponto, from, to) {
            public View getView(int position, View convertView, ViewGroup parent) {

                String routeName = aList.get(position).get("route_name");
                String servico = aList.get(position).get("servico");
                String corConsorcio = aList.get(position).get("corconsorcio");
                String referencia = aList.get(position).get("cur");
                String numero =  aList.get(position).get("txt");
                String tempo =  aList.get(position).get("tempochegada");

                View view = super.getView(position, convertView, parent);
                ImageView popUp_btn = (ImageView) view.findViewById(R.id.options);


                LinearLayout corconsorcioLayout = (LinearLayout) view.findViewById(R.id.flag);
                frameoptions = (FrameLayout) view.findViewById(R.id.frameoptionsLinhadoPonto);
                FrameLayout tempoViagem = (FrameLayout) view.findViewById(R.id.tempodeviagem);

                if (position % 2 == 1) {
                    view.setBackgroundResource(R.color.listview_linhasdoponto2);
                } else if (position % 2 == 0) {
                    view.setBackgroundResource(R.color.listview_linhasdoponto1);

                }

                if(tempo.contains("N/D"))
                    tempoViagem.setVisibility(View.GONE);
                else
                    tempoViagem.setVisibility(View.VISIBLE);

                corconsorcioLayout.setBackgroundColor(Color.parseColor(corConsorcio));
                attachPopupHandler(new Linha(routeName, servico, "", corConsorcio, numero, referencia), popUp_btn);

                return view;
            }
        };

        listView.setAdapter(adapter);
        abreTelaItinerario(aList);
    }

    private void attachPopupHandler(final Linha linha, final ImageView popUp_btn) {
        frameoptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getBaseContext(), popUp_btn);

                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                popup.getMenuInflater().inflate(R.menu.popup_filters, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            /*case R.id.favoritar:
                                favorito = new Favorito();
                                favorito.atualizaFavoritos(getBaseContext(), linha);

                                return true;*/
                            case R.id.itinerario:
                                appSingleton.setRouteNameExibirIitnerario(linha.getRouteName());
                                appSingleton.setServicoExibirIitnerario(linha.getServico());
                                appSingleton.setCorConsorcio(linha.getCorConsorcio());
                                Intent i;
                                i = new Intent(LinhasdopontoActivity.this, ItinerarioActivity.class);
                                startActivity(i);
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();
            }
        });

    }

    public void abreTelaItinerario(final List<HashMap<String, String>> aList) {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String routeName = aList.get(position).get("route_name");
                String servico = aList.get(position).get("servico");

                appSingleton.setRouteNameExibirIitnerario(routeName);
                appSingleton.setServicoExibirIitnerario(servico);
                Intent i;
                i = new Intent(LinhasdopontoActivity.this, ItinerarioActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void exibirPontoNoMapa(JSONArray linhasJson) {
        //No lugar de gps, colocar no mapa o ponto clicado na tela anterior
        Ponto ponto = new Ponto();
        ponto.setPosicao(getPosicaoGPS());
        ponto.setMarkerOptions(getResources());
        ponto.setEndereco("");

        if (this.userMaker != null) this.userMaker.remove();

        this.userMaker = googleMap.addMarker(ponto.getMarker());//marcador ponto

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ponto.getPosicao(), 15));
    }

    public void centerUsuarioMapa(View v){
        LatLng pos = getPosicaoGPS();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
    }

    @Override
    public String getPonto() {
        return appSingleton.getIdPonto();
    }

    @Override
    public String getEndereco() {
        return appSingleton.getEnderecoPonto();
    }

    @Override
    public LatLng getPosicaoGPS() {
        return appSingleton.getCoordPonto();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        presenter.carregarLinhasDoPonto(this.getBaseContext());

    }


    @Override
    public void tratarPosicaoGPS(LatLng pos) {
        presenter.carregarLinhasDoPonto(this.getBaseContext());
    }

    private void exibirLoadingListaResultado(){
        listView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        bar.setVisibility(View.VISIBLE);
    }

    public void tentarNovamente(View v){
        exibirLoadingListaResultado();
        presenter.carregarLinhasDoPonto(this.getBaseContext());
    }

}

