package br.com.anso.pdo.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

import br.com.anso.pdo.R;

public class InfoMarkerPersonalizado implements GoogleMap.InfoWindowAdapter {
    private Context context;
    private HashMap<Marker, Ponto> hash;

    public InfoMarkerPersonalizado(Context context, HashMap<Marker, Ponto> hash){
        super();

        this.context = context;
        this.hash = hash;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.info_marker_personalizado, null);
        TextView tv = (TextView) view.findViewById(R.id.caption_text_id);

        Ponto p = hash.get(marker);
        if(p != null) {
            tv.setText(p.getEndereco());
            return view;
        }
        else
            return null;
    }
}
