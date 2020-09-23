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
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import br.gov.sp.fatec.cryptotransfer.user.getFingerprint
import br.gov.sp.fatec.cryptotransfer.user.retrievePublicKey
import com.google.common.io.BaseEncoding.base16
import com.google.firebase.storage.FirebaseStorage
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.Cipher.ENCRYPT_MODE

fun debugUpload(context: Context, recipient: String, uri: Uri, name: String) =
    retrievePublicKey(recipient, {
        val stream = context.contentResolver.openInputStream(uri)
        if (stream == null)
            Toast.makeText(context, "Arquivo inválido", LENGTH_LONG).show()
        else {
            val cipher = Cipher.getInstance("RSA")
            cipher.init(
                ENCRYPT_MODE,
                KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(base16().decode(it)))
            )
            FirebaseStorage.getInstance()
                .getReference(recipient + "/" + getFingerprint(context) + "/" + System.currentTimeMillis() + "/" + name)
                .putBytes(cipher.doFinal(stream.readBytes()))
        }
    }, { Toast.makeText(context, "Destinatário inválido", LENGTH_LONG).show() })