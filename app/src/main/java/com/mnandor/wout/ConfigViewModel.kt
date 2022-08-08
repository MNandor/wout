import androidx.lifecycle.*
import com.mnandor.wout.ExerciseDatabase
import com.mnandor.wout.ExerciseTemplate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ConfigViewModel(private val database: ExerciseDatabase) : ViewModel() {

    val allTemplates: LiveData<List<ExerciseTemplate>> = database.dao().getTemplates().asLiveData()

    fun insert(template: ExerciseTemplate) {
        // todo obvious workaround is obvious
        GlobalScope.launch { database.dao().addExerciseTemplate(template)}
    }
}

class ConfigViewModelFactory(private val database: ExerciseDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfigViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConfigViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}