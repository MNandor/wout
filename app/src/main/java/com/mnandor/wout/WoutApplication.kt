package com.mnandor.wout

import android.app.Application
import com.mnandor.wout.data.ExerciseDatabase

class WoutApplication :Application() {
    val database by lazy { ExerciseDatabase.getDatabase(this)}
}