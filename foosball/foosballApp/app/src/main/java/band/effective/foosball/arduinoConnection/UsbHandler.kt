package band.effective.foosball.arduinoconnection

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.util.Log
import band.effective.foosball.presentation.components.routes.Constants
import band.effective.foosball.presentation.components.routes.Constants.STOPING_FLOW_1_SEC
import band.effective.foosball.presentation.components.routes.Constants.WAITING_TIME_SEC
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager

class UsbHandler(private val newDataHandler: (ByteArray) -> Unit) : SerialInputOutputManager.Listener {

    private var usbIoManager: SerialInputOutputManager? = null

    fun setup(context: Context) {
        val manager = context.getSystemService(Activity.USB_SERVICE) as UsbManager
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)

        if (availableDrivers.isEmpty()) {
            throw IllegalStateException("no available drivers")
        }
        val driver = availableDrivers[0]
        if (!manager.hasPermission(driver.device)) {
            Log.d("VZ", "No Permissions")
            val granted = arrayOf<Boolean?>(null)
            val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    granted[0] = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                }
            }

            val intent = Intent("com.android.example.USB_PERMISSION")
            intent.setPackage(context.packageName)
            val permissionIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
            val filter = IntentFilter("com.android.example.USB_PERMISSION")
            context.registerReceiver(usbReceiver, filter)

            manager.requestPermission(driver.device, permissionIntent)

            for (i in 0..WAITING_TIME_SEC) {
                if (granted[0] != null) break
                Thread.sleep(STOPING_FLOW_1_SEC.toLong())
            }
            Log.d("VZ", "Got permissions")
        }
        val connection = manager.openDevice(driver.device)
        if (connection == null) {
            throw IllegalStateException("connection is null")
        }
        val port = driver.ports[0]

        port.open(connection)
        port.setParameters(
            Constants.BAUD_RATE,
            Constants.DATA_BITS,
            UsbSerialPort.STOPBITS_1,
            UsbSerialPort.PARITY_NONE
        )

        usbIoManager = SerialInputOutputManager(port, this)
        usbIoManager!!.start()
    }

    override fun onNewData(data: ByteArray?) {
        if (data != null) {
            newDataHandler(data)
        }
    }

    override fun onRunError(e: java.lang.Exception?) {
        Log.e("UsbHandler", "OnRunError $e")
    }
}
