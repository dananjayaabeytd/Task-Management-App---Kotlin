import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.MainActivity
import com.example.notesapp.Task
import com.example.notesapp.R
import com.example.notesapp.UpdateTaskActivity

class NotesAdapter(private var tasks: List<Task>, private val context: MainActivity) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val db: NotesDatabaseHelper = NotesDatabaseHelper(context)

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val priorityTextView: TextView = itemView.findViewById(R.id.priorityTextView)
        val deadlineTextView: TextView = itemView.findViewById(R.id.deadlineTextView) // Add reference to deadlineTextView
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = tasks[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content
        holder.priorityTextView.text = context.resources.getStringArray(R.array.priority_levels)[note.priority] // Set priority level text
        holder.deadlineTextView.text = note.deadline // Set deadline text

        // Set background color of the CardView based on priority
        val backgroundColor = when (note.priority) {
            0 -> ContextCompat.getColor(context, R.color.white) // Low priority
            1 -> ContextCompat.getColor(context, R.color.green) // Medium priority
            2 -> ContextCompat.getColor(context, R.color.yellow) // High priority
            else -> ContextCompat.getColor(context, R.color.red) // Default color
        }
        holder.cardView.setCardBackgroundColor(backgroundColor)

        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateTaskActivity::class.java).apply {
                putExtra("note_id", note.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            db.deleteNote(note.id)
            refreshData(db.getAllNotes())
            Toast.makeText(holder.itemView.context, "Note Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshData(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}
