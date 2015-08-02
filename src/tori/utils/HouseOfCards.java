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
public class HouseOfCards extends Building{

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
	
	@Override
	public Shot GetBestShot(SceneState Scene){
		
		/*TODO: completar la implementacion
		 *
		 *	Construcción: ### House of Cards ###
			
			Tipo de pájaro:
				Rojo:
					Si no hay piedra Redonda
						A la base
					Sino
						A la piedra
			
				Amarillo:
					Apuntar del medio hacia abajo
						disparo a 45º 
						Habilidad al 50%
			
				Azul:
					Apuntar del medio hacia abajo
						Habilidad llegando a la estructura
			
				Blanco:
					Disparo sobre el chancho
						Habilidad sobre el chancho.
			
				Negro:
					Apuntar al centro de la estructura
						No usar Habilidad
		 */
		
		
		Shot resultShot = new Shot();
		System.out.println("** DISPARO A UNA HOUSE OF CARDS. **");
		
		switch (Scene.BirdOnSling) {
		case RedBird:
			if(this.circularBlocks.isEmpty()){
				resultShot.setX(this.getBoundingRect().x);
				resultShot.setY(this.getBoundingRect().y + (int)(this.getBoundingRect().height*0.75)); //TODO: VER PORCENTAJE
			}
			else{
				resultShot.setX(this.circularBlocks.get(0).x);
				resultShot.setY(this.circularBlocks.get(0).y + (int)(this.circularBlocks.get(0).height*0.75)); //TODO: VER PORCENTAJE
			}
				
			break;
		case YellowBird:
			
			/*
			 * Amarillo:
					Apuntar del medio hacia abajo
						disparo a 45º (tiro bajo)
						Habilidad al 50%
			 */
			resultShot.setX(this.getBoundingRect().x + this.getBoundingRect().width/2);
			resultShot.setY(this.getBoundingRect().y + (int)(this.getBoundingRect().height*0.60)); //TODO: VER PORCENTAJE
			break;
		case WhiteBird:
			/*
			 * Blanco:
					Disparo sobre el chancho
						Habilidad sobre el chancho.
			 */
			int WhiteThreshold = 30;
			if(!this.pigs.isEmpty()){
				resultShot.setX((int)this.pigs.get(0).getCenterX());
				resultShot.setY(this.getBoundingRect().y - WhiteThreshold); //TODO: VER threshold con el building
			}
			else{
				resultShot.setX(this.getBoundingRect().x + this.getBoundingRect().width/2);
				resultShot.setY(this.getBoundingRect().y - WhiteThreshold); //TODO: VER threshold con el building
			}
			break;
		case BlackBird:
			/*
			 * Negro:
					Apuntar al centro de la estructura
						No usar Habilidad
			 */
			resultShot.setX(this.getBoundingRect().x + this.getBoundingRect().width/2);
			resultShot.setY(this.getBoundingRect().y + this.getBoundingRect().height/2); //TODO: VER PORCENTAJE
			break;
		case BlueBird:
			/*
			 * Azul:
					Apuntar del medio hacia abajo
						Habilidad llegando a la estructura
			
			 */
			resultShot.setX(this.getBoundingRect().x);
			resultShot.setY(this.getBoundingRect().y + (int)(this.getBoundingRect().height*0.60)); //TODO: VER PORCENTAJE
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
		System.out.println("Usando el metodo desde la clase 'House of Cards'.");
		int tapTime = 0;
		switch (Bird) {
		case RedBird:
			tapTime = 0;
			break;
		case YellowBird:
			tapTime = 55;
			break;
		case WhiteBird:
			tapTime = 100;
			break;
		case BlackBird:
			tapTime = 0;
			break;
		case BlueBird:
			tapTime = 90;
			break;
		default:
			tapTime = 0;
			break;
		}
		
		return tapTime;
	}
}
