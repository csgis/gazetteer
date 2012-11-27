package org.dainst.gazetteer.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.dainst.gazetteer.dao.HarvesterDefinitionRepository;
import org.dainst.gazetteer.dao.PlaceRepository;
import org.dainst.gazetteer.dao.ThesaurusRepository;
import org.dainst.gazetteer.domain.HarvesterDefinition;
import org.dainst.gazetteer.domain.Identifier;
import org.dainst.gazetteer.domain.Location;
import org.dainst.gazetteer.domain.Place;
import org.dainst.gazetteer.domain.PlaceName;
import org.dainst.gazetteer.domain.Thesaurus;
import org.dainst.gazetteer.helpers.IdGenerator;
import org.dainst.gazetteer.search.ElasticSearchPlaceIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles requests for the application home page.
 */
@Controller
public class AdminController {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private PlaceRepository placeDao;
	
	@Autowired
	private ThesaurusRepository thesaurusDao;
	
	@Autowired
	private HarvesterDefinitionRepository harvesterDefinitionDao;
	
	@Autowired
	private ElasticSearchPlaceIndexer elasticSearchPlaceIndexer;
	
	@Autowired
	private IdGenerator idGenerator;
	
	@RequestMapping(value="/admin/generate", method = RequestMethod.POST)
	@ResponseBody
	public String generateTestData() {
		
		Place place2 = new Place();
		place2.addName(new PlaceName("Köln","de"));
		place2.addName(new PlaceName("Cologne","en"));
		place2.addLocation(new Location(50.937527,6.960268));
		place2.setId(idGenerator.generate(place2));
		placeDao.save(place2);		
		logger.info("saved {}", place2);
		
		Place place3 = new Place();
		place3.setParent(place2.getId());
		place3.addName(new PlaceName("Arbeitsstelle für digitale Archäologie","de"));
		place3.addName(new PlaceName("Cologne Digital Archaeology Lab","en"));
		place3.addLocation(new Location(50.925100, 6.925767));
		place3.setId(idGenerator.generate(place3));
		placeDao.save(place3);
		logger.info("saved {}", place3);
		
		place2.getChildren().add(place3.getId());
		placeDao.save(place2);
		
//		Random random = new Random();
//		for (int i = 0; i < 768; i++) {
//			double d1 = random.nextDouble();
//			double d2 = random.nextDouble();
//			Place place = new Place();
//			place.addName(new PlaceName("Ort " + i,"de"));
//			place.addName(new PlaceName("Place " + i,"en"));			
//			place.addLocation(new Location(d1*160-80, d2*360-180));
//			placeDao.save(place);		
//		}
		
//		Thesaurus thesaurus = new Thesaurus();
//        thesaurus.setKey("arachne");
//        thesaurus.setTitle("Arachne");
//        thesaurus.setDescription("This thesaurus contains place information imported from arachne.");
//        thesaurus = thesaurusDao.save(thesaurus);
		
//		HarvesterDefinition def = new HarvesterDefinition();
//		def.setName("test");
//		def.setHarvesterType(ArachneHarvester.class);
//		def.setTargetThesaurus("arachne");
//		def.setCronExpression("0/10 * * * * *");
//		def.setEnabled(true);
//		harvesterDefinitionDao.save(def);

		return "OK.";
		
	}
	
	@RequestMapping(value="/admin/import", method = RequestMethod.POST)
	@ResponseBody
	public String importData() {

		Connection conn = null;

        try
        {
            String userName = "root";
            String password = "tosso";
            String url = "jdbc:mysql://arachne.uni-koeln.de/arachne";
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            conn = DriverManager.getConnection(url, userName, password);
            logger.info("Database connection established");
            
            Thesaurus thesaurus = new Thesaurus();
            thesaurus.setKey("arachne");
            thesaurus.setTitle("Arachne");
            thesaurus.setDescription("This thesaurus contains place information imported from the Arachne database (http://arachne.uni-koeln.de).");
            thesaurus = thesaurusDao.save(thesaurus);
            
            Statement s = conn.createStatement ();
            s.executeQuery ("SELECT PS_OrtID, Stadt, Ort_antik, Longitude, Latitude FROM ort LIMIT 1000");
            ResultSet rs = s.getResultSet ();
            while (rs.next ())
            {
            	Place place = new Place();
            	place.setThesaurus(thesaurus.getKey());
            	place.addName(new PlaceName(rs.getString("Stadt"), "de"));
            	if (!"".equals(rs.getString("Ort_antik")))
            		place.addName(new PlaceName(rs.getString("Ort_antik"), ""));
            	place.addLocation(new Location(rs.getDouble("Latitude"), rs.getDouble("Longitude")));
                place.getIdentifiers().add(new Identifier(rs.getString("PS_OrtID"),"arachne-ort-id"));
                placeDao.save(place);
            }
            rs.close ();
            s.close ();
            
        }
        catch (Exception e)
        {
            logger.error("Cannot connect to database server",e);
        }
        finally
        {
            if (conn != null)
            {
                try
                {
                    conn.close ();
                    logger.info("Database connection terminated");
                }
                catch (Exception e) { /* ignore close errors */ }
            }
        }

		return "OK: Finished import.";
		
	}
	
	@RequestMapping(value="/admin/reindex", method=RequestMethod.POST)
	@ResponseBody
	public String reindex() {
		
		elasticSearchPlaceIndexer.reindexAllPlaces();
		
		return "OK: reindexing started";
		
	}
	
	@RequestMapping(value="/admin/resetThesaurus/{key}", method=RequestMethod.POST)
	@ResponseBody
	public String resetThesaurus(@PathVariable String key) {
		
		Thesaurus thesaurus = thesaurusDao.getThesaurusByKey(key);
		placeDao.delete(placeDao.findByThesaurus(key));
		thesaurusDao.delete(thesaurus);
		
		Thesaurus thesaurus2 = new Thesaurus();
		thesaurus2.setDescription(thesaurus.getDescription());
		thesaurus2.setKey(key);
		thesaurus2.setTitle(thesaurus.getTitle());
		thesaurusDao.save(thesaurus2);
		
		return "OK: Reset thesaurus " + key;
		
	}
	
	@RequestMapping(value="/admin/toggleHarvester/{name}", method=RequestMethod.POST)
	@ResponseBody
	public String toggleHarvester(@PathVariable String name) {
		
		HarvesterDefinition harvesterDefinition = harvesterDefinitionDao
				.getByName(name);
		harvesterDefinition.setEnabled(!harvesterDefinition.isEnabled());
		harvesterDefinitionDao.save(harvesterDefinition);
		
		return String.format("OK: set %s to enabled = %s",
				harvesterDefinition.getName(),
				harvesterDefinition.isEnabled());
		
	}
	
	@RequestMapping(value="/admin/resetHarvester/{name}", method=RequestMethod.POST)
	@ResponseBody
	public String resetHarvester(@PathVariable String name) {
		
		HarvesterDefinition harvesterDefinition = harvesterDefinitionDao
				.getByName(name);
		harvesterDefinition.setLastHarvestedDate(null);
		harvesterDefinition.setEnabled(true);
		harvesterDefinitionDao.save(harvesterDefinition);
		
		return String.format("OK: reset %s",
				harvesterDefinition.getName());
		
	}

}