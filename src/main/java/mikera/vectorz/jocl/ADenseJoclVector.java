package mikera.vectorz.jocl;

import mikera.vectorz.impl.ASizedVector;

/**
 * Abstract base class for dense Jocl vectors
 * @author Mike
 *
 */
public abstract class ADenseJoclVector extends ASizedVector {
	private static final long serialVersionUID = -6022914163576354860L;

	protected ADenseJoclVector(int length) {
		super(length);
	}

	public abstract JoclVector getData();
	
	public abstract int getDataOffset();
	
	public JoclSubVector asJoclSubVector() {
		if (this instanceof JoclSubVector) {
			return (JoclSubVector)this;
		} else {
			return JoclSubVector.wrap(getData(), getDataOffset(), length);
		}
	}
}
