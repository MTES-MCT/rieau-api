package com.github.mtesmct.rieau.api.depositaire.infra.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot.Type;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depositaire;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(DepositaireController.ROOT_URL)
public class DepositaireController {

	public static final String ROOT_URL = "/depots";

	@Autowired
	private Depositaire depositaire;
	@Autowired
	@Qualifier("dateTimeConverter")
	private DateConverter dateTimeConverter;

	@Autowired
	private DepotWebAdapter adapter;

	@GetMapping("/{id}")
	public Optional<JsonDepot> trouveMonDepot(@PathVariable String id) {
		Optional<Depot> depot = this.depositaire.trouveMonDepot(id);
		Optional<JsonDepot> jsonDepot = Optional.empty();
        if (depot.isPresent()) {
            jsonDepot = Optional.ofNullable(this.adapter.toJson(depot.get()));
        }
        return jsonDepot;
	}

	@GetMapping
	List<JsonDepot> listeMesDepots() {
		List<JsonDepot> depots = new ArrayList<JsonDepot>();
		this.depositaire.listeMesDepots().forEach(depot -> depots.add(this.adapter.toJson(depot)));
		return depots;
	}

	@PostMapping
	public void ajouteDepot(@RequestParam("file") MultipartFile file) {
		// TODO extraction des données depuis le file
		Optional<Depot> depot = this.depositaire.litDepot(file);
		this.depositaire.ajouterDepot(depot.getType());
	}

}