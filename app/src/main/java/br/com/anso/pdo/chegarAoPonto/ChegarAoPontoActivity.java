package br.com.anso.pdo.chegarAoPonto;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.anso.pdo.R;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Caminhada;
import br.com.anso.pdo.util.Usuario;
import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.util.PdoLocationCallback;
import br.com.anso.pdo.vdoactivity.VDOAppCompatActivity;


public class ChegarAoPontoActivity extends VDOAppCompatActivity implements OnMapReadyCallback, IChegarAoPontoView, PdoLocationCallback {

    private IChegarAoPontoView.IChegarAoPontoPresenter presenter;
    private HashMap<String, Marker> markersMap;
    private GoogleMap googleMap;
    private Marker userMaker;
    private ListView listView;
    private AppSingleton appPresenter = AppSingleton.getApp();
    private  Usuario usuario;
    private ArrayList<View> viewsList;
    private int positionListView = 0;
    private LinearLayout bar;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_como_chegar_ao_ponto);
        usuario = Usuario.getInstance();
        listView = (ListView) findViewById(R.id.list);
        viewsList = new ArrayList<>();

        bar = (LinearLayout) findViewById(R.id.barLinear);
        if(bar!=null) bar.setVisibility(View.VISIBLE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        TextView tv = (TextView)findViewById(R.id.enderecopontoComoChegar);
        if(tv!=null) tv.setText(appPresenter.getEnderecoPonto());

        presenter = new ChegarAoPontoPresenter(this);
    }

    public void voltarPrincipal(View view){
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        tratarPosicaoGPS(getOrigem());

    }

    @Override
    public void tratarPosicaoGPS(LatLng pos) {

        if(this.userMaker != null) this.userMaker.remove();

        usuario.setPosicao(pos);
        usuario.setMarker(getResources());

        userMaker = googleMap.addMarker(usuario.getMarker().draggable(false));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
        presenter.carregarComoChegarAoPontoWebservice();
    }

    public ArrayAdapter carregaCaminhadaAdapter(final int listViewLayout, final List<Caminhada> lista){

        return new ArrayAdapter(this, listViewLayout, lista) {
            public View getView(final int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(listViewLayout, null);

                TextView tt = (TextView) convertView.findViewById(R.id.referencia);
                TextView tt2 = (TextView) convertView.findViewById(R.id.caminhada);
                ImageView inst = (ImageView) convertView.findViewById(R.id.imageInstrucao);


                LinearLayout l = (LinearLayout) convertView.findViewById(R.id.itemListaItinerario);

                if(!viewsList.contains(convertView)){
                    viewsList.add(convertView);
                }

                //começa com o primeiro ponto da lista e do mapa já selecionado
                if(position==positionListView)
                    updateMarkLine(null, convertView);

                String instrucao = "";
                tt2.setText(getResources().getString(R.string.caminhada_de)+" "+(long)lista.get(position).getDistancia()+" "+getResources().getString(R.string.metros));

                if(lista.get(position).getInstrucao().toString().contains("Vire à direita")){
                    inst.setBackgroundResource(R.drawable.arrowright);
                    instrucao = getResources().getString(R.string.vire_a_direita);
                }

                if(lista.get(position).getInstrucao().toString().contains("Vire à esquerda")) {
                    inst.setBackgroundResource(R.drawable.arrowleft);
                    instrucao = getResources().getString(R.string.vire_a_esquerda);
                }

                if(lista.get(position).getInstrucao().toString().contains("Siga")) {
                    inst.setBackgroundResource(R.drawable.arrowup_popup);
                    instrucao = getResources().getString(R.string.siga);
                }

                if(position == lista.size()-1) {
                    convertView.findViewById(R.id.flagitinerario).setBackgroundResource(R.drawable.comochegarbackgroundfinal);
                    tt.setText(appPresenter.getEnderecoPonto());
                    inst.setVisibility(View.GONE);
                }
                else{
                    tt.setText(instrucao+" "+getResources().getString(R.string.em)+" "+lista.get(position).getRua());
                }



                final View finalConvertView = convertView;
                l.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(position != positionListView){
                            updateMarkLine(viewsList, finalConvertView);
                            toggleMarkerMap(lista.get(position).getPolilinha(), lista.get(positionListView).getPolilinha());
                            positionListView = position;
                        }
                    }
                });

                return convertView;
            }
        };
    }

    @Override
    public LatLng getOrigem() {
        return usuario.getPosicao();//Usuario.getInstance().getPosicao();
    }

    @Override
    public LatLng getDestino() {
        return appPresenter.getCoordPonto();
    }

    @Override
    public void setListaCaminhada(List<Caminhada> aList) {
        ArrayAdapter adapter = carregaCaminhadaAdapter(R.layout.listview_como_chegar_ao_ponto_layout, aList);
        listView.setAdapter(adapter);
        carregaCaminhadaNoMapa(aList);
    }

    private void carregaCaminhadaNoMapa(List<Caminhada> aList) {
        if (googleMap != null) {
            if (markersMap != null) {
                for (String k : markersMap.keySet()) {
                    markersMap.get(k).remove();
                }
                markersMap.clear();
            }
            else
                markersMap = new HashMap<>();

            LatLngBounds.Builder b = new LatLngBounds.Builder();
            Double tempoCaminhada = 0.0;

            for (Caminhada c:  aList){
                tempoCaminhada+=c.getDistancia();
                PolylineOptions options = new PolylineOptions().width(10).color(Color.parseColor("#2c3e50")).geodesic(true);
                List<LatLng> posicoes = Util.decodePoly(c.getPolilinha());

                for (int i = 0; i < posicoes.size(); i++) {
                    options.add(posicoes.get(i));
                    b.include(posicoes.get(i));
                }

                googleMap.addPolyline(options);

                c.setPosicao(options.getPoints().get(options.getPoints().size()-1));//Pega sempre o primeiro ponto de uma caminhada
                c.setMarkerOptionsParadas(getResources());

                Marker marker = googleMap.addMarker(c.getMarkerOptionsParada());

                markersMap.put(c.getPolilinha(), marker);
            }

            markersMap.get(aList.get(0).getPolilinha()).setIcon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(getResources(), R.drawable.circle3, 42, 42)));
            TextView tt = (TextView) findViewById(R.id.duracao);
            TextView tt2 = (TextView) findViewById(R.id.duracaoTXT);
            if (!(Util.converterSegundosParaTempoViagem(tempoCaminhada)).equals("") && tt!=null && tt2!=null) {
                tt.setText(Util.converterSegundosParaTempoViagem(tempoCaminhada));
                tt2.setText(getResources().getString(R.string.de_caminhada));
            }
            LatLngBounds bounds = b.build();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
        }
    }

    public void centerUsuarioMapa(View v){
        //usuario.recentralizarGPS(this);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(usuario.getPosicao(), 15));
    }

    public void toggleMarkerMap(String key, String key2){
        markersMap.get(key).setIcon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(getResources(), R.drawable.circle3, 42, 42)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(markersMap.get(key).getPosition()));
        markersMap.get(key2).setIcon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(getResources(), R.drawable.circle1, 30, 30)));
    }

    public void updateMarkLine(ArrayList<View> listViews, View view){
        if(listViews!=null){
            for(View v1 : listViews){
                v1.findViewById(R.id.imageCircle).setBackgroundResource(R.drawable.circle1);
                v1.findViewById(R.id.imageCircle).getLayoutParams().height = 30;
                v1.findViewById(R.id.imageCircle).getLayoutParams().width = 30;
                ((TextView) v1.findViewById(R.id.referencia)).setTypeface(null, Typeface.NORMAL);
            }
        }

        view.findViewById(R.id.imageCircle).setBackgroundResource(R.drawable.circle3);
        view.findViewById(R.id.imageCircle).getLayoutParams().height = 42;
        view.findViewById(R.id.imageCircle).getLayoutParams().width = 42;
        ((TextView) view.findViewById(R.id.referencia)).setTypeface(null, Typeface.BOLD);
    }
}
