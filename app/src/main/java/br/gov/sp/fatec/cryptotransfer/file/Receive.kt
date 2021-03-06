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
import br.gov.sp.fatec.cryptotransfer.file.Receive.Companion.add
import br.gov.sp.fatec.cryptotransfer.user.decrypt
import br.gov.sp.fatec.cryptotransfer.user.getFingerprint
import br.gov.sp.fatec.cryptotransfer.util.Contacts
import br.gov.sp.fatec.cryptotransfer.util.notify
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.random.Random.Default.nextInt

fun watch(context: Context) {
    val contactsList = Contacts.readFomFile(context)
    getFingerprint(context) {
        FirebaseStorage.getInstance()
            .getReference(it).listAll().addOnSuccessListener {
                it.prefixes.forEach {
                    if (add(it)) {
                        val contact = contactsList.getNameFromId(it.name) ?: it.name
                        notify(
                            context,
                            nextInt(),
                            "Arquivos disponíveis",
                            "De: " + contact,
                            it.name
                        )
                    }
                }
            }
    }
}

fun watch(context: Context, sender: String) {
    val contactsList = Contacts.readFomFile(context)
    getFingerprint(context) { fingerprint ->
        FirebaseStorage.getInstance()
            .getReference("$fingerprint/$sender").listAll().addOnSuccessListener { result ->
                result.items.forEach {
                    val time = it.name.toLong()
                    FirebaseFirestore.getInstance().collection("transfer").whereEqualTo("time", time)
                        .whereEqualTo("sender", sender).whereEqualTo("receiver", fingerprint).get()
                        .addOnSuccessListener {
                            it.forEach { document ->
                                decrypt(context, document.data["name"] as String) {
                                    val name = String(it)
                                    decrypt(context, document.data["mimeType"] as String) {
                                        val mimeType = String(it)
                                        val key = document.data["key"] as Map<String, String>
                                        decrypt(context, key["iv"]!!) {
                                            val iv = encoder.encode(it)
                                            decrypt(context, key["secret"]!!) {
                                                val secret = encoder.encode(it)
                                                val contact = contactsList.getNameFromId(sender) ?: sender
                                                notify(
                                                    context,
                                                    nextInt(),
                                                    "Arquivo disponível de $contact",
                                                    name,
                                                    sender,
                                                    name,
                                                    iv,
                                                    secret,
                                                    time,
                                                    mimeType,
                                                    document.data["signature"] as String
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                }
            }
    }
}

class Receive {
    companion object {
        private val map = HashSet<StorageReference>()
        fun add(reference: StorageReference) = map.add(reference)
    }
}