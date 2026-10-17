package com.onana.decoevent.functional;

import com.fasterxml.jackson.databind.JsonNode;
import com.onana.decoevent.enums.ModePaiement;
import com.onana.decoevent.enums.StatutFacture;
import com.onana.decoevent.models.Facture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests fonctionnels du flux de paiement : requêtes HTTP réelles de bout en bout,
 * sur le profil "test".
 *
 * L'accent est mis sur la vérification du montant, règle la plus sensible du service :
 * un paiement doit être unique et intégral par facture.
 */
public class PaiementFunctionalTest extends AbstractFunctionalTest {

    // ================================================================ Montant

    @Test
    @DisplayName("Un paiement au montant exact de la facture est accepté et la facture devient PAYE")
    void paiementMontantExactEstAccepte() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.00", ModePaiement.CASH))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.montant").value(170000.0))
                .andExpect(jsonPath("$.modePaiement").value("CASH"))
                .andExpect(jsonPath("$.factureId").value(facture.getId()));

        Facture apres = factureRepository.findById(facture.getId()).orElseThrow();
        assertThat(apres.getStatutFacture()).isEqualTo(StatutFacture.PAYE);
    }

    @Test
    @DisplayName("Un montant inférieur est refusé : c'est un paiement partiel")
    void paiementMontantInferieurEstRefuse() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "169999.99", ModePaiement.CASH))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("ne correspond pas")));

        assertThat(paiementRepository.findByFactureId(facture.getId())).isEmpty();
        assertThat(factureRepository.findById(facture.getId()).orElseThrow().getStatutFacture())
                .isEqualTo(StatutFacture.EN_ATTENTE);
    }

    @Test
    @DisplayName("Un montant supérieur est refusé : le surplus n'a pas de sens ici")
    void paiementMontantSuperieurEstRefuse() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.01", ModePaiement.CASH))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("ne correspond pas")));
    }

    @Test
    @DisplayName("La comparaison ignore l'échelle décimale, comme BigDecimal.compareTo()")
    void montantAvecEchelleDecimaleDifferenteEstAccepte() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(new BigDecimal("170000.00"));

        // 170000.0 et 170000.00 sont égaux pour compareTo(), contrairement à equals().
        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.0", ModePaiement.CASH))))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Un montant à 3 décimales est refusé par la validation")
    void montantTropDeDecimalesEstRefuse() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.001", ModePaiement.CASH))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("maximum 2 décimales")));
    }

    @Test
    @DisplayName("Un montant négatif est refusé")
    void montantNegatifEstRefuse() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "-100", ModePaiement.CASH))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("positif")));
    }

    @Test
    @DisplayName("Un montant absent est refusé")
    void montantAbsentEstRefuse() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, null, ModePaiement.CASH))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Montant obligatoire")));
    }

    // ============================================================ ModePaiement

    @Test
    @DisplayName("Les trois modes de paiement déclarés par l'enum Java sont acceptés")
    void tousLesModesPaiementDeLEnumSontAcceptes() throws Exception {
        String token = tokenPrestataire();

        for (ModePaiement mode : ModePaiement.values()) {
            Facture facture = creerFactureEnAttente(new BigDecimal("1000.00"));

            mockMvc.perform(post(BASE_URL_PAIEMENTS)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(requetePaiement(facture, "1000.00", mode))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.modePaiement").value(mode.name()));
        }
    }

    @Test
    @DisplayName("Un mode de paiement hors enum est refusé en 400")
    void modePaiementInconnuEstRefuse() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        // "BITCOIN" n'existe pas dans l'enum Java ModePaiement.
        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.00", null, "BITCOIN"))))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Un mode de paiement absent est refusé")
    void modePaiementAbsentEstRefuse() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.00", null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Mode paiement obligatoire")));
    }

    // ========================================================= Règles métier

    @Test
    @DisplayName("Payer une facture inexistante renvoie 404")
    void factureInexistanteRenvoie404() throws Exception {
        String token = tokenPrestataire();

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "factureId": 999999,
                                  "montant": 170000.00,
                                  "modePaiement": "CASH",
                                  "datePaiement": "%s"
                                }
                                """.formatted(LocalDate.now())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Facture non trouvée")));
    }

    @Test
    @DisplayName("Payer une facture déjà payée est refusé")
    void factureDejaPayeeEstRefusee() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureDejaPayee(MONTANT_170_000());

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.00", ModePaiement.CASH))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("déjà payée")));
    }

    @Test
    @DisplayName("Un second paiement sur la même facture est refusé car elle est désormais payée")
    void secondPaiementSurMemeFactureEstRefuse() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.00", ModePaiement.CASH))))
                .andExpect(status().isCreated());

        // Le premier paiement a basculé la facture en PAYE : c'est ce garde-fou,
        // testé avant celui du paiement déjà existant, qui bloque le second essai.
        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.00", ModePaiement.VIREMENT))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("déjà payée")));

        assertThat(paiementRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Un paiement existant bloque la création même si la facture n'est plus marquée payée")
    void paiementDejaExistantBloqueMemeSiFactureNonPayee() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.00", ModePaiement.CASH))))
                .andExpect(status().isCreated());

        // On repasse la facture en attente pour isolate le garde-fou suivant :
        // c'est le cas où le statut et l'existence du paiement divergent.
        facture.setStatutFacture(StatutFacture.EN_ATTENTE);
        factureRepository.save(facture);

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.00", ModePaiement.VIREMENT))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Un paiement existe déjà")));

        assertThat(paiementRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Supprimer un paiement remet la facture en attente de paiement")
    void supprimerPaiementRemetLaFactureEnAttente() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        Long paiementId = lireJson(mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.00", ModePaiement.CASH))))
                .andExpect(status().isCreated())
                .andReturn()).get("id").asLong();

        assertThat(factureRepository.findById(facture.getId()).orElseThrow().getStatutFacture())
                .isEqualTo(StatutFacture.PAYE);

        mockMvc.perform(delete(BASE_URL_PAIEMENTS + "/" + paiementId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(paiementRepository.findById(paiementId)).isEmpty();
        // La facture ne doit pas rester marquée payée sans paiement associé.
        assertThat(factureRepository.findById(facture.getId()).orElseThrow().getStatutFacture())
                .isEqualTo(StatutFacture.EN_ATTENTE);
    }

    @Test
    @DisplayName("Après suppression, un nouveau paiement de la même facture est possible")
    void paiementEstPossibleApresSuppression() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        Long paiementId = lireJson(mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.00", ModePaiement.CASH))))
                .andExpect(status().isCreated())
                .andReturn()).get("id").asLong();

        mockMvc.perform(delete(BASE_URL_PAIEMENTS + "/" + paiementId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.00", ModePaiement.VIREMENT))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.modePaiement").value("VIREMENT"));
    }

    @Test
    @DisplayName("Supprimer un paiement inexistant renvoie 404")
    void supprimerPaiementInexistantRenvoie404() throws Exception {
        String token = tokenPrestataire();

        mockMvc.perform(delete(BASE_URL_PAIEMENTS + "/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Une facture en retard peut être payée")
    void factureEnRetardPeutEtrePayee() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());
        facture.setStatutFacture(StatutFacture.EN_RETARD);
        factureRepository.save(facture);

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.00", ModePaiement.CASH))))
                .andExpect(status().isCreated());

        assertThat(factureRepository.findById(facture.getId()).orElseThrow().getStatutFacture())
                .isEqualTo(StatutFacture.PAYE);
    }

    // ======================================================== Lecture / sécurité

    @Test
    @DisplayName("La liste des paiements est vide au départ et contient le paiement créé")
    void listeDesPaiementsReflecteLesPaiements() throws Exception {
        String token = tokenPrestataire();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        mockMvc.perform(get(BASE_URL_PAIEMENTS).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.00", ModePaiement.CASH))))
                .andExpect(status().isCreated());

        mockMvc.perform(get(BASE_URL_PAIEMENTS).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].clientNom").isNotEmpty());
    }

    @Test
    @DisplayName("Sans token, l'accès aux paiements est refusé (403 faute d'AuthenticationEntryPoint)")
    void accesSansTokenEstRefuse() throws Exception {
        // L'API ne déclare aucun AuthenticationEntryPoint : Spring Security répond 403
        // et non 401, l'accès anonyme étant traité comme un refus d'autorisation.
        // Comportement observé, conservé tel quel pour le moment.
        mockMvc.perform(get(BASE_URL_PAIEMENTS))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un client ne peut pas accéder aux paiements du prestataire")
    void accesClientEstRefuse() throws Exception {
        String token = tokenClient();

        mockMvc.perform(get(BASE_URL_PAIEMENTS).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un administrateur ne peut pas accéder aux paiements du prestataire")
    void accesAdminEstRefuse() throws Exception {
        String token = tokenAdmin();

        mockMvc.perform(get(BASE_URL_PAIEMENTS).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un client ne peut pas enregistrer de paiement")
    void creationPaiementParClientEstRefusee() throws Exception {
        String token = tokenClient();
        Facture facture = creerFactureEnAttente(MONTANT_170_000());

        mockMvc.perform(post(BASE_URL_PAIEMENTS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requetePaiement(facture, "170000.00", ModePaiement.CASH))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un token invalide est rejeté sans provoquer d'erreur serveur")
    void tokenInvalideEstRejete() throws Exception {
        // Un token malformé ne doit pas remonter d'exception hors du filtre JWT.
        // La requête est ensuite refusée par les règles d'accès : comme aucun
        // AuthenticationEntryPoint n'est déclaré, la réponse est un 403.
        mockMvc.perform(get(BASE_URL_PAIEMENTS).header("Authorization", "Bearer token-bidon"))
                .andExpect(status().isForbidden());
    }

    // ================================================================= Helpers

    private RequetePaiement requetePaiement(Facture facture, String montant, ModePaiement mode) {
        return new RequetePaiement(facture.getId(), montant, mode == null ? null : mode.name(),
                LocalDate.now().toString());
    }

    /** Surcharge pour injecter une valeur brute, y compris hors enum. */
    private RequetePaiement requetePaiement(Facture facture, String montant, ModePaiement mode, String modeBrut) {
        return new RequetePaiement(facture.getId(), montant, modeBrut, LocalDate.now().toString());
    }

    /**
     * Le montant est envoyé en texte et non en nombre : cela permet de couvrir
     * les cas invalides (null, 3 décimales) sans que la construction de la
     * requête échoue côté Java.
     */
    private record RequetePaiement(Long factureId, String montant, String modePaiement, String datePaiement) {}
}