package com.example.probation.core.entity

import lombok.EqualsAndHashCode
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "verification_token")
@EqualsAndHashCode(callSuper = true)
data class VerificationToken(
    @Column(name = "token")
    var token: String? = null,
    @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER,
        cascade = [CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH])
    @JoinTable(
        name = "user_token",
        joinColumns = [JoinColumn(name = "token_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")]
    )
    var user: User? = null,
    @Column(name = "expiryDate")
    var expiryDate: Date? = null
) : AbstractEntity() {
    companion object {
        const val EXPIRATION = 60 * 2
    }

    constructor(_token: String, _user: User) : this() {
        token = _token
        user = _user
        expiryDate = calculateExpiryDate()
    }

    private fun calculateExpiryDate(): Date {
        val cal = Calendar.getInstance()
        cal.time = Timestamp(cal.time.time)
        cal.add(Calendar.MINUTE, EXPIRATION)
        return Date(cal.time.time)
    }
}
