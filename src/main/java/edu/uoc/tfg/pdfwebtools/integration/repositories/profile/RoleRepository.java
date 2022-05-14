package edu.uoc.tfg.pdfwebtools.integration.repositories.profile;



import edu.uoc.tfg.pdfwebtools.integration.entities.Role;
import edu.uoc.tfg.pdfwebtools.integration.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Integer> {

	Role findByRole(String role);

}
