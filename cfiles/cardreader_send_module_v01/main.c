#include <reg52.h>
#include<intrins.h>
#include "nrf24.h"
#include "vvspi.h"

//for无线模块电路板

sbit LED_TEST2 =P2^6;
sbit LED_TEST0 =P2^5;
//sbit LED_TEST1 =P1^1;

bit answerFlag=0;
bit alertFlag=0,ringFlag=0;
uchar statusReg=0;
uchar  READER_ADDRESS[READER_ADDR_LEN]={0x01,0x02,0x03,0x04,0x05};
uchar  CARD_RECEIVE_ADDRESS[CARD_RECEIVE_ADDR_LEN]={0x09,0x09,0x09,0x09,0x09};
uchar cardAddrArr[CARD_ADDR_LEN]={0};
uchar recBuf[DATA_LEN]={0};
uchar testAlertCount=0,callStaffIndex=0;
bit callStaffFlag=0,callAllStaffFlag=0,callStaffRunFlag=0;
#define MAX_CALL_STAFF_NUM 12
#define CALL_STAFFS_LEN 25 //第0位为人数 最大12个人
#define SIGN_CARD_NORMAL 0x00
#define SIGN_CARD_ALERT 0x01 
#define SIGN_ALL_CARD_ALERT 0x02 
#define SIGN_ANS 0x01
#define SIGN_ALERT 0x82
uint cardAddr=1;
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

void makeAddr(uint addr){
	cardAddrArr[0]=0x0;
	cardAddrArr[1]=0x0;
	cardAddrArr[2]=0x0;
	cardAddrArr[3]=(addr>>7)&0x3f; //地址为第1字节低6位 和第2字节低7位
	cardAddrArr[4]=(addr)&0x7f;
}
void makeAddr2(uchar addr[]){
	cardAddrArr[0]=0x0;
	cardAddrArr[1]=0x0;
	cardAddrArr[2]=0x0;
	cardAddrArr[3]=addr[0]; //地址为第1字节低6位 和第2字节低7位
	cardAddrArr[4]=addr[1];
}
void init(){
	
	
	init_spi();
	EA=1;
}
#define SYM_START 0x80
#define SYM_END 0xFE
#define SPI_START 0x80
#define SPI_STATE_NONE 0
#define SPI_STATE_WAIT_TIME 1
#define SPI_STATE_TIME_FINISHED 2
uchar spiState=SPI_STATE_NONE;
uchar  spiBufIndex=0;
uchar xdata spiBuf[CALL_STAFFS_LEN]={0};
void spi_isr( ) interrupt 9 using  1         //SPI interrupt routine 9 (004BH)
{

      SPSTAT = SPIF | WCOL;         //clear SPI status
//	  if(callStaffRunFlag==1){
//	  	return;
//	  }
	  switch(SPDAT){
	  case SYM_START:
	  	spiBufIndex=0;
		
		break;
	  case SYM_END:
	 	// 	spiState=SPI_STATE_TIME_FINISHED;
		LED_TEST2=~LED_TEST2;
		if(spiBuf[0]==1&&spiBuf[1]==0x7f){
		   callAllStaffFlag=1;
		}
		else 
		{
			callAllStaffFlag=0;
			if(spiBuf[0]>0){
				callStaffFlag=1;
			}
			else{
				callStaffFlag=0;
			}	
		}
	
		spiBufIndex=0;	
		spiState=SPI_STATE_NONE;		
		break;
	  default:
	  	spiBuf[spiBufIndex]=SPDAT;
		++spiBufIndex;
	  }

      SPDAT =0;                 

}


void main(){
	uchar m;
	init();
	
	LED_TEST0=0;
//	LED_TEST1=0;
	LED_TEST2=0; 

    while(1)
    {   
//		testAlertCount=(testAlertCount+1)%3;
		if(callAllStaffFlag==1){
			 recBuf[0]=SIGN_ALL_CARD_ALERT;
		}
		else if(callStaffFlag==1){
			recBuf[0]=SIGN_CARD_ALERT;
		
		}
		else{
			recBuf[0]=SIGN_CARD_NORMAL;		
		}
		nRF24L01_TxPacket(CARD_RECEIVE_ADDRESS, CARD_RECEIVE_ADDR_LEN,recBuf,DATA_LEN);
		delayMs(2);
		if(callStaffFlag==1){
			for(m=0;m<3;m++){
				for(callStaffIndex=1/*第0位为总人数*/;callStaffIndex<spiBuf[0]*2;callStaffIndex+=2)
				{ 
					nRF24L01_TxPacket(CARD_RECEIVE_ADDRESS, CARD_RECEIVE_ADDR_LEN,recBuf,DATA_LEN);
					delayMs(2);
					makeAddr2(&spiBuf[callStaffIndex]);
					nRF24L01_TxPacket(cardAddrArr, CARD_ADDR_LEN,recBuf,DATA_LEN);
					delayMs(2);
				}
			}
			LED_TEST0=~LED_TEST0;
		}
		


    	
    }
  
}
