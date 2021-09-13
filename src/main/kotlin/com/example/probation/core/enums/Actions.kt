package com.example.probation.core.enums

enum class Actions(var action: String) {
    REGISTERED("Was registered in application by"),
    REGISTER("Registered a new account of"),
    LOG_IN("Logging in"),
    UNBLOCK("Unblock account of"),
    CHANGE_PASSWORD("Changed his password"),
    DELETE("Deleted account of"),
    DELETED("Account was deleted by"),
    BLOCK("Blocking account of"),
    EDIT("Edited his personal account"),
    CHOOSE("Chose like"),
    CONFIRM("Confirmed his account by token")
}
