package nu.albert.gaekvs;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable="true")
public class Configuration {
    @SuppressWarnings("unused")
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key mId;
    
	@SuppressWarnings("unused")
	@Persistent
    private String mKey;

	@Persistent
    private String mValue;
    
    public Configuration(String key, String value) {
    	mKey = key;
    	mValue = value;
    }
    
    public void persist() {
    	PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(this);
		} finally {
			pm.close();
		}
    }
    
    public static void set(String key, String value) {
    	Configuration c = new Configuration(key, value);
    	c.persist();
    }

    public static String get(String key) {
    	if (key == null) return null;
    	
    	PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			String query = "select from " + Configuration.class.getName();
			Query q = pm.newQuery(query);
			q.setFilter(String.format("mKey == '%s'", key));
		
			@SuppressWarnings("unchecked")
			List<Configuration> cs = (List<Configuration>)q.execute();
			for (Configuration c : cs) {
				return c.mValue;
			}
			
			return null;
		} finally {
			pm.close();
		}
    }

}
