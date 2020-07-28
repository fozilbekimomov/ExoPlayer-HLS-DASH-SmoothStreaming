package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment


/**
 * Created by <a href="mailto: fozilbekimomov@gmail.com" >Fozilbek Imomov</a>
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/27/20
 * @project Cinerama
 */


abstract class BaseFragment(@LayoutRes private var layoutRes: Int) : Fragment() {

    protected var TAG = "${javaClass.simpleName}"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutRes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        afterCreate(view, savedInstanceState)
    }

    abstract fun afterCreate(view: View, bundle: Bundle?)


}