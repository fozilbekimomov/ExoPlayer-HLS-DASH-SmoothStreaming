package uz.turontelecom.tvmodule.ui.view.player


/**
 * Created by <a href="mailto: fozilbekimomov@gmail.com" >Fozilbek Imomov</a>
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/27/20
 * @project Cinerama
 */


interface PlayerFragmentContract {
    fun setPlayUrl(url: String, position: Long)
    fun setPlayUrl(list: Array<String>, currentPosition: Int, startPosition: Long)
}