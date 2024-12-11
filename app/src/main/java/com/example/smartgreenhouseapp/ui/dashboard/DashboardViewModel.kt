package com.example.smartgreenhouseapp.ui.dashboard

import PlantaModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartgreenhouseapp.model.CredentialModel
import com.example.smartgreenhouseapp.repository.DatabaseRepository
import com.example.smartgreenhouseapp.utils.ResultadoOperacao

class DashboardViewModel : ViewModel() {

    private val _credential = MutableLiveData<CredentialModel>()
    val credential: LiveData<CredentialModel> get() = _credential
    private val _plantas = MutableLiveData<List<PlantaModel>>()
    val plantas: LiveData<List<PlantaModel>> get() = _plantas
    private val _resultadoPlantas = MutableLiveData<ResultadoOperacao>()
    val resultadoPlantas: LiveData<ResultadoOperacao> = _resultadoPlantas

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

    fun buscarTodasPlantas(codigo: String) {
        databaseRepository.buscarPlantas(codigo) { resultado ->
            _resultadoPlantas.postValue(resultado)
            Log.e("Buscar Plantas ", "Msg: ${resultado.mensagem}")
        }
    }

    fun ouvirAlteracoesPlantas(codigo: String) {
        databaseRepository.ouvirAtualizacoesPlantas(codigo) { resultado ->
            if(resultado.sucesso){
                val objPlantas = resultado.objeto as? List<PlantaModel>
                objPlantas?.let {
                    _plantas.postValue(it)
                }
            } else {
                Log.e("DashboardViewModel | Buscar Plantas ", "Erro: ${resultado.mensagem}")
            }
        }
    }
}