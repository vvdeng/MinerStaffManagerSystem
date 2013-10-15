#include "config.h"

uchar  irTime=0;//红外用全局变量
bit inSetting=0,irProcessFlag=0,irReceiveFlag=0,irBtnFlag=0;
uchar IRcord[4];
uchar irData[33];
uchar irCount=0;
uchar setState=SET_STATE_NONE;

uchar code configMenuArr[CONFIG_LEN]={CONFIG_ADDR,CONFIG_DISPLAY_MODEL,CONFIG_DEBUG,CONFIG_EXIT};
uchar xdata configArr[CONFIG_LEN]={0};


uchar  curMenuIndex=0,curSelBit=0,curSelItem=0;
void Ir_work(void)
{
	switch (IRcord[2]){
	case IR_CHANNEL_MINUS:
	if(setState==SET_STATE_NONE){
		inSetting=1;
		setState=SET_STATE_MENU;
		displayBuf[DOT_POS_INDEX]=MENU_SEL_DOT_POS;
		displayBuf[0]=0;
		displayBuf[1]=0;
		displayBuf[2]=0;
		displayBuf[3]=curMenuIndex;
		displayBuf[4]=0;
		displayBuf[5]=0;
		displayBuf[6]=0;
		displayBuf[7]=0;
		
	}else if(setState==SET_STATE_MENU){
		
		setState=SET_STATE_MENU_ITEM_SEL;
		curSelItem=configMenuArr[curMenuIndex];
		curSelBit=0;
		displayBuf[DOT_POS_INDEX]=MENU_SEL_DOT_POS+curSelBit; //小数点在菜单项后面时保存数据，在配置项之中时才保存数据
		displayBuf[0]=1;
		displayBuf[3]=curMenuIndex;
		displayBuf[5]=configArr[curMenuIndex]/100;
		displayBuf[6]=configArr[curMenuIndex]/10%10;
		displayBuf[7]=configArr[curMenuIndex]%10;
		
	}
	else if(setState==SET_STATE_MENU_ITEM_SEL){
		if(curMenuIndex==CONFIG_EXIT){
			inSetting=0;
			setState=SET_STATE_NONE;
			initDisplayBuf();
		}
		else{
			if(displayBuf[DOT_POS_INDEX]!=MENU_SEL_DOT_POS){
			 	configArr[curMenuIndex]=displayBuf[5]*100+displayBuf[6]*10+displayBuf[7];
			
				saveConfig();
			}
			else{
				displayBuf[5]=configArr[curMenuIndex]/100;
				displayBuf[6]=configArr[curMenuIndex]/10%10;
				displayBuf[7]=configArr[curMenuIndex]%10;
			}
			
			setState=SET_STATE_MENU;
			displayBuf[DOT_POS_INDEX]=MENU_SEL_DOT_POS;
			displayBuf[0]=0;
			displayBuf[3]=curMenuIndex;
		
		
		}
	}		

		
	break;

	case IR_NEXT:
	if(setState==SET_STATE_MENU_ITEM_SEL){
		curSelBit=(curSelBit+1)%(VAL_LEN+1); //加一是因为要考虑菜单项
		displayBuf[DOT_POS_INDEX]=MENU_SEL_DOT_POS+curSelBit;		   //displayBuf[DOT_POS_INDEX]等于1时 当前位置为菜单项，确定选择后不保存修改过的值，直接返回“菜单选择”状态
	
	
		
	}
	break;
	case IR_PREV:
	if(setState==SET_STATE_MENU_ITEM_SEL){
		curSelBit=(curSelBit+(VAL_LEN+1)-1)%(VAL_LEN+1);
		displayBuf[DOT_POS_INDEX]=MENU_SEL_DOT_POS+curSelBit;
	
		
		
	}
	
	break;
	case IR_VOL_ADD:
	if(setState==SET_STATE_MENU){
		
		curMenuIndex=(curMenuIndex+1)%MENU_LEN;
	//	curMenu=curMenuArr[curMenuIndex];
		displayBuf[DOT_POS_INDEX]=MENU_SEL_DOT_POS;
		displayBuf[0]=0;
		displayBuf[3]=curMenuIndex;
		displayBuf[5]=0;
		displayBuf[6]=0;
		displayBuf[7]=0;
	} else 	if(setState==SET_STATE_MENU_ITEM_SEL){
		if(curSelBit!=0&&curSelBit!=1){ //等于零时小数点在菜单选择项后面；等于0时为值最高位（千位），该位忽略未用
			displayBuf[displayBuf[DOT_POS_INDEX]]=(displayBuf[displayBuf[DOT_POS_INDEX]]+1)%10;
			
		}
	}
	
	break;
	case IR_VOL_MINUS:
	if(setState==SET_STATE_MENU){
		
		curMenuIndex=(curMenuIndex+MENU_LEN-1)%MENU_LEN;
	//	curMenu=curMenuArr[curMenuIndex];
		displayBuf[DOT_POS_INDEX]=MENU_SEL_DOT_POS;
		displayBuf[0]=0;
		displayBuf[3]=curMenuIndex;
		displayBuf[5]=0;
		displayBuf[6]=0;
		displayBuf[7]=0;
		
	}else 	if(setState==SET_STATE_MENU_ITEM_SEL){
		if(curSelBit!=0&&curSelBit!=1){ //等于零时小数点在菜单选择项后面；等于0时为值最高位（千位），该位忽略未用
			displayBuf[displayBuf[DOT_POS_INDEX]]=(displayBuf[displayBuf[DOT_POS_INDEX]]+10-1)%10;
		
		}
	}
	
	break;
	default:
		break;
	
	}
	
	sendDisplay();
}

void Ircordpro(void)
{ 
	uchar i, j, k=1;
	uchar cord,value;

	for(i=0;i<4;i++)      //处理4个字节
	{
		for(j=1;j<=8;j++) //处理1个字节8位
		{
			cord=irData[k];
	//		if(cord>7)//大于某值为1，这里为7，与晶振有关，这个值需要具体调试	  0.278*7=1.946	0.256*7=    1.792
			if(cord>2)       // 0.6*3=1.8
			{
				value|=0x80;
			}
			if(j<8)
			{
				value>>=1;
			}
			k++;
		}
		IRcord[i]=value;
		value=0;     
	} 
}


void saveConfig(){
	uchar m;
	IapEraseSector(CONFIG_ROM_BASE_ADDR); 

	for(m=0;m<CONFIG_LEN-1;m++){	  //最后一项为退出选项，无需保存至rom
		IapProgramByte(CONFIG_ROM_BASE_ADDR+CONFIG_ROM_ADDR+m,configArr[m]);
		configArr[m]= IapReadByte(CONFIG_ROM_BASE_ADDR+CONFIG_ROM_ADDR+m);
	}
}
void retreiveConfig(){
	uchar m;

	for(m=0;m<CONFIG_LEN-1;m++){	  //最后一项为退出选项，无需保存至rom
//		if(m==CONFIG_XXX){}
		configArr[m]= IapReadByte(CONFIG_ROM_BASE_ADDR+CONFIG_ROM_ADDR+m);
		if(configArr[m]==0xff){
			configArr[m]=0;
		}
	}
}
void processSettingIfNecessary(){
    if(irProcessFlag)                        
	  {   
	   Ircordpro();
	   Ir_work();
 	   irProcessFlag=0;
	   
	  }

	   
  
}
void irTime0Process(){
 
	if(irReceiveFlag==1)
	{
		irTime++;  //用于计数2个下降沿之间的时间
	}
}
void irExProcess(){
	
	if(irProcessFlag==0)                      
	{
		if(irReceiveFlag==1)
		{
	   // 	if(irTime>=33&&irTime<63){//引导码 TC9012的头码，9ms+4.5ms		  0.278*33=9.174  0.278*63=17.514  0.256*33= 8.448 0.256*63= 16.128
	       if(irTime>=15&&irTime<28){ //  0.6*15=9 0.6*27=16.2
				irCount=0;
			}
	    	irData[irCount]=irTime;//存储每个电平的持续时间，用于以后判断是0还是1
	    	irTime=0;
	    	irCount++;
	   		if(irCount==33)
	      	{
				irReceiveFlag=0;
		  		irProcessFlag=1;
				irCount=0;
		//		ET0=0;    //关中断
		//     	TR0 = 0;		//定时器0停止计时
			 
		  	}
		
		}
		else{
			 irReceiveFlag=1;
			 irTime=0;
		//	 ET0=1;    //开中断
		//     TR0 = 1;		//定时器0开始计时
			 
		}
    }
}
void irInit(){	//未打开总中断
  
	AUXR &= 0x7F;		//定时器时钟12T模式
	TMOD &= 0xF0;		//设置定时器模式
	TMOD |= 0x01;		//设置定时器模式
	TL0 = 0xD7;		//设置定时初值   16位 600us 误差1%
	TH0 = 0xFD;		//设置定时初值

	TF0 = 0;		//清除TF0标志

 	IT0 = 1;   //指定外部中断0下降沿触发，INT0 (P3.2)
 	EX0 = 1;   //使能外部中断
 

}