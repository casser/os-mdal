package dc.dcsn.models.pt;

import java.util.ArrayList;
import java.util.EnumMap;

import os.bson.BsonModel.Entity;
import os.mdal.model.Model;

import dc.dcsn.models.Platform;
import dc.dcsn.models.Portal;
import dc.dcsn.models.pl.Account;


@Entity(name="installs", version=1, model=(byte)0xA0 )
public class Install extends Model{

	public static class Id extends Portal.Id{
		public Id(String value) {
			super(value);
		}
	}
	
	public static class List extends ArrayList<Install.Id> {
		private static final long serialVersionUID = 2468458752931451972L;
	}
	
	public static class Info {
		String 		token;
		Friends		friends;
	}
	
	public static class Data extends EnumMap<Platform,Info> {
		private static final long serialVersionUID = -3580999538582649071L;
		public Data() {
			super(Platform.class);
		}
	}
	
	public static class Friends extends Account.Friends {
		private static final long serialVersionUID = -4278146485482758720L;
	}
	
	public static class Inbox extends Message.List {
		private static final long serialVersionUID = -4278146485482758720L;
	}
	
	public static class Outbox extends Message.List {
		private static final long serialVersionUID = -4278146485482758720L;
	}
	
	@Override
	public Object id() {
		return getId();
	}
	@Override
	public void id(Object value) {
		setId((Id)value);
	}
		
	Id 	id;
	public Id getId() {
		return id;
	}
	
	public void setId(Id id) {
		this.id = id;
	}
	
	Game.Id game;
	public Game.Id getGame() {
		return game;
	}
	
	User.Id player;
	public User.Id getPlayer() {
		return player;
	}
	
	Inbox inbox;
	public Inbox getInbox() {
		return inbox;
	}
	public void setInbox(Inbox inbox) {
		this.inbox = inbox;
	}
	
	Outbox outbox;
	public Outbox getOutbox() {
		return outbox;
	}
	public void setOutbox(Outbox outbox) {
		this.outbox = outbox;
	}
}
