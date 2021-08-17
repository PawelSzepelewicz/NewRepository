package com.example.probation.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(of = "roleName", callSuper = false)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
@Data
public class Role extends AbstractEntity implements GrantedAuthority {
    @Column(name = "role_name")
    private String roleName;

    @Override
    public String getAuthority() {
        return getRoleName();
    }
}
