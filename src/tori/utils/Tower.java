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
public class Tower extends Bunker  {

	public Tower(List<ABObject> objs) {
		super(objs);
		// TODO Auto-generated constructor stub
	}

	public Tower(Building bld) {
		// TODO Auto-generated constructor stub
		super( new LinkedList<ABObject>(bld.blocks));
	}

	@Override
	public String GetBuildingType(){
		return "Tower";
	}
	
}
