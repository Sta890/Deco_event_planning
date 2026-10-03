package com.onana.decoevent.config;
import com.onana.decoevent.enums.Role;
import com.onana.decoevent.enums.StatutDevis;
import com.onana.decoevent.enums.StatutFacture;
import com.onana.decoevent.enums.TypeEvenement;
import com.onana.decoevent.models.Article;
import com.onana.decoevent.models.Client;
import com.onana.decoevent.models.Devis;
import com.onana.decoevent.models.Facture;
import com.onana.decoevent.models.Prestation;
import com.onana.decoevent.models.Utilisateur;
import com.onana.decoevent.repositories.ArticleRepository;
import com.onana.decoevent.repositories.ClientRepository;
import com.onana.decoevent.repositories.DevisRepository;
import com.onana.decoevent.repositories.FactureRepository;
import com.onana.decoevent.repositories.PrestationRepository;
import com.onana.decoevent.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;
    private final ArticleRepository articleRepository;
    private final PrestationRepository prestationRepository;
    private final DevisRepository devisRepository;
    private final FactureRepository factureRepository;
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
            log.info("Admin créé !");
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
            log.info("Prestataire créé !");
        }

        if (!utilisateurRepository.existsByEmail("client@facturepro.cm")) {
            utilisateurRepository.save(
                    Utilisateur.builder()
                            .nom("Hôtel Renaissance")
                            .email("client@facturepro.cm")
                            .motDePasse(passwordEncoder.encode("1234"))
                            .telephone("655000002")
                            .adresse("Yaoundé")
                            .role(Role.CLIENT)
                            .build()
            );
            log.info("Client créé !");
        }

        if (!clientRepository.existsByEmail("client@facturepro.cm")) {
            Client client = clientRepository.save(
                    Client.builder()
                            .nom("Hôtel Renaissance")
                            .telephone("655000002")
                            .adresse("Yaoundé")
                            .email("client@facturepro.cm")
                            .build()
            );

            Prestation prestation = prestationRepository.save(
                    Prestation.builder()
                            .typeEvenement(TypeEvenement.MARIAGE)
                            .dateEvenement(LocalDate.now().plusMonths(2))
                            .lieu("Hôtel Renaissance, Yaoundé")
                            .description("Décoration complète mariage")
                            .client(client)
                            .build()
            );

            Devis devis = devisRepository.save(
                    Devis.builder()
                            .dateCreation(LocalDate.now())
                            .montantTotal(new BigDecimal("170000.00"))
                            .statutDevis(StatutDevis.VALIDE)
                            .prestation(prestation)
                            .build()
            );

            factureRepository.save(
                    Facture.builder()
                            .dateFacture(LocalDate.now())
                            .montantTotal(new BigDecimal("170000.00"))
                            .statutFacture(StatutFacture.EN_ATTENTE)
                            .devis(devis)
                            .build()
            );

            log.info("Client, prestation, devis et facture de démonstration créés !");
        }

        if (articleRepository.count() == 0) {
            articleRepository.saveAll(List.of(
                    Article.builder().nom("Chaise dorée").description("Chaise dorée style royal").prixUnitaire(new BigDecimal("2500.00")).typeEvenement(TypeEvenement.MARIAGE).build(),
                    Article.builder().nom("Table ronde").description("Table ronde 8 personnes").prixUnitaire(new BigDecimal("15000.00")).typeEvenement(TypeEvenement.AUTRE).build(),
                    Article.builder().nom("Arche florale").description("Arche fleurs naturelles").prixUnitaire(new BigDecimal("45000.00")).typeEvenement(TypeEvenement.MARIAGE).build(),
                    Article.builder().nom("Ballon décoratif").description("Pack 50 ballons colorés").prixUnitaire(new BigDecimal("7000.00")).typeEvenement(TypeEvenement.BAPTEME).build(),
                    Article.builder().nom("Sono événementielle").description("Système son complet").prixUnitaire(new BigDecimal("80000.00")).typeEvenement(TypeEvenement.AUTRE).build(),
                    Article.builder().nom("Photobooth").description("Espace photo décoré").prixUnitaire(new BigDecimal("35000.00")).typeEvenement(TypeEvenement.ANNIVERSAIRE).build()
            ));
            log.info("Catalogue d'articles initialisé !");
        }
    }
}
