package com.example.probation.core.entity

import org.springframework.security.core.GrantedAuthority
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "roles")
class Role(
    var roleName: String? = null,
) : AbstractEntity(), GrantedAuthority {
    constructor(roleName: String? = null, id: Long? = null) : this() {
        this.roleName = roleName
        this.id = id
    }
    override fun getAuthority(): String? = roleName
}
