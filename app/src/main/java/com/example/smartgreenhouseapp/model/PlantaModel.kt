import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlantaModel(
    val codigo: String = "",
    val nome: String = "",
    val imagemUrl: String = "",
    val humidadeMin: String = "",
    val humidadeMax: String = "",
    val temperaturaMin: String = "",
    val temperaturaMax: String = "",
    val luminosidadeIdeal: String = "",
    val tamanho: String = "",
    val observacao: String = "",
    val codigoSensor: String = "",
    val imageRef: String = ""
) : Parcelable