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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.gov.sp.fatec.cryptotransfer.user.getFingerprint
import br.gov.sp.fatec.cryptotransfer.util.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: HistoryAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val actionBar = supportActionBar
        actionBar!!.setTitle(R.string.history)

        var fingerprint = ""
        getFingerprint(this) { fingerprint = it }

//        val rootRef = FirebaseFirestore.getInstance()
//        val query = rootRef!!.collection("transfer")
//            .whereEqualTo("receiver", fingerprint)
//            .orderBy("time", Query.Direction.DESCENDING)
//        val options = FirestoreRecyclerOptions.Builder<TransferModel>().setQuery(query, TransferModel::class.java).build()

        var history = History(fingerprint)
        readData(this, fingerprint, object: MyCallback {
            override fun onCallback(value: ArrayList<Transfer>) {
                value.sortByDescending { it.sendDate }
                history.transferList = value
            }
        })

        recyclerView = findViewById(R.id.rv_history)
        viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = viewManager
//        viewAdapter = HistoryAdapter(history.findNames(this).transferList)
//        recyclerView.adapter = viewAdapter

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
}