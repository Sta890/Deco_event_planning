package com.onana.decoevent.functional;

import com.fasterxml.jackson.databind.JsonNode;
import com.onana.decoevent.enums.Role;
import com.onana.decoevent.enums.TypeEvenement;
import com.onana.decoevent.models.Article;
import com.onana.decoevent.models.Devis;
import com.onana.decoevent.models.Facture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test du chiffrage du devis issu d'une demande client.
 *
 * Question à laquelle ce test répond : lorsqu'un client envoie une demande de devis
 * en incluant les articles de son panier, le devis généré automatiquement est-il
 * chiffré (et non plus à 0) ? Et une facture ne peut-elle plus être émise sur un
 * devis vide ?
 */
public class DevisChiffrageFunctionalTest extends AbstractFunctionalTest {

    private static final String BASE_URL_DEVIS = "/api/prestataire/devis";
    private static final String BASE_URL_DEMANDES = "/api/client/demandes-devis";
    private static final String BASE_URL_MES_DEVIS = "/api/client/mes-devis";

    @Test
    @DisplayName("Une demande avec lignes chiffre le devis auto-généré et la facture suit")
    void uneDemandeAvecLignesChiffreLeDevis() throws Exception {
        String email = "chiffrage@example.cm";
        String tokenClient = inscrire(email, "Client Chiffrage", "655111222");
        String tokenPrestataire = connecterPrestataire();

        Article article = articleRepository.save(Article.builder()
                .nom("Chaise dorée")
                .description("Chaise style royal")
                .prixUnitaire(new BigDecimal("2500.00"))
                .typeEvenement(TypeEvenement.MARIAGE)
                .build());

        // 68 chaises × 2500 = 170 000 XAF, chiffré dès la demande.
        mockMvc.perform(post(BASE_URL_DEMANDES)
                        .header("Authorization", "Bearer " + tokenClient)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new DemandeDevis(
                                "Client Chiffrage", "655111222", email,
                                TypeEvenement.MARIAGE.name(),
                                LocalDate.now().plusMonths(1).toString(),
                                "Palais des Congrès", 150, "68 chaises",
                                List.of(new LigneDevis(article.getId(), 68))))))
                .andExpect(status().isCreated());

        Long prestationId = prestationRepository.findAll().get(0).getId();
        Devis devis = devisRepository.findByPrestationId(prestationId).get(0);

        assertThat(devis.getMontantTotal())
                .as("le devis auto-généré doit être chiffré, plus à 0")
                .isEqualByComparingTo("170000.00");

        // Le client voit son devis chiffré, avec ses lignes.
        mockMvc.perform(get(BASE_URL_MES_DEVIS).header("Authorization", "Bearer " + tokenClient))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(devis.getId()))
                .andExpect(jsonPath("$[0].montantTotal").value(170000.0))
                .andExpect(jsonPath("$[0].lignes.length()").value(1));

        // Le prestataire valide puis génère la facture : elle reprend le montant.
        mockMvc.perform(put(BASE_URL_DEVIS + "/" + devis.getId() + "/statut")
                        .param("statut", "VALIDE")
                        .header("Authorization", "Bearer " + tokenPrestataire))
                .andExpect(status().isOk());

        mockMvc.perform(post(BASE_URL_FACTURES + "/generer/" + devis.getId())
                        .header("Authorization", "Bearer " + tokenPrestataire))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.montantTotal").value(170000.0));
    }

    @Test
    @DisplayName("Générer une facture depuis un devis sans article est refusé")
    void factureRefuseePourDevisSansMontant() throws Exception {
        String email = "sanslignes@example.cm";
        String tokenClient = inscrire(email, "Client Sans Lignes", "655000000");
        String tokenPrestataire = connecterPrestataire();

        mockMvc.perform(post(BASE_URL_DEMANDES)
                        .header("Authorization", "Bearer " + tokenClient)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new DemandeDevis(
                                "Client Sans Lignes", "655000000", email,
                                TypeEvenement.BAPTEME.name(),
                                LocalDate.now().plusMonths(2).toString(),
                                "Yaoundé", 50, null, null))))
                .andExpect(status().isCreated());

        Devis devis = devisRepository.findAll().get(0);
        assertThat(devis.getMontantTotal()).isEqualByComparingTo("0");

        mockMvc.perform(put(BASE_URL_DEVIS + "/" + devis.getId() + "/statut")
                        .param("statut", "VALIDE")
                        .header("Authorization", "Bearer " + tokenPrestataire))
                .andExpect(status().isOk());

        mockMvc.perform(post(BASE_URL_FACTURES + "/generer/" + devis.getId())
                        .header("Authorization", "Bearer " + tokenPrestataire))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("aucun article")));

        assertThat(factureRepository.count()).isZero();
    }

    @Test
    @DisplayName("Un paiement de montant nul est refusé")
    void paiementMontantNulRefuse() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new RequetePaiement(facture.getId(), "0", "CASH",
                                LocalDate.now().toString()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("positif")));

        assertThat(paiementRepository.count()).isZero();
    }

    // ================================================================= Helpers

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
        creerUtilisateur(email, Role.PRESTATAIRE);
        return login(email, MOT_DE_PASSE);
    }

    private record Inscription(String nom, String email, String motDePasse, String telephone, String adresse) {}

    private record DemandeDevis(String nom, String telephone, String email, String typeEvenement,
                                String dateEvenement, String lieu, int nombrePersonnes, String message,
                                List<LigneDevis> lignes) {}

    private record LigneDevis(Long articleId, int quantite) {}

    private record RequetePaiement(Long factureId, String montant, String modePaiement, String datePaiement) {}
}
