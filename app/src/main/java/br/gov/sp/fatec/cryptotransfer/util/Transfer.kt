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

package br.gov.sp.fatec.cryptotransfer.util

import android.content.Context
import br.gov.sp.fatec.cryptotransfer.file.encoder
import br.gov.sp.fatec.cryptotransfer.user.decrypt
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList

class Transfer(
    var transferType: String,
    var senderName: String?,
    var senderId: String,
    var receiverName: String?,
    var receiverId: String,
    var fileName: String,
    var sendDate: String,
)

class History(
    var userId: String,
    var history: ArrayList<Transfer?> = ArrayList()
)

fun getReceivedHistory(context: Context, userId: String): History {
    var sent: ArrayList<Transfer?> = ArrayList()
    var received: ArrayList<Transfer?> = ArrayList()

    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("transfer").whereEqualTo("receiver", userId).get().addOnSuccessListener {
        it.forEach { document ->
            decrypt(context, document.data["name"] as String) {
                val name = String(it)
                decrypt(context, document.data["mimeType"] as String) {
                    val mimeType = String(it)
                    decrypt(context, document.data["sender"] as String) {
                        val sender = String(it)
                        decrypt(context, document.data["time"] as String) {
                            val time = it.toString()
                            val key = document.data["key"] as Map<String, String>
                            decrypt(context, key["iv"]!!) {
                                val iv = encoder.encode(it)
                                decrypt(context, key["secret"]!!) {
                                    val secret = encoder.encode(it)
                                    received.add(Transfer("received",null, sender, "Me", userId, name, time))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    val contacts: Contacts = readContactsFomFile(context)
    if (contacts.contacts.isNotEmpty()) {
        sent.forEach { it?.receiverName = getNameFromContacts(contacts, it!!.receiverId) }
        received.forEach { it?.senderName = getNameFromContacts(contacts, it!!.senderId) }
    }
    sent.addAll(received)
    sent.sortByDescending { it?.sendDate }
    return History(userId, sent)
}