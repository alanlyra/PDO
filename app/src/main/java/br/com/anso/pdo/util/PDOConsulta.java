package br.com.anso.pdo.util;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class PDOConsulta extends AsyncTask<String, Void, String> {

    public static final String TOKEN = "17412336481479";
    public static final String URL_BASE_WS = "http://www.vadeonibus.com.br/VdoWS1.0/vdoconsulta/";

    PDOConsultaCallback callback;

    public PDOConsulta(PDOConsultaCallback c){
        this.callback = c;
    }

    @Override
    public String doInBackground(String... params) {

        try {
            params[0] = Util.retiraEspacosEAcentos(params[0]);
            URL url = new URL(params[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(60000);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return Util.optStringFromInputStream(in);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "{timeout: true}";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        callback.callback(s);
    }
}
