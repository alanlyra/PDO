package br.com.anso.pdo.rota;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

import br.com.anso.pdo.R;
import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.util.Viagem;


public class CaminhadaRotaObject implements RecursoMapaObject, View.OnClickListener {

    public static final String COR_LINE_CAMINHADA = "#A342B1";

    private ArrayList<Polyline> polylineArraList;
    private Button botao;
    private String tempoViagemInfo;
    private RotaActivity activity;
    private Marker[] origemDestinoMarkers;
    private GoogleMap map;
    private LatLngBounds bounds;
    private int listViewPosition;
    private final TextView tempoViagemTextView;
    private final LinearLayout layoutLinhaSelecionada;
    private final ListView listView;

    public CaminhadaRotaObject(Viagem v, ArrayList<Polyline> lines, Marker[] origemDestinoMarkers, final int listViewPosition, final LatLngBounds bounds, Button b, final RotaActivity activity, final TextView tempoViagemTextView, final GoogleMap map, final LinearLayout layoutLinhaSelecionada, final ListView listView){
        this.tempoViagemInfo = v.getTempoViagemFormatado();
        this.activity = activity;

        this.origemDestinoMarkers = origemDestinoMarkers;
        this.map = map;
        this.polylineArraList = lines;
        this.bounds = bounds;

        this.listViewPosition = listViewPosition;
        this.tempoViagemTextView = tempoViagemTextView;
        this.layoutLinhaSelecionada = layoutLinhaSelecionada;
        this.listView = listView;

        this.botao = b;
        botao.setVisibility(View.VISIBLE);
        this.botao.setOnClickListener(this);
    }

    @Override
    public void destacarMarcadorMapa(int pos) {
        for(Marker m : origemDestinoMarkers)
            m.setIcon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(activity.getResources(), R.drawable.circle3, 36, 36)));

        centralizarNoMapa();
    }

    @Override
    public void resetarMarcadorMapa(int pos) {
        for(Marker m : origemDestinoMarkers)
            m.setIcon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(activity.getResources(), R.drawable.circle1, 26, 26)));
    }

    @Override
    public void resetStyles() {
        botao.setBackgroundResource(R.drawable.person3);

        for(Polyline p : polylineArraList){
            p.setColor(Color.parseColor(COR_LINE_CAMINHADA));
        }
    }

    public void centralizarNoMapa(){
        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.4);

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
    }

    @Override
    public void onClick(View v) {
        tratarCliqueNoBotaoTransbordo(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void tratarCliqueNoBotaoTransbordo(boolean centralizarPelaPolyline){
        View v = this.botao;

        activity.resetButtonsPolylines();

        ((TextView) layoutLinhaSelecionada.findViewById(R.id.num_linha)).setText("Caminhada");
        ((TextView) layoutLinhaSelecionada.findViewById(R.id.consorcio)).setText("");

        layoutLinhaSelecionada.findViewById(R.id.layoutCorConsorcio).setBackgroundResource(R.drawable.walk);
        VectorDrawable gd = (VectorDrawable) layoutLinhaSelecionada.findViewById(R.id.layoutCorConsorcio).getBackground().getCurrent();
        gd.setTint(Color.parseColor(CaminhadaRotaObject.COR_LINE_CAMINHADA));

        //(layoutLinhaSelecionada.findViewById(R.id.layoutCorConsorcio)).setBackgroundColor(Color.parseColor(CaminhadaRotaObject.COR_LINE_CAMINHADA));

        tempoViagemTextView.setText((tempoViagemInfo == null ? "01 min" : tempoViagemInfo).concat(activity.getResources().getString(R.string.de_caminhada)));
        v.setBackgroundResource(R.drawable.person1);

        if(centralizarPelaPolyline) {
            activity.selecionarItemNaLista(listViewPosition);
            Util.animarNovaPosicaoListView(listViewPosition, listView);
            centralizarNoMapa();
        }
    }
}
