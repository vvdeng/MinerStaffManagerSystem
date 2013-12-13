#ifndef PM25_H_
#define PM25_H_
#include "useful.h"
#include "vvspi.h"
#define SFC_WREN        0x06                    //¥Æ––Flash√¸¡ÓºØ
#define SFC_WRDI        0x04
#define SFC_RDSR        0x05
#define SFC_WRSR        0x01
#define SFC_READ        0x03
#define SFC_FASTREAD    0x0B
#define SFC_RDID        0xAB
#define SFC_PAGEPROG    0x02
#define SFC_RDCR        0xA1
#define SFC_WRCR        0xF1
#define SFC_SECTORER    0xD7
#define SFC_BLOCKER     0xD8
#define SFC_CHIPER      0xC7
bit FlashCheckID();
bit IsFlashBusy();
void FlashWriteEnable();
void FlashErase();
void FlashRead(ulong addr, ulong size, uchar *buffer);
void FlashWrite(ulong addr, ulong size, uchar *buffer);
extern bit g_fFlashOK;  
#endif