package edu.uoc.tfg.pdfwebtools.bussines.profile;

import edu.uoc.tfg.pdfwebtools.integration.entities.User;

public interface ProfileService {

    User registerUser(User user);

    User activateUser(Integer id);

    User deactivateUser(Integer id);
}
