package com.salma.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.salma.data.local.NoteEntity
import java.util.Calendar

class AddNoteActivity : AppCompatActivity() {
    private lateinit var headEditText: EditText
    private lateinit var bodyEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var scheduleButton: Button

    private var scheduledTime: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        headEditText = findViewById(R.id.headEditText)
        bodyEditText = findViewById(R.id.bodyEditText)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        scheduleButton = findViewById(R.id.scheduleButton)

        scheduleButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                TimePickerDialog(this, { _, hour, minute ->
                    scheduledTime = Calendar.getInstance().apply {
                        set(year, month, day, hour, minute)
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        saveButton.setOnClickListener {
            val head = headEditText.text.toString()
            val body = bodyEditText.text.toString()

            if (head.isNotEmpty() && body.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("noteHead", head)
                resultIntent.putExtra("noteBody", body)
                scheduledTime?.let {
                    resultIntent.putExtra("scheduledTime", it.timeInMillis)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }

        cancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}
