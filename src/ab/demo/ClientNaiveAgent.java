/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2014, XiaoYu (Gary) Ge, Stephen Gould, Jochen Renz
 **  Sahan Abeyasinghe,Jim Keys,  Andrew Wang, Peng Zhang
 ** All rights reserved.
 **This work is licensed under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 **To view a copy of this license, visit http://www.gnu.org/licenses/
 *****************************************************************************/
package ab.demo;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tori.heuristics.SceneClassifier;
import tori.heuristics.SceneState;
import ab.demo.other.ClientActionRobot;
import ab.demo.other.ClientActionRobotJava;
import ab.planner.TrajectoryPlanner;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
//Naive agent (server/client version)

public class ClientNaiveAgent implements Runnable {


	//Wrapper of the communicating messages
	private ClientActionRobotJava ar;
	public byte currentLevel = 0; // TODO: UNO MENOS DEL NIVEL QUE QUEREMOS
	public int failedCounter = 0;
	public int[] solved;
	TrajectoryPlanner tp; 
	private int id = 18077;
	private SceneState Scene;
	private Random randomGenerator;
	/**
	 * Constructor using the default IP
	 * */
	public ClientNaiveAgent() {
		// the default ip is the localhost
		ar = new ClientActionRobotJava("127.0.0.1");
		tp = new TrajectoryPlanner();
		randomGenerator = new Random();
		this.Scene = new SceneState();

	}
	/**
	 * Constructor with a specified IP
	 * */
	public ClientNaiveAgent(String ip) {
		ar = new ClientActionRobotJava(ip);
		tp = new TrajectoryPlanner();
		randomGenerator = new Random();
		this.Scene = new SceneState();

	}
	public ClientNaiveAgent(String ip, int id)
	{
		ar = new ClientActionRobotJava(ip);
		tp = new TrajectoryPlanner();
		randomGenerator = new Random();
		this.Scene = new SceneState();
		this.id = id;
	}
	public int getNextLevel()
	{
		int level = 0;
		boolean unsolved = false;
		//all the level have been solved, then get the first unsolved level
		for (int i = 0; i < solved.length; i++)
		{
			if(solved[i] == 0 )
			{
				unsolved = true;
				level = i + 1;
				if(level <= currentLevel && currentLevel < solved.length)
					continue;
				else
					return level;
			}
		}
		if(unsolved)
			return level;
		level = (currentLevel + 1)%solved.length;
		if(level == 0)
			level = solved.length;
		return level; 
	}
	/* 
	 * Run the Client (Naive Agent)
	 */
	@SuppressWarnings("unused")
	private void checkMyScore()
	{

		int[] scores = ar.checkMyScore();
		System.out.println(" My score: ");
		int level = 1;	
		for(int i: scores)
		{
			System.out.println(" level " + level + "  " + i);
			if (i > 0)
				solved[level - 1] = 1;
			level ++;
		}
	}

	@SuppressWarnings("unused")
	private void GlobalBestScore(){
		//display the global best scores
		int[] scores = ar.checkScore();
		System.out.println("Global best score: ");
		for (int i = 0; i < scores.length ; i ++)
		{

			System.out.print( " level " + (i+1) + ": " + scores[i]);
		}
		System.out.println();
	}

	public void run() {	
		byte[] info = ar.configure(ClientActionRobot.intToByteArray(id));
		solved = new int[info[2]];

		//load the initial level (default 1)
		//Check my score
		//checkMyScore();

		currentLevel = (byte)getNextLevel(); 
		ar.loadLevel(currentLevel);
		GameState state;
		while (true) {

			state = solve();

			System.out.println();
			//If the level is solved , go to the next level
			if (state == GameState.WON) {

				System.out.println("###########################################");
				System.out.println("=> LOADING THE LEVEL " + (currentLevel + 1) );
				System.out.println("###########################################");
				//checkMyScore();
				System.out.println();
				currentLevel = (byte)getNextLevel(); 
				ar.loadLevel(currentLevel);
				//GlobalBestScore();

				// make a new trajectory planner whenever a new level is entered
				tp = new TrajectoryPlanner();

				// first shot on this level, try high shot first
				this.Scene.firstShot = true;

			} else 
				//If lost, then restart the level
				if (state == GameState.LOST) {
					failedCounter++;
					if(failedCounter > 3)
					{
						failedCounter = 0;
						currentLevel = (byte)getNextLevel(); 
						ar.loadLevel(currentLevel);

						//ar.loadLevel((byte)9);
					}
					else
					{		
						System.out.println("restart");
						ar.restartLevel();
					}

				} else 
					if (state == GameState.LEVEL_SELECTION) {
						System.out.println("unexpected level selection page, go to the last current level : "
								+ currentLevel);
						ar.loadLevel(currentLevel);
					} else if (state == GameState.MAIN_MENU) {
						System.out
						.println("unexpected main menu page, reload the level : "
								+ currentLevel);
						ar.loadLevel(currentLevel);
					} else if (state == GameState.EPISODE_MENU) {
						System.out.println("unexpected episode menu page, reload the level: "
								+ currentLevel);
						ar.loadLevel(currentLevel);
					}

		}

	}


	/** 
	 * Solve a particular level by shooting birds directly to pigs
	 * @return GameState: the game state after shots.
	 */
	public GameState solve()
	{

		boolean highShoot = false;

		// capture Image
		BufferedImage screenshot = ar.doScreenShot();

		// process image
		Vision vision = new Vision(screenshot);

		this.percibirElementosDeLaEscena(vision);


		//If the level is loaded (in PLAYINGã€€state)but no slingshot detected, then the agent will request to fully zoom out.
		while (this.Scene.Sling == null && ar.checkState() == GameState.PLAYING) {
			System.out.println("no slingshot detected. Please remove pop up or zoom out");

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			ar.fullyZoomOut();
			screenshot = ar.doScreenShot();
			vision = new Vision(screenshot);
			this.Scene.Sling = vision.findSlingshotMBR();
		}

		GameState state = ar.checkState();
		// if there is a sling, then play, otherwise skip.
		if (this.Scene.Sling != null) {

			//If there are pigs, we pick up a pig randomly and shoot it. 
			if (!this.Scene.Pigs.isEmpty()) {		
				Point releasePoint = null;
				// random pick up a pig

				//ABObject pig = this.Scene.Pigs.get(randomGenerator.nextInt(this.Scene.Pigs.size()));
				/**********************************************/
				/** TODO: IMPLEMENTAR INTELIGENCIA **/
				/**********************************************/

				//TODO ¿REGLAS? Por las cuales se seleccionará el target. De momento, se selecciona un cerdo,
				//deberia de considerarse q el target puede llegar a ser una estructura o punto arbitrario
				Point target;


				ABObject pig = new ABObject();

				if(!this.Scene.FreePigs.isEmpty())
					pig = this.Scene.FreePigs.get(0);
				else if(!this.Scene.ObstructedPigs.isEmpty()){
					pig = this.Scene.ObstructedPigs.get(0);
					highShoot = true;
				}
				else if(!this.Scene.PigsInBuildings.isEmpty())
					pig = this.Scene.PigsInBuildings.get(0);
				else 
					System.out.println("NO ENCONTRO OBJETO PARA DISPARA");

				System.out.println("##### PREPARANDO DISPARO #####");
				System.out.println("Seleccionado Chancho[" + this.Scene.Pigs.indexOf(pig) + "] en la pos: ( " + pig.x + ", " + pig.y + " )");
				System.out.println();


				//TODO SELECION DE PUNTO A DONDE DISPARA
				target = pig.getCenter();

				// if the target is very close to before, randomly choose a
				// point near it
				if (this.Scene.prevTarget != null && distance(this.Scene.prevTarget, target) < 10) {
					double _angle = randomGenerator.nextDouble() * Math.PI * 2;
					target.x = target.x + (int) (Math.cos(_angle) * 10);
					target.y = target.y + (int) (Math.sin(_angle) * 10);
					System.out.println("Randomly changing to " + target);
				}

				this.Scene.prevTarget = new Point(target.x, target.y);

				// estimate the trajectory
				ArrayList<Point> pts = tp.estimateLaunchPoint(this.Scene.Sling, target);


				//Este if es para que si en hay un chancho obstruido hace el tiro alto
				if (highShoot && pts.size() > 1){
					releasePoint = pts.get(1); //pts.get(1) -> tiro alto
				}
				else
					releasePoint = pts.get(0); //pts.get(0) -> tiro bajo

				Point refPoint = tp.getReferencePoint(this.Scene.Sling);

				// Get the release point from the trajectory prediction module
				int tapTime = 0;
				if (releasePoint != null) {
					double releaseAngle = tp.getReleaseAngle(this.Scene.Sling, releasePoint);
					System.out.println("Release Point: " + releasePoint);
					System.out.println("Release Angle: " + Math.toDegrees(releaseAngle));

					/*		int tapInterval = 0;					

					switch (this.Scene.BirdOnSling) 
					{
						case RedBird:
							tapInterval = 0; break;               // start of trajectory
						case YellowBird:
							tapInterval = 65 + randomGenerator.nextInt(25);break; // 65-90% of the way
						case WhiteBird:
							tapInterval =  50 + randomGenerator.nextInt(20);break; // 50-70% of the way
						case BlackBird:
							tapInterval =  0;break; // 70-90% of the way
						case BlueBird:
							tapInterval =  65 + randomGenerator.nextInt(20);break; // 65-85% of the way
						default:
							tapInterval =  60;
					}
					tapTime = tp.getTapTime(this.Scene.Sling, releasePoint, _tpt, tapInterval);
					 */

					Point tapPoint = new Point(target.x, target.y);
					tapTime = tp.getTapTimeFromPoint(this.Scene.Sling, releasePoint, target, tapPoint);

				} else {
					System.err.println("No Release Point Found");
					return ar.checkState();
				}


				// check whether the slingshot is changed. the change of the slingshot indicates a change in the scale.
				ar.fullyZoomOut();
				screenshot = ar.doScreenShot();
				vision = new Vision(screenshot);
				Rectangle _sling = vision.findSlingshotMBR();
				if(_sling != null)
				{
					double scale_diff = Math.pow((this.Scene.Sling.width - _sling.width),2) +  Math.pow((this.Scene.Sling.height - _sling.height),2);
					if(scale_diff < 25)
					{
						int dx = (int) releasePoint.getX() - refPoint.x;
						int dy = (int) releasePoint.getY() - refPoint.y;
						if(dx < 0)
						{
							long timer = System.currentTimeMillis();
							ar.shoot(refPoint.x, refPoint.y, dx, dy, 0, tapTime, false);
							System.out.println("It takes " + (System.currentTimeMillis() - timer) + " ms to take a shot");
							state = ar.checkState();
							if ( state == GameState.PLAYING )
							{
								screenshot = ar.doScreenShot();
								vision = new Vision(screenshot);
								List<Point> traj = vision.findTrajPoints();
								tp.adjustTrajectory(traj, this.Scene.Sling, releasePoint);
								this.Scene.firstShot = false;
							}
						}
					}
					else
						System.out.println("Scale is changed, can not execute the shot, will re-segement the image");
				}
				else
					System.out.println("no sling detected, can not execute the shot, will re-segement the image");

			}
		}
		return state;
	}
	/**
	 * Obtiene todos los elementos que posee la pantalla actual, y lo guarda en Scene.
	 * @param vision
	 */
	private void percibirElementosDeLaEscena(Vision vision) {

		/// TODO: Si se necesitan los otros objetos en la pantalla Descomentar las lineas necesarias.
		/// ( ^___^)b d(^___^ )
		///
		//		this.Scene.Sling = vision.findSlingshotMBR(); // Sling
		//		
		//		List<ABObject> temp = vision.findPigsRealShape() ;
		//		this.Scene.Pigs = (temp != null) ? temp : new LinkedList<ABObject>(); // Pigs
		//		temp = vision.findBlocksRealShape();
		//		this.Scene.Blocks = (temp != null) ? temp : new LinkedList<ABObject>(); // Blocks
		//		temp = vision.findBirdsRealShape(); // Birds
		//        this.Scene.Birds = (temp != null) ? temp : new LinkedList<ABObject>();
		//        temp = vision.findHills();
		//		this.Scene.Hills = (temp != null) ? temp : new LinkedList<ABObject>(); // Hills
		//		temp = vision.findTNTs();
		//		this.Scene.TNTs = (temp != null) ? temp : new LinkedList<ABObject>(); // TNTs
		//		this.Scene.BirdOnSling = ar.getBirdTypeOnSling(); // BirdType on Sling
		//		//this.Scene.Buildings = Building.FindBuildings(this.Scene.Blocks); // Construcciones
		//		this.Scene.Buildings = Building.FindBuildings(this.Scene); // Construcciones con chanchos
		//
		//		// TODO: Ver en que clase agregar esto....
		//		this.Scene.CircularBlocks.clear();
		//		for (ABObject b : this.Scene.Blocks) {
		//			if (b.shape == ABShape.Circle) {
		//				this.Scene.CircularBlocks.add(b);
		//			}
		//		}
		//		
		//		//TODO: Testing
		//		String s = "";
		//		for (ABObject p : this.Scene.Pigs) {
		//			s += String.format("Chancho: %d, %d \tAtrapado: %s\n", p.x, p.y, p.isSomethingBigAbove(this.Scene.Blocks));
		//		}
		//		System.out.println(s);
		//		
		//		
		//		System.out.println(this.Scene.toString());
		new SceneClassifier().Identify(Scene, vision, ar);

	}

	private double distance(Point p1, Point p2) {
		return Math.sqrt((double) ((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)* (p1.y - p2.y)));
	}

	public static void main(String args[]) {

		ClientNaiveAgent na;
		if(args.length > 0)
			na = new ClientNaiveAgent(args[0]);
		else
			na = new ClientNaiveAgent();
		na.run();

	}
}
