package br.com.anso.pdo.itinerario;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.anso.pdo.R;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Linha;
import br.com.anso.pdo.util.Ponto;
import br.com.anso.pdo.util.Usuario;
import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.vdoactivity.VDOAppCompatActivity;


public class ItinerarioActivity extends VDOAppCompatActivity implements OnMapReadyCallback, IItinerarioView {

    private ArrayList<View> viewsList;
    private HashMap<String, Marker> markersMap;
    private IItinerarioView.IItinerarioPresenter presenter;
    private GoogleMap googleMap;
    private ListView listView;
    private Button botaoDeInversao;
    private Polyline line;
    private AppSingleton appSingleton = AppSingleton.getApp();
    private SlidingUpPanelLayout layout;
    private int sentido = 0;//0 - IDA   1 - VOLTA
    private TextView consorcioTV;
    private TextView numLinhaTV;
    private TextView sentidoTV;
    private LinearLayout corConsorcio;
    private Button detalhesItinerario;
    SimpleAdapter adapter;

    private int positionListView = 0;

    private LinearLayout bar;
    private LinearLayout emptyView;
    private Usuario usuario;
    private Timer timer = null;
    private int DELAY   = 280;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itinerario);
        usuario = Usuario.getInstance();
        viewsList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list);
        emptyView = (LinearLayout) findViewById(R.id.itinerarioNaoCarregado);

        bar = (LinearLayout) this.findViewById(R.id.barLinear);

        consorcioTV = (TextView) findViewById(R.id.consorcio);
        numLinhaTV = (TextView) findViewById(R.id.num_linha);
        sentidoTV = (TextView) findViewById(R.id.sentido);
        botaoDeInversao = (Button) findViewById(R.id.botaoDeInversao);
        corConsorcio = (LinearLayout) findViewById(R.id.flag_itinerario);
        layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layoutItinerario);
        detalhesItinerario = (Button) findViewById(R.id.botaoDeItinerario);




        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        exibirLoadingListaResultado();

        presenter = new ItinerarioPresenter(this);
    }

    public void voltarPrincipal(View view) {
        super.onBackPressed();
    }

    public void MostrarDetalhesItinerario (View v){
        if(layout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
            layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            detalhesItinerario.setText(R.string.detalhes_Itinerario_Abrir);
        }
        if(layout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
            layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            detalhesItinerario.setText(R.string.detalhes_Itinerario_Fechar);
        }

    }

    public void InverterPartidaeDestino(View v) {


        /*RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        ImageView image= (ImageView) findViewById(R.id.botaoDeInversao);

        image.startAnimation(rotate);*/

        if (sentido == 1)
            sentido = 0;
        else
            sentido = 1;

        positionListView = 0;
        
        final Handler handler          = new Handler();
        final Runnable mRunnable        = new Runnable() {
            public void run() {
                line.remove();//remove polilinha anterior
                viewsList.clear();
                presenter.carregarItinerarios(sentido, 1);
            }
        };
        TimerTask timertask = new TimerTask() {
            @Override
            public void run() {
                handler.post(mRunnable);
            }
        };
        timer   =   new Timer();
        timer.schedule(timertask, DELAY);


    }

    public void exibirItinerariosNoMapa(JSONObject itinerarioJSON, boolean mostrarBotaoInverter) {
        if (mostrarBotaoInverter)
            botaoDeInversao.setVisibility(View.VISIBLE);

        JSONArray itinerariosJSON = itinerarioJSON.optJSONArray("paradas");
        JSONArray gpsJSON = itinerarioJSON.optJSONArray("gps");

        if (googleMap != null) {
            if(markersMap != null){
                for(String k : markersMap.keySet()){
                    markersMap.get(k).remove();
                }
                markersMap.clear();
            }
            else markersMap = new HashMap<>();

            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(getBaseContext());
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(getBaseContext());
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(getBaseContext());
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(Html.fromHtml(marker.getSnippet()));

                    info.addView(title);
                    info.addView(snippet);


                    return info;
                }
            });

            if(gpsJSON!=null){
                for (int i = 0; i < gpsJSON.length(); i++) {
                    JSONObject gpsOnibus = gpsJSON.optJSONObject(i);
                    Linha linha = new Linha();

                    linha.setOrdem(gpsOnibus.optString("ordem"));
                    linha.setNumero(gpsOnibus.optString("linha"));
                    linha.setDataHora(gpsOnibus.optString("datahora"));
                    linha.setSentido(gpsOnibus.optString("sentido"));
                    linha.setPosicao(new LatLng(gpsOnibus.optDouble("latitude"), gpsOnibus.optDouble("longitude")));
                    linha.setMarkerOptions(getResources(), this.getBaseContext());

                    Marker b = googleMap.addMarker(linha.getMarker());//marcador dos onibus proximos

                    markersMap.put(linha.getOrdem(), b);
                }
            }

            PolylineOptions options = new PolylineOptions().width(10).color(Color.parseColor("#002F3F")).geodesic(true);

            if(itinerariosJSON!=null){
                for (int i = 0; i < itinerariosJSON.length(); i++) {
                    Marker b;

                    JSONObject parada = itinerariosJSON.optJSONObject(i);
                    Ponto ponto = new Ponto();
                    ponto.setPosicao(new LatLng(parada.optDouble("latitude"), parada.optDouble("longitude")));
                    ponto.setEndereco(parada.optString("referencia"));

                    if (i == 0) {
                        ponto.setMarkerOptionsParadaHighlight(getResources());
                        b = googleMap.addMarker(ponto.getMarkerOptionsParadaHighlight());

                    } else {
                        ponto.setMarkerOptionsParadas(getResources());
                        b = googleMap.addMarker(ponto.getMarkerOptionsParada());
                    }
                    markersMap.put(parada.optString("ponto_id"), b);
                }
            }


            List<LatLng> posicoes = Util.decodePoly(itinerarioJSON.optString("polyline"));
            String consorcio = itinerarioJSON.optString("consorcio");
            String numOnibus = Util.optString(itinerarioJSON, "numero_linha");
            String vista = Util.optString(itinerarioJSON, "vista");
            String sentido = itinerarioJSON.optString("sentido");
            String consorcioCor = itinerarioJSON.optString("corconsorcio");

            appSingleton.setConsorcioExibirItinerario(consorcio);
            appSingleton.setNumOnibusExibirItinerario(numOnibus.equals("-") ? vista : numOnibus);//quando uma linha não possui numero ela vem com "-" do banco por padrão
            appSingleton.setSentidoExibirItinerario(sentido);
            appSingleton.setCorConsorcio(consorcioCor);

            consorcioTV.setText(getConsorcioExibirItinerario());
            numLinhaTV.setText(getNumOnibusExibirItinerario());
            corConsorcio.setBackgroundColor(Color.parseColor(appSingleton.getCorConsorcio()));

            String txtsentido = Util.setString(getResources().getString(R.string.sentido), "#95a4a6");
            String sentidoLinha = Util.setStringNegrito(getSentidoExibirItinerario(), "2c3e50");

            sentidoTV.setText(Html.fromHtml(txtsentido + " " + sentidoLinha));

            if(posicoes!=null){
                for (int i = 0; i < posicoes.size(); i++) {
                    options.add(posicoes.get(i));
                }

                line = googleMap.addPolyline(options);

                LatLngBounds.Builder b = new LatLngBounds.Builder();

                List<LatLng> arr = line.getPoints();
                for (int i = 0; i < arr.size(); i++) {
                    b.include(arr.get(i));
                }

                LatLngBounds bounds = b.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
            }
        }
    }

    public void centerUsuarioMapa(View v){
        usuario.recentralizarGPS(this);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(usuario.getPosicao(), 15));
    }

    @Override
    public String getRouteName() {
        return appSingleton.getRouteNameExibirIitnerario();
    }

    @Override
    public String getServico() {
        return appSingleton.getServicoExibirIitnerario();
    }

    @Override
    public String getNumOnibusExibirItinerario() {
        return appSingleton.getNumOnibusExibirItinerario();
    }

    @Override
    public String getSentidoExibirItinerario() {
        return appSingleton.getSentidoExibirItinerario();
    }

    @Override
    public String getConsorcioExibirItinerario() {
        return appSingleton.getConsorcioExibirItinerario();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void setListaItinerarios(final List<HashMap<String, String>> aList, String tempo) {

        bar.setVisibility(View.GONE);

        FrameLayout tempoViagem = (FrameLayout) findViewById(R.id.tempodeviagem2);

        if(aList.size() == 0){
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        else{
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        String[] from = {"referencia"};
        int[] to = {R.id.referencia};

        TextView tempoviagem = (TextView) findViewById(R.id.duracao);
        String viagem = Util.setString(String.valueOf(tempo), "#009FD6");
        String txtviagem = Util.setString(getResources().getString(R.string.de_viagem), "#95a4a6");
        tempoviagem.setText(Html.fromHtml(viagem + " " + txtviagem));


        if(tempo.contains("N/D"))
            tempoViagem.setVisibility(View.GONE);
        else
            tempoViagem.setVisibility(View.VISIBLE);

        adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_itinerarios_layout, from, to) {
            public View getView(final int position, View convertView, ViewGroup parent) {
                final View view = super.getView(position, convertView, parent);

                LinearLayout l = (LinearLayout) view.findViewById(R.id.itemListaItinerario);

                if(!viewsList.contains(view)){
                    viewsList.add(view);
                }

                l.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(position != positionListView){
                            for(View v1 : viewsList){
                                unmarkLine(v1);
                            }

                            markLine(view);

                            selectMarkerMap(aList.get(position).get("ponto_id"));
                            unselectMarkerMap(aList.get(positionListView).get("ponto_id"));

                            positionListView = position;
                        }
                    }
                });

                if (position == positionListView)
                    markLine(view);
                else
                    unmarkLine(view);

                if(position == 0)
                    view.findViewById(R.id.flagitinerario).setBackgroundResource(R.drawable.itinerariobackgroundinicio);
                else if(position == aList.size()-1)
                    view.findViewById(R.id.flagitinerario).setBackgroundResource(R.drawable.itinerariobackgroundfinal);
                else
                    view.findViewById(R.id.flagitinerario).setBackgroundResource(R.drawable.itinerariobackground);

            return view;
            }
        };

        listView.setAdapter(adapter);
    }

    public void markLine(View view){
        view.findViewById(R.id.imageCircle).setBackgroundResource(R.drawable.circle3);
        view.findViewById(R.id.imageCircle).getLayoutParams().height = 42;
        view.findViewById(R.id.imageCircle).getLayoutParams().width = 42;
        view.findViewById(R.id.imageCircle).setClickable(false);
        ((TextView) view.findViewById(R.id.referencia)).setTypeface(null, Typeface.BOLD);
    }

    public void unmarkLine(View view){
        view.findViewById(R.id.imageCircle).setBackgroundResource(R.drawable.circle1);
        view.findViewById(R.id.imageCircle).getLayoutParams().height = 30;
        view.findViewById(R.id.imageCircle).getLayoutParams().width = 30;
        view.findViewById(R.id.imageCircle).setClickable(false);
        ((TextView) view.findViewById(R.id.referencia)).setTypeface(null, Typeface.NORMAL);
    }

    public void selectMarkerMap(String key){
        markersMap.get(key).setIcon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(getResources(), R.drawable.circle3, 36, 36)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(markersMap.get(key).getPosition()));
    }
    public void unselectMarkerMap(String key){
        markersMap.get(key).setIcon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(getResources(), R.drawable.circle1, 26, 26)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        presenter.carregarItinerariosWebservice();
    }

    private void exibirLoadingListaResultado(){
        listView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        bar.setVisibility(View.VISIBLE);
    }

    public void tentarNovamente(View v){
        exibirLoadingListaResultado();
        presenter.carregarItinerariosWebservice();
    }

}


