package ls.yylx.lstensorflowandmlkit.XcodeScanner


import android.graphics.SurfaceTexture
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.simonlee.xcodescanner.core.*
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_scan.*
import ls.yylx.lstensorflowandmlkit.R
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.toast


class XcodeScannerFragment : Fragment(),
    CameraScanner.CameraListener,
    TextureView.SurfaceTextureListener,
    GraphicDecoder.DecodeListener {

    val mCodeTypeArray = intArrayOf(
        ZBarDecoder.CODABAR,
        ZBarDecoder.CODE39,
        ZBarDecoder.CODE93,
        ZBarDecoder.CODE128,
//        ZBarDecoder.DATABAR,
//        ZBarDecoder.DATABAR_EXP,
        ZBarDecoder.EAN8,
        ZBarDecoder.EAN13,
        ZBarDecoder.I25,
        ZBarDecoder.ISBN10,
        ZBarDecoder.ISBN13,
        ZBarDecoder.PDF417,
        ZBarDecoder.QRCODE
//        ZBarDecoder.UPCA,
//        ZBarDecoder.UPCE
    )


    var mBrightnessCount = 0
    var mCameraScanner: CameraScanner? = null
    var mGraphicDecoder: GraphicDecoder? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_scan, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textureview.surfaceTextureListener = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCameraScanner = NewCameraScanner(this)
        } else {
            mCameraScanner = OldCameraScanner(this)
        }
    }


    override fun openCameraSuccess(surfaceWidth: Int, surfaceHeight: Int, surfaceDegree: Int) {
        textureview.setImageFrameMatrix(surfaceWidth, surfaceHeight, surfaceDegree)

        if (mGraphicDecoder == null) {
            mGraphicDecoder = DebugZBarDecoder(this, mCodeTypeArray)//可使用二参构造方法指定条码识别的类型
        }
        //调用setFrameRect方法会对识别区域进行限制，注意getLeft等获取的是相对于父容器左上角的坐标，实际应传入相对于TextureView左上角的坐标。
        mCameraScanner?.run {
            scannerframe.let {
                setFrameRect(
                    it.left,
                    it.top,
                    it.right,
                    it.bottom
                )
            }
            setGraphicDecoder(mGraphicDecoder)
        }
    }

    override fun openCameraError() {
        toast("打开相机失败")
    }

    override fun noCameraPermission() {
        toast("没有相机权限")
    }

    override fun cameraDisconnected() {
        toast("相机连接失败")
    }


    override fun cameraBrightnessChanged(brightness: Int) {

        if (brightness <= 50) {
            if (mBrightnessCount < 0) {
                mBrightnessCount = 1
            } else {
                mBrightnessCount++
            }
        } else {
            if (mBrightnessCount > 0) {
                mBrightnessCount = -1
            } else {
                mBrightnessCount--
            }
        }
        if (mBrightnessCount > 4) {//连续5帧亮度低于50，显示闪光灯开关
//            mButton_Flash!!.visibility = View.VISIBLE
        } else if (mBrightnessCount < -4 && !mCameraScanner!!.isFlashOpened) {//连续5帧亮度不低于50，且闪光灯未开启，隐藏闪光灯开关
//            mButton_Flash!!.visibility = View.GONE
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        Logger.e("onSurfaceTextureSizeChanged() width = $width , height = $height")

    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        Logger.e("onSurfaceTextureDestroyed()")

        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        mCameraScanner?.run {
            setPreviewTexture(surface)
            setPreviewSize(width, height)
            openCamera(requireContext())
        }
    }

    var mResult = ""
    var mCount = 0
    override fun decodeComplete(result: String?, type: Int, quality: Int, requestCode: Int) {

        view?.snackbar(result ?: "")
        if (result == null) return
        if (result == mResult) {
            if (++mCount > 3) {//连续四次相同则显示结果（主要过滤脏数据，也可以根据条码类型自定义规则）
                when {
                    quality < 10 -> toast("[类型$type/精度00$quality]$result")
                    quality < 100 -> toast("[类型$type/精度0$quality]$result")
                    else -> toast("[类型$type/精度$quality]$result")
                }
            }
        } else {
            mCount = 1
            mResult = result
        }
        Logger.e(".decodeComplete() -> $mResult")
    }






    override fun onPause() {
        mCameraScanner?.closeCamera()
        super.onPause()
    }

    override fun onDestroyView() {
        mCameraScanner!!.setGraphicDecoder(null)
        if (mGraphicDecoder != null) {
            mGraphicDecoder!!.setDecodeListener(null)
            mGraphicDecoder!!.detach()
        }
        mCameraScanner!!.detach()




        textureview.surfaceTextureListener = null
        super.onDestroyView()
    }


}
