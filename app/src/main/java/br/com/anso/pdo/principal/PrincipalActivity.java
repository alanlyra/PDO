package br.com.anso.pdo.principal;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.anso.pdo.R;
import br.com.anso.pdo.buscaLinhaRota.BuscaLinhaRotaActivity;

import br.com.anso.pdo.selecionarEndereco.SelecionarEnderecoActivity;
import br.com.anso.pdo.util.AppSingleton;
import br.com.anso.pdo.util.Usuario;
import br.com.anso.pdo.pdoactivity.PDOAppCompatActivity;

public class PrincipalActivity extends PDOAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IPrincipalView {

    private AppSingleton appPresenter = AppSingleton.getApp();
    private Usuario usuario = Usuario.getInstance();
    private Toolbar toolbar;
    private DrawerLayout drawer;
    public static TextView numeroLinhasProximas;
    private ExibeOnibusProximosFragment tab1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        usuario.carregaGPS(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        configurarViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if(tabLayout!=null) tabLayout.setupWithViewPager(viewPager);

        //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }


    private void configurarViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        tab1 = new ExibeOnibusProximosFragment();
        //tab2 = new ExibePontosProximosFragment();

        adapter.addFragment(tab1, getResources().getString(R.string.titulo_tab_linhas));
        //adapter.addFragment(tab2, getResources().getString(R.string.titulo_tab_pontos));
        viewPager.setAdapter(adapter);
        //criaMenu();
    }

    private void criaMenu() {
        /*drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView!=null){
            navigationView.setItemTextColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(), R.color.default_screen_bg)));
            navigationView.setItemIconTintList(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(), R.color.color_primary)));
            //navigationView.setItemBackgroundResource(R.drawable.itembackground);
            navigationView.setNavigationItemSelectedListener(this);
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view3);
        if(navigationView!=null){
            navigationView.setItemTextColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(), R.color.not_selected_tab_color)));
            //navigationView.setItemBackgroundResource(R.drawable.itembackgroundpart3);
            navigationView.setNavigationItemSelectedListener(this);
        }

        numeroLinhasProximas = (TextView) findViewById(R.id.numerolinhas);
        atualizaNumLinhas();*/

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
        finish();
        System.exit(0);
        }
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
    }


    public void definirlocalpartida(View view) {
        Intent i;
        i = new Intent(PrincipalActivity.this, SelecionarEnderecoActivity.class);
        startActivity(i);
    }

    public void definirlocaldestino(View view) {
        Intent i;
        i = new Intent(PrincipalActivity.this, SelecionarEnderecoActivity.class);
        startActivity(i);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.onibusproximos) {
            // Handle the action
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer!=null) drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void tentarNovamenteCarregarLinhas(View v){
        tab1.tentarNovamenteCarregarLinhas(v);
    }

    public void centerUsuarioMapatab1(View v) {tab1.centerUsuarioMapa(v);}

    public void atualizaNumLinhas(){
        numeroLinhasProximas.setText(String.valueOf(appPresenter.getNumeroOnibusProximos()));
    }
}
