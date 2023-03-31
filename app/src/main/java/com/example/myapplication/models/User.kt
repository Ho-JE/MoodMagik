package com.example.myapplication.models

import java.io.Serializable

class User : Serializable {
    @JvmField
    var name: String? = null
    @JvmField
    var image: String? = null
    @JvmField
    var email: String? = null
    @JvmField
    var token: String? = null
    @JvmField
    var id: String? = null
}