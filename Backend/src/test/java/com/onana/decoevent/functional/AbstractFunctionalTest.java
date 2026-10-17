package com.onana.decoevent.functional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onana.decoevent.enums.Role;
import com.onana.decoevent.enums.StatutDevis;
import com.onana.decoevent.enums.StatutFacture;
import com.onana.decoevent.enums.TypeEvenement;
import com.onana.decoevent.models.Client;
import com.onana.decoevent.models.Devis;
import com.onana.decoevent.models.Facture;
import com.onana.decoevent.models.Panier;
import com.onana.decoevent.models.Prestation;
import com.onana.decoevent.models.Utilisateur;
import com.onana.decoevent.repositories.ArticleRepository;
import com.onana.decoevent.repositories.ClientRepository;
import com.onana.decoevent.repositories.DevisRepository;
import com.onana.decoevent.repositories.FactureRepository;
import com.onana.decoevent.repositories.HistoriqueRepository;
import com.onana.decoevent.repositories.LigneDevisRepository;
import com.onana.decoevent.repositories.LignePanierRepository;
import com.onana.decoevent.repositories.NotificationRepository;
import com.onana.decoevent.repositories.PaiementRepository;
import com.onana.decoevent.repositories.PanierRepository;
import com.onana.decoevent.repositories.PrestationRepository;
import com.onana.decoevent.repositories.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Socle des tests fonctionnels : démarre le contexte applicatif complet sur une base
 * H2 en mémoire (profil "test"), expose MockMvc et fournit les helpers de mise en place.
 *
 * Les tests construisent leurs données via les repositories plutôt que via l'API : seul
 * le scénario testé doit passer par HTTP, la préparation reste directe et lisible.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractFunctionalTest {

    /** Mot de passe en clair ; le hachage BCrypt est produit par le PasswordEncoder. */
    protected static final String MOT_DE_PASSE = "Test1234!";

    protected static final String BASE_URL_AUTH = "/api/auth";
    protected static final String BASE_URL_PAIEMENTS = "/api/prestataire/paiements";
    protected static final String BASE_URL_FACTURES = "/api/prestataire/factures";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected UtilisateurRepository utilisateurRepository;
    @Autowired
    protected ClientRepository clientRepository;
    @Autowired
    protected PrestationRepository prestationRepository;
    @Autowired
    protected DevisRepository devisRepository;
    @Autowired
    protected LigneDevisRepository ligneDevisRepository;
    @Autowired
    protected FactureRepository factureRepository;
    @Autowired
    protected PaiementRepository paiementRepository;
    @Autowired
    protected PanierRepository panierRepository;
    @Autowired
    protected LignePanierRepository lignePanierRepository;
    @Autowired
    protected ArticleRepository articleRepository;
    @Autowired
    protected HistoriqueRepository historiqueRepository;
    @Autowired
    protected NotificationRepository notificationRepository;

    /**
     * Purge les tables avant chaque test pour que les assertions ne dépendent pas
     * de l'ordre d'exécution. L'ordre respecte les dépendances de clés étrangères :
     * on supprime les enfants avant les parents.
     */
    @BeforeEach
    void nettoyerDonnees() {
        paiementRepository.deleteAll();
        notificationRepository.deleteAll();
        factureRepository.deleteAll();
        ligneDevisRepository.deleteAll();
        devisRepository.deleteAll();
        lignePanierRepository.deleteAll();
        panierRepository.deleteAll();
        prestationRepository.deleteAll();
        articleRepository.deleteAll();
        historiqueRepository.deleteAll();
        utilisateurRepository.deleteAll();
        clientRepository.deleteAll();
    }

    // ----------------------------------------------------------------- Comptes

    protected Utilisateur creerUtilisateur(String email, Role role) {
        return utilisateurRepository.save(
                Utilisateur.builder()
                        .nom("Test " + role.name())
                        .email(email)
                        .motDePasse(passwordEncoder.encode(MOT_DE_PASSE))
                        .telephone("655000000")
                        .adresse("Yaoundé")
                        .role(role)
                        .build()
        );
    }

    /** Connexion réelle via l'API d'authentification, comme le ferait le frontend. */
    protected String login(String email, String motDePasse) throws Exception {
        MvcResult resultat = mockMvc.perform(post(BASE_URL_AUTH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginPayload(email, motDePasse))))
                .andReturn();

        return objectMapper.readTree(resultat.getResponse().getContentAsString())
                .get("token")
                .asText();
    }

    /** Token d'un compte PRESTATAIRE créé pour l'occasion. */
    protected String tokenPrestataire() throws Exception {
        String email = "prestataire@test.cm";
        creerUtilisateur(email, Role.PRESTATAIRE);
        return login(email, MOT_DE_PASSE);
    }

    protected String tokenClient() throws Exception {
        String email = "client@test.cm";
        creerUtilisateur(email, Role.CLIENT);
        return login(email, MOT_DE_PASSE);
    }

    protected String tokenAdmin() throws Exception {
        String email = "admin@test.cm";
        creerUtilisateur(email, Role.ADMIN);
        return login(email, MOT_DE_PASSE);
    }

    // --------------------------------------------------------------- Factures

    /** Client sans utilisateur associé : suffisant pour les scénarios de paiement. */
    protected Client creerClient(String nom) {
        return clientRepository.save(
                Client.builder()
                        .nom(nom)
                        .telephone("655000001")
                        .adresse("Yaoundé")
                        .email(nom.toLowerCase() + "@test.cm")
                        .build()
        );
    }

    /**
     * Facture EN_ATTENTE créée via la chaîne Client → Prestation → Devis(VALIDE) → Facture.
     * Le devis doit être VALIDE pour que le flux métier reste cohérent, même si
     * genererDepuisDevis() n'est pas appelé ici.
     */
    protected Facture creerFactureEnAttente(BigDecimal montant) {
        Client client = creerClient("Client Test " + System.nanoTime());

        Prestation prestation = prestationRepository.save(
                Prestation.builder()
                        .typeEvenement(TypeEvenement.MARIAGE)
                        .dateEvenement(LocalDate.now().plusMonths(2))
                        .lieu("Yaoundé")
                        .description("Prestation de test")
                        .client(client)
                        .build()
        );

        Devis devis = devisRepository.save(
                Devis.builder()
                        .dateCreation(LocalDate.now())
                        .montantTotal(montant)
                        .statutDevis(StatutDevis.VALIDE)
                        .prestation(prestation)
                        .build()
        );

        return factureRepository.save(
                Facture.builder()
                        .dateFacture(LocalDate.now())
                        .montantTotal(montant)
                        .statutFacture(StatutFacture.EN_ATTENTE)
                        .devis(devis)
                        .build()
        );
    }

    /** Facture marquée payée sans paiement associé, pour tester le garde-fou. */
    protected Facture creerFactureDejaPayee(BigDecimal montant) {
        Facture facture = creerFactureEnAttente(montant);
        facture.setStatutFacture(StatutFacture.PAYE);
        return factureRepository.save(facture);
    }

    /** Montant utilisé par défaut par les tests de paiement. */
    protected static BigDecimal MONTANT_170_000() {
        return new BigDecimal("170000.00");
    }

    /** Sérialise un corps de requête JSON. */
    protected String json(Object valeur) throws Exception {
        return objectMapper.writeValueAsString(valeur);
    }

    protected JsonNode lireJson(MvcResult resultat) throws Exception {
        return objectMapper.readTree(resultat.getResponse().getContentAsString());
    }

    /** Corps minimal d'une requête de connexion. */
    protected record LoginPayload(String email, String motDePasse) {}
}