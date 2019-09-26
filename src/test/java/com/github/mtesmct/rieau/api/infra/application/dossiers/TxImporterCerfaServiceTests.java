package com.github.mtesmct.rieau.api.infra.application.dossiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.CodePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantAndBetaDetails;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

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
public class TxImporterCerfaServiceTests {
    @Autowired
    private TxImporterCerfaService importerCerfaService;

    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateConverter;

    @Autowired
    private DateService dateService;
    @Autowired
    @Qualifier("deposantBeta")
    private Personne deposantBeta;

    @Test
    @WithDeposantAndBetaDetails
    public void executeDPTest() throws IOException, DossierImportException, AuthRequiredException,
            UserForbiddenException, UserInfoServiceException {
        File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
        Optional<Dossier> dossier = this.importerCerfaService.execute(new FileInputStream(file), file.getName(), "application/pdf", file.length());
        assertTrue(dossier.isPresent());
        assertEquals(dossier.get().type().type(), TypesDossier.DP);
        assertEquals(dossier.get().statut(), StatutDossier.DEPOSE);
        assertEquals(dossier.get().dateDepot(), this.dateService.now());
        assertNotNull(dossier.get().piecesAJoindre());
        assertEquals(dossier.get().deposant().identity().toString(), this.deposantBeta.identity().toString());
        assertNotNull(dossier.get().cerfa());
        assertNotNull(dossier.get().cerfa().fichierId());
        assertEquals(new CodePieceJointe(TypesDossier.DP, "0"), dossier.get().cerfa().code());
    }

    @Test
    @WithDeposantAndBetaDetails
    public void executePCMITest() throws IOException, DossierImportException, AuthRequiredException,
            UserForbiddenException, UserInfoServiceException {
        File file = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
        Optional<Dossier> dossier = this.importerCerfaService.execute(new FileInputStream(file), file.getName(), "application/pdf", file.length());
        assertTrue(dossier.isPresent());
        assertEquals(dossier.get().type().type(), TypesDossier.PCMI);
        assertEquals(dossier.get().statut(), StatutDossier.DEPOSE);
        assertEquals(dossier.get().dateDepot(), this.dateService.now());
        assertNotNull(dossier.get().piecesAJoindre());
        assertEquals(dossier.get().deposant().identity().toString(), this.deposantBeta.identity().toString());
        assertNotNull(dossier.get().cerfa());
        assertNotNull(dossier.get().cerfa().fichierId());
        assertEquals(new CodePieceJointe(TypesDossier.PCMI, "0"), dossier.get().cerfa().code());
    }
}