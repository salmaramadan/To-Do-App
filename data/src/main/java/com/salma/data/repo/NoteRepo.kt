package com.salma.data.repo

import android.content.Context
import com.salma.data.local.NoteDatabase
import com.salma.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow

class Repo(context: Context) {
    private val noteDao = NoteDatabase.getDatabase(context).noteDao()
    val notes: Flow<List<NoteEntity>> = noteDao.getAllNotes()

    suspend fun saveNote(noteEntity: NoteEntity) {
        noteDao.insert(noteEntity)
    }

    suspend fun deleteNoteById(noteId: Int) {
        noteDao.deleteById(noteId)
    }

    suspend fun update(note: NoteEntity) {
        noteDao.update(note)
    }
}
