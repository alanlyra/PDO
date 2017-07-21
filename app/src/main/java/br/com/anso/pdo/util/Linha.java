package br.com.anso.pdo.util;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.anso.pdo.R;

public class Linha {
    private String numero;
    private String vista;
    private String consorcio;
    private LatLng posicao;
    private String corConsorcio;
    private String routeName;
    private String servico;
    private String dataHora;
    private String ordem;
    private String sentido;
    private MarkerOptions markerOptions;

    public LatLng getPosicao() {
        return posicao;
    }

    public void setPosicao(LatLng posicao) {
        this.posicao = posicao;
    }

    public Linha(String routeName, String servico, String consorcio, String corConsorcio, String numero, String vista){
        this.routeName = routeName;
        this.servico = servico;
        this.corConsorcio = corConsorcio;
        this.numero = numero;
        this.vista = vista;
        this.consorcio = consorcio;
    }
    public Linha (){

    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getConsorcio() {
        return consorcio;
    }

    public void setConsorcio(String consorcio) {
        this.consorcio = consorcio;
    }

    public String getVista() {
        return vista;
    }

    public void setVista(String vista) {
        this.vista = vista;
    }

    public String getCorConsorcio() {
        return corConsorcio;
    }

    public void setCorConsorcio(String corConsorcio) {
        this.corConsorcio = corConsorcio;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public void setOrdem(String ordem) {
        this.ordem = ordem;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    public String getDataHora() {
        return dataHora;
    }

    public String getOrdem() {
        return ordem;
    }

    public String getSentido() {
        return sentido;
    }

    public MarkerOptions getMarker() {
        return markerOptions;
    }

    public void setMarkerOptions(Resources resources, Context context){

        String tituloMarcador = Util.setStringNegrito(context.getString(R.string.linha), String.valueOf(Color.GRAY)) + " " + numero + "<br />" +
                                Util.setStringNegrito(context.getString(R.string.veiculo), String.valueOf(Color.GRAY)) + " " + ordem + "<br />"+
                                Util.setStringNegrito(context.getString(R.string.data_hora), String.valueOf(Color.GRAY))+ " " + dataHora + "<br />"+
                                Util.setStringNegrito(context.getString(R.string.sentido), String.valueOf(Color.GRAY)) + " " + sentido;
        this.markerOptions = new MarkerOptions()
                .position(posicao)
                //.icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                .icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.pin_bus_line_number, numero, resources, context)))
                //.icon(BitmapDescriptorFactory.fromBitmap(Util.resizeMapIcons(resources, R.drawable.pin_bus, 90, 90)))
                .title(context.getString(R.string.detalhes)).snippet(tituloMarcador)
                .anchor((float) 0.5, (float) 1);
    }

    private Bitmap writeTextOnDrawable(int drawableId, String text, Resources resources, Context context) {

        Bitmap bm = BitmapFactory.decodeResource(resources, drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        bm = Bitmap.createScaledBitmap(bm, 90, 115, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        //paint.setColor(Color.parseColor(String.valueOf(R.color.color_primary)));
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(context, 8));


        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(context, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos-37, paint);

        return  bm;
    }



    public static int convertToPixels(Context context, int nDP)
    {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f) ;

    }
}
