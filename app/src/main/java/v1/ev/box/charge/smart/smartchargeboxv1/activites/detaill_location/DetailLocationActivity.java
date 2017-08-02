package v1.ev.box.charge.smart.smartchargeboxv1.activites.detaill_location;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import v1.ev.box.charge.smart.smartchargeboxv1.LogoutActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.custom.WriteCommentDialog;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.ImagesDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.DetailLocationEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.FragmentDataToActivityEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.intro.IntroActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.DetailBottomSheetDataParser;
import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

public class DetailLocationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private int locationId;
    private CollapsingToolbarLayout collapsingToolbar;
    private TabPagerAdapter tabPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private CarouselView imageSlider;
    public ArrayList<byte[]> imageUrls = new ArrayList<>();
    private String address;
    private FloatingActionMenu menuRed;
    private String telephone;
    private static final int PHONE_PERMISSION = 6;
    private int isFav = 0;
    public FloatingActionButton addToFavBtn;
    public FloatingActionButton takePhoto;

    private static final int CAMERA_CODE = 4;
    private Uri imageUri;
    private boolean empty = false;

    private double lat;
    private double lng;

    private ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Glide.with(getApplicationContext()).load(imageUrls.get(position)).centerCrop().into(imageView);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_location);

        locationId = getIntent().getIntExtra("locationId", -1);
        if(savedInstanceState != null) {
            imageUri = savedInstanceState.getParcelable("imageCaptureUri");
        }
        if(locationId == -1) {
            if(PreferencesManager.getInstance(getApplicationContext()).getPrefValue(PreferencesManager.TOKEN) != null &&
                    PreferencesManager.getInstance(getApplicationContext()).getPrefValue(PreferencesManager.TOKEN).length() > 1) {
                handleIntent(getIntent());
            } else {
                Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                startActivity(intent);
                return;
            }

        }
        address = getIntent().getStringExtra("address");

        imageSlider = (CarouselView) findViewById(R.id.carouselView);

        setToolbar(address);
        setImage();

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.detail_tabs);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(tabPagerAdapter);
        mTabLayout.setTabsFromPagerAdapter(tabPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        menuRed = (FloatingActionMenu) findViewById(R.id.menu_red);
        menuRed.setMenuButtonColorNormal(Color.parseColor("#008cff"));
        addToFavBtn = new FloatingActionButton(this);
        addToFavBtn.setButtonSize(FloatingActionButton.SIZE_MINI);
        addToFavBtn.setColorNormal(Color.WHITE);
        addToFavBtn.setLabelText("Pažymėti");
        addToFavBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        menuRed.addMenuButton(addToFavBtn);
        addToFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavBtn.setLabelTextColor(Color.WHITE);
                if (isFav == 1) {
                    isFav = 0;
                    Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
                    intent.setAction(NetworkingService.REMOVE_FAV_LOCATION);
                    intent.putExtra("locationId", String.valueOf(locationId));
                    startService(intent);
                    addToFavBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                } else {
                    isFav = 1;
                    Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
                    intent.setAction(NetworkingService.INSERT_FAV_LOCATION);
                    intent.putExtra("locationId", String.valueOf(locationId));
                    startService(intent);
                    addToFavBtn.setImageResource(R.drawable.ic_favorite_black_24dp);
                }
            }
        });

        final FloatingActionButton programFab4 = new FloatingActionButton(this);
        programFab4.setButtonSize(FloatingActionButton.SIZE_MINI);
        programFab4.setColorNormal(Color.WHITE);
        programFab4.setLabelText("Rašyti komentarą");
        programFab4.setImageResource(R.drawable.ic_comment_black_24dp);
        menuRed.addMenuButton(programFab4);
        programFab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteCommentDialog dialog = WriteCommentDialog.newInstance();
                dialog.setLocationId(String.valueOf(locationId));
                dialog.show(getFragmentManager(), "Comment");
            }
        });

        takePhoto = new FloatingActionButton(this);
        takePhoto.setButtonSize(FloatingActionButton.SIZE_MINI);
        takePhoto.setColorNormal(Color.WHITE);
        takePhoto.setLabelText("Fotografuoti");
        takePhoto.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
        menuRed.addMenuButton(takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUrls.size() < 5) {
                    takePhoto();
                } else {
                    takePhoto.setColorNormal(Color.GRAY);
                    Toast.makeText(DetailLocationActivity.this, "Lokacijai leidžiama įkkelti max 5 nuotraukų!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if (PreferencesManager.getInstance(getApplicationContext()).getLoginType() == 1) {
            final FloatingActionButton programFab5 = new FloatingActionButton(this);
            programFab5.setButtonSize(FloatingActionButton.SIZE_MINI);
            programFab5.setColorNormal(Color.parseColor("#003791"));
            programFab5.setLabelText("Dalintis");
            programFab5.setImageResource(R.drawable.com_facebook_button_icon);
            menuRed.addMenuButton(programFab5);
            programFab5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    programFab5.setLabelTextColor(Color.WHITE);
                }
            });

            CallbackManager callbackManager = CallbackManager.Factory.create();
            final ShareDialog shareDialog = new ShareDialog(this);
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {

                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });

            programFab5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                                .setContentTitle("Aš kranuosi!")
//                                .setQuote("Laba diena ir viso geri")
//                                .setImageUrl(Uri.parse("http://assets.inhabitat.com/wp-content/blogs.dir/1/files/2011/05/EV-charging-station-1.jpg"))
//                                .setContentUrl(Uri.parse("www.google.lt"))
//                                .build();

                                .setContentTitle("SmartChargeBox")
                                .setQuote("Stotelė " + address)
                                .setContentUrl(Uri.parse("http://maps.google.com/maps?&z=10&q=" + lat + "+" + lng + "&ll=" + lat + "+" + lng)).build();

                        shareDialog.show(linkContent);
                    }
                }
            });
        }

        menuRed.setClosedOnTouchOutside(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void takePhoto() {
        boolean isPermissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ;

        if(!isPermissionGranted) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, Long.toString(System.currentTimeMillis()));
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent cameraIntent = new  Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, CAMERA_CODE);
            Log.d("DGSUIODSDD", "asdsdfds");
        }
    }

    private void setToolbar(String address) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setTitle(address);
        }
    }

    private void setImage() {
        imageSlider.setPageCount(imageUrls.size());
        imageSlider.setSlideInterval(4000);
        imageSlider.setImageListener(imageListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private class TabPagerAdapter extends FragmentStatePagerAdapter {

        private TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = position == 0 ? new StationsFragment() : new CommentsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("locationId", locationId);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0 ? "Krovimosi taškai" : "Komentarai";
        }
    }

    @Subscribe
    public void onBottomSheetDataArrived(DetailLocationEvent obj) {
        new DetailBottomSheetDataParser().execute(obj.data);
    }

    @Subscribe
    public void onFragmentDataArrived(final FragmentDataToActivityEvent obj) {
        if (menuRed != null && obj.getPhone() != null && obj.getPhone().length() > 0) {
            final FloatingActionButton programFab3 = new FloatingActionButton(this);
            programFab3.setButtonSize(FloatingActionButton.SIZE_MINI);
            programFab3.setColorNormal(Color.WHITE);
            programFab3.setLabelText("Sambinti");
            programFab3.setImageResource(R.drawable.ic_call_black_24dp);
            menuRed.addMenuButton(programFab3);
            programFab3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    programFab3.setLabelTextColor(Color.WHITE);
                    telephone = obj.getPhone();
                    makeCall(obj.getPhone());
                }
            });
        }

        if (menuRed != null) {
            isFav = obj.getIsFav();
            if (isFav == 1 && addToFavBtn != null) {
                addToFavBtn.setImageResource(R.drawable.ic_favorite_black_24dp);
            }
        }

        lat = obj.getLat();
        lng = obj.getLng();

        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
        intent.setAction(NetworkingService.GET_LOCATION_IMAGES);
        intent.putExtra("locationId", String.valueOf(locationId));
        startService(intent);
    }

    @Subscribe
    public void onImageDataDownloaded(ImagesDataModel obj) {
        if(obj.getImgs() != null) {
            for (String base64 : obj.getImgs()) {
                byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                imageUrls.add(decodedString);
            }
        }
        if(obj.getImgs() == null || obj.getImgs().size() == 0) {
            empty = true;
            takePhoto.setColorNormal(Color.WHITE);
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.station_no_img);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();
            imageUrls.add(bitmapdata);
        } else {
            empty = false;
            if(imageUrls.size() < 5) {
                takePhoto.setColorNormal(Color.WHITE);
            } else {
                takePhoto.setColorNormal(Color.GRAY);
            }
        }

        setImage();
    }

    public boolean makeCall(String num) {
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PHONE_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + num));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PHONE_PERMISSION:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    makeCall(telephone);
                } else {
                    Toast.makeText(getApplicationContext(), "Skambinimui reikalingi leidimai", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d("SDILSDDSD", "onNewIntent");
        if (intent != null) {
            /////handleIntent(intent);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("SDGUIDSSDSD", " " + (requestCode == CAMERA_CODE && resultCode == RESULT_OK && imageUri != null) + " " + imageUri);
        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK && imageUri != null) {
            Bitmap thumbnail = null;
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                //byte[] byteArray = stream.toByteArray();
               // Base64.encodeToString(byteArray, Base64.DEFAULT);


                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file = new File(path, "/" + System.currentTimeMillis());
                try(OutputStream outputStream = new FileOutputStream(file)) {
                    stream.writeTo(outputStream);
                }

                File compressedImage = new Compressor.Builder(this)
                        .setMaxWidth(480)
                        .setMaxHeight(360)
                        .setQuality(80)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .build()
                        .compressToFile(file);

                FileInputStream fileInputStream = new FileInputStream(compressedImage);
                int byteLength = (int) compressedImage.length();
                byte[] filecontent = new byte[byteLength];
                fileInputStream.read(filecontent, 0, byteLength);
                String base64Str = Base64.encodeToString(filecontent, Base64.DEFAULT);
                Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
                intent.setAction(NetworkingService.SEND_IMAGE_TO_SERVER);
                intent.putExtra("locationId", String.valueOf(locationId));
                intent.putExtra("img", base64Str);
                startService(intent);

                if(imageUrls.size() == 1 && empty) {
                    imageUrls.remove(0);
                    imageUrls.add(filecontent);
                    setImage();
                    empty = false;
                }

                compressedImage.delete();
                file.delete();
            } catch (IOException e) {
                Log.d("SDSDSDUSD", e.getMessage());
                e.printStackTrace();
            }
            //appCompatImageView.setImageBitmap(thumbnail);
        }
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            new NdefReaderTask().execute(tag);
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("imageCaptureUri", imageUri);
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_URI)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {

                    }
                }
            }
            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            byte[] payload = record.getPayload();
            return new String(payload, "UTF-8");
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Pattern pattern = Pattern.compile("id=(\\d+)");
                Matcher matcher = pattern.matcher(result);
                if(matcher.find()) {
                    locationId = Integer.parseInt(matcher.group(1));
//                    Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
//                    intent.setAction(NetworkingService.GET_LOCATION_IMAGES);
//                    intent.putExtra("locationId", String.valueOf(locationId));
//                    startService(intent);
                }
            }
        }
    }
}
