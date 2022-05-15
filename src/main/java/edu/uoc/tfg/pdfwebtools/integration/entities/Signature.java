package edu.uoc.tfg.pdfwebtools.integration.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "signature", schema = "pdf_web_tools")
public class Signature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "signer_name", nullable = false, length = 100)
    private String signerName;

    @Column(name = "signer_nif", nullable = false, length = 50)
    private String signerNif;

    @Column(name = "is_valid", nullable = false)
    private Boolean isValid = false;

    @Column(name = "is_cover_whole", nullable = false)
    private Boolean isCoverWhole = false;

    @Column(name = "sig_date", nullable = false)
    private Instant sigDate;

    @Column(name = "metadata", nullable = false, length = 2000)
    private String metadata;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSignerName() {
        return signerName;
    }

    public void setSignerName(String signerName) {
        this.signerName = signerName;
    }

    public String getSignerNif() {
        return signerNif;
    }

    public void setSignerNif(String signerNif) {
        this.signerNif = signerNif;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public Boolean getIsCoverWhole() {
        return isCoverWhole;
    }

    public void setIsCoverWhole(Boolean isCoverWhole) {
        this.isCoverWhole = isCoverWhole;
    }

    public Instant getSigDate() {
        return sigDate;
    }

    public void setSigDate(Instant sigDate) {
        this.sigDate = sigDate;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

}