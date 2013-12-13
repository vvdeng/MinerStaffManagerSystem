#include <reg52.h>
#include "vvspi.h"
#include "useful.h"
#include "sd2200.h"
#include "display.h"
#include "config.h"
#include "constant.h"
#include "pm25.h"
sfr P3M1=0xb1;
sfr P3M0=0xb2;
sbit alarmIO=P2^3;
sbit ledTest2=P3^5;
sbit ledTest1=P3^4;
sbit RS_485=P3^6;
#define RS485_R 1 
#define RS485_T 0
uint  setTimeBufIndex=0;
uchar   xdata callStaffBuf[CALL_STAFF_BUF_LEN]={0};
bit callStaffFlag=0; //测试时设置为1
bit queryCardsFlag=1;
bit setTimeFlag=0;
bit upTimeoutFlag=0;
bit saveCardsFlag=0;
bit staFlag=0;
uint   upTimeoutCount=0;
uint   displayCardId=0,displayCardIndex=0;
uchar xdata queryCardsArr[CARDS_ARR_LEN+2]={0};//第0字节存储包含呼叫和欠压的标识卡总数 第1字节存储实际上识别到的标识卡数量

uchar xdata setTimeBuf[SET_TIME_BUF_LEN]={0};
uchar  queryCardsLen=0;queryCardIndex=0,callStaffBufIndex=0;

uchar  commState1;commState2;

void sendCardsInfo();
void sendDataUp(uchar dat){
	RS_485=RS485_T;
	ES=0;
	SBUF=dat;	
	while(TI!=1);
 	TI=0; 
//	ledTest1=~ledTest1;
	ES=1;
	RS_485=RS485_R;
}
void sendDataUp2(uchar dat){

	SBUF=dat;	
	while(TI!=1);
 	TI=0; 
//	ledTest1=~ledTest1;
	
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
//分隔符号定义为  0x1xxx YYYx YYY不能为全0
#define SYM_BEGIN 0xFD
#define SYM_END 0xFE
#define STATION_DATA_DELIMN 0xF8
#define READER_DATA_DELIMN 0xF9 
#define DATA_END 0xFF
#define COMM_STATE_NOTHING 0
#define COMM_STATE_REQ_STATE_WAIT_ADDR 1
#define COMM_STATE_REQ_STATE 2
#define COMM_STATE_SYNCH_TIME 3
#define COMM_STATE_SYNCH_TIME_READY 4
#define COMM_STATE_CALL_STAFF 5
#define COMM_STATE_CALL_STAFF_READY 6
#define COMM_STATE_TIMEOUT_REQ_STATE 7
#define COMM_STATE_REQ_TIMEOUT_CARD_ADDR 8
#define COMM_STATE_REQ_TIMEOUT_CARD_TRANS 9
//命令格式为0x1xxx000x
#define CMD_REQ_STATE 0x80  //0x1000 000
#define CMD_SYNCH_TIME 0x81
#define CMD_CALL_STAFF 0x90
#define CMD_REQ_TIMEOUT_CARD 0x91
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
		//命令格式为0x1xxx000x
		if((SBUF&0x8e)==0x80){  
			
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
			else if(SBUF==CMD_REQ_TIMEOUT_CARD){
				commState1=COMM_STATE_REQ_TIMEOUT_CARD_ADDR;
			}
			return;
		}
	 
	  

		 if(commState1==COMM_STATE_REQ_STATE_WAIT_ADDR){
				if((configArr[CONFIG_ADDR])==SBUF){ //分站巡检时地址从0开始，实际地址码从1开始
		//		if(1==SBUF){ //分站巡检时地址从0开始，实际地址码从1开始
		//		ledTest1=~ledTest1;	
					commState1=COMM_STATE_REQ_STATE;
			//		if(saveCardsFlag==0){		 //丢弃掉尚未完成保存的当前轮次数据
						sendDataDown(CMD_REQ_CARDS);
			//		}
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
		else if(commState1==COMM_STATE_REQ_TIMEOUT_CARD_ADDR){
			if(SBUF==1){ //测试用，硬编码读卡器地址
		//	if((configArr[CONFIG_ADDR])==SBUF){ //分站巡检时地址从0开始，实际地址码从1开始
				commState1=COMM_STATE_REQ_TIMEOUT_CARD_TRANS;
				  
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
		
		if(commState1==COMM_STATE_REQ_STATE||commState1==COMM_STATE_TIMEOUT_REQ_STATE){

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
							  
					if(commState1==COMM_STATE_REQ_STATE){
						
						sendCardsInfo();
					
					}else{
				//		ledTest2=~ledTest2;	
					}
					staFlag=1;
					commState1=COMM_STATE_NOTHING;
					commState2=COMM_STATE2_NOTHING;
				}
				else{
					queryCardsArr[queryCardIndex++]=S2BUF;
				}
			}
		
		}
	
		
	}
	if(S2CON&S2TI)
	{
		
	
	
		S2CON&=~S2TI;
	

	}

}
void resetQueryCardArr(){
	queryCardsArr[0]=0;// 没必要整个数组全部重置0，将表示采集人数的第一位置0即可
	queryCardsArr[1]=0;// 没必要整个数组全部重置0，将表示采集人数的第一位置0即可
}
void sendCardsInfo(){
	uint n;
	
//	uchar counts=queryCardsArr[0]*3;
	uchar counts=queryCardsArr[0]*3; 
//	ledTest1=~ledTest1;
	dsReadTime();

	ES=0;
	RS_485=RS485_T;
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
		
//	for(n=1;n<=counts;n++) 
	for(n=0;n<counts+2;n++) //加2是因为要发送第0位总数和第1位实际数量
	{   

		SBUF=queryCardsArr[n];	
    	while(TI!=1);
    	TI=0;
 	}
	//发送结束符
	 SBUF=SYM_END;	
	 while(TI!=1);
	 TI=0; 
	 RS_485=RS485_R;
	 ES=1;
 //  queryCardsArr[0]=0;// 没必要整个数组全部重置0，将表示采集人数的第一位置0即可
	
}

void tim0_isr (void) interrupt 1 using 1
{
	TL0 = 0xD7;		//设置定时初值   16位 600us 误差1%
	TH0 = 0xFD;		//设置定时初值
	irTime0Process();
	if(upTimeoutFlag==0)
	{
		upTimeoutCount++;
	}
}


void EX0_ISR (void) interrupt 0 //外部中断0服务函数
{
	irExProcess();
}
void init(){
	alarmIO=0;
	P3M1=0x00;
	P3M0=0x40;
	RS_485=RS485_R;
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
	
	resetFlash();

 	IPH=0x10;
	IP=0x10;//设置串口1中断优先级为髙优先级
	ES = 1;
	EA = 1;

}

#define FLASH_COUNT_LIMIT 30
#define COUNT_POS 0x04
#define SECTOR_SIZE 0x1000
#define FLASH_BUF_LEN 300
#define DEFAULT_DATA_POS 0x08
#define FIRST_DATA_POS 0x1000
#define SEC_DATA_END 0xfe
#define ORIGINAL_DATA 0xff
uchar flashCount=0;
ulong dataPos;
uchar queryCardsCount=0;
uchar xdata flashBuf[FLASH_BUF_LEN]={0};

/*uchar timeTest=1;
uchar a=18,b=0;
ulong addr=0x0000;
void pm25Test(){
	g_fFlashOK=0;
	FlashCheckID();
	if(g_fFlashOK==1){
	  ledTest1=0;
	}
	else{
	  ledTest1=1;
	}
	
	
		
		eraseSector(addr);
		  
		
		FlashWrite(addr,1,&a);
	
		FlashRead(addr,1,&b);

//		eraseSector2(addr);
		  
//		delayMs(2000);
//		writeMem(addr,a);
	
//		b=readMem(addr);
		if(18==b){
		  ledTest2=0;
		}
		else{
		  ledTest2=1;
		}
	
	while(1);
}
*/
void saveDataToFlash(){
	uint n=0;
	uint tempCount=0;
	saveCardsFlag=1;
	
    FlashRead(COUNT_POS,1,&flashCount);
	if(flashCount==0||flashCount==0xff){
		flashCount=0;
	}else if(flashCount>=FLASH_COUNT_LIMIT){
//		flashCount=FLASH_COUNT_LIMIT;
//		FlashWrite(COUNT_POS,1,&flashCount);	//可注释
		return;
	}

	queryCardsCount=queryCardsArr[0]*3;
	      	
	
	if(queryCardsCount>0){
		ledTest2=~ledTest2;
		dsReadTime();
		for(n=0;n<SEND_TIME_LEN;n++)
		{           
			flashBuf[tempCount++]=sendTimeBuf[n];
		}
		

/*		
		flashBuf[0]=7;
		flashBuf[1]=7;
		flashBuf[2]=timeTest;
		timeTest++;
		tempCount=3;
*/		for(n=0;n<queryCardsCount+2;n++) //加2是因为要发送第0位总数和第1位实际数量
		{   
	
			flashBuf[tempCount++]=queryCardsArr[n];	
	 	}
		flashBuf[tempCount++]=SEC_DATA_END;
		eraseSector(FIRST_DATA_POS+flashCount*SECTOR_SIZE);
		FlashWrite((ulong)(FIRST_DATA_POS+flashCount*SECTOR_SIZE),FLASH_BUF_LEN,&flashBuf);
		flashCount++;
		eraseSector(0);
		FlashWrite(COUNT_POS,1,&flashCount);
		
	}
	saveCardsFlag=0;

}
void clearFlashBuf(){
	uint m;
	for(m=0;m<FLASH_BUF_LEN;m++){
		flashBuf[m]=0;
	}
}
/*
uchar flashCountTest=0;
void testFlash(){
	  FlashRead(COUNT_POS,1,&flashCountTest);
	if(flashCountTest==0xff){
		flashCountTest=0;
	}
	flashCountTest++;
		eraseSector(0);
		FlashWrite(COUNT_POS,1,&flashCountTest);
		flashCountTest=8;
		flashBuf[0]=7;
		flashBuf[1]=7;
		flashBuf[2]=7;
		flashBuf[3]=SEC_DATA_END;
		eraseSector((ulong)FIRST_DATA_POS);
		FlashWrite((ulong)(FIRST_DATA_POS),30,&flashBuf);
}
*/
uchar a=18,b=0;
	ulong addr=0x08;
void main(){

	uint m,n=0;
	
//	FlashWrite(COUNT_POS,1,&flashCount);//测试用
	ledTest1=0;
	 ledTest2=0;
	testInit();
	init();
	dsInit();

//	flashCount=0;
//	eraseSector(0);
//	FlashWrite(COUNT_POS,1,&flashCount);

//	testFlash();

//	pm25Test();
		g_fFlashOK=0;
	FlashCheckID();
	if(g_fFlashOK==1){
	  ledTest1=0;
	}
	else{
	  ledTest1=1;
	}
	
	
		
		eraseSector(addr);
		  
		
		FlashWrite(addr,1,&a);
	
		FlashRead(addr,1,&b);

//		eraseSector2(addr);
		  
//		delayMs(2000);
//		writeMem(addr,a);
	
//		b=readMem(addr);
		if(18==b){
		  ledTest2=0;
		}
		else{
		  ledTest2=1;
		}
	
	while(1);
	while(1){
	//	ledTest2=~ledTest2;
	/*	delayMs(7000);
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
	*/
		processSettingIfNecessary();
		if(callStaffFlag==1){ //
			alarmIO=~alarmIO;
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
			if(inSetting==0){
				initDisplayBuf();
				
				for(m=0;m<SHOW_TIME_LEN;m++){
					displayBuf[m]=showTimeBuf[m];
				}
				
				sendDisplay();
			}
			commState1=COMM_STATE_NOTHING;
			setTimeFlag=0;
		}

		if(commState1==COMM_STATE_REQ_TIMEOUT_CARD_TRANS){
			upTimeoutFlag=1; //暂停计数
			 RS_485=RS485_T;
			 ES=0;
			 sendDataUp2(1);
			 sendDataUp2(1);
			 sendDataUp2(1);
			 sendDataUp2(1);
			 sendDataUp2(1);
		//	 sendDataUp2(SYM_END);
			  
			 sendDataUp2(SYM_BEGIN);
		
			 FlashRead(COUNT_POS,1,&flashCount);
			 sendDataUp2(flashCount);	//总次数
			
			 sendDataUp2(SYM_END);
		/*	 clearFlashBuf();
			 FlashRead((ulong)(FIRST_DATA_POS),FLASH_BUF_LEN,&flashBuf);
			 	 	m=0;
				while(flashBuf[m]!=SEC_DATA_END&&m<FLASH_BUF_LEN)
			 	{
			 		sendDataUp2(flashBuf[m++]);
				
			 	}
				sendDataUp2(READER_DATA_DELIMN);
		  */
			 while(flashCount>0){
			// 	clearFlashBuf();
			sendDataUp2(flashCount);	//序号
			 	FlashRead((FIRST_DATA_POS+flashCount*SECTOR_SIZE),SECTOR_SIZE,&flashBuf);
			 	m=0;
			
			 	while(flashBuf[m]!=SEC_DATA_END&&flashBuf[m]!=ORIGINAL_DATA&&m<SECTOR_SIZE)
			 	{
			 		sendDataUp2(flashBuf[m++]);
				
			 	}
				sendDataUp2(READER_DATA_DELIMN);
			 	flashCount--;
		//		//为减少等待时间，（eraseSector至少要等50ms）考虑新建变量temp=flashCount，每传输10次再回写temp替代flashCount
		//		eraseSector(0);
		//		FlashWrite(COUNT_POS,1,&flashCount);
			 }
		   
			 sendDataUp2(SYM_END);
			 ES=1;
			 RS_485=RS485_R;
			 ledTest1=~ledTest1;
			 //尝试在传输完成后再回写flashCount，而不是每次都回写
			  	eraseSector(0);
				FlashWrite(COUNT_POS,1,&flashCount);
			   commState1= COMM_STATE_NOTHING;
			   upTimeoutFlag=0;//恢复计数
		}
		if(staFlag==1&&inSetting==0){
			
			initDisplayBuf();
			if(queryCardsArr[0]==queryCardsArr[1]){
				displayBuf[0]=0x0c;	 //"C"
				displayBuf[4]=queryCardsArr[1]/10;
				displayBuf[5]=queryCardsArr[1]%10;
			}
			else{
				displayBuf[0]=17; //"H"
				displayCardIndex=(queryCardsArr[0]-queryCardsArr[1]-1)*3+2;
				displayCardId=(((uint)queryCardsArr[displayCardIndex+1])<<7)+queryCardsArr[displayCardIndex+2];
			
				displayBuf[2]=displayCardId/1000;
				displayBuf[3]=(displayCardId%1000)/100;
				displayBuf[4]=(displayCardId%100)/10;
				displayBuf[5]=displayCardId%10;
			}
	//		resetQueryCardArr();
			sendDisplay();	
			staFlag=0;
		}
		if(upTimeoutCount>UP_TIMEOUT_THRESHOLD){
			upTimeoutFlag=1;
			upTimeoutCount=0;
			saveDataToFlash();
			resetQueryCardArr();
			commState1=COMM_STATE_TIMEOUT_REQ_STATE;
			sendDataDown(CMD_REQ_CARDS);
			upTimeoutFlag=0;
		}
	/*	if(queryCardsFlag==1){
			CS_RECV=0;
			WriteByte(SPI_START);
			queryCardsLen=ReadByte();		
		
			for(m=0;m<queryCardsLen;m++){
				queryCardsArr[m]=ReadByte();
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
		if(queryCardsArr[0]==1&&queryCardsArr[1]==0&&queryCardsArr[2]==0&&queryCardsArr[3]==2){
				ledTest1=~ledTest1;
		}

	}
*/	
//	sendDataDown(CMD_REQ_CARDS);
	}
}
