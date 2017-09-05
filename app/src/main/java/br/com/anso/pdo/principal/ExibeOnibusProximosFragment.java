package br.com.anso.pdo.principal;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.text.Html;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
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
import java.util.Locale;
import java.util.Timer;

import br.com.anso.pdo.R;
import br.com.anso.pdo.buscaLinhaRota.BuscaLinhaRotaActivity;

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
    private FrameLayout frameoptions;
    private Marker userMaker;
    private ArrayList<Marker> busMarkers;
    private SlidingUpPanelLayout layout;
    private GoogleMap googleMap;
    private Button linhasdoponto;
    private Button rotasdaqui;
    private Button outraslinhas;
    private Button language;
    private ImageView backfromlist;
    private ImageView home;
    private String endereco = "";
    private boolean partida = true;
    private boolean atual = false;
    private String enderecoAtual;
    private ImageView icon1;
    private ImageView icon2;
    private ImageView icon3;
    private ImageView minORhour;
    private ImageView click1;
    private ImageView click2;
    private ImageView click3;
    private ImageView clock;
    private LinearLayout flag2;
    private ArrayList<View> viewsList;

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

        viewsList = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.list);
        emptyView = (LinearLayout) view.findViewById(R.id.nenhumaLinhaEncontrada);
        linhasdoponto = (Button) view.findViewById(R.id.linhas_desse_ponto);
        rotasdaqui = (Button) view.findViewById(R.id.rotasdaqui);
        outraslinhas = (Button) view.findViewById(R.id.outraslinhas);
        language = (Button) view.findViewById(R.id.language);
        backfromlist = (ImageView) view.findViewById(R.id.backfromlist);
        home = (ImageView) view.findViewById(R.id.home);
        layout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layouttab1);
        icon1 = (ImageView) view.findViewById(R.id.icon1);
        icon2 = (ImageView) view.findViewById(R.id.icon2);
        icon3 = (ImageView) view.findViewById(R.id.icon3);
        click1 = (ImageView) view.findViewById(R.id.click1);
        click2 = (ImageView) view.findViewById(R.id.click2);
        click3 = (ImageView) view.findViewById(R.id.click3);

        icon1.bringToFront();
        icon2.bringToFront();
        icon3.bringToFront();

        enderecoAtual = getResources().getString(R.string.localizacao_atual);

        bar = (LinearLayout) view.findViewById(R.id.barLinear);

        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        exibirLoadingListaResultado();
        presenter = new ExibeOnibusProximosPresenter(this);

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(getActivity().getBaseContext(), language);

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
                popup.getMenuInflater().inflate(R.menu.popup_language, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.portugues:
                                String languageToLoad  = "pt";
                                Locale locale = new Locale(languageToLoad);
                                Locale.setDefault(locale);
                                Configuration config = new Configuration();
                                config.locale = locale;
                                getContext().getResources().updateConfiguration(config,getContext().getResources().getDisplayMetrics());

                                Intent i;
                                i = new Intent(ExibeOnibusProximosFragment.this.getActivity(), PrincipalActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                return true;
                            case R.id.ingles:
                                String languageToLoad_En  = "en";
                                Locale locale_En = new Locale(languageToLoad_En);
                                Locale.setDefault(locale_En);
                                Configuration config_En = new Configuration();
                                config_En.locale = locale_En;
                                getContext().getResources().updateConfiguration(config_En,getContext().getResources().getDisplayMetrics());

                                Intent i_En;
                                i_En = new Intent(ExibeOnibusProximosFragment.this.getActivity(), PrincipalActivity.class);
                                i_En.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i_En);
                                return true;
                            case R.id.espanhol:
                                String languageToLoad_Es  = "es";
                                Locale locale_Es = new Locale(languageToLoad_Es);
                                Locale.setDefault(locale_Es);
                                Configuration config_Es = new Configuration();
                                config_Es.locale = locale_Es;
                                getContext().getResources().updateConfiguration(config_Es,getContext().getResources().getDisplayMetrics());

                                Intent i_Es;
                                i_Es = new Intent(ExibeOnibusProximosFragment.this.getActivity(), PrincipalActivity.class);
                                i_Es.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i_Es);
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();

            }
        });

        linhasdoponto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                }, appSingleton.getUserTime());
            }
        });

        backfromlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        rotasdaqui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.selecionarLocal(getContext(), getResources().getString(R.string.definir_local_destino), new Util.ISetTextCallBack() {
                    @Override
                    public void setText(String value) {
                        String municipio = "";
                        if(Util.nonBlank(value))
                            municipio = " - " + appSingleton.getMunicipios().get(appSingleton.getIndexMunicipioDestino());

                        appSingleton.setAbaDefault(1);
                        appSingleton.setlocalDestinoRota(value.concat(municipio));
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
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public View getView(int position, View convertView, ViewGroup parent) {



                String routeName = aList.get(position).get("route_name");
                String servico = aList.get(position).get("servico");
                String corConsorcio = aList.get(position).get("corconsorcio");
                String referencia = aList.get(position).get("cur");
                String numero =  aList.get(position).get("txt");
                String tempo =  aList.get(position).get("tempochegada");

                final View view = super.getView(position, convertView, parent);

                if(!viewsList.contains(view)){
                    viewsList.add(view);
                }


                ImageView popUp_btn = (ImageView) view.findViewById(R.id.options);

                LinearLayout corconsorcioLayout = (LinearLayout) view.findViewById(R.id.flag);
                frameoptions = (FrameLayout) view.findViewById(R.id.frameoptions);
                FrameLayout tempoViagem = (FrameLayout) view.findViewById(R.id.tempodeviagem);
                TextView tempoviagem = (TextView) view.findViewById(R.id.tempochegada);
                minORhour = (ImageView) view.findViewById(R.id.minORhour);
                clock = (ImageView) view.findViewById(R.id.clock);
                flag2 = (LinearLayout) view.findViewById(R.id.flag2);

                String viagem = Util.setString(String.valueOf(tempo), "#009FD6");
                if(tempo.contains("h")){
                    minORhour.setBackgroundResource(R.drawable.de);
                }
                tempoviagem.setText(Html.fromHtml(viagem));

                if(tempoviagem.getText().toString().contains("0") ||
                    tempoviagem.getText().toString().contains("1") ||
                    tempoviagem.getText().toString().contains("2") ||
                    tempoviagem.getText().toString().contains("3") ||
                    tempoviagem.getText().toString().contains("4") ||
                    tempoviagem.getText().toString().contains("5") ||
                    tempoviagem.getText().toString().contains("6") ||
                    tempoviagem.getText().toString().contains("7") ||
                    tempoviagem.getText().toString().contains("8") ||
                    tempoviagem.getText().toString().contains("9")){
                        clock.setVisibility(View.VISIBLE);
                        minORhour.setVisibility(View.VISIBLE);
               }
               else {
                    clock.setVisibility(View.GONE);
                    minORhour.setVisibility(View.GONE);
                }


                if(tempo.contains("N/D")) {
                    //tempoViagem.setVisibility(View.GONE);
                    tempoviagem.setText("");
                }
                else
                    tempoViagem.setVisibility(View.VISIBLE);


                if (position % 2 == 1) {
                    view.setBackgroundResource(R.color.color_primary2);
                    //flag2.setBackgroundResource(R.color.color_primary2);
                } else if (position % 2 == 0) {
                    view.setBackgroundResource(R.color.color_primary4);
                    //flag2.setBackgroundResource(R.color.color_primary4);
                }

                VectorDrawable gd = (VectorDrawable) corconsorcioLayout.getBackground().getCurrent();
                gd.setTint(Color.parseColor(aList.get(position).get("corconsorcio")));

                //corconsorcioLayout.setBackgroundColor(Color.parseColor(aList.get(position).get("corconsorcio")));
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
                Marker b = googleMap.addMarker(l.getMarker());
                busMarkers.add(b);

            }

            appSingleton.setNumeroOnibusProximos(lista.size());
            PrincipalActivity.numeroLinhasProximas.setText(String.valueOf(appSingleton.getNumeroOnibusProximos()));

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        posicionarUsuarioMapaGPS(usuario.getPosicao());

        click1.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.flicker));
        click2.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.flicker));
        click3.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.flicker));

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
