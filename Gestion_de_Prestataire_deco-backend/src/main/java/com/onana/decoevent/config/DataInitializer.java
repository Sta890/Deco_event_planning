package com.onana.decoevent.config;
import com.onana.decoevent.enums.Role;
import com.onana.decoevent.models.Utilisateur;
import com.onana.decoevent.repostories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (!utilisateurRepository.existsByEmail("admin@facturepro.cm")) {
            utilisateurRepository.save(
                    Utilisateur.builder()
                            .nom("Administrateur")
                            .email("admin@facturepro.cm")
                            .motDePasse(passwordEncoder.encode("1234"))
                            .telephone("655000000")
                            .adresse("Yaoundé")
                            .role(Role.ADMIN)
                            .build()
            );
            System.out.println(" Admin créé !");
        }

        if (!utilisateurRepository.existsByEmail("decorateur@facturepro.cm")) {
            utilisateurRepository.save(
                    Utilisateur.builder()
                            .nom("Décorateur Événementiel")
                            .email("decorateur@facturepro.cm")
                            .motDePasse(passwordEncoder.encode("1234"))
                            .telephone("655000001")
                            .adresse("Yaoundé")
                            .role(Role.PRESTATAIRE)
                            .build()
            );
            System.out.println(" Prestataire créé !");
        }
    }
}
