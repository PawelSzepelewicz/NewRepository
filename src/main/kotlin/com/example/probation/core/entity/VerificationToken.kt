package com.example.probation.core.entity

import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "verification_token")
class VerificationToken(
    @Column(name = "token")
    var token: String? = null,
    @OneToOne(
        targetEntity = User::class, fetch = FetchType.EAGER,
        cascade = [CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH]
    )
    @JoinTable(
        name = "user_token",
        joinColumns = [JoinColumn(name = "token_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")]
    )
    var user: User? = null,
    @Column(name = "expiryDate")
    var expiryDate: LocalDateTime? = null
) : AbstractEntity() {
    companion object {
        const val EXPIRATION: Long = 60 * 2
    }

    constructor(_token: String, _user: User) : this() {
        token = _token
        user = _user
        expiryDate = calculateExpiryDate()
    }

    private fun calculateExpiryDate(): LocalDateTime =
        LocalDateTime.now().plusMinutes(EXPIRATION)
}
