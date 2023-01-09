import androidx.lifecycle.*
import com.mnandor.wout.ExerciseDatabase
import com.mnandor.wout.ExerciseTemplate
import com.mnandor.wout.TemplateItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TemplatesViewModel(private val database: ExerciseDatabase) : ViewModel() {
    val allVisibleTemplates: LiveData<List<ExerciseTemplate>> = database.dao().getTemplates().asLiveData()
    val allTemplateItems: LiveData<List<TemplateItem>> = database.dao().getDayTemplates().asLiveData()

    fun insert(item: TemplateItem){
        GlobalScope.launch { database.dao().addDayTemplate(item)}
    }

    fun remove(item: TemplateItem){
        GlobalScope.launch { database.dao().deleteDayTemplate(item)}
    }



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