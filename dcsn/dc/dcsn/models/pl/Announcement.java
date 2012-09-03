package dc.dcsn.models.pl;

import java.util.ArrayList;

import os.bson.BsonModel.Entity;
import os.mdal.model.Model;

import dc.dcsn.models.Platform;

@Entity(name="announcements", version=1, model=(byte)0xB4 )
public class Announcement extends Model{
	
	public static class Id extends Platform.Id{
		public Id(String value) {
			super(value);
		}
	}
	
	public static class List extends ArrayList<Announcement.Id> {
		private static final long serialVersionUID = 2468458752931451972L;
	}
	
	public static class Sender extends Account {
	}
	public static class Recipients extends Account.List {
		private static final long serialVersionUID = 8861643260561196548L;
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
	
	String 		message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	String 		data;
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	Sender  	sender;
	public Sender getSender() {
		return sender;
	}
	public void setSender(Sender sender) {
		this.sender = sender;
	}
	
	Recipients 	recipients;
	public Recipients getRecipients() {
		return recipients;
	}
	
	public void setRecipients(Recipients recipients) {
		this.recipients = recipients;
	}
	
	
}
