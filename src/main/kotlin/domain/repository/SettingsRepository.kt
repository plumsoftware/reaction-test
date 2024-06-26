package domain.repository

import domain.model.Settings

interface SettingsRepository {
    suspend fun saveData(settings: Settings)

    suspend fun loadSettings(): Settings
}