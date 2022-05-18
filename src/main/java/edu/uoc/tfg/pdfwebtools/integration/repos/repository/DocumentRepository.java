package edu.uoc.tfg.pdfwebtools.integration.repos.repository;

import edu.uoc.tfg.pdfwebtools.integration.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Integer> {

}
