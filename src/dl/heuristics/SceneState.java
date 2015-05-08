/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2015, Team DataLab Birds: 
 ** Karel Rymes, Radim Spetlik, Tomas Borovicka
 ** All rights reserved.
 **This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 **To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ 
 *or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *****************************************************************************/
package dl.heuristics;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import ab.vision.ABObject;
import ab.vision.ABType;
/**
**  this class encapsulates the info about the scene that is used later in the different strategies - position of birds, hills, pigs, blocks, etc.
**/
public class SceneState
{
	public List<ABObject> Birds;

    public List<ABObject> Pigs;
    
    public List<ABObject> Hills;
    
    public List<ABObject> Blocks;
    
    public Rectangle Sling;

    public List<ABObject> TNTs; 
    
    public ABType BirdOnSling;
    
    public Point prevTarget;
    
    public boolean firstShot;

    public SceneState(List<ABObject> pigs,List<ABObject> hills, List<ABObject> blocks,  Rectangle sling, List<ABObject> TNTs, Point prevTarget, boolean firstShot, List<ABObject> birds, ABType birdOnSling)
    {
    	Birds = birds;
        BirdOnSling = birdOnSling;      

        Pigs = pigs;
        Hills = hills;
        Blocks = blocks;
        Sling = sling;
        this.TNTs = TNTs;

        prevTarget = prevTarget;
        firstShot = firstShot;
    }
    
    
    /**
     * Constructor para el primer tiro de la escena.
     */
    public SceneState()
    {
    	Birds = new LinkedList<ABObject>();
        BirdOnSling = ABType.Unknown;

        Pigs = new LinkedList<ABObject>();
        Hills = new LinkedList<ABObject>();
        Blocks = new LinkedList<ABObject>();
        Sling = null;
        this.TNTs = new LinkedList<ABObject>();;

        prevTarget = null;
        firstShot = true;
    }
}