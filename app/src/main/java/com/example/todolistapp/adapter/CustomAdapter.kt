package com.example.todolistapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.R
import com.example.todolistapp.ToDo

class CustomAdapter(val context:Context, val todo:List<ToDo>) :RecyclerView.Adapter<CustomAdapter.CustomViewHolder>(){

    var tracker: SelectionTracker<ToDo>? = null



    fun getItem(position: Int):ToDo = todo[position]
    fun getPosition(goal: String) = todo.indexOfFirst { it.goal == goal }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo_list, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val toDo = todo[position]

        if (holder is CustomViewHolder) {
            holder.apply {
                tvGoal.text = toDo.goal
                tvPlace.text = toDo.place
                tvLevel.text = toDo.level

                tracker?.let {
                    setBack(toDo, it.isSelected(toDo))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return todo.size
    }

    inner class CustomViewHolder(var view: View):RecyclerView.ViewHolder(view) {
        val tvGoal = view.findViewById<TextView>(R.id.tv_goal)
        val tvPlace = view.findViewById<TextView>(R.id.tv_place)
        val tvLevel = view.findViewById<TextView>(R.id.tv_level)

        val todoLayout = view.findViewById<LinearLayout>(R.id.todo_layout)

        fun setBack(toDo: ToDo, isActiveated: Boolean = false) {
            tvGoal.text = toDo.goal
            view.isActivated = isActiveated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<ToDo> =
            object : ItemDetailsLookup.ItemDetails<ToDo>() {
                override fun getPosition(): Int {
                    return adapterPosition
                }

                override fun getSelectionKey(): ToDo? {
                    return todo[position]
                }
            }


    }
}

class MyItemKeyProvider(private val adapter: CustomAdapter): ItemKeyProvider<ToDo>(SCOPE_CACHED){
    override fun getKey(position: Int): ToDo? {
        return adapter.getItem(position)
    }

    override fun getPosition(key: ToDo): Int {
        return adapter.getPosition(key.goal)
    }

}

class MyItemDetailsLookup(private val recyclerView: RecyclerView): ItemDetailsLookup<ToDo>(){
    override fun getItemDetails(e: MotionEvent): ItemDetails<ToDo>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)

        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as CustomAdapter.CustomViewHolder).getItemDetails()
        }

        return null
    }

}