package v1.ev.box.charge.smart.smartchargeboxv1.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.ResultListener;
import im.delight.android.ddp.SubscribeListener;
import v1.ev.box.charge.smart.smartchargeboxv1.events.AuthorizationErrorEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationListCancelCharAndRes;
import v1.ev.box.charge.smart.smartchargeboxv1.events.charging_sessions.ChargingSessionDataAdded;
import v1.ev.box.charge.smart.smartchargeboxv1.events.charging_sessions.ChargingSessionDataRemoved;
import v1.ev.box.charge.smart.smartchargeboxv1.events.DataAdded;
import v1.ev.box.charge.smart.smartchargeboxv1.events.DataChanged;
import v1.ev.box.charge.smart.smartchargeboxv1.events.DataRemoved;
import v1.ev.box.charge.smart.smartchargeboxv1.events.DetailLocationEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.LocationAddressEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.NewCommentCreatedEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.PointersDownlaodEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationRemovedEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.ChargeInitParser;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.ChargingStatisticsParser;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.CommentRemoveAsyncTask;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.CommentsParser;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.CurrentChargingSessionParser;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.EditedCommentParser;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.ImagesParser;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.MyReservationsParser;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.NearestLocationsParser;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.OneCommentParser;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.QueryResultParser;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.ReservationResponseParser;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.StationTimesParser;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.TokenParser;
import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;

public class NetworkingService extends Service implements MeteorCallback {

    private Meteor mMeteor;
    private String timeSubId = null;
    private String sessionSubId = null;

    public static final String GET_MARKERS = "GET_MARKERS";
    public static final String RESERVE_STATION = "RESERVE_STATION";
    public static final String GET_DETAIL_LOCATION_INFO = "GET_DETAIL_LOCATION_INFO";
    public static final String GET_STATION_RESERVATION_TIMES = "GET_STATION_RESERVATION_TIMES";
    public static final String GET_LOCATION_ADDRESS = "GET_LOCATION_ADDRESS";
    public static final String SEARCH_BAR_QUERY = "SEARCH_BAR_QUERY";
    public static final String REMOVE_STATION_RESERVATION = "REMOVE_STATION_RESERVATION";
    public static final String AUTH_REQUEST = "AUTH_REQUEST";
    public static final String NEAREST_LOCATIONS = "NEAREST_LOCATIONS";
    public static final String FAV_STATIONS = "FAV_STATIONS";
    public static final String GET_RESERVATIONS = "GET_RESERVATIONS";
    public static final String SUB_STATION_TIME = "SUB_STATION_TIME";
    public static final String UNSUB_STATION_TIME = "UNSUB_STATION_TIME";
    public static final String SEND_COMMENT = "SEND_COMMENT";
    public static final String READ_COMMENTS = "READ_COMMENTS";
    public static final String SUB_CHARGING_SESSION = "SUB_CHARGING_SESSION";
    public static final String INIT_CHARGING = "INIT_CHARGING";
    public static final String CANCEL_CHARGING = "CANCEL_CHARGING";
    public static final String CURRENT_CHARGING_SESSION = "CURRENT_CHARGING_SESSION";
    public static final String INSERT_FAV_LOCATION = "INSERT_FAV_LOCATION";
    public static final String REMOVE_FAV_LOCATION = "REMOVE_FAV_LOCATION";
    public static final String GET_STATS_DATA = "GET_STATS_DATA";
    public static final String SEND_RATING = "SEND_RATING";
    public static final String REMOVE_COMMENT = "REMOVE_COMMENT";
    public static final String EDIT_COMMENT = "EDIT_COMMENT";
    public static final String SEND_IMAGE_TO_SERVER = "SEND_IMAGE_TO_SERVER";
    public static final String GET_LOCATION_IMAGES = "GET_LOCATION_IMAGES";

    public NetworkingService() {

    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        if (intent != null && intent.getAction() != null && mMeteor != null) {
            final String token = PreferencesManager.getInstance(getApplicationContext()).getPrefValue(PreferencesManager.TOKEN);
            switch (intent.getAction()) {
                case GET_MARKERS:
                    double leftLongitude = intent.getDoubleExtra("leftLongitude", -1);
                    double leftLatitude = intent.getDoubleExtra("leftLatitude", -1);
                    double rightLongitude = intent.getDoubleExtra("rightLongitude", -1);
                    double rightLatitude = intent.getDoubleExtra("rightLatitude", -1);
                    if(leftLatitude != -1 && leftLongitude != -1
                            && rightLongitude != -1 && rightLatitude != -1) {
                        mMeteor.call("getLocationsByType", new Object[]{token, "border", leftLongitude,
                                leftLatitude,
                                rightLongitude,
                                rightLatitude}, new ResultListener() {
                            @Override
                            public void onSuccess(String result) {
                                EventBus.getDefault().post(new PointersDownlaodEvent(result));
                            }

                            @Override
                            public void onError(String error, String reason, String details) {
                                Log.d("SDSDYIOSDSD", error + " " + reason + " " + details);
                                if("401".equals(error)) {
                                    EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                                }
                            }
                        });
                    }
                    break;
                case RESERVE_STATION:
                    final String reserveStr = intent.getStringExtra("reserveStr");
                    Log.d("DSGUDISDS", reserveStr);
                    mMeteor.call("reserveStation", new Object[]{token, reserveStr}, new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            if(result != null) {
                                new ReservationResponseParser().execute(result);
                            }
                        }

                        @Override
                        public void onError(String error, String reason, String details) {
                            if("401".equals(error)) {
                                EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                            }
                            else if ("409".equals(error)) {
                                if(reason != null) {
                                    Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Kitas vartotojas jau rezervavo šį laiką!", Toast.LENGTH_SHORT).show();
                                }
                                
                            }
                        }
                    });
                    break;
                case GET_DETAIL_LOCATION_INFO:
                    int locationId = intent.getIntExtra("locationId", -1);
                    Log.d("SDHDPSD", locationId + "");
                    mMeteor.call("getLocationDetailInfo", new Object[]{token, locationId}, new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d("DSGUIDSD", result + "");
                            EventBus.getDefault().post(new DetailLocationEvent(result));
                        }

                        @Override
                        public void onError(String error, String reason, String details) {
                            if("401".equals(error)) {
                                EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                            }
                        }
                    });
                    break;
                case GET_STATION_RESERVATION_TIMES:
                    final String stationId = intent.getStringExtra("stationId");
                    mMeteor.call("getStationReservationTimes", new Object[]{token, stationId}, new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d("SDDGSYUDSDSDSDSDS", result);
                            new StationTimesParser(stationId).execute(result);
                        }

                        @Override
                        public void onError(String error, String reason, String details) {
                            if("401".equals(error)) {
                                EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                            }
                        }
                    });
                    break;
                case GET_LOCATION_ADDRESS:
                    int locationAddressId = intent.getIntExtra("locationId", -1);
                    Log.d("SDSDIOSDSD", locationAddressId + "");
                    mMeteor.call("getLocationAddress", new Object[]{token, locationAddressId}, new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            EventBus.getDefault().post(new LocationAddressEvent(result));
                        }

                        @Override
                        public void onError(String error, String reason, String details) {
                            if("401".equals(error)) {
                                EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                            }
                        }
                    });
                    break;
                case SEARCH_BAR_QUERY:
                    String query = intent.getStringExtra("query");
                    Log.d("SDHIOSDsdfsdf", query + "");
                    mMeteor.call("getLocationsBySearchQuery", new Object[]{token, query}, new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            new QueryResultParser().execute(result);
                        }

                        @Override
                        public void onError(String error, String reason, String details) {
                            Log.d("SDGISDSD", "fsdfsdfsdffsdf " + error );
                            if("401".equals(error)) {
                                EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                            }
                        }
                    });
                    break;
                case REMOVE_STATION_RESERVATION:
                    final String reservationId = intent.getStringExtra("reservationId");
                    String stationIdToRemove = intent.getStringExtra("stationId");
                    Log.d("XSDGUSIODSD", stationIdToRemove + " " + reservationId);
                    mMeteor.call("removeStationReservation", new Object[]{token, reservationId, stationIdToRemove}, new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            EventBus.getDefault().post(new ReservationRemovedEvent(result, reservationId));
                        }
                        @Override
                        public void onError(String error, String reason, String details) {
                            Log.d("SDHSIDSDSD", error + " " +reason + " " + details);
                            if("401".equals(error)) {
                                EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                            }
                        }
                    });
                    break;
                case AUTH_REQUEST:
                    int type = intent.getIntExtra("type", -1);
                    String localToken = intent.getStringExtra("token");

                    Log.d("KREIPIASIITOKENA", type + " " + localToken);
                    if (localToken != null) {
                        mMeteor.call("authentificateUser", new Object[]{type, localToken}, new ResultListener() {
                            @Override
                            public void onSuccess(String result) {
                                Log.d("KREIPIASIITOKENA", "onSuccess " + result);
                                new TokenParser().execute(result);
                            }
                            @Override
                            public void onError(String error, String reason, String details) {
                                Log.d("KREIPIASIITOKENA", "err " +  error + " " + reason + " " + details);
                                Log.d("SDSGDsdfsdUISD", "err " +  error + " " + reason + " " + details);
                            }
                        });
                    }
                    break;
                case NEAREST_LOCATIONS:
                    double nearestLat = intent.getDoubleExtra("lat", -1);
                    double nearestLng = intent.getDoubleExtra("lng", -1);
                    int skip = intent.getIntExtra("skip", -1);
                    if (nearestLat != -1 && nearestLng != -1 && skip != -1) {
                        mMeteor.call("getNearestLocations", new Object[]{token, nearestLng, nearestLat, skip}, new ResultListener() {
                            @Override
                            public void onSuccess(String result) {
                                Log.d("SDGSDUISDSD", result);
                                new NearestLocationsParser().execute(result);
                            }
                            @Override
                            public void onError(String error, String reason, String details) {
                                if("401".equals(error)) {
                                    EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                                }
                            }
                        });
                    }
                    break;
                case FAV_STATIONS:
                    mMeteor.call("getFavLocationsInfo", new Object[]{token}, new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            new NearestLocationsParser().execute(result);
                        }
                        @Override
                        public void onError(String error, String reason, String details) {
                            Log.d("SDGSDUIdfgfdgSDSD", error + " " + reason);
                            if("401".equals(error)) {
                                EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                            }
                        }
                    });
                    break;
                case GET_RESERVATIONS:
                    long time = intent.getLongExtra("time", -1);
                    mMeteor.call("getReservations", new Object[]{token, time}, new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d("SDGSDUIdfgsdfdgSDSD", result + "");
                            //new NearestLocationsParser().execute(result);
                            new MyReservationsParser().execute(result);
                        }

                        @Override
                        public void onError(String error, String reason, String details) {
                            Log.d("SDGSDUIdfgsdfdgSDSD", "KLAIDA!!!! " + error + " " + reason + " " + details);
                            if ("401".equals(error)) {
                                EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                            }
                        }
                    });
                    break;
                case SUB_STATION_TIME:
                    String subStationId = intent.getStringExtra("stationId");
                    if(subStationId != null ) {
                        timeSubId = mMeteor.subscribe("stationTime", new Object[]{subStationId});
                    }
                    break;
                case UNSUB_STATION_TIME:
                    if(timeSubId != null) {
                        mMeteor.unsubscribe(timeSubId);
                    }
                    break;
                case SEND_COMMENT:
                    final String userImg = PreferencesManager.getInstance(getApplicationContext()).getPrefValue(PreferencesManager.IMG_URL);
                    final String userName = PreferencesManager.getInstance(getApplicationContext()).getPrefValue(PreferencesManager.USER_NAME);
                    final String comment = intent.getStringExtra("comment");
                    final String commentedLocationId = intent.getStringExtra("locationId");

                    if(comment != null) {
                        mMeteor.call("sendComment", new Object[]{token, userImg, userName, comment, commentedLocationId}, new ResultListener() {
                            @Override
                            public void onSuccess(String result) {
                                new OneCommentParser().execute(result);
                                //EventBus.getDefault().post(new NewCommentCreatedEvent(userImg, userName, comment, Calendar.getInstance().getTimeInMillis()));
                            }

                            @Override
                            public void onError(String error, String reason, String details) {
                                Log.d("SDGSDUIdfgsdfdgSDSD", "KLAIDA!!!! " + error + " " + reason + " " + details);
                                if ("401".equals(error)) {
                                    EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                                }
                            }
                        });
                    }
                    break;
                case READ_COMMENTS:
                    String locationToReadId = intent.getStringExtra("locationId");
                    int commentsSkip = intent.getIntExtra("skip", -1);

                    Log.d("SDSDSGIDSd", locationToReadId + " " + commentsSkip);
                    if(commentsSkip != -1) {
                        mMeteor.call("readComments", new Object[]{token, locationToReadId, commentsSkip}, new ResultListener() {
                            @Override
                            public void onSuccess(String result) {
                                Log.d("SDGDUIOSD", result);
                                new CommentsParser().execute(result);
                            }

                            @Override
                            public void onError(String error, String reason, String details) {
                                Log.d("SDFYSDUISDSD", error + " " +reason + " " + details);
                                if ("401".equals(error)) {
                                    EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                                }
                            }
                        });
                    }
                    break;
                case SUB_CHARGING_SESSION:
                    Log.d("RAUDONISERMUKSIA", SUB_CHARGING_SESSION);
                    String subSessionStationId = intent.getStringExtra("stationId");
                    String labas = intent.getStringExtra("labas");
                    if(subSessionStationId != null && sessionSubId == null) {
                        sessionSubId = mMeteor.subscribe("chargeSessionSub", new Object[]{subSessionStationId});
                        Log.d("SDSGDSDD", "SEKMINGAI SUBSICI " + sessionSubId + " " + subSessionStationId);
                        Log.d("RADASSSDSDSD", "SUB");
                    } else if(sessionSubId != null) {
                        mMeteor.unsubscribe(sessionSubId);
                        sessionSubId = null;
                        Log.d("RADASSSDSDSD", "UNSUB");
                    }

                    Log.d("SDSGUIDSD", labas + "");

                    break;
                case INIT_CHARGING:
                    String chargeStationId = intent.getStringExtra("stationId");
                    long startTime = intent.getLongExtra("startTime", -1);
                    long endTime = intent.getLongExtra("endTime", -1);
                    String resId = intent.getStringExtra("reservationId");
                    Log.d("SDIUDSDSD", chargeStationId + " " + startTime + " " +endTime + " " + token);
                    if (chargeStationId != null && startTime != -1 && endTime != -1 && resId != null) {
                        mMeteor.call("initCharging", new Object[]{token, chargeStationId, startTime, endTime, resId}, new ResultListener() {
                            @Override
                            public void onSuccess(String result) {
                                Log.d("SDSDUIDSD", result);
                                new ChargeInitParser().execute(result);
                            }

                            @Override
                            public void onError(String error, String reason, String details) {
                                Log.d("SDFYSDUISDSD", error + " " +reason + " " + details);
                                if ("401".equals(error)) {
                                    EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                                }
                            }
                        });
                    }
                    break;
                case CANCEL_CHARGING:
                    long time_charged = intent.getLongExtra("time_charged", 0);
                    final String reservationIdCancel = intent.getStringExtra("reservationId");
                    final int adapterPos = intent.getIntExtra("adapterPosition", -1);
                    Log.d("SDSGDISODS", time_charged + "");
                    if(reservationIdCancel != null) {
                        mMeteor.call("cancelCharging", new Object[]{token, time_charged, reservationIdCancel}, new ResultListener() {
                            @Override
                            public void onSuccess(String result) {
                                Log.d("SDSDsdsdfsffsdfUIDSD", result);
                                EventBus.getDefault().post(new ReservationListCancelCharAndRes(adapterPos));
                                //EventBus.getDefault().post(new ChargingSessionEvent(result));
                            }

                            @Override
                            public void onError(String error, String reason, String details) {
                                Log.d("SDSDsdfsdfUIDSD", error + " " +reason + " " + details);
                                if ("401".equals(error)) {
                                    EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                                }
                            }
                        });
                    }
                    break;
                case CURRENT_CHARGING_SESSION:
                    final boolean fromMenu = intent.getBooleanExtra("fromMenu", false);
                    mMeteor.call("chargingSessionsTracker", new Object[]{token}, new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d("SDSDsdfsdfUIDSD", result);
                            new CurrentChargingSessionParser(fromMenu).execute(result);
                            //EventBus.getDefault().post(new ChargingSessionEvent(result));
                        }

                        @Override
                        public void onError(String error, String reason, String details) {
                            Log.d("SDSDsdfsdfUIDSD", error + " " +reason + " " + details);
                            if ("401".equals(error)) {
                                EventBus.getDefault().post(new AuthorizationErrorEvent(reason));
                            }
                        }
                    });
                    break;
                case INSERT_FAV_LOCATION:
                    String locationIdToInsert = intent.getStringExtra("locationId");
                    if(locationIdToInsert != null) {
                        mMeteor.call("insertToUserFavLocationsTable", new Object[]{token, locationIdToInsert}, new ResultListener() {
                            @Override
                            public void onSuccess(String result) {
                                Log.d("SDSGUIDSD", result + "");
                            }

                            @Override
                            public void onError(String error, String reason, String details) {
                                Log.d("SDSGUIDSD", error + "");
                            }
                        });
                    }
                    break;
                case REMOVE_FAV_LOCATION:
                    String locationToRemove = intent.getStringExtra("locationId");
                    if(locationToRemove != null) {
                        mMeteor.call("removeFromUserFavLocationsTable", new Object[]{token, locationToRemove}, new ResultListener() {
                            @Override
                            public void onSuccess(String result) {
                                Log.d("SDSGUsdfIDSD", result + "");
                            }

                            @Override
                            public void onError(String error, String reason, String details) {
                                Log.d("SDSGUIDSD", error + "");
                            }
                        });
                    }
                    break;
                case GET_STATS_DATA:
                    mMeteor.call("getStats", new Object[]{token}, new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d("SDSGUdfgdfgfdgsdfIDSD", result + "");
                            new ChargingStatisticsParser().execute(result);
                        }

                        @Override
                        public void onError(String error, String reason, String details) {
                            Log.d("SDSGUdfgdfgfdgsdfIDSD", error + "");
                        }
                    });
                    break;
                case SEND_RATING:
                    float rating = intent.getFloatExtra("rating", -1);
                    String ratedLocationId = intent.getStringExtra("locationId");
                    if(rating > -1) {
                        mMeteor.call("rateLocation", new Object[]{token, rating, ratedLocationId}, new ResultListener() {
                            @Override
                            public void onSuccess(String result) {
                                Log.d("dghuiddfg", result + "");
                            }

                            @Override
                            public void onError(String error, String reason, String details) {
                                Log.d("dghuiddfg", error + "");
                            }
                        });
                    }
                    break;
                case REMOVE_COMMENT:
                    String removeCommentId = intent.getStringExtra("commentId");
                    if(removeCommentId != null) {
                        mMeteor.call("removeComment", new Object[]{token, removeCommentId}, new ResultListener() {
                            @Override
                            public void onSuccess(String result) {
                                new CommentRemoveAsyncTask().execute(result);
                            }

                            @Override
                            public void onError(String error, String reason, String details) {
                                Log.d("dghsdfuiddfg", error + "");
                            }
                        });
                    }
                    break;
                case EDIT_COMMENT:
                    String editCommentId = intent.getStringExtra("commentId");
                    String editText = intent.getStringExtra("editText");
                    if(editCommentId != null && editText != null) {
                        mMeteor.call("editComment", new Object[]{token, editCommentId, editText}, new ResultListener() {
                            @Override
                            public void onSuccess(String result) {
                                new EditedCommentParser().execute(result);
                            }

                            @Override
                            public void onError(String error, String reason, String details) {
                                Log.d("SDSDFYUOSDSD", error);
                            }
                        });
                    }
                    break;
                case SEND_IMAGE_TO_SERVER:
                    String imgLocationId = intent.getStringExtra("locationId");
                    String img = intent.getStringExtra("img");
                    Log.d("SDSTDYISDFSODSIDSD", imgLocationId + " " + img);
                    if(imgLocationId != null && img != null) {
                        mMeteor.call("uploadImage", new Object[]{token, imgLocationId, img}, new ResultListener() {
                            @Override
                            public void onSuccess(String result) {
                                Log.d("SDSGDUPSDSDSD", result + "");
                                int ret = Integer.parseInt(result);
                                if(ret >= 0 || ret >= 5) {
                                    Toast.makeText(getApplicationContext(), "Nuotrauka patalpinta", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Lokacija turi per daug nuotraukų! max 5", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onError(String error, String reason, String details) {
                                Log.d("SDSGDUPSDSDSD", error + " " + reason + " " + details);
                                Toast.makeText(getApplicationContext(), "Klaida", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case GET_LOCATION_IMAGES:
                    String getImgOfLocation = intent.getStringExtra("locationId");
                    if(getImgOfLocation != null) {
                        mMeteor.call("getLocationImgs", new Object[]{token, getImgOfLocation}, new ResultListener() {
                            @Override
                            public void onSuccess(String result) {
                                Log.d("SDSGUsdfsdfDISD", result + "");
                                new ImagesParser().execute(result);
                            }

                            @Override
                            public void onError(String error, String reason, String details) {

                            }
                        });
                    }
                default:
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Meteor.setLoggingEnabled(true);

        //mMeteor = new Meteor(getApplicationContext(), "http://576100.s.dedikuoti.lt:3000/websocket");
        mMeteor = new Meteor(getApplicationContext(), "http://192.168.137.1:3000/websocket");
        mMeteor.addCallback(this);
        mMeteor.connect();
    }

    @Override
    public void onConnect(boolean signedInAutomatically) {
        //mMeteor.subscribe("test1", new Object[]{"b", getISO8601StringForDate()});
        //boundsSubId = mMeteor.subscribe("userLocations", new Object[]{"b"});
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onException(Exception e) {
        Log.d("DSHSDSDD", "fgufdfdfgdfgdfgfggdfg " + e.getMessage());
    }

    @Override
    public void onDataAdded(String collectionName, String documentID, String newValuesJson) {
        Log.d("SDFSYUDSDDSD", "onDataAdded " + collectionName + " " + documentID + " " + newValuesJson);

        if("reservation".equals(collectionName)) {
            EventBus.getDefault().post(new DataAdded(collectionName, documentID, newValuesJson));
        } else {
            EventBus.getDefault().post(new ChargingSessionDataAdded(collectionName, documentID, newValuesJson));
        }
    }

    @Override
    public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {
        Log.d("SDFSYUDSDDSD", "onDataChanged " + collectionName + " " + documentID + " " + updatedValuesJson);
        EventBus.getDefault().post(new DataChanged(collectionName, documentID, updatedValuesJson, removedValuesJson));
        Log.d("SDSGDSDD", "onDataChanged " + collectionName + " " + documentID + " " + updatedValuesJson);
    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {
        Log.d("SDFSYUDSDDSD", "onDataRemoved " + collectionName + " " + documentID + " ");
        if("reservation".equals(collectionName)) {
            EventBus.getDefault().post(new DataRemoved(collectionName, documentID));
        } else if ("chargingSessions".equals(collectionName)) {
            EventBus.getDefault().post(new ChargingSessionDataRemoved(collectionName, documentID));
        }
    }

    private static String getISO8601StringForDate() {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(now);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("SDSHDSDSD", "WAT");
        try {
            mMeteor.disconnect();
        } catch (IllegalStateException e) {

        }
    }
}
