/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2015, Team DataLab Birds: 
 ** Karel Rymes, Radim Spetlik, Tomas Borovicka
 ** All rights reserved.
 **This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 **To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ 
 *or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *****************************************************************************/
package tori.utils;

import java.util.Comparator;

import ab.vision.ABObject;	

/**
*	This comparator class is used to compare the ABObjects either by X or Y coordinate.
*	The reference point of the comparator has to be set. 
*	Because this is mostly used in comparing objects with relative position to a reference object.
*/
public  class ABObjectComp implements Comparator<ABObject>
{
	 	/**
		*	true = x, false = y	
	 	*/
	 	private boolean type;
	 	/**
	 	*	true = descending, false = ascending
	 	*/	
	 	private boolean order; 
	 	/**
		*	starting position
	 	*/
	 	private int position; 
	 	/**
		*	true = width, false = height	
	 	*/
	 	private boolean width;
	 	/**
		*	Sets the comparator so that it will sort by ascending order.
	 	*/
	 	public void sortAsc()
	 	{
	 		order = false;	
	 	}
	 	/**
		*	Sets the comparator so that it will sort by descending order.
	 	*/
	 	public void sortDesc()
	 	{
	 		order = true;	
	 	}
	 	/**
		*	Sets the reference position.
	 	*/
	 	public void setPosition(int pos)
	 	{
	 		position = pos;
	 	}
	 	/**
		*	Sorts the objects on the left side of the reference point.
	 	*/
	 	public void sortLeft()
	 	{
	 		type=true;
	 		order = false;
	 	}
	 	/**
		*	Sorts the objects on the right side of the reference point.
	 	*/
	 	public void sortRight()
	 	{
	 		type=true;
	 		order = true;
	 	}
		/**
		*	Sorts the objects above the reference point.
	 	*/	 	
	 	public void sortAbove()
	 	{
	 		type=false;
	 		order = false;
	 	}
		/**
		*	Sorts the objects below the reference point.
	 	*/		 	
	 	public void sortBelow()
	 	{
	 		type=false;
	 		order = true;	 		
	 	}
	 	
	 	/**
		*	Sorts the objects by width.
	 	*/		 	
	 	public void sortByWidth()
	 	{
	 		width=true;
	 			 		
	 	}
	 	/**
		*	Compares two objects.
	 	*/
	 	public int compare(ABObject o1, ABObject o2)
	 	{
	 		if(width)
	 			if(order)
	 				return o2.width - o1.width;
	 			else
	 				return o1.width - o2.width;
	 		else if (type == true)	 		//x = width
	 		{
	 			if (order == false)
			 		//ascending	
			 		return (Math.abs(o1.x+o1.width-position)) - (Math.abs(o2.x+o2.width-position));
		 		else 
			 		//descending
			 		return  (Math.abs(o1.x-position)) - (Math.abs(o2.x-position));
		 	} else //y = height
		 	{
		 		if (order == false)
			 		//ascending	
			 		return (Math.abs(o1.y +o2.height- position)) - (Math.abs(o2.y+o2.height-position));
		 		else 
			 		//descending
			 		return  (Math.abs(o1.y-position)) - (Math.abs(o2.y-position));
		 	}
	 	}
}