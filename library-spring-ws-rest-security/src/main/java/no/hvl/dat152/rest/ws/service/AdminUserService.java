/**
 * 
 */
package no.hvl.dat152.rest.ws.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Role;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.RoleRepository;
import no.hvl.dat152.rest.ws.repository.UserRepository;

import java.util.Set;

/**
 * @author tdoy
 */
@Service
public class AdminUserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	public User saveUser(User user) {
		
		user = userRepository.save(user);
		
		return user;
	}
	
	// TODO public User deleteUserRole(Long id, String role)
	public User deleteUserRole(Long id, String role) throws UserNotFoundException{
		User user = findUser(id);
		Role userRole = findRole(role);
		Set<Role> roles = user.getRoles();
		roles.remove(userRole);
		return saveUser(user);
	}
	
	// TODO public User updateUserRole(Long id, String role)
	public User updateUserRole(Long id, String role) throws UserNotFoundException{
		User user = findUser(id);
		Role userRole = findRole(role);

		Set<Role> roles = user.getRoles();
		roles.add(userRole);
		return saveUser(user);
	}

	private Role findRole(String role) {
		return roleRepository.findByName(role.toUpperCase());
	}

	public User findUser(Long id) throws UserNotFoundException {
		
		User user = userRepository.findById(id)
				.orElseThrow(()-> new UserNotFoundException("User with id: "+id+" not found"));
		
		return user;
	}


}
