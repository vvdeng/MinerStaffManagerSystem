#ifndef _NRF24_H_
#define _NRF24_H_

#include <reg52.h>
#include "useful.h"
#define READ_REG        0x00  // Define read command to register 
#define WRITE_REG       0x20  // Define write command to register 
#define RD_RX_PLOAD     0x61  // Define RX payload register address 
#define WR_TX_PLOAD     0xA0  // Define TX payload register address 
#define FLUSH_TX        0xE1  // Define flush TX register command 
#define FLUSH_RX        0xE2  // Define flush RX register command 
#define REUSE_TX_PL     0xE3  // Define reuse TX payload register command 
#define NOP              0xFF  // Define No Operation, might be used to read status register  
#define CONFIG          0x00  // 'Config' register address
#define EN_AA           0x01  // 'Enable Auto Acknowledgment' register address 
#define EN_RXADDR       0x02  // 'Enabled RX addresses' register address 
#define SETUP_AW        0x03  // 'Setup address width' register address 
#define SETUP_RETR            0x04    // 'Setup Auto. Retrans' register address 
#define RF_CH           0x05  // 'RF channel' register address 
#define RF_SETUP        0x06  // 'RF setup' register address 
#define STATUS          0x07  // 'Status' register address 
#define OBSERVE_TX      0x08  // 'Observe TX' register address 
#define CD              0x09  // 'Carrier Detect' register address 
#define RX_ADDR_P0            0x0A    // 'RX address pipe0' register address 
#define RX_ADDR_P1            0x0B    // 'RX address pipe1' register address 
#define RX_ADDR_P2            0x0C    // 'RX address pipe2' register address 
#define RX_ADDR_P3            0x0D    // 'RX address pipe3' register address 
#define RX_ADDR_P4            0x0E    // 'RX address pipe4' register address 
#define RX_ADDR_P5            0x0F    // 'RX address pipe5' register address 
#define TX_ADDR         0x10  // 'TX address' register address 
#define RX_PW_P0                0x11    // 'RX payload width, pipe0' register address 
#define RX_PW_P1                0x12    // 'RX payload width, pipe1' register address 
#define RX_PW_P2                0x13    // 'RX payload width, pipe2' register address 
#define RX_PW_P3                0x14    // 'RX payload width, pipe3' register address 
#define RX_PW_P4                0x15    // 'RX payload width, pipe4' register address 
#define RX_PW_P5                0x16    // 'RX payload width, pipe5' register address 
#define FIFO_STATUS          0x17    // 'FIFO Status Register' register address

#define READER_ADDR_LEN 5

#define DATA_LEN 3
/*sbit CE = P2^4;            
sbit CSN = P2^3; 
sbit SCLK = P2^0;            
sbit MISO = P2^1;           
sbit MOSI = P2^2;           
sbit IRQ=P3^3;
//sbit IRQ=P2^5;
*/
///for 无线模块电路板
/*sbit CE = P3^5;            
sbit CSN = P1^0; 
sbit SCLK = P1^3;            
sbit MISO = P1^2;           
sbit MOSI = P1^1;           
sbit IRQ=P3^3;
*/
sbit CE = P2^4;            
sbit CSN = P2^3; 
sbit SCLK = P2^2;  
sbit MOSI = P2^1;          
sbit MISO = P2^0;           
           
sbit IRQ=P3^2;
uchar SPI_Write_Read(uchar Byte);
uchar SPI_Write_Read_Register(uchar Reg,uchar value);
uchar SPI_Read(uchar Reg); 
uchar SPI_Read_Buffer(uchar Reg,uchar *Buf,uchar Bytes);
uchar SPI_Write_Buffer(uchar Reg,uchar *Buf,uchar Bytes);

extern uchar tx_buf[2];
extern uchar rx_buf[3];

extern uchar  sta;

void RX_Mode(uchar *localAddr,uchar addrLen); 
void nRF24L01_TxPacket(uchar * addr,uchar addrLen,uchar *datas,uchar datasLen);
#endif
