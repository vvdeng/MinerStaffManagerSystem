#include "vvspi.h"
#include "useful.h"
void init_spi()
{
	//SSIG = 1;   //����SS��
	//SPEN = 1;   //����SPI����
	//DORD = 0;   //�ȴ���λMSB
	//MSTR = 1;   //���õ�Ƭ��Ϊ����
	SPCTL = 0xD0; //SPI Control Register SSIG SPEN DORD MSTR CPOL CPHA SPR1 SPR0 0000,0100
	SPSTAT = 0xC0; //
	//IE2 |= 0x02; //����SPI�жϿ���λ
}

void WriteByte(u8 temp)
{
//	CS=0;
	SPDAT = temp;
	while(!(SPSTAT & 0x80));
	SPSTAT = 0xC0;
//	CS=1;
//	delayMs(1);
}

u8 ReadByte(void)
{
	u8 temp;
	//SPSTAT = 0xC0;
	SPDAT = 0x00;
	while(!(SPSTAT & 0x80));
	temp = SPDAT;
	SPSTAT = 0xC0;
	return temp;
}
u8 WriteReadByte(u8 temp)
{
	u8 result;
	//SPSTAT = 0xC0;
	SPDAT = temp;
	while(!(SPSTAT & 0x80));
	result = SPDAT;
	SPSTAT = 0xC0;
	return result;
}
