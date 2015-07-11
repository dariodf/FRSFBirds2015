package tori.heuristics;

import java.awt.Point;

public class BestShoot {

	private boolean highShoot;
	private boolean CircularFirstShoot;
	
	public BestShoot() {
		super();
		highShoot = false;
		CircularFirstShoot = true;
	}

	public Point getTarget(SceneState scene){

		String objStr;
		Point targetObj = null;
		highShoot = false;
		
		/*if(!scene.TNTs.isEmpty()){
			System.out.println("** DISPARO A TNT **");
			targetObj = scene.TNTs.get(0);
		}
		else */
		if(!scene.CircularBlocks.isEmpty() && CircularFirstShoot) {
			objStr = "** DISPARO A PIEDRA CIRCULAR **";
			int x = scene.CircularBlocks.get(0).x - 1;
			int y = scene.CircularBlocks.get(0).y + scene.CircularBlocks.get(0).height;
			
			targetObj = new Point(x, y);
			CircularFirstShoot = false;
		}
		else if(!scene.PigsInBuildings.isEmpty()){
			String msj = "** DISPARO A BUILDING PIG EN ";
			if(scene.Buildings.get(0).GetBuildingType() == "House of Cards"){
				msj += "HOUSE OF CARDS **";
				targetObj = scene.PigsInBuildings.get(0).getCenter();
			} else if(scene.Buildings.get(0).GetBuildingType() == "Tower"){
				msj += "TOWER **";
				int x = scene.Buildings.get(0).getBoundingRect().x;
				int y = scene.Buildings.get(0).getBoundingRect().y + (scene.Buildings.get(0).getBoundingRect().height/2);
				
				targetObj = new Point(x, y);
				
			} else if(scene.Buildings.get(0).GetBuildingType() == "Bunker"){
				msj += "BUNKER **";
				int x = scene.Buildings.get(0).getBoundingRect().x + (scene.Buildings.get(0).getBoundingRect().width/2);
				int y = scene.Buildings.get(0).getBoundingRect().y + (scene.Buildings.get(0).getBoundingRect().height/2);
				
				targetObj = new Point(x, y);
			} else {
				msj += "NOTHING";
			}
			objStr = msj;
			
		}
		else if(!scene.ObstructedPigs.isEmpty()){
			objStr = "** DISPARO A OBSTRUCTED PIG **";
			targetObj = scene.ObstructedPigs.get(0).getCenter();
			highShoot = true;
		}
		else if(!scene.FreePigs.isEmpty()){
			objStr = "** DISPARO A FREE PIG **";
			targetObj = scene.FreePigs.get(0).getCenter();
		}
		else {
			objStr = "NO ENCONTRO OBJETO PARA DISPARA";
			return null;
		}
		
		tori.utils.Logger.Print(objStr);
		System.out.println(objStr);
		
		return targetObj;
		
	}
	
	public int getTapTime(SceneState scene){
		int tapInterval = 0;					
		
		switch (scene.BirdOnSling) 
		{
			case RedBird:
				tapInterval = 0; break;               // start of trajectory
			case YellowBird:
				tapInterval = 80; break; // 65-90% of the way
			case WhiteBird:
				tapInterval = 50; break; // 50-70% of the way
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
