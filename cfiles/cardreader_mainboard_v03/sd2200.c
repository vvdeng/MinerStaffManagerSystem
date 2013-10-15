#include "sd2200.h"
uchar xdata  initTimeBuf[SET_TIME_LEN]={13,10,10,40,10,10,10,0};//年月日周时分秒空
uchar xdata timeBuf[SET_TIME_LEN];
uchar xdata sendTimeBuf[SEND_TIME_LEN] = {0};//年月日周时分秒空
                     
uchar xdata showTimeBuf[SHOW_TIME_LEN]={0};   //年月日周时分秒空


uchar xdata  date[7]; 

/*********I2C延时***********/
void I2CWait(void)
{	
	_nop_();_nop_();_nop_();_nop_();
}


/********开启SD2200的I2C总线********/
bit I2CStart(void)
{
	SDA=1;
	SCL=1;
	I2CWait();
	if(!SDA)return 0;//SDA线为低电平则总线忙,退出
	SDA=0;
	I2CWait();
	while(SDA)return 0;//SDA线为高电平则总线出错,退出
	SCL=0;
	I2CWait();
	return 1;
}


/********关闭SD2200的I2C总线*******/
void I2CStop(void)
{
	SDA=0;
	SCL=0;
	I2CWait();
	SCL=1;
	I2CWait();
	SDA=1;
}


/*********发送 ACK*********/
void I2CAck(void)
{	
	SDA=0;
	SCL=0;
	I2CWait();
	SCL=1;
	I2CWait();
	SCL=0;
}


/*********发送NO ACK*********/
void I2CNoAck(void)
{	
	SDA=1;
	SCL=0;
	I2CWait();
	SCL=1;
	I2CWait();
	SCL=0;
}


/*********读取ACK信号*********/
bit I2CWaitAck(void)  //返回为:1=有ACK,0=无ACK
{
	uchar errtime=255;
	SCL=0;
	SDA=1;
	I2CWait();
	SCL=1;
	I2CWait();
	while(SDA)
	{
		errtime--;
		if(!errtime)
		SCL=0;
		return 0;
	}
	SCL=0;
	return 1;
}


/************从SD2200发送一个字节*************/
void I2CSendByte(uchar demand,bit order) //order=1,H_L;order=0,L_H
{
	uchar i=8;                       //order=1，从HI-->LO发送8bit数据
	                                 //order=0，从LO-->HI发送8bit数据
	if(order)
	{
		while(i--)
		{
			SCL=0;
			_nop_();
			SDA=(bit)(demand&0x80);
			demand<<=1;
			I2CWait();
			SCL=1;
			I2CWait();
		}
		SCL=0;
	}
     else
	{
		while(i--)
		 {
			SCL=0;
			_nop_();
			SDA=(bit)(demand&0x01);
			demand>>=1;
			I2CWait();
			SCL=1;
			I2CWait();
		 }
		 SCL=0;
	}
}


/*********MCU从SD2200读入一字节*********/
uchar I2CReceiveByte(void)
{
	uchar i=8;
	uchar ddata=0;
	SDA=1;
	while(i--)
	{
		ddata>>=1;          //数据从低位开始读取
		SCL=0;
		I2CWait();
		SCL=1;
		I2CWait();	   //从低位开始 ddata|=SDA;ddata>>=1
		if(SDA)
		{
			ddata|=0x80;
		}
	}
	SCL=0;
	return ddata;
}


/******读SD2200实时数据寄存器******/
void I2CReadDate(void)
{
	uchar m,tmp;
	if(!I2CStart())return;
	I2CSendByte(0x65,1);//从年开始读取数据
	if(!I2CWaitAck()){I2CStop();return;}
	for(m=0;m<7;m++)
	{
		timeBuf[m]=I2CReceiveByte();
		if (m!=6)         //最后一个数据不应答
		{
			I2CAck();
		}
	}
	I2CNoAck();
	I2CStop();
	for(m=0;m<SEND_TIME_LEN;m++)
   {           //BCD处理
		tmp=timeBuf[m+4]/16;
		sendTimeBuf[m]=timeBuf[m+4]%16;
		sendTimeBuf[m]=sendTimeBuf[m]+tmp*10;
		showTimeBuf[2*m]=timeBuf[m+4]/16;
	    showTimeBuf[2*m+1]=timeBuf[m+4]%16;
   }
  /* //待修正
	showTimeBuf[0]=timeBuf[4]/16;
	    showTimeBuf[1]=timeBuf[4]%16;

		showTimeBuf[2]=timeBuf[5]/16;
	    showTimeBuf[3]=timeBuf[5]%16;
			showTimeBuf[4]=timeBuf[6]/16;
	    showTimeBuf[5]=timeBuf[6]%16;
	*/
}


/*写SD2200状态寄存器命令*/
void I2CWriteStatus(void)
{		
	if(!I2CStart())return;
	I2CSendByte(0x60,1);      //发送SD2200状态寄存器_1命令
	if(!I2CWaitAck()){I2CStop();return;}   
//	I2CSendByte(0x03,0);      //IC进行复位初始化,24小时制
	I2CSendByte(0x02,0);      //IC不进行复位初始化,24小时制
	I2CWaitAck();
	I2CStop();        
	I2CStart();
	I2CSendByte(0x62,1);      //发送SD2200状态寄存器_2命令
	I2CWaitAck();   
	I2CSendByte(0x00,0);      //清TEST位,禁止中断输出
	I2CWaitAck();
	I2CStop();        
}

/*写SD2200时间寄存器命令*/
void I2CWriteTime(void)	 //7字节bcd year/month/day/week/hour/minite/second
{		
	uchar i,tmp;
	for(i=0;i<7;i++)
	    {                  //BCD处理
		tmp=initTimeBuf[i]/10;
		timeBuf[i]=initTimeBuf[i]%10;
		timeBuf[i]=timeBuf[i]+tmp*16;
	    }

	I2CStart();
	I2CSendByte(0x64,1);      //发送SD2200写时间寄存器命令，
	I2CWaitAck();   
        for(i=0;i<7;i++)
	    {
		I2CSendByte(timeBuf[i],0);
		I2CWaitAck();
	    }
	I2CStop();
	      
}
void dsWriteTime(void){
	
	I2CWriteTime();
}

void dsReadTime(void) {
	I2CReadDate();
}

void dsInit(void){
	I2CWriteStatus();
}

void resetInitTimeBuf(uchar timeArr[]){
	uchar m;
	for(m=0;m<SET_TIME_LEN;m++){
		initTimeBuf[m]=timeArr[m];
	}
	dsWriteTime();
}