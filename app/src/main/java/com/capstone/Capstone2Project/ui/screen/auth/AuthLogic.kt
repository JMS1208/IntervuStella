package com.capstone.Capstone2Project.ui.screen.auth

class AuthLogic {

    companion object {
        fun isValidEmail(email: String): Boolean {
            val pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
            return email.matches(pattern)
        }
    }
}