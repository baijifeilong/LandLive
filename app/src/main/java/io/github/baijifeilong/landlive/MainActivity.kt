package io.github.baijifeilong.landlive

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onItemClick
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var hell: View
    private lateinit var heaven: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()

        val inputs = mutableListOf<Char>()

        val text = Scanner(resources.openRawResource(R.raw.playlist)).useDelimiter("\\Z").next()
        text.split("\n").forEach { println(it) }
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
                                            hell.visibility = View.GONE
                                            heaven.visibility = View.VISIBLE
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
            listView {
                heaven = this
                visibility = View.GONE
                adapter = object : ArrayAdapter<Pair<String, String>>(this@MainActivity, android.R.layout.simple_list_item_1, android.R.id.text1) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                        return super.getView(position, convertView, parent).apply {
                            this.find<TextView>(android.R.id.text1).text = getItem(position).first
                        }
                    }
                }.apply {
                    text.split("\n").forEach {
                        val parts = it.split(" ")
                        add(parts.first() to parts.last())
                    }
                    onItemClick { parent, view, position, id ->
                        startActivity(Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(Uri.parse(getItem(position).second), "application/x-mpegURL")
                            putExtra("title", getItem(position).first)
                        })
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
