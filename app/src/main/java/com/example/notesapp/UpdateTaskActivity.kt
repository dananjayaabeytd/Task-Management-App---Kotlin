package com.example.notesapp

import NotesDatabaseHelper
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityUpdateNoteBinding
import java.util.*

class UpdateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateNoteBinding
    private lateinit var db: NotesDatabaseHelper
    private var noteId: Int = -1
    private var selectedDeadline: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        noteId = intent.getIntExtra("note_id", -1)

        if (noteId == -1) {
            finish()
            return
        }

        val note = db.getNoteByID(noteId)
        binding.UpdateTitleEditText.setText(note.title)
        binding.UpdateContentEditText.setText(note.content)

        // Check if the deadline is not null or empty before setting it
        if (note.deadline.isNotEmpty()) {
            binding.UpdateDeadlineEditText.setText(note.deadline)
        }

        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.UpdateTitleEditText.text.toString()
            val newContent = binding.UpdateContentEditText.text.toString()
            val priority = binding.updatePrioritySpinner.selectedItemPosition

            // Check if deadline is selected
            if (selectedDeadline.isEmpty()) {
                Toast.makeText(this, "Please select a deadline", Toast.LENGTH_SHORT).show()
            } else {
                val updatedTask = Task(noteId, newTitle, newContent, priority, selectedDeadline)
                db.updateNote(updatedTask)
                finish()
                Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
            }
        }

        val priorityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.priority_levels,
            android.R.layout.simple_spinner_item
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.updatePrioritySpinner.adapter = priorityAdapter

        binding.updatePrioritySpinner.setSelection(note.priority)

        // Implement deadline selection using DatePickerDialog
        binding.UpdateDeadlineButton.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Month returned by DatePickerDialog is zero-based, so add 1 to match Calendar.MONTH
                val formattedDeadline = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                selectedDeadline = formattedDeadline
                binding.UpdateDeadlineEditText.setText(formattedDeadline)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}
