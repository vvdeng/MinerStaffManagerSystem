#include <reg52.h>
#include "vvspi.h"
#include "useful.h"
#include "DS1302.h"
#include "display.h"
#include "config.h"
#include "constant.h"
sbit ledTest2=P3^5;
sbit ledTest1=P3^4;
sbit RS_485=P3^6; 
uint setTimeBufIndex=0;
uchar  xdata callStaffBuf[CALL_STAFF_BUF_LEN]={0};
bit callStaffFlag=0; //测试时设置为1
bit queryCardsFlag=1;
bit setTimeFlag=0;
bit upTimeoutFlag=0;
bit staDisplayFlag=0;
uint upTimeoutCount=0;
uint displayCardId=0,displayCardIndex=0;
uchar oldArrIndex=0,arrIndex=0;
bit arrOk=0,arr0or1=0;//0:queryCardsArrA[0]正在接收，queryCardsArrA[1]可上传 1:queryCardsArrA[1]正在接收，queryCardsArrA[0]可上传
uchar xdata queryCardsArrA[2][CARDS_ARR_LEN+2]={0};//第0字节存储包含呼叫和欠压的标识卡总数 第1字节存储实际上识别到的标识卡数量

uchar xdata setTimeBuf[SET_TIME_BUF_LEN]={0};
uchar queryCardsLen=0;queryCardIndex=0,callStaffBufIndex=0;

uchar commState1;commState2;
void resetQueryCardArr(uchar aIndex);
void sendCardsInfo();

void sendDataUp(uchar dat){
	RS_485=1;
	ES=0;
	SBUF=dat;	
	while(TI!=1);
 	TI=0; 
//	ledTest1=~ledTest1;
	ES=1;
	RS_485=0;
}
void sendDataDown(uchar dat){

	IE2 = 0x00;
	
	
	S2BUF=dat;
	while(!(S2CON&S2TI));
	S2CON&=~S2TI;
	IE2 = 0x01;
	
}
void testInit(){
	uchar m;
/*	callStaffBuf[0]=6;
	for (m=1;m<=6;m++){
		callStaffBuf[2*m-1]=m/128;
		callStaffBuf[2*m]=m%128;
	}
*/
	for (m=0;m<CALL_STAFF_BUF_LEN;m++){
		callStaffBuf[m]=0;
	}
}
#define SYM_BEGIN 0xF0
#define SYM_END 0xFE  //0x1111 1110 为方便串口处理 区分结束符和命令，约定格式为0x1xxx xx0x 
#define DATA_END 0xFF
#define COMM_STATE_NOTHING 0
#define COMM_STATE_REQ_STATE_WAIT_ADDR 1
#define COMM_STATE_REQ_STATE 2
#define COMM_STATE_SYNCH_TIME 3
#define COMM_STATE_SYNCH_TIME_READY 4
#define COMM_STATE_CALL_STAFF 5
#define COMM_STATE_CALL_STAFF_READY 6
#define COMM_STATE_TIMEOUT_REQ_STATE 7
#define CMD_REQ_STATE 0x80  //0x1000 000
#define CMD_SYNCH_TIME 0x81
#define CMD_CALL_STAFF 0x82
#define CMD_BROADCAST 0x01
#define COMM_STATE2_NOTHING 0
#define COMM_STATE2_REQ_READER_STATE 1
#define COMM_STATE2_REQ_READER_STATE_STARTED 2 

#define COMM_STATE2_NOTHING 0
#define COMM_STATE2_RECV_CARDS 1
#define COMM_STATE2_RECV_FINISHED 2
#define CMD_REQ_CARDS 0xFE

void ssio(void)
interrupt 4
{  
	if(RI){
		
         RI=0;
		 
/*		if(commState1==COMM_STATE_NOTHING){ 
		 	if(SBUF==CMD_REQ_STATE){
				
				commState1=COMM_STATE_REQ_STATE_WAIT_ADDR;
			
	         //   LED_TST3=~LED_TST3;			
			}
			else if(SBUF==CMD_SYNCH_TIME){
				
				commState1=COMM_STATE_SYNCH_TIME;
			}
			else if(SBUF==CMD_CALL_STAFF){
				commState1=COMM_STATE_CALL_STAFF;
			}
		}
	*/	
		if((SBUF&0x82)==0x80){ //结束符为0x1111 1110 为方便串口处理 区分结束符和命令，约定格式为0x1xxx xx0x 
		 	if(SBUF==CMD_REQ_STATE){
				
				commState1=COMM_STATE_REQ_STATE_WAIT_ADDR;
				upTimeoutCount=0;
			
	         //   LED_TST3=~LED_TST3;			
			}
			else if(SBUF==CMD_SYNCH_TIME){
				
				commState1=COMM_STATE_SYNCH_TIME;
			}
			else if(SBUF==CMD_CALL_STAFF){
				commState1=COMM_STATE_CALL_STAFF;
			}
			return;
		}
	 
	  

		 if(commState1==COMM_STATE_REQ_STATE_WAIT_ADDR){
				if((configArr[CONFIG_ADDR]-1)==SBUF){ //分站巡检时地址从0开始，实际地址码从1开始
				
				ledTest1=~ledTest1;	
					commState1=COMM_STATE_REQ_STATE;
				//	sendDataDown(CMD_REQ_CARDS);
				}
				else{
					commState1=COMM_STATE_NOTHING;
				}
		}
		else if(commState1==COMM_STATE_SYNCH_TIME){
			if(SBUF==CMD_BROADCAST){
				commState1=COMM_STATE_SYNCH_TIME_READY;
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
				callStaffBufIndex=0;
			}
		}
		else if(commState1==COMM_STATE_CALL_STAFF_READY){
			if(SBUF==SYM_END||callStaffBufIndex>=CALL_STAFF_BUF_LEN){
				callStaffFlag=1;
			}
			else{
				callStaffBuf[callStaffBufIndex++]=SBUF;	
			}
		}
               
	}
	else if(TI){                       
		TI=0;
	}

}


void S2INT() interrupt 8
{	
	if(S2CON&S2RI)
	{	
		
		S2CON&=~S2RI;
	//	if(commState1==COMM_STATE_NOTHING){
	//	if(commState1==COMM_STATE_REQ_STATE||commState1==COMM_STATE_TIMEOUT_REQ_STATE){

			if(commState2==COMM_STATE2_NOTHING){
				if(S2BUF==SYM_BEGIN){
					
					commState2=COMM_STATE2_RECV_CARDS;
					queryCardIndex=0;
				}
				else{
					//应采取其他措施
					commState2=COMM_STATE2_NOTHING;
				}
			}
			else if(commState2==COMM_STATE2_RECV_CARDS){
				if(S2BUF==SYM_END)
				{
				//	commState2=COMM_STATE2_RECV_FINISHED;
							  
				  /* if(commState1==COMM_STATE_REQ_STATE){
						
						sendCardsInfo();
					
					}else{
						
					}
				  */
				 // 	beginGetCardsInfoFlag=0;	改为显示后才设置为0，做测试
				 	oldArrIndex=arrIndex;
					arrIndex=(arrIndex+1)%2;
					resetQueryCardArr(arrIndex);
					arrOk=1;
					staDisplayFlag=1;
				//	commState1=COMM_STATE_NOTHING;
					commState2=COMM_STATE2_NOTHING;
				}
				else{
					queryCardsArrA[arrIndex][queryCardIndex++]=S2BUF;
				}
			}
		
	//	}
	
		
	}
	if(S2CON&S2TI)
	{
		
	
	
		S2CON&=~S2TI;
	

	}

}
#define CARDS_INFO_TIME_THRESHOLD 7000   //4.2s 
uint getCardsInfoTimeCount=0;
bit beginGetCardsInfoFlag=0;
void tim0_isr (void) interrupt 1 using 1
{
	TL0 = 0xD7;		//设置定时初值   16位 600us 误差1%
	TH0 = 0xFD;		//设置定时初值
	if(beginGetCardsInfoFlag==0){
		if(getCardsInfoTimeCount++>=CARDS_INFO_TIME_THRESHOLD){
			 getCardsInfoTimeCount=0;
			 beginGetCardsInfoFlag=1;
			 
		}
	}
	irTime0Process();
	upTimeoutCount++;
}
void resetQueryCardArr(uchar aIndex){
	queryCardsArrA[aIndex][0]=0;// 没必要整个数组全部重置0，将表示采集人数的第一位置0即可
	queryCardsArrA[aIndex][1]=0;// 没必要整个数组全部重置0，将表示采集人数的第一位置0即可
}
void sendCardsInfo(){
	uint n;
	
//	uchar counts=queryCardsArrA[arrIndex][0]*3;
	uchar counts=queryCardsArrA[oldArrIndex][0]*3; 
//	ledTest1=~ledTest1;
	dsReadTime();

	ES=0;
	RS_485=1;
 	//发送起始符
	 SBUF=SYM_BEGIN;	
	 while(TI!=1);
	 TI=0; 
	 //发送时间
	for(n=0;n<SEND_TIME_LEN;n++)
	{           
		SBUF=sendTimeBuf[n];	
		while(TI!=1);
		TI=0; 
	}
	if(arrOk==0){  //第1次采集尚未完成
	 	SBUF=0;	   //总数为0
    	while(TI!=1);
    	TI=0;
	}
	else{		
	//	for(n=1;n<=counts;n++) 
		for(n=0;n<counts+2;n++) //加2是因为要发送第0位总数和第1位实际数量
		{   
	
			SBUF=queryCardsArrA[oldArrIndex][n];	
	    	while(TI!=1);
	    	TI=0;
	 	}
	}
	//发送结束符
	 SBUF=SYM_END;	
	 while(TI!=1);
	 TI=0; 
	 RS_485=0;
	 ES=1;
 //  queryCardsArrA[arrIndex][0]=0;// 没必要整个数组全部重置0，将表示采集人数的第一位置0即可
	
}



void EX0_ISR (void) interrupt 0 //外部中断0服务函数
{
	irExProcess();
}
void init(){
	RS_485=0;
	commState1=COMM_STATE_NOTHING;
	commState2=COMM_STATE2_NOTHING;
	init_spi();
//	dsInit();
//	dsWriteTime();
 	//串口1 方式1 9600		  			
	SCON = 0x50;		//8位数据,可变波特率
	AUXR |= 0x40;		//定时器1时钟为Fosc,即1T
	AUXR &= 0xFE;		//串口1选择定时器1为波特率发生器
	TMOD &= 0x0F;		//设定定时器1为16位自动重装方式
	TL1 = 0xE0;		//设定定时初值
	TH1 = 0xFE;		//设定定时初值
	ET1 = 0;		//禁止定时器1中断
	TR1 = 1;

	//串口2 9600bps@11.0592MHz
	S2CON = 0x50;		//8位数据,可变波特率
	AUXR |= 0x04;		//定时器2时钟为Fosc,即1T
	T2L = 0xE0;		//设定定时初值
	T2H = 0xFE;		//设定定时初值
	AUXR |= 0x10;		//启动定时器2
	IE2 = 0x01;

//	irInit();//下面展开
	AUXR &= 0x7F;		//定时器时钟12T模式
	TMOD &= 0xF0;		//设置定时器模式
	TMOD |= 0x01;		//设置定时器模式
	TL0 = 0xD7;		//设置定时初值   16位 600us 误差1%
	TH0 = 0xFD;		//设置定时初值
	TF0 = 0;		//清除TF0标志
	ET0=1;
	TR0=1;

 	IT0 = 1;   //指定外部中断0下降沿触发，INT0 (P3.2)
 	EX0 = 1;   //使能外部中断
 
	retreiveConfig();
	
	

 	IPH=0x10;
	IP=0x10;//设置串口1中断优先级为髙优先级
	ES = 1;
	EA = 1;

}



void main(){

	uint m,n=0;

	ledTest1=0;

	testInit();
	init();
	dsInit();
	dsWriteTime();
	


	while(1){
		if(beginGetCardsInfoFlag==1){
			beginGetCardsInfoFlag=0; 
			sendDataDown(CMD_REQ_CARDS);
			
			// 接受完数据后才令beginGetCardsInfoFlag=0;
		}
		processSettingIfNecessary();
		if(callStaffFlag==1){ //
			
		//	ledTest2=~ledTest2;
			
			CS_SEND=0;
			WriteByte(SPI_START);		
			for(m=0;m<=callStaffBuf[0]*2;m++){
				WriteByte(callStaffBuf[m]);
				delayUs(5);
			}				
				
			WriteByte(SYM_END);
			CS_SEND=1;
			commState1=COMM_STATE_NOTHING;
			callStaffFlag=0;			
		}
		if(setTimeFlag==1){
		//	ledTest2=~ledTest2;
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
/*		if(upTimeoutCount>UP_TIMEOUT_THRESHOLD){
			upTimeoutCount=0;
			commState1=COMM_STATE_TIMEOUT_REQ_STATE;
			sendDataDown(CMD_REQ_CARDS);
		}

*/		
		if(commState1==COMM_STATE_REQ_STATE){
			
			sendCardsInfo();
			commState1=COMM_STATE_NOTHING;
		
		}
		if(staDisplayFlag==1){
				
			initDisplayBuf();
			if(queryCardsArrA[oldArrIndex][0]==queryCardsArrA[oldArrIndex][1]){
				displayBuf[0]=1;
				displayBuf[6]=queryCardsArrA[oldArrIndex][1]/10;
				displayBuf[7]=queryCardsArrA[oldArrIndex][1]%10;
			}
			else{
				displayBuf[0]=2;
				displayCardIndex=(queryCardsArrA[oldArrIndex][0]-queryCardsArrA[oldArrIndex][1]-1)*3+2;
				displayCardId=(((uint)queryCardsArrA[oldArrIndex][displayCardIndex+1])<<7)+queryCardsArrA[oldArrIndex][displayCardIndex+2];
			
				displayBuf[4]=displayCardId/1000;
				displayBuf[5]=(displayCardId%1000)/100;
				displayBuf[6]=(displayCardId%100)/10;
				displayBuf[7]=displayCardId%10;
			}
	//		resetQueryCardArr(); //国庆前遗留，修改reset时机，包括修改arrIndex所有，上电时判断arrOk（判断时间在sendCardsInfo中）
			sendDisplay();
				
			staDisplayFlag=0;
		}
	/*	if(queryCardsFlag==1){
			CS_RECV=0;
			WriteByte(SPI_START);
			queryCardsLen=ReadByte();		
		
			for(m=0;m<queryCardsLen;m++){
				queryCardsArrA[arrIndex][m]=ReadByte();
				delayUs(5);
			}				
				
			WriteByte(SYM_END);
			CS_RECV=1;	
			if(queryCardsLen==3){
			ledTest1=~ledTest1;
			}
		}
	   */
/*	if(commState2==COMM_STATE2_RECV_FINISHED){
		commState2=COMM_STATE2_NOTHING;
		if(queryCardsArrA[arrIndex][0]==1&&queryCardsArrA[arrIndex][1]==0&&queryCardsArrA[arrIndex][2]==0&&queryCardsArrA[arrIndex][3]==2){
				ledTest1=~ledTest1;
		}

	}
*/	
//	sendDataDown(CMD_REQ_CARDS);
	}
}
