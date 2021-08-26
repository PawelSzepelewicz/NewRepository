package com.example.probation.core.enums

enum class Actions(var action: String) {
    REGISTER("Registration in application"),
    LOG_IN("Logging in"),
    LOG_OUT("Logging out"),
    CHANGE_PASSWORD("Password changing"),
    WATCH_RATING("Watching user's rating"),
    DELETE("Deleting account by admin"),
    BLOCK("Blocking account by admin"),
    EDIT("Personal information editing"),
    CHOOSE("Choosing someone like"),
    CREATE_ACCOUNT("Creating someone account by admin"),
    CREATE_ADMIN("Giving admin authorities by admin"),
    BECOME_ADMIN("Getting admin authorities"),
    HOME("Home page visiting"),
    CONFIRM("Confirmation account by token")
}
