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

package br.gov.sp.fatec.cryptotransfer.file

import android.content.Context
import android.net.Uri
import br.gov.sp.fatec.cryptotransfer.user.getFingerprint
import br.gov.sp.fatec.cryptotransfer.user.retrievePublicKey
import br.gov.sp.fatec.cryptotransfer.user.sign
import br.gov.sp.fatec.cryptotransfer.util.notify
import com.google.common.io.BaseEncoding
import com.google.common.io.BaseEncoding.base16
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

val encoder: BaseEncoding = base16()

fun upload(context: Context, receiver: String, uri: Uri, name: String, mimeType: String) {
    val time = System.currentTimeMillis()
    val notification = Random.nextInt()
    notify(context, notification, "Envio de arquivo", "Preparando envio")
    val stream = context.contentResolver.openInputStream(uri)
    if (stream == null)
        notify(context, notification, "Falha ao enviar arquivo", "Arquivo inválido")
    else {
        retrievePublicKey(receiver, {
            try {
                val rsa = Cipher.getInstance("RSA")
                rsa.init(
                    ENCRYPT_MODE,
                    KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(encoder.decode(it)))
                )
                val secret = Random.nextBytes(32)
                val iv = Random.nextBytes(16)
                val aes = Cipher.getInstance("AES/CBC/PKCS5Padding")
                aes.init(
                    ENCRYPT_MODE,
                    SecretKeySpec(secret, "AES"),
                    IvParameterSpec(iv)
                )
                val bytes = stream.readBytes()
                sign(context, bytes) {
                    val signature = encoder.encode(it)
                    getFingerprint(context) {
                        notify(context, notification, "Envio de arquivo", "Enviando arquivo")
                        FirebaseFirestore.getInstance().collection("transfer").add(
                            mapOf(
                                "key" to mapOf(
                                    "secret" to encoder.encode(rsa.doFinal(secret)),
                                    "iv" to encoder.encode(rsa.doFinal(iv))
                                ),
                                "time" to time,
                                "name" to encoder.encode(rsa.doFinal(name.toByteArray())),
                                "receiver" to receiver,
                                "sender" to it,
                                "mimeType" to encoder.encode(rsa.doFinal(mimeType.toByteArray())),
                                "signature" to signature
                            )
                        )
                        FirebaseStorage.getInstance()
                            .getReference("$receiver/$it/$time")
                            .putBytes(aes.doFinal(bytes))
                        notify(context, notification, "Arquivo enviado", "Arquivo enviado com sucesso")
                    }
                }
            } catch (ignored: OutOfMemoryError) {
                notify(
                    context,
                    notification,
                    "Falha ao enviar arquivo",
                    "A memória do dispositivo foi esgotada"
                )
            }
        }) { notify(context, notification, "Falha ao enviar arquivo", "Destinatário inválido") }
    }
}