package com.example.novie.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"password"})
@Entity
@Table(name="users")
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;

    @Column(unique = true)
    private String emailAddress;


    @JsonProperty(access =JsonProperty.Access.WRITE_ONLY)
    private String password;








    @Column(nullable = false)
    private boolean accountVerified;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private Double balance = 0.0;

    @JsonIgnore
    public String getPassword(){
        return password;
    }


}