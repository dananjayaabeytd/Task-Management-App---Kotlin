package com.example.notesapp

import NotesDatabaseHelper
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityAddNoteBinding
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var db: NotesDatabaseHelper
    private lateinit var deadlineButton: Button
    private var selectedDeadline: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        // Initialize deadline button
        deadlineButton = binding.deadlineButton
        deadlineButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Set up spinner for priority selection
        val priorityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.priority_levels,
            android.R.layout.simple_spinner_item
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.prioritySpinner.adapter = priorityAdapter

        // Save button click listener
        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val priority = binding.prioritySpinner.selectedItemPosition

            // Check if deadline is selected
            if (selectedDeadline.isEmpty()) {
                Toast.makeText(this, "Please select a deadline", Toast.LENGTH_SHORT).show()
            } else {
                val task = Task(0, title, content, priority, selectedDeadline)
                db.insertNote(task)
                finish()
                Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, dayOfMonth ->
                // Handle the selected date here
                selectedDeadline = "$dayOfMonth/${selectedMonth + 1}/$selectedYear"
                // Update the text of the button with the selected date
                deadlineButton.text = selectedDeadline
            },
            year, month, dayOfMonth
        )
        datePickerDialog.show()
    }
}
