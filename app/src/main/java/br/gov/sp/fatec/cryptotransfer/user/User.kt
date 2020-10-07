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

package br.gov.sp.fatec.cryptotransfer.user

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import br.gov.sp.fatec.cryptotransfer.MainActivity
import br.gov.sp.fatec.cryptotransfer.PinConfirmActivity
import br.gov.sp.fatec.cryptotransfer.PinCreateActivity
import br.gov.sp.fatec.cryptotransfer.user.User.Companion.unsetPin
import br.gov.sp.fatec.cryptotransfer.user.User.Companion.waitPin
import com.google.common.io.BaseEncoding.base16
import com.google.firebase.firestore.FirebaseFirestore
import java.nio.ByteBuffer
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.util.function.Function
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private val encoder = base16()

private fun firestore() = FirebaseFirestore.getInstance().collection("user")

private fun fingerprint(bytes: ByteArray): String {
    val md = MessageDigest.getInstance("MD5")
    md.update(bytes)
    return encoder.encode(md.digest())
}

private fun newUser(context: Context, function: Function<String, Unit>) {
    waitPin(context) {
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048)
        val kp = kpg.generateKeyPair()
        val pub = kp.public.encoded
        val pubEncoded = encoder.encode(kp.public.encoded)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(
            ENCRYPT_MODE,
            SecretKeySpec(ByteBuffer.allocate(16).putInt(it).array(), "AES"),
            IvParameterSpec("[58J\\)`K/U.67fIP".toByteArray(Charsets.UTF_8))
        )

        with(context.getSharedPreferences("keys", MODE_PRIVATE).edit()) {
            putString("privatekey", encoder.encode(cipher.doFinal(kp.private.encoded)))
            putString("publickey", encoder.encode(pub))
            commit()
        }
        firestore().document(fingerprint(pub))
            .set(mapOf("publickey" to pubEncoded))
        function.apply(pubEncoded)
    }
}

private fun retrievePublicKeyFromSharedPreferences(context: Context) =
    context.getSharedPreferences("keys", MODE_PRIVATE).getString("publickey", null)

private fun retrievePrivateKeyFromSharedPreferences(context: Context, function: Function<PrivateKey, Unit>) {
    waitPin(context) {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(
            DECRYPT_MODE,
            SecretKeySpec(ByteBuffer.allocate(16).putInt(it).array(), "AES"),
            IvParameterSpec("[58J\\)`K/U.67fIP".toByteArray(Charsets.UTF_8))
        )
        try {
            function.apply(
                KeyFactory.getInstance("RSA").generatePrivate(
                    PKCS8EncodedKeySpec(
                        cipher.doFinal(
                            encoder.decode(
                                context.getSharedPreferences(
                                    "keys",
                                    MODE_PRIVATE
                                ).getString("privatekey", null)!!
                            )
                        )
                    )
                )
            )
        } catch (e: GeneralSecurityException) {
            makeText(context, "PIN inválido, tente novamente", LENGTH_LONG).show()
            unsetPin(context)
            retrievePrivateKeyFromSharedPreferences(context, function)
        }
    }
}

fun decrypt(context: Context, encoded: String, function: Function<ByteArray, Unit>) {
    retrievePrivateKeyFromSharedPreferences(context) {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(
            DECRYPT_MODE,
            it
        )
        function.apply(cipher.doFinal(encoder.decode(encoded)))
    }
}

fun getPublicKeyAsString(context: Context, function: Function<String, Unit>) {
    val preferences = retrievePublicKeyFromSharedPreferences(context)
    if (preferences.isNullOrBlank())
        newUser(context) { function.apply(it) }
    else
        function.apply(preferences)
}

fun getFingerprint(context: Context, function: Function<String, Unit>) =
    getPublicKeyAsString(context) { function.apply(fingerprint(encoder.decode(it))) }

fun isSet(context: Context) = !retrievePublicKeyFromSharedPreferences(context).isNullOrBlank()

fun <U> retrievePublicKey(fingerprint: String, success: Function<String, U>, failure: Function<Exception, U>) {
    firestore().document(fingerprint).get()
        .addOnSuccessListener {
            val pub = it.get("publickey") as String?
            if (pub == null)
                failure.apply(NullPointerException())
            else
                success.apply(pub)
        }
        .addOnFailureListener { failure.apply(it) }
}

class User {
    companion object {
        private var pin = 0
        private val waiting = HashSet<Function<Int, Unit>>()

        fun unsetPin(context: Context) {
            waiting.clear()
            pin = 0
            wrongPin(context)
        }

        fun pinIsSet() = pin != 0

        fun waitPin(context: Context, askForPin: Boolean = true, function: Function<Int, Unit>) {
            if (pinIsSet()) function.apply(pin)
            else if (askForPin) {
                if (isSet(context)) {
                    makeText(context, "Insira o PIN da sua conta", LENGTH_LONG).show()
                    waiting.add(function)
                    context.startActivity(
                        Intent(context, PinConfirmActivity::class.java).addFlags(
                            FLAG_ACTIVITY_NEW_TASK
                        )
                    )
                } else {
                    waiting.clear()
                    waiting.add { context.startActivity(Intent(context, MainActivity::class.java)) }
                    makeText(context, "Insira um PIN para proteger sua conta", LENGTH_LONG).show()
                    context.startActivity(
                        Intent(
                            context,
                            PinCreateActivity::class.java
                        ).addFlags(FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            } else waiting.add(function)
        }

        fun setPin(pin: Int) {
            this.pin = pin
            waiting.forEach { it.apply(pin) }
            waiting.clear()
        }
    }
}