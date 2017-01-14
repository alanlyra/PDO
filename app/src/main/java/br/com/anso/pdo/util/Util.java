package br.com.anso.pdo.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.anso.pdo.R;
import br.com.anso.pdo.selecionarEndereco.ISelecionarEnderecoView;
import br.com.anso.pdo.selecionarEndereco.SelecionarEnderecoActivity;



public class Util {

    public static Bitmap resizeMapIcons(Resources res, int icon, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(res, icon);
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    // convert InputStream to String
    public static String optStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    public static String retiraEspacosEAcentos(String str) {
        str = str.replaceAll("[ÂÀÁÄÃ]", "A");
        str = str.replaceAll("[âãàáä]", "a");
        str = str.replaceAll("[ÊÈÉË]", "E");
        str = str.replaceAll("[êèéë]", "e");
        str = str.replaceAll("[ÎÍÌÏ]", "I");
        str = str.replaceAll("[îíìï]", "i");
        str = str.replaceAll("[ÔÕÒÓÖ]", "O");
        str = str.replaceAll("[ôõòóö]", "o");
        str = str.replaceAll("[ÛÙÚÜ]", "U");
        str = str.replaceAll("[ûúùü]", "u");
        str = str.replaceAll("Ç", "C");
        str = str.replaceAll("ç", "c");
        str = str.replaceAll("[ýÿ]", "y");
        str = str.replaceAll("Ý", "Y");
        str = str.replaceAll("ñ", "n");
        str = str.replaceAll("Ñ", "N");
        //  str = str.replaceAll("['<>\\|/]","");
        str = str.replaceAll("\\s", "+");

        return str;
    }

    public static String construirQueryUrlConsultaRotas(Consulta c) {
        String query = "";

        try {
            query += "&origemLogradouro=" + URLEncoder.encode(c.logradouroOrigem, "UTF-8");
            query += "&origemNumero=" + URLEncoder.encode(c.numeroOrigem, "UTF-8");
            query += "&origemMunicipio=" + URLEncoder.encode(c.municipioOrigem, "UTF-8");

            query += "&destinoLogradouro=" + URLEncoder.encode(c.logradouroDestino, "UTF-8");
            query += "&destinoNumero=" + URLEncoder.encode(c.numeroDestino, "UTF-8");
            query += "&destinoMunicipio=" + URLEncoder.encode(c.municipioDestino, "UTF-8");

            query += "&raioDeBusca=" + URLEncoder.encode(String.valueOf(c.raioBusca), "UTF-8");
            query += "&metodoOrdenacao=" + URLEncoder.encode(String.valueOf(c.metodoOrdenacao), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return query;
    }

    public static String converterMinutosParaTempoViagem(double tempoViagem, Context context) {
        String str = "";

        if(tempoViagem <= 0){
            str = "N/D";
        }
        else if (tempoViagem > 0 && tempoViagem < 1) {
            str = context.getResources().getString(R.string.tempo_viagem_parte1);//"Tempo de viagem N/D";
        } else {
            int hrs = (int) tempoViagem / 60;
            int min = (int) tempoViagem % 60;

            str += (hrs > 0 ? hrs + " h " : "");
            str += (min > 0 ? min + " min " : "");

            str += context.getResources().getString(R.string.tempo_viagem_parte2);
        }

        return str;
    }

    public static String converterSegundosParaTempoViagem(double tempoViagem) {
        String str = "";

        if (tempoViagem < 0.1) {
            str = "";//"Tempo de viagem N/D";
        } else {
            int hrs = (int) tempoViagem / 60;
            int min = (int) tempoViagem % 60;

            str += (hrs > 0 ? hrs + "min " : "");
            str += (min > 0 ? min + "s " : "");
        }

        return str;
    }

    public static String converterMinutosParaTempoViagemSimples(int tempo){
        String tmp;
        int hours = tempo / 60;
        int minutes = tempo % 60;
        if(hours == 0)
            tmp = (minutes+"min");
        else if(minutes == 0)
            tmp = (hours+"h");
        else
            tmp = (hours+"h "+minutes+"min");
        return(tmp);
    }

    public static String setString(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

    public static String setStringNegrito(String text, String color) {
        return "<font color=" + color + ">" + "<b>" + text + "</b>" + "</font>";
    }


    public interface IGenericCallBack {
        void execute(Object parameter);
    }

    public interface ISetTextCallBack {
        void setText(String value);
    }

    private static Map<String, IGenericCallBack> genericCallback = new HashMap<>();

    public static void putCallback(String nome, IGenericCallBack callback) {
        genericCallback.put(nome, callback);
    }

    public static void executaCallback(String nome, Object parameter) {
        if (genericCallback.containsKey(nome)) {
            genericCallback.get(nome).execute(parameter);
        } else {
            Log.d("ErroCallback", "Callback não encontrada: " + nome);
        }
    }

    public static void selecionarLocal(Context ctx, String titulo, final Util.ISetTextCallBack callback) {
        Intent i = new Intent(ctx, SelecionarEnderecoActivity.class);

        Bundle b = new Bundle();
        b.putString("titulo", titulo);
        i.putExtras(b);

        Util.putCallback("enderecoSelecionado", new Util.IGenericCallBack() {
            @Override
            public void execute(Object parameter) {
                if (parameter != null)
                    if (parameter instanceof ISelecionarEnderecoView) {
                        ISelecionarEnderecoView view = (ISelecionarEnderecoView) parameter;

                        callback.setText(view.getEndereco());
                    } else
                        Log.d("ErroTypeCast", "Parametro não é do tipo certo: " + parameter.getClass().getName());
            }
        });

        ctx.startActivity(i);
    }

    static public boolean nonBlank(final String str) {
        return str != null && str.trim().length() > 0;
    }

    public static void carregarListaMunicipiosAtendidos() {
        AppSingleton app = AppSingleton.getApp();
        ArrayList<String> municipiosPre = new ArrayList<>();
        municipiosPre.add("Rio de Janeiro");
        app.setMunicipios(municipiosPre);

        String url = VDOConsulta.URL_BASE_WS + "obtermunicipiosatendidos?token=" + VDOConsulta.TOKEN;

        new VDOConsulta(new VDOConsultaCallback() {
            @Override
            public void callback(String result) {
                preencherListaMunicipios(result);
            }
        }).execute(url);
    }

    private static void preencherListaMunicipios(String jsonMunicipios) {
        AppSingleton app = AppSingleton.getApp();

        ArrayList<String> municipios = new ArrayList<>();
        municipios.add("Rio de Janeiro");

        try {
            JSONObject obj = new JSONObject(jsonMunicipios);
            JSONArray lista = obj.getJSONArray("municipios");

            for (int i = 0; i < lista.length(); i++) {
                municipios.add(lista.optString(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        app.setMunicipios(municipios);
        //Log.v("TAG", municipios.size() + " municipios carregados");
    }

    public static ArrayList<LatLng> decodePoly(String encoded) {

        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public static void createAndSaveFile(String path, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(path, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            //Log.d("ESCREVEU", data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray readJsonData(String path, Context context) {

        try {
            InputStream inputStream = context.openFileInput(path);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();

                return new JSONArray(stringBuilder.toString());
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static String readFileData(String path, Context context) {

        try {
            InputStream inputStream = context.openFileInput(path);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();

                return stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return "";
    }

    public static String optString(JSONObject jsonObj, String string){
        return jsonObj.optString(string)!=null ? jsonObj.optString(string) : "";
    }

    public static void carregarInformacoesVersaoApp(final Context context){
        final AppSingleton app = AppSingleton.getApp();
        PackageInfo pInfo = null;

        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String verName = pInfo.versionName;
            int verCode = pInfo.versionCode;

            app.appVersionCode = verCode;
            app.appVersionName = verName;

            String url = VDOConsulta.URL_BASE_WS + "obterinfoversaoapp?token=" + VDOConsulta.TOKEN + "&plataforma=android&nomeVersao=" + verName;

            new VDOConsulta(new VDOConsultaCallback() {
                @Override
                public void callback(String result) {
                    try {
                        if(result != null) {
                            JSONObject json = new JSONObject(result).getJSONArray("info").getJSONObject(0);
                            app.appVersionCheckJson = json;
                            createAndSaveFile("version.json", json.toString(), context);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(url);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void animarNovaPosicaoListView(int pos, ListView listView){
        int atual = listView.getFirstVisiblePosition();
        int duracao = Math.abs(atual - pos);
        duracao = (int) (duracao * 1000.0/ (1.5*duracao));
        listView.smoothScrollToPositionFromTop(pos, 0, duracao);
    }

    public static View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}