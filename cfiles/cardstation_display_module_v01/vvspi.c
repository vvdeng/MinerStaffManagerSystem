#include "vvspi.h"

void init_spi()
{
	//SSIG = 1;   //����SS��
	//SPEN = 1;   //����SPI����
	//DORD = 0;   //�ȴ���λMSB
	//MSTR = 1;   //���õ�Ƭ��Ϊ����
	SPDAT=0;
	SPSTAT = SPIF | WCOL;         //clear SPI status
	SPCTL = 0x40; //�ӷ�ʽSPI Control Register SSIG SPEN DORD MSTR CPOL CPHA SPR1 SPR0 0000,0100
//	SPSTAT = 0xC0; //
	IE2 |= 0x02; //����SPI�жϿ���λ
}

void WriteByte(u8 temp)
{
	SPDAT = temp;
	while(!(SPSTAT & 0x80));
	SPSTAT = 0xC0;
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

