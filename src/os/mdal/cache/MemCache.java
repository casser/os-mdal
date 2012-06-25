package os.mdal.cache;

import os.bson.BSON;
import os.bson.BsonModel;
import os.mdal.errors.DataNotFoundException;
import os.mdal.utils.Memcache;


public class MemCache implements DataCache {
	
	@Override
	public synchronized <T extends BsonModel> T get(String id,Class<T> type) throws DataNotFoundException {
		T model = null;
		try {
			byte[] data = (byte[]) Memcache.getInstance().get(key(id,type));
			if(data!=null){
				model = BSON.decode(data,type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(model==null){
			throw new DataNotFoundException();
		}else return model;
	}
	
	@Override
	public synchronized <T extends BsonModel> void set(T value) {
		Memcache.getInstance().set(key(value), BSON.encode(value), time(value));
	}
	
	private static synchronized <T extends BsonModel> int time(T model){
		int cacheTime = 300; 
		if(model.getClass().isAnnotationPresent(DataCache.Time.class)){
			cacheTime = model.getClass().getAnnotation(DataCache.Time.class).value();
		}
		return cacheTime;
	}
	
	private static synchronized <T extends BsonModel> String key(T model){
		return key(model.id().toString(),model.getClass());
	}
	
	private static synchronized <T extends BsonModel> String key(String id,Class<T> clazz){
		if(clazz.isAnnotationPresent(BsonModel.Entity.class)){
			BsonModel.Entity entiry = (BsonModel.Entity) clazz.getAnnotation(BsonModel.Entity.class);
			return "/models/"+entiry.name()+"/"+id;
		}
		return null;
	}
	
}
