package com.huy.pdoc.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huy.pdoc.dto.response.UserResponse;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "name", length = 50)
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author")
    private User author;
    @Column(name = "view")
    private Integer view;
    @Column(name = "star")
    private Integer star;
    @Column(name = "create_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date createAt;
    @Column(name = "slug")
    private String slug;
    @ManyToMany(mappedBy = "favoriteFolderList", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<User> usersLiked;
    @OneToMany(mappedBy = "folder", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Document> listDocuments;

    public UserResponse getAuthor() {
        return new UserResponse(this.author);
    }
}
