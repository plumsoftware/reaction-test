package domain.model

import data.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Settings(
    @SerialName("isDarkTheme") val isDarkTheme: Boolean = false,
    @SerialName("dataFormats") val dataFormats: Map<String,Boolean> = mapOf(
        Constants.Settings.XLSX to true,
        Constants.Settings.XLS to false
    ),
    @SerialName("networkDrive") val networkDrive: String = Constants.Settings.NETWORK_DRIVE,
    @SerialName("localDrive") val localDrive: String = Constants.Settings.LOCAL_DRIVE,

    @SerialName("localFolderToTable") val localFolderToTable: String = Constants.Settings.LOCAL_FOLDER_TO_TABLE
)
