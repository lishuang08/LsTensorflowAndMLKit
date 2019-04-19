package ls.yylx.lstensorflowandmlkit


import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.surfaceView


class QRZxingFragment : Fragment() {


    val viewModel: QrzxingViewModel by viewModels()

    val camera by lazy {

    }

    val callback by lazy {
        object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }

    var surfaceHolder: SurfaceHolder? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = UI {
        surfaceView {
            surfaceHolder = holder .apply {
                addCallback(callback)
            }
        }
    }.view

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        surfaceHolder?.removeCallback(callback)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
