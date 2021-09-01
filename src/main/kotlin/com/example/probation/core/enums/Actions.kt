package com.example.probation.core.enums

enum class Actions(var action: String) {
    REGISTER("Registration in application"),
    LOG_IN("Logging in"),
    UNBLOCK("Admin unblocked user"),
    CHANGE_PASSWORD("Password changing"),
    DELETE("Admin deleted account"),
    DELETED("Account was deleted by admin"),
    BLOCK("Blocking account by admin"),
    EDIT("Personal information editing"),
    CHOOSE("Choosing someone like"),
    HOME("Home page visiting"),
    CONFIRM("Confirmation account by token")
}
