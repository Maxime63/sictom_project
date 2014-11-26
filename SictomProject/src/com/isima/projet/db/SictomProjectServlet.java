package com.isima.projet.db;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.isima.project.metier.ConstantesMetier;
import com.isima.project.persistence.TCoordonnee;

@SuppressWarnings("serial")
public class SictomProjectServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		
		String action = req.getParameter("action");
		
		if(action != null)
		{
			if(action.equals("create"))
			{
				System.out.println("CREATE");
				double longitude = Double.parseDouble(req.getParameter("longitude"));
				double latitude = Double.parseDouble(req.getParameter("latitude"));
				String date = req.getParameter("date");
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy-hh:mm:ss");
				Date d;
				try {
					d = sdf.parse(date);
					TCoordonnee c = createCoordonnee(longitude, latitude, d);
					resp.getWriter().println("Coordonnée crée : " + c.getLatitude() + " " + c.getLongitude() + " " + c.getDate_enregistrement());
				} catch (ParseException e) {
					
					e.printStackTrace();
				}
			}
			else if(action.equals("get"))
			{
				System.out.println("GET");
			}
			else if(action.equals("getall"))
			{
				List<TCoordonnee> coord = getAllCoordonnees();
				for(TCoordonnee c : coord)
				{
					resp.getWriter().println(c.getLatitude() + " " + c.getLongitude() + " " + c.getDate_enregistrement());
				}
				System.out.println("GET ALL");
			}
		}
		
		
	}
	
	
	DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
	
	public TCoordonnee createCoordonnee(double longitude, double latitude, Date date)
	{
		Entity coordonnee = new Entity(ConstantesMetier.ENTITY_TCOORDONNEE);
		coordonnee.setProperty(ConstantesMetier.ENTITY_TCOORDONNEE_LONGITUDE, longitude);
		coordonnee.setProperty(ConstantesMetier.ENTITY_TCOORDONNEE_LATITUDE, latitude);
		coordonnee.setProperty(ConstantesMetier.ENTITY_TCOODONNEE_DATE, date);
		
		datastoreService.put(coordonnee);
		
		TCoordonnee coord = new TCoordonnee(coordonnee.getKey().getId(), longitude, latitude, date);
		System.out.println(coordonnee.getKey());
		return coord;
	}
	
	
	public List<TCoordonnee> getAllCoordonnees() {
		
		Query q = new Query(ConstantesMetier.ENTITY_TCOORDONNEE);
		PreparedQuery pq = datastoreService.prepare(q);
		
		List<TCoordonnee> coord = new ArrayList<TCoordonnee>();
		
		for (Entity c : pq.asIterable()) {
			double longitude = (Double) c.getProperty(ConstantesMetier.ENTITY_TCOORDONNEE_LONGITUDE);			
			double latitude = (Double) c.getProperty(ConstantesMetier.ENTITY_TCOORDONNEE_LATITUDE);			
			Date date = (Date) c.getProperty(ConstantesMetier.ENTITY_TCOODONNEE_DATE);
			TCoordonnee newCoord = new TCoordonnee(c.getKey().getId(), longitude, latitude, date);
			coord.add(newCoord);
		}
		
		return coord;
	}
	
	public TCoordonnee getCoordonnee(long id)
	{
		TCoordonnee newCoord = null;
		
		Key key = KeyFactory.createKey(ConstantesMetier.ENTITY_TCOORDONNEE, id);
		try {
			Entity auteur = datastoreService.get(key);
			double longitude = Double.parseDouble((String) auteur.getProperty(ConstantesMetier.ENTITY_TCOORDONNEE_LONGITUDE));			
			double latitude = Double.parseDouble((String) auteur.getProperty(ConstantesMetier.ENTITY_TCOORDONNEE_LATITUDE));			
			Date date = (Date) auteur.getProperty(ConstantesMetier.ENTITY_TCOODONNEE_DATE);
			newCoord = new TCoordonnee(auteur.getKey().getId(), longitude, latitude, date);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			newCoord = null;
		}
	
		return newCoord;
	}
	
	/*
	 * 
	 * Pour l'instant return 0 si ok -1 si ko
	 */
	public boolean deleteCoord(long id)
	{
		boolean res = false;
		Key coordKey = KeyFactory.createKey(ConstantesMetier.ENTITY_TCOORDONNEE, id);
		if(coordKey.isComplete())
		{	
			datastoreService.delete(coordKey);
			res = true;
		}
		return res;
	}
	
	
	
}
