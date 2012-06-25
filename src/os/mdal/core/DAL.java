package os.mdal.core;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import os.bson.BsonModel;
import os.json.JSON;
import os.mdal.cache.DataCache;
import os.mdal.cache.MemCache;
import os.mdal.core.DAL.Config.Key;
import os.mdal.errors.DataNotFoundException;
import os.mdal.model.Model;
import os.mongo.Collection;
import os.mongo.Database;
import os.mongo.IQuery;
import os.mongo.Mongo;
import os.mongo.Page;
import os.mongo.Query;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.DuplicateMemberException;

public class DAL {
	
	public static class Enhancer {
		
		private static final String[] METHODS = new String[]{
			"public static Object get(Object id) {return dc.dal.core.DAL.get(type(), id);}",
			"public static Object get(Object id) {return dc.dal.core.DAL.get(type(), id);}",
		   	
			"public static Long count() { return dc.dal.core.DAL.count(type()); }",
		   	"public static Long count(dc.mongo.db.IQuery query) { return dc.dal.core.DAL.count(type(), query); }",
		    
		   	"public static dc.mongo.db.Page find() { return dc.dal.core.DAL.find(type()); }",
		    "public static dc.mongo.db.Page find(dc.mongo.db.IQuery query) { return dc.dal.core.DAL.find(type(),query); }", 
		    
		    "public static Long remove(){ return dc.dal.core.DAL.remove(type()); }",
		    "public static Long remove(dc.mongo.db.IQuery query) { return dc.dal.core.DAL.remove(type(), query); }"
		};
		
		public static void enhance(String[] classes){
			enhance(classes,null);
		}
		
		public static void enhance(String[] classes, ClassPool pool){
			for(String cn:classes){
				enhance(pool, cn);
			}
		}
		
		public static void enhance(String className){
			enhance(null, className);
		}
		
		public static void enhance(ClassPool classPool, String className){
			new Enhancer().compile(classPool,className);
		}
		
		public static byte[] enhance(CtClass ctClass) throws CannotCompileException, IOException {
			return new Enhancer().compile(ctClass).toBytecode();
		}
		
		public void compile(ClassPool classPool, String className)  {
			if(classPool==null){
				classPool = ClassPool.getDefault();
			}			
			try{
	    		CtClass ctClass = classPool.get(className);
	    		compile(ctClass).toClass();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		public CtClass compile(CtClass ctClass) throws CannotCompileException  {
			
			ClassPool classPool = ctClass.getClassPool();
			
			classPool.importPackage("dc.dal.core.*");
			classPool.importPackage("dc.dal.model.*");
			classPool.importPackage("java.lang.*");
			
    		try{
    			System.out.println("public static Class type() { return "+ctClass.getName()+".class ; }");
	    		ctClass.addMethod(CtMethod.make(
	        		"public static Class type() { return "+ctClass.getName()+".class; }"
	        	,ctClass));
    		}catch(DuplicateMemberException ex){}
    		for(String method:METHODS){
        		try{
        			ctClass.addMethod(CtMethod.make(method,ctClass));
        		}catch(DuplicateMemberException ex){}
    		}
    		return ctClass;
		}
	}
	
	public static class Config extends EnumMap<Config.Key,Object>{
		private static final long serialVersionUID = 5736330252515924193L;
		public static enum Key{
			HOST,PORT,USER,PASS,DB,CACHE,POOL,MODELS
		}
		public Config() {
			super(Key.class);
		}
	}
	
	public static class Models extends ConcurrentHashMap<Class<? extends BsonModel>,String>{
		
		private static final long serialVersionUID = 6785117426998692935L;
		private static final Models instance = new Models();
		
		public synchronized static String collection(Class<? extends BsonModel> type) {
			if(!instance.containsKey(type)){
				String collection = type.getSimpleName().toLowerCase()+"s";
				if(type.isAnnotationPresent(BsonModel.Entity.class)){
					BsonModel.Entity entity = type.getAnnotation(BsonModel.Entity.class);
					collection = entity.name();
				}
				instance.put(type,collection);
			}
			return instance.get(type);
		}
		
		public static Models instance(){
			return instance;
		}
		
		public static Map<String,Class<? extends BsonModel>> map() {
			Map<String,Class<? extends BsonModel>> map = new Hashtable<String,Class<? extends BsonModel>>();
			for(java.util.Map.Entry<Class<? extends BsonModel>, String> entry:instance.entrySet()){
				map.put(entry.getValue(), entry.getKey());
			}
			return map;
		}
	}
	
	
	private static final Config configuration = new Config();
	
    private static Mongo mongo;
    private static Database db;
    private static DataCache cache;
    
    
    public static DataCache cache() {
    	if (cache==null){
    		try {
    			if(configuration.containsKey(Key.CACHE)){
					cache = (DataCache)configuration.get(Key.CACHE);
				}else{
					cache = new MemCache();
				}
			} catch (Exception e) {
				cache = new MemCache();
				e.printStackTrace();
			}
    	}
		return cache;
    }
    
	public static Database db() {
		if (db==null){
			if(configuration.isEmpty()){
				init();
			}
			try {
				String dbHost 	= (String)configuration.get(Key.HOST);
				String dbPort 	= (String)configuration.get(Key.PORT);
				String dbName 	= (String)configuration.get(Key.DB);
				mongo 			= new Mongo(dbHost, Integer.parseInt(dbPort));
				db 				= mongo.getDB(dbName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return db;
	}
	
	/**
	 * Static initialiser.
	 * 
	 * @throws UnknownHostException
	 * @throws MongoException
	 */
	public static void init() {
		init(null);
	}	
	
	public static void init(Config config) {
		if(config==null){
			config = new Config();
		}
		if(!config.containsKey(Key.HOST)){
			config.put(Key.HOST, "127.0.0.1");
		}
		if(!config.containsKey(Key.PORT)){
			config.put(Key.PORT, "27017");
		}
		if(!config.containsKey(Key.DB)){
			config.put(Key.DB, "sample");
		}
		configuration.clear();
		configuration.putAll(config);
	}
	
	public static synchronized <T extends Model> T get(Class<T> type, Object id) throws Exception {
		if(type==null || id ==null){
			return null;
		}
		if(!IQuery.class.isAssignableFrom(id.getClass())){
			id = Query.start("_id").is(id);
		}
		T model = null;
		try{
			model = cache().get(JSON.encode(id),type);
		}catch(DataNotFoundException ex){
			model = (T) collection(type).get(id);
			if(model!=null){
				cache().set(model);
			}
		}
		return model;
	}
	
	public static Long count(Class<? extends BsonModel> type) throws Exception{		
		return collection(type).count();
	}
	
	public static Long count(Class<? extends BsonModel> type, IQuery query) throws Exception{
		return collection(type).count(query.getQuery());
	}
	
	public static Long remove(Class<? extends BsonModel> type, IQuery query) throws Exception{
		long deleteCount = count(type,query);
		if(deleteCount>0){
			collection(type).delete(query.getQuery());
			return deleteCount;
		}
		return deleteCount;
	}
	
	public static Long remove (Class<? extends BsonModel> type) throws Exception{
		return remove(type,Query.start());
	}
	
	public static void drop (Class<? extends BsonModel> type) throws Exception{
		collection(type).drop();
	}
	
	public static <T extends BsonModel> Page<T> find(Class<T> type) throws Exception{
		return find(type,Query.start());
	}
	
	public static <T extends BsonModel> Page<T> find(Class<T> type, IQuery query) throws Exception {
		return collection(type).find(query);
	}
	
	public static <T extends BsonModel> T save(T model) throws Exception{
		if(((Collection<T>) collection(model.getClass())).save(model)){
			return model;
		}else{
			return null;
		}
	}
	
	public static <T extends BsonModel> T delete (T model) throws Exception{
		collection(model.getClass()).delete(
			Query.start("_id").is(model.id()).getQuery()
		);
		return model;
	}
	
	public static <T extends BsonModel> Collection<T> collection(Class<T> type){
		return db().getCollection(Models.collection(type),type);
	}

	public static void shutdown() {
		db().getMongo().shutdown();
	}
} 
