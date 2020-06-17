package sjm.com.sensorama2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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

    private LineChart lcFlail;
    private LineDataSet ldsFlailDataset;

    private LineChart lcForceN;
    private LineDataSet ldsForceNDataset;


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_graph, container, false);

        CreateAllGraphs(v);

        return v;
    }

    private void CreateAllGraphs(View v){

        CreateROMAngleChart(v); //horz bar
        CreateFlailChart(v); //line
        CreateForceNChart(v); //line

    }

    private void CreateROMAngleChart(View v){
        hbcROMAngle = (HorizontalBarChart) (v.findViewById(R.id.ROMAngleChart));
        if(hbcROMAngle!=null){

            ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(10.0f, 0));
            entries.add(new BarEntry(15.0f, 1));
            entries.add(new BarEntry(25.0f, 2));
            entries.add(new BarEntry(35.0f, 3));

            bdsROMAngleDataset = new BarDataSet(entries, "ROM");

            ArrayList<String> labels = new ArrayList<String>();
            labels.add("0");
            labels.add("1");
            labels.add("2");
            labels.add("3");
            labels.add("4");

            BarData bd = new BarData(labels, bdsROMAngleDataset);
            bdsROMAngleDataset.setColor(Color.rgb(0,119,204));

            hbcROMAngle.setData(bd);
            hbcROMAngle.setDescription("");//no description, hinders view
            hbcROMAngle.getLegend().setEnabled(false);
            hbcROMAngle.getXAxis().setDrawLabels(false);

        }
    }

    private void CreateFlailChart(View v){
        lcFlail = (LineChart)(v.findViewById(R.id.FlailChart));
        if(lcFlail!=null){
            List<Entry> entries = new ArrayList<Entry>();
            entries.add(new Entry(5.0f,0));
            entries.add(new Entry(15.0f,1));
            entries.add(new Entry(25.0f,2));
            entries.add(new Entry(5.0f,3));
            entries.add(new Entry(10.0f,4));

            ldsFlailDataset = new LineDataSet(entries, "Flail");

            ArrayList<String> labels = new ArrayList<String>();
            labels.add("0");
            labels.add("1");
            labels.add("2");
            labels.add("3");
            labels.add("4");

            LineData ld = new LineData(labels, ldsFlailDataset);
            ldsFlailDataset.setColor(Color.rgb(0,100,151));

            lcFlail.setData(ld);
            lcFlail.setDescription("");//no description, hinders view
            lcFlail.getLegend().setEnabled(false);
            lcFlail.getXAxis().setDrawLabels(false);
        }
    }

    private void CreateForceNChart(View v){

    }
}