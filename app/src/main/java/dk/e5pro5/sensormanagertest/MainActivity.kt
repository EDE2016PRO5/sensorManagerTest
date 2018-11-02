package dk.e5pro5.sensormanagertest


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var mSensorManager:SensorManager? = null
    private var mStepDetector : Sensor ?= null
    private var counterStepDetector=0
    private var startStepCounter=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check whether we're recreating a previously destroyed instance
        //update the value if we are, otherwise, just initialize it to 0.
        counterStepDetector = savedInstanceState?.getInt("STATE_STEP_DETECTOR") ?: 0
        startStepCounter = savedInstanceState?.getInt("STATE_STEP_COUNTER") ?: startStepCounter

        setContentView(R.layout.activity_main)
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mStepDetector= mSensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        val mStepCounter = mSensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if(mStepCounter == null)
        {
            Toast.makeText(this, "No Step Counter sensor was found!",
                Toast.LENGTH_LONG).show()
        }else{
            //If you want to continuously track the number of steps over a long period of time,
            // do NOT unregister for this sensor, so that it keeps counting steps in the background
            // even when the AP is in suspend mode and report the aggregate count
            // when the AP is awake. Application needs to stay registered for this sensor
            // because step counter does not count steps if it is not activated
            mSensorManager?.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_UI)

        }

        if(mStepDetector == null)
        {
            Toast.makeText(this, "No Step Detector sensor was found!",
                Toast.LENGTH_LONG).show()
        }
        else
        {
            mSensorManager?.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_UI)
        }

        stepDetectorTxt.text = counterStepDetector.toString()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
        outState.putInt("STATE_STEP_DETECTOR", counterStepDetector)
        outState.putInt("STATE_STEP_COUNTER", startStepCounter)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSensorManager?.unregisterListener(this)

    }
    override fun onSensorChanged(event: SensorEvent?) {

        when(event?.sensor?.type){
            Sensor.TYPE_STEP_DETECTOR-> {
                counterStepDetector+= 1
                stepDetectorTxt.text = counterStepDetector.toString()}
            Sensor.TYPE_STEP_COUNTER ->{

                if(startStepCounter==0)
                {
                    startStepCounter=event.values[0].toInt()
                }

                stepCounterTxt.text="""${event.values[0].toInt()-startStepCounter}"""

            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

}
