package edu.uoc.tfg.pdfwebtools.bussines.signature;

import edu.uoc.tfg.pdfwebtools.integration.entities.Document;

import java.io.IOException;

public interface SignatureService {

    Document scantSignaturesFromPfd(Document doc) throws IOException, SignatureCheckerException;
}
