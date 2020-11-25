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

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.gov.sp.fatec.cryptotransfer.user.decrypt
import br.gov.sp.fatec.cryptotransfer.user.getFingerprint
import br.gov.sp.fatec.cryptotransfer.util.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*

data class Transfer(
    val receiver: String,
    val time: Long,
    val mimeType: String,
    val name: String,
    val received: Boolean
)

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: HistoryAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var history: List<Transfer> = ArrayList(0)

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_history)

        val actionBar = supportActionBar
        actionBar!!.setTitle(R.string.history)

//        var fingerprint = ""
//        getFingerprint(this) { fingerprint = it }

        getFingerprint(this) { fingerprint ->
            FirebaseFirestore.getInstance().collection("transfer").whereEqualTo("sender", fingerprint)
                .get().addOnSuccessListener {
                    val transfers = HashSet<Transfer>()
                    it.forEach { document ->
                        println(document.data["name"] as String)
                        decrypt(this, document.data["name"] as String) {
                            val name = String(it)
                            decrypt(this, document.data["mimeType"] as String) {
                                val mimeType = String(it)
                                val time = document.data["time"] as Long
                                val receiver = document.data["receiver"] as String
                                FirebaseStorage.getInstance().getReference(receiver)
                                    .child("$fingerprint/$time").downloadUrl.addOnSuccessListener {
                                        transfers.add(Transfer(receiver, time, mimeType, name, false))
                                        history = transfers.sortedBy { it.time }
                                    }.addOnFailureListener {
                                        transfers.add(Transfer(receiver, time, mimeType, name, true))
                                        history = transfers.sortedBy { it.time }
                                    }
                            }
                        }
                    }
                }
        }
//        val rootRef = FirebaseFirestore.getInstance()
//        val query = rootRef!!.collection("transfer")
//            .whereEqualTo("receiver", fingerprint)
//            .orderBy("time", Query.Direction.DESCENDING)
//        val options = FirestoreRecyclerOptions.Builder<TransferModel>().setQuery(query, TransferModel::class.java).build()

//        var history = History(fingerprint)
//        readData(this, fingerprint, object: MyCallback {
//            override fun onCallback(value: ArrayList<Transfer>) {
//                value.sortByDescending { it.sendDate }
//                history.transferList = value
//            }
//        })

        recyclerView = findViewById(R.id.rv_history)
        viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = viewManager
        viewAdapter = HistoryAdapter(history as ArrayList<Transfer>)
        recyclerView.adapter = viewAdapter

        /*** Search Bar functionality ***/
        findViewById<SearchView>(R.id.sv_search_history).setOnQueryTextListener(object: SearchView.OnQueryTextListener {
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

//    private inner class FirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<TransferModel>) : FirestoreRecyclerAdapter<TransferModel, HistoryViewHolder>(options) {
//        override fun onBindViewHolder(historyViewHolder: HistoryViewHolder, position: Int, transferModel: TransferModel) {
//            historyViewHolder.setType(transferModel.mimeType)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
//            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_history, parent, false)
//            return HistoryViewHolder(view)
//        }
//    }
//
//    private inner class HistoryViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {
//        internal fun setType(type: String) {
//            val textView = view.findViewById<TextView>(R.id.tv_item_transfer_type)
//            textView.text = type
//        }
//        internal fun setReceiverName(receiverName: String) {
//            val textView = view.findViewById<TextView>(R.id.tv_item_name)
//            textView.text = receiverName
//        }
//        internal fun setReceiverId(receiverId: String) {
//            val textView = view.findViewById<TextView>(R.id.tv_item_id)
//            textView.text = receiverId
//        }
//        internal fun setFile(file: String) {
//            val textView = view.findViewById<TextView>(R.id.tv_item_file)
//            textView.text = file
//        }
//        internal fun setTime(time: Long) {
//            val textView = view.findViewById<TextView>(R.id.tv_item_date)
//            textView.text = time.toString()
//        }
//    }
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
            holder.type.text = filteredList[position].mimeType
            holder.receiverName.text = filteredList[position].receiver
            holder.receiverId.text = filteredList[position].receiver
            holder.file.text = filteredList[position].name
            holder.time.text = filteredList[position].time.toString()
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
                            (it.receiver != null && it.receiver!!.contains(charSearch, ignoreCase = true))
                                    || it.receiver.contains(charSearch, ignoreCase = true)
                                    || it.name.contains(charSearch, ignoreCase = true)
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
}
