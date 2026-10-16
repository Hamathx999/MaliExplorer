package com.maliexplorer_backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReligionRequestDTO {

    @NotBlank(message = "Le nom de la religion ou tradition est obligatoire")
    private String nom;

    private String categorie;

    @JsonAlias({"descriptionHistorique", "description"})
    private String description;

    @JsonAlias({"descriptionHistorique", "histoire"})
    private String histoire;

    @JsonAlias({"pratiquesEtFetes", "pratiques"})
    private String pratiques;

    private String fetes;

    @JsonAlias({"lieuxEmblematiques", "lieuxDeCulte"})
    private String lieuxDeCulte;

    private String regionsConcernees;

    @JsonAlias({"courantsOuBranches", "branches"})
    @JsonDeserialize(using = BranchesDeserializer.class)
    private String branches;

    @JsonAlias({"pourcentageEstime", "proportionPopulation"})
    @JsonDeserialize(using = ProportionPopulationDeserializer.class)
    private String proportionPopulation;

    private String nombrePratiquantsEstime;

    @JsonAlias({"sourceStatistique", "sourceStatistiques"})
    private String sourceStatistiques;

    @JsonAlias({"anneeStatistique", "anneeStatistiques"})
    private String anneeStatistiques;

    private String methodologieStatistiques;
    private String imageUrl;

    @JsonAlias({"ordreAffichage", "ordre"})
    private Integer ordre;

    @JsonAlias({"estPublie", "publie"})
    private Boolean publie;

    public static class BranchesDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            if (node == null || node.isNull()) {
                return null;
            }
            if (node.isArray()) {
                List<String> list = new ArrayList<>();
                for (JsonNode item : node) {
                    if (item != null && !item.asText().isBlank()) {
                        list.add(item.asText().trim());
                    }
                }
                return list.isEmpty() ? null : String.join("\n• ", list);
            }
            return node.asText();
        }
    }

    public static class ProportionPopulationDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            if (node == null || node.isNull()) {
                return null;
            }
            return node.asText();
        }
    }
}

