package com.example.smartgreenhouseapp.ui

import PlantaModel
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartgreenhouseapp.R
import com.example.smartgreenhouseapp.repository.PlantaRepository
import com.example.smartgreenhouseapp.ui.dashboard.DashboardActivity
import com.example.smartgreenhouseapp.ui.plantdetails.ExemploPlanta
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream



class cadastraPlanta : AppCompatActivity() {
    private lateinit var ivAddButton: ImageView
    private lateinit var ivPlantImage: ImageView
    private lateinit var btnSave: Button
    private lateinit var etPlantName: EditText
    private lateinit var etHumidade: EditText
    private lateinit var etPlantSize: EditText
    private lateinit var etTemperatura: EditText
    private lateinit var etLuminosidade: EditText
    private var imageBitmap: Bitmap? = null
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastra_planta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ivAddButton = findViewById(R.id.ivAddButton)
        ivPlantImage = findViewById(R.id.ivPlantImage)
        btnSave = findViewById(R.id.btnSave)
        etPlantName = findViewById(R.id.etPlantName)
        etHumidade = findViewById(R.id.etHumidade)
        etPlantSize = findViewById(R.id.etPlantSize)
        etTemperatura = findViewById(R.id.etTemperatura)
        etLuminosidade = findViewById(R.id.etLuminosidade)

        // Configure o OnClickListener para abrir a câmera
        ivAddButton.setOnClickListener {
            openCamera()
        }

        // Configure o OnClickListener para salvar a planta
        btnSave.setOnClickListener {
            if (imageBitmap != null) {
                salvarImagemNoFirebase(imageBitmap!!)
            } else {
                Toast.makeText(this, "Por favor, tire uma foto da planta.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // A imagem foi capturada com sucesso
            imageBitmap = result.data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                ivPlantImage.setImageBitmap(imageBitmap)
                Toast.makeText(this, "Imagem capturada com sucesso!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Captura da imagem cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Verifica se existe um aplicativo de câmera disponível
        if (cameraIntent.resolveActivity(packageManager) != null) {
            cameraLauncher.launch(cameraIntent)
        } else {
            Toast.makeText(this, "Nenhum aplicativo de câmera encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun salvarImagemNoFirebase(imageBitmap: Bitmap) {
        val storageRef = storage.reference
        val imageRefPath = "imagens/${System.currentTimeMillis()}.jpg"
        val imagensRef = storageRef.child(imageRefPath)

        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imagensRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            imagensRef.downloadUrl.addOnSuccessListener { uri ->
                val imagemUrl = uri.toString()
                salvarNoFirestore(imagemUrl, imageRefPath) // Passando imageRefPath junto com o URL
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Erro ao fazer upload da imagem: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun salvarNoFirestore(imagemUrl: String, imageRefPath: String) {
        val novaPlanta = PlantaModel(
            nome = etPlantName.text.toString(),
            humidadeMax = etHumidade.text.toString(),
            tamanho = etPlantSize.text.toString(),
            temperaturaMax = etTemperatura.text.toString(),
            luminosidadeIdeal = etLuminosidade.text.toString(),
            imagemUrl = imagemUrl,
            imageRef = imageRefPath
        )

        db.collection("SMARTPEI")
            .document("A1B2C3D4E5")
            .collection("plantas")
            .add(novaPlanta)
            .addOnSuccessListener { documentReference ->
                val idGerado = documentReference.id
                Toast.makeText(this, "Planta salva com sucesso, ID: $idGerado", Toast.LENGTH_SHORT).show()


                val intent = Intent(this@cadastraPlanta, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao salvar planta: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }



}

