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

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {

    lateinit var iv_plane: ImageView
    lateinit var iv_path: ImageView
    lateinit var iv_name: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()

        iv_plane = findViewById(R.id.ss_iv_logo_paperplane)
        iv_path = findViewById(R.id.ss_iv_logo_path)
        iv_name = findViewById(R.id.ss_iv_logo_app_name)


        val animationPlane: Animation = AnimationUtils.loadAnimation(this, R.anim.opening_icon)
        val animationName: Animation = AnimationUtils.loadAnimation(this, R.anim.opening_app_name)

        iv_plane.animation = animationPlane
        iv_path.animation = animationName
        iv_name.animation = animationName

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            kotlin.run {
                val intent =
                    Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//                finish()
//                var pairs = ArrayList<Pair<View, String>>()
//                pairs[0] = Pair(iv_plane, "logo_paperplane")
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    iv_plane,
                    "logo_paperplane"
                )
                startActivity(intent, options.toBundle())
            }
        }, 3000)
    }
}