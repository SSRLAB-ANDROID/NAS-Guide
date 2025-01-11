package by.ssrlab.data.data.remote

import by.ssrlab.data.data.common.DescriptionData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Development(

    @SerializedName("pk")
    override val pk: Int,

    @SerializedName("key_name")
    override val keyName: String,

    @SerializedName("logo")
    override val logo: String,

    @SerializedName("organization")
    val organization: Organization?,

    @SerializedName("department_filter")
    val departmentFilter: DepartmentFilter,

    @SerializedName("department_filter_translations")
    val translations: List<DepartmentFilterTranslations>,

    @SerializedName("images")
    override val image: Image,

    @Expose
    override val lon: Double? = null,

    @Expose
    override val lat: Double? = null
): DescriptionData
