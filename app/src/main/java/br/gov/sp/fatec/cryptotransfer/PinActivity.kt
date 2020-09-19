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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import br.gov.sp.fatec.cryptotransfer.user.User
import br.gov.sp.fatec.cryptotransfer.user.can
import br.gov.sp.fatec.cryptotransfer.user.delete
import br.gov.sp.fatec.cryptotransfer.user.wrongPin


class PinActivity : AppCompatActivity() {
    lateinit var deleteAccount: Button
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
                            pinEditText.hint = getString(R.string.confirm_pin)
                        }
                        newPin -> {
                            User.setPin(newPin)
                            Toast.makeText(context, getString(R.string.pin_created), LENGTH_LONG).show()
                            startActivity(Intent(context, MainActivity::class.java))
                        }
                        else -> {
                            wrongPin(context)
                            pinEditText.hint = getString(R.string.create_pin)
                            s.clear()
                            Toast.makeText(
                                context,
                                getString(R.string.wrong_pin),
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
        deleteAccount = findViewById(R.id.DeleteAccount)
        deleteAccount.setOnClickListener { delete(context) }
    }

    private fun minutesFormatter(minutes: Short): String {
        val days = minutes.div(24 * 60)
        var remaining = minutes - days * 24 * 60
        val hours = remaining.div(60)
        remaining -= hours * 60
        var formatted = ""
        if (days > 0) formatted += days.toString() + "d "
        if (hours > 0) formatted += hours.toString() + "h "
        if (remaining > 0) formatted += remaining.toString() + "m"
        return formatted
    }

    override fun onResume() {
        super.onResume()
        val can = can(this)
        deleteAccount.isEnabled = can == 0.toShort()
        if (can < 0) deleteAccount.visibility = GONE
        else deleteAccount.visibility = VISIBLE
        if (can > 0) deleteAccount.text =
            "Excluir sua conta e redefinir pin" + System.getProperty("line.separator") + "Aguarde " +
                    minutesFormatter(can)
    }
}