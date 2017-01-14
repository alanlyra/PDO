package br.com.anso.pdo.selecionarEndereco;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.anso.pdo.R;
import br.com.anso.pdo.util.VDOConsulta;
import br.com.anso.pdo.util.VDOConsultaCallback;

public class SuggestionAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private List<String> resultList = new ArrayList<>();

    public SuggestionAdapter(Activity context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        if(resultList!=null && index>=0 && index < resultList.size())
            return String.valueOf(resultList.get(index));
        else
            return "";
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_autocomplete, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.lbl_origem)).setText(getItem(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                resultList.clear();
                if (constraint != null && constraint.length()>2) {
                    carregaAutocompleteEndereco(constraint.toString(), ((SelecionarEnderecoActivity) mContext).getMunicipioSelecionado());
                }
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
            }};
    }

    public void carregaAutocompleteEndereco(String texto, String municipio) {
        String url = VDOConsulta.URL_BASE_WS + "enderecoautocomplete?token="+ VDOConsulta.TOKEN +"&consulta=" + texto.trim().replace(" ","+") + "&municipio=" + municipio.trim().replaceAll(" ", "+");

        new VDOConsulta(new VDOConsultaCallback() {

            @Override
            public void callback(String result) {
                resultList.clear();
                carregarEnderecos(result);
            }

            private void carregarEnderecos(String result) {
                JSONObject linhas = null;
                try {
                    linhas = new JSONObject(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONArray listaJson;

                if(linhas != null){
                    try {
                        listaJson = linhas.getJSONArray("sugestoes");
                    } catch (JSONException e) {
                        listaJson = new JSONArray();
                        e.printStackTrace();
                    }
                }
                else
                    listaJson = new JSONArray();

                for (int i = 0; i < listaJson.length(); i++){
                    try {
                        JSONObject l = listaJson.getJSONObject(i);
                        resultList.add(l.optString("endereco"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                notifyDataSetChanged();
            }
        }).execute(url);

    }

    public List<String> getResultList(){
        return this.resultList;
    }
}