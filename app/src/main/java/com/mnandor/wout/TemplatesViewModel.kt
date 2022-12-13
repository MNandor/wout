import androidx.lifecycle.*
import com.mnandor.wout.ExerciseDatabase
import com.mnandor.wout.ExerciseTemplate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TemplatesViewModel(private val database: ExerciseDatabase) : ViewModel() {

}

class TemplatesViewModelFactory(private val database: ExerciseDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfigViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConfigViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}