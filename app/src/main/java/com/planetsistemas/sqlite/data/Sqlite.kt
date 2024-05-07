package com.planetsistemas.sqlite.data

import android.app.ActionBar.Tab
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLite(
    context: Context,
    databaseName: String = "database.db",
    databaseVersion: Int = 1
) : SQLiteOpenHelper(context, databaseName, null, databaseVersion) {
    private val TABLE_NAME = "todos"

    // if not exists
    override fun onCreate(db: SQLiteDatabase) {
        println("onCreate()")
        val sql = """
            CREATE TABLE $TABLE_NAME(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                description TEXT,
                isDone INTEGER
            )
            """.trimIndent()
        println(sql)
        db.execSQL(sql)
    }

    // if exists under version
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insertTodo(todo: Todo): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("title", todo.title)
            put("description", todo.description)
            put("isDone", todo.isDone)
        }
        val id = db.insert(
            TABLE_NAME,
            null,
            values
        )
        db.close()
        return id
    }

    fun getTodo(id: Int): Todo {
        val db = this.readableDatabase
        val columns = arrayOf(
            "id",
            "title",
            "description",
            "isDone"
        )
        val where = "id = ?"
        val whereArgs = arrayOf(id.toString())
        val cursor = db.query(
            TABLE_NAME,
            columns,
            where,
            whereArgs,
            null,
            null,
            null
        )
        val todo = Todo()
        if (cursor != null) {
            cursor.moveToFirst()
            todo.id = cursor.getInt(0)
            todo.title = cursor.getString(1)
            todo.description = cursor.getString(2)
            todo.isDone = cursor.getInt(3) == 1
        }
        cursor.close()
        db.close()
        return todo
    }

    fun getAllTodo(txt: String = ""): List<Todo> {
        val db = this.readableDatabase
        val columns = arrayOf(
            "id",
            "title",
            "description",
            "isDone"
        )
        val where = "title LIKE ?"
        val whereArgs = arrayOf("%$txt%")
        val cursor = db.query(
            TABLE_NAME,
            columns,
            where,
            whereArgs,
            null,
            null,
            null
        )
        var todos = mutableListOf<Todo>()
        while (cursor.moveToNext()) {
            val todo = Todo(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3) == 1
            )
            todos.add(todo)
        }
        cursor.close()
        db.close()
        return todos
    }

    fun updateTodo(todo: Todo) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("title", todo.title)
            put("description", todo.description)
            put("isDone", todo.isDone)
        }
        val where = "id = ?"
        val whereArgs = arrayOf(todo.id.toString())
        db.update(
            TABLE_NAME,
            values,
            where,
            whereArgs
        )
        db.close()
    }

    fun deleteTodo(id: Int) {
        val db = this.writableDatabase
        db.delete(
            TABLE_NAME,
            "id = ?",
            arrayOf(id.toString()) // whereArgs
        )
        db.close()
    }
}