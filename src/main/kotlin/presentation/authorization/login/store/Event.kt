package presentation.authorization.login.store

import domain.model.regular.DrivingLicenseCategory
import domain.model.regular.Interval

sealed class Event {

    data object BackClicked : Event()
    data object StartTest : Event()
    data object OpenPrivacyPolicy : Event()

    data class OnLoginChanged(val login: String) : Event()
    data class OnPasswordChanged(val password: String) : Event()
    data class OnExperienceChanged(val experience: Int) : Event()
    data class OnCountChanged(val count: Int) : Event()
    data class OnDrivingLicenseCategoryChanged(val drivingLicenseCategory: DrivingLicenseCategory) : Event()
    data class OnIntervalChanged(val interval: Interval) : Event()
    data class OnEnableStartTestChanged(val enabled: Boolean) : Event()
}