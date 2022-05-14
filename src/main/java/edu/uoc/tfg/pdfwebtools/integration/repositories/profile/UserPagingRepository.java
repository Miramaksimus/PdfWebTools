package edu.uoc.tfg.pdfwebtools.integration.repositories.profile;


import edu.uoc.tfg.pdfwebtools.integration.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserPagingRepository extends PagingAndSortingRepository<User, Integer> {
    Page<User> findByUsernameContainingOrNameContainingAllIgnoreCase(
            String username,
            String name,
            Pageable pageable);

}
