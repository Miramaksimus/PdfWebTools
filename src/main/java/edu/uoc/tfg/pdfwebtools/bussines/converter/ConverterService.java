package edu.uoc.tfg.pdfwebtools.bussines.converter;

import edu.uoc.tfg.pdfwebtools.integration.entities.Document;

public interface ConverterService {
  
    Document convertDocxToPfd(Document doc) throws Exception;

    Document convertPfdToDocx(Document doc) throws Exception;
}
