/**
 * 
 */
package tori.heuristics;

import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import tori.utils.ABObjectComp;
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

		// Original:
		// Scene.BirdOnSling = ar.getBirdTypeOnSling(); // BirdType on Sling

		try 
		{
			Scene.BirdOnSling = Scene.Birds.remove(0).getType();
		} 
		catch (Exception name) 
		{}
		
		//Scene.Buildings = Building.FindBuildings(Scene); // Construcciones con chanchos
		List<Building> buildings1 = FindBuildings(Scene.Blocks);
		List<Building> buildings = SeparateBuildings(buildings1);
		Scene.Buildings = new LinkedList<Building>();
		Scene.FreeBuildings = new LinkedList<Building>();
		this.ClassifyBuildingsAndPigs(Scene, buildings);

		// TODO: Ver en que clase agregar esto....
		Scene.CircularBlocks.clear();
		double radMax =0;
		for (ABObject b : Scene.Blocks) 
		{
			if (b.shape == ABShape.Circle && b.width/2 > radMax) { // Buscamos el radio más grande para luego omitir las piedras chiquitas.
				radMax = b.width/2;
			}
		}
		for (ABObject b : Scene.Blocks) {
			if (b.shape == ABShape.Circle && b.width/2 >= radMax*0.9) { // Agregamos las piedras grandes. Le puse >=0.9r porque son doubles y es raro que den igual. 
				Scene.CircularBlocks.add(b);
			}
		}
		System.out.println("bloques with circular: "  + Scene.Blocks.size());
		Scene.Blocks.removeAll(Scene.CircularBlocks);
		System.out.println("bloques without circular: "  + Scene.Blocks.size());
		ABObjectComp comparator = new ABObjectComp();
        comparator.sortByWidth();
        comparator.sortDesc();
        Collections.sort(Scene.CircularBlocks, comparator);
        
		System.out.println("\n##### PIEDRAS CIRCULARES #####");
		for (ABObject p : Scene.CircularBlocks) {
			System.out.println("piedra: Diametro:" + p.getWidth() + "  Posicion: ( "+ p.x + ", " + p.y + ")");
		}
		System.out.println();



		tori.utils.Logger.Print(Scene.toString());
		System.out.println(Scene.toString());
	}

	public void setBirds(SceneState Scene, ClientActionRobotJava ar)
	{
		Scene.Birds = ar.getListOfBirds();
	}

	private void ClassifyBuildingsAndPigs(SceneState Scene, List<Building> buildings) {
		List<ABObject> bloques = new LinkedList<ABObject>();
		bloques.addAll(Scene.Blocks);


		//List<ABObject> construciones = new LinkedList<ABObject>();
		//construciones.addAll(Scene.Blocks);
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


	// Para cada building, prueba sacar un elemento y ver si eso genera dos buildings.
	public List<Building> SeparateBuildings (List<Building> buildings)
	{
		List<Building> result = new ArrayList<Building> ();
		boolean separated;

		tori.utils.Logger.Print("##### DATOS DE LAS CONSTRUCCIONES DESPUES DE SEPARAR #####");
		System.out.println("##### DATOS DE LAS CONSTRUCCIONES DESPUES DE SEPARAR #####");

		for (int i = 0; i < buildings.size(); i++)
		{
			separated = false;
			if (buildings.get(i).blocks.size() > 5) // No vale la pena si son buildings chicos
			for (int j = 0; j < buildings.get(i).blocks.size(); j++)
			{
				List<ABObject> tempBlocks = new ArrayList<ABObject>(buildings.get(i).blocks);
				tempBlocks.remove(j); // Antes de esto se podría buscar algún filtro para estos bloques, como por ejemplo que soporten por lo menos dos elementos o cosas así, para no tener que probar con todos.
				List<Building> tempBuildings = FindBuildingsInSilence(tempBlocks);
				if (tempBuildings.size() == 2 && ((tempBuildings.get(0).blocks.size() > 5 && tempBuildings.get(1).blocks.size() > 3) || (tempBuildings.get(0).blocks.size() > 3 && tempBuildings.get(1).blocks.size() > 5))) // Si al quitar el bloque y volver a analizar obtenemos dos buildings con al menos uno importante, entonces:
				{
					result.add(tempBuildings.get(0)); 
					tori.utils.Logger.Print(tempBuildings.get(0).toString());
					System.out.println(tempBuildings.get(0).toString());
					
					result.add(tempBuildings.get(1));
					tori.utils.Logger.Print(tempBuildings.get(1).toString());
					System.out.println(tempBuildings.get(1).toString());
					separated = true;
					break;
				}
			}
			if (!separated && buildings.get(i).blocks.size() > 1)
			{
				result.add(buildings.get(i));
				tori.utils.Logger.Print(buildings.get(i).toString());
				System.out.println(buildings.get(i).toString());
			}
		}
		tori.utils.Logger.Print("\nSE HAN ENCONTRADO " + result.size() + " CONSTRUCCIONES.\n");
		System.out.println("\nSE HAN ENCONTRADO " + result.size() + " CONSTRUCCIONES.\n");
		return result;
	}


	public List<Building> FindBuildingsInSilence (List<ABObject> objs) 
	{ // Hace lo mismo pero sin mostrar los mensajes.
		List<ABObject> tobevisited= new ArrayList<ABObject>(objs);
		List<Building> boundingboxes = new ArrayList<Building> ();


		while(tobevisited.size() != 0){
			Building b = FindBuildingInSilence(tobevisited);
			if(b.blocks.size() > 1)
				boundingboxes.add(b);

		}

		return boundingboxes;
	}

	private Building FindBuildingInSilence( List<ABObject> blocks){

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
		Building bld =  ClasifyBuildingInSilence(total);
		return bld;
	}	

	public Building ClasifyBuildingInSilence( List<ABObject> total){
		Building result = new Building(total); 
	
		if(result.Densidad() < 0.39 || (result.blocks.size() < 4 && result.blocks.size() > 1)){ // Si la densidad es menor o tiene 2 o 3 elementos y el elemento de arriba de todo es horizontal o cuadrado o redondo, entonces:
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
		return result;
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

		tori.utils.Logger.Print("##### DATOS DE LAS CONSTRUCCIONES #####");
		System.out.println("##### DATOS DE LAS CONSTRUCCIONES #####");
		while(tobevisited.size() != 0){
			Building b = FindBuilding(tobevisited);
			if(b.blocks.size() > 1)
				boundingboxes.add(b);

		}
		tori.utils.Logger.Print("\nSE HAN ENCONTRADO " + boundingboxes.size() + " CONSTRUCCIONES.\n");
		System.out.println("\nSE HAN ENCONTRADO " + boundingboxes.size() + " CONSTRUCCIONES.\n");


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
	
		if(result.Densidad() < 0.39 || (result.blocks.size() < 4 && result.blocks.size() > 1)){ // Si la densidad es menor o tiene 2 o 3 elementos y el elemento de arriba de todo es horizontal o cuadrado o redondo, entonces:
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
		tori.utils.Logger.Print(result.toString());
		System.out.println(result.toString());
		return result;
	}
}
