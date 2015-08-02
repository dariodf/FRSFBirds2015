/**
 * 
 */
package tori.utils;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import tori.heuristics.SceneState;
import tori.utils.interfaces.IBuilding;
import ab.demo.other.Shot;
import ab.vision.ABObject;
import ab.vision.ABType;

/**
 * @author msp20_000
 *
 */
public class Bunker extends Building  {

	/**
	 * @param objs
	 */
	public Bunker(List<ABObject> objs) {
		super(objs);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param objs
	 */
	public Bunker(Building bld) {
		super( new LinkedList<ABObject>(bld.blocks));
		
	}

	@Override
	public String GetBuildingType(){
		return "Bunker";
	}
	
	@Override
	public Shot GetBestShot(SceneState Scene){
		System.out.println("** DISPARO A UN BUNKER. **");
		/**
		 * TODO: completar la implementacion
		 *
		 *	Construcción: ### Bunker ###

			tipo de pájaro:
				Rojo:
					Apuntar al Chancho
			
				Amarillo:
					Apuntar a la base del bunker para que impacte en el centro
						disparo bajo
						Habilidad al 50%
			
				Azul:
					Apuntar del medio
						Habilidad llegando a la estructura
			
				Blanco:
					Disparo a una de las dos paredes de la construccion
						Habilidad sobre el Punto seleccionado.
			
				Negro:
					Apuntar a la base de la estructura
						No usar Habilidad
		 **/
		
		
		Shot resultShot = new Shot();
		
		
		switch (Scene.BirdOnSling) {
		case RedBird:
			resultShot.setX((int)this.pigs.get(0).getCenterX());
			resultShot.setY((int)this.pigs.get(0).getCenterY());
			break;
		case YellowBird:
			
			/*
			 * Amarillo:
					Apuntar a la base del bunker para que impacte en el centro
						disparo bajo
						Habilidad al 50%
			 */
			resultShot.setX(this.getBoundingRect().x + this.getBoundingRect().width/2);
			resultShot.setY(this.getBoundingRect().y + (int)(this.getBoundingRect().height*0.80)); //TODO: VER PORCENTAJE
			break;
		case WhiteBird:
			/*
			 * Blanco:
					Disparo a una de las dos paredes de la construccion
						Habilidad sobre el Punto seleccionado.
			 */
			int WhiteThreshold = 2;
				resultShot.setX((int)this.getBoundingRect().x - WhiteThreshold);
				resultShot.setY(this.getBoundingRect().y - WhiteThreshold *5); //TODO: VER threshold con el building
			
			break;
		case BlackBird:
			/*
			 * Negro:
					Apuntar a la base de la estructura
						No usar Habilidad
			 */
			resultShot.setX(this.getBoundingRect().x + this.getBoundingRect().width/2);
			resultShot.setY(this.getBoundingRect().y + this.getBoundingRect().height); //TODO: VER PORCENTAJE
			break;
		case BlueBird:
			/*
			 * Azul:
					Apuntar del medio
						Habilidad llegando a la estructura
			 */
			resultShot.setX(this.getBoundingRect().x + this.getBoundingRect().width/2);
			resultShot.setY(this.getBoundingRect().y + (int)(this.getBoundingRect().height*0.45)); //TODO: VER PORCENTAJE
			
			break;
		default:
			break;
		}
		resultShot.setT_tap(this.GetTapTime(Scene.BirdOnSling));
		return resultShot;
		
	}
	@Override
	public int GetTapTime(ABType Bird) {
		// TODO Auto-generated method stub
		int result = 0;
		switch (Bird) {
		case RedBird:
			result = 0;
			break;
		case YellowBird:
			result = 55;
			break;
		case WhiteBird:
			result = 100;
			break;
		case BlackBird:
			result = 0;
			break;
		case BlueBird:
			result = 85;
			break;
		default:
			result = 0;
			break;
		}
		
		return result;
	}
}
