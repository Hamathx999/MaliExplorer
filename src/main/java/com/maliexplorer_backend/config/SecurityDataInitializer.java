package com.maliexplorer_backend.config;

import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.model.utilisateurModel;
import com.maliexplorer_backend.repository.utilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityDataInitializer implements CommandLineRunner {

    private final utilisateurRepository userRepository;

    @Override
    public void run(String... args) {
        log.info("Vérification et initialisation des comptes administrateurs MaliExplorer...");

        // 1. Initialisation / élévation du compte administrateur principal : famoryjosuesissoko@gmail.com
        initOrPromoteAdmin("famoryjosuesissoko@gmail.com", "Famory Josue", "Sissoko");

        // 2. Initialisation / élévation du compte administrateur technique : hamath.o.diallo18@gmail.com
        initOrPromoteAdmin("hamath.o.diallo18@gmail.com", "Hamath", "Diallo");
    }

    private void initOrPromoteAdmin(String email, String prenom, String nom) {
        Optional<utilisateurModel> userOpt = userRepository.findByEmail(email);

        try {
            if (userOpt.isPresent()) {
                utilisateurModel user = userOpt.get();
                boolean needsSave = false;

                if (user.getRole() != RoleModel.admin && user.getRole() != RoleModel.superAdmin) {
                    log.info("Élévation du compte {} au rôle ADMIN (ancien rôle : {})", email, user.getRole());
                    user.setRole(RoleModel.admin);
                    needsSave = true;
                }

                if (!"ACTIF".equalsIgnoreCase(user.getStatut())) {
                    log.info("Réactivation du compte {} en statut ACTIF", email);
                    user.setStatut("ACTIF");
                    needsSave = true;
                }

                if (needsSave) {
                    userRepository.save(user);
                    log.info("Compte administrateur {} mis à jour avec succès en base de données.", email);
                } else {
                    log.info("Compte administrateur {} déjà configuré avec le rôle ADMIN.", email);
                }
            } else {
                log.info("Création du compte administrateur par défaut : {}", email);
                utilisateurModel newAdmin = utilisateurModel.builder()
                        .email(email)
                        .prenom(prenom)
                        .nom(nom)
                        .role(RoleModel.admin)
                        .statut("ACTIF")
                        .points(100)
                        .nomOrganisation("")
                        .telephone("")
                        .pieceIdentite("")
                        .adresse("")
                        .photoUrl("")
                        .dateCreation(new Date(System.currentTimeMillis()))
                        .build();

                userRepository.save(newAdmin);
                log.info("Compte administrateur {} créé avec succès avec le rôle ADMIN.", email);
            }
        } catch (Exception e) {
            log.warn("Avertissement lors de l'initialisation du compte administrateur {} : {}", email, e.getMessage());
        }
    }
}

