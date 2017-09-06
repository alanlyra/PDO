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

public class PrincipalActivity extends PDOAppCompatActivity implements IPrincipalView {

    private AppSingleton appPresenter = AppSingleton.getApp();
    private Usuario usuario = Usuario.getInstance();
    private Toolbar toolbar;
    private ExibeOnibusProximosFragment tab1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);

        usuario.carregaGPS(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        configurarViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if(tabLayout!=null) tabLayout.setupWithViewPager(viewPager);
    }

    private void configurarViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        tab1 = new ExibeOnibusProximosFragment();
        adapter.addFragment(tab1, getResources().getString(R.string.titulo_tab_linhas));
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);

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

}
