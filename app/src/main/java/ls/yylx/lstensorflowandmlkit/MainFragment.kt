package ls.yylx.lstensorflowandmlkit


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import org.jetbrains.anko.button
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.verticalLayout

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = UI {
        verticalLayout {
            button("Qrzxing") { }
            button("XcodeScanner") {
                setOnClickListener {
                    findNavController().navigate(R.id.action_mainFragment_to_xcodeScannerFragment)

                }
            }
            button("Mlkit") {
                setOnClickListener {
                    findNavController().navigate(R.id.action_mainFragment_to_mlKitFragment)

                }
            }
        }
    }.view


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val volumeNames: Set<String> = MediaStore.getAllVolumeNames(context)
        } else {
        }

    }

}
