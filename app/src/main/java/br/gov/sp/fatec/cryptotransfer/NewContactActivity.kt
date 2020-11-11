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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import br.gov.sp.fatec.cryptotransfer.util.Contact
import br.gov.sp.fatec.cryptotransfer.util.addNewContactToFile
import br.gov.sp.fatec.cryptotransfer.util.updateContactFromFile

class NewContactActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_contact)

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        val update = intent.getStringExtra("updateContact")

        val newContactName = findViewById<EditText>(R.id.newContactName)
        val newContactId = findViewById<EditText>(R.id.newContactId)

        /*** Update from contact list ***/
        if (update != null && update.isNotBlank()) {
            actionBar!!.setTitle(R.string.update_contact)
            newContactName.setText(intent.getStringExtra("selectedContactName"))
            newContactId.setText(intent.getStringExtra("selectedContactID"))
        } else
            actionBar!!.setTitle(R.string.new_contact)

        findViewById<Button>(R.id.btnSaveContact).setOnClickListener {
            val name: String = newContactName.text.toString().trim()
            val id: String = newContactId.text.toString().trim()
            if (name.isNullOrBlank()) {
                Toast.makeText(this, "Insert the name.", Toast.LENGTH_LONG).show()
            } else if (id.isNullOrBlank()) {
                Toast.makeText(this, "Insert the ID.", Toast.LENGTH_LONG).show()
            } else {
                val new = Contact(name, id, false)
                if (update != null && update.isNotBlank()) {
                    val old = Contact(intent.getStringExtra("selectedContactName")!!, intent.getStringExtra("selectedContactID")!!)
                    updateContactFromFile(this, old, new)
                } else
                    addNewContactToFile(this, new)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}