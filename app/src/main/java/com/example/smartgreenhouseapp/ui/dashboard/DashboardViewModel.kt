package com.example.smartgreenhouseapp.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartgreenhouseapp.model.CredentialModel
import com.example.smartgreenhouseapp.repository.DatabaseRepository

class DashboardViewModel : ViewModel() {

    private val _credential = MutableLiveData<CredentialModel>()
    val credential: LiveData<CredentialModel> get() = _credential

    val databaseRepository = DatabaseRepository()

    fun setCredential(credentialModel: CredentialModel) {
        _credential.value = credentialModel
    }

    fun listenToCredentialUpdates(codigo: String) {
        databaseRepository.ouvirAtualizacoesCredenciais(codigo) { resultado ->
            if (resultado.sucesso) {
                val credentialModel = resultado.objeto as? CredentialModel
                credentialModel?.let {
                    _credential.postValue(it) // Atualiza o LiveData em tempo real
                }
            } else {
                // Opcional: Logar ou lidar com erros
                Log.e("DashboardViewModel", "Erro: ${resultado.mensagem}")
            }
        }
    }
}