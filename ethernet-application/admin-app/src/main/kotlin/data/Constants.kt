package data

object Constants {
    private val USER_NAME: String = System.getProperty("user.name")
    const val FOLDER_NAME = "Admin - Reaction test"

    object General {
        val PATH_TO_SETTINGS_FOLDER = "C:\\Users\\${USER_NAME}\\AppData\\Local\\${FOLDER_NAME}"
        val PATH_TO_SETTINGS_FILE = "C:\\Users\\${USER_NAME}\\AppData\\Local\\${FOLDER_NAME}\\settings.json"
    }

    object UI {
        val lineChartParams = listOf("Среднее значение", "Стандартное отклонение", "Количество ошибок", "Количество попыток")
    }

    object Table {
        const val SHEET_NAME: String = "Данные тестирования"
        const val PATH: String = "C:\\Данные тестирования\\База тестирования.xlsx"
        const val FILE_NAME: String = "База тестирования"

        const val TABLE_NAME_XLSX: String = "База тестирования.xlsx"
        const val TABLE_NAME_XLSM: String = "База тестирования.xlsm"

        const val PATH_FOLDER: String = "C:\\Данные тестирования"
    }

    object Settings {
        const val NETWORK_DRIVE: String = ""
    }

    object Database {
        private const val LOCAL_DATABASE_NAME = "user_database.db"
        private const val SESSIONS_DATABASE_NAME = "sessions_database.db"

        fun collapseNetDriver(netDriver: String): String {
            val driver = "${netDriver.split(":")[0]}:"
            return "jdbc:sqlite:${driver}\\AppData\\Roaming\\${FOLDER_NAME}\\${LOCAL_DATABASE_NAME}"
        }

        fun collapseNetDriverToSessions(netDriver: String): String {
            val driver = "${netDriver.split(":")[0]}:"
            return "jdbc:sqlite:${driver}\\AppData\\Roaming\\${FOLDER_NAME}\\${SESSIONS_DATABASE_NAME}"
        }
    }
}