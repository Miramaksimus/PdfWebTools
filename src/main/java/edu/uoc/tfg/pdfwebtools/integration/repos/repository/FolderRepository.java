package edu.uoc.tfg.pdfwebtools.integration.repos.repository;



import edu.uoc.tfg.pdfwebtools.integration.entities.Folder;
import edu.uoc.tfg.pdfwebtools.integration.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FolderRepository extends JpaRepository<Folder, Integer> {

	Folder findByUser(User user);


}
