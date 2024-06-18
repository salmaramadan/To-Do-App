package com.salma.todo.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.salma.todo.R

class GuideActivity : AppCompatActivity() {
    private lateinit var guideImg: ImageView
    private lateinit var guideTV: TextView
    private lateinit var guideBtn: Button
    private var clickCount=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_guide)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initComponent()
    }

    private fun initComponent() {
        guideImg=findViewById(R.id.guideImg)
        guideTV=findViewById(R.id.guideTV)
        guideBtn=findViewById(R.id.guideBtn)
    }

    fun nextClick(view: View) {
        clickCount++
        when(clickCount){
            1->{
                guideImg.setImageResource(R.drawable.write)
                guideTV.text="Start writting your thoughts"
            }
            2->{
                guideImg.setImageResource(R.drawable.save)
                guideTV.text="Now you can save it"
            }
            3->{
                guideImg.setImageResource(R.drawable.delete)
                guideTV.text="Or delete"
            }
            4->{
                guideBtn.text="Create your first note"
                val noteIntent= Intent(this, NoteActivity::class.java)
                startActivity(noteIntent)
                finish()
            }
        }
    }


}