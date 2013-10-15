
#include "ds1302.h"
uchar initTimeBuf[SET_TIME_LEN] = {20,10,6,5,12,55,00,6};//空年月日时分秒周
uchar timeBuf[SET_TIME_LEN];
uchar sendTimeBuf[SEND_TIME_LEN] = {0};//空年月日时分秒周
                      //空年月日时分秒周
uchar showTimeBuf[SHOW_TIME_LEN]={0};
/*------------------------------------------------
           向DS1302写入一字节数据
------------------------------------------------*/
void dsWriteByte(uchar addr, uchar d)
{

	uchar i;
	RST=1;	
	
	//写入目标地址：addr
	addr = addr & 0xFE;     //最低位置零
	for (i = 0; i < 8; i ++) 
	    { 
		if (addr & 0x01) 
		    {
			SDA=1;
			}
		else 
		    {
			SDA=0;
			}
		SCK=1;
		SCK=0;
		addr = addr >> 1;
		}
	
	//写入数据：d
	for (i = 0; i < 8; i ++) 
	   {
		if (d & 0x01) 
		    {
			SDA=1;
			}
		else 
		    {
			SDA=0;
			}
		SCK=1;
		SCK=0;
		d = d >> 1;
		}
	RST=0;					//停止DS1302总线
}
/*------------------------------------------------
           从DS1302读出一字节数据
------------------------------------------------*/

uchar dsReadByte(uchar addr) 
{

	uchar i;
	uchar temp;
	RST=1;	

	//写入目标地址：addr
	addr = addr | 0x01;//最低位置高
	for (i = 0; i < 8; i ++) 
	    {
	     
		if (addr & 0x01) 
		   {
			SDA=1;
			}
		else 
		    {
			SDA=0;
			}
		SCK=1;
		SCK=0;
		addr = addr >> 1;
		}
	
	//输出数据：temp
	for (i = 0; i < 8; i ++) 
	    {
		temp = temp >> 1;
		if (SDA) 
		   {
			temp |= 0x80;
			}
		else 
		   {
			temp &= 0x7F;
			}
		SCK=1;
		SCK=0;
		}
	
	RST=0;	//停止DS1302总线
	return temp;
}

/*------------------------------------------------
           向DS1302写入时钟数据
------------------------------------------------*/
void dsWriteTime(void) 
{
     
  
}

/*------------------------------------------------
           从DS1302读出时钟数据
------------------------------------------------*/
void dsReadTime(void)  
{ 
 
		
 
}

/*------------------------------------------------
                DS1302初始化
------------------------------------------------*/
void dsInit(void)
{
	
				 
}

void resetInitTimeBuf(uchar timeArr[]){

}
