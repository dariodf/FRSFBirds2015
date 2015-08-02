package tori.heuristics;

import java.awt.Point;
import java.util.Random;

import ab.demo.other.Shot;
import ab.vision.ABType;
import tori.utils.Building;

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
			boolean inBuilding = false;
			int bld = -1;
			int x,y;
			for (int i = 0; i < scene.Buildings.size(); i++) {
				if(scene.Buildings.get(i).isBlockinBuilding(scene.CircularBlocks.get(0) ) ){
					inBuilding = true;
					bld = i;
					break;
				}
			}
			
			if(inBuilding){
//				objStr = "** DISPARO A PIEDRA CIRCULAR EN BUILDING **";
//				x = scene.Buildings.get(bld).x;
//				y = (int)(scene.Buildings.get(bld).getBoundingRect().y + (scene.Buildings.get(bld).getBoundingRect().height*0.35));
				this.CircularFirstShoot = false;
				return this.getTarget(scene);
				
			}
			else{
				objStr = "** DISPARO A PIEDRA CIRCULAR **";
				x = scene.CircularBlocks.get(0).x - 1;
				y = (int)(scene.CircularBlocks.get(0).y + scene.CircularBlocks.get(0).height * 0.7);
			}
			
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
				int y = (int)(scene.Buildings.get(0).getBoundingRect().y + (scene.Buildings.get(0).getBoundingRect().height*0.35));
				
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
			objStr = "NO ENCONTRO OBJETO PARA DISPARAR";
			return null;
		}
		
		//tori.utils.Logger.Print(objStr);
		System.out.println(objStr);
		
		return targetObj;
		
	}
	
	public Shot getBestTarget(SceneState scene){
		
		
		Shot target = null;
		String objStr = "";
		Point targetObj = null;
		highShoot = false;
		
		/*if(!scene.TNTs.isEmpty()){
			System.out.println("** DISPARO A TNT **");
			targetObj = scene.TNTs.get(0);
		}
		else */
		if(!scene.CircularBlocks.isEmpty() && CircularFirstShoot) {
			boolean inBuilding = false;
			int bld = -1;
			int x,y;
			for (int i = 0; i < scene.Buildings.size(); i++) {
				if(scene.Buildings.get(i).isBlockinBuilding(scene.CircularBlocks.get(0) ) ){
					inBuilding = true;
					bld = i;
					break;
				}
			}
			
			if(inBuilding){
//				objStr = "** DISPARO A PIEDRA CIRCULAR EN BUILDING **";
//				x = scene.Buildings.get(bld).x;
//				y = (int)(scene.Buildings.get(bld).getBoundingRect().y + (scene.Buildings.get(bld).getBoundingRect().height*0.35));
				this.CircularFirstShoot = false;
				target = this.getBestTarget(scene);
				
			}
			else{
				objStr = "** DISPARO A PIEDRA CIRCULAR **";
				target = new Shot();
				target.setX(scene.CircularBlocks.get(0).x - 1);
				target.setY((int)(scene.CircularBlocks.get(0).y + scene.CircularBlocks.get(0).height * 0.7));
				target.setT_tap(this.getTapTime(scene));
			}
			CircularFirstShoot = false;
		}
		else if(!scene.PigsInBuildings.isEmpty()){
			objStr = "** DISPARO A CONSTRUCCION \"" + scene.Buildings.get(0).GetBuildingType().toUpperCase() + "\" **";
			target =scene.Buildings.get(0).GetBestShot(scene);
		}
		else if(!scene.ObstructedPigs.isEmpty()){
			objStr = "** DISPARO A OBSTRUCTED PIG **";
			target = new Shot();
			target.setX(scene.ObstructedPigs.get(0).getCenter().x);
			target.setY(scene.ObstructedPigs.get(0).getCenter().y);
			target.setT_tap(this.getTapTime(scene));
			highShoot = true;
		}
		else if(!scene.FreePigs.isEmpty()){
			objStr = "** DISPARO A FREE PIG **";
			target = new Shot();
			target.setX(scene.FreePigs.get(0).getCenter().x);
			target.setY(scene.FreePigs.get(0).getCenter().y);
			if(scene.BirdOnSling == ABType.YellowBird)
				target.setT_tap(0);
			else
				target.setT_tap(this.getTapTime(scene));
		}
		else {
			objStr = "NO ENCONTRO OBJETO PARA DISPARAR";
			return null;
		}
		
		//tori.utils.Logger.Print(objStr);
		System.out.println(objStr);
		
		//Compare if the target point is the same that the las one.
		this.CompareActualTargetWithTheLast(scene, target);
		return target;
		
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
				tapInterval = 100; break; // 50-70% of the way
			case BlackBird:
				tapInterval = 0;break; // 70-90% of the way
			case BlueBird:
				if(this.isHighShoot())
					tapInterval =80;
				else
					tapInterval = 90;
				
				break; // 65-85% of the way
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
	
	public void CompareActualTargetWithTheLast(SceneState Scene, Shot actualTarget){
		Random randomGenerator = new Random();
		// if the target is very close to before, randomly choose a
		// point near it
		if (Scene.prevTarget != null && distance(Scene.prevTarget, actualTarget.getXY()) < 10) {
			double _angle = randomGenerator.nextDouble() * Math.PI;
			actualTarget.setX( actualTarget.getX() + (int) (Math.cos(_angle) * 2));
			actualTarget.setY( actualTarget.getY() + (int) (Math.sin(_angle) * 2));
			System.out.println("[Correction] New Target Point " + actualTarget.getXY());
		}
	}
	private double distance(Point p1, Point p2) {
		return Math.sqrt((double) ((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)* (p1.y - p2.y)));
	}
}
