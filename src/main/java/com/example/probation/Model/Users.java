package com.example.probation.Model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "users")
public class Users extends AbstractEntity {

    private String userName;
    private String description;
    private Integer rating;

}
