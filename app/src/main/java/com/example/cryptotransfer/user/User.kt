/*
 * Crypto Transfer is a file transfer application that uses encryption.
 * Copyright (c) 2020  Juliana Tacacima, Luciana Callefe Donadio,
 * Marcos Vinícius Lourenço Teixeira, Melissa Yuri Campos Imai.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.cryptotransfer.user

import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import java.security.KeyPairGenerator
import java.util.*

private fun newUser(sharedPreferences: SharedPreferences) {
    val encoder = Base64.getEncoder()
    val kpg = KeyPairGenerator.getInstance("RSA")
    kpg.initialize(2048)
    val kp = kpg.generateKeyPair()
    val pub = kp.public
    with(sharedPreferences.edit()) {
        putString("privatekey", encoder.encodeToString(kp.private.encoded))
        commit()
    }
    FirebaseFirestore.getInstance().collection("user").document(encoder.encodeToString(pub.encoded))
        .set(mapOf("publickey" to pub.toString()))
        .addOnSuccessListener { println("Conta criada") }
        .addOnFailureListener { println("Fracasso") }


    FirebaseFirestore.getInstance().collection("user").document(encoder.encodeToString(pub.encoded))
        .get()
        .addOnSuccessListener { s -> println(s) }
        .addOnFailureListener { println("Fracasso") }
    println("- - - - - - - - - - - - - -")
    println(sharedPreferences.getString("privatekey", "Fracasso"))
    println("- - - - - - - - - - - - - -")
}

private fun retrieveUser(sharedPreferences: SharedPreferences) {
    TODO()
}

fun getCurrent(sharedPreferences: SharedPreferences) {
    if (false) {
        return retrieveUser(sharedPreferences)
    } else {
        return newUser(sharedPreferences)
    }
}