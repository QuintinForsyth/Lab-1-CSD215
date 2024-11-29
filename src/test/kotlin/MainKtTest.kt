import org.example.Task
import org.example.addTask
import org.example.removeAllCompleted
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class TestForRemoveAllCompleted {

    @Test
    fun removeAllCompleted () {

        val tasks = listOf(
            Task("Task 1", isComplete = true),
            Task("Task 2", isComplete = false),
            Task("Task 3", isComplete = true),
            Task("Task 4", isComplete = false)
        )
        val expected = listOf(
            Task("Task 2", isComplete = false),
            Task("Task 4", isComplete = false)
        )
        val result = removeAllCompleted(tasks)
        assertEquals(expected, result)
    }
}
class TestAddTask {
    @Test
    fun addTask(){
        val tasks = listOf(
            Task("Task 1", isComplete = false),
            Task("Task 2", isComplete = true)
        )
        val newTaskResponse = "Task 3"

        val expected = listOf(
            Task("Task 1", isComplete = false),
            Task("Task 2", isComplete = true),
            Task("Task 3", isComplete = false)
        )

        val result = addTask(tasks, newTaskResponse)
        assertEquals(expected, result)
    }
}
