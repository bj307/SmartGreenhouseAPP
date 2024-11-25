package com.example.smartgreenhouseapp.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CredentialModel (
    val codigo: String = "",
    val pin: String = "",
    val nome: String = "",
    val status: String = "",
    val email: String = ""
) : Parcelable