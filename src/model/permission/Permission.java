/* Permission.java */

/* The package of this class. */
package model.permission;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;
import view.XMLable;
import model.limitation.Limitation;

/** Implements the permissions that control the existence of an agent
 *  in SimPatrol.  */
public abstract class Permission implements XMLable {
	/* Attributes. */
	/** The limitations imposed to the agent. */
	protected Set<Limitation> limitations;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param limitations The limitations imposed to the agent. */
	public Permission(Limitation[] limitations) {
		if(limitations != null && limitations.length > 0) {
			this.limitations = new HashSet<Limitation>();
			
			for(int i = 0; i < limitations.length; i++)
				this.limitations.add(limitations[i]);
		}
		else this.limitations = null;
	}
	
	/** Returns the limitations of the permission.
	 * 
	 *  @return The limitations of the permission. */
	public Limitation[] getLimitations() {
		Limitation[] answer = new Limitation[0];
		
		if(this.limitations != null) {
			Object[] limitations_array = this.limitations.toArray();
			answer = new Limitation[limitations_array.length];
			for(int i = 0; i < answer.length; i++)
				answer[i] = (Limitation) limitations_array[i];
		}
		
		return answer;
	}
	
	public String reducedToXML(int identation) {
		// a permission doesn't have a lighter version
		return this.fullToXML(identation);
	}
	
	public String getObjectId() {
		// a permission doesn't need an id
		return null;
	}
	
	public void setObjectId(String object_id) {
		// a permission doesn't need an id
		// so, do nothing
	}	
}