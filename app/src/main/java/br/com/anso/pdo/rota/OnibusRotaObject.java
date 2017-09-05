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


public class OnibusRotaObject implements RecursoMapaObject, View.OnClickListener {
    public static final String COR_LINE_ONIBUS = "#002F3F";
    public static final String COR_LINE_ONIBUS_SELECIONADO = "#009FD6";

    private ArrayList<Polyline> polylineArraList;
    private ArrayList<Marker> markersArrayList;
    private Button botao;
    private Viagem viagem;
    private RotaActivity activity;
    private int listViewPosition;
    private GoogleMap map;
    private LatLngBounds bounds;
    private final TextView tempoViagemTextView;
    private final LinearLayout layoutLinhaSelecionada;
    private final ListView listView;

    public OnibusRotaObject(final Viagem v, ArrayList<Polyline> lines, ArrayList<Marker> pontosMarkers, final int listViewPosition, final LatLngBounds bounds, Button b, final RotaActivity activity, final TextView tempoViagemTextView, final GoogleMap map, final LinearLayout layoutLinhaSelecionada, final ListView listView){
        this.viagem = v;
        this.activity = activity;
        this.map = map;
        this.bounds = bounds;

        this.listViewPosition = listViewPosition;

        this.polylineArraList = lines;
        this.markersArrayList = pontosMarkers;

        this.tempoViagemTextView = tempoViagemTextView;
        this.layoutLinhaSelecionada = layoutLinhaSelecionada;
        this.listView = listView;

        this.botao = b;
        botao.setVisibility(View.VISIBLE);
        this.botao.setOnClickListener(this);
    }

    @Override
    public void destacarMarcadorMapa(int pos) {
        pos = pos - listViewPosition;
        if(pos >= 0 && pos < markersArrayList.size()){
            markersArrayList.get(pos).setIcon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(activity.getResources(), R.drawable.circle3, 36, 36)));

            map.animateCamera(CameraUpdateFactory.newLatLng(markersArrayList.get(pos).getPosition()));
        }

    }

    @Override
    public void resetarMarcadorMapa(int pos) {
        pos = pos - listViewPosition;
        if(pos >= 0 && pos < markersArrayList.size()){
            markersArrayList.get(pos).setIcon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(activity.getResources(), R.drawable.circle1, 26, 26)));
        }
    }

    @Override
    public void resetStyles() {
        this.botao.setBackgroundResource(R.drawable.bus3);

        for(Polyline p : polylineArraList){
            p.setColor(Color.parseColor(COR_LINE_ONIBUS));
        }
    }

    @Override
    public void centralizarNoMapa() {
        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.3);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
    }

    @Override
    public void onClick(View view) {
        tratarCliqueNoBotaoTransbordo(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void tratarCliqueNoBotaoTransbordo(boolean centralizarPelaPolyline) {
        View view = this.botao;

        activity.resetButtonsPolylines();
        String txt = activity.getResources().getString(R.string.de_viagem);

        String tempoViagemInfo = viagem.getTempoViagemFormatado();

        if(tempoViagemInfo.contains("N/D") || tempoViagemInfo == null)
            tempoViagemTextView.setText("");
        else
            tempoViagemTextView.setText(tempoViagemInfo.concat(" "+txt));
        view.setBackgroundResource(R.drawable.bus);
        ((TextView) layoutLinhaSelecionada.findViewById(R.id.num_linha)).setText(viagem.getLinha().getNumero());
        ((TextView) layoutLinhaSelecionada.findViewById(R.id.consorcio)).setText(viagem.getLinha().getConsorcio());

        layoutLinhaSelecionada.findViewById(R.id.layoutCorConsorcio).setBackgroundResource(R.drawable.busvector);
        VectorDrawable gd = (VectorDrawable) layoutLinhaSelecionada.findViewById(R.id.layoutCorConsorcio).getBackground().getCurrent();
        gd.setTint(Color.parseColor(viagem.getLinha().getCorConsorcio()));
        //(layoutLinhaSelecionada.findViewById(R.id.layoutCorConsorcio)).setBackgroundColor(Color.parseColor(viagem.getLinha().getCorConsorcio()));

        for(Polyline p : polylineArraList) {
            p.setColor(Color.parseColor(COR_LINE_ONIBUS_SELECIONADO));
        }

        if(centralizarPelaPolyline){
            activity.selecionarItemNaLista(listViewPosition);
            Util.animarNovaPosicaoListView(listViewPosition, listView);
            centralizarNoMapa();
        }

    }
}
