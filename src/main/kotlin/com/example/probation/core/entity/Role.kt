package com.example.probation.core.entity

import org.springframework.security.core.GrantedAuthority
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "roles")
class Role(
    @Column(name = "role_name")
    var roleName: String? = null
) : AbstractEntity(), GrantedAuthority {
    override fun getAuthority(): String? = roleName
}
