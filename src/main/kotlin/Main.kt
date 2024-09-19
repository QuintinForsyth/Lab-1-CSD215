package org.example
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.system.exitProcess

data class Task(val name: String, val isComplete: Boolean)

fun main() {
    val file = File("Task List")
    var tasks: List<Task> = listOf()
    val reader = BufferedReader(InputStreamReader(System.`in`))

    tasks = fileExists(file)  // Initialize tasks from file
    println("Welcome to Lab-1 Kotlin TODO List App\n" +
            "-------------------------------------")

    while (true) {
        println("What Would You Like To Do?\n" +
                "1. Add Task\n" +
                "2. Complete Tasks\n" +
                "3. Remove Completed Tasks\n" +
                "4. Quit \n")

        if(file.readLines().isEmpty()){
            println("No Current Tasks")
        }
        else{
            println("Current Tasks:")
        }

        taskPrinter(tasks)

        val response = reader.readLine()?.toIntOrNull()
        when (response) {
            1 -> {
                tasks = createTask(reader, tasks)
                fileWriter(file, tasks)
            }
            2 -> {
                tasks = taskCompleter(reader, tasks)
                fileWriter(file, tasks)
            }
            3 -> {
                tasks = removeAllCompleted(tasks)
                fileWriter(file, tasks)
            }
            4 -> {
                killProgram(file, tasks)
            }
            else -> println("Invalid option, try again.")
        }
    }
}

/**
 * the user writes the new task they want to add here and it goes to addTask
 * @return tasks after it gets the updated task from addTask
 */
fun createTask(reader: BufferedReader, tasks: List<Task>): List<Task> {
    while (true) {
        print("Type Your Task: ")
        val response = reader.readLine() ?: "Unknown"
        if (response.isNotBlank()) {
            return addTask(tasks, response)
        } else {
            println("You are trying to add nothing.")
        }
    }
}

/**
 * prints tasks out sequentially with numbers in front
 */
fun taskPrinter(tasks: List<Task>) {
    tasks.forEachIndexed { index, task ->
        val displayIndex = index + 1  // Adjust index to start from 1
        val status = if (task.isComplete) "Completed" else "Incomplete"
        println("$displayIndex: ${task.name}: $status")
    }
}
/**
 * adds a task to the task list and returns updated list
 * always puts tasks as incomplete initially
 */
fun addTask(tasks: List<Task>, response: String): List<Task> {
    return tasks + Task(response, false)
}

/**
 * Marks tasks as complete
 */
fun taskCompleter(reader: BufferedReader, tasks: List<Task>): List<Task> {
    println("Select a task to mark as complete:")
    taskPrinter(tasks)  // Display tasks with 1-based index

    val taskIndexInput = reader.readLine()?.toIntOrNull()
    val taskIndex = taskIndexInput?.minus(1)  // Convert 1-based index to 0-based index

    if (taskIndex != null && taskIndex in tasks.indices) {
        val task = tasks[taskIndex]
        val updatedTask = task.copy(isComplete = true)
        return tasks.mapIndexed { index, t -> if (index == taskIndex) updatedTask else t }
    } else {
        println("Invalid task number. Please enter a valid number.")
        return tasks  // Return unchanged list
    }
}

/**
 * Removes all completed tasks and returns updated list
 */
fun removeAllCompleted(tasks: List<Task>): List<Task> {
    return tasks.filter { !it.isComplete }
}

/**
 * kills program and saves
 * @param file so it can save
 * @param tasks so it can save tasks to file
 */
fun killProgram(file: File, tasks: List<Task>) {
    fileWriter(file, tasks)
    println("Goodbye")
    exitProcess(0)
}

/**
 * fileExists checks if the file exists and loads tasks from it if so
 * Otherwise, creates a new file with a default task
 */
fun fileExists(file: File): List<Task> {
    return if (file.exists()) {
        fileReader(file)  // Read tasks from file
    } else {
        file.writeText("")  // Create new file
        listOf(Task("Create a Task", false))  // Return default task
    }
}

/**
 * Reads the contents of the file and loads tasks into the list
 */
fun fileReader(file: File): List<Task> {
    return file.readLines().mapNotNull { line ->
        val parts = line.split(":")
        if (parts.size == 2) {
            val name = parts[0]
            val isComplete = parts[1].toBoolean()
            Task(name, isComplete)
        } else {
            println("Ignoring malformed line: $line")
            null
        }
    }
}

/**
 * Writes the immutable task list to the file
 */
fun fileWriter(file: File, tasks: List<Task>) {
    try {
        BufferedWriter(FileWriter(file)).use { writer ->
            for (task in tasks) {
                val taskString = "${task.name}:${task.isComplete}\n"
                writer.write(taskString)
                writer.newLine()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}