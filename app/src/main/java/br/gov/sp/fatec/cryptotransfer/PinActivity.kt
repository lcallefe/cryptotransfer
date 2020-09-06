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
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import br.gov.sp.fatec.cryptotransfer.user.User

class PinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)
        val context = this
        var pin = 0
        val pinEditText = findViewById<EditText>(R.id.pin)
        pinEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.length == 6) {
                    val newPin = s.toString().toInt()
                    when (pin) {
                        0 -> {
                            pin = newPin
                            s.clear()
                            pinEditText.hint = "Confirme o pin inserido"
                        }
                        newPin -> {
                            User.setPin(newPin)
                            Toast.makeText(context,"PIN inserido com sucesso", LENGTH_LONG).show()
                            startActivity(Intent(context,MainActivity::class.java))
                        }
                        else -> {
                            pinEditText.hint = "Digite um PIN de seis dígitos"
                            s.clear()
                            Toast.makeText(
                                context,
                                "PIN incorreto, tente novamente!",
                                LENGTH_LONG
                            ).show()
                            pin = 0
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }
}