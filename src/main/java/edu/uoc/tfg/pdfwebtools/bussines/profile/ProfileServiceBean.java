package edu.uoc.tfg.pdfwebtools.bussines.profile;

import edu.uoc.tfg.pdfwebtools.bussines.repository.RepositoryService;
import edu.uoc.tfg.pdfwebtools.integration.entities.Folder;
import edu.uoc.tfg.pdfwebtools.integration.entities.Role;
import edu.uoc.tfg.pdfwebtools.integration.entities.User;
import edu.uoc.tfg.pdfwebtools.integration.repos.profile.RoleRepository;
import edu.uoc.tfg.pdfwebtools.integration.repos.profile.UserRepository;
import edu.uoc.tfg.pdfwebtools.integration.repos.profile.UserRoleType;
import edu.uoc.tfg.pdfwebtools.integration.repos.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class ProfileServiceBean implements ProfileService {

    BCryptPasswordEncoder passwordEncoder;
    UserRepository userRepository;

    RoleRepository roleRepository;

    RepositoryService repositoryService;

    @Autowired
    public ProfileServiceBean(BCryptPasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository,  RepositoryService repositoryService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.repositoryService = repositoryService;
    }

    @Override
    @Transactional
    public User registerUser(User user) {
        final String encryptedPassword = passwordEncoder.encode(user.getPassword());
        Set<Role> roles = new LinkedHashSet<>();
        roles.add(roleRepository.findByRole(String.valueOf(UserRoleType.USER)));
        user.setPassword(encryptedPassword);
        user.setRegisterDate(Instant.now());
        user.setRoles(roles);
        user.setIsActive(false);
        userRepository.save(user);
        Folder folder = new Folder();
        folder.setName(user.getUsername().toUpperCase() + " Repository Root Folder");
        folder.setUser(user);
        repositoryService.createFolder(user.getUsername(), folder);

        user.getFolders().add(folder);
        return user;

    }

    @Override
    @Transactional
    public User activateUser(Integer id) {
        User user = userRepository.getById(id);
        user.setIsActive(true);
        return userRepository.saveAndFlush(user);
    }

    @Override
    @Transactional
    public User deactivateUser(Integer id) {
        User user = userRepository.getById(id);
        user.setIsActive(false);
        return userRepository.saveAndFlush(user);
    }
}
