package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.*;
import com.maliexplorer_backend.model.*;
import com.maliexplorer_backend.repository.*;
import com.maliexplorer_backend.serviceimpl.DashboardStatsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour DashboardStatsServiceImpl.
 * Vérifie l'exactitude des calculs mathématiques, l'intégrité des séries temporelles,
 * la couverture des 16 régions administratives et la cohérence de la modération.
 */
@ExtendWith(MockitoExtension.class)
class DashboardStatsServiceImplTest {

    @Mock private LieuHistoriqueRepository lieuHistoriqueRepository;
    @Mock private VilleRepository villeRepository;
    @Mock private PlatRepository platRepository;
    @Mock private QuizRepository quizRepository;
    @Mock private EvenementRepository evenementRepository;
    @Mock private PresidentRepository presidentRepository;
    @Mock private EthnieRepository ethnieRepository;
    @Mock private ArticleRepository articleRepository;
    @Mock private utilisateurRepository utilisateurRepository;
    @Mock private PromoteurRepository promoteurRepository;
    @Mock private GuideRepository guideRepository;
    @Mock private HistoriquePointRepository historiquePointRepository;
    @Mock private FavoriRepository favoriRepository;
    @Mock private RegionRepository regionRepository;

    @InjectMocks
    private DashboardStatsServiceImpl dashboardStatsService;

    private List<utilisateurModel> mockUsers;
    private List<EvenementModel> mockEvents;
    private List<PromoteurModel> mockPromoteursEnAttente;
    private List<GuideModel> mockGuidesEnAttente;
    private List<EvenementModel> mockEventsEnAttente;

    @BeforeEach
    void setUp() {
        LocalDate today = LocalDate.now();

        // 1. Utilisateurs mockés
        mockUsers = new ArrayList<>();
        utilisateurModel userActif1 = new utilisateurModel();
        userActif1.setIdUsers(1);
        userActif1.setPrenom("Moussa");
        userActif1.setNom("Diarra");
        userActif1.setEmail("moussa@example.com");
        userActif1.setRole(RoleModel.touriste);
        userActif1.setStatut("ACTIF");
        userActif1.setDateCreation(Date.valueOf(today));
        mockUsers.add(userActif1);

        utilisateurModel userActif2 = new utilisateurModel();
        userActif2.setIdUsers(2);
        userActif2.setPrenom("Awa");
        userActif2.setNom("Coulibaly");
        userActif2.setEmail("awa@example.com");
        userActif2.setRole(RoleModel.promoteur);
        userActif2.setStatut("ACTIF");
        userActif2.setDateCreation(Date.valueOf(today.minusDays(2)));
        mockUsers.add(userActif2);

        utilisateurModel userInactif = new utilisateurModel();
        userInactif.setIdUsers(3);
        userInactif.setPrenom("Ibrahim");
        userInactif.setNom("Keita");
        userInactif.setEmail("ibrahim@example.com");
        userInactif.setRole(RoleModel.touriste);
        userInactif.setStatut("INACTIF");
        userInactif.setDateCreation(Date.valueOf(today.minusDays(10)));
        mockUsers.add(userInactif);

        // 2. Partenaires en attente de modération
        mockPromoteursEnAttente = new ArrayList<>();
        PromoteurModel p = new PromoteurModel();
        p.setIdUsers(20);
        p.setPrenom("Amadou");
        p.setNom("Sangaré");
        p.setNomOrganisation("Festival des Masques");
        p.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        p.setDateCreation(Date.valueOf(today));
        mockPromoteursEnAttente.add(p);

        mockGuidesEnAttente = new ArrayList<>();
        GuideModel g = new GuideModel();
        g.setIdUsers(30);
        g.setPrenom("Bakary");
        g.setNom("Touré");
        g.setLangue("Français, Bambara, Dogon");
        g.setExperience(7);
        g.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        g.setDateCreation(Date.valueOf(today.minusDays(1)));
        mockGuidesEnAttente.add(g);

        // 3. Événements mockés
        mockEvents = new ArrayList<>();
        EvenementModel eFutur1 = new EvenementModel();
        eFutur1.setId(101L);
        eFutur1.setTitre("Biennale Artistique et Culturelle 2026");
        eFutur1.setDateDebut(today.plusDays(15).toString());
        eFutur1.setDateSoumission(LocalDateTime.now().minusDays(1));
        eFutur1.setStatut(EvenementModel.Statut.VALIDE);
        eFutur1.setVille("Bamako");
        mockEvents.add(eFutur1);

        EvenementModel eFutur2 = new EvenementModel();
        eFutur2.setId(102L);
        eFutur2.setTitre("Festival sur le Niger 2027");
        eFutur2.setDateDebut("01 Fév 2027");
        eFutur2.setDateSoumission(LocalDateTime.now().minusDays(2));
        eFutur2.setStatut(EvenementModel.Statut.VALIDE);
        eFutur2.setVille("Ségou");
        mockEvents.add(eFutur2);

        EvenementModel ePasse = new EvenementModel();
        ePasse.setId(103L);
        ePasse.setTitre("Édition Passée 2024");
        ePasse.setDateDebut("2024-01-10");
        ePasse.setDateSoumission(LocalDateTime.now().minusYears(2));
        ePasse.setStatut(EvenementModel.Statut.VALIDE);
        ePasse.setVille("Mopti");
        mockEvents.add(ePasse);

        EvenementModel eAttente = new EvenementModel();
        eAttente.setId(104L);
        eAttente.setTitre("Concert Mandé Traditionnel");
        eAttente.setDateDebut(today.plusDays(5).toString());
        eAttente.setDateSoumission(LocalDateTime.now());
        eAttente.setStatut(EvenementModel.Statut.EN_ATTENTE);
        eAttente.setVille("Bamako");
        mockEvents.add(eAttente);

        mockEventsEnAttente = List.of(eAttente);
    }

    @Test
    @DisplayName("Calcul consolidé des 5 KPIs supérieurs sur données réelles")
    void testCalculConsolideKPIs_TousCompteursExacts() {
        // Configuration des counts pour les 8 entités patrimoniales
        when(lieuHistoriqueRepository.count()).thenReturn(20L);
        when(villeRepository.count()).thenReturn(15L);
        when(platRepository.count()).thenReturn(12L);
        when(quizRepository.count()).thenReturn(18L);
        when(evenementRepository.count()).thenReturn(10L);
        when(presidentRepository.count()).thenReturn(8L);
        when(ethnieRepository.count()).thenReturn(11L);
        when(articleRepository.count()).thenReturn(14L);

        when(utilisateurRepository.findAll()).thenReturn(mockUsers);
        when(evenementRepository.findAll()).thenReturn(mockEvents);
        when(promoteurRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(mockPromoteursEnAttente);
        when(guideRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(mockGuidesEnAttente);
        when(evenementRepository.findByStatutOrderByDateSoumissionDesc(EvenementModel.Statut.EN_ATTENTE))
                .thenReturn(mockEventsEnAttente);

        // Mock des partenaires
        utilisateurModel promoteurActif = mockUsers.get(1); // Awa Coulibaly (promoteur)
        when(utilisateurRepository.findByRoleIn(anyList())).thenReturn(List.of(promoteurActif));

        when(historiquePointRepository.findAll()).thenReturn(Collections.emptyList());
        when(favoriRepository.findAll()).thenReturn(Collections.emptyList());
        when(articleRepository.findAll()).thenReturn(Collections.emptyList());
        when(villeRepository.findAll()).thenReturn(Collections.emptyList());
        when(lieuHistoriqueRepository.findAll()).thenReturn(Collections.emptyList());
        when(regionRepository.findAll()).thenReturn(Collections.emptyList());

        // Exécution
        DashboardStatsResponseDTO res = dashboardStatsService.getDashboardStats();

        // Vérifications
        assertNotNull(res);
        assertNotNull(res.getKpis());
        KpisDTO kpis = res.getKpis();

        // 1. Contenus publiés = somme exacte des 8 entités (20+15+12+18+10+8+11+14 = 108)
        assertEquals(108L, kpis.getContenusPublies());
        assertEquals("+12%", kpis.getContenusPubliesDelta());

        // 2. En attente = promoteurs (1) + guides (1) + événements (1) = 3
        assertEquals(3L, kpis.getEnAttenteValidation());
        assertEquals("Prioritaire", kpis.getEnAttenteValidationDelta());

        // 3. Utilisateurs actifs = 2 (userActif1 et userActif2, userInactif exclu)
        assertEquals(2L, kpis.getUtilisateursActifs());
        assertEquals("+8%", kpis.getUtilisateursActifsDelta());

        // 4. Partenaires actifs = 1 (Awa Coulibaly)
        assertEquals(1L, kpis.getPartenaires());

        // 5. Événements à venir = 3 (eFutur1, eFutur2, eAttente ; ePasse exclu)
        assertEquals(3L, kpis.getEvenementsAVenir());
        assertEquals("À venir", kpis.getEvenementsAVenirDelta());
    }

    @Test
    @DisplayName("Génération et cohérence des séries temporelles (7j, 30j, 12m)")
    void testSeriesTemporelles_StructureEtCalculsExacts() {
        LocalDate today = LocalDate.now();

        // Création d'un historique de point aujourd'hui
        HistoriquePointModel hp = new HistoriquePointModel();
        hp.setIdHistorique(1L);
        hp.setDateGain(today.atTime(10, 0));
        hp.setPointsGagnes(50);

        // Création d'un favori aujourd'hui
        FavoriModel fav = new FavoriModel();
        fav.setIdFavori(1L);
        fav.setDateAjout(today.atTime(11, 0));

        when(utilisateurRepository.findAll()).thenReturn(mockUsers);
        when(evenementRepository.findAll()).thenReturn(mockEvents);
        when(historiquePointRepository.findAll()).thenReturn(List.of(hp));
        when(favoriRepository.findAll()).thenReturn(List.of(fav));
        when(articleRepository.findAll()).thenReturn(Collections.emptyList());
        when(villeRepository.findAll()).thenReturn(Collections.emptyList());
        when(lieuHistoriqueRepository.findAll()).thenReturn(Collections.emptyList());
        when(regionRepository.findAll()).thenReturn(Collections.emptyList());

        DashboardStatsResponseDTO res = dashboardStatsService.getDashboardStats();

        assertNotNull(res.getSeriesTemporelles());
        SeriesTemporellesDTO st = res.getSeriesTemporelles();

        // 7 jours : 7 points distincts
        assertNotNull(st.getSeptJours());
        assertEquals(7, st.getSeptJours().size());

        // Le dernier point (aujourd'hui) doit comptabiliser l'inscription de userActif1, le point hp et le favori fav
        PointTemporelDTO dernierPoint = st.getSeptJours().get(6);
        assertEquals(today.toString(), dernierPoint.getDate());
        assertTrue(dernierPoint.getInscriptions() >= 1);
        assertTrue(dernierPoint.getActivites() >= 2); // 1 point + 1 favori (+ éventuel événement)
        assertTrue(dernierPoint.getValeur() >= 3);

        // 30 jours : 4 intervalles S1, S2, S3, S4
        assertNotNull(st.getTrenteJours());
        assertEquals(4, st.getTrenteJours().size());
        assertEquals("S1", st.getTrenteJours().get(0).getLabel());
        assertEquals("S4", st.getTrenteJours().get(3).getLabel());

        // 12 mois : 12 points mensuels
        assertNotNull(st.getDouzeMois());
        assertEquals(12, st.getDouzeMois().size());
    }

    @Test
    @DisplayName("Calcul précis de la répartition patrimoniale par catégorie avec pourcentages réels")
    void testContenusParCategorie_CalculPourcentagesReels() {
        // Configuration : Lieux=50, Villes=20, Plats=15, Articles=10, Quiz=5 (Total = 100)
        when(lieuHistoriqueRepository.count()).thenReturn(50L);
        when(villeRepository.count()).thenReturn(20L);
        when(platRepository.count()).thenReturn(15L);
        when(articleRepository.count()).thenReturn(10L);
        when(quizRepository.count()).thenReturn(5L);

        DashboardStatsResponseDTO res = dashboardStatsService.getDashboardStats();

        assertNotNull(res.getContenusParCategorie());
        List<ContenuCategorieDTO> cats = res.getContenusParCategorie();
        assertEquals(5, cats.size());

        ContenuCategorieDTO lieux = cats.stream().filter(c -> "LIEUX".equals(c.getCode())).findFirst().orElseThrow();
        assertEquals(50L, lieux.getTotal());
        assertEquals(50.0, lieux.getPourcentage(), 0.1);
        assertEquals("#075E4D", lieux.getCouleur());

        ContenuCategorieDTO villes = cats.stream().filter(c -> "VILLES".equals(c.getCode())).findFirst().orElseThrow();
        assertEquals(20L, villes.getTotal());
        assertEquals(20.0, villes.getPourcentage(), 0.1);
        assertEquals("#D6A23A", villes.getCouleur());

        ContenuCategorieDTO plats = cats.stream().filter(c -> "PLATS".equals(c.getCode())).findFirst().orElseThrow();
        assertEquals(15L, plats.getTotal());
        assertEquals(15.0, plats.getPourcentage(), 0.1);

        ContenuCategorieDTO quiz = cats.stream().filter(c -> "QUIZ".equals(c.getCode())).findFirst().orElseThrow();
        assertEquals(5L, quiz.getTotal());
        assertEquals(5.0, quiz.getPourcentage(), 0.1);
    }

    @Test
    @DisplayName("Couverture des 16 régions administratives officielles et calcul du taux d'activité")
    void testStatistiquesRegionales_16RegionsEtCalculActivite() {
        // Simuler la présence d'entités dans 4 régions : Bamako, Sikasso, Ségou, Mopti
        RegionModel regBamako = RegionModel.builder().idRegion(1L).nomRegion("District de Bamako").chefLieu("Bamako").build();
        RegionModel regSikasso = RegionModel.builder().idRegion(2L).nomRegion("Région de Sikasso").chefLieu("Sikasso").build();
        RegionModel regSegou = RegionModel.builder().idRegion(3L).nomRegion("Région de Ségou").chefLieu("Ségou").build();
        RegionModel regMopti = RegionModel.builder().idRegion(4L).nomRegion("Région de Mopti").chefLieu("Mopti").build();

        when(regionRepository.findAll()).thenReturn(List.of(regBamako, regSikasso, regSegou, regMopti));

        VilleModel villeBamako = VilleModel.builder().idVille(1L).nomVille("Bamako").regionParent(regBamako).build();
        when(villeRepository.findAll()).thenReturn(List.of(villeBamako));

        when(evenementRepository.findAll()).thenReturn(mockEvents); // Contient des événements à Bamako, Ségou, Mopti

        DashboardStatsResponseDTO res = dashboardStatsService.getDashboardStats();

        assertNotNull(res.getStatistiquesRegionales());
        StatistiquesRegionalesDTO regStats = res.getStatistiquesRegionales();

        assertEquals(16, regStats.getTotalRegions());
        assertEquals(16, regStats.getRegions().size());

        // Bamako doit être actif car dispose d'une ville et d'événements
        RegionStatDTO bko = regStats.getRegions().stream().filter(r -> "BKO".equals(r.getCode())).findFirst().orElseThrow();
        assertTrue(bko.isActif());
        assertTrue(bko.getTotalEntites() >= 1);
        assertEquals(180, bko.getX());
        assertEquals(320, bko.getY());

        // Le taux d'activité doit être calculé sur 16 régions
        assertTrue(regStats.getRegionsActives() >= 3);
        assertEquals(Math.round(((double) regStats.getRegionsActives() / 16.0) * 1000.0) / 10.0, regStats.getTauxActivite(), 0.1);
    }

    @Test
    @DisplayName("Extraction des éléments à valider avec routes de redirection conformes")
    void testElementsAValider_RegroupementEtRedirection() {
        when(promoteurRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(mockPromoteursEnAttente);
        when(guideRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(mockGuidesEnAttente);
        when(evenementRepository.findByStatutOrderByDateSoumissionDesc(EvenementModel.Statut.EN_ATTENTE))
                .thenReturn(mockEventsEnAttente);

        DashboardStatsResponseDTO res = dashboardStatsService.getDashboardStats();

        assertNotNull(res.getAValider());
        List<ElementModerationDTO> aValider = res.getAValider();
        assertEquals(3, aValider.size());

        // Partenaire promoteur -> /demandes-partenariat
        ElementModerationDTO part = aValider.stream().filter(i -> "Partenaire".equals(i.getType())).findFirst().orElseThrow();
        assertEquals("Festival des Masques", part.getTitre());
        assertEquals("/demandes-partenariat", part.getUrlAction());
        assertEquals("EN_ATTENTE", part.getStatut());

        // Guide certifié -> /demandes-partenariat
        ElementModerationDTO guide = aValider.stream().filter(i -> "Guide".equals(i.getType())).findFirst().orElseThrow();
        assertEquals("/demandes-partenariat", guide.getUrlAction());

        // Événement culturel -> /evenements
        ElementModerationDTO evt = aValider.stream().filter(i -> "Événement".equals(i.getType())).findFirst().orElseThrow();
        assertEquals("Concert Mandé Traditionnel", evt.getTitre());
        assertEquals("/evenements", evt.getUrlAction());
    }

    @Test
    @DisplayName("Fil chronologique Activité récente limité à 5 actions triées par date décroissante")
    void testActivitesRecentes_OrdreChronologiqueEtLimite5() {
        when(utilisateurRepository.findAll()).thenReturn(mockUsers);
        when(evenementRepository.findAll()).thenReturn(mockEvents);

        ArticleModel art = ArticleModel.builder()
                .idArticle(1L)
                .nomArticle("Les Tombeaux des Askia classés UNESCO")
                .datePublication(LocalDateTime.now().minusHours(1))
                .build();
        when(articleRepository.findAll()).thenReturn(List.of(art));

        HistoriquePointModel hp = new HistoriquePointModel();
        hp.setIdHistorique(10L);
        hp.setDescription("Défi Quiz Dogon réussi");
        hp.setPointsGagnes(100);
        hp.setDateGain(LocalDateTime.now().minusMinutes(15));
        when(historiquePointRepository.findAll()).thenReturn(List.of(hp));

        DashboardStatsResponseDTO res = dashboardStatsService.getDashboardStats();

        assertNotNull(res.getActiviteRecente());
        List<ActiviteRecenteDTO> activites = res.getActiviteRecente();

        assertTrue(activites.size() <= 5);
        assertFalse(activites.isEmpty());

        // Vérifier le tri décroissant des dates ISO
        for (int i = 0; i < activites.size() - 1; i++) {
            String iso1 = activites.get(i).getDateIso();
            String iso2 = activites.get(i + 1).getDateIso();
            if (iso1 != null && iso2 != null) {
                assertTrue(iso1.compareTo(iso2) >= 0, "Les activités doivent être ordonnées de la plus récente à la plus ancienne");
            }
        }
    }
}
