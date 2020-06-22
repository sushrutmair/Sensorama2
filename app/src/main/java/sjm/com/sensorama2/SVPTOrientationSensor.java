package sjm.com.sensorama2;

/*
Manages the orientation sensing for the SVPT app. The orientation sensor is a soft sensor
derived from the readings of the accelerometer and magnetometer.
 */


import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

public class SVPTOrientationSensor extends Activity implements SensorEventListener {

    private SensorManager sensorMgr;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    //for flail / jitter
    private float accel;
    private float accelCurrent;
    private float accelLast;
    float x; float y; float z;
    private float weight = 9.8f;

    public void start(SensorManager sm){
        sensorMgr = sm;
        registerSensorListeners();

        accel = 10f;
        accelCurrent = SensorManager.GRAVITY_EARTH;
        accelLast = SensorManager.GRAVITY_EARTH;
    }

    public void stop(SensorManager sm){
        sensorMgr.unregisterListener(this);
    }

    private void registerSensorListeners(){
        Sensor accelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorMgr.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        Sensor magneticField = sensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorMgr.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public float[] getCurrentOrientation(){
        updateOrientationAngles();
        return orientationAngles;
    }

    public float getFlailFactor(){
        accelLast = accelCurrent;
        accelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = accelCurrent - accelLast;
        accel = accel * 0.9f + delta;
        return accel;
    }

    public float getForceInNewtons(){

        return ( (weight/9.8f) * (accelCurrent));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }
    }

    private void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // "RotationMatrix" now has up-to-date information.
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        // "OrientationAngles" now has up-to-date information.
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Don't receive any more updates from either sensor.
        sensorMgr.unregisterListener(this);
    }

    public void getorclicked(View view) {
        getCurrentOrientation();
    }
}
