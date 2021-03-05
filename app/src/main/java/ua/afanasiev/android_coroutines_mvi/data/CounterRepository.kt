package ua.afanasiev.android_coroutines_mvi.data

import kotlinx.coroutines.delay
import kotlin.random.Random

class CounterRepository {

    suspend fun getRandomValueDelayed(): Int {
        delay(2000)
        return getRandomValue()
    }

    fun getRandomValue(): Int {
        return Random.nextInt(100)
    }
}