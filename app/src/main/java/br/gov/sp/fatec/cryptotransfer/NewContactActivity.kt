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

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_new_contact.*
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class NewContactActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_contact)

        val newContactName = findViewById<EditText>(R.id.newContactName)
        val newContactId = findViewById<EditText>(R.id.newContactId)

        findViewById<Button>(R.id.btnSaveContact).setOnClickListener {
            val name: String = newContactName.text.toString().trim()
            val id: String = newContactId.text.toString().trim()
            if (name.isNullOrBlank()) {
                Toast.makeText(this, "Insert the name.", Toast.LENGTH_LONG).show()
            } else if (id.isNullOrBlank()) {
                Toast.makeText(this, "Insert the ID.", Toast.LENGTH_LONG).show()
            } else {
                val new = Contact(name, id, false)
                saveContactToFile("contacts.json", new)
                val intent = Intent(this, ContactActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun saveFile(fileName: String, text: String) {
        //Initialize the File Writer and write into file
        val file = File(filesDir, fileName)
        file.writeText(text)
        Toast.makeText(this, "New Contact saved.", Toast.LENGTH_LONG).show()
    }

    private fun readContactsFomFile(fileName: String): Contacts {
        val file = File(filesDir, fileName)
        if (file.exists()) {
            //Creating a new Gson object to read data
            var gson = Gson()
            //Read the PostJSON.json file
            val bufferedReader: BufferedReader = file.bufferedReader()
            // Read the text from buffferReader and store in String variable
            val inputString = bufferedReader.use { it.readText() }
            //Convert the Json File to Gson Object
            return gson.fromJson(inputString, Contacts::class.java)
        }
        return Contacts()
    }

    private fun saveContactToFile(fileName: String, contact: Contact) {
        //Reading current file
        var contactList: Contacts = readContactsFomFile(fileName)
        //Adding New Contact
        contactList.contacts.add(contact)
        //Creating a new Gson object to read data
        var gson = Gson()
        //Writing to JSON file
        val json = gson.toJson(contactList)
        saveFile(fileName, json)
    }
}