/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2015, Team DataLab Birds: 
 ** Karel Rymes, Radim Spetlik, Tomas Borovicka
 ** All rights reserved.
 **This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 **To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ 
 *or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *****************************************************************************/
package tori.heuristics;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import tori.utils.Building;
import ab.vision.ABObject;
import ab.vision.ABType;
/**
**  this class encapsulates the info about the scene that is used later in the different strategies - position of birds, hills, pigs, blocks, etc.
**/
public class SceneState
{
	public List<ABObject> Birds;

    public List<ABObject> Pigs;
    
    public List<ABObject> FreePigs;
    
    public List<ABObject> ObstructedPigs;
    
    public List<ABObject> PigsInBuildings;
    
    public List<ABObject> Hills;
    
    public List<ABObject> Blocks;
    
    public List<ABObject> CircularBlocks;
    
    public List<Building> Buildings;
    
    public List<Building> FreeBuildings;
    
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
        this.FreeBuildings = new LinkedList<Building>();
        this.Buildings = Building.FindBuildings(this.Blocks);
        Sling = sling;
        this.TNTs = TNTs;
        this.prevTarget = prevTarget;
        this.firstShot = firstShot;
    }
    
    
    /**
     * Constructor para el primer tiro de la escena.
     */
    public SceneState()
    {
    	Birds = new LinkedList<ABObject>();
        BirdOnSling = ABType.Unknown;
        this.FreeBuildings = new LinkedList<Building>();
        Pigs = new LinkedList<ABObject>();
        FreePigs = new LinkedList<ABObject>();
        ObstructedPigs = new LinkedList<ABObject>();
        PigsInBuildings = new LinkedList<ABObject>();
        Hills = new LinkedList<ABObject>();
        Blocks = new LinkedList<ABObject>();
        Sling = null;
        this.TNTs = new LinkedList<ABObject>();;

        prevTarget = null;
        firstShot = true;
        
        this.Buildings = new LinkedList<Building>();
        this.CircularBlocks = new LinkedList<ABObject>();
    }
    
    @Override public String toString() {
    	String result = "##### DATOS DEL ESCENARIO #####\n";
    	result += "---------Chanchos------------------------\n";
    	result += " # Total: " + this.Pigs.size() + ".\n";
        result += " # En Construcción: " + this.PigsInBuildings.size() + ".\n";
        result += " # Chanchos Obstruidos: " + this.ObstructedPigs.size() + ".\n";
        result += " # Chanchos Libres: " + this.FreePigs.size() + ".\n";
        
        result += "---------Bloques-------------------------\n";
        result += " # Total: " + this.Blocks.size() + ".\n";
        result += " # Circulares: " + this.CircularBlocks.size() + ".\n";
        
        result += "---------Construcciones------------------\n";
        
        result += " # Total: " + ( this.Buildings.size() + this.FreeBuildings.size() ) + ". (2 o más bloques)\n";
        result += " # Con chanchos dentro: " + this.Buildings.size() + ".\n";
        result += " # Sin chanchos dentro: " + this.FreeBuildings.size() + ".\n";
        
        result += "---------Tipos de Construcciones---------\n";
        
        int HoC = 0,
        	Bkr = 0,
        	Twr = 0;
        List<Building> blds = new LinkedList<Building>();
        blds.addAll(this.Buildings);
        blds.addAll(this.FreeBuildings);
        for (Building bld : blds) {
        	
        	switch(bld.GetBuildingType()){
        		case "Bunker":
        			Bkr ++;
        			break;
        		case "House of Cards":
        			HoC ++;
        			break;
        		case "Tower": 
        			Twr ++;
        			break;
        	}
		}
        result += " # House of Cards: " + HoC + "\n";
        result += " # Bunkers: " + Bkr + "\n";
        result += " # Tower: " + Twr + "\n";
        
        result += "---------Pájaros-------------------------\n";
        
        result += " # Total: " + this.Birds.size() + ".\n";
        result += " # En la resortera: " + this.BirdOnSling.toString() + ".\n"; 
        
        result += "---------Colinas-------------------------\n";
        
        result += " # Total: " + this.Hills.size() + ".\n";
        
        result += "---------TNTs----------------------------\n";
        
        result += " # Total: " + this.TNTs.size() + ".\n";
    	return result;
    }
    
}