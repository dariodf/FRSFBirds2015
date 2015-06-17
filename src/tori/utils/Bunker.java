/**
 * 
 */
package tori.utils;

import java.util.LinkedList;
import java.util.List;

import ab.vision.ABObject;

/**
 * @author msp20_000
 *
 */
public class Bunker extends Building  {

	/**
	 * @param objs
	 */
	public Bunker(List<ABObject> objs) {
		super(objs);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param objs
	 */
	public Bunker(Building bld) {
		super( new LinkedList<ABObject>(bld.blocks));
		
	}

	@Override
	public String GetBuildingType(){
		return "Bunker";
	}
}
