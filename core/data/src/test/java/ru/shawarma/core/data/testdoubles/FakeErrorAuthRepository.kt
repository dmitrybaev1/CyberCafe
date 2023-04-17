package ru.shawarma.core.data.testdoubles

import ru.shawarma.core.data.entities.*
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result

class FakeErrorAuthRepository: AuthRepository {
    override suspend fun login(userLoginRequest: UserLoginRequest): Result<AuthData> =
        Result.Failure("")

    override suspend fun clearAuthData() {

    }

    override suspend fun getActualAuthData(): Result<AuthData> =
        Result.Failure(Errors.REFRESH_TOKEN_ERROR)

    override suspend fun register(userRegisterRequest: UserRegisterRequest): Result<RegisteredUser> =
        Result.Failure("")

    override suspend fun getInfo(): Result<InfoResponse> =
        Result.Failure("")
}