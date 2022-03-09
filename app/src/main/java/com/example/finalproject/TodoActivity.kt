package com.example.finalproject

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*


class TodoActivity : AppCompatActivity() {

    var auth = FirebaseAuth.getInstance();
    private var db = FirebaseFirestore.getInstance()
    private var todoList: MutableList<Todo> = ArrayList()
    private var dbContext =  db.collection("todos")
    var adapter = TodoAdapter(this, todoList,dbContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)
        val btnAdd = findViewById<Button>(R.id.buttonAddTodo)

        btnAdd.setOnClickListener {
            val txt = findViewById<EditText>(R.id.editTextTodo)
            if (txt.text.toString().isEmpty()) {
                Toast.makeText(this, "Input text", Toast.LENGTH_SHORT).show()
            }
            val todo: Todo = Todo()
            todo.task = txt.text.toString()
            todo.user = auth.currentUser!!.email.toString()
            saveTodo(todo)
            txt.text = null
            adapter.notifyDataSetChanged()
        }

        val rView = findViewById<RecyclerView>(R.id.recyclerViewTodo)
        rView.layoutManager = LinearLayoutManager(this);
        rView.adapter = adapter
        retrieveTasks()
        adapter.notifyDataSetChanged()
    }

    private fun retrieveTasks() = CoroutineScope(Dispatchers.IO).async {
        try {
            val querySnapshot =  dbContext
                .whereEqualTo("user", auth.currentUser?.email)
                .get()
            querySnapshot.addOnSuccessListener { documents ->
                for(document in documents) {
                    val todo = document.toObject<Todo>()
                    Log.d(TAG, todo.task)
                    todoList.add(todo)
                }
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(this@TodoActivity,"loaded",Toast.LENGTH_SHORT).show()
            }

        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
               Toast.makeText(this@TodoActivity,"error",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveTodo(todo: Todo) = CoroutineScope(Dispatchers.IO).async {
        try {
            dbContext.add(todo).addOnSuccessListener { result ->
                todo.id =  result.id
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(this@TodoActivity, "Successfully saved data.", Toast.LENGTH_LONG).show()
                todoList.add(todo)
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@TodoActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }


}