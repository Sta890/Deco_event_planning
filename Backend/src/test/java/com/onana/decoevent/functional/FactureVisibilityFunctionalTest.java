package com.onana.decoevent.functional;

import com.fasterxml.jackson.databind.JsonNode;
import com.onana.decoevent.enums.TypeEvenement;
import com.onana.decoevent.models.Article;
import com.onana.decoevent.models.Devis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de diagnostic du parcours complet devis → facture, vu du client.
 *
 * Question à laquelle ce test répond : lorsque le prestataire génère une facture,
 * le client connecté la voit-il dans « Mon compte » ?
 *
 * Le parcours part de la demande de devis du client, car c'est le seul chemin
 * réel aujourd'hui : l'inscription (/api/auth/inscription) ne crée pas de fiche
 * Client, c'est creerDepuisDemande() qui la crée en rattachant par email.
 *
 * Tout passe par les vraies APIs avec de vrais tokens JWT, à une exception
 * volontaire : la récupération de l'identifiant du devis, faite via le repository
 * parce que la réponse de la demande de devis est une prestation qui ne l'expose pas.
 * Ce n'est pas l'objet du test.
 */
public class FactureVisibilityFunctionalTest extends AbstractFunctionalTest {

    private static final String BASE_URL_DEVIS = "/api/prestataire/devis";
    private static final String BASE_URL_DEMANDES = "/api/client/demandes-devis";
    private static final String BASE_URL_MES_FACTURES = "/api/client/mes-factures";
    private static final String BASE_URL_MES_DEVIS = "/api/client/mes-devis";

    @Test
    @DisplayName("Parcours complet : le client voit la facture générée par le prestataire")
    void leClientVoitLaFactureGenereeParLePrestataire() throws Exception {

        // ---------------------------------------------------------------- 1. Inscription
        String email = "jean.dupont@example.cm";
        String tokenClient = inscrire(email, "Jean Dupont", "Jean Dupont");

        // À ce stade, aucune fiche Client n'existe encore : l'inscription ne crée
        // qu'un Utilisateur. On le vérifie pour documenter d'où vient le rattachement.
        assertThat(clientRepository.findByEmail(email))
                .as("l'inscription ne doit pas créer de fiche Client")
                .isEmpty();

        // ------------------------------------------------- 2. Demande de devis du client
        String tokenPrestataire = connecterPrestataire();

        mockMvc.perform(post(BASE_URL_DEMANDES)
                        .header("Authorization", "Bearer " + tokenClient)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new DemandeDevis(
                                "Jean Dupont",
                                "655123456",
                                email,
                                TypeEvenement.MARIAGE.name(),
                                LocalDate.now().plusMonths(3).toString(),
                                "Palais des Congrès",
                                150,
                                "Mariage/bootstrap 150 personnes"
                        ))))
                .andExpect(status().isCreated());

        assertThat(clientRepository.findByEmail(email))
                .as("la demande de devis doit créer la fiche Client")
                .isPresent();

        // ---------------------------------- 3. Le prestataire chiffre le devis auto-généré
        Long prestationId = prestationRepository.findAll().get(0).getId();
        Devis devis = devisRepository.findByPrestationId(prestationId).get(0);

        // Le devis créé automatiquement est à 0 XAF : on lui ajoute une ligne pour
        // obtenir une facture représentative.
        Article article = articleRepository.save(Article.builder()
                .nom("Chaise dorée")
                .description("Chaise style royal")
                .prixUnitaire(new BigDecimal("2500.00"))
                .typeEvenement(TypeEvenement.MARIAGE)
                .build());

        mockMvc.perform(put(BASE_URL_DEVIS + "/" + devis.getId() + "/lignes")
                        .header("Authorization", "Bearer " + tokenPrestataire)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(java.util.List.of(new LigneDevis(article.getId(), 68)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montantTotal").value(170000.0));

        // ------------------------------------------------------ 4. Validation du devis
        mockMvc.perform(put(BASE_URL_DEVIS + "/" + devis.getId() + "/statut")
                        .param("statut", "VALIDE")
                        .header("Authorization", "Bearer " + tokenPrestataire))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statutDevis").value("VALIDE"));

        // ------------------------------------------------- 5. Génération de la facture
        mockMvc.perform(post(BASE_URL_FACTURES + "/generer/" + devis.getId())
                        .header("Authorization", "Bearer " + tokenPrestataire))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statutFacture").value("EN_ATTENTE"))
                .andExpect(jsonPath("$.montantTotal").value(170000.0));

        Long factureId = factureRepository.findByDevisId(devis.getId()).orElseThrow().getId();

        // ------------------------------------- 6. Le client consulte « Mon compte »
        JsonNode mesFactures = lireJson(mockMvc.perform(get(BASE_URL_MES_FACTURES)
                        .header("Authorization", "Bearer " + tokenClient))
                .andExpect(status().isOk())
                .andReturn());

        assertThat(mesFactures.isArray()).isTrue();
        assertThat(mesFactures)
                .as("le client doit voir la facture générée par le prestataire")
                .hasSize(1);
        assertThat(mesFactures.get(0).get("id").asLong()).isEqualTo(factureId);
        assertThat(mesFactures.get(0).get("statutFacture").asText()).isEqualTo("EN_ATTENTE");
        assertThat(mesFactures.get(0).get("montantTotal").asDouble()).isEqualTo(170000.0);

        // Le devis suit le même chemin de visibilité.
        mockMvc.perform(get(BASE_URL_MES_DEVIS)
                        .header("Authorization", "Bearer " + tokenClient))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(devis.getId()));
    }

    @Test
    @DisplayName("Une facture n'est visible que par le client concerné, pas par les autres")
    void uneFactureEstVisibleParSonTitulaireSeul() throws Exception {
        String emailJean = "jean@example.cm";
        String tokenJean = inscrire(emailJean, "Jean", "Jean Dupont");

        demanderDevis(tokenJean, emailJean, "Jean", "MARIAGE");
        String tokenPrestataire = connecterPrestataire();
        preparerFacture(tokenPrestataire, new BigDecimal("50000.00"));

        // Un second client, sans rapport avec la facture.
        String tokenAutre = inscrire("marie@example.cm", "Marie", "Marie Curie");
        demanderDevis(tokenAutre, "marie@example.cm", "Marie", "BAPTEME");

        JsonNode facturesJean = lireJson(mockMvc.perform(get(BASE_URL_MES_FACTURES)
                        .header("Authorization", "Bearer " + tokenJean))
                .andExpect(status().isOk())
                .andReturn());
        assertThat(facturesJean).hasSize(1);

        JsonNode facturesMarie = lireJson(mockMvc.perform(get(BASE_URL_MES_FACTURES)
                        .header("Authorization", "Bearer " + tokenAutre))
                .andExpect(status().isOk())
                .andReturn());

        // Marie n'a demandé qu'un devis, aucune facture n'a été générée pour elle.
        assertThat(facturesMarie)
                .as("un client ne doit pas voir les factures d'un autre client")
                .isEmpty();
    }

    // ================================================================= Helpers

    /** Inscription réelle via l'API, retourne le token JWT. */
    private String inscrire(String email, String nom, String telephone) throws Exception {
        MvcResult resultat = mockMvc.perform(post(BASE_URL_AUTH + "/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new Inscription(nom, email, MOT_DE_PASSE, telephone, "Yaoundé"))))
                .andExpect(status().isOk())
                .andReturn();
        return lireJson(resultat).get("token").asText();
    }

    private String connecterPrestataire() throws Exception {
        String email = "prestataire@test.cm";
        creerUtilisateur(email, com.onana.decoevent.enums.Role.PRESTATAIRE);
        return login(email, MOT_DE_PASSE);
    }

    /** Soumet une demande de devis pour le client authentifié. */
    private void demanderDevis(String token, String email, String nom, String typeEvenement) throws Exception {
        mockMvc.perform(post(BASE_URL_DEMANDES)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new DemandeDevis(
                                nom, "655000000", email,
                                typeEvenement,
                                LocalDate.now().plusMonths(2).toString(),
                                "Yaoundé", 100, null))))
                .andExpect(status().isCreated());
    }

    /** Chiffre, valide et facture le devis le plus récent. */
    private void preparerFacture(String token, BigDecimal montant) throws Exception {
        Devis devis = devisRepository.findAll().get(0);

        Article article = articleRepository.save(Article.builder()
                .nom("Article test " + System.nanoTime())
                .description("Article")
                .prixUnitaire(montant)
                .typeEvenement(TypeEvenement.MARIAGE)
                .build());

        mockMvc.perform(put(BASE_URL_DEVIS + "/" + devis.getId() + "/lignes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(java.util.List.of(new LigneDevis(article.getId(), 1)))))
                .andExpect(status().isOk());

        mockMvc.perform(put(BASE_URL_DEVIS + "/" + devis.getId() + "/statut")
                        .param("statut", "VALIDE")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post(BASE_URL_FACTURES + "/generer/" + devis.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
    }

    private record Inscription(String nom, String email, String motDePasse, String telephone, String adresse) {}

    private record DemandeDevis(String nom, String telephone, String email, String typeEvenement,
                                String dateEvenement, String lieu, int nombrePersonnes, String message) {}

    private record LigneDevis(Long articleId, int quantite) {}
}