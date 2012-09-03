package dc.dcsn.models.pl;

import java.util.ArrayList;

import os.bson.BsonModel.Entity;
import os.mdal.model.Model;
import os.mongo.Query;

import dc.dcsn.models.Platform;
import dc.dcsn.models.pt.Install;
import dc.dcsn.models.pt.User;

@Entity(name="accounts", version=1, model=(byte)0xB1 )
public class Account extends Model{
	
	public static class Id extends Platform.Id{
		public Id(String value) {
			super(value);
		}
	}
	
	public static class List extends ArrayList<Account.Id> {
		private static final long serialVersionUID = 2468458752931451972L;
	}
	
	public static class Friends extends List {
		private static final long serialVersionUID = -6784846996752949310L;
	}
	
	public static class Installs extends Install.List {
		private static final long serialVersionUID = -6784846996752949310L;
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
	
	String 	name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	String 	picture;
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	Friends	friends;
	public Friends getFriends() {
		return friends;
	}
	public void setFriends(Friends friends) {
		this.friends = friends;
	}
	
	Installs installs;
	public Installs getInstalls() {
		return installs;
	}
	public void setInstalls(Installs installs) {
		this.installs = installs;
	}
	
	public Account(){
	}
	public Account(Id id){
		setId(id);
	}
	
	@Override
	public String toString() {
		return "Account<"+getId()+">";
	}
	
	public User user() {
		try{
			User user = User.get(Query.start("accounts").is(getId()));
			if(user==null){
				user = new User(new User.Id());
				user.setAccounts(new User.Accounts(getId()));
				user.save();
			}
			return user;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
}
