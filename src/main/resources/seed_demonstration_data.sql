-- =============================================================================
-- JEU DE DONNÉES DE DÉMONSTRATION - MALIEXPLORER
-- Opportunités B2B & Partenariats (Artisans, Promoteurs, Guides)
-- =============================================================================

USE maliexplorer_db;

-- -----------------------------------------------------------------------------
-- 1. ARTISAN 1 : Mamadou Coulibaly (Cuir de Tombouctou - VALIDÉ)
-- -----------------------------------------------------------------------------
INSERT INTO utilisateurs (prenom, nom, email, adresse, photo_url, role, points, date_creation)
VALUES (
    'Mamadou',
    'Coulibaly',
    'mamadou.coulibaly@artisanat-mali.ml',
    'Quartier Artisanat, Tombouctou',
    'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=800&q=80',
    'artisan',
    150,
    CURDATE()
);

SET @id_artisan_1 = LAST_INSERT_ID();

INSERT INTO artisans (id_users, type_artisanat, recherche_partenariat, titre_projet, besoin_partenariat, statut_moderation, motif_rejet)
VALUES (
    @id_artisan_1,
    'Maroquinerie & Cuir traditionnel de Tombouctou',
    TRUE,
    'Modernisation de l''Atelier de Cuir et Tannage Naturel',
    'Recherche d''un investisseur B2B ou partenaire commercial pour un financement d''amorçage de 3 500 000 FCFA dédié à l''achat de machines à coudre industrielles et au développement d''un canal d''exportation vers l''Europe.',
    'VALIDE',
    NULL
);

-- -----------------------------------------------------------------------------
-- 2. ARTISAN 2 : Aïssata Traoré (Bogolan de Ségou - VALIDÉ)
-- -----------------------------------------------------------------------------
INSERT INTO utilisateurs (prenom, nom, email, adresse, photo_url, role, points, date_creation)
VALUES (
    'Aïssata',
    'Traoré',
    'aissata.traore@bogolan-segou.ml',
    'Atelier Ndomo, Ségou',
    'https://images.unsplash.com/photo-1579783902614-a3fb3927b675?auto=format&fit=crop&w=800&q=80',
    'artisan',
    200,
    CURDATE()
);

SET @id_artisan_2 = LAST_INSERT_ID();

INSERT INTO artisans (id_users, type_artisanat, recherche_partenariat, titre_projet, besoin_partenariat, statut_moderation, motif_rejet)
VALUES (
    @id_artisan_2,
    'Teinture Bogolan & Tissage de coton bio malien',
    TRUE,
    'Coopérative Féminine de Bogolan Éco-responsable',
    'Recherche de partenaires pour le co-financement d''un séchoir solaire (2 000 000 FCFA) et l''intégration de nos tissus traditionnels dans des collections de mode éthique internationale.',
    'VALIDE',
    NULL
);

-- -----------------------------------------------------------------------------
-- 3. PROMOTEUR : Moussa Diakité (Festival Culturel - VALIDÉ)
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
-- 4. GUIDE TOURISTIQUE : Boubacar Cissé (Djenné - VALIDÉ)
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
-- 5. CAS DE TEST MODÉRATION ADMIN : Oumar Sangaré (EN ATTENTE DE VALIDATION)
-- -----------------------------------------------------------------------------
INSERT INTO utilisateurs (prenom, nom, email, adresse, photo_url, role, points, date_creation)
VALUES (
    'Oumar',
    'Sangaré',
    'oumar.sangare@poterie-kalaban.ml',
    'Kalaban-Coro, Bamako',
    'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=800&q=80',
    'artisan',
    50,
    CURDATE()
);

SET @id_artisan_pending = LAST_INSERT_ID();

INSERT INTO artisans (id_users, type_artisanat, recherche_partenariat, titre_projet, besoin_partenariat, statut_moderation, motif_rejet)
VALUES (
    @id_artisan_pending,
    'Poterie traditionnelle et Céramique artisanale',
    TRUE,
    'Construction d''un Four Écologique Haute Température',
    'Recherche d''un soutien financier de 1 500 000 FCFA pour réduire l''empreinte carbone de notre atelier de poterie.',
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
    COALESCE(a.titre_projet, p.titre_projet, g.titre_projet) AS titre_projet,
    COALESCE(a.recherche_partenariat, p.recherche_partenariat, g.recherche_partenariat) AS recherche_partenariat,
    COALESCE(a.statut_moderation, p.statut_moderation, g.statut_moderation) AS statut_moderation
FROM utilisateurs u
LEFT JOIN artisans a ON u.id_users = a.id_users
LEFT JOIN promoteurs p ON u.id_users = p.id_users
LEFT JOIN guides g ON u.id_users = g.id_users
WHERE COALESCE(a.recherche_partenariat, p.recherche_partenariat, g.recherche_partenariat) = TRUE;
