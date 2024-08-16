package com.ristu.todoapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ristu.todoapp.R
import com.ristu.todoapp.databinding.TodoItemLayoutBinding
import com.ristu.todoapp.model.TodoModel

class TodoRecyclerAdapter(
    private val onClickTodo: (uid: Long) -> Unit,
    private val onClickDelete: (uid: Long) -> Unit
) :
    RecyclerView.Adapter<TodoRecyclerAdapter.ViewHolder>() {

    private var itemList: List<TodoModel>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setTodoData(itemList: List<TodoModel>) {
        this.itemList = itemList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: TodoItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.apply {
                titleTextView.text = itemList?.get(adapterPosition)?.title
                bodyTextView.text = itemList?.get(adapterPosition)?.body
                onItemClick = View.OnClickListener { view ->
                    when (view.id) {
                        R.id.delete_todo_button -> {
                            itemList?.get(adapterPosition)?.uid?.let { onClickDelete(it) }
                        }

                        R.id.todo_item_layout -> {
                            itemList?.get(adapterPosition)?.uid?.let { onClickTodo(it) }

                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TodoItemLayoutBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }


    override fun getItemCount() = itemList?.size ?: 0
}