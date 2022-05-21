package edu.uoc.tfg.pdfwebtools.bussines.signature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uoc.tfg.pdfwebtools.appexceptions.PdfAppException;
import edu.uoc.tfg.pdfwebtools.bussines.alfresco.DocumentECM;
import edu.uoc.tfg.pdfwebtools.bussines.repository.RepositoryService;
import edu.uoc.tfg.pdfwebtools.integration.entities.Document;
import edu.uoc.tfg.pdfwebtools.integration.entities.Signature;
import edu.uoc.tfg.pdfwebtools.integration.repos.repository.DocumentRepository;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SignatureServiceBean implements SignatureService {

    private static final Logger logger = LoggerFactory.getLogger(SignatureServiceBean.class);

    RepositoryService repositoryService;

    DocumentRepository documentRepository;

    private static final String letersNif = "TRWAGMYFPDXBNJZSQVHLCKE";

    private static final String letersNie = "XYZ";

    @Autowired
    public SignatureServiceBean(RepositoryService repositoryService, DocumentRepository documentRepository) {
        this.repositoryService = repositoryService;
        this.documentRepository = documentRepository;
    }

    @Override
    public Document scantSignaturesFromPfd(Document doc) throws IOException {
        DocumentECM repoDoc = repositoryService.downloadDocument(doc.getId());
        Set<Signature> signatures;
        try {
            signatures = procesDocument(repoDoc, doc);
        } catch (SignatureCheckerException e) {
            throw new RuntimeException(e);
        }
        doc.setSignatures(signatures);
        return documentRepository.save(doc);
    }


//    PRIVATE

    private Set<Signature> procesDocument(DocumentECM repoDoc, Document doc) throws SignatureCheckerException, JsonProcessingException {
        Set<Signature> signatures = new LinkedHashSet<>();
        List<PDFSignatureInfo> signaturesInfo = getPDFSignatureInfo(repoDoc.getInputStream());
        for (PDFSignatureInfo info : signaturesInfo){
            Signature sig = new Signature();
            sig.setSignerName(info.getCertificateInfo().getSubjectName() + " " + info.getCertificateInfo().getSubjectSurmame());
            sig.setSignerNif(parseDNIoNIE(info.getCertificateInfo().getSubjectSN()));
            sig.setIsValid(!info.getIsSelfSigned() && info.getSignatureVerified());
            sig.setIsCoverWhole(info.isCoversWholeDocument());
            sig.setSigDate(info.getSignDate().toInstant());
            ObjectMapper mapper = new ObjectMapper();
            sig.setMetadata(mapper.writeValueAsString(info));
            sig.setDocument(doc);
            signatures.add(sig);
        }
        return signatures;
    }


    private static List<PDFSignatureInfo> getPDFSignatureInfo(InputStream is) throws SignatureCheckerException {
        try {
            byte[] byteArray = getbyteArray(is);
            return getPDFSignatureInfo(byteArray);
        } catch (Exception ex) {
            throw new PdfAppException("Error during signature verification process", PdfAppException.Type.UNEXPECTED);
        }

    }

    private static byte[] getbyteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();

        return baos.toByteArray();
    }

    private static List<PDFSignatureInfo> getPDFSignatureInfo(byte[] byteArray) throws
            NoSuchAlgorithmException, NoSuchProviderException, InvalidNameException, SignatureCheckerException {

        List<PDFSignatureInfo> signatures = new ArrayList<PDFSignatureInfo>();

        // Try to open the input file as PDF
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(byteArray))) {
            PDSignature lastSig = SignatureUtils.getLastRelevantSignature(document);
            List<PDSignature> allSigs = document.getSignatureDictionaries();
            if (lastSig != null && lastSig.getName() != null) {
                for (PDSignature sig : allSigs) {
                    if (sig.getName() != null && lastSig.getName().equalsIgnoreCase(sig.getName())
                            && lastSig.getSignDate().compareTo(sig.getSignDate()) == 0) {
                        allSigs.remove(sig);
                        allSigs.add(lastSig);
                        break;
                    }
                }
            }
            for (PDSignature sig : allSigs) {
                PDFSignatureInfo psi = new PDFSignatureInfo();
                signatures.add(psi);

                COSDictionary sigDict = sig.getCOSObject();
                COSString contents = (COSString) sigDict.getDictionaryObject(COSName.CONTENTS);

                Set<Map.Entry<COSName, COSBase>> entries = sigDict.entrySet();
                for (Map.Entry<COSName, COSBase> entry : entries) {
                    // Don't return contents
                    if (!entry.getKey().equals(COSName.CONTENTS)) {
//                        psi.getEntries().put(entry.getKey().getName(), entry.getValue().toString());
                    }
                }

                psi.setReason(sig.getReason());
                psi.setName(sig.getName());
                psi.setSignDate(sig.getSignDate().getTime());
                psi.setSubFilter(sig.getSubFilter());
                psi.setContactInfo(sig.getContactInfo());
                psi.setFilter(sig.getFilter());
                psi.setLocation(sig.getLocation());

                // download the signed content
                byte[] buf;
                buf = sig.getSignedContent(new ByteArrayInputStream(byteArray));

                int[] byteRange = sig.getByteRange();
                if (byteRange.length != 4) {
                    throw new IOException("Signature byteRange must have 4 items");
                } else {
                    long fileLen = byteArray.length;
                    long rangeMax = byteRange[2] + (long) byteRange[3];
                    // multiply content length with 2 (because it is in hex in the PDF) and add 2 for < and >
                    int contentLen = sigDict.getString(COSName.CONTENTS).length() * 2 + 2;
                    if (fileLen != rangeMax || byteRange[0] != 0 || byteRange[1] + contentLen != byteRange[2]) {
                        // a false result doesn't necessarily mean that the PDF is a fake
                        psi.setCoversWholeDocument(false);
                    } else {
                        psi.setCoversWholeDocument(true);
                    }

                }

                String subFilter = sig.getSubFilter();
                if (subFilter != null) {
                    switch (subFilter) {
                        case "adbe.pkcs7.detached":
                        case "ETSI.CAdES.detached":
                            verifyPKCS7(buf, contents, sig, psi);
                            break;
                        case "adbe.pkcs7.sha1": {
                            COSString certString = (COSString) sigDict.getDictionaryObject(COSName.CONTENTS);
                            byte[] certData = certString.getBytes();
                            CertificateFactory factory = CertificateFactory.getInstance("X.509");
                            ByteArrayInputStream certStream = new ByteArrayInputStream(certData);
                            Collection<? extends Certificate> certs = factory.generateCertificates(certStream);

                            byte[] hash = MessageDigest.getInstance("SHA1").digest(buf);
                            verifyPKCS7(hash, contents, sig, psi);
                            break;
                        }
                        case "adbe.x509.rsa_sha1": {

                            COSString certString = (COSString) sigDict.getDictionaryObject(COSName.getPDFName("Cert"));
                            byte[] certData = certString.getBytes();
                            CertificateFactory factory = CertificateFactory.getInstance("X.509");
                            ByteArrayInputStream certStream = new ByteArrayInputStream(certData);
                            Collection<? extends Certificate> certs = factory.generateCertificates(certStream);
                            psi.setSignatureVerified(false);
                            logger.debug("Unable to verify adbe.x509.rsa_sha1 subfilter");
                            throw new SignatureCheckerException("Unable to verify adbe.x509.rsa_sha1 subfilter");
                        }
                        default:
                            throw new SignatureCheckerException("Unknown certificate type " + subFilter);

                    }
                } else {
                    throw new SignatureCheckerException("Missing subfilter for cert dictionary");
                }
            }
        } catch (IOException | CertificateException | CMSException | OperatorCreationException | CRLException |
                 CertPathValidatorException | ParseException ex) {
            throw new SignatureCheckerException("Error durant el procés de verificació de les signatures", ex);
        }

        return signatures;
    }

    /**
     * Verify a PKCS7 signature.
     *
     * @param byteArray the byte sequence that has been signed
     * @param contents  the /Contents field as a COSString
     * @param sig       the PDF signature (the /V dictionary)
     * @throws CertificateException
     * @throws CMSException
     * @throws StoreException
     * @throws OperatorCreationException
     */
    private static void verifyPKCS7(byte[] byteArray, COSString contents, PDSignature sig, PDFSignatureInfo psi)
            throws CMSException, CertificateException, StoreException, OperatorCreationException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidNameException, IOException, CRLException, CertPathValidatorException, ParseException {


        CMSProcessable signedContent = new CMSProcessableByteArray(byteArray);
        CMSSignedData signedData = new CMSSignedData(signedContent, contents.getBytes());
        Store certificatesStore = signedData.getCertificates();
        Collection<SignerInformation> signers = signedData.getSignerInfos().getSigners();
        SignerInformation signerInformation = signers.iterator().next();

        Collection matches = certificatesStore.getMatches(signerInformation.getSID());
        X509CertificateHolder certificateHolder = (X509CertificateHolder) matches.iterator().next();
        X509Certificate certFromSignedData = new JcaX509CertificateConverter().getCertificate(certificateHolder);


        CertificateInfo ci = new CertificateInfo();
        psi.setCertificateInfo(ci);
        ci.setIssuerDN(certFromSignedData.getIssuerDN().toString());
        ci.setSubjectDN(certFromSignedData.getSubjectDN().toString());

        ci.setNotValidAfter(certFromSignedData.getNotAfter());
        ci.setNotValidBefore(certFromSignedData.getNotBefore());

        ci.setSignAlgorithm(certFromSignedData.getSigAlgName());
        ci.setSerial(certFromSignedData.getSerialNumber().toString());
        ci.setType(certFromSignedData.getType());

        LdapName ldapDN = new LdapName(ci.getIssuerDN());
        for (Rdn rdn : ldapDN.getRdns()) {
            ci.getIssuerOIDs().put(rdn.getType(), rdn.getValue().toString());
        }

        ldapDN = new LdapName(ci.getSubjectDN());
        for (Rdn rdn : ldapDN.getRdns()) {
            ci.getSubjectOIDs().put(rdn.getType(), rdn.getValue().toString());
            String t = rdn.getType();
            if (t.equalsIgnoreCase("SERIALNUMBER")) {
                ci.setSubjectSN(rdn.getValue().toString());
            } else if (t.equalsIgnoreCase("GIVENNAME")) {
                ci.setSubjectName(rdn.getValue().toString());
            } else if (t.equalsIgnoreCase("SURNAME")) {
                ci.setSubjectSurmame(rdn.getValue().toString());
            } else if (t.equalsIgnoreCase("o")) {
                ci.setOrganization(rdn.getValue().toString());
            } else if (t.equalsIgnoreCase("OU")) {
                ci.setOrganicUnit(rdn.getValue().toString());
            }
        }

        certFromSignedData.checkValidity(sig.getSignDate().getTime());

        if (isSelfSigned(certFromSignedData)) {
            psi.setSelfSigned(true);
        } else {
            psi.setSelfSigned(false);
        }

        if (CRLVerifier.verifyCertificateCRLs(certFromSignedData)) {
            psi.setRevoked(true);
        } else {
            psi.setRevoked(false);
        }

        if (signerInformation.verify(new JcaSimpleSignerInfoVerifierBuilder().build(certFromSignedData))) {
            psi.setSignatureVerified(true);
        } else {
            psi.setSignatureVerified(false);
        }
    }


    /**
     * Checks whether given X.509 certificate is self-signed.
     */
    private static boolean isSelfSigned(X509Certificate cert) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            // Try to verify certificate signature with its own public key
            PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        } catch (SignatureException | InvalidKeyException sigEx) {
            return false;
        }
    }

    private String parseDNIoNIE(String subjectSN) {

        Pattern pDNI = Pattern.compile("\\d{8}[" + letersNif + "]$");
        Matcher matcherDNI = pDNI.matcher(subjectSN);
        if (matcherDNI.find()) {
            return matcherDNI.group(0);
        }

        Pattern pNIE = Pattern.compile("[" + letersNie + "]\\d{7}[" + letersNif + "]$");
        Matcher matcherNIE = pNIE.matcher(subjectSN);
        if (matcherNIE.find()) {
            return matcherNIE.group(0);
        }
        return subjectSN.length() > 8 ? subjectSN.substring(subjectSN.length() - 9) : "000000000";
    }


    private static final class CRLVerifier {

        /**
         * Extracts the CRL distribution points from the certificate (if available)
         * and checks the certificate revocation status against the CRLs coming from
         * the distribution points. Supports HTTP, HTTPS, FTP and LDAP based URLs.     *
         */

        private static final Logger logger = LoggerFactory.getLogger(SignatureServiceBean.class);

        public static boolean verifyCertificateCRLs(X509Certificate cert) {
            Boolean revoked;
            try {
                List<String> crlDistPoints = getCrlDistributionPoints(cert);
                for (String crlDP : crlDistPoints) {
                    X509CRL crl = downloadCRL(crlDP);
                    revoked = crl.isRevoked(cert);
                    if (revoked) {
                        return true;
                    }
                }
            } catch (Exception e) {
                logger.error("Unexpected error verifying certificate revocation: " + e.getMessage(), e);
            }
            return false;
        }

        /**
         * Downloads CRL from given URL. Supports http, https, ftp and ldap based
         * URLs.
         */
        private static X509CRL downloadCRL(String crlURL) throws IOException,
                CertificateException, CRLException, NamingException {
            if (crlURL.startsWith("http://") || crlURL.startsWith("https://")
                    || crlURL.startsWith("ftp://")) {
                return downloadCRLFromWeb(crlURL);
            } else if (crlURL.startsWith("ldap://")) {
                return downloadCRLFromLDAP(crlURL);
            } else {
                return null;
            }
        }

        /**
         * Downloads a CRL from given LDAP url, e.g.
         * ldap://ldap.infonotary.com/dc=identity-ca,dc=infonotary,dc=com
         */
        private static X509CRL downloadCRLFromLDAP(String ldapURL) throws CertificateException,
                NamingException, CRLException {
            Map<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY,
                    "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapURL);

            DirContext ctx = new InitialDirContext((Hashtable) env);
            Attributes avals = ctx.getAttributes("");
            Attribute aval = avals.get("certificateRevocationList;binary");
            byte[] val = (byte[]) aval.get();
            if ((val == null) || (val.length == 0)) {
                return null;
            } else {
                InputStream inStream = new ByteArrayInputStream(val);
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                return (X509CRL) cf.generateCRL(inStream);
            }
        }

        /**
         * Downloads a CRL from given HTTP/HTTPS/FTP URL, e.g.
         * http://crl.infonotary.com/crl/identity-ca.crl
         */
        private static X509CRL downloadCRLFromWeb(String crlURL) throws MalformedURLException,
                IOException, CertificateException,
                CRLException {
            URL url = new URL(crlURL);
            InputStream crlStream = url.openStream();
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                return (X509CRL) cf.generateCRL(crlStream);
            } finally {
                crlStream.close();
            }
        }

        /**
         * Extracts all CRL distribution point URLs from the
         * "CRL Distribution Point" extension in a X.509 certificate. If CRL
         * distribution point extension is unavailable, returns an empty list.
         */
        public static List<String>
        getCrlDistributionPoints(X509Certificate cert) throws CertificateParsingException, IOException {
            byte[] crldpExt = cert
                    .getExtensionValue(X509Extensions.CRLDistributionPoints.getId());
            if (crldpExt == null) {
                return new ArrayList<String>();
            }
            ASN1InputStream oAsnInStream = new ASN1InputStream(
                    new ByteArrayInputStream(crldpExt));
            ASN1Primitive derObjCrlDP = oAsnInStream.readObject();
            DEROctetString dosCrlDP = (DEROctetString) derObjCrlDP;
            byte[] crldpExtOctets = dosCrlDP.getOctets();
            ASN1InputStream oAsnInStream2 = new ASN1InputStream(
                    new ByteArrayInputStream(crldpExtOctets));
            ASN1Primitive derObj2 = oAsnInStream2.readObject();
            CRLDistPoint distPoint = CRLDistPoint.getInstance(derObj2);
            List<String> crlUrls = new ArrayList<String>();
            for (DistributionPoint dp : distPoint.getDistributionPoints()) {
                DistributionPointName dpn = dp.getDistributionPoint();
                // Look for URIs in fullName
                if (dpn != null
                        && dpn.getType() == DistributionPointName.FULL_NAME) {
                    GeneralName[] genNames = GeneralNames.getInstance(
                            dpn.getName()).getNames();
                    // Look for an URI
                    for (int j = 0; j < genNames.length; j++) {
                        if (genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier) {
                            String url = DERIA5String.getInstance(
                                    genNames[j].getName()).getString();
                            crlUrls.add(url);
                        }
                    }
                }
            }
            return crlUrls;
        }

    }


}
