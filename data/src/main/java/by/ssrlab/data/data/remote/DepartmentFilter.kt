package by.ssrlab.data.data.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DepartmentFilter(

    @SerializedName("pk")
    val pk: Int,

    @SerializedName("key_name")
    val keyName: String
): Parcelable {
    companion object
}