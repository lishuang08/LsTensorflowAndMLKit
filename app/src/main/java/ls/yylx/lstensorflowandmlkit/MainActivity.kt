package ls.yylx.lstensorflowandmlkit

import android.Manifest
import android.hardware.Camera
import android.hardware.camera2.CameraDevice
import android.media.MediaCodec
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult.*
import com.google.android.gms.common.GoogleApiAvailability
import com.orhanobut.logger.Logger
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {
    var googleserviceFlag = true

    val navController by lazy {
        findNavController(R.id.fragment_launch)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {

            }
        }



        if (Build.VERSION.SDK_INT<= 28) {
            checkEachPermission(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            ) {
                it.forEach { item ->
                    if (!item.first) toast("没有${item.second}权限")
                }
            }


        } else {
            checkEachPermission(
                arrayOf(
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA
                )
            ) {
                it.forEach { item ->
                    if (!item.first) toast("没有${item.second}权限")
                }
            }

        }


        val intresultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        googleserviceFlag = when (intresultCode) {
            SUCCESS -> true
            SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED, SERVICE_DISABLED, SERVICE_INVALID -> false
            else -> true
        }




        Logger.e("google    $googleserviceFlag            code:$intresultCode       count:${Camera.getNumberOfCameras()}")


    }


    override fun onSupportNavigateUp() = navController.navigateUp()


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.onRequestMultiplePermissionsResult(requestCode, permissions, grantResults)

    }
}
