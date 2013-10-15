
#include<reg52.h> 
#include "vvspi.h"
#include "useful.h"

#define DOT_POS_NONE 8
#define DIGIT_NONE 20 //约定数字等于20时 不显示该位数字
#define DISPLAY_BUF_LEN 9 //1个小数点位置 8个数字
sfr P0M1=0x93;
sfr P0M0=0x94;




uchar code shumaMap[18]={0x3f,0x06,0x5b,0x4f,0x66,0x6d,0x7d,0x07,0x7f,0x6f,0x77,0x7c,0x39,0x5e,0x79,0x71,0x3d,0x76};
uchar code shumaDotMap[18]={0xbf,0x86,0xdb,0xcf,0xe6,0xed,0xfd,0x87,0xff,0xef,0xf7,0xfc,0xb9,0xde,0xf9,0xf1,0xbd,0xf6};
uchar duanInteval[4]={3,12,48,192};
unsigned char code posCodeArr[]={0xfe,0xfd,0xfb,0xf7,0xef,0xdf,0xbf,0x7f};
#define DOT_POS_INDEX 8 //在spiBuf数组中的索引
#define SEG_COUNT 6 //6位数码管
unsigned char digiBuf[SEG_COUNT]; //存储显示值的全局变量
uchar dotPos=DOT_POS_NONE;


void displayShuma(uchar digi[],uchar len){
	uchar m,n,disSeg;
	for(m=0;m<len;m++){
		
		P0=0; //消影
	//	P2=0xff-(0x0|(1<<m));
		P2=posCodeArr[m];
	// 	delayMs(1);
		delayUs(500);
	 	if(m==dotPos){
			disSeg=shumaDotMap[digi[m]];
		}
		else{
			disSeg=shumaMap[digi[m]];
		}	
		for(n=0;n<4;n++){
			P0=disSeg&duanInteval[n];
			delayUs(200);
		}
	}

}



#define SYM_START 0x80
#define SYM_END 0xFE
#define SPI_START 0x80
#define SPI_STATE_NONE 0
#define SPI_STATE_FINISHED 1
uchar spiState=SPI_STATE_NONE;
uchar  spiBufIndex=0;
uchar xdata spiBuf[15]={0};
void spi_isr( ) interrupt 9 using  1         //SPI interrupt routine 9 (004BH)
{

      SPSTAT = SPIF | WCOL;         //clear SPI status
	  switch(SPDAT){
	  case SYM_START:
	  	spiBufIndex=0;
		break;
	  case SYM_END:
	  	spiState=SPI_STATE_FINISHED;
		break;
	  default:
	  	spiBuf[spiBufIndex]=SPDAT;
		++spiBufIndex;
	  }

      SPDAT =0;                 

}
void init(){
	P0M1=0x00;
	P0M0=0xff;
	P0=0x00;//初始化清零，高有效
	P2=0xff;//初始化清零，低有效
	init_spi();

	EA=1;            //总中断打开
}
void resetDigiBuf(){
   uchar m;
   for(m=0;m<SEG_COUNT;m++){
   		digiBuf[m]=spiBuf[m];
   }
   dotPos=spiBuf[DOT_POS_INDEX];
}
void testInit(){
	uchar m;
	for(m=0;m<SEG_COUNT;m++){
		spiBuf[m]=0;
	}
	
	resetDigiBuf();
	dotPos=DOT_POS_NONE;
}

void main (void)
{
	
	init();
	testInit();
	while(1){
		if(spiState==SPI_STATE_FINISHED){
				
			spiBufIndex=0;
			resetDigiBuf();	
			spiState=SPI_STATE_NONE;
		}
		displayShuma(digiBuf,SEG_COUNT);      // 调用数码管扫描
		
	}

}
