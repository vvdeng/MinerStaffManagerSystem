#include <reg52.h>
#include<intrins.h>
#include<string.h>
#include "nrf24.h"
#include "vvspi.h"
#include "useful.h"
//#include "display.h"
//#include "insideStorage.h"
//sfr AUXR  = 0x8e;               //�����Ĵ���
sfr S2CON = 0x9a;               //UART2 ���ƼĴ���
sfr S2BUF = 0x9b;               //UART2 ���ݼĴ���
sfr T2H   = 0xd6;               //��ʱ��2��8λ
sfr T2L   = 0xd7;               //��ʱ��2��8λ
//sfr IE2   = 0xaf;               //�жϿ��ƼĴ���2

#define S2RI  0x01              //S2CON.0
#define S2TI  0x02              //S2CON.1
#define S2RB8 0x04              //S2CON.2
#define S2TB8 0x08              //S2CON.3


#define  _Nop()  _nop_()

#define READER_CARDNUM 80  //ÿ����������󱣴�ı�ʶ������
#define READER_CARD_EXTRA_NUM  20  //ÿ����������󱣴�ı�ʶ�����л�Ƿѹ�ϱ�����
#define CARD_INFO_BYTES 3  //ÿ����ʶ����Ϣ3���ֽ�
#define CARDS_ARR_LEN (READER_CARDNUM*CARD_INFO_BYTES)
#define CARDS_EXTRA_ARR_LEN (READER_CARD_EXTRA_NUM*CARD_INFO_BYTES)
sfr WDT_CONTR = 0xc1; //���Ź���ַ
sfr IPH    = 0xB7;             
               
               












sbit LED_TEST0 =P2^5;
sbit LED_TEST1 =P2^6;
uchar  READER_ADDRESS[READER_ADDR_LEN]={0x01,0x02,0x03,0x04,0x05};
uchar  cardAddr[DATA_LEN]={0x00};
uint  cardAddrInt=0;
uchar transBuf[DATA_LEN]={0};
//bit alertFlag=0,ringFlag=0;
#define SIGN_CARD_NORMAL 0x00
#define SIGN_CARD_RING 0x01 
#define SIGN_ANS 0x01
#define SIGN_ALERT 0x02

#define FLAG_BIT 6 //
#define CARD_TOTAL 1000 //��ʶ����������Ŵ�1��CARD_TOTAL
#define MAX_STAFFS_COUNT 80
#define CALL_STAFFS_LEN (MAX_STAFFS_COUNT*3+1) //��0λΪ����
//uchar xdata receivedCardNo[CARD_TOTAL/8]={0};
//uchar xdata ringCardNo[CARD_TOTAL/8]={0};
uchar xdata receivedCardNoAndRing[CARD_TOTAL/8]={0};
uchar localAddress,commState1,commState2,currentReaderIndex=0,currentCardsInfoIndex=0,currentCardsExtraIndex=0,receivedFlag=0;

uchar  xdata cardsInfoArr[CARDS_ARR_LEN]={0};
uchar  xdata cardsExtraArr[CARDS_EXTRA_ARR_LEN]={0};
bit reqCardsFlag=0,testFlag=0;














#define COMM_STATE_NOTHING 0

#define COMM_STATE_REQ_STATE 1
#define CMD_REQ_STATE 0x80
#define COMM_STATE2_NOTHING 0
#define COMM_STATE2_REQ_READER_STATE 1
#define COMM_STATE2_REQ_READER_STATE_STARTED 2 
#define CMD_REQ_READER_STATE 0x80
#define SYM_BEGIN 0xF0
#define SYM_END 0xFE
/*
//uchar preData=0,curData=0; 

#define SYM_START 0x80
#define SYM_END 0xFE
#define SPI_START 0x80
#define SPI_STATE_NONE 0
#define SPI_STATE_WAIT_TIME 1
#define SPI_STATE_TIME_FINISHED 2
//uchar xdata spiState=SPI_STATE_NONE;*/
//uchar  spiBufIndex=0;
//uchar spiTemp;
//uchar xdata spiBuf[CALL_STAFFS_LEN]={0};
//uchar xdata testArr[128]={0};
/*void spi_isr( ) interrupt 9 using  1         //SPI interrupt routine 9 (004BH)
{
		
      SPSTAT = SPIF | WCOL;         //clear SPI status
//	spiTemp=SPDAT;
	  switch(SPDAT){
	  
	  case SYM_START:
	  	
	  	spiBufIndex=0;
		
		break;
	  case SYM_END:
	  	spiState=SPI_STATE_TIME_FINISHED;
		
		break;
	  default:
	  	testArr[spiBufIndex]=SPDAT;
		++spiBufIndex;
		  
	  }
                  
}
*/
#define CMD_REQ_CARDS 0xFE
void Uart2() interrupt 8 using 1
{
    if (S2CON & S2RI)
    {
	LED_TEST0=~LED_TEST0;
        S2CON &= ~S2RI;         
		if(S2BUF==CMD_REQ_CARDS){
				reqCardsFlag=1;
		} 
	}
    if (S2CON & S2TI)
    {
        S2CON &= ~S2TI;         
        
    }
}


void init() {
	uchar n;

//	init_spi();
	//����2
 	S2CON = 0x50;		//8λ����,�ɱ䲨����
	AUXR |= 0x04;		//��ʱ��2ʱ��ΪFosc,��1T
	T2L = 0xE0;		//�趨��ʱ��ֵ
	T2H = 0xFE;		//�趨��ʱ��ֵ
	AUXR |= 0x10;		//������ʱ��2
	IE2 = 0x01;             //ʹ�ܴ���2�ж�
	EA = 1;
	
//    commState1=COMM_STATE_NOTHING;
    
	

	for(n=0;n<(CARD_TOTAL/8);n++)
	{
		receivedCardNoAndRing[n]=0;
	}


                                     
}
void sendCardsInfo(){
	uint n;
//	LED_TST=~LED_TST;

	IE2 = 0x00; 
	
	//������ʼ��
	S2BUF=SYM_BEGIN;	
	while(!(S2CON&S2TI));
 	S2CON &= ~S2TI; 
	//���͵�ǰ��ʶ���������������к�Ƿѹ�ı�ʶ����
	S2BUF=(currentCardsInfoIndex+currentCardsExtraIndex)/3;	
	while(!(S2CON&S2TI));
 	S2CON &= ~S2TI;  
	//���͵�ǰ��ʶ����
	S2BUF=(currentCardsInfoIndex/3);	
	while(!(S2CON&S2TI));
 	S2CON &= ~S2TI; 
	for(n=0;n</*READER_BUF_LEN*/currentCardsInfoIndex;n++) 
	{   
/*		if(cardsInfoArr[n]==0xff){
			break;
		}
*/		S2BUF=cardsInfoArr[n];	
		while(!(S2CON&S2TI));
	 	S2CON &= ~S2TI; 
	
	}
	for(n=0;n</*READER_BUF_LEN*/currentCardsExtraIndex;n++) 
	{   
/*		if(cardsExtraArr[n]==0xff){
			break;
		}
*/		S2BUF=cardsExtraArr[n];	
		while(!(S2CON&S2TI));
	 	S2CON &= ~S2TI; 
	
	}
	//���ͽ�����
	 S2BUF=SYM_END;	
	 while(!(S2CON&S2TI));
 	 S2CON &= ~S2TI; 

	IE2 = 0x01; 
	memset(receivedCardNoAndRing, 0,CARD_TOTAL/8);
	currentCardsInfoIndex=0;//���ݷ�����ϣ������ۼ�
	currentCardsExtraIndex=0;//���ݷ�����ϣ������ۼ�
	// LED_TST4=~LED_TST4;
}





void testInit(){
//	uint n;
	
/*	for(n=0;n<CARDS_ARR_LEN;n++)
	{
		
		cardsInfoArr[n]=0xff; //���Կ���memset
	}
	for(n=0;n<CARDS_EXTRA_ARR_LEN;n++)
	{
		
		cardsExtraArr[n]=0xff; //���Կ���memset
	}
*/
	cardsInfoArr[0]=0x0;
	cardsExtraArr[0]=0x0;
}
uint testCount=0;
uchar cardNormalOrRingIndex=0,cardNoDiv8,cardNoMod8;
void main() {
	LED_TEST0=0;
	LED_TEST1=0;
	//showMode=DISPLAY_MESSAGE;
	testInit();
	init();
	
    RX_Mode(READER_ADDRESS, READER_ADDR_LEN);
//	LCD_PutString(1,3,"t");
//	LCD_PutString(1,4,"e");
//	LCD_PutString(2,3,"s");
    
	WDT_CONTR = 0x3f;//���ÿ��Ź������ʱ��9.1022s
	while (1) {
		
	    sta=SPI_Read(STATUS);
        if((sta&0x40)==0x40)
        {   
			LED_TEST1=~LED_TEST1;
            SPI_Read_Buffer(RD_RX_PLOAD, cardAddr, DATA_LEN);
			SPI_Write_Read(FLUSH_RX);
			SPI_Write_Read_Register(WRITE_REG + STATUS,0xff);  // ���RX_DS�жϱ�־*/
//			if(cardAddr[0]==0x00){
				cardAddrInt=cardAddr[2]+((uint)cardAddr[1])<<7;
				cardNoDiv8=cardAddrInt/8;
				cardNoMod8=cardAddrInt%8;
				receivedFlag=(receivedCardNoAndRing[cardNoDiv8]>>cardNoMod8)&1;
				if(receivedFlag==0)
				{
					receivedCardNoAndRing[cardNoDiv8]|=(1>>cardNoMod8);
					if(currentCardsInfoIndex<CARDS_ARR_LEN)
					{
						cardsInfoArr[currentCardsInfoIndex+0]=0;
						cardsInfoArr[currentCardsInfoIndex+1]=cardAddr[1];
						cardsInfoArr[currentCardsInfoIndex+2]=cardAddr[2];
						currentCardsInfoIndex+=3; //Ϊspi��ȡ��Ա����׼ȷ��������һ����Ա��Ϣ����ͳһ�������ۼӡ�
					}
				}
//			}
//			else if(currentCardsExtraIndex<CARDS_EXTRA_ARR_LEN)
			if(cardAddr[0]!=0x00&&currentCardsExtraIndex<CARDS_EXTRA_ARR_LEN)
			{
				//	LED_TEST0=~LED_TEST0;
					cardsExtraArr[currentCardsExtraIndex+0]=cardAddr[0];
					cardsExtraArr[currentCardsExtraIndex+1]=cardAddr[1];
					cardsExtraArr[currentCardsExtraIndex+2]=cardAddr[2];
					currentCardsExtraIndex+=3;
			}
			
		
		
		


      }
		else{

			SPI_Read_Buffer(CONFIG, rx_buf, 1);
			if(rx_buf[0]==0x3f){
		//		LED_TEST0=1;
			}
			else{
		//		LED_TEST0=0;
				
	            RX_Mode(READER_ADDRESS, READER_ADDR_LEN);
			}
		}
	
	
		if(reqCardsFlag==1){
	//		LED_TEST0=~LED_TEST0;
			sendCardsInfo();
			reqCardsFlag=0;
			
		}

	
	//	LED_TST2=0;
		WDT_CONTR = 0x3f;//���ÿ��Ź������ʱ��9.1022s
		
		
	}

}



