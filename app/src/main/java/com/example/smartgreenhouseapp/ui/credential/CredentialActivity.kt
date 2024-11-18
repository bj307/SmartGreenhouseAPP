package com.example.smartgreenhouseapp.ui.credential

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.ViewModelProvider
import com.example.smartgreenhouseapp.databinding.ActivityCredentialBinding
import com.example.smartgreenhouseapp.model.CredentialModel
import com.example.smartgreenhouseapp.ui.dashboard.DashboardActivity


class CredentialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCredentialBinding
    private lateinit var viewModel: CredentialViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCredentialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CredentialViewModel::class.java]

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.btnEntrar.setOnClickListener {
            val codigo = binding.edtCodigo.text.toString()
            viewModel.validarCredencial(codigo)
        }

        // Limita o input para 10 dígitos
        binding.edtCodigo.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(10))
    }

    private fun observeViewModel() {
        viewModel.resultado.observe(this) { resultado ->
            resultado?.let {
                when {
                    resultado.sucesso -> {
                        //Navega para próxima tela (Dashboard)
                        val credential = resultado.objeto as CredentialModel
                        val intent = Intent(this, DashboardActivity::class.java).apply {
                            putExtra("CREDENTIAL", credential)
                        }
                        startActivity(intent)
                        finish()
                        //Toast.makeText(this, resultado.mensagem, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // Mostra mensagem de erro
                        Toast.makeText(this, resultado.mensagem, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}