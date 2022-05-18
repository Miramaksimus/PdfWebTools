package edu.uoc.tfg.pdfwebtools.integration.repos.repository;



import edu.uoc.tfg.pdfwebtools.integration.entities.Folder;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FolderRepository extends JpaRepository<Folder, Integer> {

	Folder findByUser_Username(String username);

	Folder findByUser_UsernameAndParentFolder(String username, Folder parent);

	Folder findByUser_UsernameAndId(String username, Integer id);


}
