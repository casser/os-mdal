package os.mdal.utils;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import os.utils.stats.StatEvent;
import os.utils.stats.Stats;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Locator;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.FailureMode;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.transcoders.SerializingTranscoder;


/**
 * Memcached implementation (using http://code.google.com/p/spymemcached/)
 *
 * expiration is specified in seconds
 */
public class Memcache {
	
	public static class Config extends EnumMap<Config.Key,Object>{
		private static final long serialVersionUID = 5736330252515924193L;
		public static enum Key{
			HOSTS,
			MAX_RECONNECT_DELAY,
			OPERATION_TIMEOUT,
			READ_BUFFER_SIZE,
			OPTIMIZATION_ENABLED,
			NAGLE_ALGORITHM_ENABLED,
			DEMON_MODE_ENABLED,
			PROTOCOL,
			FAILURE_MODE,
			LOCATOR_TYPE,
			HASH_ALGORITHM,
			POOL_SIZE,
			LOGGER
		}
		
		public Config() {
			super(Key.class);
		}
		
		public List<InetSocketAddress> getAddresses(){
			if(containsKey(Key.HOSTS)){
				return AddrUtil.getAddresses(get(Key.HOSTS).toString());
			}else{
				return AddrUtil.getAddresses("127.0.0.1:11211");
			}
		}
		
		public Long getMaxReconnectDelay() {
			if(containsKey(Key.MAX_RECONNECT_DELAY)){
				return (Long)get(Key.MAX_RECONNECT_DELAY);
			}else{
				return 30L;
			}
		}

		public Long getOperationTimeout() {
			if(containsKey(Key.OPERATION_TIMEOUT)){
				return (Long)get(Key.OPERATION_TIMEOUT);
			}else{
				return 1000L;
			}
		}

		public Integer getReadBufferSize() {
			if(containsKey(Key.READ_BUFFER_SIZE)){
				return (Integer)get(Key.READ_BUFFER_SIZE);
			}else{
				return 16384;
			}
		}
		
		public Integer getPoolSize() {
			if(containsKey(Key.POOL_SIZE)){
				return (Integer)get(Key.POOL_SIZE);
			}else{
				return 1;
			}
		}

		public String getLogger() {
			if(containsKey(Key.LOGGER)){
				return get(Key.LOGGER).toString();
			}else{
				return "net.spy.memcached.compat.log.DefaultLogger";
			}
		}

		public Protocol getProtocol() {
			if(containsKey(Key.PROTOCOL)){
				return Protocol.valueOf(get(Key.PROTOCOL).toString());
			}else{
				return Protocol.TEXT;
			}
		}
		
		public FailureMode getFailureMode() {
			if(containsKey(Key.FAILURE_MODE)){
				return FailureMode.valueOf(get(Key.FAILURE_MODE).toString());
			}else{
				return FailureMode.Redistribute;
			}
		}
		
		public Locator getLocatorType() {
			if(containsKey(Key.LOCATOR_TYPE)){
				return Locator.valueOf(get(Key.LOCATOR_TYPE).toString());
			}else{
				return Locator.ARRAY_MOD;
			}
		}
		
		public HashAlgorithm getHashAlgorithm() {
			if(containsKey(Key.HASH_ALGORITHM)){
				return HashAlgorithm.valueOf(get(Key.HASH_ALGORITHM).toString());
			}else{
				return HashAlgorithm.FNV1_64_HASH;
			}
		}
		
		public Boolean isOptimizationEnabled() {
			if(containsKey(Key.OPTIMIZATION_ENABLED)){
				return (Boolean)get(Key.OPTIMIZATION_ENABLED);
			}else{
				return true;
			}
		}
		
		public Boolean isNagleAlgorithmEnabled() {
			if(containsKey(Key.NAGLE_ALGORITHM_ENABLED)){
				return (Boolean)get(Key.NAGLE_ALGORITHM_ENABLED);
			}else{
				return true;
			}
		}
		public Boolean isDaemonModeEnabled() {
			if(containsKey(Key.DEMON_MODE_ENABLED)){
				return (Boolean)get(Key.DEMON_MODE_ENABLED);
			}else{
				return true;
			}
		}

		
	}
	
	
	
	static class SerializingTranscoderImplementation extends SerializingTranscoder{
      
        @Override
		public Object deserialize(byte[] data) {
			return null;
        }
        
        @Override
        protected byte[] serialize(Object data) {
        	return null;
        }
	}
	
   
    
    AtomicInteger cid = new AtomicInteger(0);
    MemcachedClient[] clients;
    
    SerializingTranscoderImplementation tc;
    
   
    private static final Config configuration = new Config();
    private static Memcache instance;
    
    public static Memcache getInstance(){
    	if(instance==null){
    		instance = new Memcache();
    	}
    	return instance;
    }
    
    public static void init(Config config){
    	configuration.clear();
    	configuration.putAll(config);
    	instance = new Memcache();
    }
    
    private Memcache(){
    	this.tc = new SerializingTranscoderImplementation();
    	System.setProperty("net.spy.log.LoggerImpl", configuration.getLogger());
        List<InetSocketAddress> addrs = configuration.getAddresses();
        
        ConnectionFactoryBuilder cf = new ConnectionFactoryBuilder()
        	.setMaxReconnectDelay(configuration.getMaxReconnectDelay())
        	.setOpTimeout(configuration.getOperationTimeout())
        	.setReadBufferSize(configuration.getReadBufferSize())
        	.setShouldOptimize(configuration.isOptimizationEnabled())
        	.setUseNagleAlgorithm(configuration.isNagleAlgorithmEnabled())
        	.setDaemon(configuration.isDaemonModeEnabled())
        	.setProtocol(configuration.getProtocol())
        	.setFailureMode(configuration.getFailureMode())
        	.setLocatorType(configuration.getLocatorType())
        	.setHashAlg(configuration.getHashAlgorithm());
        ConnectionFactory mcf = cf.build();
        
        Integer poolSize = configuration.getPoolSize();
        clients = new MemcachedClient[poolSize];
        try{
        	for(int i=0;i<poolSize;i++){
        		clients[i] = new MemcachedClient(mcf,addrs);
        	}
        }catch(Exception ex){
        	ex.printStackTrace();
        }
        printConfig(mcf);
    }
  
    public void initClient() {
        
    }
    
    private synchronized MemcachedClient client(){
    	return clients[ cid.getAndIncrement() % clients.length];
    }
    
    private void printConfig(ConnectionFactory mcf){
    	System.out.println(
 			"Memcached Config {\n"+
    	    "  Connection Pool                 : " + clients.length + "\n"+
    		"  Operation Factory               : " + mcf.getOperationFactory().getClass().getName() + "\n" +
    		"  Transcoder                      : " + mcf.getDefaultTranscoder().getClass().getName()+ "\n" +
    		"  Failure Mode                    : " + mcf.getFailureMode().name() + "\n" +
    		"  Hash Algorithm                  : " + mcf.getHashAlg().name() + "\n" +
    		"  Is Daemon                       : " + mcf.isDaemon() + "\n" +
    		"  Optimized                       : " + mcf.shouldOptimize() + "\n" +
    		"  Using Nagle                     : " + mcf.useNagleAlgorithm() + "\n" +
    		"  Read Buffer Size                : " + mcf.getReadBufSize() + "\n"+
    		"  Max Reconnect Delay             : " + mcf.getMaxReconnectDelay() + "\n" +
    		"  Max Op Timeout                  : " + mcf.getOperationTimeout() + "\n" +
   			"}\n"
    	);
    }
    
    public Object get(String key) {
    	StatEvent c = Stats.track("Memcache.Actions.get");
    	Boolean isError = false;
    	for(int ri = 0; ri<clients.length; ri++){
	        Future<Object> future = client().asyncGet(key,tc);
	        try {
	        	Object r = future.get(1, TimeUnit.SECONDS); 
	        	c.finish(false);
	        	isError = false;
	            return r;
	        } catch (Exception e) {
	            future.cancel(false);
	            isError = true;	            
	        }
    	}
    	c.finish(isError);
        return null;
    }
    
    public Map<String, Object> get(String[] keys) {
    	StatEvent c = Stats.track("Memcache.Actions.gets");
        Future<Map<String, Object>> future = client().asyncGetBulk(tc,keys);
        try {
        	Map<String, Object> r = future.get(1, TimeUnit.SECONDS);
        	c.finish(false);
            return r;
        } catch (Exception e) {
            future.cancel(false);
            c.finish(true);
        }
        return Collections.<String, Object>emptyMap();
    }
    
    public void clear() {}
    
    public long incr(String key, int by) {
        StatEvent c = Stats.track("Memcache.Actions.incr");
        long result = client().incr(key, by, 0);
        c.finish(false);
        return result;
    }
    
    public long decr(String key, int by) {
    	StatEvent c = Stats.track("Memcache.Actions.decr");
        long result = client().decr(key, by, 0);
        c.finish(false);
        return result;
    }

   
    public void add(String key, Object value, int expiration) {
    	StatEvent c = Stats.track("Memcache.Actions.add");
    	client().add(key, expiration, value, tc);
    	c.finish(false);
    }
    
    public boolean safeAdd(String key, Object value, int expiration) {
    	StatEvent c = Stats.track("Memcache.Actions.safeAdd");
     	Boolean isError = false;
     	for(int ri = 0; ri<clients.length; ri++){
     		Future<Boolean> future = client().add(key, expiration, value, tc);
 	        try {
 	        	future.get(1, TimeUnit.SECONDS); 
 	        	c.finish(false);
 	        	isError = false;
 	        } catch (Exception e) {
 	            future.cancel(false);
 	            isError = true;	            
 	        }
     	}
     	c.finish(isError);
     	return !isError;
    }
    
    public void delete(String key) {
    	StatEvent c = Stats.track("Memcache.Actions.delete");
    	client().delete(key);
    	c.finish(false);
    }
    
    public boolean safeDelete(String key) {
    	StatEvent c = Stats.track("Memcache.Actions.safeDelete");
     	Boolean isError = false;
     	for(int ri = 0; ri<clients.length; ri++){
     		Future<Boolean> future = client().delete(key);
 	        try {
 	        	future.get(1, TimeUnit.SECONDS); 
 	        	c.finish(false);
 	        	isError = false;
 	        } catch (Exception e) {
 	            future.cancel(false);
 	            isError = true;	            
 	        }
     	}
     	c.finish(isError);
     	return !isError;
    }
    
    public void replace(String key, Object value, int expiration) {
    	StatEvent c = Stats.track("Memcache.Actions.replace");
    	client().replace(key, expiration, value, tc);
    	c.finish(false);
    }
    
    public boolean safeReplace(String key, Object value, int expiration) {
    	StatEvent c = Stats.track("Memcache.Actions.safeReplace");
     	Boolean isError = false;
     	for(int ri = 0; ri<clients.length; ri++){
     		Future<Boolean> future = client().replace(key, expiration, value, tc);
 	        try {
 	        	future.get(1, TimeUnit.SECONDS); 
 	        	c.finish(false);
 	        	isError = false;
 	        } catch (Exception e) {
 	            future.cancel(false);
 	            isError = true;	            
 	        }
     	}
     	c.finish(isError);
     	return !isError;
    }

    public boolean safeSet(String key, Object value, int expiration) {
    	StatEvent c = Stats.track("Memcache.Actions.safeSet");
     	Boolean isError = false;
     	for(int ri = 0; ri<clients.length; ri++){
     		Future<Boolean> future = client().set(key, expiration, value, tc);
 	        try {
 	        	future.get(1, TimeUnit.SECONDS); 
 	        	c.finish(false);
 	        	isError = false;
 	        } catch (Exception e) {
 	            future.cancel(false);
 	            isError = true;	            
 	        }
     	}
     	c.finish(isError);
     	return !isError;
    }

    public void set(String key, Object value, int expiration) {
    	StatEvent c = Stats.track("Memcache.Actions.set");
    	client().set(key, Math.min(86400, expiration), value, tc);
    	c.finish(false);
    }

    public void stop() {
		for(int i=0;i<clients.length;i++){
	    	clients[i].shutdown();
	    }
    }
}