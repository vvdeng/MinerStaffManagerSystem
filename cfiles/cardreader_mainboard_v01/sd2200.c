#include "sd2200.h"
uchar xdata  initTimeBuf[SET_TIME_LEN];//空年月日时分秒周
uchar xdata  sendTimeBuf[SEND_TIME_LEN];
uchar xdata  showTimeBuf[SHOW_TIME_LEN];


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
	uchar n;
	if(!I2CStart())return;
	I2CSendByte(0x65,1);//从年开始读取数据
	if(!I2CWaitAck()){I2CStop();return;}
	for(n=0;n<7;n++)
	{
		date[n]=I2CReceiveByte();
		if (n!=6)         //最后一个数据不应答
		{
			I2CAck();
		}
	}
	I2CNoAck();
	I2CStop();
}


/*写SD2200状态寄存器命令*/
void I2CWriteStatus(void)
{		
	if(!I2CStart())return;
	I2CSendByte(0x60,1);      //发送SD2200状态寄存器_1命令
	if(!I2CWaitAck()){I2CStop();return;}   
	I2CSendByte(0x03,0);      //IC进行复位初始化,24小时制
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
void I2CWriteTime(void)
{		
        
	uchar n;
	date[0]=0x06;//year;2006/03/03/05/13/14/50
        date[1]=0x03;//month
        date[2]=0x03;//day
        date[3]=0x05;//week
        date[4]=0x13;//hour
        date[5]=0x14;//minute
        date[6]=0x50;//second	
	I2CStart();
	I2CSendByte(0x64,1);      //发送SD2200写时间寄存器命令，
	I2CWaitAck();   
        for(n=0;n<7;n++)
	    {
		I2CSendByte(date[n],0);
		I2CWaitAck();
	    }
	I2CStop();
	      
}
void dsWriteTime(void){
}

void dsReadTime(void) {
}

void dsInit(void){
}

void resetInitTimeBuf(uchar timeArr[]){
}