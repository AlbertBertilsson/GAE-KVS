package nu.albert.gaekvs;

import java.util.ArrayList;
import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import nu.albert.gaekvs.Blob;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable="true")
public class Resource {
    @SuppressWarnings("unused")
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key mId;

    @Persistent
    private String mKey;

    @SuppressWarnings("unused")
	@Persistent
    private long mModified;
    
    @Persistent
    private String mType;

    @Persistent
    private String mEncoding;

    @Persistent
    private String mPath;

    @Persistent(serialized = "true")
    private Blob mResource;
    
    public String getKey() {
    	return mKey;
    }
    
    public String getType() {
    	return mType;
    }
    
    public String getPath() {
    	return mPath;
    }
    
    public String getEncoding() {
    	return mEncoding;
    }
    
    
    public int getLength() {
    	return mResource.content.length;
    }
    
    public byte[] getResource() {
    	return mResource.content;
    }

    public Resource(String key, String type, String encoding, byte[] value) {
    	mModified = System.currentTimeMillis();
    	mType = type;
    	mEncoding = encoding;
    	mPath = path(key);
    	mKey = key(key);
    	mResource = new Blob(value);
    }
    
    private static String path(String key) {
    	if (key.contains("/")) {
    		int pos = key.lastIndexOf("/");
        	return key.substring(0, pos);
    	} else {
    		return "";
    	}
    }
    
    private static String key(String key) {
    	if (key.contains("/")) {
    		int pos = key.lastIndexOf("/");
        	return key.substring(pos + 1);
    	} else {
    		return key;
    	}
    }
    
    public static Resource get(String key) {
    	PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			String query = "select from " + Resource.class.getName();
			Query q = pm.newQuery(query);
			q.setFilter("mPath == '" + path(key) + "' && mKey == '" + key(key) + "'");
			
			@SuppressWarnings("unchecked")
			List<Resource> res = (List<Resource>)q.execute();
			for (Resource r : res) {
				Resource ret = pm.detachCopy(r);
				ret.mResource = new Blob(r.getResource());
				
				return ret;
			}
			
			return null;
		} catch (javax.jdo.JDOObjectNotFoundException nf) {
			return null;
		} finally {
			pm.close();
		}
    }
    
    public static Resource[] getPath(String path) {
    	if (path.endsWith("/"))
    		path = path.substring(0, path.length() - 1);
    	
    	PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			String query = "select from " + Resource.class.getName();
			Query q = pm.newQuery(query);
			q.setFilter("mPath == '" + path + "'");
			q.setOrdering("mPath asc, mModified desc");
			
			@SuppressWarnings("unchecked")
			List<Resource> res = (List<Resource>)q.execute();
			List<Resource> rc = new ArrayList<Resource>();
			for (Resource r : res) {
				Resource ret = pm.detachCopy(r);
				ret.mResource = new Blob(r.getResource());
				rc.add(ret);
			}
			
			return rc.toArray(new Resource[rc.size()]);
		} catch (javax.jdo.JDOObjectNotFoundException nf) {
			return null;
		} finally {
			pm.close();
		}
    }
    
    public static int delete(String key) {
    	PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			String query = "select from " + Resource.class.getName();
			Query q = pm.newQuery(query);
			q.setFilter("mPath == '" + path(key) + "' && mKey == '" + key(key) + "'");
			
			@SuppressWarnings("unchecked")
			List<Resource> res = (List<Resource>)q.execute();
			for (Resource r : res) {
		    	pm.deletePersistent(r);
				return 200;
			}
			
			return 404;
		} catch (javax.jdo.JDOObjectNotFoundException nf) {
			return 404;
		} finally {
			pm.close();
		}
    }
    
    public static int deletePath(String path) {
    	if (path.endsWith("/"))
    		path = path.substring(0, path.length() - 1);
    	
    	PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			String query = "select from " + Resource.class.getName();
			Query q = pm.newQuery(query);
			q.setFilter("mPath == '" + path + "'");
			
			@SuppressWarnings("unchecked")
			List<Resource> res = (List<Resource>)q.execute();
			pm.deletePersistentAll(res);
			return 200;
		} catch (javax.jdo.JDOObjectNotFoundException nf) {
			return 404;
		} finally {
			pm.close();
		}
    }
    
    public int put() {
    	PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			String query = "select from " + Resource.class.getName();
			Query q = pm.newQuery(query);
			q.setFilter("mPath == '" + mPath + "' && mKey == '" + mKey + "'");
			
			@SuppressWarnings("unchecked")
			List<Resource> res = (List<Resource>)q.execute();
			for (Resource s : res) {
				s.mModified = System.currentTimeMillis();
		    	s.mType = mType;
		    	s.mEncoding = mEncoding;
		    	s.mKey = mKey;
		    	s.mPath = mPath;
		    	s.mResource = mResource;
		    	pm.makePersistent(s);
				return 200;
			}
			
			pm.makePersistent(this);
	    	return 201; //Ok
		} catch (javax.jdo.JDOObjectNotFoundException nf) {
			pm.makePersistent(this);
			
			return 201; //Created
		} finally {
			pm.close();
		}
    }
}
