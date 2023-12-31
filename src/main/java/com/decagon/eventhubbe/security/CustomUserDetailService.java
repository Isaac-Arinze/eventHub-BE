package com.decagon.eventhubbe.security;

import com.decagon.eventhubbe.domain.entities.AppUser;
import com.decagon.eventhubbe.domain.repository.AppUserRepository;
import com.decagon.eventhubbe.exception.AppUserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final AppUserRepository appUserRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(()->new AppUserNotFoundException(email));
        return new User(appUser.getEmail(),appUser.getPassword(),new ArrayList<>());
    }
}
