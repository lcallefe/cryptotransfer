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
import android.widget.Filter
import android.widget.Filterable
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.gov.sp.fatec.cryptotransfer.util.*
import kotlin.collections.ArrayList

class ContactAdapter (private val context: Context,
                      private val myDataset: ArrayList<Contact?>,
                      private val cellClickListener: CellOnClickListener) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(), Filterable {
    
    var filteredList = ArrayList<Contact?>()

    init {
        filteredList = myDataset
    }

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var contactName: TextView = view.findViewById(R.id.contactName) as TextView
        internal var contactId: TextView = view.findViewById(R.id.contactId) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_contact, parent, false) as View
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.contactName.text = filteredList[position]!!.name
        holder.contactId.text = filteredList[position]!!.id

        val selectedContact = filteredList[position] as Contact
//        val popupMenu = PopupMenu(context, holder.itemView)
//        popupMenu.inflate(R.menu.rv_item_contact_menu)
//        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
//            override fun onMenuItemClick(item: MenuItem): Boolean {
//                when (item.itemId) {
//                    R.id.item_menu_send -> {
//                        return true
//                    }
//                    R.id.item_menu_edit -> {
////                        cellClickListener.onCellClickListener(selectedContact)
//                        return true
//                    }
//                    R.id.item_menu_delete -> {
////                        removeItem(position)
//                        return true
//                    }
//                    else -> return false
//                }
//            }
//        })
//        popupMenu.show()
        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(selectedContact)
        }
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filteredList = myDataset
                } else {
                    filteredList = myDataset.filter { it != null &&
                        (it.name.contains(charSearch, ignoreCase = true) || it.id.contains(charSearch, ignoreCase = true))
                    } as ArrayList<Contact?>
                }
                val filterResult = FilterResults()
                filterResult.values = filteredList
                return filterResult
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<Contact?>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (filteredList[position]!!.showMenu)
            return 1
        else
            return 0
    }

    fun showMenu(position: Int) {
        filteredList.forEach { c -> c!!.showMenu = false }
        filteredList[position]?.showMenu = true
        notifyDataSetChanged()
    }

    fun isMenuShown(): Boolean {
        for (c in filteredList) {
            if (c!!.showMenu)
                return true
        }
        return false
    }

    fun closeMenu() {
        filteredList.forEach { c -> c!!.showMenu = false }
        notifyDataSetChanged()
    }

    private fun removeItem(position: Int) {
        val deleted = filteredList[position] as Contact
        deleteContactFromFile(context, deleted)
        filteredList.removeAt(position)
        notifyItemRemoved(position)
    }


    private fun sendToContact(contact: Contact) {
        /*** pass selected contactID to MainActivity ***/
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("selectedReceiverID", contact.id)
        context.startActivity(intent)
    }

    private fun editContact(contact: Contact) {
        /*** pass selected contactID to NewContactActivity ***/
        val intent = Intent(context, NewContactActivity::class.java)
        intent.putExtra("updateContact", "update")
        intent.putExtra("selectedContactName", contact.name)
        intent.putExtra("selectedContactID", contact.id)
        context.startActivity(intent)
    }
}
