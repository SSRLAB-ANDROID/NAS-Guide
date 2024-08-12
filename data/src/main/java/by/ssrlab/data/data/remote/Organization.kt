package by.ssrlab.data.data.remote

import by.ssrlab.data.data.common.DescriptionData
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Organization(

    @SerializedName("pk")
    override val pk: Int,

    @SerializedName("key_name")
    override val keyName: String,

    @SerializedName("latitude")
    override val lat: Double,

    @SerializedName("longitude")
    override val lon: Double,

    @SerializedName("logo")
    override val logo: String,

    @SerializedName("department_filter")
    val departmentFilter: DepartmentFilter,

    @SerializedName("images")
    override val image: Image
): DescriptionData {
    companion object
}
