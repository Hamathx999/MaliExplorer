package com.maliexplorer_backend.config;

import com.maliexplorer_backend.model.PlatModel;
import com.maliexplorer_backend.model.PropositionModel;
import com.maliexplorer_backend.model.QuestionModel;
import com.maliexplorer_backend.model.QuizModel;
import com.maliexplorer_backend.repository.PlatRepository;
import com.maliexplorer_backend.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
public class CultureDataInitializer implements CommandLineRunner {

    private final PlatRepository platRepository;
    private final QuizRepository quizRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Synchronisation des données culturelles (Plats, Ingrédients & Quizzes)...");

        // Assurer que la colonne legacy propositions est nullable pour éviter SQL Error 1364
        try {
            jdbcTemplate.execute("ALTER TABLE questions MODIFY COLUMN propositions TEXT NULL");
        } catch (Exception e) {
            log.debug("Note vérification table questions: {}", e.getMessage());
        }
        try {
            initOrUpdatePlats();
        } catch (Exception e) {
            log.warn("Avertissement lors de la synchronisation des plats : {}", e.getMessage());
        }

        try {
            cleanupAndInitQuizzes();
        } catch (Exception e) {
            log.warn("Avertissement lors de la synchronisation des quizzes : {}", e.getMessage());
        }
    }

    private void initOrUpdatePlats() {
        record PlatSeed(String nom, String desc, int personnes, int temps, String difficulte, String ingredients, String imageUrl) {}

        List<PlatSeed> seeds = List.of(
            new PlatSeed(
                "Tigadèguèna",
                "Plat national incontournable mijoté dans une onctueuse sauce à base de pâte d’arachide pure torréfiée, relevé aux tomates fraîches et patates douces fondantes.",
                6, 75, "Facile",
                "Pâte d'arachide, Viande de bœuf ou poulet, Concentré de tomate, Patates douces, Oignons",
                "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/plats/tiguadegue_na.jpeg"
            ),
            new PlatSeed(
                "Fakoye",
                "Plat emblématique et raffiné du Nord Mali (Tombouctou et Gao), préparé avec des feuilles séchées de corète potagère, épices locales et viande de mouton.",
                5, 90, "Moyen",
                "Feuilles de corète séchées (Fakoye), Viande de mouton, Épices de Tombouctou, Beurre de karité",
                "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/plats/fakoye.jpeg"
            ),
            new PlatSeed(
                "Tô à la sauce Gombo",
                "Pâte dense et nutritive de farine de mil ou de maïs blanc accompagnée d’une sauce gluante de gombo frais ou séché.",
                4, 45, "Facile",
                "Farine de mil ou maïs blanc, Gombo frais pilé, Poudre de poisson fumé, Potasse",
                "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/plats/to.jpeg"
            ),
            new PlatSeed(
                "Zaamin (Riz gras malien)",
                "Riz parfumé cuit directement dans le bouillon de viande dorée, garni de carottes, chou, piments et oignons caramélisés.",
                6, 60, "Facile",
                "Riz parfumé étuvé, Viande de bœuf ou mouton, Légumes du potager, Concentré de tomate",
                "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/plats/zaamin.jpeg"
            ),
            new PlatSeed(
                "Widjila",
                "Petits pains traditionnels cuits à la vapeur dans un couscoussier, servis avec une savoureuse sauce tomate relevée à la viande.",
                4, 60, "Traditionnelle",
                "Farine de blé tamisée, Levure boulangère, Sauce tomate mijotée, Morceaux de viande",
                "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/plats/widjila.jpeg"
            ),
            new PlatSeed(
                "Sakasaka",
                "Ragoût traditionnel de feuilles de manioc pilées cuisiné avec du poisson fumé, de la pâte d'arachide et de l'huile de palme.",
                5, 80, "Moyen",
                "Feuilles de manioc pilées, Poisson fumé, Pâte d'arachide, Huile de palme rouge",
                "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/plats/sakasaka.jpeg"
            )
        );

        List<PlatModel> existingPlats = platRepository.findAll();

        for (PlatSeed seed : seeds) {
            Optional<PlatModel> match = existingPlats.stream()
                .filter(p -> p.getNomPlat() != null && (
                    p.getNomPlat().equalsIgnoreCase(seed.nom()) ||
                    p.getNomPlat().toLowerCase().contains(seed.nom().toLowerCase()) ||
                    seed.nom().toLowerCase().contains(p.getNomPlat().toLowerCase())
                ))
                .findFirst();

            if (match.isPresent()) {
                PlatModel p = match.get();
                boolean changed = false;
                if (p.getImageUrl() == null || p.getImageUrl().trim().isEmpty() || p.getImageUrl().contains("1a4120a4-11f1-4850-833f-19bd89ea6639_ville_de_tombouctou.jpg")) {
                    p.setImageUrl(seed.imageUrl());
                    changed = true;
                }
                if (p.getIngredientPrincipal() == null || p.getIngredientPrincipal().trim().isEmpty()) {
                    p.setIngredientPrincipal(seed.ingredients());
                    changed = true;
                }
                if (p.getDescription() == null || p.getDescription().trim().isEmpty()) {
                    p.setDescription(seed.desc());
                    changed = true;
                }
                if (p.getNbrePersonnes() == null || p.getNbrePersonnes() <= 0) {
                    p.setNbrePersonnes(seed.personnes());
                    changed = true;
                }
                if (p.getTempsPreparation() == null || p.getTempsPreparation() <= 0) {
                    p.setTempsPreparation(seed.temps());
                    changed = true;
                }
                if (p.getDifficulte() == null || p.getDifficulte().trim().isEmpty()) {
                    p.setDifficulte(seed.difficulte());
                    changed = true;
                }
                if (changed) {
                    platRepository.save(p);
                    log.info("Plat mis à jour avec données authentiques : {}", p.getNomPlat());
                }
            } else {
                PlatModel newPlat = PlatModel.builder()
                    .nomPlat(seed.nom())
                    .description(seed.desc())
                    .nbrePersonnes(seed.personnes())
                    .tempsPreparation(seed.temps())
                    .difficulte(seed.difficulte())
                    .ingredientPrincipal(seed.ingredients())
                    .imageUrl(seed.imageUrl())
                    .build();
                platRepository.save(newPlat);
                log.info("Nouveau plat initialisé : {}", seed.nom());
            }
        }
    }

    private void cleanupAndInitQuizzes() {
        List<QuizModel> allQuizzes = quizRepository.findAll();

        // 1. Déduplication : Regrouper par clé nettoyée
        Map<String, List<QuizModel>> grouped = new HashMap<>();
        for (QuizModel q : allQuizzes) {
            String raw = q.getNomQuiz() != null ? q.getNomQuiz().trim().toLowerCase() : "";
            String key = raw.replaceFirst("^(el|le|la|les)\\s+", "");
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(q);
        }

        for (Map.Entry<String, List<QuizModel>> entry : grouped.entrySet()) {
            List<QuizModel> list = entry.getValue();
            if (list.size() > 1) {
                // Trier pour garder celui qui a le plus de questions, puis le plus ancien ID
                list.sort((a, b) -> {
                    int qa = (a.getQuestions() != null) ? a.getQuestions().size() : 0;
                    int qb = (b.getQuestions() != null) ? b.getQuestions().size() : 0;
                    if (qa != qb) return Integer.compare(qb, qa);
                    return Long.compare(a.getIdQuiz() != null ? a.getIdQuiz() : 0, b.getIdQuiz() != null ? b.getIdQuiz() : 0);
                });

                QuizModel toKeep = list.get(0);
                for (int i = 1; i < list.size(); i++) {
                    QuizModel duplicate = list.get(i);
                    log.info("Suppression du quiz doublon ID {} : '{}'", duplicate.getIdQuiz(), duplicate.getNomQuiz());
                    quizRepository.delete(duplicate);
                }
            }
        }

        // 2. Vérification / Initialisation des 5 quiz de référence
        record QuizRef(String nom, String desc, String image, String cat, int pts) {}

        List<QuizRef> refs = List.of(
            new QuizRef("Histoire & Grands Empires du Mali", "Testez vos connaissances sur l'empire du Mali, Soundiata Keïta, Kankou Moussa et la dynastie des Askia.", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/quiz/7bbb2668-fadc-4cd1-bccd-de93aed2f9bf_quiz-histoire-empire-mali.jpg", "Histoire", 40),
            new QuizRef("Villes Mythiques & Patrimoine", "Explorez Djenné, Tombouctou, le pays Dogon et les merveilles architecturales classées à l'UNESCO.", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/quiz/a8a790f8-419f-459d-ab02-a56516a71b4f_quiz-villes-patrimoine.jpg", "Patrimoine", 40),
            new QuizRef("Cuisine et Délices du Terroir", "Êtes-vous incollable sur le Tigadèguèna, le Fakoye et les saveurs maliennes ?", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/quiz/35314f13-0179-4e41-9779-2d64ce0a5fc3_quiz-delices.jpeg", "Gastronomie", 40),
            new QuizRef("Présidents & République du Mali", "Revivez les grandes dates républicaines et les figures présidentielles du Mali indépendant.", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/quiz/e705b3d2-21e1-4d72-b179-9cfad36e67f3_quiz-presidents.jpeg", "Histoire", 40),
            new QuizRef("Cultures, Arts & Traditions", "Plongez dans l'âme du Mali : kora, bogolan, masques dogons et traditions séculaires.", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/quiz/5ba00149-9b46-4698-ad1e-c044ba55ba57_quiz-culture.jpg", "Culture", 40)
        );

        List<QuizModel> currentQuizzes = quizRepository.findAll();

        for (QuizRef ref : refs) {
            String refKey = ref.nom().toLowerCase().replaceFirst("^(el|le|la|les)\\s+", "");
            Optional<QuizModel> found = currentQuizzes.stream()
                .filter(q -> {
                    String k = (q.getNomQuiz() != null ? q.getNomQuiz() : "").trim().toLowerCase().replaceFirst("^(el|le|la|les)\\s+", "");
                    return k.contains(refKey) || refKey.contains(k);
                })
                .findFirst();

            if (found.isPresent()) {
                QuizModel q = found.get();
                boolean changed = false;
                if (q.getImageQuiz() == null || q.getImageQuiz().trim().isEmpty() || q.getImageQuiz().contains("ccdf2147-5998-4f94-8db4-683bc327e128") || q.getImageQuiz().contains("unsplash.com")) {
                    q.setImageQuiz(ref.image());
                    changed = true;
                }
                if (q.getCategorie() == null || q.getCategorie().trim().isEmpty()) {
                    q.setCategorie(ref.cat());
                    changed = true;
                }
                if (q.getPoint() == null || q.getPoint() == 0) {
                    q.setPoint(ref.pts());
                    changed = true;
                }
                if (changed) {
                    quizRepository.save(q);
                    log.info("Quiz mis à jour avec métadonnées officielles : {}", q.getNomQuiz());
                }

                // Si le quiz n'a pas de questions, injecter les questions de base
                if (q.getQuestions() == null || q.getQuestions().isEmpty()) {
                    seedQuestionsForQuiz(q);
                }
            } else {
                QuizModel newQuiz = QuizModel.builder()
                    .nomQuiz(ref.nom())
                    .description(ref.desc())
                    .imageQuiz(ref.image())
                    .categorie(ref.cat())
                    .point(ref.pts())
                    .build();
                quizRepository.save(newQuiz);
                seedQuestionsForQuiz(newQuiz);
                log.info("Nouveau quiz officiel créé avec ses questions : {}", ref.nom());
            }
        }
    }

    private void seedQuestionsForQuiz(QuizModel quiz) {
        String name = (quiz.getNomQuiz() != null ? quiz.getNomQuiz() : "").toLowerCase();

        if (name.contains("histoire") && name.contains("empire")) {
            addQuestion(quiz, "Qui a proclamé l'indépendance du Mali le 22 septembre 1960 ?", "Modibo Keïta",
                List.of("Modibo Keïta", "Moussa Traoré", "Alpha Oumar Konaré", "Tiéba Traoré"));
            addQuestion(quiz, "Quelle bataille historique en 1235 a consacré la victoire de Soundiata Keïta ?", "Bataille de Kirina",
                List.of("Bataille de Kirina", "Bataille de Tondibi", "Bataille de Kansala", "Bataille de Sikasso"));
            addQuestion(quiz, "Quel souverain du Mali est réputé pour son célèbre pèlerinage fastueux à La Mecque en 1324 ?", "Kankou Moussa",
                List.of("Kankou Moussa", "Soundiata Keïta", "Sony Ali Ber", "Askia Mohamed"));
            addQuestion(quiz, "Quel grand empire ouest-africain a précédé l'Empire du Mali au XIe siècle ?", "L'Empire du Ghana (Wagadou)",
                List.of("L'Empire du Ghana (Wagadou)", "L'Empire Songhaï", "L'Empire Mossi", "Le Royaume Bambara de Ségou"));
        } else if (name.contains("ville") || name.contains("patrimoine")) {
            addQuestion(quiz, "En quel matériau est principalement construite la Grande Mosquée de Djenné ?", "En terre crue (banco)",
                List.of("En terre crue (banco)", "En marbre blanc", "En granit taillé", "En briques cuites"));
            addQuestion(quiz, "Combien de saints patrons protègent traditionnellement la ville de Tombouctou ?", "333",
                List.of("333", "99", "12", "7"));
            addQuestion(quiz, "Quelle falaise majestueuse abrite les villages troglodytes et la culture Dogon ?", "La falaise de Bandiagara",
                List.of("La falaise de Bandiagara", "Le mont Hombori", "La falaise de Tambaoura", "Les monts Mandingues"));
            addQuestion(quiz, "Quel monument emblématique de Sikasso fut construit pour résister aux troupes coloniales ?", "Le Tata de Sikasso",
                List.of("Le Tata de Sikasso", "La Mosquée de Djingareyber", "Le Tombeau des Askia", "Le Fort de Médine"));
        } else if (name.contains("cuisine") || name.contains("terroir") || name.contains("gastro")) {
            addQuestion(quiz, "Quel est l'ingrédient de base principal de la sauce Tigadèguèna ?", "La pâte d'arachide",
                List.of("La pâte d'arachide", "Les feuilles de manioc", "La sauce graine", "Le soumbala pur"));
            addQuestion(quiz, "De quelle région du Mali le Fakoye est-il la spécialité gastronomique réputée ?", "Tombouctou et le Nord",
                List.of("Tombouctou et le Nord", "Sikasso", "Kayes", "Bamako"));
            addQuestion(quiz, "Avec quoi prépare-t-on traditionnellement la pâte nutritive du Tô malien ?", "De la farine de mil ou de maïs",
                List.of("De la farine de mil ou de maïs", "De la fécule de pomme de terre", "Du riz gluant", "De la semoule de blé"));
            addQuestion(quiz, "Comment appelle-t-on les petits pains traditionnels cuits à la vapeur dans le Nord du Mali ?", "Le Widjila",
                List.of("Le Widjila", "Le Dèguè", "Le Ngomi", "Le Mouni"));
        } else if (name.contains("président") || name.contains("president") || name.contains("république")) {
            addQuestion(quiz, "Quel président malien est affectueusement surnommé le « Soldat de la démocratie » ou ATT ?", "Amadou Toumani Touré",
                List.of("Amadou Toumani Touré", "Alpha Oumar Konaré", "Modibo Keïta", "Ibrahim Boubacar Keïta"));
            addQuestion(quiz, "En quelle année Alpha Oumar Konaré a-t-il été élu lors des premières élections multipartites démocratiques ?", "1992",
                List.of("1992", "1968", "1997", "2002"));
            addQuestion(quiz, "Quel poste international de premier plan Alpha Oumar Konaré a-t-il occupé après sa présidence ?", "Président de la Commission de l'Union africaine",
                List.of("Président de la Commission de l'Union africaine", "Secrétaire général de l'ONU", "Président de la CEDEAO", "Directeur de l'UNESCO"));
            addQuestion(quiz, "Quelle est la devise nationale inscrite dans les armoiries de la République du Mali ?", "Un Peuple - Un But - Une Foi",
                List.of("Un Peuple - Un But - Une Foi", "Travail - Liberté - Patrie", "Union - Discipline - Travail", "Justice - Paix - Progrès"));
        } else if (name.contains("culture") || name.contains("art") || name.contains("tradition")) {
            addQuestion(quiz, "Quel instrument à cordes traditionnel mandingue est composé d'une demi-calebasse recouverte de peau ?", "La Kora",
                List.of("La Kora", "Le Balafon", "Le Djembé", "Le Tama"));
            addQuestion(quiz, "Quel tissu traditionnel emblématique du Mali est teint avec de la décoction de plantes et de la boue fermentée ?", "Le Bogolan",
                List.of("Le Bogolan", "Le Kente", "Le Bazin riche", "Le Faso Danfani"));
            addQuestion(quiz, "Quelle grande fête communautaire annuelle réunit la population de Djenné pour entretenir sa Grande Mosquée ?", "Le Crépissage",
                List.of("Le Crépissage", "La Traversée des bœufs de Diafarabé", "Le Festival au Désert", "Le Gna"));
            addQuestion(quiz, "Quel fleuve nourricier majestueux traverse le Mali du sud au nord-est sur plus de 1 700 kilomètres ?", "Le fleuve Niger (Djoliba)",
                List.of("Le fleuve Niger (Djoliba)", "Le fleuve Sénégal (Bafing)", "Le fleuve Bani", "Le fleuve Sankarani"));
        }

        quizRepository.save(quiz);
    }

    private void addQuestion(QuizModel quiz, String title, String correctAnswer, List<String> options) {
        String propsJson = "[\"" + String.join("\", \"", options) + "\"]";
        QuestionModel q = QuestionModel.builder()
            .nomQuestion(title)
            .reponse(correctAnswer)
            .points(10)
            .duree(25)
            .quiz(quiz)
            .propositions(new ArrayList<>())
            .propositionsJson(propsJson)
            .build();

        for (String opt : options) {
            PropositionModel prop = new PropositionModel();
            prop.setNomProposition(opt);
            prop.setQuestion(q);
            q.getPropositions().add(prop);
        }

        quiz.getQuestions().add(q);
    }
}
