package v1.ev.box.charge.smart.smartchargeboxv1.intro;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.menu.MenuActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener {
    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 456;
    private GoogleApiClient mGoogleApiClient;
    private boolean logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity());
        callbackManager = CallbackManager.Factory.create();

        Bundle bundle = getArguments();
        if(bundle != null) {
            logout = bundle.getBoolean("logout", false);
            if (logout) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                }
            }
        }

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        LinearLayout fbLogin = (LinearLayout) view.findViewById(R.id.facebook_login_button);
        fbLogin.setOnClickListener(this);

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        try {
                            String userId = user.getString("id");
                            String name = user.getString("name");
                            String email = user.getString("email");
                            String photoPath = "https://graph.facebook.com/" + userId + "/picture?type=large";
                            saveProfile(1, photoPath, name, email, userId);
                            serverValidation(1, loginResult.getAccessToken().getToken());
                        } catch (JSONException e) {
                            Log.d("addfdsfdsf", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                AccessToken.setCurrentAccessToken(null);
            }

            @Override
            public void onError(FacebookException exception) {
                AccessToken.setCurrentAccessToken(null);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder
                (GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile().requestId().requestIdToken("15889245939-n49utt0t9rsh46q1p33705itn2s5sd6r.apps.googleusercontent.com")
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        LinearLayout signInButton = (LinearLayout) view.findViewById(R.id.google_login_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(!logout) {
            if (AccessToken.getCurrentAccessToken() != null) {
                serverValidation(1, AccessToken.getCurrentAccessToken().getToken());

                return;
            }

            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                // If the user has not previously signed in on this device or the sign-in has expired,
                // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                // single sign-on will occur in this branch.
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        } else {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Uri photoUri = acct.getPhotoUrl();
            String uri = photoUri != null ? photoUri.toString() : null;
            saveProfile(2, uri, acct.getDisplayName(), acct.getEmail(), acct.getId());

            serverValidation(2, acct.getIdToken());
        }

        Log.d("SDSGDLUSD", "sdfsdfdsf " + result.isSuccess());
    }

    private void serverValidation(int connection, String token) {
        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
        intent.setAction(NetworkingService.AUTH_REQUEST);
        intent.putExtra("type", connection);
        intent.putExtra("token", token);
        getActivity().startService(intent);
    }

    private void saveProfile(int loginType, final String url, String name, String email, String userId) {
        PreferencesManager.getInstance(getContext()).writeString(PreferencesManager.USER_NAME, name);
        PreferencesManager.getInstance(getContext()).writeString(PreferencesManager.USER_EMAIL, email);
        PreferencesManager.getInstance(getContext()).writeString(PreferencesManager.USER_ID, userId);
        PreferencesManager.getInstance(getContext()).writeLoginType(loginType);

        Log.d("SDSHIODSDSD", userId  +"");


        if(url != null) {
            PreferencesManager.getInstance(getContext()).writeString(PreferencesManager.IMG_URL, url);
//            Glide.with(this).load(url).downloadOnly(new SimpleTarget<File>() {
//                @Override
//                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
//                    PreferencesManager.getInstance(getContext()).writeString(PreferencesManager.IMG_URL, url);
//                }
//
//                @Override
//                public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                    super.onLoadFailed(e, errorDrawable);
//                }
//            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(logout) {
            Log.d("SDSGUODISDSD", "google sign out");
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
