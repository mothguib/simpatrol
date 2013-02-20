package Analyses;

import java.awt.Color;

import util.CurveViewer;
import util.DrawStyle;

public class courbes {

	public static void main(String[] args){
		Double[] percents = {25.0, 24.0, 23.0, 22.0, 21.0, 20.0, 19.0, 18.0, 17.0, 16.0, 15.0, 14.0, 13.0, 12.0, 11.0, 10.0, 9.0, 8.0, 7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0};
		Double[] percents2 = {75.0, 76.0, 77.0, 78.0, 79.0, 80.0, 81.0, 82.0, 83.0, 84.0, 85.0, 86.0, 87.0, 88.0, 89.0, 90.0, 91.0, 92.0, 93.0, 94.0, 95.0, 96.0, 97.0, 98.0, 99.0};
		Double[] RR_25 = {21.0,	22.0, 40.0, 41.0, 58.0, 70.0, 71.0, 82.0, 95.0, 97.0, 101.0, 103.0, 106.0, 117.0, 124.0, 126.0, 147.0, 160.0, 164.0, 178.0, 181.0, 187.0, 190.0, 193.0, 228.0};
		Double[] CR_25 ={0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 3.0, 4.0, 4.0, 13.0, 16.0, 25.0, 59.0, 66.0, 91.0};
		Double[] SCf_25 ={39.0, 39.0, 40.0, 40.0, 40.0, 41.0, 42.0, 42.0, 43.0, 43.0, 44.0, 44.0, 45.0, 46.0, 46.0, 47.0, 48.0, 48.0, 49.0, 51.0, 52.0, 53.0, 55.0, 58.0, 65.0};
		Double[] SCS1_25 ={456.0, 457.0, 457.0, 457.0, 457.0, 457.0, 458.0, 458.0, 458.0, 458.0, 459.0, 459.0, 459.0, 460.0, 460.0, 461.0, 461.0, 461.0, 462.0, 462.0, 463.0, 464.0, 465.0, 468.0, 481.0};
		Double[] SCS2_25 ={718.0, 719.0, 720.0, 722.0, 723.0, 725.0, 727.0, 728.0, 730.0, 732.0, 734.0, 736.0, 738.0, 740.0, 742.0, 744.0, 747.0, 749.0, 752.0, 755.0, 758.0, 763.0, 768.0, 775.0, 784.0};
		Double[] CC_25 ={0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 4.0, 7.0, 13.0, 19.0, 30.0, 49.0, 67.0, 78.0, 104.0};
		Double[] HPCC_25 ={0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0, 7.0, 17.0, 33.0, 40.0, 50.0};

		for(int i = 0; i < percents.length; i++){
			RR_25[i] = Math.log10(RR_25[i]);
			if(RR_25[i] < 0) RR_25[i] = 0d;
			
			CR_25[i] = Math.log10(CR_25[i]);
			if(CR_25[i] < 0) CR_25[i] = 0d;
			
			SCf_25[i] = Math.log10(SCf_25[i]);
			if(SCf_25[i] < 0) SCf_25[i] = 0d;
			
			SCS1_25[i] = Math.log10(SCS1_25[i]);
			if(SCS1_25[i] < 0) SCS1_25[i] = 0d;
			
			SCS2_25[i] = Math.log10(SCS2_25[i]);
			if(SCS2_25[i] < 0) SCS2_25[i] = 0d;
			
			CC_25[i] = Math.log10(CC_25[i]);
			if(CC_25[i] < 0) CC_25[i] = 0d;
			
			HPCC_25[i] = Math.log10(HPCC_25[i]);
			if(HPCC_25[i] < 0) HPCC_25[i] = 0d;
			
		}
		
		
		
		CurveViewer myviewer = new CurveViewer("25->24");
		myviewer.addCurve(percents2, RR_25, Color.blue, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer.addCurve(percents2, CR_25, Color.red, DrawStyle.LONG_DOTS);
		myviewer.addCurve(percents2, SCf_25, Color.yellow, DrawStyle.POINT_ROUND);
		myviewer.addCurve(percents2, SCS1_25, Color.green, DrawStyle.POINT_ROUND);
		myviewer.addCurve(percents2, SCS2_25, Color.magenta, DrawStyle.POINT_ROUND);
		myviewer.addCurve(percents2, CC_25, Color.cyan, DrawStyle.POINT_ROUND);
		myviewer.addCurve(percents2, HPCC_25, Color.DARK_GRAY, DrawStyle.POINT_ROUND);
		
//		myviewer.addCurve(RR_25, percents2, Color.blue, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
//		myviewer.addCurve(CR_25, percents2, Color.red, DrawStyle.LONG_DOTS);
//		myviewer.addCurve(SCf_25, percents2, Color.cyan, DrawStyle.POINT_ROUND);
//		myviewer.addCurve(SCS1_25, percents2, Color.green, DrawStyle.POINT_ROUND);
//		myviewer.addCurve(SCS2_25, percents2, Color.orange, DrawStyle.POINT_ROUND);
//		myviewer.addCurve(CC_25, percents2, Color.gray, DrawStyle.POINT_ROUND);
//		myviewer.addCurve(HPCC_25, percents2, Color.pink, DrawStyle.POINT_ROUND);
		
		//myviewer.setXdivision(50);
		//myviewer.setYdivision(50);
		myviewer.setXcenter(75);
		myviewer.setVisible(true);
		
		
		
		Double[] RR_15 = {76.0, 78.0, 79.0, 80.0, 82.0, 94.0, 96.0, 108.0, 110.0, 111.0, 113.0, 118.0, 131.0, 138.0, 185.0, 187.0, 192.0, 199.0, 201.0, 208.0, 222.0, 233.0, 236.0, 270.0, 354.0};
		Double[] CR_15 = {12.0, 16.0, 16.0, 17.0, 19.0, 21.0, 23.0, 24.0, 27.0, 29.0, 32.0, 41.0, 51.0, 64.0, 75.0, 79.0, 82.0, 85.0, 119.0, 124.0, 136.0, 140.0, 145.0, 160.0, 194.0};
		Double[] SCf_15 = {44.0, 44.0, 45.0, 46.0, 46.0, 47.0, 48.0, 48.0, 49.0, 50.0, 51.0, 51.0, 52.0, 53.0, 54.0, 55.0, 56.0, 58.0, 59.0, 61.0, 62.0, 64.0, 67.0, 70.0, 80.0};
		Double[] SCS1_15 = {448.0, 448.0, 448.0, 449.0, 449.0, 449.0, 450.0, 450.0, 451.0, 451.0, 452.0, 452.0, 453.0, 453.0, 454.0, 455.0, 457.0, 466.0, 466.0, 467.0, 468.0, 470.0, 471.0, 473.0, 476.0};
		Double[] SCS2_15 = {704.0, 706.0, 708.0, 710.0, 712.0, 715.0, 717.0, 719.0, 721.0, 723.0, 725.0, 728.0, 731.0, 733.0, 736.0, 739.0, 742.0, 745.0, 749.0, 753.0, 758.0, 762.0, 769.0, 779.0, 787.0};
		Double[] CC_15 = {1.0, 2.0, 3.0, 4.0, 4.0, 4.0, 5.0, 6.0, 7.0, 19.0, 21.0, 25.0, 27.0, 30.0, 72.0, 77.0, 101.0, 105.0, 142.0, 151.0, 191.0, 213.0, 299.0, 343.0, 353.0};
		Double[] HPCC_15 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0, 13.0, 15.0, 17.0, 23.0, 35.0, 91.0};
	
		for(int i = 0; i < percents.length; i++){
			RR_15[i] = Math.log10(RR_15[i]);
			if(RR_15[i] < 0) RR_15[i] = 0d;
			
			CR_15[i] = Math.log10(CR_15[i]);
			if(CR_15[i] < 0) CR_15[i] = 0d;
			
			SCf_15[i] = Math.log10(SCf_15[i]);
			if(SCf_15[i] < 0) SCf_15[i] = 0d;
			
			SCS1_15[i] = Math.log10(SCS1_15[i]);
			if(SCS1_15[i] < 0) SCS1_15[i] = 0d;
			
			SCS2_15[i] = Math.log10(SCS2_15[i]);
			if(SCS2_15[i] < 0) SCS2_15[i] = 0d;
			
			CC_15[i] = Math.log10(CC_15[i]);
			if(CC_15[i] < 0) CC_15[i] = 0d;
			
			HPCC_15[i] = Math.log10(HPCC_15[i]);
			if(HPCC_15[i] < 0) HPCC_15[i] = 0d;
			
		}
		
		CurveViewer myviewer2 = new CurveViewer("15->14");
//		myviewer2.addCurve(percents2, RR_15, Color.blue, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
//		myviewer2.addCurve(percents2, CR_15, Color.red, DrawStyle.LONG_DOTS);
//		myviewer2.addCurve(percents2, SCf_15, Color.cyan, DrawStyle.POINT_ROUND);
//		myviewer2.addCurve(percents2, SCS1_15, Color.green, DrawStyle.POINT_ROUND);
//		myviewer2.addCurve(percents2, SCS2_15, Color.orange, DrawStyle.POINT_ROUND);
//		myviewer2.addCurve(percents2, CC_15, Color.gray, DrawStyle.POINT_ROUND);
//		myviewer2.addCurve(percents2, HPCC_15, Color.pink, DrawStyle.POINT_ROUND);
		
		myviewer2.addCurve(RR_15, percents2, Color.blue, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer2.addCurve(CR_15, percents2, Color.red, DrawStyle.LONG_DOTS);
		myviewer2.addCurve(SCf_15, percents2, Color.yellow, DrawStyle.POINT_ROUND);
		myviewer2.addCurve(SCS1_15, percents2, Color.green, DrawStyle.POINT_ROUND);
		myviewer2.addCurve(SCS2_15, percents2, Color.magenta, DrawStyle.POINT_ROUND);
		myviewer2.addCurve(CC_15, percents2, Color.cyan, DrawStyle.POINT_ROUND);
		myviewer2.addCurve(HPCC_15, percents2, Color.DARK_GRAY, DrawStyle.POINT_ROUND);
		
		//myviewer.setXdivision(50);
		//myviewer.setYdivision(50);
		myviewer2.setYcenter(75);
		myviewer2.setVisible(true);
		
		
		
		
		Double[] RR_10 = {119.0, 121.0, 123.0, 131.0, 134.0, 160.0, 162.0, 165.0, 185.0, 187.0, 190.0, 194.0, 198.0, 303.0, 341.0, 350.0, 360.0, 377.0, 460.0, 476.0, 485.0, 536.0, 571.0, 644.0, 679.0};
		Double[] CR_10 = {25.0, 26.0, 27.0, 31.0, 86.0, 87.0, 92.0, 93.0, 94.0, 95.0, 101.0, 104.0, 111.0, 129.0, 156.0, 184.0, 189.0, 193.0, 204.0, 207.0, 224.0, 300.0, 312.0, 359.0, 395.0};
		Double[] SCf_10 = {54.0, 55.0, 56.0, 57.0, 58.0, 59.0, 60.0, 61.0, 62.0, 63.0, 64.0, 65.0, 66.0, 67.0, 69.0, 70.0, 72.0, 73.0, 75.0, 77.0, 80.0, 82.0, 85.0, 89.0, 96.0};
		Double[] SCS1_10 = {462.0, 462.0, 463.0, 463.0, 463.0, 464.0, 464.0, 465.0, 465.0, 466.0, 467.0, 467.0, 468.0, 468.0, 469.0, 470.0, 471.0, 471.0, 473.0, 474.0, 475.0, 476.0, 478.0, 482.0, 494.0};
		Double[] SCS2_10 = {677.0, 679.0, 681.0, 683.0, 685.0, 687.0, 689.0, 691.0, 694.0, 696.0, 698.0, 701.0, 704.0, 707.0, 710.0, 713.0, 717.0, 720.0, 724.0, 728.0, 732.0, 738.0, 742.0, 748.0, 757.0};
		Double[] CC_10 = {5.0, 6.0, 7.0, 10.0, 25.0, 35.0, 38.0, 44.0, 48.0, 55.0, 62.0, 83.0, 92.0, 99.0, 118.0, 130.0, 136.0, 183.0, 237.0, 275.0, 381.0, 448.0, 475.0, 606.0, 655.0};
		Double[] HPCC_10 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 3.0, 6.0, 17.0, 38.0, 55.0, 66.0, 76.0, 101.0, 113.0};

		for(int i = 0; i < percents.length; i++){
			RR_10[i] = Math.log10(RR_10[i]);
			if(RR_10[i] < 0) RR_10[i] = 0d;
			
			CR_10[i] = Math.log10(CR_10[i]);
			if(CR_10[i] < 0) CR_10[i] = 0d;
			
			SCf_10[i] = Math.log10(SCf_10[i]);
			if(SCf_10[i] < 0) SCf_10[i] = 0d;
			
			SCS1_10[i] = Math.log10(SCS1_10[i]);
			if(SCS1_10[i] < 0) SCS1_10[i] = 0d;
			
			SCS2_10[i] = Math.log10(SCS2_10[i]);
			if(SCS2_10[i] < 0) SCS2_10[i] = 0d;
			
			CC_10[i] = Math.log10(CC_10[i]);
			if(CC_10[i] < 0) CC_10[i] = 0d;
			
			HPCC_10[i] = Math.log10(HPCC_10[i]);
			if(HPCC_10[i] < 0) HPCC_10[i] = 0d;
			
		}
		
		CurveViewer myviewer3 = new CurveViewer("10->9");
//		myviewer3.addCurve(percents2, RR_10, Color.blue, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
//		myviewer3.addCurve(percents2, CR_10, Color.red, DrawStyle.LONG_DOTS);
//		myviewer3.addCurve(percents2, SCf_10, Color.cyan, DrawStyle.POINT_ROUND);
//		myviewer3.addCurve(percents2, SCS1_10, Color.green, DrawStyle.POINT_ROUND);
//		myviewer3.addCurve(percents2, SCS2_10, Color.orange, DrawStyle.POINT_ROUND);
//		myviewer3.addCurve(percents2, CC_10, Color.gray, DrawStyle.POINT_ROUND);
//		myviewer3.addCurve(percents2, HPCC_10, Color.pink, DrawStyle.POINT_ROUND);
		
		myviewer3.addCurve(RR_10, percents2, Color.blue, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer3.addCurve(CR_10, percents2, Color.red, DrawStyle.LONG_DOTS);
		myviewer3.addCurve(SCf_10, percents2, Color.cyan, DrawStyle.POINT_ROUND);
		myviewer3.addCurve(SCS1_10, percents2, Color.green, DrawStyle.POINT_ROUND);
		myviewer3.addCurve(SCS2_10, percents2, Color.orange, DrawStyle.POINT_ROUND);
		myviewer3.addCurve(CC_10, percents2, Color.gray, DrawStyle.POINT_ROUND);
		myviewer3.addCurve(HPCC_10, percents2, Color.pink, DrawStyle.POINT_ROUND);
		
		//myviewer3.setXdivision(50);
		//myviewer3.setYdivision(50);
		myviewer3.setYcenter(75);
		myviewer3.setVisible(true);
		
		
		
		Double[] RR_5 = {133.0, 137.0, 212.0, 233.0, 237.0, 242.0, 247.0, 293.0, 301.0, 360.0, 371.0, 376.0, 383.0, 387.0, 433.0, 442.0, 447.0, 452.0, 457.0, 469.0, 474.0, 484.0, 540.0, 545.0, 551.0};
		Double[] CR_5 = {50.0, 68.0, 70.0, 74.0, 161.0, 164.0, 167.0, 170.0, 182.0, 205.0, 211.0, 216.0, 230.0, 243.0, 279.0, 286.0, 309.0, 320.0, 333.0, 351.0, 356.0, 363.0, 433.0, 439.0, 569.0};
		Double[] SCf_5 = {88.0, 90.0, 91.0, 93.0, 95.0, 97.0, 98.0, 100.0, 102.0, 104.0, 106.0, 108.0, 111.0, 113.0, 115.0, 118.0, 120.0, 123.0, 127.0, 130.0, 134.0, 138.0, 143.0, 149.0, 158.0};
		Double[] SCS1_5 = {482.0, 483.0, 484.0, 486.0, 487.0, 488.0, 489.0, 490.0, 491.0, 492.0, 493.0, 495.0, 496.0, 498.0, 499.0, 501.0, 502.0, 504.0, 506.0, 508.0, 511.0, 513.0, 517.0, 521.0, 526.0};
		Double[] SCS2_5 = {587.0, 589.0, 591.0, 593.0, 595.0, 598.0, 600.0, 602.0, 604.0, 607.0, 609.0, 628.0, 631.0, 634.0, 637.0, 640.0, 644.0, 647.0, 651.0, 655.0, 659.0, 663.0, 668.0, 674.0, 682.0};
		Double[] CC_5 = {0.0, 0.0, 0.0, 1.0, 1.0, 2.0, 3.0, 6.0, 10.0, 14.0, 17.0, 26.0, 34.0, 36.0, 40.0, 43.0, 53.0, 61.0, 144.0, 150.0, 185.0, 197.0, 247.0, 289.0, 345.0};
		Double[] HPCC_5 = {0.0, 0.0, 1.0, 1.0, 2.0, 2.0, 2.0, 4.0, 7.0, 12.0, 14.0, 20.0, 30.0, 40.0, 57.0, 74.0, 103.0, 108.0, 121.0, 132.0, 141.0, 155.0, 236.0, 244.0, 269.0};

		for(int i = 0; i < percents.length; i++){
			RR_5[i] = Math.log10(RR_5[i]);
			if(RR_5[i] < 0) RR_5[i] = 0d;
			
			CR_5[i] = Math.log10(CR_5[i]);
			if(CR_5[i] < 0) CR_5[i] = 0d;
			
			SCf_5[i] = Math.log10(SCf_5[i]);
			if(SCf_5[i] < 0) SCf_5[i] = 0d;
			
			SCS1_5[i] = Math.log10(SCS1_5[i]);
			if(SCS1_5[i] < 0) SCS1_5[i] = 0d;
			
			SCS2_5[i] = Math.log10(SCS2_5[i]);
			if(SCS2_5[i] < 0) SCS2_5[i] = 0d;
			
			CC_5[i] = Math.log10(CC_5[i]);
			if(CC_5[i] < 0) CC_5[i] = 0d;
			
			HPCC_5[i] = Math.log10(HPCC_5[i]);
			if(HPCC_5[i] < 0) HPCC_5[i] = 0d;
			
		}
		
		CurveViewer myviewer4 = new CurveViewer("5->4");
//		myviewer4.addCurve(percents2, RR_5, Color.blue, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
//		myviewer4.addCurve(percents2, CR_5, Color.red, DrawStyle.LONG_DOTS);
//		myviewer4.addCurve(percents2, SCf_5, Color.cyan, DrawStyle.POINT_ROUND);
//		myviewer4.addCurve(percents2, SCS1_5, Color.green, DrawStyle.POINT_ROUND);
//		myviewer4.addCurve(percents2, SCS2_5, Color.orange, DrawStyle.POINT_ROUND);
//		myviewer4.addCurve(percents2, CC_5, Color.gray, DrawStyle.POINT_ROUND);
//		myviewer4.addCurve(percents2, HPCC_5, Color.pink, DrawStyle.POINT_ROUND);
		
		myviewer4.addCurve(RR_5, percents2, Color.blue, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer4.addCurve(CR_5, percents2, Color.red, DrawStyle.LONG_DOTS);
		myviewer4.addCurve(SCf_5, percents2, Color.cyan, DrawStyle.POINT_ROUND);
		myviewer4.addCurve(SCS1_5, percents2, Color.green, DrawStyle.POINT_ROUND);
		myviewer4.addCurve(SCS2_5, percents2, Color.orange, DrawStyle.POINT_ROUND);
		myviewer4.addCurve(CC_5, percents2, Color.gray, DrawStyle.POINT_ROUND);
		myviewer4.addCurve(HPCC_5, percents2, Color.pink, DrawStyle.POINT_ROUND);
		
		//myviewer4.setXdivision(50);
		//myviewer4.setYdivision(50);
		myviewer4.setYcenter(75);
		myviewer4.setVisible(true);
		
		Double[] RR_2 = {597.0, 656.0, 688.0, 722.0, 741.0, 806.0, 877.0, 932.0, 1109.0, 1121.0, 1244.0, 1293.0, 1337.0, 1381.0, 1426.0, 1511.0, 1648.0, 1656.0, 1702.0, 1833.0, 1837.0, 1838.0, 1881.0, 1955.0, 1955.0};
		Double[] CR_2 = {169.0, 178.0, 189.0, 214.0, 231.0, 240.0, 264.0, 288.0, 298.0, 308.0, 319.0, 342.0, 360.0, 399.0, 487.0, 496.0, 510.0, 518.0, 527.0, 544.0, 552.0, 577.0, 592.0, 634.0, 692.0};
		Double[] SC_2 = {53.0, 57.0, 61.0, 65.0, 69.0, 72.0, 76.0, 80.0, 84.0, 88.0, 92.0, 96.0, 100.0, 104.0, 108.0, 112.0, 116.0, 121.0, 126.0, 132.0, 139.0, 146.0, 154.0, 162.0, 179.0};
		Double[] CC_2 = {15.0, 18.0, 20.0, 24.0, 27.0, 31.0, 37.0, 43.0, 49.0, 58.0, 65.0, 73.0, 81.0, 91.0, 99.0, 107.0, 116.0, 137.0, 146.0, 159.0, 168.0, 176.0, 200.0, 207.0, 215.0};
		Double[] HPCC_2 = {183.0, 190.0, 197.0, 215.0, 222.0, 242.0, 250.0, 264.0, 272.0, 323.0, 334.0, 340.0, 346.0, 352.0, 421.0, 482.0, 489.0, 553.0, 579.0, 584.0, 591.0, 625.0, 713.0, 783.0, 835.0};

		for(int i = 0; i < percents.length; i++){
			RR_2[i] = Math.log10(RR_2[i]);
			if(RR_2[i] < 0) RR_2[i] = 0d;
			
			CR_2[i] = Math.log10(CR_2[i]);
			if(CR_2[i] < 0) CR_2[i] = 0d;
			
			SC_2[i] = Math.log10(SC_2[i]);
			if(SC_2[i] < 0) SC_2[i] = 0d;
			
			CC_2[i] = Math.log10(CC_2[i]);
			if(CC_2[i] < 0) CC_2[i] = 0d;
			
			HPCC_2[i] = Math.log10(HPCC_2[i]);
			if(HPCC_2[i] < 0) HPCC_2[i] = 0d;
			
		}
		
		CurveViewer myviewer5 = new CurveViewer("2->1");
//		myviewer5.addCurve(percents2, RR_2, Color.blue, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
//		myviewer5.addCurve(percents2, CR_2, Color.red, DrawStyle.LONG_DOTS);
//		myviewer5.addCurve(percents2, SCf_2, Color.cyan, DrawStyle.POINT_ROUND);
//		myviewer5.addCurve(percents2, SCS1_2, Color.green, DrawStyle.POINT_ROUND);
//		myviewer5.addCurve(percents2, SCS2_2, Color.orange, DrawStyle.POINT_ROUND);
//		myviewer5.addCurve(percents2, CC_2, Color.gray, DrawStyle.POINT_ROUND);
//		myviewer5.addCurve(percents2, HPCC_2, Color.pink, DrawStyle.POINT_ROUND);
		
		myviewer5.addCurve(RR_2, percents2, Color.blue, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer5.addCurve(CR_2, percents2, Color.red, DrawStyle.LONG_DOTS);
		myviewer5.addCurve(SC_2, percents2, Color.cyan, DrawStyle.POINT_ROUND);
		myviewer5.addCurve(CC_2, percents2, Color.gray, DrawStyle.POINT_ROUND);
		myviewer5.addCurve(HPCC_2, percents2, Color.pink, DrawStyle.POINT_ROUND);
		
		//myviewer5.setXdivision(50);
		//myviewer5.setYdivision(50);
		myviewer5.setYcenter(75);
		myviewer5.setVisible(true);
	}
}
