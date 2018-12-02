package io.github.baijifeilong.landlive

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var hell: View
    private lateinit var heaven: View
    private var freedom = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()

        val inputs = mutableListOf<Char>()

        val text = Scanner(resources.openRawResource(R.raw.playlist)).useDelimiter("\\A").next()
        val playlist = text.split("\n").map { it.split(" ") }.map { it.first() to it.last() }
        setContentView(verticalLayout {
            relativeLayout {
                hell = this
                verticalLayout {
                    val chars = arrayOf('7', '8', '9', '4', '5', '6', '1', '2', '3', '0', '.', '@')
                    for (i in 0..3) {
                        linearLayout {
                            for (j in 0..2) {
                                button(chars[i * 3 + j].toString()) {
                                    textSize = (textSize * 1.5).toFloat()
                                    onClick {
                                        inputs.add((it as Button).text[0])
                                        if (inputs.size >= 4 && inputs.reversed().subList(0, 4).joinToString("") == "4698") {
                                            freedom = true
                                            recreate()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }.lparams {
                    addRule(RelativeLayout.CENTER_HORIZONTAL)
                    addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    bottomMargin = 120
                }
            }.lparams(matchParent, matchParent)
            addView(RecyclerView(this@MainActivity).apply {
                heaven = this
                val landscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                layoutManager = StaggeredGridLayoutManager(if (landscape) 4 else 2, StaggeredGridLayoutManager.VERTICAL)
                adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                        return object : RecyclerView.ViewHolder(LayoutInflater.from(this@MainActivity).inflate(android.R.layout.simple_list_item_1, parent, false).apply {
                            with(TypedValue()) {
                                context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
                                backgroundResource = resourceId
                            }
                        }) {}.apply {
                            itemView.onClick {
                                val pos = (heaven as RecyclerView).getChildLayoutPosition(itemView)
                                startActivity(Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(Uri.parse(playlist.get(pos).second), "application/x-mpegURL")
                                    putExtra("title", "LandLive(${pos + 1}) ${playlist.get(pos).first}")
                                })
                            }
                        }
                    }

                    override fun getItemCount(): Int {
                        return playlist.size
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        holder.itemView.findViewById<TextView>(android.R.id.text1).text = "${position + 1}. ${playlist[position].first}"
                    }
                }
            })
        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putBoolean("freedom", freedom)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        freedom = savedInstanceState!!.getBoolean("freedom", false)
    }

    override fun onResume() {
        super.onResume()
        hell.visibility = if (freedom) View.GONE else View.VISIBLE
        heaven.visibility = if (!freedom) View.GONE else View.VISIBLE
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
