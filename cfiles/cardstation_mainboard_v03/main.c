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


#define STATION_PORT_NUM 4 //每个分站最多4个端口
#define CARD_INFO_BYTES 3  //每个标识卡信息3个字节
#define POLL_COUNT 4  //保存POLLCOUNT个巡检周期的数据
#define MAX_CALL_STAFF_COUNT 80
#define STAFF_BUF_LEN (MAX_CALL_STAFF_COUNT*2+1) //呼叫时每个id两个字节 第0字节保存人数

#define SPI_START 0x80
#define SYM_END 0xFE
#define SEG_COUNT 8    

#define READER_BUF_LEN (READER_CARDNUM*CARD_INFO_BYTES+6) //第0至2字节为读卡器采集时间时分秒各1字节，第3字节为标识卡总数或其他标志含义（例如超时） ，第4字节为标识卡实际数量，最后一位为添加的分隔符， 所以要加6
#define SET_TIME_BUF_LEN 8
          
#define   S2RI    0x01                 
#define   S2TI    0x02    

#define RS485_R 1
#define RS485_T 0      
               





//指示灯
//sbit LED_TXD1 =P1^0;
//sbit LED_RXD1 =P1^1;
//sbit LED_TXD2 =P3^3;
//sbit LED_RXD2 =P3^4;
//sbit k0=P3^5;

//串口1 485读写控制
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
#define COMM_STATE_REQ_STATE_WAIT_ADDR_END 7 //传输接口传输地址位后传输SYS_END，必须等SYS_END后才能发送数据
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
#define SYS_TIMEOUT 0x60 //SYS_TIMEOUT必须大于80小于128 80以下表示采集人数
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
			if(SBUF==1)//测试用，硬编码分站地址
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
			//	sendDataDown2(1); //测试用，硬编码分站地址
				sendDataDown2(1);//发送读卡器编号 应该为tempReaderNo
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
			commState2=COMM_STATE2_NOTHING; //串口1处理数据时串口2停止 可以考虑逻辑更换为只有在串口1 采集人员信息时 串口2才停止
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



void EX0_ISR (void) interrupt 0 //外部中断0服务函数
{
	
	irExProcess();
}

void init() {
	
	P3M1=0x00;
	P3M0=0xC0;
	//启用扩展内存，放在第一句
	localAddress=1; //测试用，地址硬编码

//	init_spi();
//	dsInit();
//	dsWriteTime();
	RS_485_1=RS485_R ;
 	RS_485_2=RS485_T ;
/* 	//定时器0 方式1 ，50ms。
//	SBUF=0x0;
	TMOD |= 0x01;	  		     
	TH0=(65536-45872)/256;	      
	TL0=(65536-45872)%256;
	ET0=1;           
	TR0=1;   
*/
 	//串口1 方式1 9600		  			
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
	//发送时间
	dsReadTime();		
	for(m=0;m<SEND_TIME_LEN;m++){
		SBUF=sendTimeBuf[m];
	 	while(TI!=1);
	 	TI=0; 
	} 
	if(pollCount!=1){ //借用pollCount来作为判断是否采集完成和可以采集的标志
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
		/*	//模拟轮询采集时的时间
		 	SBUF=(0x40|k);	
			while(TI!=1);
		 	TI=0;
		*/
		
			for(m=0;m<STATION_PORT_NUM;m++){
		    	//发送端口号，以1开始
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
				//防止cardsInfoArr丢失SYM_DELIMITER字节，所以在遍历完后统一处理。
				SBUF=SYM_DELIMITER;	
			    while(TI!=1);
			    TI=0; 
	
			}
		}
	}
	//发送结束符
	 SBUF=SYM_END;	
	 while(TI!=1);
	 TI=0; 
	 pollCount=0;//充值轮询起始索引
	 RS_485_1=RS485_R;
	 ES=1;
}
void commAction(){
	
	if((commState1==COMM_STATE_REQ_STATE)/*&&totalRefreshedFlag==1*/){

		sendCardsInfo();
		commState1=COMM_STATE_NOTHING;
		statesSentFlag=1;//为1 则开始巡检读卡器收集到的信息
	}
}
void setCurrentReaderTimeout(){
	
	cardsInfoArr[pollCount][currentReaderIndex][currentBufferIndex++]=SYS_TIMEOUT;//SYS_TIMEOUT必须大于80小于128 80以下表示采集人数
	cardsInfoArr[pollCount][currentReaderIndex][currentBufferIndex++]=SYM_DELIMITER;
	
}
void endGetReaderInfo(){
   commState2=COMM_STATE2_NOTHING;
   totalRefreshedFlag=1;
   pollCount++; //借用pollCount来作为判断是否采集完成和可以采集的标志
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
	//发送命令之后再设置状态
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
//定时器1 函数
void Timer0_isr(void) interrupt 1 using 1
{
	//50ms

//	TH0=(65536-45872)/256;	      
// 	TL0=(65536-45872)%256;
	TL0 = 0xD7;		//设置定时初值   16位 600us 误差1%
	TH0 = 0xFD;		//设置定时初值
	irTime0Process();	
	if(commState2==COMM_STATE2_REQ_READER_STATE){
		
		if(commTimeoutFlag==0&&commTimeoutActionFlag==0)
		{
			
			commTimeoutCount++;
//			if(commTimeoutCount>=4){
			if(commTimeoutCount>=350){	
			    //超时后通信结束，再次开始通信过程，只有在发送请求状态命令后，才才更新commState2=COMM_STATE2_REQ_READER_STATE
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
//			if(commFinishedTimeCount>=20){ //20*50ms内数据仍未传输完成
			if(commFinishedTimeCount>=1667){ //1667*0.6ms内数据仍未传输完成
				commState2=COMM_STATE2_NOTHING;
				commFinishedTimeCount=0;
				//暂不进行重试，简单认为断线
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
		//		statesSentFlag=1;//为1 则开始巡检读卡器收集到的信息				  分站不进行多次巡检
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
	AUXR |= 0x02; //stc单片机扩展内存
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
	WDT_CONTR = 0x3f;//设置看门狗，溢出时间9.1022s
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
	//		staffBuf[0]=(staffBuf[0]*2)<STAFF_BUF_LEN?staffBuf[0]:0;//如果总人数*2大于缓冲区，则认为传输出错
			
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
		WDT_CONTR = 0x3f;//设置看门狗，溢出时间9.1022s
		
		
	}

}



