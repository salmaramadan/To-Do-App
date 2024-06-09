package com.salma.todo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salma.data.local.NoteEntity

class NoteActivity : AppCompatActivity() {
    private val notesList = mutableListOf<NoteEntity>()
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var notesPlaceHolderImageView: ImageView
    private lateinit var addButton: Button
    private lateinit var notesAdapter: NotesAdapter

    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_note)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponent()
        setupRecyclerview()

        addButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            addNoteLauncher.launch(intent)
        }

        noteViewModel.allNotes.observe(this, Observer { notes ->
            notesList.clear()
            notesList.addAll(notes)
            notesAdapter.notifyDataSetChanged()
            notesRecyclerView.visibility = if (notesList.isEmpty()) View.GONE else View.VISIBLE
            notesPlaceHolderImageView.visibility =
                if (notesList.isEmpty()) View.VISIBLE else View.GONE
        })
    }

    private val addNoteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    val noteHead = data.getStringExtra("noteHead") ?: return@let
                    val noteBody = data.getStringExtra("noteBody") ?: return@let
                    val scheduledTime = data.getLongExtra("scheduledTime", -1)
                    val newNote = NoteEntity(
                        head = noteHead,
                        body = noteBody,
                        scheduledTime = if (scheduledTime != -1L) java.util.Date(scheduledTime) else null
                    )
                    notesList.add(newNote)
                    notesAdapter.notifyItemInserted(notesList.size - 1)
                    noteViewModel.saveNote(newNote)
                    if (newNote.scheduledTime != null) {
                        scheduleNotification(newNote)
                    }
                }
            }
        }

    private val editNoteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    val noteId = data.getIntExtra("noteId", -1)
                    if (noteId != -1) {
                        val noteHead = data.getStringExtra("noteHead") ?: return@let
                        val noteBody = data.getStringExtra("noteBody") ?: return@let
                        val scheduledTime = data.getLongExtra("scheduledTime", -1)
                        val updatedNote = NoteEntity(
                            id = noteId,
                            head = noteHead,
                            body = noteBody,
                            scheduledTime = if (scheduledTime != -1L) java.util.Date(scheduledTime) else null
                        )
                        val index = notesList.indexOfFirst { it.id == noteId }
                        if (index != -1) {
                            notesList[index] = updatedNote
                            notesAdapter.notifyItemChanged(index)
                            noteViewModel.update(updatedNote)
                            if (updatedNote.scheduledTime != null) {
                                scheduleNotification(updatedNote)
                            }
                        }
                    }
                }
            }
        }

    private fun setupRecyclerview() {
        notesAdapter = NotesAdapter(
            notesList,
            this::onNoteClick,
            this::onEditClick,
            this::onDeleteClick
        )
        notesRecyclerView.layoutManager = LinearLayoutManager(this)
        notesRecyclerView.adapter = notesAdapter
    }

    private fun onEditClick(note: NoteEntity) {
        val intent = Intent(this, AddNoteActivity::class.java).apply {
            putExtra("isEditing", true)
            putExtra("noteId", note.id)
            putExtra("noteHead", note.head)
            putExtra("noteBody", note.body)
            note.scheduledTime?.let {
                putExtra("scheduledTime", it.time)
            }
        }
        editNoteLauncher.launch(intent)
    }


    private fun onDeleteClick(note: NoteEntity) {
        showDeleteConfirmationDialog(note)
    }

    private fun showDeleteConfirmationDialog(note: NoteEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { dialog, _ ->
                noteViewModel.deleteNoteById(note.id)
                notesList.remove(note)
                notesAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun initComponent() {
        notesRecyclerView = findViewById(R.id.notesRecyclerView)
        notesPlaceHolderImageView = findViewById(R.id.notesPlaceHolderImageView)
        addButton = findViewById(R.id.addButton)
    }

    private fun onNoteClick(note: NoteEntity) {
        val options = arrayOf("Edit", "Delete")
        if (options.isEmpty()) {
            Log.e("NoteActivity", "Options array is empty")
            return
        }
        Log.d("NoteActivity", "Options: ${options.joinToString()}")
        AlertDialog.Builder(this).setItems(options) { _, which ->
            Log.d("NoteActivity", "Selected option: $which")
            when (which) {
                0 -> onEditClick(note)
                1 -> onDeleteClick(note)
            }
        }.show()
    }


    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(note: NoteEntity) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("noteId", note.id)
            putExtra("noteHead", note.head)
            putExtra("noteBody", note.body)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            note.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        note.scheduledTime?.let {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, it.time, pendingIntent)
        }
    }
}