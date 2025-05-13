package ca.sheridancollege.vonghil.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ca.sheridancollege.vonghil.beans.User;
import ca.sheridancollege.vonghil.database.DatabaseAccess;

@Service
public class UserService {
    
    @Autowired
    private DatabaseAccess databaseAccess; 
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public void registerNewUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getEncryptedPassword());
        user.setEncryptedPassword(encodedPassword);
        Long userId = databaseAccess.saveUser(user); 
        databaseAccess.assignUserRole(userId, "ROLE_USER"); 
    }
}

