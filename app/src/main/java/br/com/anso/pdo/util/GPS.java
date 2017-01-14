package br.com.anso.pdo.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import br.com.anso.pdo.R;


public class GPS implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private Activity activity;
    private Usuario usuario = Usuario.getInstance();
    private AppSingleton appPresenter = AppSingleton.getApp();

    public GPS(Activity act) {
        prepararLocationService(act);
    }

    private void prepararLocationService(final Activity activity) {
        this.activity = activity;

        buildGoogleApiClient(activity);

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }


        final LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (!usuario.getIsManualPos()) {
                    usuario.setPosicao(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                if (checkLocationPermission(activity)) {
                    /*requisitaPermissao()*/
                    return;
                }

                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    usuario.setPosicao(new LatLng(location.getLatitude(), location.getLongitude()));
                    atualizaPosicaoUsuario(activity);
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                atualizaPosicaoUsuario(activity);
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //permissões Android 6
        if (checkLocationPermission(activity)) {
            requisitaPermissao();
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, activity.getResources().getString(R.string.cancel), Toast.LENGTH_SHORT).show();
                } else {
                    // permission denied.
                    Toast.makeText(activity, activity.getResources().getString(R.string.denied), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void recentralizarGPS(Activity activity) {
        usuario.setIsManualPos(false);
        atualizaPosicaoUsuario(activity);
    }

    private synchronized void buildGoogleApiClient(Activity activity) {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void verificaGPS() {
        LocationManager service = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            ExibirMensagemGPS();
        }
    }

    private void atualizaPosicaoUsuario(Activity activity) {
        if (checkLocationPermission(activity)) {
            //requisitaPermissao();
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(location!=null){
            usuario.setPosicao(new LatLng(location.getLatitude(), location.getLongitude()));//atualiza posicao global do usuario
            appPresenter.atualizaUsuarioViews(usuario.getPosicao());//atualiza todas as views
        }
    }

    private void requisitaPermissao() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    private  void ExibirMensagemGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setMessage(activity.getResources().getString(R.string.desable))
                .setCancelable(false)
                .setPositiveButton(activity.getResources().getString(R.string.ativar), new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activity.startActivity(intent);
                            }
                        }
                );
        final AlertDialog alert = builder.create();
        alert.show();
       /* alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#3b3f44"));*/
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3b3f44"));

    }

    public boolean checkLocationPermission(Activity activity){
        if(activity != null)
            return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        else
            return false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        //verificaGPS();
        atualizaPosicaoUsuario(this.activity);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
