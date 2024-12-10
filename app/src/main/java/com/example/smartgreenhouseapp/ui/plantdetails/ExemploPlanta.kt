package com.example.smartgreenhouseapp.ui.plantdetails

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartgreenhouseapp.R

class ExemploPlanta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exemplo_planta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setContentView(R.layout.activity_exemplo_planta)

        val dynamicImageView: ImageView = findViewById(R.id.imageView2)

        // Recebendo o ID da imagem passada pela Intent
        val imageResId = intent.getIntExtra("IMAGE_ID", -1)

        // Verificando se um ID válido foi recebido
        if (imageResId != -1) {
            dynamicImageView.setImageResource(imageResId)
        }
    }
}