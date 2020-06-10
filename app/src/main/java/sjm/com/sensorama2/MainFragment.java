package sjm.com.sensorama2;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView tv;
    private TextView tvPitch;
    private AndroidSensors as;
    private CountDownTimer orTimer;
    private boolean bTimerStarted = false;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        tv = (TextView) v.findViewById(R.id.textView);
        tvPitch = (TextView)v.findViewById(R.id.tvPitch);

        createLocalUIHandlers(v);

        createSensorHandlers();

        return v;
    }

    private void createSensorHandlers() {
        as = new AndroidSensors();
        as.init(getContext());
    }

    //@todo - check if this works
    public void stopSensing(){
        if(bTimerStarted && orTimer!=null)
            orTimer.cancel();
        if(as!=null){
            as.stop();
        }
    }

    private void createLocalUIHandlers(View v) {
        Button btnGetReading = (Button) (v.findViewById(R.id.buttonReading));
        if(btnGetReading!=null){
            btnGetReading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //get and display orientation
                    if(as!=null) {
                        float[] vals = as.getCurrOrientation();//vals is in radians
                        String z = Double.toString(Math.toDegrees(vals[0]));
                        String x = Double.toString(Math.toDegrees(vals[1]));
                        String y = Double.toString(Math.toDegrees(vals[2]));
                        String all = "-Z (Azimuth): " + z + " \n " +
                                "X (Pitch): " + x + " \n " +
                                "Y (Roll): " + y;
                        tv.setText(all);
                        startTimer();
                    }
                    else
                        tv.setText("error in retrieving custom sensor manager.");
                }
            });
        }

    }

    private void startTimer(){
        if(!bTimerStarted) {
            orTimer = new CountDownTimer(60000, 150) {
                @Override
                public void onTick(long l) {
                    float[] vals = as.getCurrOrientation();
                    String pitch_x = Double.toString(Math.abs(Math.toDegrees(vals[1])));
                    tvPitch.setText(pitch_x);
                }

                @Override
                public void onFinish() {
                    tvPitch.setText("time over. click button to restart for 1 min.");
                    bTimerStarted = false;
                }
            };
            orTimer.start();
            bTimerStarted = true;
        }
    }

    public interface OnFragmentMainMenuInteractionListener {
        // TODO: Update argument type and name
        void onFragmentMainMenInteraction();
    }
}