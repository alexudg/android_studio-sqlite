package com.planetsistemas.sqlite.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.planetsistemas.sqlite.data.SQLite
import com.planetsistemas.sqlite.data.Todo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(context: Context) {
    var isAlertInsUpd by rememberSaveable { mutableStateOf(false) }
    var isAlertDel by rememberSaveable { mutableStateOf(false) }
    var idSelected by rememberSaveable { mutableIntStateOf(-1) }
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var isDone by rememberSaveable { mutableStateOf(false) }
    var todos by rememberSaveable { mutableStateOf(listOf<Todo>()) }
    val focus = remember { FocusRequester() }

    LaunchedEffect(true) {
        todos = SQLite(context).getAllTodo()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Lista de tareas")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                ),
                actions = {
                    Row {
                        IconButton(
                            enabled = idSelected > -1,
                            onClick = {
                                // get Todo with id
                                val todo = todos.find { todo ->
                                    todo.id == idSelected
                                }
                                title = todo!!.title
                                description = todo!!.description
                                isDone = todo!!.isDone
                                isAlertInsUpd = true
                            }
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                "Editar"
                            )
                        }
                        IconButton(
                            enabled = idSelected != -1,
                            onClick = { isAlertDel = true }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                "Editar"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    idSelected = -1
                    title = ""
                    description = ""
                    isDone = false
                    isAlertInsUpd = true
                }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Nueva tarea"
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(todos) { item ->
                val txt = if (item.isDone) "Hecha" else "Pendiente"
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = idSelected == item.id,
                            onClick = { idSelected = item.id }
                        )
                        .fillMaxWidth()
                ) {
                    RadioButton(
                        selected = idSelected == item.id,
                        onClick = { idSelected = item.id }
                    )
                    Column {
                        Text(item.title)
                        Text(item.description)
                        Text(txt)
                    }
                }
                Divider()

            }
        }
    }

    // insert or update
    if (isAlertInsUpd) {
        val txt = if (idSelected == -1) "Agregar tarea" else "Actualizar tarea"

        LaunchedEffect(key1 = true) {
            // focus after shown
            if (idSelected == -1)
                focus.requestFocus()
        }

        AlertDialog(
            title = { Text(txt) },
            text = {
                Column {
                    TextField(
                        modifier = Modifier.focusRequester(focus),
                        value = title,
                        placeholder = { Text("Titulo") },
                        onValueChange = { txt ->
                            title = txt
                        },
                        isError = title.isEmpty()
                    )
                    if (title.isEmpty()) {
                        Text("Titulo vacío")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    TextField(
                        value = description,
                        placeholder = { Text("Descripción") },
                        onValueChange = { txt ->
                            description = txt
                        },
                        isError = description.isEmpty()
                    )
                    if (description.isEmpty()) {
                        Text("Descripción vacía")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            enabled = idSelected > -1,
                            checked = isDone,
                            onCheckedChange = {
                                isDone = !isDone
                            }
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("Hecha")
                    }
                }
            },
            onDismissRequest = { isAlertInsUpd = false },
            confirmButton = {
                Button(
                    enabled = title.isNotEmpty() && description.isNotEmpty(),
                    onClick = {
                        val todo = Todo(
                            idSelected,
                            title,
                            description,
                            isDone
                        )
                        if (idSelected == -1)
                            idSelected = SQLite(context).insertTodo(todo).toInt()
                        else
                            SQLite(context).updateTodo(todo)
                        todos = SQLite(context).getAllTodo()
                        isAlertInsUpd = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { isAlertInsUpd = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    // isSureDelete?
    if (isAlertDel) {
        AlertDialog(
            title = { Text(text = "Eliminar") },
            text = {
                Text("¿Estás segur@ de eliminar la tarea seleccionada?")
            },
            onDismissRequest = { isAlertDel = false },
            confirmButton = {
                Button(
                    onClick = {
                        SQLite(context).deleteTodo(idSelected)
                        idSelected = -1
                        todos = SQLite(context).getAllTodo()
                        isAlertDel = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { isAlertDel = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
