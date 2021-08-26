package com.example.probation.core.entity

import lombok.EqualsAndHashCode
import org.springframework.security.core.GrantedAuthority
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "roles")
@EqualsAndHashCode(of = ["roleName"], callSuper = false)
data class Role(
    @Column(name = "role_name")
    var roleName: String? = null
) : AbstractEntity(), GrantedAuthority {

    override fun getAuthority(): String? = roleName
}