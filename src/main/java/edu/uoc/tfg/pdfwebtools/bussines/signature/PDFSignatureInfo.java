package edu.uoc.tfg.pdfwebtools.bussines.signature;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class PDFSignatureInfo implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    public Map<String, Object> entries = new HashMap<>();

    private String reason;
    private String name;
    private String subFilter;
    private String filter;
    private String contactInfo;
    private String location;
    private Date signDate;
    private boolean coversWholeDocument;
    private boolean isSelfSigned;
    private boolean isRevoked;
    private boolean signatureVerified;

    private CertificateInfo certificateInfo;

    public Map<String, Object> getEntries() {
        return entries;
    }

    public void setEntries(Map<String, Object> entries) {
        this.entries = entries;
    }

    public boolean isSignatureVerified() {
        return signatureVerified;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubFilter() {
        return subFilter;
    }

    public void setSubFilter(String subFilter) {
        this.subFilter = subFilter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public boolean isCoversWholeDocument() {
        return coversWholeDocument;
    }

    public void setCoversWholeDocument(boolean coversWholeDocument) {
        this.coversWholeDocument = coversWholeDocument;
    }

    public boolean getIsSelfSigned() {
        return isSelfSigned;
    }

    public void setSelfSigned(boolean selfSigned) {
        isSelfSigned = selfSigned;
    }

    public boolean getIsRevoked() {
        return isRevoked;
    }

    public void setRevoked(boolean revoked) {
        isRevoked = revoked;
    }

    public boolean getSignatureVerified() {
        return signatureVerified;
    }

    public void setSignatureVerified(boolean signatureVerified) {
        this.signatureVerified = signatureVerified;
    }

    public CertificateInfo getCertificateInfo() {
        return certificateInfo;
    }

    public void setCertificateInfo(CertificateInfo certificateInfo) {
        this.certificateInfo = certificateInfo;
    }



}
