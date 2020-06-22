package sjm.com.sensorama2;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private HorizontalBarChart hbcROMAngle;
    private BarDataSet bdsROMAngleDataset;

    private AndroidSensors as;
    private CountDownTimer orTimer;
    private boolean bTimerStarted = false;

    LineGraphSeries<DataPoint> mLineSeriesFlail;
    GraphView graphFlail;
    private int xLastFlail = 0;
    private int maxPointsToStoreFlail = 4000;

    private TextView tvROMAngle;

    public GraphFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GraphFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GraphFragment newInstance(String param1, String param2) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_graph, container, false);

        CreateAllGraphs(v);

        createSensorHandlers();

        tvROMAngle = v.findViewById(R.id.textView2);

        return v;
    }

    private void createSensorHandlers() {
        as = new AndroidSensors();
        as.init(getContext());
    }

    public void stopSensing(){
        if(bTimerStarted && orTimer!=null)
            orTimer.cancel();

        if(as!=null){
            as.stop();
        }
    }

    private void startTimer(){
        if(!bTimerStarted) {
            orTimer = new CountDownTimer((60*10*1000)/*10 min*/, 179) {
                @Override
                public void onTick(long l) {
                    float[] vals = as.getCurrOrientation();
                    float signed_rom_angle = (float) Math.toDegrees(vals[1]);
                    float final_rom_angle = 0f;
                    tvROMAngle.setText(Float.toString(signed_rom_angle));
                    if(signed_rom_angle<0f){
                        final_rom_angle = (float) (90.0+Math.abs(signed_rom_angle));
                    }
                    else
                        final_rom_angle = (float) (90.0-signed_rom_angle);
                    //String rom_angle = Double.toString(Math.abs(Math.toDegrees(vals[1])));
                    String rom_angle = Double.toString(final_rom_angle);

                    bdsROMAngleDataset.removeEntry(0);
                    bdsROMAngleDataset.addEntry(new BarEntry(Float.parseFloat(rom_angle),0));
                    hbcROMAngle.notifyDataSetChanged();
                    hbcROMAngle.invalidate();

                    //flail
                    xLastFlail++;
                    mLineSeriesFlail.appendData(new DataPoint(xLastFlail,
                                    as.getFlailReading()),true,
                            maxPointsToStoreFlail);
                }

                @Override
                public void onFinish() {
                    bTimerStarted = false;
                    stopSensing();
                }
            };
            orTimer.start();

            bTimerStarted = true;
        }
    }

    private void makeFlailGraphVViewportDynamic(boolean bMakeDyn) {

        if(bMakeDyn){
            graphFlail.getViewport().setScalable(true);
            graphFlail.getViewport().setScrollable(true);
            graphFlail.getViewport().setScalableY(true);
            graphFlail.getViewport().setScrollableY(true);
            graphFlail.getViewport().setXAxisBoundsManual(false);
        }
        else{
            graphFlail.getViewport().setScalable(false);
            graphFlail.getViewport().setScrollable(false);
            graphFlail.getViewport().setScalableY(false);
            graphFlail.getViewport().setScrollableY(false);
            graphFlail.getViewport().setXAxisBoundsManual(true);
            graphFlail.getViewport().setMaxX(100.0);
            graphFlail.getViewport().setYAxisBoundsManual(true);
            graphFlail.getViewport().setMinY(-40.0);
            graphFlail.getViewport().setMaxY(40.0);
        }
    }

    private void CreateAllGraphs(View v){

        CreateROMAngleChart(v); //horz bar
        CreateFlailGraph(v);
        Button btnStart = (Button)(v.findViewById(R.id.button));
        if(btnStart!=null){
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //start
                    makeFlailGraphVViewportDynamic(false);
                    startTimer();
                }
            });
        }

    }

    private void CreateFlailGraph(View v){
        graphFlail =(GraphView)(v.findViewById(R.id.graphFlail));
        mLineSeriesFlail = new LineGraphSeries<>();
        graphFlail.addSeries(mLineSeriesFlail);
    }

    private void CreateROMAngleChart(View v){
        hbcROMAngle = (HorizontalBarChart) (v.findViewById(R.id.ROMAngleChart));
        if(hbcROMAngle!=null){

            ArrayList<BarEntry> entries = new ArrayList<>();
            float maxDegree = 185.0f;
            entries.add(new BarEntry(maxDegree, 0));

            bdsROMAngleDataset = new BarDataSet(entries, "ROM");

            ArrayList<String> labels = new ArrayList<String>();
            labels.add("0");
            labels.add("10");

            BarData bd = new BarData(labels, bdsROMAngleDataset);
            bdsROMAngleDataset.setColor(Color.rgb(0,119,204));
            bdsROMAngleDataset.setValueTextSize(16f);

            hbcROMAngle.setData(bd);
            hbcROMAngle.setDescription("");//no description, hinders view
            hbcROMAngle.getLegend().setEnabled(false);
            hbcROMAngle.getXAxis().setDrawLabels(false);

        }
    }
}