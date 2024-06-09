package com.salma.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.salma.data.local.NoteEntity

class NotesAdapter(
    private var notesList: List<NoteEntity>,
    private val onNoteClickListener: (NoteEntity) -> Unit,
    private val onEditClickListener: (NoteEntity) -> Unit,
    private val onDeleteClickListener: (NoteEntity) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notesList[position]
        holder.bind(currentNote)

        holder.itemView.setOnClickListener {
            onNoteClickListener(currentNote)
        }

        holder.editIcon.setOnClickListener {
            onEditClickListener(currentNote)
        }

        holder.deleteIcon.setOnClickListener {
            onDeleteClickListener(currentNote)
        }
    }


    override fun getItemCount(): Int {
        return notesList.size
    }

    fun setNotesList(noteList: List<NoteEntity>) {
        this.notesList = noteList
        notifyDataSetChanged()
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val editIcon: ImageView = itemView.findViewById(R.id.editIcon)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)
        private val noteHeadTextView: TextView = itemView.findViewById(R.id.noteHeadTextView)
        private val noteBodyTextView: TextView = itemView.findViewById(R.id.noteBodyTextView)

        fun bind(note: NoteEntity) {
            noteHeadTextView.text = note.head
            noteBodyTextView.text = note.body
        }
    }
}
