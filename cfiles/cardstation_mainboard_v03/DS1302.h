
#ifndef __DS1302_H__
#define __DS1302_H__

#include <reg52.h>
#include<intrins.h>
#include "useful.h"
sfr P4=0xC0;
sbit SCK=P4^3;		
sbit SDA=P4^1;		
sbit RST=P4^0;

#define ds1302_sec_add			0x80		//�����ݵ�ַ
#define ds1302_min_add			0x82		//�����ݵ�ַ
#define ds1302_hr_add			0x84		//ʱ���ݵ�ַ
#define ds1302_date_add			0x86		//�����ݵ�ַ
#define ds1302_month_add		0x88		//�����ݵ�ַ
#define ds1302_day_add			0x8a		//�������ݵ�ַ
#define ds1302_year_add			0x8c		//�����ݵ�ַ
#define ds1302_control_add		0x8e		//�������ݵ�ַ
#define ds1302_charger_add		0x90 					 
#define ds1302_clkburst_add		0xbe

#define SET_TIME_LEN 8  //��������ʱ������
#define SHOW_TIME_LEN 6 //ʱʮʱ����ʮ�ָ���ʮ���
#define SEND_TIME_LEN 3 //ʱ����
extern uchar initTimeBuf[SET_TIME_LEN];//��������ʱ������
extern uchar sendTimeBuf[SEND_TIME_LEN];
extern uchar showTimeBuf[SHOW_TIME_LEN];
/*------------------------------------------------
           ��DS1302д��һ�ֽ�����
------------------------------------------------*/
void dsWriteByte(uchar addr, uchar d);
/*------------------------------------------------
           ��DS1302����һ�ֽ�����
------------------------------------------------*/
uchar dsReadByte(uchar addr) ;
/*------------------------------------------------
           ��DS1302д��ʱ������
------------------------------------------------*/
void dsWriteTime(void) ;
/*------------------------------------------------
           ��DS1302����ʱ������
------------------------------------------------*/
void dsReadTime(void)  ;
/*------------------------------------------------
                DS1302��ʼ��
------------------------------------------------*/
void dsInit(void);

void resetInitTimeBuf(uchar timeArr[]);
#endif
