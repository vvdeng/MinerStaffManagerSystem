#include "sd2200.h"
uchar xdata  initTimeBuf[SET_TIME_LEN]={13,10,10,40,10,10,10,0};//��������ʱ�����
uchar xdata timeBuf[SET_TIME_LEN];
uchar xdata sendTimeBuf[SEND_TIME_LEN] = {0};//��������ʱ�����
                     
uchar xdata showTimeBuf[SHOW_TIME_LEN]={0};   //��������ʱ�����


uchar xdata  date[7]; 

/*********I2C��ʱ***********/
void I2CWait(void)
{	
	_nop_();_nop_();_nop_();_nop_();
}


/********����SD2200��I2C����********/
bit I2CStart(void)
{
	SDA=1;
	SCL=1;
	I2CWait();
	if(!SDA)return 0;//SDA��Ϊ�͵�ƽ������æ,�˳�
	SDA=0;
	I2CWait();
	while(SDA)return 0;//SDA��Ϊ�ߵ�ƽ�����߳���,�˳�
	SCL=0;
	I2CWait();
	return 1;
}


/********�ر�SD2200��I2C����*******/
void I2CStop(void)
{
	SDA=0;
	SCL=0;
	I2CWait();
	SCL=1;
	I2CWait();
	SDA=1;
}


/*********���� ACK*********/
void I2CAck(void)
{	
	SDA=0;
	SCL=0;
	I2CWait();
	SCL=1;
	I2CWait();
	SCL=0;
}


/*********����NO ACK*********/
void I2CNoAck(void)
{	
	SDA=1;
	SCL=0;
	I2CWait();
	SCL=1;
	I2CWait();
	SCL=0;
}


/*********��ȡACK�ź�*********/
bit I2CWaitAck(void)  //����Ϊ:1=��ACK,0=��ACK
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


/************��SD2200����һ���ֽ�*************/
void I2CSendByte(uchar demand,bit order) //order=1,H_L;order=0,L_H
{
	uchar i=8;                       //order=1����HI-->LO����8bit����
	                                 //order=0����LO-->HI����8bit����
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


/*********MCU��SD2200����һ�ֽ�*********/
uchar I2CReceiveByte(void)
{
	uchar i=8;
	uchar ddata=0;
	SDA=1;
	while(i--)
	{
		ddata>>=1;          //���ݴӵ�λ��ʼ��ȡ
		SCL=0;
		I2CWait();
		SCL=1;
		I2CWait();	   //�ӵ�λ��ʼ ddata|=SDA;ddata>>=1
		if(SDA)
		{
			ddata|=0x80;
		}
	}
	SCL=0;
	return ddata;
}


/******��SD2200ʵʱ���ݼĴ���******/
void I2CReadDate(void)
{
	uchar m,tmp;
	if(!I2CStart())return;
	I2CSendByte(0x65,1);//���꿪ʼ��ȡ����
	if(!I2CWaitAck()){I2CStop();return;}
	for(m=0;m<7;m++)
	{
		timeBuf[m]=I2CReceiveByte();
		if (m!=6)         //���һ�����ݲ�Ӧ��
		{
			I2CAck();
		}
	}
	I2CNoAck();
	I2CStop();
	for(m=0;m<SEND_TIME_LEN;m++)
   {           //BCD����
		tmp=timeBuf[m+4]/16;
		sendTimeBuf[m]=timeBuf[m+4]%16;
		sendTimeBuf[m]=sendTimeBuf[m]+tmp*10;
		showTimeBuf[2*m]=timeBuf[m+4]/16;
	    showTimeBuf[2*m+1]=timeBuf[m+4]%16;
   }
  /* //������
	showTimeBuf[0]=timeBuf[4]/16;
	    showTimeBuf[1]=timeBuf[4]%16;

		showTimeBuf[2]=timeBuf[5]/16;
	    showTimeBuf[3]=timeBuf[5]%16;
			showTimeBuf[4]=timeBuf[6]/16;
	    showTimeBuf[5]=timeBuf[6]%16;
	*/
}


/*дSD2200״̬�Ĵ�������*/
void I2CWriteStatus(void)
{		
	if(!I2CStart())return;
	I2CSendByte(0x60,1);      //����SD2200״̬�Ĵ���_1����
	if(!I2CWaitAck()){I2CStop();return;}   
//	I2CSendByte(0x03,0);      //IC���и�λ��ʼ��,24Сʱ��
	I2CSendByte(0x02,0);      //IC�����и�λ��ʼ��,24Сʱ��
	I2CWaitAck();
	I2CStop();        
	I2CStart();
	I2CSendByte(0x62,1);      //����SD2200״̬�Ĵ���_2����
	I2CWaitAck();   
	I2CSendByte(0x00,0);      //��TESTλ,��ֹ�ж����
	I2CWaitAck();
	I2CStop();        
}

/*дSD2200ʱ��Ĵ�������*/
void I2CWriteTime(void)	 //7�ֽ�bcd year/month/day/week/hour/minite/second
{		
	uchar i,tmp;
	for(i=0;i<7;i++)
	    {                  //BCD����
		tmp=initTimeBuf[i]/10;
		timeBuf[i]=initTimeBuf[i]%10;
		timeBuf[i]=timeBuf[i]+tmp*16;
	    }

	I2CStart();
	I2CSendByte(0x64,1);      //����SD2200дʱ��Ĵ������
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