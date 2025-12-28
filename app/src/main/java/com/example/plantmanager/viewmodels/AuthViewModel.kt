package com.example.plantmanager.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantmanager.data.local.User
import com.example.plantmanager.data.local.UserDao
import com.example.plantmanager.data.local.SessionDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.regex.Pattern

class AuthViewModel(
    private val userDao: UserDao,
    private val sessionDao:SessionDao
) : ViewModel() {

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Error(val message: String) : AuthState()
        data class SignedIn(val userId: Int) : AuthState()
        object SignedUp : AuthState()
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun validateEmail(email: String): Boolean {
        val pattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return pattern.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 6 && password.any { it.isDigit() }
    }

    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun signUp(name: String, email: String, password: String) = viewModelScope.launch {
        if (name.isBlank()) { _authState.value = AuthState.Error("Nom requis"); return@launch }
        if (!validateEmail(email)) { _authState.value = AuthState.Error("Email invalide"); return@launch }
        if (!validatePassword(password)) { _authState.value = AuthState.Error("Mot de passe faible (≥6 avec un chiffre)"); return@launch }

        _authState.value = AuthState.Loading
        val existing = userDao.getUserByEmailNow(email)
        if (existing != null) {
            _authState.value = AuthState.Error("Email déjà utilisé")
            return@launch
        }
        val id = userDao.insertUser(User(name = name, email = email, passwordHash = hashPassword(password))).toInt()
        sessionDao.upsert(com.example.plantmanager.data.local.Session(id = 1, currentUserId = id))
        _authState.value = AuthState.SignedUp
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {
        if (!validateEmail(email)) { _authState.value = AuthState.Error("Email invalide"); return@launch }
        if (password.isBlank()) { _authState.value = AuthState.Error("Mot de passe requis"); return@launch }
        _authState.value = AuthState.Loading
        val user = userDao.getUserByEmailNow(email)
        if (user == null) {
            _authState.value = AuthState.Error("Utilisateur introuvable")
            return@launch
        }
        val hash = hashPassword(password)
        if (user.passwordHash != hash) {
            _authState.value = AuthState.Error("Mot de passe incorrect")
            return@launch
        }
        sessionDao.upsert(com.example.plantmanager.data.local.Session(id = 1, currentUserId = user.id))
        _authState.value = AuthState.SignedIn(user.id)
    }

    fun logout() = viewModelScope.launch {
        sessionDao.clear()
    }

    suspend fun getCurrentUserNow(): User? {
        val s = sessionDao.getSessionNow()
        val id = s?.currentUserId ?: return null
        return userDao.getUserByIdNow(id)
    }

    fun setReminderLeadHours(hours: Int) = viewModelScope.launch {
        val s = sessionDao.getSessionNow()
        val id = s?.currentUserId ?: return@launch
        val u = userDao.getUserByIdNow(id) ?: return@launch
        userDao.updateUser(u.copy(reminderLeadHours = hours))
    }

    fun updateProfile(name: String, email: String) = viewModelScope.launch {
        val s = sessionDao.getSessionNow()
        val id = s?.currentUserId ?: return@launch
        val u = userDao.getUserByIdNow(id) ?: return@launch
        userDao.updateUser(u.copy(name = name, email = email))
    }
}
