package nu.albert.gaekvs;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable="true")
public class Blob implements Serializable {
	private static final long serialVersionUID = 5978568757670357714L;
	
	@Persistent
	public byte[] content;
	
	public Blob() {
		content = null;
	}
	
	public Blob(byte[] b) {
		content = b;
	}
}
