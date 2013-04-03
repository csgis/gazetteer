var messages = angular.module('gazetteer.messages', []);

messages.factory('messages', function(){
	return {
		"ui.error": "Error",
		"ui.contactAdmin": "Please contact arachne@uni-koeln.de if the problem persists.",
		"ui.search.results": "Search result",
		"ui.search.hits": "Hits",
		"ui.search.limit.10.tooltip": "Show 10 hits per page",
		"ui.search.limit.50.tooltip": "Show 50 hits per page",
		"ui.search.limit.100.tooltip": "Show 100 hits per page",
		"ui.search.limit.1000.tooltip": "Show 1000 hits per page",
		"ui.search.sort.score.tooltip": "Sort by relevance",
		"ui.search.sort.id.tooltip": "Sort by ID",
		"ui.search.sort.name.tooltip": "Sort by name",
		"ui.search.sort.thesaurus.tooltip": "sort by thesaurus",
		"ui.place.children.search": "Show places in search",
		"ui.place.save.success": "Successfully created place",
		"ui.place.save.failure": "Could not create place",
		"ui.place.remove.success": "Successfully deleted place",
		"ui.place.remove.failure": "Could not delete place",
		"ui.thesaurus": "Thesaurus",
		"ui.link.tooltip": "Link to current place",
		"ui.place.deleted": "This place has been deleted",
		"ui.merge.tooltip": "Merge this and the current place",
		"ui.merge.success.head": "Successfully merged places",
		"ui.merge.success.body": "Please review the information of the newly created place",
		"ui.extendedSearch": "Extended search",
		"place.types.continent": "Continent",
		"place.types.country": "Country",
		"place.types.city": "City",
		"place.types.region": "Region",
		"place.types.lake": "Lake",
		"place.types.river": "River",
		"place.types.ocean": "Ocean",
		"place.types.district": "District",
		"place.types.state": "State",
		"place.name.ancient": "Ancient",
		"location.confidence.0": "Not specified",
		"location.confidence.1": "Uncertain",
		"location.confidence.2": "Certain",
		"location.confidence.3": "Exact",
		"languages.deu": "German",
		"languages.eng": "English",
		"languages.ita": "Italian",
		"languages.fra": "French",
		"languages.ell": "Greek (Modern)",
		"languages.lat": "Latin",
		"languages.grc": "Ancient Greek",
		"languages.sqi": "Albanian",
		"languages.pol": "Polish",
		"domain.place.parent": "Falls within",
		"domain.place.type": "Type",
		"domain.place.tags": "Tags"
	};
});