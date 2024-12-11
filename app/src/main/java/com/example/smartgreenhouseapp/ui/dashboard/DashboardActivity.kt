package com.example.smartgreenhouseapp.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.smartgreenhouseapp.ui.plantdetails.ExemploPlanta
import com.example.smartgreenhouseapp.R
import com.example.smartgreenhouseapp.databinding.ActivityDashboardBinding
import com.example.smartgreenhouseapp.model.CredentialModel


class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        // Recupera os dados da credencial
        val credential = intent.getParcelableExtra<CredentialModel>("CREDENTIAL")
        credential?.let {
            viewModel.setCredential(it)
            viewModel.listenToCredentialUpdates(it.codigo)
            viewModel.ouvirAlteracoesPlantas(it.codigo)
            viewModel.buscarTodasPlantas(it.codigo)
        }
        observeViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val cardView  = findViewById<CardView>(R.id.cardView);


        cardView.setOnClickListener {
            val intent = Intent(this, ExemploPlanta::class.java)
            intent.putExtra("IMAGE_ID", R.drawable.cadastrar_planta)
            startActivity(intent)
        }


    }

    private fun observeViewModel() {
        viewModel.credential.observe(this) { credential ->
            // Atualiza o TextView com o nome da estufa
            binding.nomeEstufa.text = credential.nome
        }
    }
}