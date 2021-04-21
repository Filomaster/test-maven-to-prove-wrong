import com.google.gson.annotations.SerializedName
import com.sun.xml.internal.ws.developer.Serialization

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("surname") val surname: String,
    @SerializedName("age") val age: Int,
    @SerializedName("pesel") val pesel: String
)
