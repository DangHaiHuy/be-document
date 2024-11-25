package com.huy.pdoc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String location;
    private String picture;
    private String namePictureFirebase;
    @Column(unique = true)
    private String phone;
    @Column(unique = true)
    private String email;
    @Column(name = "activated")
    private boolean activated;
    @Column(name="activate_code")
    private String activateCode;
    // private Set<String> roles;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> authorities;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_folder", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "folder_id"))
    @JsonIgnore
    private List<Folder> favoriteFolderList;
}
