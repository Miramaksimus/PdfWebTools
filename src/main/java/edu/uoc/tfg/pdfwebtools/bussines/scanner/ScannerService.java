package edu.uoc.tfg.pdfwebtools.bussines.scanner;

import edu.uoc.tfg.pdfwebtools.integration.entities.Document;

import java.io.IOException;

public interface ScannerService {

    Document scanCodes(Document doc) throws IOException;


}
