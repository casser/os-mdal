package dc.dcsn.models.pt;

import java.util.ArrayList;

import os.bson.BsonModel.Entity;
import os.mdal.model.Model;

import dc.dcsn.models.Portal;
import dc.dcsn.models.pl.Application;


@Entity(name="games", version=1, model=(byte)0xA2 )
public class Game extends Model{

	public static class Id extends Portal.Id{
	}
	
	public static class List extends ArrayList<Game.Id> {
		private static final long serialVersionUID = 2468458752931451972L;
	}
	
	public static class Applications extends Application.List {
		private static final long serialVersionUID = -4744253222371285476L;
		public Applications() {
		}
		public Applications(Application.Id ...ids) {
			for(Application.Id id:ids){
				this.add(id);
			}
		}
	}
	
	@Override
	public Object id() {
		return getId();
	}
	@Override
	public void id(Object value) {
		setId((Id)value);
	}
		
	Id 			id;
	public Id getId() {
		return id;
	}
	public void setId(Id id) {
		this.id = id;
	}
	
	Applications applications;
	public Applications getApplications() {
		return applications;
	}
	public void setApplications(Applications applications) {
		this.applications = applications;
	}
	
	public Game(){
	}
	public Game(Id id){
		setId(id);
	}
}
