
#ifndef __DS1302_H__
#define __DS1302_H__

#include <reg52.h>
#include<intrins.h>
#include "useful.h"
sfr P4=0xC0;
sbit SCK=P4^3;		
sbit SDA=P4^1;		
sbit RST=P4^0;

#define ds1302_sec_add			0x80		//秒数据地址
#define ds1302_min_add			0x82		//分数据地址
#define ds1302_hr_add			0x84		//时数据地址
#define ds1302_date_add			0x86		//日数据地址
#define ds1302_month_add		0x88		//月数据地址
#define ds1302_day_add			0x8a		//星期数据地址
#define ds1302_year_add			0x8c		//年数据地址
#define ds1302_control_add		0x8e		//控制数据地址
#define ds1302_charger_add		0x90 					 
#define ds1302_clkburst_add		0xbe

#define SET_TIME_LEN 8  //年月日周时分秒周
#define SHOW_TIME_LEN 6 //时十时个分十分个秒十秒个
#define SEND_TIME_LEN 3 //时分秒
extern uchar initTimeBuf[SET_TIME_LEN];//空年月日时分秒周
extern uchar sendTimeBuf[SEND_TIME_LEN];
extern uchar showTimeBuf[SHOW_TIME_LEN];
/*------------------------------------------------
           向DS1302写入一字节数据
------------------------------------------------*/
void dsWriteByte(uchar addr, uchar d);
/*------------------------------------------------
           从DS1302读出一字节数据
------------------------------------------------*/
uchar dsReadByte(uchar addr) ;
/*------------------------------------------------
           向DS1302写入时钟数据
------------------------------------------------*/
void dsWriteTime(void) ;
/*------------------------------------------------
           从DS1302读出时钟数据
------------------------------------------------*/
void dsReadTime(void)  ;
/*------------------------------------------------
                DS1302初始化
------------------------------------------------*/
void dsInit(void);

void resetInitTimeBuf(uchar timeArr[]);
#endif
