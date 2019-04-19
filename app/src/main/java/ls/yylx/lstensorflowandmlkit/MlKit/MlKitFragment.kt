package ls.yylx.lstensorflowandmlkit.MlKit


import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.orhanobut.logger.Logger
import ls.yylx.lstensorflowandmlkit.MlKit.barcodescanning.BarcodeScanningProcessor
import ls.yylx.lstensorflowandmlkit.MlKit.common.CameraSource
import ls.yylx.lstensorflowandmlkit.MlKit.common.CameraSourcePreview
import ls.yylx.lstensorflowandmlkit.MlKit.common.GraphicOverlay
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.custom.customView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.verticalLayout
import java.io.IOException

class MlKitFragment : Fragment() {


    var cameraSource: CameraSource? = null
    var cameraSourcePreview: CameraSourcePreview? = null
    var graphicOverlay: GraphicOverlay? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = UI {
        verticalLayout {
            backgroundResource = R.color.black
            keepScreenOn = true

            customView<CameraSourcePreview> {
                cameraSourcePreview = this
                customView<GraphicOverlay> {
                    graphicOverlay = this

                }
            }
        }
    }.view


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (graphicOverlay != null) {
            createCameraSource(graphicOverlay!!)
        }




//        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
//            .setBarcodeFormats(
//                FirebaseVisionBarcode.FORMAT_QR_CODE,
//                FirebaseVisionBarcode.FORMAT_AZTEC)
//            .build()
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    private fun startCameraSource() {
        cameraSource?.let {
            try {
                if (cameraSourcePreview == null) {
                    Logger.d("resume: Preview is null")
                }
                if (cameraSourcePreview == null) {
                    Logger.d("resume: graphOverlay is null")
                }
                cameraSourcePreview?.start(cameraSource, graphicOverlay)
            } catch (e: IOException) {
                Logger.e("Unable to start camera source.", e)
                cameraSource?.release()
                cameraSource = null
            }
        }
    }

    fun createCameraSource(graphicOverlay: GraphicOverlay) {

        if (cameraSource == null) {
            cameraSource = CameraSource(requireActivity(), graphicOverlay)
        }

        cameraSource?.run {
            setFacing(CameraSource.CAMERA_FACING_BACK)
            setMachineLearningFrameProcessor(BarcodeScanningProcessor())

        }

    }


    override fun onPause() {
        super.onPause()
        cameraSourcePreview?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
    }


}
