package com.example.smartgreenhouseapp.repository

import PlantaModel
import android.content.Context
import android.graphics.Bitmap
import com.example.smartgreenhouseapp.utils.ResultadoOperacao
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

class PlantaRepository(
    private val context: Context,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    fun salvarPlantaComImagem(
        planta: PlantaModel,
        imagemBitmap: Bitmap?,
        codigoSmartpei: String,
        codigoPlanta: String,
        callback: (ResultadoOperacao) -> Unit
    ) {
        // Se nenhuma imagem foi capturada, salve a planta sem imagem
        if (imagemBitmap == null) {
            salvarNoFirestore(planta, codigoSmartpei, codigoPlanta, callback)
            return
        }

        // Gerar um nome único para a imagem
        val nomeImagem = "planta_${UUID.randomUUID()}.jpg"

        // Referência para o local de armazenamento no Firebase Storage
        val storageRef = storage.reference
            .child("plantas")
            .child(codigoSmartpei)
            .child(nomeImagem)

        // Converter Bitmap para array de bytes
        val baos = ByteArrayOutputStream()
        imagemBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val dadosImagem = baos.toByteArray()

        // Fazer upload da imagem
        storageRef.putBytes(dadosImagem)
            .addOnSuccessListener { taskSnapshot ->
                // Obter a URL de download da imagem
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Criar uma cópia da planta com a URL da imagem e referência
                    val plantaComImagem = planta.copy(
                        codigo = codigoPlanta,
                        imagemUrl = uri.toString(),
                        imageRef = storageRef.path
                    )

                    // Salvar no Firestore
                    salvarNoFirestore(plantaComImagem, codigoSmartpei, codigoPlanta, callback)
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
                        mensagem = "Erro ao fazer upload da imagem: ${exception.message}",
                        sucesso = false,
                        objeto = null
                    )
                )
            }
    }

    private fun salvarNoFirestore(
        planta: PlantaModel,
        codigoSmartpei: String,
        codigoPlanta: String,
        callback: (ResultadoOperacao) -> Unit
    ) {
        db.collection("SMARTPEI")
            .document(codigoSmartpei)
            .collection("plantas")
            .document(codigoPlanta)
            .set(planta)
            .addOnSuccessListener {
                callback(
                    ResultadoOperacao(
                        mensagem = "Planta salva com sucesso",
                        sucesso = true,
                        objeto = planta
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
}