package br.com.anso.pdo.pdoactivity;

import android.app.Activity;

public class PDOActivity extends Activity {
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
