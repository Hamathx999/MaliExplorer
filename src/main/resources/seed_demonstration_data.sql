-- =============================================================================
-- JEU DE DONNÉES DE DÉMONSTRATION - MALIEXPLORER
-- Opportunités B2B & Partenariats (Promoteurs, Guides)
-- =============================================================================

USE maliexplorer_db;

-- -----------------------------------------------------------------------------
-- 1. PROMOTEUR : Moussa Diakité (Festival Culturel - VALIDÉ)
-- -----------------------------------------------------------------------------
INSERT INTO utilisateurs (prenom, nom, email, adresse, photo_url, role, points, date_creation)
VALUES (
    'Moussa',
    'Diakité',
    'moussa.diakite@festivalsahel.ml',
    'Badalabougou, Bamako',
    'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=800&q=80',
    'promoteur',
    350,
    CURDATE()
);

SET @id_promoteur = LAST_INSERT_ID();

INSERT INTO promoteurs (id_users, nom_organisation, piece_identite, recherche_partenariat, titre_projet, besoin_partenariat, statut_moderation, motif_rejet)
VALUES (
    @id_promoteur,
    'Agence Sahel Roots Événements',
    'ID_CARD_ML_789456',
    TRUE,
    'Festival des Masques et Traditions du Mali 2027',
    'Recherche de sponsors et investisseurs hôteliers/touristiques à hauteur de 10 000 000 FCFA pour la logistique scénique, la promotion internationale et l''accueil des touristes.',
    'VALIDE',
    NULL
);

-- -----------------------------------------------------------------------------
-- 2. GUIDE TOURISTIQUE : Boubacar Cissé (Djenné - VALIDÉ)
-- -----------------------------------------------------------------------------
INSERT INTO utilisateurs (prenom, nom, email, adresse, photo_url, role, points, date_creation)
VALUES (
    'Boubacar',
    'Cissé',
    'boubacar.cisse@guidedjenne.ml',
    'Quartier Djoboro, Djenné',
    'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=800&q=80',
    'guide',
    400,
    CURDATE()
);

SET @id_guide = LAST_INSERT_ID();

INSERT INTO guides (id_users, langue, description, experience, piece_identite, recherche_partenariat, titre_projet, besoin_partenariat, statut_moderation, motif_rejet)
VALUES (
    @id_guide,
    'Bambara, Français, Anglais, Espagnol',
    'Guide certifié patrimoine mondial UNESCO, 12 ans d''expérience dans les visites architecturales en banco et la traversée du fleuve Bani.',
    12,
    'ID_GUIDE_PRO_123',
    TRUE,
    'Éco-Circuits Fluviaux Djenné-Mopti en Pirogues Solaires',
    'Recherche d''un partenaire technique et financier spécialisé dans l''énergie solaire pour l''acquisition de 2 moteurs électriques solaires silencieux (5 000 000 FCFA).',
    'VALIDE',
    NULL
);

-- -----------------------------------------------------------------------------
-- 3. CAS DE TEST MODÉRATION ADMIN : Aminata Diallo (EN ATTENTE DE VALIDATION)
-- -----------------------------------------------------------------------------
INSERT INTO utilisateurs (prenom, nom, email, adresse, photo_url, role, points, date_creation)
VALUES (
    'Aminata',
    'Diallo',
    'aminata.diallo@guide-segou.ml',
    'Ségou Coura, Ségou',
    'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=800&q=80',
    'guide',
    100,
    CURDATE()
);

SET @id_guide_pending = LAST_INSERT_ID();

INSERT INTO guides (id_users, langue, description, experience, piece_identite, recherche_partenariat, titre_projet, besoin_partenariat, statut_moderation, motif_rejet)
VALUES (
    @id_guide_pending,
    'Français, Bambara, Peul',
    'Guide culturelle et patrimoniale spécialisée dans l''histoire des royaumes Bambara et les rives du fleuve Niger.',
    6,
    'ID_GUIDE_PRO_456',
    TRUE,
    'Circuit Pédestre Historique des Palais Royaux de Ségou',
    'Recherche d''un partenaire pour la digitalisation du parcours en audioguides multilingues (2 000 000 FCFA).',
    'EN_ATTENTE_VALIDATION',
    NULL
);

-- =============================================================================
-- VÉRIFICATION DU RÉSULTAT DU SEED
-- =============================================================================
SELECT 
    u.id_users, 
    CONCAT(u.prenom, ' ', u.nom) AS nom_complet, 
    u.role,
    u.email,
    COALESCE(p.titre_projet, g.titre_projet) AS titre_projet,
    COALESCE(p.recherche_partenariat, g.recherche_partenariat) AS recherche_partenariat,
    COALESCE(p.statut_moderation, g.statut_moderation) AS statut_moderation
FROM utilisateurs u
LEFT JOIN promoteurs p ON u.id_users = p.id_users
LEFT JOIN guides g ON u.id_users = g.id_users
WHERE COALESCE(p.recherche_partenariat, g.recherche_partenariat) = TRUE;
