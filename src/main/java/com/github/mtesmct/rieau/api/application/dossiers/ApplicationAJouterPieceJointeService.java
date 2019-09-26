package com.github.mtesmct.rieau.api.application.dossiers;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DeposantNonAutoriseException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.NumeroPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.FichierService;

@ApplicationService
public class ApplicationAJouterPieceJointeService implements AjouterPieceJointeService {
    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private DossierRepository dossierRepository;
    private FichierService fichierService;
    private FichierFactory fichierFactory;

    public ApplicationAJouterPieceJointeService(AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierRepository dossierRepository,
            FichierFactory fichierFactory, FichierService fichierService) {
        if (authenticationService == null)
            throw new IllegalArgumentException("Le service d'authentification ne peut pas être nul.");
        this.authenticationService = authenticationService;
        if (authorizationService == null)
            throw new IllegalArgumentException("Le service d'authorisation ne peut pas être nul.");
        this.authorizationService = authorizationService;
        if (dossierRepository == null)
            throw new IllegalArgumentException("Le repository des dossiers ne peut pas être nul.");
        this.dossierRepository = dossierRepository;
        if (fichierService == null)
            throw new IllegalArgumentException("Le service des fichiers ne peut pas être nul.");
        this.fichierService = fichierService;
        if (fichierFactory == null)
            throw new IllegalArgumentException("La factory des fichiers ne peut pas être nulle.");
        this.fichierFactory = fichierFactory;
    }

    @Override
    public Optional<PieceJointe> execute(DossierId id, String numero, File file, String mimeType)
            throws AjouterPieceJointeException, AuthRequiredException, UserForbiddenException,
            UserInfoServiceException {
        this.authorizationService.isDeposantAndBetaAuthorized();
        if (numero.equals("0"))
            throw new AjouterPieceJointeException(new NumeroPieceJointeException());
        Fichier fichier;
        Optional<PieceJointe> pieceJointe = Optional.empty();
        try {
            Optional<Dossier> dossier = this.dossierRepository.findById(id.toString());
            if (dossier.isEmpty())
                throw new AjouterPieceJointeException(new DossierNotFoundException(id.toString()));
            if (!dossier.get().deposant().identity().equals(this.authenticationService.user().get().identity()))
                throw new AjouterPieceJointeException(
                        new DeposantNonAutoriseException(this.authenticationService.user().get()));
            fichier = this.fichierFactory.creer(file, mimeType);
            this.fichierService.save(fichier);
            pieceJointe = dossier.get().ajouter(numero, fichier.identity());
            this.dossierRepository.save(dossier.get());
            fichier.fermer();
        } catch (IOException e) {
            throw new AjouterPieceJointeException(e);
        }
        return pieceJointe;
    }

}