package com.example.smartgreenhouseapp.repository

import PlantaModel
import android.net.Uri
import android.util.Log
import com.example.smartgreenhouseapp.model.CredentialModel
import com.example.smartgreenhouseapp.utils.ResultadoOperacao
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

class DatabaseRepository {

    val collectionPei = "SMARTPEI"

    fun buscarCredencial(codigo: String, callback: (ResultadoOperacao) -> Unit) {
        val db = Firebase.firestore
        db.collection(collectionPei)
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
        return db.collection(collectionPei)
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

    fun salvarPlanta(
        codigoSmartpei: String,
        planta: PlantaModel,
        imagemUri: Uri?,
        callback: (ResultadoOperacao) -> Unit
    ) {
        val db = Firebase.firestore
        val storage = Firebase.storage

        // Gera um código se não existir
        val codigoPlanta = planta.codigo.ifEmpty {
            db.collection(collectionPei)
                .document(codigoSmartpei)
                .collection("plantas")
                .document().id
        }

        // Função para salvar no Firestore após upload da imagem
        fun salvarNoFirestore(imagemUrl: String = "") {
            val plantaComImagem = planta.copy(
                codigo = codigoPlanta,
                imagemUrl = imagemUrl
            )

            db.collection("SMARTPEI")
                .document(codigoSmartpei)
                .collection("plantas")
                .document(codigoPlanta)
                .set(plantaComImagem)
                .addOnSuccessListener {
                    callback(
                        ResultadoOperacao(
                            mensagem = "Planta salva com sucesso",
                            sucesso = true,
                            objeto = plantaComImagem
                        )
                    )
                }
                .addOnFailureListener { exception ->
                    callback(
                        ResultadoOperacao(
                            mensagem = "Erro ao salvar planta: ${exception.message}",
                            sucesso = false,
                            objeto = null
                        )
                    )
                }
        }

        // Se não tem imagem, salva direto
        if (imagemUri == null) {
            salvarNoFirestore()
            return
        }

        // Upload da imagem
        val nomeImagem = "$codigoPlanta.jpg"
        val imagemRef = storage.reference.child("imagens_plantas/$nomeImagem")

        imagemRef.putFile(imagemUri)
            .addOnSuccessListener {
                // Obtém a URL de download após upload
                imagemRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        // Salva no Firestore com a URL da imagem
                        salvarNoFirestore(uri.toString())
                    }
                    .addOnFailureListener { exception ->
                        callback(
                            ResultadoOperacao(
                                mensagem = "Erro ao obter URL da imagem: ${exception.message}",
                                sucesso = false,
                                objeto = null
                            )
                        )
                    }
            }
            .addOnFailureListener { exception ->
                callback(
                    ResultadoOperacao(
                        mensagem = "Erro no upload da imagem: ${exception.message}",
                        sucesso = false,
                        objeto = null
                    )
                )
            }
    }

    fun ouvirAtualizacoesPlantas(
        codigoSmartpei: String,
        callback: (ResultadoOperacao) -> Unit
    ): ListenerRegistration {
        val db = Firebase.firestore

        return db.collection(collectionPei)
            .document(codigoSmartpei)
            .collection("plantas")
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

                if (documentSnapshot != null && !documentSnapshot.isEmpty) {
                    // Documento existe, converte para o modelo
                    val plantas = documentSnapshot.documents.mapNotNull { document ->
                        document.toObject(CredentialModel::class.java)
                    }
                    callback(ResultadoOperacao(
                        mensagem = "Plantas encontradas",
                        sucesso = true,
                        objeto = plantas
                    ))
                } else {
                    // Documento não existe
                    callback(ResultadoOperacao(
                        mensagem = "Plantas não encontradas",
                        sucesso = false,
                        objeto = null
                    ))
                }
            }
    }

    fun buscarPlantas(
        codigoSmartpei: String,
        callback: (ResultadoOperacao) -> Unit
    ) {
        val db = Firebase.firestore

        db.collection(collectionPei)
            .document(codigoSmartpei)
            .collection("plantas")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val plantas = querySnapshot.toObjects(PlantaModel::class.java)
                callback(
                    ResultadoOperacao(
                        mensagem = "Plantas encontradas",
                        sucesso = true,
                        objeto = plantas
                    )
                )
            }
            .addOnFailureListener { exception ->
                callback(
                    ResultadoOperacao(
                        mensagem = "Erro ao buscar plantas: ${exception.message}",
                        sucesso = false,
                        objeto = null
                    )
                )
            }
    }

    fun buscarPlanta(
        codigoSmartpei: String,
        codigoPlanta: String,
        callback: (ResultadoOperacao) -> Unit
    ) {
        val db = Firebase.firestore

        db.collection(collectionPei)
            .document(codigoSmartpei)
            .collection("plantas")
            .document(codigoPlanta)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val planta = document.toObject(PlantaModel::class.java)
                    callback(
                        ResultadoOperacao(
                            mensagem = "Planta encontrada",
                            sucesso = true,
                            objeto = planta
                        )
                    )
                } else {
                    callback(
                        ResultadoOperacao(
                            mensagem = "Planta não encontrada",
                            sucesso = false,
                            objeto = null
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                callback(
                    ResultadoOperacao(
                        mensagem = "Erro ao buscar planta: ${exception.message}",
                        sucesso = false,
                        objeto = null
                    )
                )
            }
    }

    // Método para atualizar a lista de plantas
    fun atualizarListaDePlantas(codigoSmartpei: String, callback: (ResultadoOperacao) -> Unit) {
        // Chama o método de buscar plantas do Firestore
        buscarPlantas(codigoSmartpei) { resultado ->
            if (resultado.sucesso) {
                val plantas = resultado.objeto as List<PlantaModel>
                callback(
                    ResultadoOperacao(
                        mensagem = "Lista de plantas atualizada",
                        sucesso = true,
                        objeto = plantas
                    )
                )
            } else {
                callback(
                    ResultadoOperacao(
                        mensagem = "Erro ao atualizar lista de plantas: ${resultado.mensagem}",
                        sucesso = false,
                        objeto = null
                    )
                )
            }
        }
    }
}