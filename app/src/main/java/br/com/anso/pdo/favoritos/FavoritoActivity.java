package br.com.anso.pdo.favoritos;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.anso.pdo.R;
import br.com.anso.pdo.itinerario.ItinerarioActivity;
import br.com.anso.pdo.linhasdoponto.LinhasdopontoActivity;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Linha;
import br.com.anso.pdo.util.Ponto;
import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.vdoactivity.VDOAppCompatActivity;

public class FavoritoActivity extends VDOAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private AppSingleton appSingleton = AppSingleton.getApp();
    ListView listView, listView2;
    private FrameLayout frameoptions;
    private LinearLayout nenhumPontoEncontrado;
    private LinearLayout nenhumaLinhaEncontrada;
    private LinearLayout linhasLayout;
    private LinearLayout pontoslayout;
    private float historicX = Float.NaN;
    private float historicY = Float.NaN;
    private static final int DELTA = 50;
    private enum Direction {LEFT, RIGHT;}
    private Favorito favorito;
    private Timer timer = null;  //at class level;
    private int DELAY   = 400;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linha_ponto_favoritos);

        listView = (ListView) findViewById(R.id.list_linhas_favoritas);
        listView2 = (ListView) findViewById(R.id.list_pontos_favoritos);

        nenhumPontoEncontrado = (LinearLayout)findViewById(R.id.nenhumPontoEncontrado);
        nenhumaLinhaEncontrada = (LinearLayout)findViewById(R.id.nenhumaLinhaEncontrada);

        setListaLinhasPontosFavoritos();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    public void setListaLinhasPontosFavoritos() {
        final ArrayList<Linha> listaLinhas = appSingleton.getLinhasFavoritas();
        final ArrayList<Ponto> listaPontos = appSingleton.getPontosFavoritos();

        if(listaLinhas.size()>0){
            ArrayAdapter adapterLinhas = carregaLinhasAdapter(R.layout.listview_linhas_favoritas, listaLinhas);
            listView.setAdapter(adapterLinhas);
            abreTelaItinerario(listaLinhas);
        }
        else if(listaLinhas.size()==0){
            listView.setVisibility(View.GONE);
            nenhumaLinhaEncontrada.setVisibility(View.VISIBLE);
        }

        if(listaPontos.size()>0){
            ArrayAdapter adapterPontos = carregaPontosAdapter(R.layout.listview_pontos_favoritos, listaPontos);
            listView2.setAdapter(adapterPontos);
            abreTelaLinhasDoPonto(listaPontos);
        }
        else if(listaPontos.size()==0){
            listView2.setVisibility(View.GONE);
            nenhumPontoEncontrado.setVisibility(View.VISIBLE);
        }
    }

    public void voltar(View view) {
        super.onBackPressed();
    }

    public void abreTelaItinerario(final List<Linha> aList) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                appSingleton.setCorConsorcio(aList.get(position).getCorConsorcio());
                appSingleton.setRouteNameExibirIitnerario(aList.get(position).getRouteName());
                appSingleton.setServicoExibirIitnerario(aList.get(position).getServico());

                Intent i = new Intent(FavoritoActivity.this, ItinerarioActivity.class);
                startActivity(i);
            }
        });
    }

    private void abreTelaLinhasDoPonto(final List<Ponto> aList) {
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                appSingleton.setEnderecoPonto(aList.get(position).getEndereco());
                appSingleton.setIdPonto(aList.get(position).getIdPonto());
                appSingleton.setCoordPonto(aList.get(position).getPosicao());

                Intent intent = new Intent(FavoritoActivity.this, LinhasdopontoActivity.class);
                startActivity(intent);
            }
        });
    }


    public ArrayAdapter carregaLinhasAdapter(final int listViewLayout, final ArrayList<Linha> lista){
        return new ArrayAdapter(this, listViewLayout, lista) {
            public View getView(int position, View convertView, ViewGroup parent) {

                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(listViewLayout, null);

                TextView tt = (TextView) convertView.findViewById(R.id.txt);
                tt.setText(lista.get(position).getNumero());

                TextView vista = (TextView) convertView.findViewById(R.id.cur);
                vista.setText(lista.get(position).getVista());

                LinearLayout corconsorcioLayout = (LinearLayout) convertView.findViewById(R.id.flag);
                corconsorcioLayout.setBackgroundColor(Color.parseColor(lista.get(position).getCorConsorcio()));

                ImageView popUp_btn = (ImageView) convertView.findViewById(R.id.options);
                frameoptions = (FrameLayout) convertView.findViewById(R.id.frameoptions);
                linhasLayout = (LinearLayout) convertView.findViewById(R.id.linearlinhas);

                if (position % 2 == 1) {
                    convertView.setBackgroundResource(R.color.listview2);
                } else if (position % 2 == 0) {
                    convertView.setBackgroundResource(R.color.listview1);
                }

                attachPopupHandler(lista.get(position), popUp_btn, position, linhasLayout);

                return convertView;
            }
        };
    }

    public ArrayAdapter carregaPontosAdapter(final int listViewLayout, final ArrayList<Ponto> lista){
        return new ArrayAdapter(this, listViewLayout, lista) {
            public View getView(int position, View convertView, ViewGroup parent) {

                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(listViewLayout, null);

                TextView tt = (TextView) convertView.findViewById(R.id.txt);
                tt.setText(lista.get(position).getEndereco());

                ImageView popUp_btn = (ImageView) convertView.findViewById(R.id.options);
                frameoptions = (FrameLayout) convertView.findViewById(R.id.frameoptions);
                linhasLayout = (LinearLayout) convertView.findViewById(R.id.linearlinhas);

                if (position % 2 == 1) {
                    convertView.setBackgroundResource(R.color.listview2);
                } else if (position % 2 == 0) {
                    convertView.setBackgroundResource(R.color.listview1);
                }

                attachPopupHandler(lista.get(position), popUp_btn, position, linhasLayout);




                return convertView;
            }
        };
    }

    private void attachPopupHandler(final Object object, final ImageView popUp_btn, final int position, final LinearLayout linhasLayout2) {

        frameoptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getBaseContext(), popUp_btn);

               /* try {
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
                }*/
                popup.getMenuInflater().inflate(R.menu.pop_up_favoritos, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            /*case R.id.favoritar:
                                if(object.getClass().getName().equals(getBaseContext().getPackageName() + ".util.Linha")){
                                    for(int i = 0; i < appSingleton.getLinhasFavoritas().size(); i++){
                                        if (appSingleton.getLinhasFavoritas().get(i).getRouteName().equals(((Linha)object).getRouteName())){
                                            appSingleton.getLinhasFavoritas().remove(i);

                                            atualizaFavoritos("linhas.json", appSingleton.getLinhasFavoritas());
                                            return true;
                                        }
                                    }
                                }
                                else{
                                    for(int i = 0; i < appSingleton.getPontosFavoritos().size(); i++){
                                        if (appSingleton.getPontosFavoritos().get(i).getIdPonto().equals(((Ponto)object).getIdPonto())){
                                            appSingleton.getPontosFavoritos().remove(i);

                                            atualizaFavoritos("pontos.json", appSingleton.getPontosFavoritos());
                                            return true;
                                        }
                                    }
                                }
*/
                            case R.id.itinerario:
                                if(object.getClass().getName().equals(getBaseContext().getPackageName() + ".util.Linha")) {
                                    appSingleton.getLinhasFavoritas().clear();
                                    atualizaFavoritos("linhas.json", appSingleton.getLinhasFavoritas());
                                    return true;
                                }
                                else{
                                    appSingleton.getPontosFavoritos().clear();
                                    atualizaFavoritos("pontos.json", appSingleton.getPontosFavoritos());
                                    return true;
                                }
                            default:
                                return false;
                        }
                    }
                });

                popup.show();
            }
        });

        linhasLayout.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getBaseContext(), popUp_btn);
                final ArrayList<Linha> listaLinhas = appSingleton.getLinhasFavoritas();
                final ArrayList<Ponto> listaPontos = appSingleton.getPontosFavoritos();

                if(listaLinhas.size()>0){
                    ArrayAdapter adapterLinhas = carregaLinhasAdapter(R.layout.listview_linhas_favoritas, listaLinhas);
                    listView.setAdapter(adapterLinhas);
                    abreTelaItinerario(listaLinhas);
                }
                else if(listaLinhas.size()==0){
                    listView.setVisibility(View.GONE);
                    nenhumaLinhaEncontrada.setVisibility(View.VISIBLE);
                }

                if(listaPontos.size()>0){
                    ArrayAdapter adapterPontos = carregaPontosAdapter(R.layout.listview_pontos_favoritos, listaPontos);
                    listView2.setAdapter(adapterPontos);
                    abreTelaLinhasDoPonto(listaPontos);
                }
                else if(listaPontos.size()==0){
                    listView2.setVisibility(View.GONE);
                    nenhumPontoEncontrado.setVisibility(View.VISIBLE);
                }

                if(object.getClass().getName().equals(getBaseContext().getPackageName() + ".util.Linha")){
                    appSingleton.setCorConsorcio(listaLinhas.get(position).getCorConsorcio());
                    appSingleton.setRouteNameExibirIitnerario(listaLinhas.get(position).getRouteName());
                    appSingleton.setServicoExibirIitnerario(listaLinhas.get(position).getServico());

                    Intent i = new Intent(FavoritoActivity.this, ItinerarioActivity.class);
                    startActivity(i);
                }
                else{
                    appSingleton.setEnderecoPonto(listaPontos.get(position).getEndereco());
                    appSingleton.setIdPonto(listaPontos.get(position).getIdPonto());
                    appSingleton.setCoordPonto(listaPontos.get(position).getPosicao());

                    Intent intent = new Intent(FavoritoActivity.this, LinhasdopontoActivity.class);
                    startActivity(intent);
                }

                /*popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.favoritar:
                                if(object.getClass().getName().equals(getBaseContext().getPackageName() + ".util.Linha")){
                                    for(int i = 0; i < appSingleton.getLinhasFavoritas().size(); i++){
                                        if (appSingleton.getLinhasFavoritas().get(i).getRouteName().equals(((Linha)object).getRouteName())){
                                            appSingleton.getLinhasFavoritas().remove(i);

                                            atualizaFavoritos("linhas.json", appSingleton.getLinhasFavoritas());
                                            return true;
                                        }
                                    }
                                }
                                else{
                                    for(int i = 0; i < appSingleton.getPontosFavoritos().size(); i++){
                                        if (appSingleton.getPontosFavoritos().get(i).getIdPonto().equals(((Ponto)object).getIdPonto())){
                                            appSingleton.getPontosFavoritos().remove(i);

                                            atualizaFavoritos("pontos.json", appSingleton.getPontosFavoritos());
                                            return true;
                                        }
                                    }
                                }
                            default:
                                return false;
                        }
                    }
                });*/
            }

        });

        linhasLayout.setOnTouchListener(new View.OnTouchListener() {
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

                            fadeOutView(linhasLayout2);
                            slideInViewLeft(linhasLayout2);

                            final Handler handler          = new Handler();
                            final Runnable mRunnable        = new Runnable() {
                                public void run() {
                                    if(object.getClass().getName().equals(getBaseContext().getPackageName() + ".util.Linha")){
                                        for(int i = 0; i < appSingleton.getLinhasFavoritas().size(); i++){
                                            if (appSingleton.getLinhasFavoritas().get(i).getRouteName().equals(((Linha)object).getRouteName())){
                                                appSingleton.getLinhasFavoritas().remove(i);
                                                atualizaFavoritos("linhas.json", appSingleton.getLinhasFavoritas());
                                            }
                                        }
                                    }
                                    else{
                                        for(int i = 0; i < appSingleton.getPontosFavoritos().size(); i++){
                                            if (appSingleton.getPontosFavoritos().get(i).getIdPonto().equals(((Ponto)object).getIdPonto())){
                                                appSingleton.getPontosFavoritos().remove(i);
                                                atualizaFavoritos("pontos.json", appSingleton.getPontosFavoritos());
                                            }
                                        }
                                    }
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

                            return true;
                        }
                        else if (event.getX() - historicX > DELTA)
                        {
                            Log.d("SWIPE: ", "RIGHT");

                            fadeOutView(linhasLayout2);
                            slideInViewRight(linhasLayout2);

                            final Handler handler          = new Handler();
                            final Runnable mRunnable        = new Runnable() {
                                public void run() {
                                    if(object.getClass().getName().equals(getBaseContext().getPackageName() + ".util.Linha")){
                                        for(int i = 0; i < appSingleton.getLinhasFavoritas().size(); i++){
                                            if (appSingleton.getLinhasFavoritas().get(i).getRouteName().equals(((Linha)object).getRouteName())){
                                                appSingleton.getLinhasFavoritas().remove(i);
                                                atualizaFavoritos("linhas.json", appSingleton.getLinhasFavoritas());
                                            }
                                        }
                                    }
                                    else{
                                        for(int i = 0; i < appSingleton.getPontosFavoritos().size(); i++){
                                            if (appSingleton.getPontosFavoritos().get(i).getIdPonto().equals(((Ponto)object).getIdPonto())){
                                                appSingleton.getPontosFavoritos().remove(i);
                                                atualizaFavoritos("pontos.json", appSingleton.getPontosFavoritos());
                                            }
                                        }
                                    }
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

                            return true;
                        } break;
                    default: return false;
                }
                return false;
            }
        });

    }

    public void atualizaFavoritos(String file, List lista){
        Gson jsonObject = new Gson();
        //Log.d("COUNT_", String.valueOf(appSingleton.getLinhasFavoritas().size()));
        String jsonString = jsonObject.toJson(lista);
        //jsonString = "[]";//para limpar arquivo, basta descomentar
        Util.createAndSaveFile(file, jsonString, getBaseContext());

        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    private void fadeOutView(View view) {
        Animation fadeOut = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_out);
        if (fadeOut != null) {
            fadeOut.setAnimationListener(new ViewAnimationListener(view) {
                @Override
                protected void onAnimationStart(View view, Animation animation) {

                }

                @Override
                protected void onAnimationEnd(View view, Animation animation) {
                    view.setVisibility(View.GONE);
                }
            });
            view.startAnimation(fadeOut);
        }
    }

    private void slideInViewRight(View view) {
        Animation slideIn = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_in_right);
        if (slideIn != null) {
            slideIn.setAnimationListener(new ViewAnimationListener(view) {
                @Override
                protected void onAnimationStart(View view, Animation animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                protected void onAnimationEnd(View view, Animation animation) {

                }
            });
            view.startAnimation(slideIn);
        }
    }

    private void slideInViewLeft(View view) {
        Animation slideIn = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_in_left);
        if (slideIn != null) {
            slideIn.setAnimationListener(new ViewAnimationListener(view) {
                @Override
                protected void onAnimationStart(View view, Animation animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                protected void onAnimationEnd(View view, Animation animation) {

                }
            });
            view.startAnimation(slideIn);
        }
    }

    private abstract class ViewAnimationListener implements Animation.AnimationListener {

        private final View view;

        protected ViewAnimationListener(View view) {
            this.view = view;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            onAnimationStart(this.view, animation);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            onAnimationEnd(this.view, animation);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        protected abstract void onAnimationStart(View view, Animation animation);
        protected abstract void onAnimationEnd(View view, Animation animation);
    }
}
