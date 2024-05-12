package com.example.notesapp


import NotesAdapter
import NotesDatabaseHelper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.databinding.ActivityMainBinding
import java.util.*
import androidx.appcompat.widget.SearchView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: NotesDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var allTasks: List<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)
        allTasks = db.getAllNotes()
        notesAdapter = NotesAdapter(allTasks, this)

        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = notesAdapter

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        // Implement search functionality


// Inside onCreate method
                binding.searchEditText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        filter(newText)
                        return true
                    }
                })
        binding.sortButton.setOnClickListener {
            allTasks = allTasks.sortedByDescending { it.priority }
            notesAdapter.refreshData(allTasks)
        }

    }

    private fun filter(query: String?) {
        val filteredTasks = mutableListOf<Task>()
        if (query.isNullOrEmpty()) {
            filteredTasks.addAll(allTasks)
        } else {
            val searchText = query.toLowerCase(Locale.getDefault())
            allTasks.forEach { note ->
                if (note.title.toLowerCase(Locale.getDefault()).contains(searchText) ||
                    note.content.toLowerCase(Locale.getDefault()).contains(searchText)
                ) {
                    filteredTasks.add(note)
                }
            }
        }
        notesAdapter.refreshData(filteredTasks)
    }

    override fun onResume() {
        super.onResume()
        allTasks = db.getAllNotes()
        notesAdapter.refreshData(allTasks)
    }
}
