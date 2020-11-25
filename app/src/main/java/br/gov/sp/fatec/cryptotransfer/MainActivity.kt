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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.gov.sp.fatec.cryptotransfer.file.debugUpload
import br.gov.sp.fatec.cryptotransfer.file.watch
import br.gov.sp.fatec.cryptotransfer.user.getFingerprint
import br.gov.sp.fatec.cryptotransfer.util.copyTextToClipboard
import br.gov.sp.fatec.cryptotransfer.util.pasteTextFromClipboard
import br.gov.sp.fatec.cryptotransfer.util.set
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random.Default.nextInt

class MainActivity : AppCompatActivity() {
    private val requestCode = nextInt(65536)
    private lateinit var receiver: EditText
    private lateinit var btnSend: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        receiver = findViewById(R.id.ReceiverUserId)
        btnSend = findViewById(R.id.btnSend)

        /*** Show user ID ***/
        getFingerprint(this) { fingerprint ->
            findViewById<TextView>(R.id.SenderUserId).text = fingerprint
            ButtonCopy.setOnClickListener {
                copyTextToClipboard(this, fingerprint)
            }
        }

        /*** Paste functionality for Receiver ID ***/
        ButtonPaste.setOnClickListener {
            ReceiverUserId.setText(pasteTextFromClipboard(this))
        }

        /*** Get ReceiverID from contact list ***/
        val receiverID = intent.getStringExtra("selectedReceiverID")
        if (receiverID != null && receiverID.isNotBlank())
            receiver.setText(receiverID)

        /*** Send File ***/
        btnSend.setOnClickListener {
            startActivityForResult(
                Intent.createChooser(
                    Intent().setType("*/*").setAction(ACTION_GET_CONTENT),
                    "Selecione o arquivo"
                ), requestCode
            )
        }

        /*** Bottom bar navigation functionality ***/
        bottomNavigationView.setOnNavigationItemSelectedListener(set(this))
    }

    override fun onResume() {
        super.onResume()
        watch(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == this.requestCode && resultCode == RESULT_OK && data != null && receiver.text != null) {
            val uri = data.data
            if (uri != null) {
                contentResolver.query(uri, null, null, null, null, null)?.use {
                    if (it.moveToFirst()) {
                        val name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        findViewById<TextView>(R.id.DebugFileName).text = name

                        val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                        findViewById<TextView>(R.id.DebugFileSize).text =
                            if (!it.isNull(sizeIndex)) it.getString(sizeIndex) else "Desconhecido"

                        debugUpload(this, receiver.text.toString(), uri, name, contentResolver.getType(uri)!!)
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel("Transferência de arquivo", "Transferência de arquivo", IMPORTANCE_DEFAULT).apply {
                    description = "Transferência de arquivo"
                })
        }
    }
}