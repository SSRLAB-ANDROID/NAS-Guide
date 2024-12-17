package by.ssrlab.data.data.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DepartmentFilterTranslations(

    @SerializedName("pk")
    val pk: Int,

    @SerializedName("department")
    val departmentFilter: DepartmentFilter,

    @SerializedName("name")
    val name: String
): Parcelable {
    companion object
}