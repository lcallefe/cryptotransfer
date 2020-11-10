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

import android.app.Activity
import android.content.Intent
import br.gov.sp.fatec.cryptotransfer.ContactActivity
import br.gov.sp.fatec.cryptotransfer.MainActivity
import br.gov.sp.fatec.cryptotransfer.R
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener

fun set(activity: Activity) = OnNavigationItemSelectedListener {
    when (it.itemId) {
        R.id.contactFragment -> {
            activity.startActivity(Intent(activity, ContactActivity::class.java))
            activity.finish()
            return@OnNavigationItemSelectedListener true
        }
        R.id.sendFragment -> {
            activity.startActivity(Intent(activity, MainActivity::class.java))
            activity.finish()
            return@OnNavigationItemSelectedListener true
        }
        R.id.historyFragment -> {
            return@OnNavigationItemSelectedListener true
        }
    }
    false
}