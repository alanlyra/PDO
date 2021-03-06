package br.com.anso.pdo.rota;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import br.com.anso.pdo.R;
import br.com.anso.pdo.principal.PrincipalActivity;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Ponto;
import br.com.anso.pdo.util.PontoGeometrico;
import br.com.anso.pdo.util.Rota;
import br.com.anso.pdo.util.Usuario;
import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.util.Viagem;
import br.com.anso.pdo.pdoactivity.PDOAppCompatActivity;

public class RotaActivity extends PDOAppCompatActivity implements OnMapReadyCallback, IRotaView {

    private ListView listView;
    private LinearLayout bar;
    private GoogleMap googleMap;
    private IRotaView.IRotaPresenter presenter;
    private LatLngBounds bounds;
    private AppSingleton app = AppSingleton.getApp();

    private ArrayList<Button> caminhadaButtons = new ArrayList<>();
    private ArrayList<Button> onibusButtons = new ArrayList<>();
    private ArrayList<Button> transicaoImageViews = new ArrayList<>();
    private TextView tempoViagem;
    private LinearLayout layoutLinhaSelecionada;

    private ArrayList<OnibusRotaObject> onibusRota;
    private ArrayList<CaminhadaRotaObject> caminhadaRota;

    private int positionListView = 0;
    private ArrayList<View> viewsList;
    private ArrayAdapter adapter;
    private ArrayList<String> aList;

    private SlidingUpPanelLayout layout;
    private Button detalhesRota;

    private ImageView click4;
    private Button click13;
    private ImageView arrow1;
    private ImageView arrow2;
    private ImageView arrow3;
    private ImageView arrow4;

    private HashMap<Integer, RecursoMapaObject> associacaoListaObjeto;

    private Usuario usuario;

    private ImageView home;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rota);
        usuario = Usuario.getInstance();
        listView = (ListView) findViewById(R.id.list);

        layoutLinhaSelecionada = (LinearLayout) findViewById(R.id.layoutViagemSelecionada);
        layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layoutRota);
        detalhesRota = (Button) findViewById(R.id.botaoDeRotaDetalhes);
        home = (ImageView) findViewById(R.id.home);

        bar = (LinearLayout) this.findViewById(R.id.barLinear);
        if(bar!=null) bar.setVisibility(View.VISIBLE);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        tempoViagem = (TextView) findViewById(R.id.tempoViagem);
        click4 = (ImageView) findViewById(R.id.click4);
        click4.startAnimation(AnimationUtils.loadAnimation(RotaActivity.this, R.anim.flicker));
        click13 = (Button) findViewById(R.id.click13);
        click13.startAnimation(AnimationUtils.loadAnimation(RotaActivity.this, R.anim.flicker));
        arrow1 = (ImageView) findViewById(R.id.arrow1);
        arrow1.startAnimation(AnimationUtils.loadAnimation(RotaActivity.this, R.anim.flicker));
        arrow2 = (ImageView) findViewById(R.id.arrow2);
        arrow2.startAnimation(AnimationUtils.loadAnimation(RotaActivity.this, R.anim.flicker));
        arrow3 = (ImageView) findViewById(R.id.arrow3);
        arrow3.startAnimation(AnimationUtils.loadAnimation(RotaActivity.this, R.anim.flicker));
        arrow4 = (ImageView) findViewById(R.id.arrow4);
        arrow4.startAnimation(AnimationUtils.loadAnimation(RotaActivity.this, R.anim.flicker));

        onibusRota = new ArrayList<>();
        caminhadaRota = new ArrayList<>();
        associacaoListaObjeto = new HashMap<>();

        viewsList = new ArrayList<>();

        inicializarListaViewsButtons();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(RotaActivity.this, PrincipalActivity.class);
                startActivity(i);
            }
        });

        layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener(){
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(layout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    detalhesRota.setText(R.string.detalhes_Itinerario_Abrir);
                    arrow1.setRotation(90);
                    arrow2.setRotation(90);
                    arrow3.setRotation(90);
                    arrow4.setRotation(90);
                }
                if(layout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
                    detalhesRota.setText(R.string.detalhes_Itinerario_Fechar);
                    arrow1.setRotation(-90);
                    arrow2.setRotation(-90);
                    arrow3.setRotation(-90);
                    arrow4.setRotation(-90);
                }
                if(detalhesRota.getText().toString().contains("Fechar")){
                    ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
                    params.height = layout.getHeight()-listView.getHeight();
                    mapFragment.getView().setLayoutParams(params);
                }
            }
        } );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;
                i = new Intent(RotaActivity.this, PrincipalActivity.class);
                startActivity(i);
                finish();
            }
        }, app.getUserTime());

        mapFragment.getMapAsync(this);
        presenter = new RotaPresenter(this);
    }

    private void inicializarListaViewsButtons(){
        for(int i = 0; i < 3; i++){
            int resId = getResources().getIdentifier("onibus_"+i, "id", getBaseContext().getPackageName());
            onibusButtons.add((Button) this.findViewById(resId));
        }
        for(int i = 0; i < 4; i++){
            int resId = getResources().getIdentifier("caminhada_"+i, "id", getBaseContext().getPackageName());
            caminhadaButtons.add((Button) this.findViewById(resId));
        }
        for(int i = 0; i < 6; i++){
            int resId = getResources().getIdentifier("transicao_"+i, "id", getBaseContext().getPackageName());
            transicaoImageViews.add((Button) this.findViewById(resId));
        }
    }

    public void centerUsuarioMapa(View v){
        usuario.recentralizarGPS(this);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
    }

    public void voltar(View view){
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;

        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        try {
            presenter.carregarRotas();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void exibirRotaNoMapa(Rota rota){
        if (googleMap == null) return;

        int transicao_i = 0;
        int onibus_i = 0;
        int caminhada_i = 0;

        aList = new ArrayList<>();
        LatLngBounds.Builder geralBounds = new LatLngBounds.Builder();

        PontoGeometrico origem = rota.getPontoOrigem();

        boolean ultimaViagemIsCaminhada = false;

        for(Viagem v : rota.getViagens()){

            ArrayList<Polyline> polylines = new ArrayList<>();
            LatLngBounds.Builder itemBounds = new LatLngBounds.Builder();


            if(v.getTipoTransporte().equals("Caminhada")) { // VIAGEM É UMA CAMINHADA

                if(v.getDistancia()>200){  //SOMENTE INSERE CAMINHADAS ACIMA DE 200m

                    ultimaViagemIsCaminhada = true;

                    if(v.getPolylines().size() > 0) {
                        for(ArrayList<LatLng> l : v.getDecodedPolyline()) {
                            for(LatLng place : l) {
                                itemBounds.include(place);
                                geralBounds.include(place);
                            }

                            // Adicionando Polyline ao Mapa
                            Polyline p = googleMap.addPolyline(new PolylineOptions().addAll(l).width(10).color(Color.parseColor(CaminhadaRotaObject.COR_LINE_CAMINHADA)).geodesic(true));

                            polylines.add(p);
                        }
                    }

                    int listPosition = aList.size();
                    aList.add(getResources().getString(R.string.caminhe) + " "+(int) v.getDistancia() + " "+getResources().getString(R.string.metros_ate) + " "+construirTextoEndereco(v.getPontoDestino().getLogradouro(),
                                                                                                                          v.getPontoDestino().getNumero(),
                                                                                                                          v.getPontoDestino().getReferencia()));

                    Marker origemDestinoMarkers[] = new Marker[2];

                    origemDestinoMarkers[0] = googleMap.addMarker(new MarkerOptions()
                                                                    .position(origem.getCoordenadas())
                                                                    .icon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(getResources(), R.drawable.circle1, 26, 26)))
                                                                    .title(construirTextoEndereco(origem.getLogradouro(), origem.getNumero(), origem.getReferencia()))
                                                                    .anchor((float) 0.5, (float) 0.5));

                    origemDestinoMarkers[1] = googleMap.addMarker(new MarkerOptions()
                                                                    .position(v.getPontoDestino().getCoordenadas())
                                                                    .icon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(getResources(), R.drawable.circle1, 26, 26)))
                                                                    .title(construirTextoEndereco(v.getPontoDestino().getLogradouro(), v.getPontoDestino().getNumero(), v.getPontoDestino().getReferencia()))
                                                                    .anchor((float) 0.5, (float) 0.5));

                    Button button = caminhadaButtons.get(caminhada_i);
                    CaminhadaRotaObject c = new CaminhadaRotaObject(v, polylines, origemDestinoMarkers, listPosition, itemBounds.build(), button, this, tempoViagem, googleMap, layoutLinhaSelecionada, listView);

                    associacaoListaObjeto.put(listPosition, c);

                    caminhadaRota.add(c);
                    caminhada_i++;

                    if(transicao_i > 5)
                        transicaoImageViews.get(transicao_i-1).setVisibility(View.VISIBLE);
                    else
                        transicaoImageViews.get(transicao_i).setVisibility(View.VISIBLE);
                    transicao_i++;
                    origem = v.getPontoDestino();
                }
            }

            else{   // VIAGEM É ÔNIBUS/TREM/METRÔ/ETC.
                ArrayList<Marker> paradasMarkers = new ArrayList<>();

                if(v.getPolylines().size() > 0){
                    for(ArrayList<LatLng> l : v.getDecodedPolyline()){
                        for(LatLng place : l){
                            itemBounds.include(place);
                            geralBounds.include(place);
                        }

                        // Adicionando Polyline ao Mapa
                        Polyline p = googleMap.addPolyline(new PolylineOptions().addAll(l).width(10).color(Color.parseColor(OnibusRotaObject.COR_LINE_ONIBUS)).geodesic(true));

                        polylines.add(p);
                    }
                }

                int listPosition = aList.size();

                for(int i = 0; i < v.getParadas().size(); i++){
                    Ponto p = v.getParadas().get(i);
                    String txt;

                    // Adicionando ponto de parada no mapa
                    p.setMarkerOptionsParadas(getResources());
                    Marker m = googleMap.addMarker(p.getMarkerOptionsParada());

                    paradasMarkers.add(m);

                    if(i == 0){
                        txt = getResources().getString(R.string.embarque_em) +" "+ v.getLinha().getVista() + " "+ getResources().getString(R.string.no_ponto) + " "+p.getEndereco();
                    }
                    else if(i == v.getParadas().size()-1){
                        txt = getResources().getString(R.string.desembarque_em) + " "+ p.getEndereco();
                    }
                    else {
                        txt = p.getEndereco();
                    }
                    aList.add(txt);
                }

                int finalListPosition = aList.size();

                Button button = onibusButtons.get(onibus_i);
                OnibusRotaObject o = new OnibusRotaObject(v, polylines, paradasMarkers, listPosition, itemBounds.build(), button, this, tempoViagem, googleMap, layoutLinhaSelecionada, listView);

                for(int i = listPosition; i < finalListPosition; i++)
                    associacaoListaObjeto.put(i, o);

                onibusRota.add(o);
                onibus_i++;
                if(!ultimaViagemIsCaminhada){
                    caminhada_i++;
                    transicao_i++;
                }
                ultimaViagemIsCaminhada = false;

                if(transicao_i > 5)
                    transicaoImageViews.get(transicao_i-1).setVisibility(View.VISIBLE);
                else
                    transicaoImageViews.get(transicao_i).setVisibility(View.VISIBLE);
                transicao_i++;
                origem = v.getPontoDestino();
            }

        }

        if(transicao_i > 0 && transicao_i <=6)
            transicaoImageViews.get(transicao_i-1).setVisibility(View.GONE);

        bounds = geralBounds.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.3);

        CameraUpdate cup = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        construirListView();

        googleMap.animateCamera(cup);

        selecionarPrimeiraViagem(rota);

    }

    public void MostrarDetalhesRota (View v){
        if(layout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
            layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            detalhesRota.setText(R.string.detalhes_Itinerario_Abrir);
        }
        if(layout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
            layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            detalhesRota.setText(R.string.detalhes_Itinerario_Fechar);
        }

    }
    private void selecionarPrimeiraViagem(Rota rota) {

        onibusButtons.get(0).performClick(); //Inicia com o Onibus (Otimizar mais tarde)
    }

    private String construirTextoEndereco(String logradouro, String numero, String referencia) {
        String destino = logradouro != null && logradouro.length() > 0 ?
                (logradouro + (numero != null && numero.length() > 0 ? ", " + numero  : "")) :
                "";

        destino += referencia !=null && referencia.length() > 0 ? referencia : "";
        return destino;
    }

    private void construirListView() {
        bar.setVisibility(View.GONE);

        adapter =  new ArrayAdapter(getBaseContext(), R.layout.listview_itinerarios_layout, R.id.referencia, aList) {
            public View getView(final int position, View convertView, ViewGroup parent) {
                final View view = super.getView(position, convertView, parent);

                LinearLayout l = (LinearLayout) view.findViewById(R.id.itemListaItinerario);

                if(!viewsList.contains(view)){
                    viewsList.add(view);
                }

                l.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selecionarItemNaLista(position);
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

    public void selecionarItemNaLista(int pos){
        if(pos != positionListView){
            for(View v1 : viewsList){
                unmarkLine(v1);
            }


            View view = Util.getViewByPosition(pos, listView);

            int posicaoLista = aList.indexOf(adapter.getItem(pos).toString());
            associacaoListaObjeto.get(posicaoLista).tratarCliqueNoBotaoTransbordo(false);

            markLine(view);

            selectMarkerMap(pos);
            unselectMarkerMap(positionListView);

            positionListView = pos;
        }
    }

    private void markLine(View view){
        view.findViewById(R.id.imageCircle).setBackgroundResource(R.drawable.circle3);
        view.findViewById(R.id.imageCircle).getLayoutParams().height = 42;
        view.findViewById(R.id.imageCircle).getLayoutParams().width = 42;
        ((TextView) view.findViewById(R.id.referencia)).setTypeface(null, Typeface.BOLD);
    }

    private void unmarkLine(View view){
        view.findViewById(R.id.imageCircle).setBackgroundResource(R.drawable.circle1);
        view.findViewById(R.id.imageCircle).getLayoutParams().height = 30;
        view.findViewById(R.id.imageCircle).getLayoutParams().width = 30;
        ((TextView) view.findViewById(R.id.referencia)).setTypeface(null, Typeface.NORMAL);
    }

    private void selectMarkerMap(int pos){
        RecursoMapaObject recurso = associacaoListaObjeto.get(pos);
        recurso.destacarMarcadorMapa(pos);
    }
    private void unselectMarkerMap(int pos){
        RecursoMapaObject recurso = associacaoListaObjeto.get(pos);
        recurso.resetarMarcadorMapa(pos);
    }

    public void resetButtonsPolylines(){
        for(OnibusRotaObject o : onibusRota){
            o.resetStyles();
        }
        for(CaminhadaRotaObject c : caminhadaRota){
            c.resetStyles();
        }
    }
}