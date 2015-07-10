/**
 * 
 */
package tori.heuristics;

import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import tori.utils.Building;
import tori.utils.Bunker;
import tori.utils.HouseOfCards;
import tori.utils.Tower;
import ab.demo.other.ClientActionRobotJava;
import ab.planner.TrajectoryPlanner;
import ab.vision.ABObject;
import ab.vision.ABShape;
import ab.vision.Vision;

/**
 * @author Emilio J A
 *
 */
public class SceneClassifier {

	public SceneClassifier(){

	}

	public void Identify(SceneState Scene, Vision vision, ClientActionRobotJava ar){
		Scene.Sling = vision.findSlingshotMBR(); // Sling

		List<ABObject> temp = vision.findPigsRealShape() ;
		Scene.Pigs = (temp != null) ? temp : new LinkedList<ABObject>(); // Pigs

		temp = vision.findBlocksRealShape();
		Scene.Blocks = (temp != null) ? temp : new LinkedList<ABObject>(); // Blocks

		temp = vision.findBirdsRealShape(); // Birds
		Scene.Birds = (temp != null) ? temp : new LinkedList<ABObject>();

		temp = vision.findHills();
		Scene.Hills = (temp != null) ? temp : new LinkedList<ABObject>(); // Hills

		temp = vision.findTNTs();
		Scene.TNTs = (temp != null) ? temp : new LinkedList<ABObject>(); // TNTs

		Scene.BirdOnSling = ar.getBirdTypeOnSling(); // BirdType on Sling


		//Scene.Buildings = Building.FindBuildings(Scene); // Construcciones con chanchos
		List<Building> buildings = FindBuildings(Scene.Blocks);
		Scene.Buildings = new LinkedList<Building>();
		Scene.FreeBuildings = new LinkedList<Building>();
		this.ClassifyBuildingsAndPigs(Scene, buildings);

		// TODO: Ver en que clase agregar esto....
		Scene.CircularBlocks.clear();
		for (ABObject b : Scene.Blocks) {
			if (b.shape == ABShape.Circle) {
				Scene.CircularBlocks.add(b);
			}
		}

		System.out.println(Scene.toString());
	}

	private void ClassifyBuildingsAndPigs(SceneState Scene, List<Building> buildings) {
		List<ABObject> bloques = new LinkedList<ABObject>();
		bloques.addAll(Scene.Blocks);


		//        List<ABObject> construciones = new LinkedList<ABObject>();
		//        construciones.addAll(Scene.Blocks);
		//construciones.addAll(Scene.Hills);

		Scene.ObstructedPigs = new LinkedList<ABObject>();
		Scene.FreePigs = new LinkedList<ABObject>();
		Scene.PigsInBuildings = new LinkedList<ABObject>();


		TrajectoryPlanner tp = new TrajectoryPlanner();
		for (ABObject p : Scene.Pigs) {
			boolean PigsObstructed = false;
			for(ABObject bloque : bloques){
				if(tp.trajectoriaObstruida(	Scene.Sling, 
						tp.estimateLaunchPoint(Scene.Sling, p.getCenter()), 
						p.getCenter(), 
						bloque)){
					PigsObstructed = true;
					break;
				} 
			}

			if(PigsObstructed){
				Scene.ObstructedPigs.add(p);
			} else {
				Scene.FreePigs.add(p);
			}

		}

		for (int i = 0; i < buildings.size(); i++) {
			Rectangle buildingBoundary = buildings.get(i).bounding;
			if(buildingBoundary == null)
				buildingBoundary = buildings.get(i).getBoundingRect();        		       		
			boolean havePig = false;
			for (int j = 0; j < Scene.ObstructedPigs.size(); j++) {
				if (Scene.ObstructedPigs.get(j).x >= buildingBoundary.x && Scene.ObstructedPigs.get(j).x <= buildingBoundary.x + buildingBoundary.width &&
						Scene.ObstructedPigs.get(j).y >= buildingBoundary.y && Scene.ObstructedPigs.get(j).y <= buildingBoundary.y + buildingBoundary.height ) {
					havePig = true;
					// Actualizo el SceneState con los chanchos que estan dentro de una construccion.
					Scene.PigsInBuildings.add(Scene.ObstructedPigs.get(j));
					Scene.ObstructedPigs.remove(j);
					j--;
				} 
			}

			if(!havePig){
				Scene.FreeBuildings.add(buildings.get(i));
				buildings.remove(i);
				i--;
			}
		}

		Scene.Buildings = buildings;

	}


	/**
	 * Dado una lista de bloques, se busca si existen construcciones.
	 * @param blocks Listado de bloques que fueron identificados en la pantalla.
	 * @return Lista de construcciones que fueron detectadas.
	 */
	public List<Building> FindBuildings (List<ABObject> objs)
	{
		List<ABObject> tobevisited= new ArrayList<ABObject>(objs);
		List<Building> boundingboxes = new ArrayList<Building> ();

		System.out.println("##### DATOS DE LAS CONSTRUCCIONES #####");
		while(tobevisited.size() != 0){
			Building b = FindBuilding(tobevisited);
			if(b.blocks.size() > 1)
				boundingboxes.add(b);

		}
		System.out.println("\nSE HAN ENCONTRADO " + boundingboxes.size() + " CONSRUCCIONES.\n");
		return boundingboxes;
	}

	/**
	 * Dado una lista de bloques, se busca si existen construcciones.
	 * @param blocks Listado de bloques que fueron identificados en la pantalla.
	 * @return primer construccion encontrada..
	 */
	private Building FindBuilding( List<ABObject> blocks){

		Queue<ABObject> fronta = new ArrayDeque<ABObject> ();
		List<ABObject> total = new ArrayList<ABObject> ();

		fronta.add(blocks.get(0));
		blocks.remove(0);

		while(fronta.size() != 0)
		{
			ABObject tmp = fronta.poll();
			total.add(tmp);

			for (int i=0;i<blocks.size();++i)
			{
				if (tmp.touches(blocks.get(i) ) )
				{
					fronta.add(blocks.get(i));
					blocks.remove(i);  
					--i;
				}
			}
		}
		Building bld =  ClasifyBuilding(total);
		return bld;
	}

	/**
	 * 
	 * @param total
	 * @return
	 */
	public Building ClasifyBuilding( List<ABObject> total){
		Building result = new Building(total); 

		if(result.Densidad() < 0.39 || result.blocks.size() < 4){
			result = new HouseOfCards(result);
		}
		else {
			Rectangle boundary = result.getBoundingRectTWO();
			//				System.out.println("Alto: " + boundary.height + ">= Ancho: " + boundary.width  + " * 1.3\n");
			if(boundary.height >= (boundary.width * 1.3)){
				result = new Tower(result);
			}
			else{
				result = new Bunker(result);
			}

		}

		System.out.println(result.toString());
		return result;
	}
}
