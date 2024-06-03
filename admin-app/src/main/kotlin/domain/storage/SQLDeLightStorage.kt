package domain.storage

import domain.model.regular.user.User
import domain.usecase.sqldelight.*

class SQLDeLightStorage(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getSessionsWithUserIdUseCase: GetSessionsWithUserIdUseCase,
    private val insertNewUserUseCase: InsertNewUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) {

    suspend fun getAllUsers() = getAllUsersUseCase.execute()
    suspend fun getSessions(userId: Long) = getSessionsWithUserIdUseCase.execute(userId = userId)
    suspend fun insert(user: User, login: String, password: String) =
        insertNewUserUseCase.execute(user = user, login = login, password = password)

    suspend fun update(user: User, login: String, password: String) =
        updateUserUseCase.execute(user = user, login = login, password = password)

    suspend fun delete(userId: Long) = deleteUserUseCase.execute(userId = userId)
}