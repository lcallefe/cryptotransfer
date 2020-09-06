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
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import br.gov.sp.fatec.cryptotransfer.PinActivity
import com.google.common.io.BaseEncoding.base16
import com.google.firebase.firestore.FirebaseFirestore
import java.nio.ByteBuffer
import java.security.KeyPairGenerator
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private val encoder = base16()

private fun fingerprint(bytes: ByteArray): String {
    val md = MessageDigest.getInstance("MD5")
    md.update(bytes)
    return encoder.encode(md.digest())
}

private fun newUser(context: Context): String? {
    val pin = User.getPin(context)
    if (pin == null) return pin
    else {
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048)
        val kp = kpg.generateKeyPair()
        val pub = kp.public.encoded
        val pubEncoded = encoder.encode(kp.public.encoded)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(ByteBuffer.allocate(16).putInt(pin).array(), "AES"),
            IvParameterSpec("[58J\\)`K/U.67fIP".toByteArray(Charsets.UTF_8))
        )

        with(context.getSharedPreferences("keys", MODE_PRIVATE).edit()) {
            putString("privatekey", encoder.encode(cipher.doFinal(kp.private.encoded)))
            putString("publickey", encoder.encode(pub))
            commit()
        }
        FirebaseFirestore.getInstance().collection("user").document(fingerprint(pub))
            .set(mapOf("publickey" to pubEncoded))
        return pubEncoded
    }
}

private fun retrievePublicKeyFromSharedPreferences(context: Context) =
    context.getSharedPreferences("keys", MODE_PRIVATE).getString("publickey", null)

fun getPublicKeyAsString(context: Context) =
    retrievePublicKeyFromSharedPreferences(context) ?: newUser(context)

fun getFingerprint(context: Context): String? {
    val s = getPublicKeyAsString(context)
    if (s == null) return s
    else return fingerprint(encoder.decode(s))
}

class User {
    companion object {
        private var PIN = 0
        fun getPin(context: Context): Int? {
            if (PIN == 0) {
                Toast.makeText(context, "Insira um PIN para proteger sua conta", LENGTH_LONG).show()
                context.startActivity(Intent(context, PinActivity::class.java))
                return null
            } else
                return PIN
        }

        fun setPin(PIN: Int) {
            this.PIN = PIN
        }
    }
}