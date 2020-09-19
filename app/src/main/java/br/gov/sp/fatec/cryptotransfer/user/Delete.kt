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

package br.gov.sp.fatec.cryptotransfer.user

import android.content.Context
import android.content.Context.MODE_PRIVATE

private fun retrieveTime(context: Context) =
    context.getSharedPreferences("user", MODE_PRIVATE).getLong("wrongPin", Long.MIN_VALUE)

/**
 * Returns 0 if the current user can be deleted,
 * a negative number if it can't be deleted or
 * the remaining minutes until it can be deleted
 */
fun can(context: Context): Short {
    val time = retrieveTime(context)
    return if (isSet(context) && time != Long.MIN_VALUE) {
        val millis = System.currentTimeMillis()
        if (time < millis.minus(24 * 60 * 60 * 1000))
            0.toShort()
        else
            millis.minus(time).div(1000).div(60).toShort()
    } else {
        Short.MIN_VALUE
    }
}

fun wrongPin(context: Context) {
    if (retrieveTime(context) == Long.MIN_VALUE)
        with(context.getSharedPreferences("user", MODE_PRIVATE).edit()) {
            putLong("wrongPin", System.currentTimeMillis())
            commit()
        }
}

fun delete(context: Context) = if (can(context) == 0.toShort()) {
    with(context.getSharedPreferences("user", MODE_PRIVATE).edit()) {
        clear()
        commit()
    }
    with(context.getSharedPreferences("keys", MODE_PRIVATE).edit()) {
        clear()
        commit()
    }
} else throw IllegalStateException("User can't yet be deleted")