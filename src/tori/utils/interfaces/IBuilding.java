package tori.utils.interfaces;

import ab.demo.other.Shot;
import ab.vision.ABType;
import tori.heuristics.SceneState;

public interface IBuilding {

	public Shot GetBestShot(SceneState Scene);
	
	public int GetTapTime(ABType Bird);
}
