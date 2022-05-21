package edu.uoc.tfg.pdfwebtools.bussines.signature;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class CertificateInfo implements java.io.Serializable{

    private static final long serialVersionUID = 1L;

    private String issuerDN;
    private String subjectDN;

    private Date notValidBefore;
    private Date notValidAfter;

    private String signAlgorithm;
    private String serial;
    private String type;
    private String subjectSN;
    private String subjectName;
    private String subjectSurmame;
    private String organization;
    private String organicUnit;


    public CertificateInfo() {
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganicUnit() {
        return organicUnit;
    }

    public void setOrganicUnit(String organicUnit) {
        this.organicUnit = organicUnit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private Map<String, String> issuerOIDs = new HashMap<>();

    private Map<String, String> subjectOIDs = new HashMap<>();

    public String getIssuerDN() {
        return issuerDN;
    }

    public void setIssuerDN(String issuerDN) {
        this.issuerDN = issuerDN;
    }

    public String getSubjectDN() {
        return subjectDN;
    }

    public void setSubjectDN(String subjectDN) {
        this.subjectDN = subjectDN;
    }

    public Date getNotValidBefore() {
        return notValidBefore;
    }

    public void setNotValidBefore(Date notValidBefore) {
        this.notValidBefore = notValidBefore;
    }

    public Date getNotValidAfter() {
        return notValidAfter;
    }

    public void setNotValidAfter(Date notValidAfter) {
        this.notValidAfter = notValidAfter;
    }

    public String getSignAlgorithm() {
        return signAlgorithm;
    }

    public void setSignAlgorithm(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSubjectSN() {
        return subjectSN;
    }

    public void setSubjectSN(String subjectSN) {
        this.subjectSN = subjectSN;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectSurmame() {
        return subjectSurmame;
    }

    public void setSubjectSurmame(String subjectSurmame) {
        this.subjectSurmame = subjectSurmame;
    }

    public Map<String, String> getIssuerOIDs() {
        return issuerOIDs;
    }

    public void setIssuerOIDs(Map<String, String> issuerOIDs) {
        this.issuerOIDs = issuerOIDs;
    }

    public Map<String, String> getSubjectOIDs() {
        return subjectOIDs;
    }

    public void setSubjectOIDs(Map<String, String> subjectOIDs) {
        this.subjectOIDs = subjectOIDs;
    }

}
