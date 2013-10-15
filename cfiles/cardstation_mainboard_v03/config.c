#include "config.h"

uchar  irTime=0;//������ȫ�ֱ���
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
		displayBuf[DOT_POS_INDEX]=MENU_SEL_DOT_POS+curSelBit; //С�����ڲ˵������ʱ�������ݣ���������֮��ʱ�ű�������
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
		curSelBit=(curSelBit+1)%(VAL_LEN+1); //��һ����ΪҪ���ǲ˵���
		displayBuf[DOT_POS_INDEX]=MENU_SEL_DOT_POS+curSelBit;		   //displayBuf[DOT_POS_INDEX]����1ʱ ��ǰλ��Ϊ�˵��ȷ��ѡ��󲻱����޸Ĺ���ֵ��ֱ�ӷ��ء��˵�ѡ��״̬
	
	
		
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
		if(curSelBit!=0&&curSelBit!=1){ //������ʱС�����ڲ˵�ѡ������棻����0ʱΪֵ���λ��ǧλ������λ����δ��
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
		if(curSelBit!=0&&curSelBit!=1){ //������ʱС�����ڲ˵�ѡ������棻����0ʱΪֵ���λ��ǧλ������λ����δ��
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

	for(i=0;i<4;i++)      //����4���ֽ�
	{
		for(j=1;j<=8;j++) //����1���ֽ�8λ
		{
			cord=irData[k];
	//		if(cord>7)//����ĳֵΪ1������Ϊ7���뾧���йأ����ֵ��Ҫ�������	  0.278*7=1.946	0.256*7=    1.792
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

	for(m=0;m<CONFIG_LEN-1;m++){	  //���һ��Ϊ�˳�ѡ����豣����rom
		IapProgramByte(CONFIG_ROM_BASE_ADDR+CONFIG_ROM_ADDR+m,configArr[m]);
		configArr[m]= IapReadByte(CONFIG_ROM_BASE_ADDR+CONFIG_ROM_ADDR+m);
	}
}
void retreiveConfig(){
	uchar m;

	for(m=0;m<CONFIG_LEN-1;m++){	  //���һ��Ϊ�˳�ѡ����豣����rom
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
		irTime++;  //���ڼ���2���½���֮���ʱ��
	}
}
void irExProcess(){
	
	if(irProcessFlag==0)                      
	{
		if(irReceiveFlag==1)
		{
	   // 	if(irTime>=33&&irTime<63){//������ TC9012��ͷ�룬9ms+4.5ms		  0.278*33=9.174  0.278*63=17.514  0.256*33= 8.448 0.256*63= 16.128
	       if(irTime>=15&&irTime<28){ //  0.6*15=9 0.6*27=16.2
				irCount=0;
			}
	    	irData[irCount]=irTime;//�洢ÿ����ƽ�ĳ���ʱ�䣬�����Ժ��ж���0����1
	    	irTime=0;
	    	irCount++;
	   		if(irCount==33)
	      	{
				irReceiveFlag=0;
		  		irProcessFlag=1;
				irCount=0;
		//		ET0=0;    //���ж�
		//     	TR0 = 0;		//��ʱ��0ֹͣ��ʱ
			 
		  	}
		
		}
		else{
			 irReceiveFlag=1;
			 irTime=0;
		//	 ET0=1;    //���ж�
		//     TR0 = 1;		//��ʱ��0��ʼ��ʱ
			 
		}
    }
}
void irInit(){	//δ�����ж�
  
	AUXR &= 0x7F;		//��ʱ��ʱ��12Tģʽ
	TMOD &= 0xF0;		//���ö�ʱ��ģʽ
	TMOD |= 0x01;		//���ö�ʱ��ģʽ
	TL0 = 0xD7;		//���ö�ʱ��ֵ   16λ 600us ���1%
	TH0 = 0xFD;		//���ö�ʱ��ֵ

	TF0 = 0;		//���TF0��־

 	IT0 = 1;   //ָ���ⲿ�ж�0�½��ش�����INT0 (P3.2)
 	EX0 = 1;   //ʹ���ⲿ�ж�
 

}