package br.com.anso.pdo.principal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.anso.pdo.R;
import br.com.anso.pdo.buscaLinhaRota.BuscaLinhaRotaActivity;
import br.com.anso.pdo.buscaPontosEstacoes.BuscaPontosEstacoesActivity;
import br.com.anso.pdo.chegarAoPonto.ChegarAoPontoActivity;
import br.com.anso.pdo.favoritos.Favorito;
import br.com.anso.pdo.linhasdoponto.LinhasdopontoActivity;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.DoubleClick;
import br.com.anso.pdo.util.InfoMarkerPersonalizado;
import br.com.anso.pdo.util.Ponto;
import br.com.anso.pdo.util.Usuario;
import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.util.PdoLocationCallback;

public class ExibePontosProximosFragment extends Fragment implements OnMapReadyCallback, IPrincipalTab2View, PdoLocationCallback {

    private IPrincipalTab2View.IPrincipalTab2Presenter presenter;
    private ListView listView;

    private LinearLayout emptyView;
    private LinearLayout bar;
    private Favorito favorito;
    private Marker userMaker;
    private HashMap<String, Marker> stopMarkers;
    private HashMap<String, Integer> posicaoPontoNaLista;
    private FrameLayout frameoptions;
    private Timer timer = null;  //at class level;
    private int DELAY   = 500;
    private FrameLayout buscapontos;
    private SlidingUpPanelLayout layout;
    private GoogleMap googleMap;
    private boolean collapseLayout = false;
    private float historicX = Float.NaN;
    private float historicY = Float.NaN;
    private static final int DELTA = 50;
    private enum Direction {LEFT, RIGHT;}

    private AppSingleton appSingleton = AppSingleton.getApp();
    private Usuario usuario = Usuario.getInstance();

    public ExibePontosProximosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        appSingleton.setViewPontos(this);

        View view = inflater.inflate(R.layout.fragment_principal_tab2, container, false);

        listView = (ListView) view.findViewById(R.id.list2);
        emptyView = (LinearLayout) view.findViewById(R.id.nenhumPontoEncontrado);
        buscapontos = (FrameLayout) view.findViewById(R.id.buscapontoseestacoes);
        layout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layouttab2);

        bar = (LinearLayout) view.findViewById(R.id.barLinear);

        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map2));
        mapFragment.getMapAsync(this);

        exibirLoadingListaResultado();
        presenter = new ExibePontosProximosPresenter(this);

        buscapontos.setOnClickListener(new DoubleClick() {

            @Override
            public void onSingleClick(View v) {

                final Handler handler          = new Handler();
                final Runnable mRunnable        = new Runnable() {
                    public void run() {
                        Intent i;
                        i = new Intent(ExibePontosProximosFragment.this.getActivity(), BuscaPontosEstacoesActivity.class);
                        startActivity(i);
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

            @Override
            public void onDoubleClick(View v) {
                if(timer!=null)
                {
                    timer.cancel();
                    timer.purge();
                }
                if(!collapseLayout) {
                    layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    collapseLayout = true;
                }
                else{
                    layout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                    collapseLayout = false;
                }
            }
        });

        buscapontos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                // TODO Auto-generated method stub
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        historicX = event.getX();
                        historicY = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        if (event.getX() - historicX < -DELTA)
                        {
                            Log.d("SWIPE: ", "LEFT");

                            return true;
                        }
                        else if (event.getX() - historicX > DELTA)
                        {
                            Log.d("SWIPE: ", "RIGHT");

                            ViewPager viewPager = (ViewPager)getActivity().findViewById(R.id.viewpager);
                            viewPager.setCurrentItem(0);
                            return true;
                        } break;
                    default: return false;
                }
                return false;
            }
        });


        return view;
    }

    @Override
    public void setListaPontosProximos(final List<HashMap<String, String>> aList) {
        String[] from = {"txt", "distancia", "options"};
        int[] to = {R.id.txt, R.id.distancia1km, R.id.options};

        bar.setVisibility(View.GONE);

        if (aList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        inicializarMapPosicaoPontoNaListView(aList);

        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.listview_pontos_layout, from, to) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                String lat = aList.get(position).get("latitude");
                String lon = aList.get(position).get("longitude");
                String tempo =  aList.get(position).get("distancia");

                ImageView popUp_btn = (ImageView) view.findViewById(R.id.options);
                frameoptions = (FrameLayout) view.findViewById(R.id.frameoptionsPontos);
                TextView tempoviagem = (TextView) view.findViewById(R.id.tempochegada);

                String txtviagem = Util.setString(getResources().getString(R.string.a_pe), "#95a4a6");
                String viagem = Util.setString(Util.converterSegundosParaTempoViagem(Double.parseDouble(tempo)), "#009FD6");
                tempoviagem.setText(Html.fromHtml(txtviagem + " " + viagem));

                if (position % 2 == 1) {
                    view.setBackgroundResource(R.color.listview2);
                } else if (position % 2 == 0) {
                    view.setBackgroundResource(R.color.listview1);
                }

                LatLng pos = new LatLng(Double.parseDouble(aList.get(position).get("latitude")), Double.parseDouble(aList.get(position).get("longitude")));

                String enderecoWS = "POINT(" + lon + " " + lat + ")";
                String ponto = aList.get(position).get("id");
                TextView enderecoTV = (TextView) view.findViewById(R.id.txt);
                String endereco = (String) enderecoTV.getText();

                attachPopupHandler(new Ponto(pos, endereco, ponto),enderecoWS,popUp_btn);
                abreLinhasDoPonto(aList);

                return view;
            }
        };

        listView.setAdapter(adapter);
    }

    private void inicializarMapPosicaoPontoNaListView(List<HashMap<String, String>> aList) {
        posicaoPontoNaLista = new HashMap<String, Integer>();
        for(int i = 0; i < aList.size(); i++){
            HashMap<String, String> map = aList.get(i);

            posicaoPontoNaLista.put(map.get("id"), i);
        }
    }

    private void abreLinhasDoPonto(final List<HashMap<String, String>> aList) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String idPonto = aList.get(position).get("id");
                Marker m = stopMarkers.get(idPonto);
                m.showInfoWindow();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 17));
            }
        });
    }

    private void attachPopupHandler(final Ponto ponto, final String enderecoWS, final ImageView popUp_btn) {
        frameoptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity().getBaseContext(), popUp_btn);

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
                popup.getMenuInflater().inflate(R.menu.popup_filters_pontos, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            /*case R.id.favoritarponto:
                                favorito = new Favorito();
                                favorito.atualizaFavoritos(getContext(), ponto);

                                return true;*/
                            case R.id.linhasponto:
                                appSingleton.setEnderecoPonto(ponto.getEndereco());
                                appSingleton.setIdPonto(ponto.getIdPonto());
                                appSingleton.setCoordPonto(ponto.getPosicao());

                                Intent intent = new Intent(getActivity(), LinhasdopontoActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.comochegar:
                                appSingleton.setEnderecoPonto(ponto.getEndereco());
                                appSingleton.setIdPonto(ponto.getIdPonto());
                                appSingleton.setCoordPonto(ponto.getPosicao());

                                intent = new Intent(getActivity(), ChegarAoPontoActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.localpartida:
                                appSingleton.setEnderecoOrigem(appSingleton.getEnderecoOrigemExibicao(), enderecoWS);
                                appSingleton.setEnderecoOrigem(ponto.getEndereco(), enderecoWS);
                                appSingleton.setAbaDefault(2);
                                intent = new Intent(getActivity(), BuscaLinhaRotaActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.localdestino:
                                appSingleton.setEnderecoDestino(appSingleton.getEnderecoDestinoExibicao(), enderecoWS);
                                appSingleton.setEnderecoDestino(ponto.getEndereco(), enderecoWS);
                                appSingleton.setAbaDefault(2);
                                intent = new Intent(getActivity(), BuscaLinhaRotaActivity.class);
                                startActivity(intent);
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

    @Override
    public void carregaPontosNoMapa(JSONArray listaDePontos) {
        if (googleMap != null) {
            if (stopMarkers != null) {
                for (String key : stopMarkers.keySet()) {
                    stopMarkers.get(key).remove();
                }
            }
            stopMarkers = new HashMap<String, Marker>();
            final HashMap<Marker, Ponto> hashMarker = new HashMap<>();

            for (int i = 0; i < listaDePontos.length(); i++) {
                Ponto p = new Ponto();
                try {
                    JSONObject ponto = listaDePontos.getJSONObject(i);

                    p.setPosicao(new LatLng(ponto.getDouble("latitude"), ponto.getDouble("longitude")));
                    p.setEndereco(ponto.optString("referencia"));
                    p.setMarkerOptions(getResources());
                    p.setIdPonto(ponto.optString("id"));

                    Marker b = googleMap.addMarker(p.getMarker());
                    hashMarker.put(b, p);
                    stopMarkers.put(p.getIdPonto(), b);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            customizarMarcadores(hashMarker);
        }
    }

    private void customizarMarcadores(final HashMap<Marker, Ponto> hashMarker) {
        // Info window com view personalizada
        googleMap.setInfoWindowAdapter(new InfoMarkerPersonalizado(getContext(), hashMarker));

        // Clique no marcador
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                Ponto p = hashMarker.get(marker);
                if(p != null){
                    String idPonto = p.getIdPonto();
                    int pos = posicaoPontoNaLista.get(idPonto);
                    Util.animarNovaPosicaoListView(pos, listView);
                }

                return true;
            }
        });

        // Clique no info window
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker m) {
                appSingleton.setEnderecoPonto(hashMarker.get(m).getEndereco());
                appSingleton.setIdPonto(hashMarker.get(m).getIdPonto());
                appSingleton.setCoordPonto(hashMarker.get(m).getPosicao());

                Intent intent = new Intent(getContext(), LinhasdopontoActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        posicionarUsuarioMapaGPS(usuario.getPosicao());

        this.googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                appSingleton.atualizaUsuarioViews(marker.getPosition());
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

        });
    }

    public void posicionarUsuarioMapaGPS(LatLng pos) {
        //Log.d("GPS POSITION: ", String.valueOf(pos));
        if (userMaker != null) userMaker.remove();

        usuario.setPosicao(pos);
        usuario.setMarker(getResources());

        userMaker = googleMap.addMarker(usuario.getMarker().draggable(true));//marcador do usuário

        presenter.carregarPontosProximos(pos, this.getContext());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
    }

    public void centerUsuarioMapa(View v){
        usuario.recentralizarGPS(getActivity());
    }

    @Override
    public void tratarPosicaoGPS(LatLng pos) {
        posicionarUsuarioMapaGPS(pos);
    }

    private void exibirLoadingListaResultado(){
        listView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        bar.setVisibility(View.VISIBLE);
    }

    public void tentarNovamenteCarregarPontos(View v){
        exibirLoadingListaResultado();
        presenter.carregarPontosProximos(usuario.getPosicao(), this.getContext());
    }
}
