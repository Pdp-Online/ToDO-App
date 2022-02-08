package com.example.todolistapp

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.adapter.CustomAdapter
import com.example.todolistapp.adapter.MyItemDetailsLookup
import com.example.todolistapp.adapter.MyItemKeyProvider

class MainActivity : AppCompatActivity(), ActionMode.Callback {

    private var toDo:MutableList<ToDo> = mutableListOf()
    private lateinit var context: Context
    private lateinit var rvMain:RecyclerView

    private lateinit var adapter:CustomAdapter
    private var tracker: SelectionTracker<ToDo>? = null

    private var actionMode: ActionMode? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvMain = findViewById(R.id.rv_main)
        rvMain.layoutManager = GridLayoutManager(this, 1)

        refreshAdapter(prepareToDoList())
        initViews()


    }

    private fun initViews(){
        context = this



        tracker = SelectionTracker.Builder(
            "mySelection",
            rvMain,
            MyItemKeyProvider(adapter),
            MyItemDetailsLookup(rvMain),
            StorageStrategy.createParcelableStorage(ToDo::class.java)
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        adapter.tracker = tracker

        setupObserver()

    }

    private fun setupObserver() {
        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<ToDo>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    tracker?.let {
                        toDo = it.selection.toMutableList()
                        if (toDo.isEmpty()){
                            actionMode?.finish()
                        }else {
                            if (actionMode == null) actionMode = startSupportActionMode(this@MainActivity)
                            actionMode?.title = "${toDo.size}"

                        }
                    }
                }
            }
        )
    }


    private fun refreshAdapter(toDo: List<ToDo>) {
        adapter = CustomAdapter(this, toDo)
        rvMain.adapter = adapter

    }

    private fun prepareToDoList() :MutableList<ToDo>{
        val list = mutableListOf<ToDo>()
        for (i in 0..10) {
            list.add(ToDo("Play Football", "Stadium", "High"))
        }
        return list
    }

    override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
        p0?.let {
            val inflater: MenuInflater = it.menuInflater
            inflater.inflate(R.menu.menu, p1)
            return true
        }
        return false
    }

    override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
        when(p1?.itemId){
            R.id.goal -> {
                Toast.makeText(this@MainActivity, toDo.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDestroyActionMode(p0: ActionMode?) {
        adapter.tracker?.clearSelection()
        adapter.notifyDataSetChanged()
        actionMode = null
    }
}