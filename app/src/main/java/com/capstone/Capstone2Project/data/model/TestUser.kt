package com.capstone.Capstone2Project.data.model

data class TestUser(
    val email: String,
    val password: String
) {
    companion object {
        val adminUser: TestUser = TestUser("admin","admin")
        fun isAdminUser(email: String, password: String): Boolean {
            return email == adminUser.email && password == adminUser.password
        }
    }
}
