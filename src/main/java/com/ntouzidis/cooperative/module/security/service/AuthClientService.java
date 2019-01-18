package com.ntouzidis.cooperative.module.security.service;

import com.ntouzidis.cooperative.module.security.entity.AuthClient;
import com.ntouzidis.cooperative.module.security.entity.AuthClientAuthority;
import com.ntouzidis.cooperative.module.security.entity.AuthClientScope;
import com.ntouzidis.cooperative.module.security.entity.AuthGrantType;
import com.ntouzidis.cooperative.module.security.repository.AuthClientAuthorityRepository;
import com.ntouzidis.cooperative.module.security.repository.AuthClientRepository;
import com.ntouzidis.cooperative.module.security.repository.AuthClientScopeRepository;
import com.ntouzidis.cooperative.module.security.repository.AuthGrantTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthClientService {

    private final AuthClientRepository authClientRepository;
    private final AuthGrantTypeRepository authGrantTypeRepository;
    private final AuthClientScopeRepository authClientScopeRepository;
    private final AuthClientAuthorityRepository authClientAuthorityRepository;

    public AuthClientService(AuthClientRepository authClientRepository,
                             AuthGrantTypeRepository authGrantTypeRepository,
                             AuthClientScopeRepository authClientScopeRepository,
                             AuthClientAuthorityRepository authClientAuthorityRepository) {
        this.authClientRepository = authClientRepository;
        this.authGrantTypeRepository = authGrantTypeRepository;
        this.authClientScopeRepository = authClientScopeRepository;
        this.authClientAuthorityRepository = authClientAuthorityRepository;
    }

    Optional<AuthClient> findByClientId(String clientId) {
        return authClientRepository.findByClientId(clientId);
    }

    List<AuthClientScope> findScopesByAuthClientId(Integer authClientId) {
        return authClientScopeRepository.findByClientId(authClientId);
    }

    List<AuthGrantType> findGrantTypesByAuthClientId(Integer authClientId) {
        return authGrantTypeRepository.findByClientId(authClientId);
    }

    List<AuthClientAuthority> findAuthoritiesByAuthClientId(Integer authClientId) {
        return authClientAuthorityRepository.findByClientId(authClientId);
    }
}
