#include <reg52.h>
#include<intrins.h>
#include "nrf24.h"
#include "card_no.h"
sfr WKTCL = 0xaa;                   //掉电唤醒定时器计时低字节
sfr WKTCH = 0xab;                   //掉电唤醒定时器计时高字节
sfr P1M1=0x91;
sfr P1M0=0x92;
sfr P2M1=0x95;
sfr P2M0=0x96;
//sbit BTN=P0^0;
sbit BTN=P2^0;
//sbit LED_ALARM=P0^1;
//sbit RING_ALARM=P0^2;
//sbit RING_ALARM=P2^3;
//sbit LED_TEST2 =P3^4;
//sbit LED_TEST0 =P1^0;

//sbit LED_TEST1 =P1^1;
sbit RING_ALARM=P1^0;
sbit LED_TEST2 =P2^1;


bit answerFlag=0;
bit alertFlag=0,ringFlag=0,needInitFlag=0,btnDownFlag=0,changeReceiveModeFlag=0,batLowFlag=0;
uchar statusReg=0,delayTime,signalVal;
uchar  READER_ADDRESS[READER_ADDR_LEN]={0x01,0x02,0x03,0x04,0x05};
uchar cardAddr[CARD_ADDR_LEN]={0};
uchar cardAddrToReader[CARD_ADDR_TO_READER_LEN]={0};
uchar receiveAddr[CARD_RECEIVE_ADDR_LEN]={0x09,0x09,0x09,0x09,0x09};
uchar recBuf[DATA_LEN]={0};
#define SIGN_CARD_NORMAL 0x00
#define SIGN_CARD_ALERT 0x01 
#define SIGN_CARD_BAT_LOW 0x02 
#define SIGN_ANS 0x01
#define SIGN_ALERT 0x02

//uchar xdata TX_BUF[2]={0x01,0x01};

/*
void EX1_ISR (void) interrupt 2{
	//answerFlag=1;
	//关闭中断
	EA=0;
	sta=SPI_Read(STATUS);    // 返回状态寄存器
	
	LED_TEST2=~LED_TEST2;
    
	if((sta&0x40)==0x40)
	{
		answerFlag=1;
		
	}

	SPI_Write_Read_Register(WRITE_REG + STATUS, 0xff); // 清除TX_DS或MAX_RT中断标志	
    EA=1;
}
*/
#define BTN_HIGH_LIMIT 50000
#define BTN_LOW_LIMIT 2
#define ALERT_HIGH_LIMIT 2000
#define ALERT_LOW_LIMIT 2
uint btnCount=BTN_LOW_LIMIT;
uint alertCount=ALERT_LOW_LIMIT;
uchar alertM;
void alertIfNecessary(){
	if(alertFlag==1){
		alertFlag=0;
		alertCount=ALERT_HIGH_LIMIT;
//		LED_ALARM=0;//低有效
		LED_TEST2=0;
		while((--alertCount)>ALERT_LOW_LIMIT)
		{
			for(alertM=0;alertM<=150;alertM++){
				  RING_ALARM=1;
			}
			for(alertM=0;alertM<=150;alertM++){
				 RING_ALARM=0;
			}
		}
		{
			  	alertCount=ALERT_LOW_LIMIT;
				RING_ALARM=0;
//				LED_ALARM=1;
				LED_TEST2=1;
		}
	}

}
void makeAddr(){
	cardAddr[0]=0x0;
	cardAddr[1]=0x0;
	cardAddr[2]=0x0;
	cardAddr[3]=(CARD_ADDR>>7)&0x3f; //地址为第1字节低6位 和第2字节低7位
	cardAddr[4]=(CARD_ADDR)&0x7f;
	cardAddrToReader[0]=0x0;
	cardAddrToReader[1]=(CARD_ADDR>>7)&0x3f; //地址为第1字节低6位 和第2字节低7位
	cardAddrToReader[2]=(CARD_ADDR)&0x7f;

}
void initPin(){
	CE=0;
	CSN=1;
	SCLK=0;
//	MISO=0;
//	MOSI=0;
//	IRQ=1;
	delayMs(20);
}
//定时器1 函数
uchar timeCount;
void Timer0_isr(void) interrupt 1 using 1
{
	//50ms
	TH0=(65536-45872)/256;	      
 	TL0=(65536-45872)%256;
	if(timeCount++>=100){ //5m
	
		timeCount=0;
		needInitFlag=1;
	}
}
void init(){
	P1M1=0x0;
	P1M0=0x1;
	P2M1=0x1; //P2.0 仅为输入 P2.3 推挽输出
	P2M0=0x8;
    RING_ALARM=0;
	initPin();
	makeAddr();
 	//定时器0 方式1 ，50ms。
//	SBUF=0x0;
	TMOD |= 0x01;	  		     
	TH0=(65536-45872)/256;	      
	TL0=(65536-45872)%256;
	ET0=1;           
	TR0=1;  
	EA = 1;
}

void main(){
	

	init();

	
	WKTCL = 0xff;                     //设置唤醒周期为488us*(2047) = 1s
    WKTCH = 0x87;                   //使能掉电唤醒定时器
    while(1)
    {   
		PCON = 0x02;                //进入掉电模式
        _nop_();                    //掉电模式被唤醒后,直接从此语句开始向下执行
        _nop_();
	//	LED_TEST2=~LED_TEST2;
	//	RX_Mode_2(cardAddr,CARD_ADDR_LEN,receiveAddr,CARD_RECEIVE_ADDR_LEN);
				RX_Mode(receiveAddr,CARD_RECEIVE_ADDR_LEN);
		delayMs(5);
		sta=SPI_Read(STATUS);
		if((sta&0x40)==0x40)
		{
			LED_TEST2=~LED_TEST2;
			SPI_Read_Buffer(RD_RX_PLOAD, recBuf, DATA_LEN);
			SPI_Write_Read(FLUSH_RX);
		    SPI_Write_Read_Register(WRITE_REG + STATUS, 0xff); // 清除TX_DS或MAX_RT中断标志
			if(recBuf[0]==SIGN_CARD_ALERT)
			{
				LED_TEST2=~LED_TEST2;
				changeReceiveModeFlag=1;
			}
			cardAddrToReader[0]=signalVal;
			nRF24L01_TxPacket(READER_ADDRESS, READER_ADDR_LEN,cardAddrToReader,CARD_ADDR_TO_READER_LEN); 
			cardAddrToReader[0]=0;
			signalVal=0;
		}
		if(changeReceiveModeFlag==1){
			changeReceiveModeFlag=0;
			RX_Mode(cardAddr,CARD_ADDR_LEN);
	
			PCON = 0x02;                //进入掉电模式
        	_nop_();                    //掉电模式被唤醒后,直接从此语句开始向下执行
        	_nop_();
			sta=SPI_Read(STATUS);
			if((sta&0x40)==0x40)
			{
			//	LED_TEST2=~LED_TEST2;
				SPI_Read_Buffer(RD_RX_PLOAD, recBuf, DATA_LEN);
				SPI_Write_Read(FLUSH_RX);
			    SPI_Write_Read_Register(WRITE_REG + STATUS, 0xff); // 清除TX_DS或MAX_RT中断标志
				if(recBuf[0]==SIGN_CARD_ALERT)
				{
					alertFlag=1;
				}
				
			}
			cardAddrToReader[0]=signalVal;
			nRF24L01_TxPacket(READER_ADDRESS, READER_ADDR_LEN,cardAddrToReader,CARD_ADDR_TO_READER_LEN); 
			cardAddrToReader[0]=0;	
			signalVal=0;	
		}

		
		if(BTN==1){
			btnDownFlag=1;
			
		}
		delayMs(1);
		powerOff();
		if(btnDownFlag==1&&BTN==1){
			btnDownFlag=0;
			alertFlag=1;
			signalVal|=SIGN_CARD_ALERT;
			
		}
		if(batLowFlag==1){
			batLowFlag=0;
			signalVal|=SIGN_CARD_BAT_LOW;
		}
		alertIfNecessary();


    }
	
  
}
