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
import android.content.Intent
import android.widget.Toast
import br.gov.sp.fatec.cryptotransfer.ContactActivity
import br.gov.sp.fatec.cryptotransfer.R
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File
import java.lang.Exception

class Contact(var name: String, var id: String, var showMenu: Boolean = false)

class Contacts(var contacts: ArrayList<Contact?> = ArrayList())

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

fun addNewContactToFile(context: Context, contact: Contact) {
    //Reading current file
    var contacts: Contacts = readContactsFomFile(context)
    if (contacts.contacts.any{ c -> c!!.id == contact.id }) {
        Toast.makeText(context, R.string.id_already_exists, Toast.LENGTH_LONG).show()
    } else {
        //Adding New Contact
        contacts = addToContacts(contacts, contact)
        saveContactsToFile(context, contacts)
        val intent = Intent(context, ContactActivity::class.java)
        context.startActivity(intent)
    }
}

fun deleteContactFromFile(context: Context, contact: Contact) {
    //Reading current file
    var contacts: Contacts = readContactsFomFile(context)
    //Delete contact
    val update = deleteFromContacts(contacts, contact)
    //Save updated list
    saveContactsToFile(context, update)
}

fun updateContactFromFile(context: Context, old: Contact, new: Contact) {
    //Reading current file
    var contacts: Contacts = readContactsFomFile(context)
    //Delete old contact
    contacts = deleteFromContacts(contacts, old)
    //Add new
    if (contacts.contacts.any{ c -> c!!.id == new.id }) {
        Toast.makeText(context, R.string.id_already_exists, Toast.LENGTH_LONG).show()
    } else {
        //Adding New Contact
        contacts = addToContacts(contacts, new)
        saveContactsToFile(context, contacts)
        val intent = Intent(context, ContactActivity::class.java)
        context.startActivity(intent)
    }
}

private fun addToContacts(allContacts: Contacts, contact: Contact): Contacts {
    var update = allContacts
    update.contacts.add(contact)
    return update
}

private fun deleteFromContacts(allContacts: Contacts, contact: Contact): Contacts {
    val update = allContacts.contacts.filter {
        it != null && it.name != contact.name && it.id != contact.id
    } as ArrayList<Contact?>
    return Contacts(update)
}

private fun saveContactsToFile(context: Context, contacts: Contacts) {
    //Writing to JSON file
    val json = objContactsToJSON(contacts)
    saveJSONtoFile(context, json)
}

private fun objContactsToJSON(contacts: Contacts): String {
    //Creating a new Gson object to read data
    var gson = Gson()
    //Writing to JSON file
    return gson.toJson(contacts)
}

private fun saveJSONtoFile(context: Context, jsonText: String) {
    //Initialize the File Writer and write into file
    val file = File(context.filesDir, "contacts.json")
    file.writeText(jsonText)
    Toast.makeText(context, R.string.contact_saved, Toast.LENGTH_LONG).show()
}
