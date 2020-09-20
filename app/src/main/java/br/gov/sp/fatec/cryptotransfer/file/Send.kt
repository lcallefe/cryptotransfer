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
import com.google.firebase.storage.FirebaseStorage

fun debugUpload(context: Context, recipient: String, uri: Uri, name: String) {
    FirebaseStorage.getInstance()
        .getReference(recipient + "/" + getFingerprint(context) + "/" + System.currentTimeMillis() + "/" + name)
        .putFile(uri)
}