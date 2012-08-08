package org.dainst.gazetteer.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;


@Entity
public class PlaceName {

	@Id
	@GeneratedValue
	private long id;
	
	private String title;
	
	private String language;
	
	private String script;
	
	private boolean modern;
	
	private int ordering = 0;

	@ManyToOne
	private Place place;

	@Version
	private Date lastModified;
	
	private Date created;
	
	public PlaceName() {
		created = new Date();
	}
	
	public PlaceName(String title, String language) {
		this.title = title;
		this.language = language;
		created = new Date();
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public boolean isModern() {
		return modern;
	}

	public void setModern(boolean modern) {
		this.modern = modern;
	}

	public int getOrdering() {
		return ordering;
	}

	public void setOrdering(int ordering) {
		this.ordering = ordering;
	}
	
}
