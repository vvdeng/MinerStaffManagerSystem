
#include "ds1302.h"
uchar xdata initTimeBuf[SET_TIME_LEN] = {20,10,6,5,12,55,07,6};//空年月日时分秒周
uchar xdata timeBuf[SET_TIME_LEN];
uchar xdata sendTimeBuf[SEND_TIME_LEN] = {0};//空年月日时分秒周
                      //空年月日时分秒周
uchar xdata showTimeBuf[SHOW_TIME_LEN]={0};
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
     
    uchar i,tmp;
	for(i=0;i<8;i++)
	    {                  //BCD处理
		tmp=initTimeBuf[i]/10;
		timeBuf[i]=initTimeBuf[i]%10;
		timeBuf[i]=timeBuf[i]+tmp*16;
	    }
	dsWriteByte(ds1302_control_add,0x00);			//关闭写保护 
	dsWriteByte(ds1302_sec_add,0x80);				//暂停 
	//dsWriteByte(ds1302_charger_add,0xa9);			//涓流充电 
	dsWriteByte(ds1302_year_add,timeBuf[1]);		//年 
	dsWriteByte(ds1302_month_add,timeBuf[2]);	//月 
	dsWriteByte(ds1302_date_add,timeBuf[3]);		//日 
	dsWriteByte(ds1302_day_add,timeBuf[7]);		//周 
	dsWriteByte(ds1302_hr_add,timeBuf[4]);		//时 
	dsWriteByte(ds1302_min_add,timeBuf[5]);		//分
	dsWriteByte(ds1302_sec_add,timeBuf[6]);		//秒
	dsWriteByte(ds1302_day_add,timeBuf[7]);		//周 
	dsWriteByte(ds1302_control_add,0x80);			//打开写保护 
}

/*------------------------------------------------
           从DS1302读出时钟数据
------------------------------------------------*/
void dsReadTime(void)  
{ 
   	uchar m,tmp;
/*	timeBuf[1]=dsReadByte(ds1302_year_add);		//年 
	timeBuf[2]=dsReadByte(ds1302_month_add);		//月 
	timeBuf[3]=dsReadByte(ds1302_date_add);		//日 
	timeBuf[4]=dsReadByte(ds1302_hr_add);		//时 
	timeBuf[5]=dsReadByte(ds1302_min_add);		//分 
	timeBuf[6]=(dsReadByte(ds1302_sec_add))&0x7F;//秒 
	timeBuf[7]=dsReadByte(ds1302_day_add);		//周 
*/
	timeBuf[0]=dsReadByte(ds1302_hr_add);		//时 
	timeBuf[1]=dsReadByte(ds1302_min_add);		//分 
	timeBuf[2]=dsReadByte(ds1302_sec_add);//秒
	for(m=0;m<SEND_TIME_LEN;m++)
   {           //BCD处理
		tmp=timeBuf[m]/16;
		sendTimeBuf[m]=timeBuf[m]%16;
		sendTimeBuf[m]=sendTimeBuf[m]+tmp*10;
		showTimeBuf[2*m]=timeBuf[m]/16;
	    showTimeBuf[2*m+1]=timeBuf[m]%16;
   }
		
 
}

/*------------------------------------------------
                DS1302初始化
------------------------------------------------*/
void dsInit(void)
{
	
	RST=0;			//RST脚置低
	SCK=0;			//SCK脚置低
    dsWriteByte(ds1302_sec_add,0x00);				 
}

void resetInitTimeBuf(uchar timeArr[]){
	uchar m;
	for(m=0;m<SET_TIME_LEN;m++){
		initTimeBuf[m]=timeArr[m];
	}
	dsWriteTime();
}
