package br.com.anso.pdo.principal;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import br.com.anso.pdo.R;
import br.com.anso.pdo.buscaLinhaRota.BuscaLinhaRotaActivity;
import br.com.anso.pdo.favoritos.Favorito;
import br.com.anso.pdo.itinerario.ItinerarioActivity;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Linha;
import br.com.anso.pdo.util.Usuario;
import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.util.PdoLocationCallback;

public class ExibeOnibusProximosFragment extends Fragment implements OnMapReadyCallback, IPrincipalTab1View, PdoLocationCallback {

    private IPrincipalTab1View.IPrincipalTab1Presenter presenter;
    private ListView listView;

    private LinearLayout emptyView;
    private LinearLayout bar;
    private Favorito favorito;
    private FrameLayout frameoptions;
    private Marker userMaker;
    private ArrayList<Marker> busMarkers;
    private Timer timer = null;  //at class level;
    private int DELAY   = 500;
    private FrameLayout buscalinhas;
    private SlidingUpPanelLayout layout;
    private GoogleMap googleMap;
    private boolean collapseLayout = false;
    private float historicX = Float.NaN;
    private float historicY = Float.NaN;
    private static final int DELTA = 50;
    private enum Direction {LEFT, RIGHT;}
    private Button linhasdoponto;
    private Button rotasdaqui;
    private Button outraslinhas;
    //private Button outrasrotas;
    private ImageView backfromlist;
    private String endereco = "";
    private boolean partida = true;
    private boolean atual = false;
    private String enderecoAtual;
    private ImageView icon1;
    private ImageView icon2;
    private ImageView icon3;

    private AppSingleton appSingleton = AppSingleton.getApp();
    private Usuario usuario;

    public ExibeOnibusProximosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_principal_tab1, container, false);

        appSingleton.setViewLinhas(this);
        usuario = Usuario.getInstance();

        listView = (ListView) view.findViewById(R.id.list);
        emptyView = (LinearLayout) view.findViewById(R.id.nenhumaLinhaEncontrada);
        //buscalinhas = (FrameLayout) view.findViewById(R.id.buscarlinhaserotas);
        linhasdoponto = (Button) view.findViewById(R.id.linhas_desse_ponto);
        rotasdaqui = (Button) view.findViewById(R.id.rotasdaqui);
        outraslinhas = (Button) view.findViewById(R.id.outraslinhas);
        //outrasrotas = (Button) view.findViewById(R.id.outrasrotas);
        backfromlist = (ImageView) view.findViewById(R.id.backfromlist);
        layout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layouttab1);
        icon1 = (ImageView) view.findViewById(R.id.icon1);
        icon2 = (ImageView) view.findViewById(R.id.icon2);
        icon3 = (ImageView) view.findViewById(R.id.icon3);

        icon1.bringToFront();
        icon2.bringToFront();
        icon3.bringToFront();

        enderecoAtual = getResources().getString(R.string.localizacao_atual);

        bar = (LinearLayout) view.findViewById(R.id.barLinear);

        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        exibirLoadingListaResultado();
        presenter = new ExibeOnibusProximosPresenter(this);

        linhasdoponto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        backfromlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        rotasdaqui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //localDestinoLinhasRotas = (TextView) view.findViewById(R.id.localDestinoLinhasRotas);
                Util.selecionarLocal(getContext(), getResources().getString(R.string.definir_local_destino), new Util.ISetTextCallBack() {
                    @Override
                    public void setText(String value) {
                        String municipio = "";
                        if(Util.nonBlank(value))
                            municipio = " - " + appSingleton.getMunicipios().get(appSingleton.getIndexMunicipioDestino());

                        appSingleton.setAbaDefault(1);
                        appSingleton.setlocalDestinoRota(value.concat(municipio));
                        //localDestinoLinhasRotas.setText( value.concat(municipio));
                        appSingleton.setEnderecoDestino( value, appSingleton.getEnderecoDestinoWS() );
                        appSingleton.setEnderecoDestino(appSingleton.getEnderecoDestinoExibicao(), value);

                        atual = true;
                        String pontoWS = "POINT(";
                        double lat = getPosicao().latitude;
                        double lon = getPosicao().longitude;
                        pontoWS=pontoWS+lon+" "+lat+")";
                        enderecoSelecionado(pontoWS);
                        if(partida) {
                            appSingleton.setEnderecoOrigem(enderecoAtual, pontoWS);
                        }
                        else{
                            appSingleton.setEnderecoDestino(enderecoAtual, pontoWS);
                        }

                    }
                });

            }
        });

        outraslinhas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appSingleton.setAbaDefault(0);
                Intent i;
                i = new Intent(ExibeOnibusProximosFragment.this.getActivity(), BuscaLinhaRotaActivity.class);
                startActivity(i);
            }
        });

      /*  outrasrotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appSingleton.setAbaDefault(1);
                Intent i;
                i = new Intent(ExibeOnibusProximosFragment.this.getActivity(), BuscaLinhaRotaActivity.class);
                startActivity(i);
            }
        });*/
        /*buscalinhas.setOnClickListener(new DoubleClick() {

            @Override
            public void onSingleClick(View v) {

                final Handler handler          = new Handler();
                final Runnable mRunnable        = new Runnable() {
                    public void run() {
                        Intent i;
                        i = new Intent(ExibeOnibusProximosFragment.this.getActivity(), BuscaLinhaRotaActivity.class);
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

        buscalinhas.setOnTouchListener(new View.OnTouchListener() {
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

                            ViewPager viewPager = (ViewPager)getActivity().findViewById(R.id.viewpager);
                            viewPager.setCurrentItem(1);
                            return true;
                        }
                        else if (event.getX() - historicX > DELTA)
                        {
                            Log.d("SWIPE: ", "RIGHT");

                            return true;
                        } break;
                    default: return false;
                }
                return false;
            }
        });*/
        return view;
    }


    @Override
    public void setListaLinhas(final List<HashMap<String, String>> aList) {
        String[] from = {"txt", "cur", "options"};
        int[] to = {R.id.txt, R.id.cur, R.id.options};



        bar.setVisibility(View.GONE);

        if (aList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.listview_layout, from, to) {
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
                frameoptions = (FrameLayout) view.findViewById(R.id.frameoptions);
                FrameLayout tempoViagem = (FrameLayout) view.findViewById(R.id.tempodeviagem);
                TextView tempoviagem = (TextView) view.findViewById(R.id.tempochegada);

                String txtviagem = Util.setString(getResources().getString(R.string.tempo_chegada), "#95a4a6");
                String viagem = Util.setString(String.valueOf(tempo), "#009FD6");
                tempoviagem.setText(Html.fromHtml(txtviagem + " " + viagem));

                if(tempo.contains("N/D"))
                    tempoViagem.setVisibility(View.GONE);
                else
                    tempoViagem.setVisibility(View.VISIBLE);

                if (position % 2 == 1) {
                    view.setBackgroundResource(R.color.color_primary2);
                } else if (position % 2 == 0) {
                    view.setBackgroundResource(R.color.color_primary4);
                }
                corconsorcioLayout.setBackgroundColor(Color.parseColor(aList.get(position).get("corconsorcio")));
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

                appSingleton.setRouteNameExibirIitnerario(linha.getRouteName());
                appSingleton.setServicoExibirIitnerario(linha.getServico());
                appSingleton.setCorConsorcio(linha.getCorConsorcio());
                Intent i;
                i = new Intent(getActivity(), ItinerarioActivity.class);
                startActivity(i);


                /*PopupMenu popup = new PopupMenu(getActivity().getBaseContext(), popUp_btn);

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
                           *//* case R.id.favoritar:
                                favorito = new Favorito();
                                favorito.atualizaFavoritos(getContext(), linha);

                                return true;*//*
                            case R.id.itinerario:
                                appSingleton.setRouteNameExibirIitnerario(linha.getRouteName());
                                appSingleton.setServicoExibirIitnerario(linha.getServico());
                                appSingleton.setCorConsorcio(linha.getCorConsorcio());
                                Intent i;
                                i = new Intent(getActivity(), ItinerarioActivity.class);
                                startActivity(i);
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();*/
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
                i = new Intent(getActivity(), ItinerarioActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void exibirLinhasNoMapa(List<Linha> lista){
        if (googleMap != null) {
            if (busMarkers != null) {
                for (Marker m : busMarkers) {
                    m.remove();
                }
            }
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    if((marker.getTitle()!=null && marker.getSnippet()!=null)){
                        LinearLayout info = new LinearLayout(getContext());
                        info.setOrientation(LinearLayout.VERTICAL);

                        if(!marker.getTitle().equals("")){
                            TextView title = new TextView(getContext());
                            title.setTextColor(Color.BLACK);
                            title.setGravity(Gravity.CENTER);
                            title.setTypeface(null, Typeface.BOLD);
                            title.setText(marker.getTitle());
                            info.addView(title);
                        }

                        if(!marker.getSnippet().equals("")){
                            TextView snippet = new TextView(getContext());
                            snippet.setTextColor(Color.GRAY);
                            snippet.setSingleLine(false);
                            snippet.setText(Html.fromHtml(marker.getSnippet()));
                            info.addView(snippet);
                        }
                        return info;
                    }
                    return null;
                }
            });

            busMarkers = new ArrayList<>();

            for (Linha l: lista){
                l.setMarkerOptions(getResources(), this.getContext());
                Marker b = googleMap.addMarker(l.getMarker());//marcador das onibus proximos
                busMarkers.add(b);
                //b.showInfoWindow();
            }

            appSingleton.setNumeroOnibusProximos(lista.size());
            PrincipalActivity.numeroLinhasProximas.setText(String.valueOf(appSingleton.getNumeroOnibusProximos()));

        }
       /* for(int i =0;i<busMarkers.size();i++)
            busMarkers.get(i).showInfoWindow();*/
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

    public void posicionarUsuarioMapaGPS(LatLng posicaoUsuario) {

        if (userMaker != null) userMaker.remove();

        usuario.setPosicao(posicaoUsuario);
        usuario.setMarker(getResources());

        userMaker = googleMap.addMarker(usuario.getMarker().draggable(true));//marcador do usuário


        presenter.carregarLinhasProximas(posicaoUsuario, this.getContext());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicaoUsuario, 15));
    }

    public void centerUsuarioMapa(View v){
        usuario.recentralizarGPS(this.getActivity());

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

    public void tentarNovamenteCarregarLinhas(View v){
        exibirLoadingListaResultado();
        presenter.carregarLinhasProximas(usuario.getPosicao(), this.getContext());
    }

    private void enderecoSelecionado(String selecionado) {


        endereco = selecionado;
        setEnderecos(endereco,endereco);

        Util.executaCallback("enderecoSelecionado", this);

       /* appSingleton.setAbaDefault(1);
        Intent i;
        i = new Intent(ExibeOnibusProximosFragment.this.getActivity(), BuscaLinhaRotaActivity.class);
        startActivity(i);*/
    }

    public void setEnderecos(String endereco, String enderecoWS){
        if(partida) {
            appSingleton.setEnderecoOrigem(endereco, enderecoWS);
        }
        else{
            appSingleton.setEnderecoDestino(endereco, enderecoWS);
        }
    }

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

    public void definirPosicaoAtual(){
        atual = true;
        String pontoWS = "POINT(";
        double lat = getPosicao().latitude;
        double lon = getPosicao().longitude;
        pontoWS=pontoWS+lon+" "+lat+")";
        enderecoSelecionado(pontoWS);
        if(partida) {
            appSingleton.setEnderecoOrigem(enderecoAtual, pontoWS);
        }
        else{
            appSingleton.setEnderecoDestino(enderecoAtual, pontoWS);
        }
    }

}
