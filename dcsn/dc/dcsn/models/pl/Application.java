
package dc.dcsn.models.pl;

import java.util.ArrayList;

import os.bson.BsonModel.Entity;
import os.mdal.model.Model;
import os.mongo.Query;

import dc.dcsn.models.Platform;
import dc.dcsn.models.pt.Game;

@Entity(name="applications", version=1, model=(byte)0xB2 )
public class Application extends Model{
	
	public static class Id extends Platform.Id{
		public Id(String value) {
			super(value);
		}
	}
	
	public static class List extends ArrayList<Application.Id> {
		private static final long serialVersionUID = 4060026840359794140L;	
	}
	
	@Override
	public Object id() {
		return getId();
	}
	@Override
	public void id(Object value) {
		setId((Id)value);
	}
	
	Id 		id;
	public Id getId() {
		return id;
	}
	public void setId(Id id) {
		this.id = id;
	}
	
	String 	secret;
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public Application(){
	}
	public Application(Id id){
		setId(id);
	}
	
	@Override
	public String toString() {
		return "Application<"+getId()+">";
	}
	
	public Game game() {
		try {
			Game game = Game.get(Query.start("applications").is(getId()));
			if(game==null){
				game = new Game(new Game.Id());
				game.setApplications(new Game.Applications(getId()));
				game.save();
			}
			return game;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
