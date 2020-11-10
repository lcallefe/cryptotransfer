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

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_contact.view.*
import java.util.*
import kotlin.collections.ArrayList

class ContactAdapter (private val myDataset: ArrayList<Contact?>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(), Filterable {
    
    var filteredList = ArrayList<Contact?>()

    init {
        filteredList = myDataset
    }

    inner class ContactViewHolder(cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        internal var contactName: TextView = cardView.findViewById(R.id.contactName) as TextView
        internal var contactId: TextView = cardView.findViewById(R.id.contactId) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_contact, parent, false) as CardView
        return ContactViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.contactName.text = filteredList[position]?.name
        holder.contactId.text = filteredList[position]?.id

//        holder.
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filteredList = myDataset
                } else {
//                    val resultList = ArrayList<Contact?>()
//                    myDataset.forEach { c ->
//                        if (c != null && (c.name.contains(charSearch, ignoreCase = true) || c.id.contains(charSearch, ignoreCase = true))) {
//                            resultList.add(c)
//                        }
//                    }
//                    filteredList = resultList
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
}
