package br.com.anso.pdo.util;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

public class TimerApp{

    private Timer timerAtual = new Timer();
    private final Handler handler = new Handler();


    public void ativaTimer(int time){
        TimerTask task = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        //Log.d("TIMER", "Timer ativado");
                    }
                });
            }
        };

        timerAtual.schedule(task, time, time);
    }
}