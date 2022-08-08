package com.mnandor.wout

import android.app.Application

class WoutApplication :Application() {
    val database by lazy {ExerciseDatabase.getDatabase(this)}
}