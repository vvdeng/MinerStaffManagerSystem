#include <reg52.h>
#include<intrins.h>
#include "useful.h"

#define FOSC 11059200L          //ϵͳƵ��
#define BAUD 9600             //���ڲ�����
sfr WDT_CONTR = 0xc1; //���Ź���ַ
sfr AUXR  = 0x8e;               //�����Ĵ���
sfr S2CON = 0x9a;               //UART2 ���ƼĴ���
sfr S2BUF = 0x9b;               //UART2 ���ݼĴ���
sfr T2H   = 0xd6;               //��ʱ��2��8λ
sfr T2L   = 0xd7;               //��ʱ��2��8λ
sfr   IE2     = 0xaf;  //�ж�����Ĵ���2 ���λΪES2  
sfr IPH    = 0xB7;   
sfr P3M1=0xb1;
sfr P3M0=0xb2;
#define S2RI  0x01              //S2CON.0
#define S2TI  0x02              //S2CON.1
#define S2RB8 0x04              //S2CON.2
#define S2TB8 0x08              //S2CON.3


uchar commState1,commState2,commBufUp,commBufDown;
//sbit RS_485_0=P3^6;
//sbit RS_485_2=P3^7;
sbit RS_485_2=P3^5;
//sbit ledTest0=P2^0;
//sbit ledTest1=P0^1;
sbit ledTest0=P2^0;
sbit ledTest1=P2^1;
sbit ledTest2=P3^3;
sbit ledTest3=P3^4;
#define COMM_STATE_NOTHING 0
#define COMM_STATE_REQ_STATE_WAIT_ADDR 1
#define COMM_STATE_REQ_STATE 2
#define CMD_REQ_STATE 0x80
#define CMD_SYNCH_TIME 0x81
#define CMD_BROADCAST 0x01
#define COMM_STATE2_NOTHING 0
#define COMM_STATE2_REQ_READER_STATE 1
#define COMM_STATE2_REQ_READER_STATE_STARTED 2 
#define READER_CARDNUM 10  //ÿ�����������10����ʶ����Ϣ
#define STATION_PORT_NUM 8 //ÿ����վ���8���˿�
#define CARD_INFO_BYTES 3  //ÿ����ʶ����Ϣ3���ֽ�

#define CMD_REQ_READER_STATE 0x80
#define SYM_BEGIN 0xf0
#define SYM_END 0xFE
#define COMM_UP_BUF_LEN 1004 //�·��������
#define  COMM_DOWN_BUF_LEN 1004 //�ϴ����ݻ�����
#define R_485 1
#define T_485 0 
void sendDataDown(uchar dat){
	RS_485_2=T_485;
	ES=0;
	SBUF=dat;	
	while(TI!=1);
 	TI=0; 
//	ledTest1=~ledTest1;
	ES=1;
	RS_485_2=R_485;
}
void sendDataUp(uchar dat){
//	RS_485_0=1;
	IE2 = 0x00;
	
	
	S2BUF=dat;
	while(!(S2CON&S2TI));
	S2CON&=~S2TI;
	IE2 = 0x01;
//	RS_485_0=0;
}
uchar cmdUpEndFlag=0;
uchar dataDownEndFlag=0;
uint commUpBufIndex=0;
uint commDownBufIndex=0;
uchar xdata commUpBuf[COMM_UP_BUF_LEN];
uchar xdata commDownBuf[COMM_DOWN_BUF_LEN];
void Uart() interrupt 4 using 1
{
//	uchar m,temp;
	if(RI)
	{	
		RI=0;
		if(dataDownEndFlag==0)
		{
			commDownBuf[commDownBufIndex++]=SBUF; 
			if(SBUF==SYM_END)
		    {
				
			  	dataDownEndFlag=1;
			}

		}
		
	}
	else if(TI){                       
		TI=0;
	}

}


void Uart2() interrupt 8 using 1
{
 //   uchar m;     
	if(S2CON&S2RI)
	{	
		S2CON&=~S2RI;
	//	 commBufUp=SBUF;
	//     sendDataDown(commBufUp);
	//	 ledTest0=~ledTest0;
		if(cmdUpEndFlag==0)
		{
		 	 commUpBuf[commUpBufIndex++]=S2BUF; 
			if(S2BUF==SYM_END)
		    {
			  	  cmdUpEndFlag=1;
			}
		
		}				
			             
	}
	else if(S2CON&S2TI)
	{
		
//	ledTest0=~ledTest0;
		S2CON&=~S2TI;
	//	RS_485_2=;
	
	}

}
void init() {
	P3M1=0x00;
	P3M0=0x20;
//	RS_485_0=0;
 	RS_485_2=R_485 ;
 	//��ʱ��0 ��ʽ1 ��50ms��
//	SBUF=0x0;
/*	TMOD |= 0x01;	  		     
	TH0=(65536-45872)/256;	      
	TL0=(65536-45872)%256;
	ET0=1;           
	TR0=1;   
*/
 	//����1 ��ʽ1 9600		  			
	SCON = 0x50;		//8λ����,�ɱ䲨����
	AUXR |= 0x40;		//��ʱ��1ʱ��ΪFosc,��1T
	AUXR &= 0xFE;		//����1ѡ��ʱ��1Ϊ�����ʷ�����
	TMOD &= 0x0F;		//�趨��ʱ��1Ϊ16λ�Զ���װ��ʽ
	TL1 = 0xE0;		//�趨��ʱ��ֵ
	TH1 = 0xFE;		//�趨��ʱ��ֵ
	ET1 = 0;		//��ֹ��ʱ��1�ж�
	TR1 = 1;
	ES  =1;
	//����2 9600bps@11.0592MHz
	S2CON = 0x50;		//8λ����,�ɱ䲨����
	AUXR |= 0x04;		//��ʱ��2ʱ��ΪFosc,��1T
	T2L = 0xE0;		//�趨��ʱ��ֵ
	T2H = 0xFE;		//�趨��ʱ��ֵ
	AUXR |= 0x10;		//������ʱ��2
	IE2 = 0x01;

 //	IPH=0x10;
//	IP=0x10;//���ô���1�ж����ȼ�Ϊ�{���ȼ�
	
	EA = 1;
	
 //   commState1=COMM_STATE_NOTHING;
 //   commState2=COMM_STATE2_NOTHING;
                                     
}
void main(){
	uint m;
	init();
	ledTest0=0;
	ledTest1=0;
	ledTest2=0;
	ledTest3=0;
	while(1){
//	sendDataDown(67);
//		delayMs(1000);
		if(cmdUpEndFlag==1){
			 	ledTest0=~ledTest0;
			for(m=0;m<commUpBufIndex;m++){
		
				sendDataDown(commUpBuf[m]);
			}
		/*	
			for(m=0;m<200;m++){
				  sendDataDown(65);
			}
	
		*/	
			commUpBufIndex=0;
			cmdUpEndFlag=0;
		}
		if(dataDownEndFlag==1){
			for(m=0;m<commDownBufIndex;m++){
				sendDataUp(commDownBuf[m]);
			}
			ledTest1=~ledTest1;
			commDownBufIndex=0;
			dataDownEndFlag=0;
		}
	}
}
