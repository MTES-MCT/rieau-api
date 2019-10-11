package com.github.mtesmct.rieau.api.infra.application.dossiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithMairieBetaDetails;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TxQualifierDossierServiceTests {
    @MockBean
    private DossierRepository dossierRepository;

    @Autowired
    private TxQualifierDossierService qualifierDossierService;
    @Autowired
    private DossierFactory dossierFactory;
    @Autowired
    private ProjetFactory projetFactory;

    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateConverter;

    private Dossier dossier;

    private Dossier otherDossier;
    @Autowired
    @Qualifier("deposantBeta")
    private Personne deposantBeta;

    @BeforeEach
    public void setUp() throws Exception {
        Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
                new ParcelleCadastrale("0", "1", "2"), true, true);
        this.dossier = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP, projet);
        Mockito.when(this.dossierRepository.save(this.dossier)).thenReturn(this.dossier);
        this.dossier = this.dossierRepository.save(this.dossier);
        assertNotNull(this.dossier);
        assertNotNull(this.dossier.identity());
        assertNotNull(this.dossier.deposant());
        assertTrue(this.dossier.pieceJointes().isEmpty());
        Projet otherProjet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "75400", "BP 44", "Cedex 01",
                new ParcelleCadastrale("0", "1", "2"), true, true);
        this.otherDossier = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP, otherProjet);
        Mockito.when(this.dossierRepository.save(this.otherDossier)).thenReturn(this.otherDossier);
        this.otherDossier = this.dossierRepository.save(this.otherDossier);
        assertNotNull(this.otherDossier);
        assertNotNull(this.otherDossier.identity());
        assertNotNull(this.otherDossier.deposant());
    }

    @Test
    @WithMairieBetaDetails
    public void executeMairieTest()
            throws AuthRequiredException, UserForbiddenException, UserInfoServiceException,
            MairieForbiddenException, DossierNotFoundException {
        Mockito.when(this.dossierRepository.findById(anyString())).thenReturn(Optional.ofNullable(this.dossier));
        Optional<Dossier> dossierQualifie = this.qualifierDossierService.execute(this.dossier.identity());
        assertTrue(dossierQualifie.isPresent());
        assertEquals(this.dossier.identity(), dossierQualifie.get().identity());
        assertEquals(StatutDossier.QUALIFIE, dossierQualifie.get().statut());
    }

    @Test
    @WithMairieBetaDetails
    public void executeAutreMairieInterditTest()
            throws AuthRequiredException, UserForbiddenException, UserInfoServiceException,
            MairieForbiddenException, DossierNotFoundException {
        Mockito.when(this.dossierRepository.findById(anyString())).thenReturn(Optional.ofNullable(this.otherDossier));
        assertThrows(MairieForbiddenException.class, () -> this.qualifierDossierService.execute(this.otherDossier.identity()));
    }
    
    @Test
    @WithDeposantBetaDetails
    public void executeDeposantInterditTest() throws Exception {
        Mockito.when(this.dossierRepository.findById(anyString())).thenReturn(Optional.ofNullable(this.dossier));
        assertThrows(UserForbiddenException.class, () -> this.qualifierDossierService.execute(this.dossier.identity()));
    }
}