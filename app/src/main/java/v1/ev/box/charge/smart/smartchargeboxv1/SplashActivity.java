package v1.ev.box.charge.smart.smartchargeboxv1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import v1.ev.box.charge.smart.smartchargeboxv1.events.AuthTokenEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.intro.IntroActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.menu.MenuActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

public class SplashActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!isNetworkAvailable()) {
            noInternetDialog();
        }

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();

        final RelativeLayout background = (RelativeLayout) findViewById(R.id.background);

        ColorDrawable[] colors = {
                new ColorDrawable(Color.parseColor("#2196f3")),
                new ColorDrawable(Color.parseColor("#ffca28")),
                new ColorDrawable(Color.parseColor("#00e676")),
                new ColorDrawable(Color.parseColor("#40c4ff"))
        };
        TransitionDrawable trans = new TransitionDrawable(colors);
        //This will work also on old devices. The latest API says you have to use setBackground instead.
        background.setBackgroundDrawable(trans);
        trans.startTransition(5000);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder
                (GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile().requestId().requestIdToken("15889245939-n49utt0t9rsh46q1p33705itn2s5sd6r.apps.googleusercontent.com")
                .build();

       // if(!mGoogleApiClient.isConnected()) {
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (Exception e) {

        }

       // }

        if (AccessToken.getCurrentAccessToken() != null) {
            createJsonObject(1, AccessToken.getCurrentAccessToken().getToken());
            return;
        }

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            handleSignInResult(opr.get());
            return;
        }

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Uri photoUri = acct.getPhotoUrl();
            String uri = photoUri != null ? photoUri.toString() : null;
            createJsonObject(2, acct.getIdToken());
        } else {
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        }
    }

    private void createJsonObject(int connection, String token) {
        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
        intent.setAction(NetworkingService.AUTH_REQUEST);
        intent.putExtra("type", connection);
        intent.putExtra("token", token);
        startService(intent);
    }

    @Subscribe
    public void onAuthTokenArrived(AuthTokenEvent obj) {
        PreferencesManager.getInstance(getApplicationContext()).writeString(PreferencesManager.TOKEN, obj.getToken());
        Log.d("SDSGDUISDSD", "Pasibaige :("  + obj.getToken());
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void noInternetDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle("Jūs esate neprisijungęs prie interneto!");
        dialog.setMessage("Interneto ryšys programai yra būtinas");
        dialog.setNegativeButton("Uždaryti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mGoogleApiClient.stopAutoManage(this);
        mGoogleApiClient.disconnect();
    }
}