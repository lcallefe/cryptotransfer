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
import android.graphics.ColorSpace
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.gov.sp.fatec.cryptotransfer.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item_contact.*

class ContactActivity : AppCompatActivity(), CellOnClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ContactAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        val actionBar = supportActionBar
        actionBar!!.setTitle(R.string.contacts)

        val contactsAll: ArrayList<Contact?> = readContactsFomFile(this).contacts
        recyclerView = findViewById(R.id.rv_contacts)
        viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = viewManager
        viewAdapter = ContactAdapter(this, contactsAll, this)
        recyclerView.adapter = viewAdapter

        /*** Search Bar functionality ***/
        findViewById<SearchView>(R.id.sv_search_contact).setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewAdapter.filter.filter(newText)
                return false
            }
        })

        /*** Bottom bar navigation functionality ***/
        bottomNavigationView.setOnNavigationItemSelectedListener(set(this))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_contact_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*** new contact functionality ***/
        val intent: Intent
        when (item.itemId) {
            R.id.newContactActivity -> {
                intent = Intent(this, NewContactActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCellClickListener(data: Contact) {
//        findViewById<LinearLayout>(R.id.ll_container_menu).visibility = View.VISIBLE
        sendToContact(data)
//        editContact(data)
    }

    private fun sendToContact(contact: Contact) {
        /*** pass selected contactID to MainActivity ***/
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("selectedReceiverID", contact.id)
        startActivity(intent)
    }

    private fun editContact(contact: Contact) {
        /*** pass selected contactID to NewContactActivity ***/
        val intent = Intent(this, NewContactActivity::class.java)
        intent.putExtra("updateContact", "update")
        intent.putExtra("selectedContactName", contact.name)
        intent.putExtra("selectedContactID", contact.id)
        startActivity(intent)
    }
}