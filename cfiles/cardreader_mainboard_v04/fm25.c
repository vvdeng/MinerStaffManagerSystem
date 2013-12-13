#include "fm25.h"

void enableCS(){
	CS_PM25=0;
}
void disableCS(){
	CS_PM25=1;
}
void writeEN(){
	enableCS();
	WriteByte(FRAM_WREN);
	disableCS();
}
void writeDN(){
	enableCS();
	WriteByte(FRAM_WRDI);
	disableCS();
}
void writeStatusReg(uchar sta){
	writeEN();
	enableCS();
	WriteByte(FRAM_WRSR);
	WriteByte(sta);
	disableCS();
//	writeDN();
}
uchar readStatusReg(void){
	uchar sta;
	delayUs(10);
	enableCS();
	WriteByte(FRAM_RDSR);
	sta=ReadByte();
	disableCS();

	return sta;
}
void writeMem(uint addr,uchar dat){
	uchar addrHigh,addrLow;
	addrHigh=(addr&0xff00)>>8;
	addrLow=(addr&0xff);
	writeEN();
	enableCS();
	delayUs(10);
	WriteByte(FRAM_WR_REM);
	WriteByte(addrHigh);
	WriteByte(addrLow);
	WriteByte(dat);
	disableCS();
	delayUs(10);
//	writeDN();
}
uchar readMem(uint addr){
	uchar sta;
	uchar addrHigh,addrLow;
	addrHigh=(addr&0xff00)>>8;
	addrLow=(addr&0xff);

	enableCS();
	delayUs(10);
	WriteByte(FRAM_RD_REM);
	WriteByte(addrHigh);
	WriteByte(addrLow);
	sta=ReadByte();
	disableCS();

	return sta;
}
void writeNextLocation(uint location)
{
     uchar temp = 0;
     temp = location&0x00ff;
     writeMem(NEXT_LOCATION_LOW,temp);
     temp = (location&0xff00)>>8;
     writeMem(NEXT_LOCATION_HIGH,temp);
}

uint readNextLocation(void)
{
     uint location=0;
     location = readMem(NEXT_LOCATION_HIGH);
     location = (location<<8)+ readMem(NEXT_LOCATION_LOW);
     return location;
}

