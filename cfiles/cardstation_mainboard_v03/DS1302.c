
#include "ds1302.h"
uchar initTimeBuf[SET_TIME_LEN] = {20,10,6,5,12,55,00,6};//��������ʱ������
uchar timeBuf[SET_TIME_LEN];
uchar sendTimeBuf[SEND_TIME_LEN] = {0};//��������ʱ������
                      //��������ʱ������
uchar showTimeBuf[SHOW_TIME_LEN]={0};
/*------------------------------------------------
           ��DS1302д��һ�ֽ�����
------------------------------------------------*/
void dsWriteByte(uchar addr, uchar d)
{

	uchar i;
	RST=1;	
	
	//д��Ŀ���ַ��addr
	addr = addr & 0xFE;     //���λ����
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
	
	//д�����ݣ�d
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
	RST=0;					//ֹͣDS1302����
}
/*------------------------------------------------
           ��DS1302����һ�ֽ�����
------------------------------------------------*/

uchar dsReadByte(uchar addr) 
{

	uchar i;
	uchar temp;
	RST=1;	

	//д��Ŀ���ַ��addr
	addr = addr | 0x01;//���λ�ø�
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
	
	//������ݣ�temp
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
	
	RST=0;	//ֹͣDS1302����
	return temp;
}

/*------------------------------------------------
           ��DS1302д��ʱ������
------------------------------------------------*/
void dsWriteTime(void) 
{
     
  
}

/*------------------------------------------------
           ��DS1302����ʱ������
------------------------------------------------*/
void dsReadTime(void)  
{ 
 
		
 
}

/*------------------------------------------------
                DS1302��ʼ��
------------------------------------------------*/
void dsInit(void)
{
	
				 
}

void resetInitTimeBuf(uchar timeArr[]){

}
