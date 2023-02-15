
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.stepcountpoc.sevices.MyService

class MyPhoneReciver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
            ContextCompat.startForegroundService(context!!, Intent(context, MyService::class.java))
    }
}