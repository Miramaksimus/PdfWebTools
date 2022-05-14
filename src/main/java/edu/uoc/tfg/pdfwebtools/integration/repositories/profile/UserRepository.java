package edu.uoc.tfg.pdfwebtools.integration.repositories.profile;



import edu.uoc.tfg.pdfwebtools.integration.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Integer> {

	User findByUsername(String username);

}
