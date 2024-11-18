package com.example.smartgreenhouseapp.repository

import com.example.smartgreenhouseapp.model.CredentialModel
import com.example.smartgreenhouseapp.utils.ResultadoOperacao
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DatabaseRepository {
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.reference

    fun buscarCredencial(codigo: String, callback: (ResultadoOperacao) -> Unit) {
        reference.child("credenciais").child(codigo).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Dados encontrados
                    val credential = snapshot.getValue(CredentialModel::class.java)
                    callback(
                        ResultadoOperacao(
                            mensagem = "Credencial encontrada",
                            sucesso = true,
                            objeto = credential
                        )
                    )
                } else {
                    // Dados não encontrados
                    callback(
                        ResultadoOperacao(
                            mensagem = "Credencial não encontrada",
                            sucesso = false,
                            objeto = null
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                // Erro ao buscar dados
                callback(
                    ResultadoOperacao(
                        mensagem = "Erro ao buscar credencial: ${exception.message}",
                        sucesso = false,
                        objeto = null
                    )
                )
            }
    }


    fun ouvirAtualizacoesCredenciais(codigo: String, callback: (ResultadoOperacao) -> Unit) {
        val credencialRef = reference.child("credenciais").child(codigo)

        credencialRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val credential = snapshot.getValue(CredentialModel::class.java)
                    callback(ResultadoOperacao(
                        mensagem = "Credencial atualizada",
                        sucesso = true,
                        objeto = credential
                    ))
                } else {
                    callback(ResultadoOperacao(
                        mensagem = "Credencial não encontrada",
                        sucesso = false,
                        objeto = null
                    ))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(ResultadoOperacao(
                    mensagem = "Erro ao ouvir atualizações: ${error.message}",
                    sucesso = false,
                    objeto = null
                ))
            }
        })
    }
}