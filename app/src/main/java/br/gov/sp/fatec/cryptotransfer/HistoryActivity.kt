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

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import br.gov.sp.fatec.cryptotransfer.user.decrypt
import br.gov.sp.fatec.cryptotransfer.user.getFingerprint
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

private data class Transfer(
    val receiver: String,
    val time: Long,
    val mimeType: String,
    val name: String,
    val received: Boolean
)

class HistoryActivity : AppCompatActivity() {
    private var history: List<Transfer> = ArrayList(0)

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        getFingerprint(this) { fingerprint ->
            FirebaseFirestore.getInstance().collection("transfer").whereEqualTo("sender", fingerprint)
                .get().addOnSuccessListener {
                    val transfers = HashSet<Transfer>()
                    it.forEach { document ->
                        println(document.data["name"] as String)
                        decrypt(this, document.data["name"] as String) {
                            val name = String(it)
                            decrypt(this, document.data["mimeType"] as String) {
                                val mimeType = String(it)
                                val time = document.data["time"] as Long
                                val receiver = document.data["receiver"] as String
                                FirebaseStorage.getInstance().getReference(receiver)
                                    .child("$fingerprint/$time").downloadUrl.addOnSuccessListener {
                                        transfers.add(Transfer(receiver, time, mimeType, name, false))
                                        history = transfers.sortedBy { it.time }
                                    }.addOnFailureListener {
                                        transfers.add(Transfer(receiver, time, mimeType, name, true))
                                        history = transfers.sortedBy { it.time }
                                    }
                            }
                        }
                    }
                }
        }
    }
}