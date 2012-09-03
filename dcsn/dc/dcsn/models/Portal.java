package dc.dcsn.models;

import os.bson.BsonId;
import os.utils.Types.Simple;

public class Portal {
	public static class Id extends BsonId implements Simple{
		
		private String value;
		
		public Id() {
			this.bytes = getTypedBytes(this.getClass().getDeclaringClass());
		}
		
		public Id(String value) {
			this.bytes = getTypedBytes(this.getClass().getDeclaringClass(),value);
		}
				
		public String id(){
			return value;
		}
		
		public Platform platform(){
			return Platform.DC;
		}
		
	}
}
