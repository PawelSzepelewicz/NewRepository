package com.example.probation.core.entity;

import com.example.probation.core.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(of="role")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
@Data
public class Role extends AbstractEntity implements GrantedAuthority {
    private String role;

    @Override
    public String getAuthority() {
        return getRole();
    }
}