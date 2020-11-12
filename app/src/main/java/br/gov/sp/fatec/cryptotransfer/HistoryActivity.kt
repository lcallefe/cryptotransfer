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
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.gov.sp.fatec.cryptotransfer.user.getFingerprint
import br.gov.sp.fatec.cryptotransfer.util.*
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

        val history: ArrayList<Transfer?> = ArrayList()
        history.add(Transfer("Recebido", fingerprint, fingerprint,fingerprint,"","",""))
//        val history: ArrayList<Transfer?> = getReceivedHistory(this, fingerprint).history

        recyclerView = findViewById(R.id.rv_history)
        viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = viewManager
        viewAdapter = HistoryAdapter(history)
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
}