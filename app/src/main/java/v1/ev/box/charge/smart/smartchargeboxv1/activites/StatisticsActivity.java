package v1.ev.box.charge.smart.smartchargeboxv1.activites;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PreviewColumnChartView;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.ChargingStatsDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
        intent.setAction(NetworkingService.GET_STATS_DATA);
        startService(intent);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    public static class PlaceholderFragment extends Fragment {

        private ColumnChartView chart;
        private PreviewColumnChartView previewChart;
        private ColumnChartData data;
        /**
         * Deep copy of data.
         */
        private ColumnChartData previewData;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_preview_column_chart, container, false);

            chart = (ColumnChartView) rootView.findViewById(R.id.chart);
            previewChart = (PreviewColumnChartView) rootView.findViewById(R.id.chart_preview);

            // Generate data for previewed chart and copy of that data for preview chart.
            //generateDefaultData();



            return rootView;
        }


        @Override
        public void onResume() {
            super.onResume();

            if(!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }

        @Override
        public void onStop() {
            super.onStop();

            if(EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        }

        @Subscribe
        public void onDataArrived(ChargingStatsDataModel obj) {
            int iYear = Calendar.getInstance().get(Calendar.YEAR);
            int iMonth = Calendar.getInstance().get(Calendar.MONTH);
            int iDay =  Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);
            int numColumns = mycal.getActualMaximum(Calendar.DATE);

            Log.d("SDSGDLSDS", numColumns + "");
            List<Column> columns = new ArrayList<>();
            List<SubcolumnValue> values;
            values = new ArrayList<>();
            values.add(new SubcolumnValue(26, Color.TRANSPARENT));
            columns.add(new Column(values));
            for(int i=1; i<numColumns; i++) {
                values = new ArrayList<>();
                values.add(new SubcolumnValue(0, Color.TRANSPARENT));
                columns.add(new Column(values));
            }
            try {
                for (int i = 0; i < numColumns; i++) {
                    int day = -1;
                    int color = Color.TRANSPARENT;
                    float realTime = 0.0f;
                    if (i < obj.list.size()) {
                        long time = obj.list.get(i).time;
                        long totalSecs = time/1000;
                        int hours = (int) (totalSecs / 3600);
                        int minutes = (int) (totalSecs / 60) % 60;
                        String tmp = hours + "." + minutes;
                        realTime = Float.parseFloat(tmp);
                        if(realTime <= 5) {
                            color = Color.parseColor("#99CC00");
                        } else if (realTime <= 8) {
                            color = Color.parseColor("#FFBB33");
                        } else {
                            color = Color.parseColor("#FF4444");
                        }
                        Log.d("SDSGUIPDISD", obj.list.get(i).time + "");
                        day = obj.list.get(i).day;
                    }
                    values = new ArrayList<>();
                    if(day > -1) {
                        values.add(new SubcolumnValue(realTime, color));
                        columns.set(day, new Column(values));
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                Log.d("SDHSIDSD", e.getMessage());
            }

            data = new ColumnChartData(columns);
            List<AxisValue> axisValues = new ArrayList<>();
            for(int i=0; i<numColumns; i++) {
                AxisValue value = new AxisValue(i);
                axisValues.add(value);
            }

            Axis axisValue = new Axis(axisValues);
            data.setAxisXBottom(axisValue);
            data.setAxisYLeft(new Axis().setHasLines(true));
            //previewData.setBaseValue(24);

            // prepare preview data, is better to use separate deep copy for preview chart.
            // set color to grey to make preview area more visible.
            int counter = 0;
            previewData = new ColumnChartData(data);
            for (Column column : previewData.getColumns()) {
                for (SubcolumnValue value : column.getValues()) {
                    if(counter > 0) {
                        if(value.getValue() > 0) {
                            value.setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
                        }
                    }
                    counter++;
                }
            }

            chart.setColumnChartData(data);
            // Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
            // zoom/scroll is unnecessary.
            chart.setZoomEnabled(false);
            chart.setScrollEnabled(false);

            previewChart.setColumnChartData(previewData);
            previewChart.setViewportChangeListener(new ViewportListener());

            previewX(false);
        }

        private void previewX(boolean animate) {
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            float dx = tempViewport.width() / 4;
            tempViewport.inset(dx, 0);
            if (animate) {
                previewChart.setCurrentViewportWithAnimation(tempViewport);
            } else {
                previewChart.setCurrentViewport(tempViewport);
            }
            previewChart.setZoomType(ZoomType.HORIZONTAL);
        }

        /**
         * Viewport listener for preview chart(lower one). in {@link #onViewportChanged(Viewport)} method change
         * viewport of upper chart.
         */
        private class ViewportListener implements ViewportChangeListener {

            @Override
            public void onViewportChanged(Viewport newViewport) {
                // don't use animation, it is unnecessary when using preview chart because usually viewport changes
                // happens to often.
                chart.setCurrentViewport(newViewport);
            }

        }
    }
}