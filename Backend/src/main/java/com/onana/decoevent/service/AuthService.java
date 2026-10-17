package com.onana.decoevent.service;
import com.onana.decoevent.dto.response.AuthResponse;
import com.onana.decoevent.dto.request.InscriptionRequest;
import com.onana.decoevent.dto.request.LoginRequest;
import com.onana.decoevent.enums.Role;
import com.onana.decoevent.mapper.UtilisateurMapper;
import com.onana.decoevent.models.Utilisateur;
import com.onana.decoevent.repositories.UtilisateurRepository;
import com.onana.decoevent.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.onana.decoevent.exceptions.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse login(LoginRequest dto) throws BadRequestException {
        log.info("Tentative de connexion - Email: {}", dto.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getMotDePasse())
        );

        Utilisateur utilisateur = utilisateurRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadRequestException("Utilisateur non trouvé"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());
        String token = jwtService.generateToken(userDetails);

        AuthResponse response = utilisateurMapper.toAuthResponse(utilisateur);
        response.setToken(token);

        log.info("Connexion réussie - Email: {}, Role: {}", utilisateur.getEmail(), utilisateur.getRole());
        return response;
    }

    @Transactional
    public AuthResponse inscrire(InscriptionRequest dto) throws BadRequestException {
        log.info("Tentative d'inscription - Email: {}", dto.getEmail());

        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            log.warn("Echec inscription : l'email {} existe déjà", dto.getEmail());
            throw new BadRequestException("Email déjà utilisé : " + dto.getEmail());
        }

        Utilisateur utilisateur = utilisateurMapper.toEntity(dto);
        utilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        utilisateur.setRole(Role.CLIENT);
        utilisateur = utilisateurRepository.save(utilisateur);

        UserDetails userDetails = userDetailsService.loadUserByUsername(utilisateur.getEmail());
        String token = jwtService.generateToken(userDetails);

        AuthResponse response = utilisateurMapper.toAuthResponse(utilisateur);
        response.setToken(token);

        log.info("Inscription réussie - Email: {}, Role: {}", utilisateur.getEmail(), utilisateur.getRole());
        return response;
    }
}