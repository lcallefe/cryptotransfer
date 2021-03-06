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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.core.app.NotificationManagerCompat.from
import br.gov.sp.fatec.cryptotransfer.file.watch
import br.gov.sp.fatec.cryptotransfer.user.getFingerprint
import br.gov.sp.fatec.cryptotransfer.util.notify
import com.google.firebase.storage.FirebaseStorage.getInstance
import kotlin.random.Random.Default.nextInt
import kotlin.random.Random.Default.nextLong

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action!!
        val id = intent.getIntExtra("id", nextInt())
        val sender = intent.getStringExtra("sender")
        if (action.startsWith("Mostrar")) {
            watch(context, intent.getStringExtra("sender")!!)
            from(context).cancel(id)
        } else if (action.startsWith("Baixar")) {
            context.startActivity(
                Intent(context, ReceiverActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK).putExtra("id", id)
                    .putExtra("sender", sender)
                    .putExtra("archive", intent.getStringExtra("archive"))
                    .putExtra("iv", intent.getStringExtra("iv"))
                    .putExtra("secret", intent.getStringExtra("secret"))
                    .putExtra("time", intent.getLongExtra("time", 0))
                    .putExtra("mimeType", intent.getStringExtra("mimeType"))
                    .putExtra("signature", intent.getStringExtra("signature"))
            )
        } else if (action.startsWith("Excluir todos")) {
            getFingerprint(context) {
                getInstance().getReference("$it/$sender").listAll().addOnSuccessListener {
                    it.items.forEach { it.delete() }
                    notify(context, id, "Arquivos excluídos", "Os arquivos foram excluídos")
                }
            }
        } else if (action.startsWith("Excluir")) {
            getFingerprint(context) {
                val time = intent.getLongExtra("time", nextLong())
                getInstance().reference.child("$it/$sender/$time").delete().addOnSuccessListener {
                    val archive = intent.getStringExtra("archive")
                    notify(context, id, "Arquivo excluído", "$archive foi excluído")
                }
            }
        }
    }
}