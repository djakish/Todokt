package com.example.finalproject

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*


class TodoAdapter(val context: Context, val todos: MutableList<Todo>, val dbContext: CollectionReference): RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.todo_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo: Todo = todos.get(position);
        holder.checkBox.text = todo.task
        holder.checkBox.isChecked = todo.status
        holder.checkBox.setOnCheckedChangeListener { _, _ ->
            todo.status = !todo.status
                try {
                    dbContext.document(todo.id).update("status",todo.status);
                } catch(e: Exception) {
                    Log.v(" ","error")
                }

        }

        holder.btn.setOnClickListener {
                try {
                    dbContext.document(todo.id).delete()
                    Log.v("THIS IS SUPPOSED TO BE ",todo.id)
                        todos.remove(todo)
                        notifyDataSetChanged()

                } catch(e: Exception) {
                        Log.v(" ","error")
                }

        }
    }


    override fun getItemCount(): Int {
        return todos.size
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val checkBox: CheckBox
    val btn: ImageButton

    init {
        checkBox = view.findViewById<CheckBox>(R.id.checkBoxItem)
        btn = view.findViewById<ImageButton>(R.id.imageButtonItem)
    }
}

