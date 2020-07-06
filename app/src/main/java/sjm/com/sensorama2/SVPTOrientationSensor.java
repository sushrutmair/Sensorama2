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
    private float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    //for flail / jitter
    private float accel;
    private float accelCurrent;
    private float accelLast;
    float x; float y; float z;
    private float weight = 9.8f;
    private boolean bIsMagSensorPresent = true;

    public void start(SensorManager sm, boolean bMagSensor){
        sensorMgr = sm;
        registerSensorListeners();

        accel = 10f;
        accelCurrent = SensorManager.GRAVITY_EARTH;
        accelLast = SensorManager.GRAVITY_EARTH;

        bIsMagSensorPresent = !bMagSensor;
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
        if(bIsMagSensorPresent) {
            Sensor magneticField = sensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            if (magneticField != null) {
                sensorMgr.registerListener(this, magneticField,
                        SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    public float[] getCurrentOrientation(){
        if(bIsMagSensorPresent)
            updateOrientationAngles();
        else
            updateOrientationAnglesNoMagSensor();

        return orientationAngles;
    }

    public float getZ(){
        return z;
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

    private void updateOrientationAnglesNoMagSensor() {
        double gx, gy, gz;
        gx = accelerometerReading[0] / 9.81f;
        gy = accelerometerReading[1] / 9.81f;
        gz = accelerometerReading[2] / 9.81f;

        orientationAngles[1] = (float) -Math.atan(gy / Math.sqrt(gx * gx + gz * gz));
        orientationAngles[0] = 0;
        orientationAngles[2] = (float) -Math.atan(gx / Math.sqrt(gy * gy + gz * gz)); // Impossible to guess

        rotationMatrix = getRotationMatrixFromOrientation(orientationAngles);
    }

    private static float[] getRotationMatrixFromOrientation(float[] o) {
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];

        float sinX = (float) Math.sin(o[1]);
        float cosX = (float) Math.cos(o[1]);
        float sinY = (float) Math.sin(o[2]);
        float cosY = (float) Math.cos(o[2]);
        float sinZ = (float) Math.sin(o[0]);
        float cosZ = (float) Math.cos(o[0]);

        // rotation about x-axis (pitch)
        xM[0] = 1.0f;xM[1] = 0.0f;xM[2] = 0.0f;
        xM[3] = 0.0f;xM[4] = cosX;xM[5] = sinX;
        xM[6] = 0.0f;xM[7] =-sinX;xM[8] = cosX;

        // rotation about y-axis (roll)
        yM[0] = cosY;yM[1] = 0.0f;yM[2] = sinY;
        yM[3] = 0.0f;yM[4] = 1.0f;yM[5] = 0.0f;
        yM[6] =-sinY;yM[7] = 0.0f;yM[8] = cosY;

        // rotation about z-axis (azimuth)
        zM[0] = cosZ;zM[1] = sinZ;zM[2] = 0.0f;
        zM[3] =-sinZ;zM[4] = cosZ;zM[5] = 0.0f;
        zM[6] = 0.0f;zM[7] = 0.0f;zM[8] = 1.0f;

        // rotation order is y, x, z (roll, pitch, azimuth)
        float[] resultMatrix = matrixMultiplication(xM, yM);
        resultMatrix = matrixMultiplication(zM, resultMatrix);
        return resultMatrix;
    }

    private static float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
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
