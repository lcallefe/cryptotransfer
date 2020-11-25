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
import android.widget.Toast
import br.gov.sp.fatec.cryptotransfer.R
import br.gov.sp.fatec.cryptotransfer.file.encoder
import br.gov.sp.fatec.cryptotransfer.user.decrypt
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList

//class Key(
//    val iv: String,
//    val secret: String
//)
//
//class TransferModel (
//    val key: Key,
//    val time: Long,
//    val name: String,
//    val receiver: String,
//    val sender: String,
//    val mimeType: String,
//    val signature: String
//)
//
//class Transfer(
//    var transferType: String,
//    var senderName: String?,
//    var senderId: String,
//    var receiverName: String?,
//    var receiverId: String,
//    var fileName: String,
//    var sendDate: Long,
//)

//class History(
//    var userId: String,
//    var transferList: ArrayList<Transfer> = ArrayList()
//) {
//    fun findNames(context: Context): History {
//        val userId = this.userId
//        val updated = History(userId)
//        val contacts: Contacts = Contacts.readFomFile(context)
//        if (contacts.list.isNotEmpty()) {
//            this.transferList.forEach {
//                val t = it
//                t.senderName = if (it.senderId == userId) "Me" else contacts.getNameFromId(it.senderId)
//                t.receiverName = if (it.receiverId == userId) "Me" else contacts.getNameFromId(it.receiverId)
//                updated.transferList.add(t)
//            }
//        }
//        return updated
//    }
//
//    companion object {
//        fun getSent(context: Context, userId: String, myCallback: MyCallback) {
//            FirebaseFirestore.getInstance()
//                .collection("transfer")
//                .whereEqualTo("receiver", userId)
//                .get()
//                .addOnSuccessListener {
//                    val sent = ArrayList<Transfer>()
//                    it.forEach { document ->
//                        val time = document.data["time"] as Long
//                        val sender = document.data["sender"] as String
//                        val receiver = document.data["receiver"] as String
//                        sent.add(Transfer("received",null, sender, "Me", receiver, "name", time))
//                    }
//                    myCallback.onCallback(sent)
//                }
////                .addOnFailureListener { exception ->
//////                    Log.w(TAG, "Error getting documents: ", exception)
////                    Toast.makeText(context, "xiii", Toast.LENGTH_LONG).show()
////                }
////            sent.sortByDescending { it.sendDate }
////            return History(userId, sent).findNames(context)
//        }
//    }
//}

//fun getSent(context: Context, userId: String, myCallback: MyCallback) {
//    FirebaseFirestore.getInstance()
//        .collection("transfer")
//        .whereEqualTo("receiver", userId)
//        .get()
//        .addOnSuccessListener {
//            val sent = ArrayList<Transfer>()
//            it.forEach { document ->
//                val time = document.data["time"] as Long
//                val sender = document.data["sender"] as String
//                val receiver = document.data["receiver"] as String
//                sent.add(Transfer("received",null, sender, "Me", receiver, "name", time))
//            }
//            myCallback.onCallback(sent)
//        }
//                .addOnFailureListener { exception ->
////                    Log.w(TAG, "Error getting documents: ", exception)
//                    Toast.makeText(context, "xiii", Toast.LENGTH_LONG).show()
//                }
//}

//fun readData(context: Context, userId: String, myCallback : MyCallback) {
//    FirebaseFirestore.getInstance()
//        .collection("transfer")
//        .whereEqualTo("receiver", userId)
//        .get()
//        .addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val list = ArrayList<Transfer>()
//                for (document in task.result!!) {
//                    val time = document.data["time"] as Long
//                    val sender = document.data["sender"] as String
//                    val receiver = document.data["receiver"] as String
//                    list.add(Transfer("received",null, sender, "Me", receiver, "name", time))
//                }
//                myCallback.onCallback(list)
//            }
//        }
//}


//fun getReceivedHistory(context: Context, userId: String): History {
//    var sent: ArrayList<Transfer> = ArrayList()
//    var received: ArrayList<Transfer> = ArrayList()
//    val firestore = FirebaseFirestore.getInstance()
//    firestore.collection("transfer").whereEqualTo("receiver", userId).get().addOnSuccessListener {
//        it.forEach { document ->
//            val time = document.data["time"] as Long
//            val sender = document.data["sender"] as String
//            val receiver = document.data["receiver"] as String
////            decrypt(context, document.data["name"] as String) {
////                val name = String(it)
//                val t = Transfer("received",null, sender, "Me", receiver, "name", time)
//            received.add(t)
////            }
//        }
//    }
//    val contacts: Contacts = Contacts.readFomFile(context)
//    if (contacts.list.isNotEmpty()) {
//        sent.forEach { it.receiverName = contacts.getNameFromId(it.receiverId) }
//        received.forEach { it.senderName = contacts.getNameFromId(it.senderId) }
//    }
//    sent.addAll(received)
//    sent.sortByDescending { it.sendDate }
//    return History(userId, sent)
//}

//interface MyCallback {
//    fun onCallback(value: ArrayList<Transfer>)
//}