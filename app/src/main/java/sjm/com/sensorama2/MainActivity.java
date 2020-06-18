package sjm.com.sensorama2;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements
        MainFragment.OnFragmentMainMenuInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SpawnMMF();
        SpawnGF();
    }

    private void SpawnMMF(){
        MainFragment mf = new MainFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragMainMenuContainer, mf);
        ft.commit();
    }

    private void SpawnGF(){
        GraphFragment gf = new GraphFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragMainMenuContainer, gf);
        ft.commit();
    }

    protected void onStop() {
        super.onStop();
        /*MainFragment mf = new MainFragment();
        if (mf != null) {
            mf.stopSensing();
        }*/

        GraphFragment gf = new GraphFragment();
        if (gf != null) {
            gf.stopSensing();
        }
    }

    @Override
    public void onFragmentMainMenInteraction() {

    }
}