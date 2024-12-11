package com.example.smartgreenhouseapp.ui.plantdetails

import PlantaModel
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.smartgreenhouseapp.R
import com.example.smartgreenhouseapp.repository.DatabaseRepository
import com.google.firebase.storage.FirebaseStorage

class ExemploPlanta : AppCompatActivity() {
    private lateinit var database: DatabaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exemplo_planta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recupera a planta passada pelo Intent
        val planta = intent.getParcelableExtra<PlantaModel>("PLANTA")


        if (planta != null) {
            val humidadeText: TextView = findViewById(R.id.tdescHumidade)
            val imagemView: ImageView = findViewById(R.id.imageView2)
            val temperatura: TextView = findViewById(R.id.tTemperatura)
            val tamanho: TextView = findViewById(R.id.tTemanho)
            val luminosidade: TextView = findViewById(R.id.tLuminosidade)



            humidadeText.text = planta.humidadeMax
            temperatura.text = planta.temperaturaMax
            tamanho.text = planta.tamanho
            luminosidade.text = planta.luminosidadeIdeal

            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val imageRef = storageRef.child(planta.imageRef)

            imageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri.toString())
                    .placeholder(R.drawable.exemplo_planta)
                    .into(imagemView)
            }.addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar imagem: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }


        }
    }
}