package br.com.anso.pdo.buscaPontosEstacoes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.anso.pdo.R;
import br.com.anso.pdo.linhasdoponto.LinhasdopontoActivity;
import br.com.anso.pdo.resultadoRotas.ResultadoRotasActivity;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.DoubleClick;
import br.com.anso.pdo.util.InfoMarkerPersonalizado;
import br.com.anso.pdo.util.Ponto;
import br.com.anso.pdo.util.Usuario;
import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.util.VdoLocationCallback;
import br.com.anso.pdo.vdoactivity.VDOAppCompatActivity;

public class BuscaPontosEstacoesActivity extends VDOAppCompatActivity implements OnMapReadyCallback, IBuscaPontosEstacoesView, VdoLocationCallback, SeekBar.OnSeekBarChangeListener {

    private IBuscaPontosEstacoesView.IBuscaPontosEstacoesPresenter presenter;
    private TextView localDestinoBuscaPontosEstacoes;
    private TextView localPartidaBuscaPontosEstacoes;
    private GoogleMap googleMap;
    private LatLng pos;
    private Marker userMaker;
    private GroundOverlay groundOverlay;
    private Paint p = new Paint();
    private ArrayList<Marker> stopMarkers;
    private int raio[] = {250, 500, 1000, 1500, 2000};
    private int raioInicial = raio[2];
    private CharSequence text;
    AppSingleton appSingleton = AppSingleton.getApp();
    private Usuario usuario;
    private Timer timer = null;  //at class level;
    private int DELAY   = 500;
    private SlidingUpPanelLayout layout;
    private boolean collapseLayout = false;

    private FrameLayout seekBarPontosProximos;
    private FrameLayout loadingBar;

    private LinearLayout emptyView;
    private ProgressBar bar;

    public BuscaPontosEstacoesActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appSingleton.setViewPontosEstacoes(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_pontos_estacoes);
        initializeVariables();
        usuario = Usuario.getInstance();

        text = getResources().getString(R.string.definir_locais_partidas_destino);

        SeekBar mSeekBar = (SeekBar) findViewById(R.id.seekBar1);
        if(mSeekBar != null)
            mSeekBar.setOnSeekBarChangeListener(this);


        if (Util.nonBlank(appSingleton.getEnderecoOrigemExibicao())) {
            String municipioOrigem = " - ".concat(appSingleton.getMunicipios().get(appSingleton.getIndexMunicipioOrigem()));
            localPartidaBuscaPontosEstacoes.setText(appSingleton.getEnderecoOrigemExibicao().concat(municipioOrigem));
        }

        if (Util.nonBlank(appSingleton.getEnderecoDestinoExibicao())) {
            String municipioDestino = " - ".concat(appSingleton.getMunicipios().get(appSingleton.getIndexMunicipioDestino()));
            localDestinoBuscaPontosEstacoes.setText(appSingleton.getEnderecoDestinoExibicao().concat(municipioDestino));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        emptyView = (LinearLayout) findViewById(R.id.pontosProximosNaoCarregados);
        seekBarPontosProximos = (FrameLayout) findViewById(R.id.seekBarPontosProximos);
        loadingBar = (FrameLayout) findViewById(R.id.loadingBar);
        FrameLayout frameraiobusca = (FrameLayout) findViewById(R.id.seekBarPontosProximos2);
        layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout_buscapontoseestacoes);

        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        if(bar!=null) bar.setVisibility(View.VISIBLE);

        exibirLoadingListaResultado();
        presenter = new BuscaPontosEstacoesPresenter(this);

        if (frameraiobusca != null) {
            frameraiobusca.setOnClickListener(new DoubleClick() {

                @Override
                public void onSingleClick(View v) {

                    final Handler handler          = new Handler();
                    final Runnable mRunnable        = new Runnable() {
                        public void run() {
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
        }
    }

    public void voltar(View view) {
        super.onBackPressed();
    }

    @Override
    public void setErroTimeout(String msg){
        bar.setVisibility(View.GONE);
        ((TextView) emptyView.findViewById(R.id.pontosProximosNaoCarregadosText)).setText(msg);
        loadingBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.VISIBLE);
        seekBarPontosProximos.setVisibility(View.GONE);
    }

    public void definirlocalpartida(View v){
        localPartidaBuscaPontosEstacoes = (TextView)findViewById(R.id.localPartidaBuscaPontosEstacoes);
        Util.selecionarLocal(this, getResources().getString(R.string.definir_local_partida), new Util.ISetTextCallBack() {
            @Override
            public void setText(String value) {
                String municipio = "";
                if(Util.nonBlank(value))
                    municipio = " - " + appSingleton.getMunicipios().get(appSingleton.getIndexMunicipioOrigem());

                localPartidaBuscaPontosEstacoes.setText( value.concat(municipio));
                appSingleton.setEnderecoOrigem( value, appSingleton.getEnderecoOrigemWS() );
                appSingleton.setEnderecoOrigem(appSingleton.getEnderecoOrigemExibicao(), value);
            }
        });
    }

    public void definirlocaldestino(View v){
        localDestinoBuscaPontosEstacoes = (TextView)findViewById(R.id.localDestinoBuscaPontosEstacoes);
        Util.selecionarLocal(this, getResources().getString(R.string.definir_local_destino), new Util.ISetTextCallBack() {
            @Override
            public void setText(String value) {
                String municipio = "";
                if(Util.nonBlank(value))
                    municipio = " - " + appSingleton.getMunicipios().get(appSingleton.getIndexMunicipioDestino());


                localDestinoBuscaPontosEstacoes.setText( value.concat(municipio));
                appSingleton.setEnderecoDestino( value, appSingleton.getEnderecoDestinoWS() );
                appSingleton.setEnderecoDestino(appSingleton.getEnderecoDestinoExibicao(), value);
            }
        });
    }

    // A private method to help us initialize our variables.
    private void initializeVariables() {
        //seekBar = (SeekBar) findViewById(R.id.seekBar1);
        localDestinoBuscaPontosEstacoes = (TextView) findViewById(R.id.localDestinoBuscaPontosEstacoes);
        localPartidaBuscaPontosEstacoes = (TextView) findViewById(R.id.localPartidaBuscaPontosEstacoes);
        //Button exibirResultadosButton = (Button) findViewById(R.id.exibirResultadosButton);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        //if(!Usuario.getInstance().getIsGPSLocation()) Usuario.getInstance().getPosicaoGPS(this, (VDOAppCompatActivity) this);

        posicionarUsuarioMapaGPS(usuario.getPosicao());

        this.googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                posicionarUsuarioMapaGPS(marker.getPosition());
                appSingleton.atualizaUsuarioViews(marker.getPosition());
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

        });
    }


    public void posicionarUsuarioMapaGPS(LatLng pos) {
        this.pos = pos;//atualiza variável global pra ser usada no shape circular referente ao raio de busca
        if (this.userMaker != null) this.userMaker.remove();

        usuario.setPosicao(pos);
        usuario.setMarker(getResources());

        userMaker = googleMap.addMarker(usuario.getMarker().draggable(true));//marcador

        addCircleToMap(raioInicial, pos);
        presenter.consultaPontosEstacoesWebservice(pos, this.getBaseContext());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
    }

    public void centerUsuarioMapa(View v){
        usuario.recentralizarGPS(this);
    }

    @Override
    public void carregaPontosNoMapa(List<Ponto> listaDePontos, int raio) {
        bar.setVisibility(View.GONE);

        if(listaDePontos.size() == 0){
            loadingBar.setVisibility(View.VISIBLE);
            ((TextView) emptyView.findViewById(R.id.pontosProximosNaoCarregadosText)).setText(R.string.pontos_proximos_nao_carregados);
            emptyView.setVisibility(View.VISIBLE);
            seekBarPontosProximos.setVisibility(View.GONE);
        }
        else{
            loadingBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            seekBarPontosProximos.setVisibility(View.VISIBLE);
        }


        if (googleMap != null) {
            if (stopMarkers != null) {
                for (Marker m : stopMarkers) {
                    m.remove();
                }
            }

            stopMarkers = new ArrayList<>();
            final HashMap<Marker, Ponto> hashMarker = new HashMap<>();

            for (Ponto ponto : listaDePontos) {
                if(ponto.getDistancia()<=raio){
                    Marker b = googleMap.addMarker(new MarkerOptions()
                            .position(ponto.getPosicao())
                            .icon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(getResources(), R.drawable.pin_bus_station, 50, 65)))
                            .title(ponto.getEndereco())
                            .anchor((float) 0.5, (float) 1));

                    hashMarker.put(b, ponto);
                    stopMarkers.add(b);

                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker m) {

                            //Log.d("POINT_INFO", hashMarker.get(m).getEndereco()+" ID="+hashMarker.get(m).getIdPonto()+" POS="+hashMarker.get(m).getPosicao());
                            appSingleton.setEnderecoPonto(hashMarker.get(m).getEndereco());
                            appSingleton.setIdPonto(hashMarker.get(m).getIdPonto());
                            appSingleton.setCoordPonto(hashMarker.get(m).getPosicao());

                            Intent intent = new Intent(getBaseContext(), LinhasdopontoActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }

            googleMap.setInfoWindowAdapter(new InfoMarkerPersonalizado(getBaseContext(), hashMarker));
        }
    }


    private void addCircleToMap(int radiusM, LatLng centro) {

        if(groundOverlay != null)
            groundOverlay.remove();

        // draw circle
        Bitmap bm = Bitmap.createBitmap(radiusM, radiusM, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        p.setColor(ContextCompat.getColor(getBaseContext(),R.color.not_selected_tab_color));
        c.drawCircle(radiusM/2, radiusM/2, radiusM/2, p);

        // generate BitmapDescriptor from circle Bitmap
        BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);

        groundOverlay = googleMap.addGroundOverlay(new GroundOverlayOptions().
                image(bmD).
                position(centro,radiusM*2,radiusM*2).
                transparency(0.4f));
    }

    @Override
    public void tratarPosicaoGPS(LatLng pos) {
        /*posicionarUsuarioMapaGPS();
        presenter.consultaPontosEstacoesWebservice(raioInicial);*/
    }

    public void InverterPartidaeDestino (View view){

        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        ImageView image= (ImageView) findViewById(R.id.inverterImageBuscaPontosEstacoes);

        if(image!=null) image.startAnimation(rotate);
        TextView tv1 = (TextView)findViewById(R.id.localPartidaBuscaPontosEstacoes);
        TextView tv2 = (TextView)findViewById(R.id.localDestinoBuscaPontosEstacoes);

        String partida="";
        String destino="";

        if(tv1 != null) partida = tv1.getText().toString();
        if(tv2 != null) destino = tv2.getText().toString();

        if (tv1 != null) {
            tv1.setText(destino);
        }
        if (tv2 != null) {
            tv2.setText(partida);
        }

        String tmp = appSingleton.getEnderecoOrigemExibicao();
        String tmpWS = appSingleton.getEnderecoOrigemWS();
        appSingleton.setEnderecoOrigem(appSingleton.getEnderecoDestinoExibicao(), appSingleton.getEnderecoDestinoWS());
        appSingleton.setEnderecoDestino(tmp, tmpWS);

        int idx = appSingleton.getIndexMunicipioOrigem();
        appSingleton.setIndexMunicipioOrigem(appSingleton.getIndexMunicipioDestino());
        appSingleton.setIndexMunicipioDestino(idx);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        TextView tv250mts = (TextView)findViewById(R.id.distancia250mts);
        TextView tv500mts = (TextView)findViewById(R.id.distancia500mts);
        TextView tv1km = (TextView)findViewById(R.id.distancia1km);
        TextView tv1_5km = (TextView)findViewById(R.id.distancia1_5km);
        TextView tv2km = (TextView)findViewById(R.id.distancia2km);

        TextView textViews[] = {tv250mts, tv500mts, tv1km, tv1_5km, tv2km};

        carregaPontosNoMapa(appSingleton.getListaDePontosPorRaio(), raio[progress]);
        addCircleToMap(raio[progress], pos);

        for (int i=0; i<textViews.length; i++){
            if(i==progress)
                textViews[i].setTextColor(ContextCompat.getColor(getBaseContext(),R.color.color_primary));
            else
                textViews[i].setTextColor(ContextCompat.getColor(getBaseContext(), R.color.not_selected_tab_color));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void pesquisarRotas(View v) {
        if(Util.nonBlank(appSingleton.getEnderecoOrigemExibicao()) && Util.nonBlank(appSingleton.getEnderecoDestinoExibicao())){
            Intent i = new Intent(this, ResultadoRotasActivity.class);
            startActivity(i);
        }
        else{
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(BuscaPontosEstacoesActivity.this,
                    text, duration).show();
        }
    }

    private void exibirLoadingListaResultado(){
        seekBarPontosProximos.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        loadingBar.setVisibility(View.VISIBLE);
        bar.setVisibility(View.VISIBLE);
    }

    public void tentarNovamente(View v){
        exibirLoadingListaResultado();
        presenter.consultaPontosEstacoesWebservice(usuario.getPosicao(),this.getBaseContext());
    }
}
