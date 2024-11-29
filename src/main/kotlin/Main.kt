package org.example
import java.io.*
import kotlin.system.exitProcess

const val dashString  = "-------------------------------------"
const val mainMenu = dashString +
        "\nWhat Would You Like To Do?\n" +
        "1. Add Task\n" +
        "2. Complete Tasks\n" +
        "3. Remove Completed Tasks\n" +
        "4. List Tasks\n" +
        "5. Quit \n" +
        dashString

fun main() {
    var tasks: List<Task>
    val introductionString = "$dashString\nWelcome to Lab-1 Kotlin TODO List App"
    tasks = readTask("Task List")
    println(colourText("introductionString", TextColour.GREEN))
    val reader = BufferedReader(InputStreamReader(System.`in`))
    while (true) {
        tasks = userInterfaceForApplication(tasks, reader)
        saveTasks(tasks,"Task List")
    }
}

/**
 * the user interface for the application to run loops through allowed options
 * @param tasks is a list of the task data class containing all tasks
 * @param reader is used to get the users input
 */
fun userInterfaceForApplication(tasks: List<Task>, reader: BufferedReader) : List<Task> {
    println(colourText("mainMenu", TextColour.GREEN))

    var newTasks: List<Task>
    val response = promptForNumber(reader)
    when (response) {
        1 -> {
            newTasks = addTask(tasks, promptForTask(reader))
            println("Task added")
            return newTasks
        }
        2 -> {
            newTasks = taskCompleter(reader, tasks)
            println("Task marked as completed")
            return newTasks
        }
        3 -> {
            newTasks = removeAllCompleted(tasks)
            println("All tasks that were marked as completed removed")
            return newTasks
        }
        4 -> {
            taskPrinter(tasks)
            return tasks
        }
        5 -> {
            println("Goodbye")
            killProgram(tasks)
            return tasks
        }
        else -> {
            println("Invalid option, please input a valid menu option.")
            return tasks
        }
    }
}
/**
 * prints tasks out tasks sequentially with numbers in front and a separator at the end
 * @param tasks is the task list which is needed to print out the tasks
 * then it prints the dashString Separator at the end
 */
fun taskPrinter(tasks: List<Task>) {
    if(tasks.isEmpty()){
        println(colourText("No Current Tasks", TextColour.RED))
    }

    else{
        println("Current Tasks:")
        tasks.forEachIndexed { index, task ->
            val displayIndex = index + 1
            val status = if (task.isComplete) colourText("Completed",TextColour.GREEN) else colourText("Uncompleted",TextColour.RED)
            println("$displayIndex: ${task.name}: $status")
        }
        println(dashString)
    }

}


/**
 * the user is prompted to input values to create a new task and will keep trying to get the user to give a valid task
 * @param reader is the reader used to get the user input
 * @return returns the userPromptedResponse value, will return
 */

fun promptForTask(reader: BufferedReader): String {
    while (true) {
        print(colourText("Type Your Task (-1 to exit):",TextColour.BLUE))
        val userPromptedResponse = reader.readLine() ?: "Unknown"

        if (userPromptedResponse.isNotBlank()) {
            return userPromptedResponse

        } else {
            println(colourText("You are trying to add nothing.", TextColour.RED))

        }
    }
}
/**
 * the user is prompted to input a integer value
 * @param reader is the reader used to get the user input
 * @return returns the userPromptedResponse value, will return
 */
fun promptForNumber(reader: BufferedReader): Int? {
    while(true){
        var response = reader.readLine()?.toIntOrNull()
        return response
    }
}


/**
 * adds a task to the task list and returns updated list
 * @param tasks is the task list we pass to everything
 * @param response is the task the user would like to add
 * @return this returns a new task with the response as name and isComplete as False
 */
fun addTask(tasks: List<Task>, response: String): List<Task> {
    return tasks + Task(response, false)
}

/**
 * Marks tasks as complete
 * @param reader is the reader and it reads the input
 * @param tasks is the list of tasks
 * @return tasks to give the tasks list back to the application
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
        return tasks
    }
}
/**
 * Colours a print based on its specified colour
 * @param text The text to be coloured
 * @param color is related to an enumerator of specific Colours with their colourCode
 * @return the String passed to it to be colours
 */
fun colourText(text: String, colour: TextColour): String {
    return "${colour.code}$text\u001B[0m"
}

/**
 * ENUM for the possible colour variants of the colourText function
 */
enum class TextColour(val code: String) {
    GREEN("\u001B[32m"),
    RED("\u001B[31m"),
    BLUE("\u001B[34m"),
}


/**
 * removes all completed tasks from the tasks list
 * @param tasks is the task list containing all tasks
 * @return tasks is filters out the completed objects from the Tasks List then returns it.
 */
fun removeAllCompleted(tasks: List<Task>) = tasks.filterNot { it.isComplete }

/**
 * kills program and saves the Tasks
 * @param tasks so it can save tasks to file
 */
fun killProgram(tasks: List<Task>) {
    saveTasks(tasks, "Task List")
    exitProcess(0)
}

/**
 * Reads the contents of the file if no file is present it makes a new one
 * @param filePathName is the file Path name for the file being used
 * @return returns a list of the objects Tasks after turning file values into Tasks
 */

fun readTask(filePathName: String): List<Task> {
    val file = File(filePathName)
    if (!file.exists() || file.readLines().isEmpty()) {
        file.writeText("")
        return listOf(Task("Create a Task", false))
    }
    return file.readLines().mapNotNull { line ->
        val parts = line.split(":")
        if (parts.size == 2) {
            val name = parts[0]
            val isComplete = parts[1].trim().toBoolean()
            Task(name, isComplete)
        } else {
            null
        }
    }
}
/**
 * saves the List of Tasks to a file called Task List
 * @param filePathName is the file Path name for the file being used
 * @param tasks the list of tasks that are to be saved to the file
 */
fun saveTasks(tasks: List<Task>, filePathName: String) {
    val file = File(filePathName)
    try {
        BufferedWriter(FileWriter(file)).use { writer ->
            for (task in tasks) {
                val taskString = "${task.name}:${task.isComplete}"
                writer.write(taskString)
                writer.newLine()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}