package cd.infoset.vx990training

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cd.infoset.vx990training.ui.theme.Vx990trainingTheme
import com.vfi.smartpos.deviceservice.aidl.IDeviceService
import com.vfi.smartpos.deviceservice.aidl.IPrinter
import com.vfi.smartpos.deviceservice.aidl.PrinterListener

class MainActivity : ComponentActivity() {

    private var deviceService: IDeviceService? = null
    private var printer: IPrinter? = null

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            deviceService = IDeviceService.Stub.asInterface(service)
            try {
                printer = deviceService?.printer
            }catch (e: Exception) {
                Log.e("MainActivity", e.message, e)
            }
            Log.d("MainActivity", "bind service success")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("MainActivity", "${name?.packageName} is disconnected")
            deviceService = null
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Vx990trainingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android") {
                        preparePrint()
                        print()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent()
        intent.action = "com.vfi.smartpos.device_service"
        intent.`package` = "com.vfi.smartpos.deviceservice"

        val serviceConnection = bindService(intent, connection, Context.BIND_AUTO_CREATE)
        if (serviceConnection) {
            Log.d("MainActivity", "Connected")
        } else {
            Log.d("MainActivity", "not connected")
        }
    }

    private fun preparePrint() {
        val format = Bundle()
        format.putInt("font", 1)
        format.putInt("align",0)
        format.putBoolean("newline", true)

        printer?.addText(format, "\n\n")
        printer?.addText(format, "Bonjour")
        printer?.addText(format, "\n\n")
        printer?.addText(format, "\n\n")
    }


    private fun print() {
        printer?.startPrint(object : PrinterListener.Stub() {
            override fun onFinish() { Toast.makeText(applicationContext, "Print finished",
                Toast.LENGTH_SHORT).show() }
            override fun onError(error: Int) { Toast.makeText(applicationContext, "Error",
                Toast.LENGTH_SHORT).show() }
        }) }
}

@Composable
fun Greeting(
    name: String,
    onCLick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = { onCLick() }) {
            Text(text = "PRINT")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Vx990trainingTheme {
        Greeting("Android")
    }
}