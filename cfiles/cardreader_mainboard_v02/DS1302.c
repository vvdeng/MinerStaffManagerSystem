
#include "ds1302.h"
uchar xdata initTimeBuf[SET_TIME_LEN] = {20,10,6,5,12,55,07,6};//��������ʱ������
uchar xdata timeBuf[SET_TIME_LEN];
uchar xdata sendTimeBuf[SEND_TIME_LEN] = {0};//��������ʱ������
                      //��������ʱ������
uchar xdata showTimeBuf[SHOW_TIME_LEN]={0};
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
     
    uchar i,tmp;
	for(i=0;i<8;i++)
	    {                  //BCD����
		tmp=initTimeBuf[i]/10;
		timeBuf[i]=initTimeBuf[i]%10;
		timeBuf[i]=timeBuf[i]+tmp*16;
	    }
	dsWriteByte(ds1302_control_add,0x00);			//�ر�д���� 
	dsWriteByte(ds1302_sec_add,0x80);				//��ͣ 
	//dsWriteByte(ds1302_charger_add,0xa9);			//������ 
	dsWriteByte(ds1302_year_add,timeBuf[1]);		//�� 
	dsWriteByte(ds1302_month_add,timeBuf[2]);	//�� 
	dsWriteByte(ds1302_date_add,timeBuf[3]);		//�� 
	dsWriteByte(ds1302_day_add,timeBuf[7]);		//�� 
	dsWriteByte(ds1302_hr_add,timeBuf[4]);		//ʱ 
	dsWriteByte(ds1302_min_add,timeBuf[5]);		//��
	dsWriteByte(ds1302_sec_add,timeBuf[6]);		//��
	dsWriteByte(ds1302_day_add,timeBuf[7]);		//�� 
	dsWriteByte(ds1302_control_add,0x80);			//��д���� 
}

/*------------------------------------------------
           ��DS1302����ʱ������
------------------------------------------------*/
void dsReadTime(void)  
{ 
   	uchar m,tmp;
/*	timeBuf[1]=dsReadByte(ds1302_year_add);		//�� 
	timeBuf[2]=dsReadByte(ds1302_month_add);		//�� 
	timeBuf[3]=dsReadByte(ds1302_date_add);		//�� 
	timeBuf[4]=dsReadByte(ds1302_hr_add);		//ʱ 
	timeBuf[5]=dsReadByte(ds1302_min_add);		//�� 
	timeBuf[6]=(dsReadByte(ds1302_sec_add))&0x7F;//�� 
	timeBuf[7]=dsReadByte(ds1302_day_add);		//�� 
*/
	timeBuf[0]=dsReadByte(ds1302_hr_add);		//ʱ 
	timeBuf[1]=dsReadByte(ds1302_min_add);		//�� 
	timeBuf[2]=dsReadByte(ds1302_sec_add);//��
	for(m=0;m<SEND_TIME_LEN;m++)
   {           //BCD����
		tmp=timeBuf[m]/16;
		sendTimeBuf[m]=timeBuf[m]%16;
		sendTimeBuf[m]=sendTimeBuf[m]+tmp*10;
		showTimeBuf[2*m]=timeBuf[m]/16;
	    showTimeBuf[2*m+1]=timeBuf[m]%16;
   }
		
 
}

/*------------------------------------------------
                DS1302��ʼ��
------------------------------------------------*/
void dsInit(void)
{
	
	RST=0;			//RST���õ�
	SCK=0;			//SCK���õ�
    dsWriteByte(ds1302_sec_add,0x00);				 
}

void resetInitTimeBuf(uchar timeArr[]){
	uchar m;
	for(m=0;m<SET_TIME_LEN;m++){
		initTimeBuf[m]=timeArr[m];
	}
	dsWriteTime();
}
