package br.com.anso.pdo.splash;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import org.json.JSONException;

import br.com.anso.pdo.R;
import br.com.anso.pdo.principal.PrincipalActivity;
import br.com.anso.pdo.tutorial.TutorialActivity;
import br.com.anso.pdo.vdoactivity.VDOAppCompatActivity;

public class SplashActivity extends VDOAppCompatActivity implements ISplashView {

    private static final int SPLASH_TIME = 2000;
    ISplashPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        presenter = new SplashPresenter(this);

        try {
            presenter.iniciarApp(this.getBaseContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.verificarVersaoApp(getBaseContext());
            }
        }, SPLASH_TIME);
    }

    public void abrirDialogoAtualizarApp(boolean deprecated){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert).setTitle(getResources().getString(R.string.update)).setCancelable(false);
        if(deprecated){
                    builder.setMessage(getResources().getString(R.string.need_update))
                    .setPositiveButton(getResources().getString(R.string.need_update_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            abrirAtualizarAplicativo();
                        }
                    });
        }
        else{
            builder.setMessage(getResources().getString(R.string.ask_update))
                    .setPositiveButton(getResources().getString(R.string.need_update_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            abrirAtualizarAplicativo();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.not_now_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            abrirProximaView();
                        }
                    });

        }

        builder.show();
    }

    public void abrirProximaView(){
        if(presenter.carregaPreferencias(getBaseContext())){
            Intent i = new Intent(SplashActivity.this, PrincipalActivity.class); //TutorialActivity
            startActivity(i);
            finish();
        }
        else{
            Intent i = new Intent(SplashActivity.this, PrincipalActivity.class); //PrincipalActivity
            startActivity(i);
            finish();
        }
    }

    public void abrirAtualizarAplicativo(){
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
        this.finishAffinity();
    }
}