package dc.dcsn.models.pt;

import java.util.ArrayList;

import os.bson.BsonModel.Entity;
import os.mdal.model.Model;

import dc.dcsn.models.Portal;
import dc.dcsn.models.pl.Account;


@Entity(name="users", version=1, model=(byte)0xA1 )
public class User extends Model{

	public static class Id extends Portal.Id{
	}
	
	public static class List extends ArrayList<User.Id> {
		private static final long serialVersionUID = 2468458752931451972L;
	}
	

	public static class Accounts extends Account.List {
		private static final long serialVersionUID = -4744253222371285476L;
		public Accounts() {
		}
		public Accounts(Account.Id ...ids) {
			for(Account.Id id:ids){
				this.add(id);
			}
		}
	}
	
	public static class Friend extends User {
		public static enum Relation { NONE, PENDING, ACCEPTED, RECEIVED }
				
		Relation relation;
		public Relation getRelation() {
			return relation;
		}
		public void setRelation(Relation relation) {
			this.relation = relation;
		}
	}
	
	public static class Friends extends ArrayList<Friend> {
		private static final long serialVersionUID = -4278146485482758720L;
	}
	
	public static class Games extends Game.List {
		private static final long serialVersionUID = 4885530879333854723L;
		public Games() {
		}
		public Games(Game.Id ...ids) {
			for(Game.Id id:ids){
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
	
	Accounts accounts;
	public Accounts getAccounts() {
		return accounts;
	}
	public void setAccounts(Accounts users) {
		this.accounts = users;
	}
	
	Friends friends;
	public Friends getFriends() {
		return friends;
	}
	public void setFriends(Friends friends) {
		this.friends = friends;
	}
	
	Games games;
	public Games getGames() {
		return games;
	}
	public void setGames(Games games) {
		this.games = games;
	}
	
	public User(){
	}
	public User(Id id){
		setId(id);
	}
}
