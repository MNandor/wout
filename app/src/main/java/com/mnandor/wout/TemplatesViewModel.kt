import androidx.lifecycle.*
import com.mnandor.wout.ExerciseDatabase
import com.mnandor.wout.ExerciseTemplate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TemplatesViewModel(private val database: ExerciseDatabase) : ViewModel() {
    val allVisibleTemplates: LiveData<List<ExerciseTemplate>> = database.dao().getNonhiddenTemplates().asLiveData()
}

class TemplatesViewModelFactory(private val database: ExerciseDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TemplatesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TemplatesViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}