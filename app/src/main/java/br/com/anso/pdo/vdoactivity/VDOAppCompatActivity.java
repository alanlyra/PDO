package br.com.anso.pdo.vdoactivity;

import android.support.v7.app.AppCompatActivity;

public class VDOAppCompatActivity extends AppCompatActivity {
    public static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        active = false;
    }
}
