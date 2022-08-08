package ru.maxryazan.bankterminal.security;


import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.maxryazan.bankterminal.model.Client;
import ru.maxryazan.bankterminal.service.ClientService;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final ClientService clientService;


    public MyUserDetailsService(ClientService clientService) {
        this.clientService = clientService;

    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        Client client = clientService.findByPhoneNumber(phoneNumber);

        return User.builder()
                .username(client.getPhoneNumber())
                .password(client.getPinCode())
                .roles()
                .build();
    }

}
