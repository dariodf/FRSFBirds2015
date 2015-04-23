/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2015, Team DataLab Birds: 
 ** Karel Rymes, Radim Spetlik, Tomas Borovicka
 ** All rights reserved.
 **This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 **To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ 
 *or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *****************************************************************************/
package dl.heuristics;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Collections;
import java.util.Comparator;

import ab.demo.other.ClientActionRobot;
import ab.demo.other.ClientActionRobotJava;

import ab.demo.other.Shot;

import ab.planner.TrajectoryPlanner;

import ab.utils.StateUtil;
import ab.utils.ABUtil;

import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
import ab.vision.VisionUtils;
import ab.vision.real.shape.Body;

import dl.utils.*;
/**
*	Dynamite strategy tries to aim at the TNT only if there is a pig nearby. We count that the dynamite is even more useful when there are a lot of stone objects and other TNTs within a given pixel range.
*/
public class DynamiteHeuristic extends AbstractHeuristic
{
	private boolean noHighShotFound = false;	
	
	private static final int [] dynamiteUtility = {
		//Pig
			//Ice 
				//Wood 
					//Stone 
		3700,50,100,400
	};

	/**
    *    Basic constructor. It has to have all the information about the game scene, i.e. blocks, hills,pigs, birds, actionRobot, etc.
    */	

	public DynamiteHeuristic(SceneState currentState, ClientActionRobot actionRobot,  TrajectoryPlanner tp, LogWriter lg)
	{
		super(currentState, actionRobot, tp, lg);

		_utility = estimateUtility();

		if ( _possibleDLTrajectories.size() == 0 )
		{
			noHighShotFound = true;
			_utility = estimateUtility();
		}
	}
	
    /**
    *	Performs the calculation of the trajectory utility.
    */ 
	
	@Override
	protected int estimateUtility()
	{
		_possibleDLTrajectories = new ArrayList<DLTrajectory>();           
	
		// find all reachable targets and save them to _possibleDLTrajectories array
		for (ABObject tmpTargetObject : _currentState._TNTs)
		{
			
			boolean flag = false;
			
			for (ABObject pig : _currentState._pigs)
			{
				if ( isInTheRightDistance(pig, tmpTargetObject))
				{
					flag = true;
					break;
				}
			}

			if ( flag == false)
			{
				continue;
			}	

			// get target point
			Point tmpTargetCenterPoint = tmpTargetObject.getCenter();	

			estimateTrajectories(tmpTargetCenterPoint, tmpTargetObject);

			int whiteBirdCorrection = 0;

			if (_currentState._birdOnSling == ABType.WhiteBird)
			{
				whiteBirdCorrection = 3;				
			}

			// search around the target
			for (int i = -1 - whiteBirdCorrection; i < 2 + whiteBirdCorrection; i += 2)
			{
				searchAroundTheTarget(i, tmpTargetCenterPoint, tmpTargetObject);
			}					
		}
		
		if (_possibleDLTrajectories.size() == 0)
			return 0xffff0000;	
		
		// sort available DLTrajectory possibilities by number of pigs in the way
		Collections.sort(_possibleDLTrajectories, new pigCountComparator());

		_selectedDLTrajectory = selectBestTrajectoryWRTAvailableBirds();

		return _selectedDLTrajectory.heuristicUtility;
	}
	/**
	**	searches around the target for a better trajectory
	**/
	private void searchAroundTheTarget(int i, Point tmpTargetCenterPoint, ABObject tmpTargetObject)
	{
		int radius = _currentState._birdOnSling.getBirdRadius();

		if (_currentState._birdOnSling == ABType.WhiteBird)
		{
			if (i == 0)
			{
				return;
			}
			radius = (int)(radius * 0.9);
		}

		Point tmpTargetPoint = new Point(tmpTargetCenterPoint.x + i * radius,tmpTargetCenterPoint.y );

		estimateTrajectories(tmpTargetPoint, tmpTargetObject);

		if (_currentState._birdOnSling != ABType.WhiteBird)
		{
			tmpTargetPoint = new Point(tmpTargetCenterPoint.x, tmpTargetCenterPoint.y + i * radius);

			estimateTrajectories(tmpTargetPoint, tmpTargetObject);
		}

	}
	/**
	**	decides if there is a pig in the right distance from the TNT
	**/
	private boolean isInTheRightDistance(ABObject pig, ABObject tmpTargetObject)
	{
		if ( tmpTargetObject.getCenterX() - 90 < pig.x
			&& tmpTargetObject.getCenterX() + 90 > pig.x 
			&& tmpTargetObject.getCenterY() - 90 < pig.y
			&& tmpTargetObject.getCenterY() + 90 > pig.y)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	*	Finds and calculates a trajectory and its utilities for a given target object. (dynamite)
	*/
	protected void estimateTrajectories(Point tmpTargetPoint, ABObject tmpTargetObject)
	{
		ArrayList<Point> pts = null;

		// estimate launch point
		if (noHighShotFound == false)
		{
			pts = _tp.estimateLaunchPoint(_currentState._sling, tmpTargetPoint, _currentState._hills, _currentState._blocks, tmpTargetObject, _currentState._birdOnSling);
		} 
		else
		{
			pts = _tp.estimateLaunchPoint(_currentState._sling, tmpTargetPoint);
		}

		for (Point tmpReleasePoint : pts)
		{
			estimateSingleTrajectory(tmpReleasePoint, tmpTargetPoint, tmpTargetObject);
		}
	}

	/**
	**	estimates a single trajectory for the target
	**/
	private void estimateSingleTrajectory(Point tmpReleasePoint, Point tmpTargetPoint, ABObject tmpTargetObject)
	{
		// create new instance of DLTrajectory
		DLTrajectory tmpDLTrajectory = new DLTrajectory(_actionRobot, _tp, _currentState._sling, _currentState._birdOnSling, tmpReleasePoint, tmpTargetPoint, tmpTargetObject, _currentState._hills, _currentState._blocks, _currentState._pigs);
		int trajUtility = 0;
		for (ABObject tmp : _currentState._blocks)
		{
			double dist = distance(tmpTargetPoint, new Point((int)tmp.getCenterX(), (int)tmp.getCenterY() ) );

			if (dist < 60 && tmp.type.id > 8)
			{
				trajUtility += ((60 - dist) / 100.0) * dynamiteUtility[tmp.type.id - 9];
			}
		}

		for (ABObject tmp : _currentState._pigs)
		{
			double dist = distance(tmpTargetPoint, new Point((int)tmp.getCenterX(), (int)tmp.getCenterY() ) );

			if (dist < 110 && tmp.type.id > 8)
			{
				trajUtility += ((110 - dist) / 100.0) * dynamiteUtility[tmp.type.id - 9];
			}
		}

		for (ABObject tmpTNT : _currentState._TNTs)
		{
			double dist = distance(tmpTargetPoint, new Point((int)tmpTNT.getCenterX(), (int)tmpTNT.getCenterY() ) );

			if (dist < 50 )
			{
				trajUtility += ((50 - dist) / 100.0) * 2500;
			}
		}

		tmpDLTrajectory.heuristicUtility = trajUtility +(int) ( 1.2 * tmpDLTrajectory.trajectoryUtility) + tmpDLTrajectory.pigsInTheWay.size() * 5000;

		// add trajectory to possible trajectories
		_possibleDLTrajectories.add(tmpDLTrajectory);
	}
    /**
    *	This function calculates when to tap for a given bird and given target object. This information is later passed to the server.
    *	@return the time in milliseconds.
    */     
    @Override
    protected int getTapInterval()
    {
        int tapInterval = 0;
        int collision = 100;

        if(_currentState._birdOnSling == ABType.YellowBird 
            || _currentState._birdOnSling == ABType.BlueBird 
            || noHighShotFound == true  )
        {
            collision = _selectedDLTrajectory.getPercentageOfTheTrajectoryWhenTheFirstObjectIsHit(_currentState._blocks);
        }
        
        switch (_currentState._birdOnSling)
        {
            case RedBird:
                tapInterval = 0; break;               // start of trajectory
            case YellowBird:
                tapInterval = collision - 15;break; // 65-90% of the way
            case WhiteBird:
                tapInterval =  collision - 7;break; // 70-90% of the way
            case BlackBird:
                tapInterval =  0;break; 
            case BlueBird:
                tapInterval =  collision - 15;break; // 65-85% of the way
            default:
                tapInterval =  60;
        }
        if ( _currentState._birdOnSling == ABType.WhiteBird && noHighShotFound == false )
        {
            if (Math.toDegrees(_selectedDLTrajectory.releaseAngle) > 45)
                tapInterval = 95;
            else 
                tapInterval = 98;
            
            return _tp.getTapTime(_currentState._sling, _selectedDLTrajectory.releasePoint, new Point(_selectedDLTrajectory.targetPoint.x - 10, _selectedDLTrajectory.targetPoint.y) , tapInterval);
        }
        
        int ret = _tp.getTapTime(_currentState._sling, _selectedDLTrajectory.releasePoint, _selectedDLTrajectory.targetPoint, tapInterval);
        if (_currentState._birdOnSling == ABType.BlackBird )
        {
            ret = 6000;
        }

        return ret;
    }
    /**
   	*	@return ID of the heuristic for log purposes.
    */
    @Override
    public int getHeuristicId()
    {
        return 3;
    }
}