package ls.yylx.lstensorflowandmlkit

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import com.google.firebase.FirebaseApp
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.squareup.leakcanary.LeakCanary
import io.objectbox.android.AndroidObjectBrowser


class MyApp : Application() {


    override fun onCreate() {
        super.onCreate()
        //防止multiprocess 多次初始化
        val  curProcess = Process.myUid()
        if (getProcessName(curProcess) != packageName) {
            return
        }


        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
        instance = this

        init()

    }


    inline fun init() {
        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })


        FirebaseApp.initializeApp(this)
    }

    private inline fun getProcessName(uid: Int): String? {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val amrp = am.runningAppProcesses.find { it.uid == uid }
        return amrp?.processName

    }


    companion object {
        lateinit var instance: MyApp
//        if (this::instance::isInitialized.get())


    }
}

