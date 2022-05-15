package edu.uoc.tfg.pdfwebtools.integration.repos.profile;



import edu.uoc.tfg.pdfwebtools.integration.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Integer> {

	Role findByRole(String role);

}
