package edu.uoc.tfg.pdfwebtools.integration.entities;

import javax.persistence.*;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "document", schema = "pdf_web_tools")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "mime_type", nullable = false, length = 50)
    private String mimeType;

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "ecmid", nullable = false, length = 200)
    private String ecmid;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    @OneToMany(mappedBy = "document", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Barcode> barcodes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Signature> signatures = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getEcmid() {
        return ecmid;
    }

    public void setEcmid(String ecmid) {
        this.ecmid = ecmid;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public Set<Barcode> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(Set<Barcode> barcodes) {
        this.barcodes = barcodes;
    }

    public Set<Signature> getSignatures() {
        return signatures;
    }

    public void setSignatures(Set<Signature> signatures) {
        this.signatures = signatures;
    }

}