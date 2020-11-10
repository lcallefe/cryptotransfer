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

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File

class Contact(var name: String, var id: String, var deleted: Boolean = false)

class Contacts(var contacts: ArrayList<Contact?> = ArrayList())

fun saveFile(context: Context, text: String) {
    //Initialize the File Writer and write into file
    val file = File(context.filesDir, "contacts.json")
    file.writeText(text)
    Toast.makeText(context, "Contact saved.", Toast.LENGTH_LONG).show()
}

fun readContactsFomFile(context: Context): Contacts {
    val file = File(context.filesDir, "contacts.json")
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

fun saveContactToFile(context: Context, contact: Contact) {
    //Reading current file
    var contactList: Contacts = readContactsFomFile(context)
    //Adding New Contact
    contactList.contacts.add(contact)
    //Creating a new Gson object to read data
    var gson = Gson()
    //Writing to JSON file
    val json = gson.toJson(contactList)
    saveFile(context, json)
}
