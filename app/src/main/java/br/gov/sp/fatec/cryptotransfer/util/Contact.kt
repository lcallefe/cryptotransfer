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
import java.util.*
import kotlin.collections.ArrayList

class Contact(var name: String, var id: String, var showMenu: Boolean = false)

class Contacts(var list: ArrayList<Contact> = ArrayList()) {
    fun getNameFromId(id: String): String {
        return this.list.findLast { it.id == id }?.name as String
    }

    private fun addContact(contact: Contact): Contacts {
        val update  = this
        update.list.add(contact)
        //order
        update.list.sortBy { it.name.toLowerCase(Locale.ROOT) }
        return update
    }

    private fun deleteContact(contact: Contact): Contacts {
        val update = this.list.filter { it.name != contact.name && it.id != contact.id } as ArrayList<Contact>
        return Contacts(update)
    }

    private fun saveToJSONFile(context: Context) {
        //Writing to JSON file
        val json = this.toJSON()
        saveToFile(context, json)
    }

    private fun toJSON(): String {
        //Creating a new Gson object to read data
        val gson = Gson()
        //Writing to JSON file
        return gson.toJson(this)
    }

    companion object {
        private fun saveToFile(context: Context, jsonText: String) {
            //Initialize the File Writer and write into file
            val file = File(context.filesDir, "contacts.json")
            file.writeText(jsonText)
            Toast.makeText(context, R.string.contact_saved, Toast.LENGTH_LONG).show()
        }

        fun readFomFile(context: Context): Contacts {
            val file = File(context.filesDir, "contacts.json")
            if (file.exists()) {
                //Creating a new Gson object to read data
                val gson = Gson()
                //Read the PostJSON.json file
                val bufferedReader: BufferedReader = file.bufferedReader()
                // Read the text from bufferReader and store in String variable
                val inputString = bufferedReader.use { it.readText() }
                //Convert the Json File to Gson Object
                return gson.fromJson(inputString, Contacts::class.java)
            }
            return Contacts()
        }

        fun addNewContactToFile(context: Context, contact: Contact) {
            //Reading current file
            var contacts: Contacts = readFomFile(context)
            if (contacts.list.any{ c -> c.id == contact.id }) {
                Toast.makeText(context, R.string.id_already_exists, Toast.LENGTH_LONG).show()
            } else {
                //Adding New Contact
                contacts = contacts.addContact(contact)
                contacts.saveToJSONFile(context)
                val intent = Intent(context, ContactActivity::class.java)
                context.startActivity(intent)
            }
        }

        fun deleteContactFromFile(context: Context, contact: Contact) {
            //Reading current file
            val contacts: Contacts = readFomFile(context)
            //Delete contact
            val update = contacts.deleteContact(contact)
            //Save updated list
            update.saveToJSONFile(context)
        }

        fun updateContactInFile(context: Context, old: Contact, new: Contact) {
            if (old.id == new.id && old.name == new.name) {
                Toast.makeText(context, R.string.no_changes, Toast.LENGTH_LONG).show()
            } else {
                //Reading current file
                var contacts: Contacts = readFomFile(context)
                if (old.id != new.id && contacts.list.any{ c -> c.id == new.id }) {
                    Toast.makeText(context, R.string.id_already_exists, Toast.LENGTH_LONG).show()
                } else {
                    //Delete old contact
                    contacts = contacts.deleteContact(old)
                    //Adding New Contact
                    contacts = contacts.addContact(new)
                    contacts.saveToJSONFile(context)
                    val intent = Intent(context, ContactActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }
}
