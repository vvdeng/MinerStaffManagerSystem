#ifndef _FM25_H_
#define _FM25_H_
#include "vvspi.h"
#include "useful.h"
#define NEXT_LOCATION_HIGH 0x1ffc
#define NEXT_LOCATION_LOW 0x1ffd
#define FRAM_WREN 0x06
#define FRAM_WRDI 0x04
#define FRAM_RDSR 0x05
#define FRAM_WRSR 0x01
#define FRAM_RD_REM 0x03
#define FRAM_WR_REM 0x02
void writeStatusReg(uchar sta);
uchar readStatusReg(void);
void writeMem(uint addr,uchar dat);
uchar readMem(uint addr);
void writeNextLocation(uint location);
uint readNextLocation(void);
#endif
