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
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File

class ContactActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        var contactList: ArrayList<Contact?> = readContactsFomFile("contacts.json").contacts
        viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewAdapter = ContactAdapter(contactList)
        recyclerView = findViewById<RecyclerView>(R.id.rv_contacts)
        recyclerView!!.layoutManager = viewManager
        recyclerView!!.adapter = viewAdapter

        findViewById<Button>(R.id.btnAddContact).setOnClickListener {
            val intent = Intent(this, NewContactActivity::class.java)
            startActivity(intent)
        }
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
}