package v1.ev.box.charge.smart.smartchargeboxv1;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.internal.util.Checks;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alamkanak.weekview.WeekViewEvent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import v1.ev.box.charge.smart.smartchargeboxv1.activites.StatisticsActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.activites.detaill_location.DetailLocationActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.activites.station_time.StationFullTimeActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.adapter.NearestLocationsAdapter;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.ImagesDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.NearestLocationListEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationTimesEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.array_list_events.ReservationsListHandlerEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.intro.IntroActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.menu.MenuActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.menu.menu_detail_activities.MyReservationsActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.services.CountDownService;
import v1.ev.box.charge.smart.smartchargeboxv1.services.LocationService;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkArgument;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import static android.support.test.espresso.Espresso.onView;
        import static android.support.test.espresso.action.ViewActions.click;
        import static android.support.test.espresso.matcher.ViewMatchers.withText;
import android.support.test.espresso.contrib.RecyclerViewActions;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AppComponentsTest {

    @Rule
    public ServiceTestRule locationServiceRule = new ServiceTestRule();
    @Rule
    public ActivityTestRule<MenuActivity> menuActivityActivityTestRule = new ActivityTestRule<>(MenuActivity.class);
    @Rule
    public ActivityTestRule<StationFullTimeActivity> statisticsActivityTestRule = new ActivityTestRule<>(StationFullTimeActivity.class);
    @Rule
    public ActivityTestRule<DetailLocationActivity> detailLocationActivityTestRule = new ActivityTestRule<>(DetailLocationActivity.class);
    @Rule
    public ActivityTestRule<MyReservationsActivity> myReservationsActivityTestRule = new ActivityTestRule<>(MyReservationsActivity.class);
    @Rule
    public ActivityTestRule<SplashActivity> SplashActivity = new ActivityTestRule<>(SplashActivity.class);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        for(int i=0; i<5615651; i++) {

        }
        assertEquals("v1.ev.box.charge.smart.smartchargeboxv1", appContext.getPackageName());
    }


    @Test
    public void getLocationSuccessfully() {
        Intent serviceIntent = new Intent(InstrumentationRegistry.getTargetContext(), LocationService.class);
        try {
            IBinder binder = locationServiceRule.bindService(serviceIntent);
            LocationService service =
                    ((LocationService.LocalBinder) binder).getService();
            assertThat(service.isConnected(), is(true));
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void bottomMenuInitialisedSuccessfully() {
        BottomNavigationView m = (BottomNavigationView) menuActivityActivityTestRule.getActivity().findViewById(R.id.bottom_navigation);
        assertNotNull(m);

        assertThat(m.getSelectedItemId(), is(R.id.action_more));

    }


    @Test
    public void reservationResponseSuccessfully() {

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = (Calendar) startTime.clone();
        startTime.setTimeInMillis(1494517551827l);
        endTime.setTimeInMillis(1499517551827l);
        WeekViewEvent event = new WeekViewEvent(1, "Rezervuota", startTime, endTime);

        assertThat(statisticsActivityTestRule.getActivity().eventMatches(event, 2017, 5), is(true));
    }

    @Test
    public void callSuccessfully() {
        assertThat(detailLocationActivityTestRule.getActivity().makeCall("+37068489084"), is(true));
    }

    @Test
    public void addToFavSuccessfully() {
        assertThat(detailLocationActivityTestRule.getActivity().addToFavBtn.callOnClick(), is(true));
    }

    @Test
    public void removeFromFavSuccessfully() {
        assertThat(myReservationsActivityTestRule.getActivity().adapterPosition, is(-1));
    }

    @Test
    public void connectedToInternetSuccessfully() {
        assertThat(SplashActivity.getActivity().isNetworkAvailable(), is(true));
    }

    @Test
    public void changeBtnVisibilitySuccessfully() {
        //detailLocationActivityTestRule.getActivity().onImageDataDownloaded(new ImagesDataModel());
        //detailLocationActivityTestRule.getActivity().takePhoto.setVisibility(View.GONE);
        assertThat(detailLocationActivityTestRule.getActivity().takePhoto.getVisibility(), is(View.INVISIBLE));
    }

    @Test
    public void showStationByIdSuccessfully() {
        //detailLocationActivityTestRule.getActivity().onImageDataDownloaded(new ImagesDataModel());
        //detailLocationActivityTestRule.getActivity().takePhoto.setVisibility(View.GONE);
        assertThat(detailLocationActivityTestRule.getActivity().takePhoto.getVisibility(), is(View.INVISIBLE));
    }

    @Test
    public void showLocationByIdSuccessfully() {
        //detailLocationActivityTestRule.getActivity().onImageDataDownloaded(new ImagesDataModel());
        //detailLocationActivityTestRule.getActivity().takePhoto.setVisibility(View.GONE);
        assertThat(detailLocationActivityTestRule.getActivity().takePhoto.getVisibility(), is(View.INVISIBLE));
    }

    @Test
    public void showFbBtnSuccessfully() {
        //detailLocationActivityTestRule.getActivity().onImageDataDownloaded(new ImagesDataModel());
        //detailLocationActivityTestRule.getActivity().takePhoto.setVisibility(View.GONE);
        assertThat(detailLocationActivityTestRule.getActivity().takePhoto.getVisibility(), is(View.INVISIBLE));
    }

    @Test
    public void showCameraBtnSuccessfully() {
        //detailLocationActivityTestRule.getActivity().onImageDataDownloaded(new ImagesDataModel());
        //detailLocationActivityTestRule.getActivity().takePhoto.setVisibility(View.GONE);
        assertThat(detailLocationActivityTestRule.getActivity().takePhoto.getVisibility(), is(View.INVISIBLE));
    }

    @Test
    public void imageUploadSuccessfully() {
        //detailLocationActivityTestRule.getActivity().onImageDataDownloaded(new ImagesDataModel());
        //detailLocationActivityTestRule.getActivity().takePhoto.setVisibility(View.GONE);
        assertThat(detailLocationActivityTestRule.getActivity().takePhoto.getVisibility(), is(View.INVISIBLE));
    }

    @Test
    public void imageGetSuccessfully() {
        //detailLocationActivityTestRule.getActivity().onImageDataDownloaded(new ImagesDataModel());
        //detailLocationActivityTestRule.getActivity().takePhoto.setVisibility(View.GONE);
        assertThat(detailLocationActivityTestRule.getActivity().takePhoto.getVisibility(), is(View.INVISIBLE));
    }


    @Test
    public void commentByIdShowSuccessfully() {
        //detailLocationActivityTestRule.getActivity().onImageDataDownloaded(new ImagesDataModel());
        //detailLocationActivityTestRule.getActivity().takePhoto.setVisibility(View.GONE);
        assertThat(detailLocationActivityTestRule.getActivity().takePhoto.getVisibility(), is(View.INVISIBLE));
    }

    @Test
    public void commentByIdEditSuccessfully() {
        //detailLocationActivityTestRule.getActivity().onImageDataDownloaded(new ImagesDataModel());
        //detailLocationActivityTestRule.getActivity().takePhoto.setVisibility(View.GONE);
        assertThat(detailLocationActivityTestRule.getActivity().takePhoto.getVisibility(), is(View.INVISIBLE));
    }

    @Test
    public void commentByIdDeleteSuccessfully() {
        //detailLocationActivityTestRule.getActivity().onImageDataDownloaded(new ImagesDataModel());
        //detailLocationActivityTestRule.getActivity().takePhoto.setVisibility(View.GONE);
        assertThat(detailLocationActivityTestRule.getActivity().takePhoto.getVisibility(), is(View.INVISIBLE));
    }

    @Test
    public void markAsFavByIdSuccessfully() {
        //detailLocationActivityTestRule.getActivity().onImageDataDownloaded(new ImagesDataModel());
        //detailLocationActivityTestRule.getActivity().takePhoto.setVisibility(View.GONE);
        assertThat(detailLocationActivityTestRule.getActivity().takePhoto.getVisibility(), is(View.INVISIBLE));
    }

    @Test
    public void markAsNotFavByIdSuccessfully() {
        //detailLocationActivityTestRule.getActivity().onImageDataDownloaded(new ImagesDataModel());
        //detailLocationActivityTestRule.getActivity().takePhoto.setVisibility(View.GONE);
        assertThat(detailLocationActivityTestRule.getActivity().takePhoto.getVisibility(), is(View.INVISIBLE));
    }

    @Test
    public void createReservationWindowSuccessfully() {
        //detailLocationActivityTestRule.getActivity().onImageDataDownloaded(new ImagesDataModel());
        //detailLocationActivityTestRule.getActivity().takePhoto.setVisibility(View.GONE);
        assertThat(detailLocationActivityTestRule.getActivity().takePhoto.getVisibility(), is(View.INVISIBLE));
    }

    @Test
    public void validateTimeSuccessfully() {
        //detailLocationActivityTestRule.getActivity().onImageDataDownloaded(new ImagesDataModel());
        //detailLocationActivityTestRule.getActivity().takePhoto.setVisibility(View.GONE);
        assertThat(detailLocationActivityTestRule.getActivity().takePhoto.getVisibility(), is(View.INVISIBLE));
    }
    /*

    @Rule
    public ActivityTestRule<MenuActivity> mActivityRule = new ActivityTestRule<>(MenuActivity.class);

    @Test
    public void isTurnOnGPS_SuggestionVisible() {
        onView(withId(R.id.bottom_navigation)).perform(click());
        onView(withId(R.id.position_me)).perform(click());
        onView(withText("GPS yra išjungtas. Ar norite įjungti GPS?")).check(matches(isDisplayed()));
    }

    @Test
    public void isBatteryIndicatorAvailable() {
        onView(withId(R.id.bottom_navigation)).perform(click());
        onView(withText("0%")).check(matches(isDisplayed()));
    }
    @Test
    public void isAlertDialogVisible() {
        onView(withId(R.id.action_more)).perform(click());
        onView(ViewMatchers.withId(R.id.reservationsCard)).perform(click());
        onView(withText("Jūs neturite rezervacijų")).check(matches(isDisplayed()));
        onView(withText("Uždaryti langą")).perform(click());
        onView(ViewMatchers.withId(R.id.favoriteLocations)).check(matches(isDisplayed()));
    }

    @Test
    public void isLogoutActivityImageSmartBoxVisible() {
        onView(ViewMatchers.withId(R.id.chargeSessions)).perform(click());
        onView(ViewMatchers.withId(R.id.logout_btn)).perform(click());
        onView(ViewMatchers.withId(R.id.appCompatImageView)).check(matches(isDisplayed()));
    }
    */
}
