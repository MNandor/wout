import androidx.lifecycle.*
import com.mnandor.wout.ExerciseDatabase
import com.mnandor.wout.ExerciseLog
import com.mnandor.wout.ExerciseTemplate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(private val database: ExerciseDatabase) : ViewModel() {

    val allVisibleTemplates: LiveData<List<ExerciseTemplate>> = database.dao().getNonhiddenTemplates().asLiveData()

    val allLogs: LiveData<List<ExerciseLog>> = database.dao().getLogs().asLiveData()

    fun insert(log: ExerciseLog) {
        // todo obvious workaround is obvious
        GlobalScope.launch { database.dao().addExerciseLog(log)}
    }
}

class MainViewModelFactory(private val database: ExerciseDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}