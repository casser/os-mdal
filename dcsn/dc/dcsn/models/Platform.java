package dc.dcsn.models;

import os.mdal.model.Model;
import os.utils.Types;
import os.utils.Types.Simple;

public enum Platform {
	
	DC,FB,GP,VK;
	
	public static class Id implements Simple{
		private String value;
		
		public Id(String value) {
			this.value = value;
		}
		
		public String id(){
			return value.split("@")[1];
		}
		public Platform platform(){
			return Platform.valueOf(value.split("@")[0]);
		}
		
		@Override
		public String toString() {
			return value;
		}
		
		@Override
		public Object toSimple() {
			return value;
		}
		
		public <T extends Model> T model() {
			Class<?> cls = this.getClass().getDeclaringClass();
			if(Model.class.isAssignableFrom(cls)){
				System.out.println(cls);
				try {
					return (T) Types.getType(cls).newInstance(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}

	public <T extends Id> T id(Class<? extends Id> type, String id) {
		return (T) Types.getType(type).newInstance(this.name()+"@"+id);
	}
	
}
