package sjm.com.sensorama2;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Manages the various sensors required for SVPT. Calls specific sensor classes as needed.
 */

//@todo - add logging

public class AndroidSensors {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magneticField;
    private SVPTOrientationSensor orSensor;

    /*private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];*/

    /*
    Gets called from the relevant classes
     */
    public boolean init(Context myContext){
        if(myContext==null)
            return false;

        boolean bRet = false;

        if(sensorManager == null){
            sensorManager = (SensorManager) myContext.getSystemService(Context.SENSOR_SERVICE);
            if(sensorManager!=null){

                getSensorList();
                if(checkPresenceReqSensors()){
                    orSensor = new SVPTOrientationSensor();
                    orSensor.start(sensorManager);
                    bRet = true;
                }
            }
        }

        return bRet;
    }

    public void stop(){
        if(orSensor!=null && sensorManager!=null){
            orSensor.stop(sensorManager);
        }
    }

    public float[] getCurrOrientation() {
        if(orSensor!=null){
            return ( orSensor.getCurrentOrientation());
        }
        return new float[0];
    }

    public float getFlailReading(){
        if(orSensor!=null) {
            return (orSensor.getFlailFactor());
        }

        return 0f;
    }

    public float getZ(){
        if(orSensor!=null){
            return orSensor.getZ();
        }
        return 0;
    }

    public float getForceReadingN(){
        if(orSensor!=null) {
            return (orSensor.getForceInNewtons());
        }
        return 0f;
    }

    private void getSensorList() {

        /*if(sensorManager!=null){
            availableSensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        }*/
    }

    /*
    Checks for presence and availability of required sensors. For orientation calc we need the
    accelerometer and magnetometer. Either being unavailable is a showstopper.
     */
    private boolean checkPresenceReqSensors() {
        boolean bRet = false;

        if(sensorManager!=null){
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            if( (magneticField != null) && (accelerometer != null))
                bRet = true;
        }

        return bRet;
    }
}
