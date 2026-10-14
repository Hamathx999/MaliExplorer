package com.maliexplorer_backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleModel {
    touriste,
    artisan,
    guide,
    promoteur,
    partenaire,
    investisseur,
    admin,
    superAdmin;

    @JsonCreator
    public static RoleModel fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return touriste;
        }
        String clean = value.trim().toLowerCase();
        if (clean.contains("super")) return superAdmin;
        if (clean.contains("admin")) return admin;
        if (clean.contains("guide")) return guide;
        if (clean.contains("artisan")) return artisan;
        if (clean.contains("promoteur")) return promoteur;
        if (clean.contains("partenaire")) return partenaire;
        if (clean.contains("investisseur")) return investisseur;
        if (clean.contains("touriste") || clean.contains("explorateur") || clean.contains("visiteur")) return touriste;

        for (RoleModel r : RoleModel.values()) {
            if (r.name().equalsIgnoreCase(clean)) {
                return r;
            }
        }
        return touriste;
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
