package com.example.smartgreenhouseapp.repository

import android.util.Log
import com.example.smartgreenhouseapp.model.CredentialModel
import com.example.smartgreenhouseapp.utils.ResultadoOperacao
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class DatabaseRepository {

    fun buscarCredencial(codigo: String, callback: (ResultadoOperacao) -> Unit) {
        val db = Firebase.firestore
        db.collection("estufas")
            .document(codigo)
            .get()
            .addOnSuccessListener {document ->
                if (document != null && document.exists()) {
                    // Dados encontrados
                    val credential = document.toObject(CredentialModel::class.java)
                    callback(
                        ResultadoOperacao(
                            mensagem = "Credencial encontrada",
                            sucesso = true,
                            objeto = credential
                        )
                    )
                } else {
                    // Documento não encontrado
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


    fun ouvirAtualizacoesCredenciais(codigo: String, callback: (ResultadoOperacao) -> Unit): ListenerRegistration {
        val db = Firebase.firestore
        // Referência para o documento específico na collection "estufas"
        return db.collection("estufas")
            .document(codigo)
            .addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    // Se houver erro, notifica através do callback
                    callback(ResultadoOperacao(
                        mensagem = "Erro ao ouvir atualizações: ${error.message}",
                        sucesso = false,
                        objeto = null
                    ))
                    return@addSnapshotListener
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Documento existe, converte para o modelo
                    val credential = documentSnapshot.toObject(CredentialModel::class.java)
                    callback(ResultadoOperacao(
                        mensagem = "Credencial atualizada",
                        sucesso = true,
                        objeto = credential
                    ))
                } else {
                    // Documento não existe
                    callback(ResultadoOperacao(
                        mensagem = "Credencial não encontrada",
                        sucesso = false,
                        objeto = null
                    ))
                }
            }
    }
}