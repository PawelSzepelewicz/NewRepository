package com.example.probation.core.entity

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Column(name = "user_name")
    var username: String? = null,
    @Column(name = "description")
    var description: String? = null,
    @Column(name = "email")
    var email: String? = null,
    @Column(name = "rating")
    var rating: Int = INITIAL_RATING,
    @Column(name = "password")
    var password: String? = null,
    @ManyToMany(
        targetEntity = Role::class, fetch = FetchType.EAGER,
        cascade = [CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH]
    )
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: Set<Role> = emptySet(),
    @Column(name = "enabled")
    var enabled: Boolean = INITIAL_ENABLED
) : AbstractEntity() {
    companion object {
        const val INITIAL_RATING = 2500
        const val INITIAL_ENABLED = false
    }
}
