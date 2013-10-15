package com.vvdeng.miner.staff.ui;
import java.awt.*;


public class GBC extends GridBagConstraints
{
  public static final int FULL=1;
   public GBC(int gridx, int gridy)
   {
      this.gridx = gridx;
      this.gridy = gridy;
   }
   public GBC(int type){
	   switch(type){
	   case FULL:
		   this.gridx=0;
		   this.gridy=0;
		   this.setWeightFull().setFillBoth();
		   break;
	   }
   }
  
   public GBC(int gridx, int gridy, int gridwidth, int gridheight)
   {
      this.gridx = gridx;
      this.gridy = gridy;
      this.gridwidth = gridwidth;
      this.gridheight = gridheight;
   }

  
   public GBC setAnchor(int anchor)
   {
      this.anchor = anchor;
      return this;
   }

   
   public GBC setFill(int fill)
   {
      this.fill = fill;
      return this;
   }
   public GBC setFillBoth(){
	   this.fill=BOTH;
	   return this;
   }
   public GBC setFillV(){
	   this.fill=VERTICAL;
	   return this;
   }
   public GBC setFillH(){
	   this.fill=HORIZONTAL;
	   return this;
   }
  
   public GBC setWeight(double weightx, double weighty)
   {
      this.weightx = weightx;
      this.weighty = weighty;
      return this;
   }
   public GBC setWeightFull(){
	   this.weightx=100;
	   this.weighty=100;
	   return this;
   }
  
   public GBC setInsets(int distance)
   {
      this.insets = new Insets(distance, distance, distance, distance);
      return this;
   }

  
   public GBC setInsets(int top, int left, int bottom, int right)
   {
      this.insets = new Insets(top, left, bottom, right);
      return this;
   }

  
   public GBC setIpad(int ipadx, int ipady)
   {
      this.ipadx = ipadx;
      this.ipady = ipady;
      return this;
   }
}
