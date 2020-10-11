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

import android.app.PendingIntent.getBroadcast
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BigTextStyle
import androidx.core.app.NotificationManagerCompat
import br.gov.sp.fatec.cryptotransfer.R
import br.gov.sp.fatec.cryptotransfer.Receiver

fun notify(context: Context, id: Int, title: String, text: String) {
    with(NotificationManagerCompat.from(context)) {
        notify(
            id, NotificationCompat.Builder(context, "Transferência de arquivo")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true).build()
        )
    }
}

fun notify(context: Context, id: Int, title: String, text: String, sender: String) {
    with(NotificationManagerCompat.from(context)) {
        notify(
            id, NotificationCompat.Builder(context, "Transferência de arquivo")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(BigTextStyle().bigText(text))
                .addAction(
                    R.drawable.logo, "Mostrar", getBroadcast(
                        context,
                        0,
                        Intent(context, Receiver::class.java).apply {
                            action = "Mostrar" + System.currentTimeMillis()
                            putExtra("id", id)
                            putExtra("sender", sender)
                        },
                        0
                    )
                )
                .addAction(
                    R.drawable.logo,
                    "Excluir todos",
                    getBroadcast(context, 0, Intent(context, Receiver::class.java).apply {
                        action = "Excluir todos" + System.currentTimeMillis()
                        putExtra("id", id)
                        putExtra("sender", sender)
                    }, 0)
                ).build()
        )
    }
}

fun notify(
    context: Context,
    id: Int,
    title: String,
    text: String,
    sender: String,
    archive: String,
    iv: String,
    secret: String,
    time: Long,
    mimeType: String,
    signature: String
) {
    with(NotificationManagerCompat.from(context)) {
        notify(
            id, NotificationCompat.Builder(context, "Transferência de arquivo")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(BigTextStyle().bigText(text))
                .addAction(
                    R.drawable.logo, "Baixar", getBroadcast(
                        context,
                        0,
                        Intent(context, Receiver::class.java).apply {
                            action = "Baixar" + System.currentTimeMillis()
                            putExtra("id", id)
                            putExtra("sender", sender)
                            putExtra("archive", archive)
                            putExtra("iv", iv)
                            putExtra("secret", secret)
                            putExtra("time", time)
                            putExtra("mimeType", mimeType)
                            putExtra("signature", signature)
                        },
                        0
                    )
                )
                .addAction(
                    R.drawable.logo,
                    "Excluir",
                    getBroadcast(context, 0, Intent(context, Receiver::class.java).apply {
                        action = "Excluir" + System.currentTimeMillis()
                        putExtra("id", id)
                        putExtra("sender", sender)
                        putExtra("archive", archive)
                        putExtra("time", time)
                    }, 0)
                ).build()
        )
    }
}