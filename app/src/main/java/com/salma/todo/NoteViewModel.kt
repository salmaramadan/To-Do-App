package com.salma.todo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.salma.data.local.NoteEntity
import com.salma.data.repo.Repo
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: Repo = Repo(application)
    val allNotes: LiveData<List<NoteEntity>> = repo.notes.asLiveData()

    fun saveNote(note: NoteEntity) {
        viewModelScope.launch {
            repo.saveNote(note)
        }
    }

    fun deleteNoteById(noteId: Int) {
        viewModelScope.launch {
            repo.deleteNoteById(noteId)
        }
    }

    fun update(note: NoteEntity) {
        viewModelScope.launch {
            repo.update(note)
        }
    }
}
