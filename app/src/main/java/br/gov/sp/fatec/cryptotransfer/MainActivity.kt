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
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random.Default.nextInt

class MainActivity : AppCompatActivity() {
    private val requestCode = nextInt(65536)
    lateinit var receiver: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        getFingerprint(this) { fingerprint ->
            findViewById<TextView>(R.id.SenderUserId).text = fingerprint
            ButtonCopy.setOnClickListener {
                copyTextToClipboard(fingerprint)
            }
        }

        ButtonPaste.setOnClickListener {
            pasteTextFromClipboard()
        }

        findViewById<Button>(R.id.btnSend).setOnClickListener {
            startActivityForResult(
                Intent.createChooser(
                    Intent().setType("*/*").setAction(ACTION_GET_CONTENT),
                    "Selecione o arquivo"
                ), requestCode
            )
        }

        receiver = findViewById(R.id.ReceiverUserId)

        /*** Bottom bar navigation functionality ***/
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {item ->
            when (item.itemId) {
                R.id.contactFragment -> {
                    val intent = Intent(this, ContactActivity::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.sendFragment -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.historyFragment -> {
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
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

    private fun copyTextToClipboard(textToCopy: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(
            ClipData.newPlainText("sender", textToCopy)
        )

        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()
    }

    private fun pasteTextFromClipboard() {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        ReceiverUserId.setText(
            clipboardManager.primaryClip?.getItemAt(0)?.text.toString().trim()
        )
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