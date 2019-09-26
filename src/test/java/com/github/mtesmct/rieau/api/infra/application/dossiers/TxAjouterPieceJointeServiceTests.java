package com.github.mtesmct.rieau.api.infra.application.dossiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantAndBetaDetails;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WithDeposantAndBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TxAjouterPieceJointeServiceTests {
    @Autowired
    private TxAjouterPieceJointeService ajouterPieceJointe;

    @Autowired
    @Qualifier("deposantBeta")
    private Personne deposantBeta;
    @Autowired
    private DossierFactory dossierFactory;
    @Autowired
    private DossierRepository dossierRepository;
    @Autowired
    private FichierService fichierService;
    @Autowired
    private FichierFactory fichierFactory;

    @Test
    @WithDeposantAndBetaDetails
    public void executeDP1Test() throws IOException, AjouterPieceJointeException, AuthRequiredException,
            UserForbiddenException, UserInfoServiceException {
        Dossier dp = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP);
        dp = this.dossierRepository.save(dp);
        assertEquals(dp.statut(), StatutDossier.DEPOSE);
        File file = new File("src/test/fixtures/dummy.pdf");
        Fichier fichier = this.fichierFactory.creer(file, "application/pdf");
        this.fichierService.save(fichier);
        Optional<PieceJointe> pieceJointe = this.ajouterPieceJointe.execute(dp.identity(), "1", file, fichier.mimeType());
        assertTrue(pieceJointe.isPresent());
        assertEquals(pieceJointe.get().code().type(), TypesDossier.DP);
        fichier.fermer();
    }

    @Test
    @WithDeposantAndBetaDetails
    public void executePCMI1Test() throws IOException, AjouterPieceJointeException, AuthRequiredException,
            UserForbiddenException, UserInfoServiceException {
        Dossier pcmi = this.dossierFactory.creer(this.deposantBeta, TypesDossier.PCMI);
        pcmi = this.dossierRepository.save(pcmi);
        Optional<Dossier> optionalDossier = this.dossierRepository.findById(pcmi.identity().toString());
        assertTrue(optionalDossier.isPresent());
        pcmi = optionalDossier.get();
        assertEquals(pcmi.statut(), StatutDossier.DEPOSE);
        File file = new File("src/test/fixtures/dummy.pdf");
        Fichier fichier = this.fichierFactory.creer(file, "application/pdf");
        this.fichierService.save(fichier);
        Optional<PieceJointe> pieceJointe = this.ajouterPieceJointe.execute(pcmi.identity(), "1", file, fichier.mimeType());
        assertTrue(pieceJointe.isPresent());
        assertEquals(pieceJointe.get().code().type(), TypesDossier.PCMI);
        fichier.fermer();
    }
}