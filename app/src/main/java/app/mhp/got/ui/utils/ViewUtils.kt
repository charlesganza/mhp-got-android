package app.mhp.got.ui.utils

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.*
import app.mhp.got.R
import app.mhp.got.networking.RequestStatus
import com.google.android.material.snackbar.Snackbar

object Fonts {
    const val FONT_NORMAL = "fonts/Font-Regular.ttf"
    const val FONT_BOLD = "fonts/Font-Bold.ttf"
}

fun ViewGroup.showSnackBar(message: Int){
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun View.hideView(){
    this.visibility = View.GONE
}

fun View.makeInvisible(){
    this.visibility = View.INVISIBLE
}

fun View.showView(){
    this.visibility = View.VISIBLE
}

fun Context.makeBold(vararg textView: TextView?){
    textView.map { view ->
        view?.let {
            view.setTypeface(null, Typeface.BOLD)
            view.typeface = Typeface.createFromAsset(assets, Fonts.FONT_BOLD)
        }
    }
}

fun Context.makeSemiBold(vararg textView: TextView){
    textView.map { view ->
        view.setTypeface(null, Typeface.BOLD)
        //view.typeface = Typeface.createFromAsset(assets, Fonts.FONT_MEDIUM)
    }
}

fun ViewGroup.showErrorResponse(response: RequestStatus<Any, Any>){
    when(response){
        is RequestStatus.ServerUnreachable -> {
            this.showSnackBar(R.string.server_unreachable)
        }
        is RequestStatus.ServerError -> {
            this.showSnackBar(R.string.server_error)
        }
        is RequestStatus.NetworkError -> {
            this.showSnackBar(R.string.network_error)
        }
        is RequestStatus.ParseError -> {
            this.showSnackBar(R.string.json_parsing_failed)
        }
        is RequestStatus.ApiError -> {
            when(response.statusCode){
                404 -> this.showSnackBar(R.string.not_available)
                else -> {
                    this.showSnackBar(R.string.error_encountered)
                }
            }
        }
        is RequestStatus.UnknownError -> {
            this.showSnackBar(R.string.unknown_error_encountered)
        }
        else -> {
            //SUCCESS, CACHED, LOADING cases are handled by the individual fragment itself
        }
    }
}
