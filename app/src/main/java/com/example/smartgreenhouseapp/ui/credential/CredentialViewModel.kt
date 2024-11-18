package com.example.smartgreenhouseapp.ui.credential

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartgreenhouseapp.utils.ResultadoOperacao
import com.example.smartgreenhouseapp.model.CredentialModel
import com.example.smartgreenhouseapp.repository.DatabaseRepository

class CredentialViewModel : ViewModel() {

    private val databaseRepository = DatabaseRepository()
    private val _resultado = MutableLiveData<ResultadoOperacao>()
    val resultado: LiveData<ResultadoOperacao> = _resultado

    fun validarCredencial(codigo: String) {
        if (codigo.isEmpty()) {
            _resultado.postValue(ResultadoOperacao(
                mensagem = "Digite o código",
                sucesso = false,
                objeto = null
            ))
            return
        }

        if (codigo.length != 10) {
            _resultado.postValue(ResultadoOperacao(
                mensagem = "O código deve ter 10 dígitos",
                sucesso = false,
                objeto = null
            ))
            return
        }

        databaseRepository.buscarCredencial(codigo) { resultado ->
            _resultado.postValue(resultado)
        }
    }
}