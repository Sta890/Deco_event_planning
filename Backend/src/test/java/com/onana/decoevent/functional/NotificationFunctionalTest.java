package com.onana.decoevent.functional;

import com.fasterxml.jackson.databind.JsonNode;
import com.onana.decoevent.models.Article;
import com.onana.decoevent.enums.TypeEvenement;
import com.onana.decoevent.models.Devis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Notifications adressées au client.
 *
 * Vérifie que le client est prévenu quand le prestataire tranche sur son devis,
 * ce qui est précisément ce qu'il ne pouvait pas savoir autrement : la liste de
 * ses devis n'était rechargée qu'à l'ouverture de la page.
 */
public class NotificationFunctionalTest extends AbstractFunctionalTest {

    private static final String BASE_URL_DEVIS = "/api/prestataire/devis";
    private static final String BASE_URL_DEMANDES = "/api/client/demandes-devis";
    private static final String BASE_URL_NOTIFS = "/api/client/notifications";

    @Test
    @DisplayName("Le client est prévenu quand son devis est validé")
    void clientEstPrevenuDeLaValidationDuDevis() throws Exception {
        String email = "jean@example.cm";
        String tokenClient = inscrireClient(email);
        demanderDevis(tokenClient, email);

        String tokenPrestataire = connecterPrestataire();

        // Aucune notification tant que le prestataire n'a pas tranché.
        assertThat(lireNotifications(tokenClient))
                .as("rien à signaler avant la décision du prestataire")
                .isEmpty();

        Devis devis = devisRepository.findAll().get(0);
        validerDevis(tokenPrestataire, devis.getId(), "VALIDE");

        JsonNode notifications = lireNotifications(tokenClient);

        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).get("type").asText()).isEqualTo("DEVIS_VALIDE");
        assertThat(notifications.get(0).get("titre").asText()).contains("validé");
        assertThat(notifications.get(0).get("referenceId").asLong()).isEqualTo(devis.getId());
        assertThat(notifications.get(0).get("lue").asBoolean()).isFalse();
    }

    @Test
    @DisplayName("Le client est prévenu quand son devis est refusé")
    void clientEstPrevenuDuRefusDuDevis() throws Exception {
        String email = "marie@example.cm";
        String tokenClient = inscrireClient(email);
        demanderDevis(tokenClient, email);

        String tokenPrestataire = connecterPrestataire();
        Devis devis = devisRepository.findAll().get(0);
        validerDevis(tokenPrestataire, devis.getId(), "REFUSE");

        JsonNode notifications = lireNotifications(tokenClient);

        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).get("type").asText()).isEqualTo("DEVIS_REFUSE");
    }

    @Test
    @DisplayName("Marquer une notification comme lue la retire du compteur")
    void marquerUneNotificationCommeLue() throws Exception {
        String email = "paul@example.cm";
        String tokenClient = inscrireClient(email);
        demanderDevis(tokenClient, email);

        String tokenPrestataire = connecterPrestataire();
        Devis devis = devisRepository.findAll().get(0);
        validerDevis(tokenPrestataire, devis.getId(), "VALIDE");

        assertThat(compteurNonLues(tokenClient)).isEqualTo(1);

        long notificationId = lireNotifications(tokenClient).get(0).get("id").asLong();

        mockMvc.perform(put(BASE_URL_NOTIFS + "/" + notificationId + "/lue")
                        .header("Authorization", "Bearer " + tokenClient))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lue").value(true));

        assertThat(compteurNonLues(tokenClient)).isZero();

        mockMvc.perform(put(BASE_URL_NOTIFS + "/tout-lire")
                        .header("Authorization", "Bearer " + tokenClient))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Un client ne peut pas lire la notification d'un autre client")
    void unClientNePeutPasLireLaNotificationDUnAutre() throws Exception {
        String emailJean = "jean@example.cm";
        String tokenJean = inscrireClient(emailJean);
        demanderDevis(tokenJean, emailJean);

        String tokenPrestataire = connecterPrestataire();
        Devis devis = devisRepository.findAll().get(0);
        validerDevis(tokenPrestataire, devis.getId(), "VALIDE");

        long notificationId = lireNotifications(tokenJean).get(0).get("id").asLong();

        // Un autre client tente de marquer la notification de Jean comme lue.
        String tokenMarie = inscrireClient("marie@example.cm");

        mockMvc.perform(put(BASE_URL_NOTIFS + "/" + notificationId + "/lue")
                        .header("Authorization", "Bearer " + tokenMarie))
                .andExpect(status().isNotFound());

        assertThat(compteurNonLues(tokenJean))
                .as("la notification de Jean doit rester non lue")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Le client est prévenu de la génération de sa facture et du paiement")
    void clientEstPrevenuDeLaFactureEtDuPaiement() throws Exception {
        String email = "sophie@example.cm";
        String tokenClient = inscrireClient(email);
        demanderDevis(tokenClient, email);

        String tokenPrestataire = connecterPrestataire();
        Devis devis = devisRepository.findAll().get(0);

        // Le devis créé automatiquement vaut 0 XAF : on le chiffre pour obtenir
        // une facture payable (un montant de 0 est refusé par la validation).
        chiffrerDevis(tokenPrestataire, devis.getId(), "25000.00");
        validerDevis(tokenPrestataire, devis.getId(), "VALIDE");

        mockMvc.perform(post(BASE_URL_FACTURES + "/generer/" + devis.getId())
                        .header("Authorization", "Bearer " + tokenPrestataire))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.montantTotal").value(25000.0));

        long factureId = factureRepository.findByDevisId(devis.getId()).orElseThrow().getId();

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + tokenPrestataire)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new PaiementPayload(
                                factureId, "25000.00", "CASH", LocalDate.now().toString()))))
                .andExpect(status().isCreated());

        JsonNode notifications = lireNotifications(tokenClient);

        List<String> types = notifications.findValuesAsText("type");
        assertThat(types).contains("DEVIS_VALIDE", "FACTURE_EMISE", "PAIEMENT_RECUS");
    }

    // ================================================================= Helpers

    private JsonNode lireNotifications(String token) throws Exception {
        return lireJson(mockMvc.perform(get(BASE_URL_NOTIFS)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn());
    }

    private long compteurNonLues(String token) throws Exception {
        return lireJson(mockMvc.perform(get(BASE_URL_NOTIFS + "/non-lues")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()).get("count").asLong();
    }

    private String inscrireClient(String email) throws Exception {
        return lireJson(mockMvc.perform(post(BASE_URL_AUTH + "/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new InscriptionPayload(
                                "Client Test", email, MOT_DE_PASSE, "655000000", "Yaoundé"))))
                .andExpect(status().isOk())
                .andReturn()).get("token").asText();
    }

    private String connecterPrestataire() throws Exception {
        String email = "prestataire@test.cm";
        creerUtilisateur(email, com.onana.decoevent.enums.Role.PRESTATAIRE);
        return login(email, MOT_DE_PASSE);
    }

    private void demanderDevis(String token, String email) throws Exception {
        mockMvc.perform(post(BASE_URL_DEMANDES)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new DemandeDevisPayload(
                                "Client Test",
                                "655000000",
                                email,
                                "MARIAGE",
                                LocalDate.now().plusMonths(2).toString(),
                                "Yaoundé",
                                100,
                                null))))
                .andExpect(status().isCreated());
    }

    /** Ajoute une ligne au devis pour lui donner un montant non nul. */
    private void chiffrerDevis(String token, Long devisId, String prixUnitaire) throws Exception {
        Article article = articleRepository.save(Article.builder()
                .nom("Article " + System.nanoTime())
                .description("Article de test")
                .prixUnitaire(new BigDecimal(prixUnitaire))
                .typeEvenement(TypeEvenement.MARIAGE)
                .build());

        mockMvc.perform(put(BASE_URL_DEVIS + "/" + devisId + "/lignes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(List.of(new LigneDevisPayload(article.getId(), 1)))))
                .andExpect(status().isOk());
    }

    private void validerDevis(String token, Long devisId, String statut) throws Exception {
        mockMvc.perform(put(BASE_URL_DEVIS + "/" + devisId + "/statut")
                        .param("statut", statut)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private record InscriptionPayload(String nom, String email, String motDePasse,
                                      String telephone, String adresse) {}

    private record DemandeDevisPayload(String nom, String telephone, String email, String typeEvenement,
                                    String dateEvenement, String lieu, int nombrePersonnes, String message) {}

    private record PaiementPayload(Long factureId, String montant, String modePaiement, String datePaiement) {}

    private record LigneDevisPayload(Long articleId, int quantite) {}
}