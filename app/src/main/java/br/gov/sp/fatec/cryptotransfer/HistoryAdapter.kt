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
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.gov.sp.fatec.cryptotransfer.util.Transfer

class HistoryAdapter(private val myDataset: ArrayList<Transfer>):
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>(), Filterable {

    var filteredList = ArrayList<Transfer>()

    init {
        filteredList = myDataset
    }

    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var type = view.findViewById(R.id.tv_item_transfer_type) as TextView
        internal var receiverName = view.findViewById(R.id.tv_item_name) as TextView
        internal var receiverId = view.findViewById(R.id.tv_item_id) as TextView
        internal var file = view.findViewById(R.id.tv_item_file) as TextView
        internal var time = view.findViewById(R.id.tv_item_date) as TextView
//        internal var btnItemHistoryMenu = view.findViewById(R.id.btnItemHistoryMenu) as Button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_history, parent, false) as View
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.type.text = filteredList[position].transferType
        holder.receiverName.text = filteredList[position].receiverName
        holder.receiverId.text = filteredList[position].receiverId
        holder.file.text = filteredList[position].fileName
        holder.time.text = filteredList[position].sendDate.toString()
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filteredList = if (charSearch.isEmpty()) {
                    myDataset
                } else {
                    myDataset.filter {
                        (it.receiverName != null && it.receiverName!!.contains(charSearch, ignoreCase = true))
                            || it.receiverId.contains(charSearch, ignoreCase = true)
                            || it.fileName.contains(charSearch, ignoreCase = true)
                    } as ArrayList<Transfer>
                }
                val filterResult = FilterResults()
                filterResult.values = filteredList
                return filterResult
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<Transfer>
                notifyDataSetChanged()
            }
        }
    }
}