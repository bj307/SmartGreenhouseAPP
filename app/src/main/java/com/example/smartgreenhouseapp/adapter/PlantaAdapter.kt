import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartgreenhouseapp.R

class PlantaAdapter(
    private val listaPlantas: List<PlantaModel>,
    private val onClick: (PlantaModel) -> Unit
) : RecyclerView.Adapter<PlantaAdapter.PlantaViewHolder>() {

    class PlantaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nomeTextView: TextView = view.findViewById(R.id.nomePlantaTextView)
        val imagemView: ImageView = view.findViewById(R.id.imagemPlantaImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_planta, parent, false)
        return PlantaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantaViewHolder, position: Int) {
        val planta = listaPlantas[position]


        holder.nomeTextView.text = planta.nome

        Glide.with(holder.itemView.context)
            .load(planta.imagemUrl)
            .placeholder(R.drawable.exemplo_planta)
            .into(holder.imagemView)


        holder.itemView.setOnClickListener {
            onClick(planta)
        }
    }

    override fun getItemCount(): Int = listaPlantas.size
}
