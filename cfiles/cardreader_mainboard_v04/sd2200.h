
#ifndef __SD2200_H__
#define __SD2200_H__

#include <reg52.h>
#include<intrins.h>
#include "useful.h"
sfr P4=0xC0;		
sbit SDA=P2^4;		
sbit SCL=P2^5;	


#define SET_TIME_LEN 8  //年月日周时分秒空
#define SHOW_TIME_LEN 6 //时十时个分十分个秒十秒个
#define SEND_TIME_LEN 6 //时分秒年月日
extern uchar xdata  initTimeBuf[SET_TIME_LEN];//空年月日时分秒周
extern uchar xdata  sendTimeBuf[SEND_TIME_LEN];
extern uchar xdata  showTimeBuf[SHOW_TIME_LEN];
extern uchar xdata  date[7];
/********SD2200函数名********/
void I2CWait(void);
bit  I2CStart(void);
void I2CStop(void);
void I2CAck(void);
void I2CNoAck(void);
bit  I2CWaitAck(void);
void I2CSendByte(uchar demand,bit order);
uchar I2CReceiveByte(void);
void I2CReadDate(void);
void I2CWriteStatus(void);
void dsWriteTime(void) ;

void dsReadTime(void)  ;

void dsInit(void);

void resetInitTimeBuf(uchar timeArr[]);
#endif
