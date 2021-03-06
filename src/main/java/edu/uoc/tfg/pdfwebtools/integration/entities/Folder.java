package edu.uoc.tfg.pdfwebtools.integration.entities;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "folder", schema = "pdf_web_tools")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_folder")
    private Folder parentFolder;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Document> documents = new LinkedHashSet<>();

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Folder> folders = new LinkedHashSet<>();

    @Column(name = "ecmid", nullable = false, length = 200)
    private String ecmid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Folder getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(Folder parentFolder) {
        this.parentFolder = parentFolder;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    public Set<Folder> getFolders() {
        return folders;
    }

    public void setFolders(Set<Folder> folders) {
        this.folders = folders;
    }

    public String getEcmid() {
        return ecmid;
    }

    public void setEcmid(String ecmid) {
        this.ecmid = ecmid;
    }

}