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
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import br.gov.sp.fatec.cryptotransfer.util.*
import kotlin.collections.ArrayList

class ContactAdapter (private val context: Context,
                      private val myDataset: ArrayList<Contact>,
                      private val cellClickListener: CellOnClickListener) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(), Filterable {
    
    var filteredList = ArrayList<Contact>()

    init {
        filteredList = myDataset
    }

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var contactName = view.findViewById(R.id.contactName) as TextView
        internal var contactId = view.findViewById(R.id.contactId) as TextView
        internal var btnItemContactMenu = view.findViewById(R.id.btnItemContactMenu) as ImageButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_contact, parent, false) as View
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.contactName.text = filteredList[position].name
        holder.contactId.text = filteredList[position].id

//        val selectedContact = filteredList[position] as Contact
//        holder.itemView.setOnClickListener {
//            cellClickListener.onCellClickListener(selectedContact)
//        }
        holder.btnItemContactMenu.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
//            popupMenu.menuInflater.inflate(R.menu.rv_item_contact_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.item_menu_send -> {
                        sendToContact(position)
                        true
                    }
                    R.id.item_menu_edit -> {
                        editItem(position)
                        true
                    }
                    R.id.item_menu_delete -> {
                        removeItem(position)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.rv_item_contact_menu)
            popupMenu.show()
        }
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filteredList == if (charSearch.isEmpty()) {
                    myDataset
                } else {
                    myDataset.filter {
                        it.name.contains(charSearch, ignoreCase = true) || it.id.contains(charSearch, ignoreCase = true)
                    } as ArrayList<Contact>
                }
                val filterResult = FilterResults()
                filterResult.values = filteredList
                return filterResult
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<Contact>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (filteredList[position].showMenu)
            return 1
        else
            return 0
    }

    private fun removeItem(position: Int) {
        val deleted = filteredList[position] as Contact
        Contacts.deleteContactFromFile(context, deleted)
        filteredList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    private fun sendToContact(position: Int) {
        val selected = filteredList[position] as Contact
        /*** pass selected contactID to MainActivity ***/
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("selectedReceiverID", selected.id)
        context.startActivity(intent)
    }

    private fun editItem(position: Int) {
        val selected = filteredList[position] as Contact
        /*** pass selected contactID to NewContactActivity ***/
        val intent = Intent(context, NewContactActivity::class.java)
        intent.putExtra("updateContact", "update")
        intent.putExtra("selectedContactName", selected.name)
        intent.putExtra("selectedContactID", selected.id)
        context.startActivity(intent)
    }
}
