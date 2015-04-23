/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2015,  XiaoYu (Gary) Ge, Stephen Gould,Jochen Renz
 ** Sahan Abeyasinghe, Jim Keys,   Andrew Wang, Peng Zhang
 ** Team DataLab Birds: Karel Rymes, Radim Spetlik, Tomas Borovicka
 ** All rights reserved.
 **This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 **To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/
 *or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *****************************************************************************/
package ab.demo;

import ab.planner.abTrajectory;
import ab.utils.GameImageRecorder;
import ab.vision.ShowSeg;


public class MainEntry {
	// the entry of the software.
	public static void main(String args[])
	{
		String command = "";
		if (args.length > 0)
		{
			command = args[0];
			if (args.length == 1)
			{
				//int level = Integer.parseInt(args[0]);
				ClientNaiveAgent na = new ClientNaiveAgent(args[0]);
				//ClientNaiveAgent na = new ClientNaiveAgent(level);
				na.run();
			} 
			else if (args.length == 2)
			{
				int id = Integer.parseInt(args[1]);
				ClientNaiveAgent na = new ClientNaiveAgent(args[0],id);

				na.run();
			}
			else if (args.length == 3)
			{
				int id = Integer.parseInt(args[1]);
				int level = Integer.parseInt(args[2]);
				ClientNaiveAgent na = new ClientNaiveAgent(args[0],id,level);

				na.run();
			}
		}
		else 
			System.out.println("Please input the correct command");

		System.exit(0);
	}
}
