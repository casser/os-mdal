package os.mdal.model;

import java.util.List;

import os.bson.BsonModel;
import os.mdal.core.DAL;
import os.mongo.IQuery;
import os.mongo.Page;


abstract public class Model implements BsonModel {
	
	private BsonModel.Info info;
	
	@Override
	public BsonModel.Info info() {
		return info;
	}
	@Override
	public void info(BsonModel.Info info) {
		this.info = info;
	}
	
	abstract public Object id();
	abstract public void id(Object value);
	
    public void save() throws Exception {
    	DAL.save(this);
    }
    
    public void delete() throws Exception {
    	DAL.delete(this);
    }
    
    public List<String> indexes(){
    	return null;
    }
        
    public static Class type() {
    	throw new UnsupportedOperationException("Annotate with class with <DataEntity>");
    }
    
    public static <T> T get(Object id) {
    	throw new UnsupportedOperationException("Annotate with class with <DataEntity>");
	}
    
    public static Long count() { 
    	throw new UnsupportedOperationException("Annotate with class with <DataEntity>");
    }
    
    public static Long count(IQuery query) { 
    	throw new UnsupportedOperationException("Annotate with class with <DataEntity>");
    }
    
    public static Long remove(){
    	throw new UnsupportedOperationException("Annotate with class with <DataEntity>");
    }
    
    public static Long remove(IQuery query) {
    	throw new UnsupportedOperationException("Annotate with class with <DataEntity>");
    }
    
    public static <T> Page<T> find() { 
    	throw new UnsupportedOperationException("Annotate with class with <DataEntity>");
    }
    
    public static <T> Page<T> find(IQuery query) {
    	throw new UnsupportedOperationException("Annotate with class with <DataEntity>");
    }
    
   
    
}
