package tori.heuristics;

import java.awt.Point;

import ab.vision.ABObject;

public class BestShoot {

	private boolean highShoot;
	private boolean CircularFirstShoot;
	
	public BestShoot() {
		super();
		highShoot = false;
		CircularFirstShoot = true;
	}

	public Point getTarget(SceneState scene){

		ABObject targetObj = new ABObject();
		Point tower = null;
		
		if(!scene.TNTs.isEmpty()){
			System.out.println("** DISPARO A TNT **");
			targetObj = scene.TNTs.get(0);
		}
		else if(!scene.CircularBlocks.isEmpty() && CircularFirstShoot) {
			System.out.println("** DISPARO A PIEDRA CIRCULAR **");
			targetObj = scene.CircularBlocks.get(0);
			CircularFirstShoot = false;
		} 
		else if(!scene.FreePigs.isEmpty()){
			System.out.println("** DISPARO A FREE PIG **");
			targetObj = scene.FreePigs.get(0);
		}
		else if(!scene.ObstructedPigs.isEmpty()){
			System.out.println("** DISPARO A OBSTRUCTED PIG **");
			targetObj = scene.ObstructedPigs.get(0);
			highShoot = true;
		}
		else if(!scene.PigsInBuildings.isEmpty()){
			String msj = "** DISPARO A BUILDING PIG EN ";
			if(scene.Buildings.get(0).GetBuildingType() == "House of Cards"){
				msj += "House of Cards **";
				targetObj = scene.PigsInBuildings.get(0);
			} else if(scene.Buildings.get(0).GetBuildingType() == "Tower"){
				msj += "Tower **";
				int x = scene.Buildings.get(0).getBoundingRect().x + (scene.Buildings.get(0).getBoundingRect().width/2);
				int y = (int) (scene.Buildings.get(0).getBoundingRect().y + (scene.Buildings.get(0).getBoundingRect().height*(0.2)));
				
				tower = new Point(x, y);
				
			} else if(scene.Buildings.get(0).GetBuildingType() == "Bunker"){
				msj += "Bunker **";
				targetObj = scene.PigsInBuildings.get(0);
			} else {
				msj += "algo";
			}
			System.out.println(msj);
			
		}
		else {
			System.out.println("NO ENCONTRO OBJETO PARA DISPARA");
			return null;
		}
		
		if(tower == null){
			return targetObj.getCenter();
		} else {
			return tower;
		}
		
	}
	
	public int getTapTime(SceneState scene){
		int tapInterval = 0;					
		
		switch (scene.BirdOnSling) 
		{
			case RedBird:
				tapInterval = 0; break;               // start of trajectory
			case YellowBird:
				tapInterval = 65; break; // 65-90% of the way
			case WhiteBird:
				tapInterval = 90; break; // 50-70% of the way
			case BlackBird:
				tapInterval = 0;break; // 70-90% of the way
			case BlueBird:
				tapInterval = 90;break; // 65-85% of the way
			default:
				tapInterval = 50;
		}
		
		return tapInterval;
	}

	public boolean isHighShoot() {
		return highShoot;
	}

	public void setHighShoot(boolean highShoot) {
		this.highShoot = highShoot;
	}

	public boolean isCircularFirstShoot() {
		return CircularFirstShoot;
	}

	public void setCircularFirstShoot(boolean circularFirstShoot) {
		CircularFirstShoot = circularFirstShoot;
	}
}
