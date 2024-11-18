package com.example.smartgreenhouseapp.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CredentialModel (
    val codigo: String = "",
    val nome: String = "",
    val status: String = ""
) : Parcelable