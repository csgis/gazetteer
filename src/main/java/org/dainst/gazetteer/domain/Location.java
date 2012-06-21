package org.dainst.gazetteer.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequenceFactory;

@Entity
public class Location {

	@Id
	@GeneratedValue
	private long id;

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private Set<Description> descriptions;

	@Type(type="org.hibernatespatial.GeometryUserType")
	private Point point;

	@Type(type="org.hibernatespatial.GeometryUserType")
	private Polygon polygon;

	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	private Place place;

	@Version
	private Date lastModified;
	
	private Date created;
	
	public Location() {
		created = new Date();
	}
	
	public Location(double lat, double lng) {
		PackedCoordinateSequenceFactory factory = new PackedCoordinateSequenceFactory(PackedCoordinateSequenceFactory.DOUBLE);
		CoordinateSequence coordinates = factory.create(new double[]{lat, lng}, 2);
		point = new Point(coordinates, new GeometryFactory(factory));
		created = new Date();
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Polygon getPolygon() {
		return polygon;
	}

	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
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
	
	@Transient
	public double[] getCoordinates() {
		CoordinateSequence sequence = point.getCoordinateSequence();
		return new double[]{ sequence.getY(0), sequence.getX(0) };
	}
	
	public void setCoordinates(double[] coordinates) {
		PackedCoordinateSequenceFactory factory = new PackedCoordinateSequenceFactory(PackedCoordinateSequenceFactory.DOUBLE);
		CoordinateSequence sequence = factory.create(coordinates, 2);
		point = new Point(sequence, new GeometryFactory(factory));
	}
	
	@Transient
	public double getLat() {
		return point.getCoordinateSequence().getX(0);
	}
	
	@Transient
	public double getLng() {
		return point.getCoordinateSequence().getY(0);
	}

	public Set<Description> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(Set<Description> descriptions) {
		this.descriptions = descriptions;
	}
	
	public void addDescription(Description description) {
		descriptions.add(description);
	}

}
