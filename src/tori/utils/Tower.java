/**
 * 
 */
package tori.utils;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import tori.heuristics.SceneState;
import ab.demo.other.Shot;
import ab.vision.ABObject;
import ab.vision.ABType;


/**
 * @author msp20_000
 *
 */
public class Tower extends Bunker  {

	public Tower(List<ABObject> objs) {
		super(objs);
		// TODO Auto-generated constructor stub
	}

	public Tower(Building bld) {
		// TODO Auto-generated constructor stub
		super( new LinkedList<ABObject>(bld.blocks));
	}

	@Override
	public String GetBuildingType(){
		return "Tower";
	}
	
	@Override
	public Shot GetBestShot(SceneState Scene){
		System.out.println("** DISPARO A UNA TOWER. **");
		/*TODO: completar la implementacion
		 *
		 *	Construcción: ### Tower ###

			tipo de pájaro:
				Rojo:
					Si no hay piedra Redonda
						A la base
					Sino
						A la piedra
			
				Amarillo:
					Apuntar al centro de la torre
						disparo bajo
						Habilidad llegando a la contruccion
			
				Azul:
					Apuntar al punto medio
						Habilidad llegando a la estructura
			
				Blanco:
					Apuntar al chancho
						Habilidad sobre el Punto seleccionado.
			
				Negro:
					Apuntar al centro de la estructura
						No usar Habilidad
		 */
		
		
		Shot resultShot = new Shot();
		
		
		switch (Scene.BirdOnSling) {
		case RedBird:
			if(this.circularBlocks.isEmpty()){
				resultShot.setX(this.getBoundingRect().x + (int)(this.getBoundingRect().width * 0.5));
				resultShot.setY(this.getBoundingRect().y + (int)(this.getBoundingRect().height * 0.2)); //TODO: VER PORCENTAJE
			}
			else{
				resultShot.setX(this.circularBlocks.get(0).getBounds().x + (int)(this.circularBlocks.get(0).getBounds().width * 0.5));
				resultShot.setY(this.circularBlocks.get(0).getBounds().y + (int)(this.circularBlocks.get(0).getBounds().height)); //TODO: VER PORCENTAJE
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
			resultShot.setY(this.getBoundingRect().y + (int)(this.getBoundingRect().height*0.65)); //TODO: VER PORCENTAJE
			break;
		case WhiteBird:
			/*
			 * Blanco:
					Disparo sobre el chancho
						Habilidad sobre el chancho.
			 */
			int WhiteThreshold = 10;
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
			resultShot.setX(this.getBoundingRect().x );
			resultShot.setY(this.getBoundingRect().y + (int)(this.getBoundingRect().height*0.5)); //TODO: VER PORCENTAJE
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
