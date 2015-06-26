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
public class HouseOfCards extends Bunker{

	/**
	 * @param objs
	 */
	public HouseOfCards(List<ABObject> objs) {
		super(objs);
		// TODO Auto-generated constructor stub
	}
	
	public HouseOfCards(Building bld){
		super( new LinkedList<ABObject>(bld.blocks));
	}

	@Override
	public String GetBuildingType(){
		return "House of Cards";
	}
}
