#include <reg52.h>
#include<intrins.h>
#include "useful.h"
#include "vvspi.h"
#include "sd2200.h"
#include "display.h"
#include "constant.h"
#include "pm25.h"
#include "config.h"


#define  _Nop()  _nop_()


#define STATION_PORT_NUM 4 //ÿ����վ���4���˿�
#define CARD_INFO_BYTES 3  //ÿ����ʶ����Ϣ3���ֽ�
#define POLL_COUNT 4  //����POLLCOUNT��Ѳ�����ڵ�����
#define MAX_CALL_STAFF_COUNT 80
#define STAFF_BUF_LEN (MAX_CALL_STAFF_COUNT*2+1) //����ʱÿ��id�����ֽ� ��0�ֽڱ�������

#define SPI_START 0x80
#define SYM_END 0xFE
#define SEG_COUNT 8    

#define READER_BUF_LEN (READER_CARDNUM*CARD_INFO_BYTES+6) //��0��2�ֽ�Ϊ�������ɼ�ʱ��ʱ�����1�ֽڣ���3�ֽ�Ϊ��ʶ��������������־���壨���糬ʱ�� ����4�ֽ�Ϊ��ʶ��ʵ�����������һλΪ��ӵķָ����� ����Ҫ��6
#define SET_TIME_BUF_LEN 8
          
#define   S2RI    0x01                 
#define   S2TI    0x02    

#define RS485_R 1
#define RS485_T 0      
               





//ָʾ��
//sbit LED_TXD1 =P1^0;
//sbit LED_RXD1 =P1^1;
//sbit LED_TXD2 =P3^3;
//sbit LED_RXD2 =P3^4;
//sbit k0=P3^5;

//����1 485��д����
/*
sbit RS_485_1=P3^6;
sbit RS_485_2=P3^7;
sbit SEL_A=P1^4;
sbit SEL_B=P1^5;
sbit SEL_C=P1^6;
sbit SEL_D=P1^7;
sbit LED_TEST1=P3^4;
sbit LED_TEST2=P3^3;
sbit LED_TEST3=P1^0;
sbit LED_TEST4=P1^1;
*/
sfr P3M1=0xb1;
sfr P3M0=0xb2;
sbit RS_485_1=P3^6;
sbit RS_485_2=P3^7;

sbit ledTest1=P3^4;
sbit ledTest2=P3^5;
sbit ledTest3=P4^3;
//sbit LED_TEST4=P5^4;

//sbit LED_TEST2=P2^7;







uchar localAddress,commState1,commState2,currentReaderIndex=0,commTimeoutRetryCount,pollCount=0,staffBufIndex=0;
uint currentBufferIndex=0,commTimeoutCount=0,commFinishedTimeCount=0,setTimeBufIndex=0;
uchar  xdata cardsInfoArr[POLL_COUNT][STATION_PORT_NUM][READER_BUF_LEN]={0};
uchar xdata cardPerReaderNumArr[STATION_PORT_NUM]={0};
uchar xdata setTimeBuf[SET_TIME_BUF_LEN]={0};
uchar xdata staffBuf[STAFF_BUF_LEN]={0};
bit statesSentFlag=0,commTimeoutFlag=0,commFinishedFlag=0,totalRefreshedFlag=1,commTimeoutActionFlag=0,commFinishedActionFlag=0,setTimeFlag=0,staffFlag=0;
uchar xdata exramArr[4000];

void sendDataUp(uchar dat){
	RS_485_1=RS485_T;
	SBUF=dat;
	while(!TI);
	TI=0;
	RS_485_1=RS485_R;
}

void sendDataDown2(uchar dat){
	S2BUF=dat;
	while(!(S2CON&S2TI));
	S2CON&=~S2TI;
}

/*void sendCmdAndAddr(uchar addr,uchar cmd){

	RS_485_2=1;

	
	S2BUF=cmd;
	while(!(S2CON&S2TI));
	S2CON&=~S2TI;
	
	RS_485_2=0;
}

void sendDataInstance(uchar ldata,uchar pre){
	LED_TXD2=~LED_TXD2;
	RS_485_2=1;
		
	S2BUF=((ldata>>4)&0x0f)|pre;
	while(!(S2CON&S2TI));
	S2CON&=~S2TI;

	S2BUF=(ldata&0x0f)|pre;
	while(!(S2CON&S2TI));
	S2CON&=~S2TI;
	
	RS_485_2=0;
	
	
}
sendDataWithoutSplitInstance(uchar ldata){
		LED_TXD2=~LED_TXD2;
	RS_485_2=1;
		
	S2BUF=ldata;
	while(!(S2CON&S2TI));
	S2CON&=~S2TI;

	
	RS_485_2=0;
}
void sendCmdInstance(uchar ldata){
	sendDataWithoutSplitInstance(ldata);	
}

*/









#define COMM_STATE_NOTHING 0
#define COMM_STATE_REQ_STATE_WAIT_ADDR 1
#define COMM_STATE_REQ_STATE 2
#define COMM_STATE_SYNCH_TIME 3
#define COMM_STATE_SYNCH_TIME_READY 4
#define COMM_STATE_CALL_STAFF 5
#define COMM_STATE_CALL_STAFF_READY 6
#define COMM_STATE_REQ_STATE_WAIT_ADDR_END 7 //����ӿڴ����ַλ����SYS_END�������SYS_END����ܷ�������
#define COMM_STATE_REQ_TIMEOUT_CARD_STATION_ADDR 8
#define COMM_STATE_REQ_TIMEOUT_CARD_ADDR 9
#define COMM_STATE_REQ_TIMEOUT_CARD_ADDR_END 10
#define COMM_STATE_REQ_TIMEOUT_CARD 11
#define CMD_REQ_STATE 0x80
#define CMD_SYNCH_TIME 0x81
#define CMD_CALL_STAFF 0x84
#define CMD_REQ_TIMEOUT_CARD 0x85
#define CMD_REQ_TIMEOUT_STATION 0x88
#define CMD_BROADCAST 0x01

#define COMM_STATE2_NOTHING 0
#define COMM_STATE2_REQ_READER_STATE 1
#define COMM_STATE2_REQ_READER_STATE_STARTED 2 
#define COMM_STATE2_REQ_TIMEOUT_CARD 3
#define CMD_REQ_READER_STATE 0x80
#define SYM_BEGIN 0xF0
#define SYM_END 0xFE
#define DATA_END 0xFE
#define SYM_DELIMITER 0xFD
#define SYS_TIMEOUT 0x60 //SYS_TIMEOUT�������80С��128 80���±�ʾ�ɼ�����
uchar tempReaderNo=0;
void ssio(void)
interrupt 4
{
          
	if(RI){
         RI=0;
/*			
	//	if(commState1==COMM_STATE_NOTHING&&commState2==COMM_STATE2_NOTHING){
		if(commState1==COMM_STATE_NOTHING){
		 	if(SBUF==CMD_REQ_STATE){
				commState1=COMM_STATE_REQ_STATE_WAIT_ADDR;
				
			}else if(SBUF==CMD_SYNCH_TIME){
			 
				commState1=COMM_STATE_SYNCH_TIME;
			}
			else if(SBUF==CMD_CALL_STAFF){
				commState1=COMM_STATE_CALL_STAFF;
			}
			else if(SBUF==CMD_REQ_TIMEOUT_CARD){
				commState1=COMM_STATE_REQ_TIMEOUT_CARD_STATION_ADDR;
			}
		
		}
*/		
		if((SBUF&0x82)==0x80){
			
		 	if(SBUF==CMD_REQ_STATE){
				commState1=COMM_STATE_REQ_STATE_WAIT_ADDR;
				
			}else if(SBUF==CMD_SYNCH_TIME){
			 
				commState1=COMM_STATE_SYNCH_TIME;
			}
			else if(SBUF==CMD_CALL_STAFF){
				commState1=COMM_STATE_CALL_STAFF;
			}
			else if(SBUF==CMD_REQ_TIMEOUT_CARD){
				commState1=COMM_STATE_REQ_TIMEOUT_CARD_STATION_ADDR;
			}
		
		}

		else if((commState1==COMM_STATE_REQ_STATE_WAIT_ADDR))
		{
			if(SBUF==localAddress){
				commState1=COMM_STATE_REQ_STATE_WAIT_ADDR_END;
				
			}else{
				commState1=COMM_STATE_NOTHING;
			}

		}
		else if(commState1==COMM_STATE_REQ_STATE_WAIT_ADDR_END){
			if(SBUF==SYM_END){
				commState1=COMM_STATE_REQ_STATE;
			//	LED_TEST3=~LED_TEST3;
			}
			else{
				commState1=COMM_STATE_NOTHING;
			}

		}
		else if(commState1==COMM_STATE_SYNCH_TIME){
			if(SBUF==CMD_BROADCAST){
				commState1=COMM_STATE_SYNCH_TIME_READY;
		//		ledTest3=~ledTest3;
				setTimeBufIndex=0;
			}
		}
		else if(commState1==COMM_STATE_SYNCH_TIME_READY){
			if(SBUF==SYM_END||setTimeBufIndex>=SET_TIME_BUF_LEN){
				setTimeFlag=1;
			}
			else{
				setTimeBuf[setTimeBufIndex++]=SBUF;	
			}
		}
		else if(commState1==COMM_STATE_CALL_STAFF){
			if(SBUF==CMD_BROADCAST){
				commState1=COMM_STATE_CALL_STAFF_READY;
				staffBufIndex=0;
			}
		}
		else if(commState1==COMM_STATE_CALL_STAFF_READY){
			if(SBUF==SYM_END||staffBufIndex>=STAFF_BUF_LEN){
				staffFlag=1;
			}
			else{
				staffBuf[staffBufIndex++]=SBUF;	
			}
		}
		else if(commState1==COMM_STATE_REQ_TIMEOUT_CARD_STATION_ADDR){
			if(SBUF==1)//�����ã�Ӳ�����վ��ַ
			{
				commState1=COMM_STATE_REQ_TIMEOUT_CARD_ADDR;
			}
		}
		else if(commState1==COMM_STATE_REQ_TIMEOUT_CARD_ADDR){
			 	tempReaderNo=SBUF;
				commState1=COMM_STATE_REQ_TIMEOUT_CARD_ADDR_END;
	
		}
		else if(commState1==COMM_STATE_REQ_TIMEOUT_CARD_ADDR_END){
			if(SBUF==SYM_END){
				RS_485_2=RS485_T;
				IE2=0;
				sendDataDown2(CMD_REQ_TIMEOUT_CARD);
			//	sendDataDown2(1); //�����ã�Ӳ�����վ��ַ
				sendDataDown2(1);//���Ͷ�������� Ӧ��ΪtempReaderNo
			//	sendDataDown2(SYM_END);
				IE2=1;
				RS_485_2=RS485_R;
				commState1=COMM_STATE_NOTHING;
				commState2=COMM_STATE2_REQ_TIMEOUT_CARD;
			}
			else{
				 commState1=COMM_STATE_NOTHING;
				 commState2=COMM_STATE2_NOTHING;
			}
		}
               
	}
	else if(TI){                       
		TI=0;
	}

}
uchar preData=0,curData=0; 
void S2INT() interrupt 8
{
	uchar temp;		
	if(S2CON&S2RI)
	{	
	
		S2CON&=~S2RI;
		if(commState1!=COMM_STATE_NOTHING){
			commState2=COMM_STATE2_NOTHING; //����1��������ʱ����2ֹͣ ���Կ����߼�����Ϊֻ���ڴ���1 �ɼ���Ա��Ϣʱ ����2��ֹͣ
			return;
		}
		temp=S2BUF;
		if(commState2==COMM_STATE2_REQ_READER_STATE)
		{
			if(S2BUF==SYM_BEGIN)
			{
				commTimeoutFlag=1;
				commState2=COMM_STATE2_REQ_READER_STATE_STARTED;
			}
			
		}
        else if(commState2==COMM_STATE2_REQ_READER_STATE_STARTED){
			if((S2BUF==SYM_END)||(currentBufferIndex>=READER_BUF_LEN-1)){
				commState2=COMM_STATE2_NOTHING;
				cardsInfoArr[pollCount][currentReaderIndex][currentBufferIndex++]=SYM_DELIMITER;
				commFinishedFlag=1;
				commFinishedActionFlag=1;
				
				
			}
		//	preData=curData;
		//	curData=S2BUF;
			else{
				cardsInfoArr[pollCount][currentReaderIndex][currentBufferIndex++]=S2BUF;
			}
		}
		 else if(commState2==COMM_STATE2_REQ_TIMEOUT_CARD){
		 	sendDataUp(S2BUF);
			if(S2BUF==SYM_END){
				commState1=COMM_STATE_NOTHING;
				commState2=COMM_STATE2_NOTHING;
				
				
				
			}
		
		}
	
		
	}
	if(S2CON&S2TI)
	{
		
	
	
		S2CON&=~S2TI;
		RS_485_2=RS485_R;

	}

}
/*
char * itostr(uchar num,char * str, uchar strLen){
	uchar m;
 	for(m=0;m<strLen;m++){
		str[m]='0';
 	}
	while (num!=0){
	    str[--m]='0'+(num%10);
		num/=10;
	}
	str[strLen]='\0';
 	return str;
}
*/



void EX0_ISR (void) interrupt 0 //�ⲿ�ж�0������
{
	
	irExProcess();
}

void init() {
	
	P3M1=0x00;
	P3M0=0xC0;
	//������չ�ڴ棬���ڵ�һ��
	localAddress=1; //�����ã���ַӲ����

//	init_spi();
//	dsInit();
//	dsWriteTime();
	RS_485_1=RS485_R ;
 	RS_485_2=RS485_T ;
/* 	//��ʱ��0 ��ʽ1 ��50ms��
//	SBUF=0x0;
	TMOD |= 0x01;	  		     
	TH0=(65536-45872)/256;	      
	TL0=(65536-45872)%256;
	ET0=1;           
	TR0=1;   
*/
 	//����1 ��ʽ1 9600		  			
 	//����1 ��ʽ1 9600		  			
	SCON = 0x50;		//8λ����,�ɱ䲨����
	AUXR |= 0x40;		//��ʱ��1ʱ��ΪFosc,��1T
	AUXR &= 0xFE;		//����1ѡ��ʱ��1Ϊ�����ʷ�����
	TMOD &= 0x0F;		//�趨��ʱ��1Ϊ16λ�Զ���װ��ʽ
	TL1 = 0xE0;		//�趨��ʱ��ֵ
	TH1 = 0xFE;		//�趨��ʱ��ֵ
	ET1 = 0;		//��ֹ��ʱ��1�ж�
	TR1 = 1;


	//����2 9600bps@11.0592MHz
	S2CON = 0x50;		//8λ����,�ɱ䲨����
	AUXR |= 0x04;		//��ʱ��2ʱ��ΪFosc,��1T
	T2L = 0xE0;		//�趨��ʱ��ֵ
	T2H = 0xFE;		//�趨��ʱ��ֵ
	AUXR |= 0x10;		//������ʱ��2
	IE2 = 0x01;
	
 //	irInit();//����չ��
	AUXR &= 0x7F;		//��ʱ��ʱ��12Tģʽ
	TMOD &= 0xF0;		//���ö�ʱ��ģʽ
	TMOD |= 0x01;		//���ö�ʱ��ģʽ
	TL0 = 0xD7;		//���ö�ʱ��ֵ   16λ 600us ���1%
	TH0 = 0xFD;		//���ö�ʱ��ֵ
	TF0 = 0;		//���TF0��־
	ET0=1;
	TR0=1;

 	IT0 = 1;   //ָ���ⲿ�ж�0�½��ش�����INT0 (P3.2)
 	EX0 = 1;   //ʹ���ⲿ�ж�
 	retreiveConfig();
	
	

 	IPH=0x10;
	IP=0x10;//���ô���1�ж����ȼ�Ϊ�{���ȼ�
	ES = 1;
	EA = 1;
	init_spi();
    commState1=COMM_STATE_NOTHING;
    commState2=COMM_STATE2_NOTHING;
	
                                     
}
#define COMM_NOTREADY 0x01
#define COMM_OK 0x02
void sendCardsInfo(){
	uint k,m,n;
//	LED_TEST=~LED_TEST;
//	LED_TEST1=1;
	ES=0;
	RS_485_1=RS485_T;
	//����ʱ��
	dsReadTime();		
	for(m=0;m<SEND_TIME_LEN;m++){
		SBUF=sendTimeBuf[m];
	 	while(TI!=1);
	 	TI=0; 
	} 
	if(pollCount!=1){ //����pollCount����Ϊ�ж��Ƿ�ɼ���ɺͿ��Բɼ��ı�־
	 	SBUF=COMM_NOTREADY;	
		while(TI!=1);
	 	TI=0;
	}
	else{
	 	SBUF=COMM_OK;	
		while(TI!=1);
	 	TI=0;
		for(k=0;k<pollCount;k++)
		{
		/*	//ģ����ѯ�ɼ�ʱ��ʱ��
		 	SBUF=(0x40|k);	
			while(TI!=1);
		 	TI=0;
		*/
		
			for(m=0;m<STATION_PORT_NUM;m++){
		    	//���Ͷ˿ںţ���1��ʼ
			 	SBUF=m+1;	
			 	while(TI!=1);
			 	TI=0; 
				for(n=0;n<READER_BUF_LEN;n++) 
				{   
					if(cardsInfoArr[k][m][n]==SYM_DELIMITER){
						break;
					}
					SBUF=cardsInfoArr[k][m][n];	
			    	while(TI!=1);
			    	TI=0; 
			 	}
				//��ֹcardsInfoArr��ʧSYM_DELIMITER�ֽڣ������ڱ������ͳһ����
				SBUF=SYM_DELIMITER;	
			    while(TI!=1);
			    TI=0; 
	
			}
		}
	}
	//���ͽ�����
	 SBUF=SYM_END;	
	 while(TI!=1);
	 TI=0; 
	 pollCount=0;//��ֵ��ѯ��ʼ����
	 RS_485_1=RS485_R;
	 ES=1;
}
void commAction(){
	
	if((commState1==COMM_STATE_REQ_STATE)/*&&totalRefreshedFlag==1*/){

		sendCardsInfo();
		commState1=COMM_STATE_NOTHING;
		statesSentFlag=1;//Ϊ1 ��ʼѲ��������ռ�������Ϣ
	}
}
void setCurrentReaderTimeout(){
	
	cardsInfoArr[pollCount][currentReaderIndex][currentBufferIndex++]=SYS_TIMEOUT;//SYS_TIMEOUT�������80С��128 80���±�ʾ�ɼ�����
	cardsInfoArr[pollCount][currentReaderIndex][currentBufferIndex++]=SYM_DELIMITER;
	
}
void endGetReaderInfo(){
   commState2=COMM_STATE2_NOTHING;
   totalRefreshedFlag=1;
   pollCount++; //����pollCount����Ϊ�ж��Ƿ�ɼ���ɺͿ��Բɼ��ı�־
}
void getCurrentReaderInfo(){


	currentBufferIndex=0;
	commTimeoutCount=0;
	commFinishedTimeCount=0;
	commTimeoutFlag=0;
	commFinishedFlag=0;
//	sendCmdAndAddr(currentReaderIndex,CMD_REQ_READER_STATE);

	
	TR0=0;
	IE2 = 0x00;
	RS_485_2=RS485_T;
		
	
	S2BUF=CMD_REQ_READER_STATE;
	while(!(S2CON&S2TI));
	S2CON&=~S2TI;
	
	S2BUF=(currentReaderIndex+1);
	while(!(S2CON&S2TI));
	S2CON&=~S2TI;

	RS_485_2=RS485_R;

	IE2 = 0x01;
	//��������֮��������״̬
	commState2=COMM_STATE2_REQ_READER_STATE;
	TH0=(65536-45872)/256;	      
 	TL0=(65536-45872)%256;
	TR0=1;
		
//	LED_TEST2=~LED_TEST2;
}
void getNextReaderInfo(){

	commTimeoutRetryCount=0;
	currentReaderIndex++;
	if(currentReaderIndex<STATION_PORT_NUM)
	{
		getCurrentReaderInfo();
	}
	else{
		endGetReaderInfo();
	}
}
void startGetReaderInfo(){
	
	totalRefreshedFlag=0;    
	currentReaderIndex=0;
//	LED_TEST3=~LED_TEST3;
	getCurrentReaderInfo();
}
uint inteval2s=0;
//��ʱ��1 ����
void Timer0_isr(void) interrupt 1 using 1
{
	//50ms

//	TH0=(65536-45872)/256;	      
// 	TL0=(65536-45872)%256;
	TL0 = 0xD7;		//���ö�ʱ��ֵ   16λ 600us ���1%
	TH0 = 0xFD;		//���ö�ʱ��ֵ
	irTime0Process();	
	if(commState2==COMM_STATE2_REQ_READER_STATE){
		
		if(commTimeoutFlag==0&&commTimeoutActionFlag==0)
		{
			
			commTimeoutCount++;
//			if(commTimeoutCount>=4){
			if(commTimeoutCount>=350){	
			    //��ʱ��ͨ�Ž������ٴο�ʼͨ�Ź��̣�ֻ���ڷ�������״̬����󣬲ŲŸ���commState2=COMM_STATE2_REQ_READER_STATE
			    commState2=COMM_STATE2_NOTHING;
			    commTimeoutCount=0;
		  	    commTimeoutRetryCount++;
		
			    if(commTimeoutRetryCount<=4){
					
				    getCurrentReaderInfo();
			    }else{
				
				    commTimeoutActionFlag=1;
//					LED_TEST4=~LED_TEST4;
			    }
						
			
			}
		}
		else if(commFinishedFlag==0){
			commFinishedTimeCount++;
//			if(commFinishedTimeCount>=20){ //20*50ms��������δ�������
			if(commFinishedTimeCount>=1667){ //1667*0.6ms��������δ�������
				commState2=COMM_STATE2_NOTHING;
				commFinishedTimeCount=0;
				//�ݲ��������ԣ�����Ϊ����
				setCurrentReaderTimeout();
				getNextReaderInfo();
			}
		}
	}
	else
	{
		if(totalRefreshedFlag==1){
			inteval2s++;
//			if(inteval2s>=60){//3s
			if(inteval2s>=10000){//0.6*10000=6s
		//		statesSentFlag=1;//Ϊ1 ��ʼѲ��������ռ�������Ϣ				  ��վ�����ж��Ѳ��
			}
		}
	}

}
void processReceivedData(){

}
void testInit(){
	uint k,m,n;
	for(k=0;k<POLL_COUNT;k++)
	{
		for(m=0;m<STATION_PORT_NUM;m++){
			for(n=0;n<READER_BUF_LEN;n++)
			{
				cardsInfoArr[k][m][n]=0x00;
			}
		}
   }
}
void synTime(){

}
void pm25Test(){
	g_fFlashOK=0;
	FlashCheckID();
	if(g_fFlashOK==1){
	  ledTest1=1;
	}
	else{
	  ledTest1=0;
	}
	{
		uchar a=0x12345678,b=0;
		FlashWrite(0x00,4,&a);
		FlashRead(0x00,4,&b);
		if(a==b){
		  ledTest2=1;
		}
		else{
		  ledTest2=0;
		}
	}
	while(1);
}

void main() {

	uchar m,n;
//	uint i;
	AUXR |= 0x02; //stc��Ƭ����չ�ڴ�
/*	for(i=0;i<1000;i++){
		exramArr[i]=5;
	}
		for(i=1000;i<2000;i++){
		exramArr[i]=6;
	}
		for(i=2000;i<3000;i++){
		exramArr[i]=7;
	}
		for(i=3000;i<4000;i++){
		exramArr[i]=8;
	}
*/	testInit();
	init();
	dsInit();
	ledTest1=0;
	ledTest2=0;
	ledTest3=0;
//	pm25Test();
	
 /*
	dsWriteTime();	
	while(1){
	 delayMs(1000);
	 dsReadTime();
	 		CS_DISP=0;
		WriteByte(SPI_START);		
		for(m=0;m<SHOW_TIME_LEN;m++){
			WriteByte(showTimeBuf[m]);
			delayUs(5);
		}
		for(m=0; m<(128-SHOW_TIME_LEN);m++){
			WriteByte(m);
			delayUs(5);
		}					
			
		WriteByte(SYM_END);
		CS_DISP=1;
	}

  */
	WDT_CONTR = 0x3f;//���ÿ��Ź������ʱ��9.1022s
	while (1) {
		
	/*   delayMs(2000);
	  	dsReadTime();
			initDisplayBuf();
			
			for(m=0;m<SHOW_TIME_LEN;m++){
				displayBuf[m]=showTimeBuf[m];
			}
			
			sendDisplay();
	*/
		processSettingIfNecessary();
		commAction();
		if(statesSentFlag==1){
			
			statesSentFlag=0;
 		//  inteval2s=0;
			if(pollCount==0){
				startGetReaderInfo();
			}
		//	if(pollCount<POLL_COUNT){
		//		startGetReaderInfo();
		//	}
			
		}
		if(commTimeoutActionFlag==1){
			
			setCurrentReaderTimeout();
				getNextReaderInfo();
			commTimeoutActionFlag=0;
		}
		if(commFinishedActionFlag==1){

			commFinishedActionFlag=0;
			processReceivedData();
			getNextReaderInfo();
		}
/*		if(setTimeFlag==1){
			
			for(i=0;i<100;i++){
			  sendDataUp(exramArr[i]);
			}
	//		sendDataUp(SYM_END);
				for(i=1000;i<1100;i++){
			  sendDataUp(exramArr[i]);
			}
	//		sendDataUp(SYM_END);
				for(i=2000;i<2100;i++){
			  sendDataUp(exramArr[i]);
			}
	//		sendDataUp(SYM_END);
				for(i=3000;i<3100;i++){
			  sendDataUp(exramArr[i]);
			}
			sendDataUp(SYM_END);
			setTimeFlag=0;
		}
*/		if(setTimeFlag==1){
	//		LED_TEST2=~LED_TEST2;
			RS_485_2=RS485_T ;
			IE2 = 0x00; 
			
			{
			
				sendDataDown2(CMD_SYNCH_TIME);
				sendDataDown2(CMD_BROADCAST);
				for(n=0;n<SET_TIME_BUF_LEN;n++){
					sendDataDown2(setTimeBuf[n]);
				}
				sendDataDown2(SYM_END);
		    }
			IE2 = 0x01; 
			RS_485_2=RS485_R ;
		//	synTime();
			resetInitTimeBuf(setTimeBuf);
			dsReadTime();
			initDisplayBuf();
			
			for(m=0;m<SHOW_TIME_LEN;m++){
				displayBuf[m]=showTimeBuf[m];
			}
			
			sendDisplay();
			commState1=COMM_STATE_NOTHING;
		    setTimeFlag=0;
		}

		if(staffFlag==1){
	//		LED_TEST2=~LED_TEST2;
			RS_485_2=RS485_T ;
			IE2 = 0x00; 
	//		staffBuf[0]=(staffBuf[0]*2)<STAFF_BUF_LEN?staffBuf[0]:0;//���������*2���ڻ�����������Ϊ�������
			
			{
				
				sendDataDown2(CMD_CALL_STAFF);
				sendDataDown2(CMD_BROADCAST);
				for(n=0;n<=staffBuf[0]*2;n++){
					sendDataDown2(staffBuf[n]);
				}
				sendDataDown2(SYM_END);
		    }
			IE2 = 0x01; 
			RS_485_2=RS485_R ;
			
			commState1=COMM_STATE_NOTHING;
		   	staffFlag=0;
		}

	//	LED_TEST2=0;
		WDT_CONTR = 0x3f;//���ÿ��Ź������ʱ��9.1022s
		
		
	}

}



