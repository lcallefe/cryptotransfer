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

package br.gov.sp.fatec.cryptotransfer

import android.content.Intent
import android.content.Intent.ACTION_CREATE_DOCUMENT
import android.content.Intent.EXTRA_TITLE
import androidx.appcompat.app.AppCompatActivity
import br.gov.sp.fatec.cryptotransfer.user.getFingerprint
import br.gov.sp.fatec.cryptotransfer.user.retrievePublicKey
import br.gov.sp.fatec.cryptotransfer.util.notify
import com.google.common.io.BaseEncoding
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayInputStream
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.zip.GZIPInputStream
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random.Default.nextInt

class ReceiverActivity : AppCompatActivity() {
    val encoder = BaseEncoding.base16()
    private val MAXSIZEBYTES = 128 * 1000 * 1000
    private val requestCode = nextInt(65536)
    private var id = 0
    private var time = 0L
    private lateinit var sender: String
    private lateinit var iv: String
    private lateinit var secret: String
    private lateinit var archive: String
    private lateinit var mimeType: String
    private lateinit var signature: String
    private var asked = false

    override fun onStart() {
        super.onStart()
        if (!asked) {
            Int.MAX_VALUE
            id = intent.getIntExtra("id", nextInt())
            sender = intent.getStringExtra("sender")!!
            iv = intent.getStringExtra("iv")!!
            secret = intent.getStringExtra("secret")!!
            archive = intent.getStringExtra("archive")!!
            time = intent.getLongExtra("time", 0)
            mimeType = intent.getStringExtra("mimeType")!!
            signature = intent.getStringExtra("signature")!!
            startActivityForResult(
                Intent(ACTION_CREATE_DOCUMENT).setType(mimeType)
                    .putExtra(EXTRA_TITLE, archive), requestCode
            )
            asked = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == this.requestCode && resultCode == RESULT_OK && data != null) {
            try {
                val uri = data.data
                if (uri != null) {
                    getFingerprint(this) {
                        val child = FirebaseStorage.getInstance().reference.child("$it/$sender/$time")
                        child.getBytes(MAXSIZEBYTES.toLong()).addOnSuccessListener {
                            val stream = contentResolver.openOutputStream(uri)
                            if (stream != null) {
                                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                                cipher.init(
                                    DECRYPT_MODE,
                                    SecretKeySpec(encoder.decode(secret), "AES"),
                                    IvParameterSpec(encoder.decode(iv))
                                )
                                val gzip = GZIPInputStream(ByteArrayInputStream(it))
                                val bytes = ByteArray(MAXSIZEBYTES)
                                var length = 0
                                while (true) {
                                    val read = gzip.read()
                                    if (read >= 0) bytes[length++] = read.toByte() else break
                                }
                                gzip.close()
                                val final = cipher.doFinal(bytes, 0, length)
                                retrievePublicKey(sender, {
                                    val sig = Signature.getInstance("SHA512withRSA")
                                    sig.initVerify(
                                        KeyFactory.getInstance("RSA")
                                            .generatePublic(X509EncodedKeySpec(encoder.decode(it)))
                                    )
                                    sig.update(final)
                                    if (sig.verify(encoder.decode(signature))) {
                                        stream.write(final)
                                        child.delete()
                                        notify(this, id, "Arquivo salvo", "$archive foi salvo")
                                    } else notify(this, id, "Arquivo corrompido", "O arquivo foi corrompido")
                                }) { notify(this, id, "Arquivo corrompido", "O arquivo foi corrompido") }
                            }
                            this.finish()
                        }
                    }
                }
            } catch (ignored: OutOfMemoryError) {
                notify(this, id, "Falha ao receber arquivo", "A memória do dispositivo foi esgotada")
            }
        }
    }
}