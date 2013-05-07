package org.dainst.gazetteer.match;

import java.util.ArrayList;
import java.util.List;

import org.dainst.gazetteer.dao.PlaceRepository;
import org.dainst.gazetteer.domain.Identifier;
import org.dainst.gazetteer.domain.Place;
import org.dainst.gazetteer.domain.PlaceName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleNameAndIdBasedEntityIdentifier implements EntityIdentifier {
	
	private static final Logger logger = LoggerFactory.getLogger(SimpleNameAndIdBasedEntityIdentifier.class);
	
	private PlaceRepository placeDao;

	@Override
	public List<Candidate> getCandidates(Place place) {
		
		List<Candidate> candidates = new ArrayList<Candidate>();
		
		// identifier equality is a perfect match
		for (Identifier id : place.getIdentifiers()) {
			Place matchedPlace = placeDao.findByIdsAndTypeAndNeedsReviewAndIdNot(
					id, place.getType(), false, place.getId());
			if (matchedPlace != null && id.getValue() != null) {
				logger.debug("matched id: " + id);
				candidates.add(new Candidate(place, matchedPlace, 1));
				return candidates;
			}
		}
		
		if ("continent".equals(place.getType())) {
			
			// we suppose that the names of continents are unique
			List<Place> resultList = placeDao.findByPrefNameTitleAndTypeAndNeedsReviewAndIdNot(
					place.getPrefName().getTitle(), "continent", false, place.getId());
			logger.debug("matched continents: " + resultList.size());
			if (resultList.size() == 1) {
				candidates.add(new Candidate(place, resultList.get(0), 1));
				return candidates;
			}
		
		} else if ("country".equals(place.getType())) {

			// we suppose that the names of countries are unique
			List<Place> resultList = placeDao.findByPrefNameTitleAndTypeAndNeedsReviewAndIdNot(
					place.getPrefName().getTitle(), "country", false, place.getId());
			logger.debug("matched countries: " + resultList.size());
			if (resultList.size() == 1) {
				candidates.add(new Candidate(place, resultList.get(0), 1));
				return candidates;
			}
			
		} else if ("city".equals(place.getType())) {

			// XXX we suppose that the names of cities in the same country are unique
			List<Place> resultList = placeDao.findByPrefNameTitleAndNeedsReviewAndIdNot(
					place.getPrefName().getTitle(), false, place.getId());
			resultList.addAll(placeDao.findByNamesTitleAndNeedsReviewAndIdNot(
					place.getPrefName().getTitle(), false, place.getId()));
			for (PlaceName name : place.getNames()) {
				resultList.addAll(placeDao.findByPrefNameTitleAndNeedsReviewAndIdNot(
						name.getTitle(), false, place.getId()));
				resultList.addAll(placeDao.findByNamesTitleAndNeedsReviewAndIdNot(
						name.getTitle(), false, place.getId()));
			}
			logger.debug("matched cities: " + resultList.size());
			
			if (place.getParent() == null) {
				if (resultList.size() == 1) {
					Place candidate = resultList.get(0);
					if (candidate.getParent() == null) {
						candidates.add(new Candidate(place, candidate, 1));
						return candidates;
					}
				}
			} else {
				for (Place candidate : resultList) {
					// TODO check ancestors for country with equal name 
					if (candidate.getParent() != null) {
						Place candidateCountry = retrieveCountryFor(candidate);
						Place placeCountry = retrieveCountryFor(place);
						if (candidateCountry != null && candidateCountry.equals(placeCountry)) {
							candidates.add(new Candidate(place, candidate, 1));
							return candidates;
						}
					}
				}
			}
			
		}
		
		return candidates;
		
	}
	
	private Place retrieveCountryFor(Place place) {
		Place parent = placeDao.findOne(place.getParent());
		if (parent == null) {
			return null;
		} else if ("country".equals(parent.getType())) {
			return parent;
		} else {
			return retrieveCountryFor(parent);
		}
	}

	@Override
	public void setPlaceRepository(PlaceRepository placeRepository) {
		placeDao = placeRepository;
	}

}
