package com.travellerapp.business;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserServiceImpl user;
	
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	com.travellerapp.domain.User u= user.getUserByEmail(email);
        return new User(u.getEmail(), u.getFirstName(),new ArrayList<>());
    }
}