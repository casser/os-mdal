package dc.dcsn.models.pt;

import java.util.ArrayList;

import os.bson.BsonModel.Entity;
import os.mdal.model.Model;

import dc.dcsn.models.Portal;
import dc.dcsn.models.pl.Announcement;

@Entity(name="messages", version=1, model=(byte)0xA4 )
public class Message extends Model{

	public static class Id extends Portal.Id{
		public Id(String value) {
			super(value);
		}
	}
	
	public static class List extends ArrayList<Message.Id> {
		private static final long serialVersionUID = 2468458752931451972L;
	}
	
	public static class Announcements extends Announcement.List {
		private static final long serialVersionUID = -3580999538582649071L;
	}
	
	Id 	id;
	public Id getId() {
		return id;
	}
	
	public void setId(Id id) {
		this.id = id;
	}
	
	@Override
	public Object id() {
		return getId();
	}
	@Override
	public void id(Object value) {
		setId((Id)value);
	}
	
	Announcement announcement;
	public Announcement getAnnouncement() {
		return announcement;
	}
	public void setAnnouncement(Announcement announcement) {
		this.announcement = announcement;
	}
	
}
