package br.com.anso.pdo.buscaLinhaRota;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.anso.pdo.R;
import br.com.anso.pdo.principal.ExibeOnibusProximosFragment;
import br.com.anso.pdo.principal.PrincipalActivity;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Util;
import br.com.anso.pdo.vdoactivity.VDOAppCompatActivity;

public class BuscaLinhaRotaActivity extends VDOAppCompatActivity implements IBuscaLinhaRotaView {
    private TextView localPartidaLinhasRotas, localDestinoLinhasRotas;
    private Tab1Fragment tab1;
    private Tab2Fragment tab2;
    private AppSingleton app = AppSingleton.getApp();
    private static int userTimeBuscaLinhaRota = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.busca_linha_rota);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        if(tabLayout!=null){
            tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.linhas)));
            tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.rotas)));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabLayout.setTabTextColors(Color.parseColor("#95a4a6"), Color.parseColor("#009FD6"));

            /*tabLayout.setTabTextColors(R.color.not_selected_tab_color, R.color.color_primary);*/

            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

            final PagerAdapter adapter = new PagerAdapter
                    (getSupportFragmentManager(), tabLayout.getTabCount());


            if (viewPager != null) {
                viewPager.setAdapter(adapter);
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

                tab1 = (Tab1Fragment) adapter.getItem(0);
                tab2 = (Tab2Fragment) adapter.getItem(1);


                viewPager.setCurrentItem(app.getAbaDefault());

                viewPager.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        return true;
                    }
                });

                //localDestinoLinhasRotas.setText(app.getLocalDestinoRota());
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;
                i = new Intent(BuscaLinhaRotaActivity.this, PrincipalActivity.class);
                startActivity(i);
                finish();
            }
        }, app.getUserTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    public void pesquisarLinhas(View v){
        tab1.pesquisarLinhas(v);
    }

    public void pesquisarRotas(View v){
        tab2.pesquisarRotas(v);
    }

    public void voltar(View view){
        app.setAbaDefault(0);
        super.onBackPressed();
        finish();
    }

    public void definirlocalpartida(View v){
        localPartidaLinhasRotas = (TextView)findViewById(R.id.localPartidaLinhasRotas);
        Util.selecionarLocal(this, getResources().getString(R.string.definir_local_partida), new Util.ISetTextCallBack() {
            @Override
            public void setText(String value) {
                String municipio = "";
                if(Util.nonBlank(value))
                    municipio = " - " + app.getMunicipios().get(app.getIndexMunicipioOrigem());

                localPartidaLinhasRotas.setText( value.concat(municipio));
                app.setEnderecoOrigem( value, app.getEnderecoOrigemWS() );
                app.setEnderecoOrigem(app.getEnderecoOrigemExibicao(), value);
            }
        });
    }

    public void definirlocaldestino(View v){
        localDestinoLinhasRotas = (TextView)findViewById(R.id.localDestinoLinhasRotas);
        Util.selecionarLocal(this, getResources().getString(R.string.definir_local_destino), new Util.ISetTextCallBack() {
            @Override
            public void setText(String value) {
                String municipio = "";
                if(Util.nonBlank(value))
                    municipio = " - " + app.getMunicipios().get(app.getIndexMunicipioDestino());


                localDestinoLinhasRotas.setText( value.concat(municipio));
                app.setEnderecoDestino( value, app.getEnderecoDestinoWS() );
                app.setEnderecoDestino(app.getEnderecoDestinoExibicao(), value);
            }
        });
    }

    public void InverterPartidaeDestino (View view){

        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        ImageView image= (ImageView) findViewById(R.id.inverterImageBuscalinhaserotas);

        image.startAnimation(rotate);

        localPartidaLinhasRotas = (TextView)findViewById(R.id.localPartidaLinhasRotas);
        localDestinoLinhasRotas = (TextView)findViewById(R.id.localDestinoLinhasRotas);
        String partida = localPartidaLinhasRotas.getText().toString();
        String destino = localDestinoLinhasRotas.getText().toString();
        localPartidaLinhasRotas.setText(destino);
        localDestinoLinhasRotas.setText(partida);

        String tmp = app.getEnderecoOrigemExibicao();
        String tmpWS = app.getEnderecoOrigemWS();
        app.setEnderecoOrigem(app.getEnderecoDestinoExibicao(), app.getEnderecoDestinoWS());
        app.setEnderecoDestino(tmp, tmpWS);

        int idx = app.getIndexMunicipioOrigem();
        app.setIndexMunicipioOrigem(app.getIndexMunicipioDestino());
        app.setIndexMunicipioDestino(idx);
    }


}
