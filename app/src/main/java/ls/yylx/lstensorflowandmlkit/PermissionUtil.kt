package ls.yylx.lstensorflowandmlkit

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.orhanobut.logger.Logger
import ls.yylx.lstensorflowandmlkit.PermissionManager.permissionCode


/**
 * android P之后，权限细分，需要列出全部权限
 * 如需独立判断，修改代码为_MultiplePermission
 *
 *
 * Caution: Future versions of the Android SDK might move a particular permission from one group to another.
 * Therefore, don't base your app's logic on the structure of these permission groups.
 * For example, READ_CONTACTS is in the same permission group as WRITE_CONTACTS as of Android 8.1 (API level 27).
 * If your app requests the READ_CONTACTS permission,
 * and then requests the WRITE_CONTACTS permission,
 * don't assume that the system can automatically grant the WRITE_CONTACTS permission.
 * */

//        val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(this, pm)

data class _Permission(val granted: (Boolean) -> Unit)

data class _MultiplePermission(val granted: (Array<Pair<Boolean, String>>) -> Unit)


object PermissionManager {
    var permissionCode = 0

    val permissionList = HashMap<Int, _Permission>()

    val multipleList = HashMap<Int, _MultiplePermission>()


    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (permissionList.containsKey(requestCode)) {
            val granted = permissionList.remove(requestCode) ?: return
            var isGranted = true
            grantResults.forEach {
                if (it != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false
                    return@forEach
                }
            }

            granted.granted.invoke(isGranted)
        }
    }


    fun onRequestMultiplePermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (multipleList.containsKey(requestCode)) {
            val list = arrayListOf<Pair<Boolean, String>>()
            val granted = multipleList.remove(requestCode) ?: return


            grantResults.forEachIndexed { index, i ->
                list.add((i == PackageManager.PERMISSION_GRANTED) to permissions[index])
            }
            granted.granted.invoke(list.toTypedArray())
        }
    }
}


fun AppCompatActivity.checkPermission(pm: String, granted: (Boolean) -> Unit) {
    if (ActivityCompat.checkSelfPermission(this, pm) == PackageManager.PERMISSION_GRANTED)
        granted(true)
    else {
        val requestCode = ++permissionCode
        PermissionManager.permissionList[requestCode] =
            _Permission(granted)
        ActivityCompat.requestPermissions(this, arrayOf(pm), requestCode)
    }
}

fun AppCompatActivity.checkPermission(pms: Array<String>, granted: (Boolean) -> Unit) {
    if (checkArrayPermission(pms))
        granted(true)
    else {
        val requestCode = ++permissionCode
        PermissionManager.permissionList[requestCode] =
            _Permission(granted)
        ActivityCompat.requestPermissions(this, pms, requestCode)
    }
}


fun AppCompatActivity.checkEachPermission(pms: Array<String>, granted: (Array<Pair<Boolean, String>>) -> Unit) {
    val back = checkArrayEachPermission(pms)
    if (back.first) granted(emptyArray())
    else {
        val requestCode = ++permissionCode
        PermissionManager.multipleList[requestCode] =
            _MultiplePermission(granted)
        ActivityCompat.requestPermissions(this, back.second, requestCode)
    }
}

fun AppCompatActivity.checkArrayEachPermission(pms: Array<String>): Pair<Boolean, Array<String>> {
    var granted = true
    val list = arrayListOf<String>()
    pms.forEach {
        if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
            granted = false
            list.add(it)
            Logger.e("$granted      $it")
        }
    }
    return granted to list.toTypedArray()
}


fun AppCompatActivity.checkArrayPermission(pms: Array<String>): Boolean {
    var granted = true
    pms.forEach {
        if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) granted = false
    }
    return granted
}


fun AppCompatActivity.locationPermission(granted: (Boolean) -> Unit) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
        granted(true)
    else {
        val requestCode = ++permissionCode
        PermissionManager.permissionList[requestCode] =
            _Permission(granted)
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
    }
}

fun AppCompatActivity.storagePermission(granted: (Boolean) -> Unit) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED)
        granted(true)
    else {
        val requestCode = ++permissionCode
        PermissionManager.permissionList[requestCode] =
            _Permission(granted)
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode)
    }
}


fun AppCompatActivity.contactsPermission(granted: (Boolean) -> Unit) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
            == PackageManager.PERMISSION_GRANTED
    )
        granted(true)
    else {
        val requestCode = ++permissionCode
        PermissionManager.permissionList[requestCode] =
            _Permission(granted)
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.WRITE_CONTACTS), requestCode)
    }
}

fun AppCompatActivity.phonePermission(granted: (Boolean) -> Unit) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_SIP)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ADD_VOICEMAIL)
            == PackageManager.PERMISSION_GRANTED
    )
        granted(true)
    else {
        val requestCode = ++permissionCode
        PermissionManager.permissionList[requestCode] =
            _Permission(granted)
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.USE_SIP,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.ADD_VOICEMAIL
        ), requestCode)
    }
}

fun AppCompatActivity.calendarPermission(granted: (Boolean) -> Unit) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
            == PackageManager.PERMISSION_GRANTED)
        granted(true)
    else {
        val requestCode = ++permissionCode
        PermissionManager.permissionList[requestCode] =
            _Permission(granted)
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR), requestCode)
    }
}

fun AppCompatActivity.cameraPermission(granted: (Boolean) -> Unit) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        granted(true)
    else {
        val requestCode = ++permissionCode
        PermissionManager.permissionList[requestCode] =
            _Permission(granted)
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.CAMERA), requestCode)
    }
}

fun AppCompatActivity.sensorsPermission(granted: (Boolean) -> Unit) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED)
        granted(true)
    else {
        val requestCode = ++permissionCode
        PermissionManager.permissionList[requestCode] =
            _Permission(granted)
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.BODY_SENSORS), requestCode)
    }
}

fun AppCompatActivity.microphonePermission(granted: (Boolean) -> Unit) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
        granted(true)
    else {
        val requestCode = ++permissionCode
        PermissionManager.permissionList[requestCode] =
            _Permission(granted)
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.RECORD_AUDIO), requestCode)
    }
}

fun AppCompatActivity.smsPermission(granted: (Boolean) -> Unit) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_WAP_PUSH)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_MMS)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
            == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            == PackageManager.PERMISSION_GRANTED
    )
        granted(true)
    else {
        val requestCode = ++permissionCode
        PermissionManager.permissionList[requestCode] =
            _Permission(granted)
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_WAP_PUSH,
                Manifest.permission.RECEIVE_MMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS), requestCode)
    }
}