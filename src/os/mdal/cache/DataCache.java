package os.mdal.cache;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import os.bson.BsonModel;
import os.mdal.errors.DataNotFoundException;


public interface DataCache {

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Time {
		int value() default 300;
	}
	
	<T extends BsonModel> T    get(String id, Class<T> type) throws DataNotFoundException;
	<T extends BsonModel> void set(T value);
}
