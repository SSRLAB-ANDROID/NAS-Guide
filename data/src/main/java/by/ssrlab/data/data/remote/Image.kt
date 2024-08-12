package by.ssrlab.data.data.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(

    @SerializedName("1")
    val address: String
): Parcelable {
    companion object
}
