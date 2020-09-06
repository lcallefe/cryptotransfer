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

import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.gov.sp.fatec.cryptotransfer.user.getFingerprint
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val requestCode = Random.nextInt(65536)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.PublicKey).text = getFingerprint(this)
        findViewById<Button>(R.id.Send).setOnClickListener {
            startActivityForResult(
                Intent.createChooser(
                    Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT),
                    "Selecione o arquivo"
                ), requestCode
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCode && resultCode == RESULT_OK) {
            contentResolver.query(data!!.data!!, null, null, null, null, null)?.use {
                if (it.moveToFirst()) {
                    findViewById<TextView>(R.id.DebugFileName).text =
                        it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                    val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                    findViewById<TextView>(R.id.DebugFileSize).text =
                        if (!it.isNull(sizeIndex)) it.getString(sizeIndex) else "Desconhecido"
                }
            }
        }
    }
}