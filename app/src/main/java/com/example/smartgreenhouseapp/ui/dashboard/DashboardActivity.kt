package com.example.smartgreenhouseapp.ui.dashboard

import PlantaAdapter
import PlantaModel
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgreenhouseapp.ui.plantdetails.ExemploPlanta
import com.example.smartgreenhouseapp.R
import com.example.smartgreenhouseapp.databinding.ActivityDashboardBinding
import com.example.smartgreenhouseapp.model.CredentialModel
import com.example.smartgreenhouseapp.repository.DatabaseRepository


class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var databaseRepository: DatabaseRepository

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
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewPlantas)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        databaseRepository = DatabaseRepository()

        val codigoSmartpei = "A1B2C3D4E5"

        // Chama o método para buscar as plantas
        databaseRepository.buscarPlantas(codigoSmartpei) { resultado ->
            if (resultado.sucesso) {
                val plantas = resultado.objeto as List<PlantaModel>

                // Configura o Adapter com a lista de plantas
                val adapter = PlantaAdapter(plantas) { planta ->
                    // Configura o clique no card para abrir outra Activity
                    val intent = Intent(this, ExemploPlanta::class.java)
                    intent.putExtra("PLANTA", planta)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this)
            } else {
                Toast.makeText(this, resultado.mensagem, Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun observeViewModel() {
        viewModel.credential.observe(this) { credential ->
            // Atualiza o TextView com o nome da estufa
            binding.nomeEstufa.text = credential.nome
        }
    }
}