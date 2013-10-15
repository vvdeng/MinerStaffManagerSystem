#include "display.h"
uchar xdata displayBuf[DISPLAY_BUF_LEN]={0};
void sendDisplay(){
	uchar m;
		CS_DISP=0;
		WriteByte(SPI_START);
		delayUs(5);
		for(m=0;m<DISPLAY_BUF_LEN;m++){
			WriteByte(displayBuf[m]);
			delayUs(5);
		}			
		WriteByte(SYM_END);
		CS_DISP=1;
}
void initDisplayBuf(){
	uchar m;
	
	for(m=0;m<DISPLAY_BUF_LEN;m++){
		displayBuf[m]=0;
	}
	displayBuf[DOT_POS_INDEX]=DOT_POS_NONE;//默认不显示小数点	
}
