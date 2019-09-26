package com.github.mtesmct.rieau.api.domain;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.CodePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.NumeroPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceNonAJoindreException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.services.FichierService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DossierTests {

    @Autowired
    private DossierFactory dossierFactory;
    @Autowired
    private FichierFactory fichierFactory;
    @Autowired
    private FichierService fichierService;
    @Autowired
    @Qualifier("deposantBeta")
    private Personne deposantBeta;

    private Dossier dossier;
    private Fichier cerfa;
    private Fichier dp1;

    @BeforeEach
    public void setUp() throws IOException {
        File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
        this.cerfa = this.fichierFactory.creer(file, "application/pdf");
        this.fichierService.save(this.cerfa);
        this.cerfa.fermer();
        this.dossier = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP);
        assertNotNull(this.dossier);
        assertNotNull(this.dossier.piecesAJoindre());
        assertEquals(this.dossier.statut(), StatutDossier.DEPOSE);
        assertEquals(this.dossier.piecesAJoindre().codesPiecesAJoindre().size(), 1);
        assertTrue(this.dossier.piecesAJoindre().codesPiecesAJoindre().contains(new CodePieceJointe(TypesDossier.DP, "1")));
        this.dossier.ajouterCerfa(this.cerfa.identity());
        file = new File("src/test/fixtures/dummy.pdf");
        this.dp1 = this.fichierFactory.creer(file, "application/pdf");
        this.fichierService.save(this.dp1);    
        this.dp1.fermer();
    }

    @AfterEach
    public void cleanUp(){
        this.fichierService.delete(this.cerfa.identity());
        this.fichierService.delete(this.dp1.identity());
    }

    @Test
    public void ajouterCerfa() {
        assertNotNull(this.dossier.cerfa());
        assertNotNull(this.dossier.cerfa().code());
        assertTrue(this.dossier.cerfa().code().isCerfa());
        assertNotNull(this.dossier.cerfa().fichierId());
        assertEquals(this.dossier.cerfa().fichierId(), this.cerfa.identity());
        assertEquals(this.dossier.pieceJointes().size(), 0);
    }

    @Test
    public void ajouterPieceJointeCERFAAuDossier_throwsErreurNumeroInvalide() {
        AjouterPieceJointeException exception = assertThrows(AjouterPieceJointeException.class, () -> this.dossier.ajouter("0", this.dp1.identity()));
        assertNotNull(exception.getCause());
        assertEquals(exception.getCause().getClass(), NumeroPieceJointeException.class);
    }

    @Test
    public void ajouterPieceJointeCERFAAuDossier_throwsErreurPieceNonAJoindre() {
        AjouterPieceJointeException exception = assertThrows(AjouterPieceJointeException.class, () -> this.dossier.ajouter("2", this.dp1.identity()));
        assertNotNull(exception.getCause());
        assertEquals(exception.getCause().getClass(), PieceNonAJoindreException.class);
    }

    @Test
    public void ajouterPieceJointeObligatoireAuDossier() {
        Optional<PieceJointe> pjDP1 = this.dossier.ajouter("1", this.dp1.identity());
        assertTrue(pjDP1.isPresent());
        assertTrue(pjDP1.get().isAJoindre());
        assertEquals(this.dossier.pieceJointes().size(), 1);
        assertTrue(this.dossier.pieceJointes().contains(pjDP1.get()));
    }

}