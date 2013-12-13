#ifndef _CONSTANT_H_
#define _CONSTANT_H_
#define READER_CARDNUM 80  //每个读卡器最大容量
#define READER_CARD_EXTRA_NUM 20
#define CARD_INFO_BYTES 3  //每个标识卡信息3个字节
#define CARDS_ARR_LEN ((READER_CARDNUM+READER_CARD_EXTRA_NUM)*CARD_INFO_BYTES)
sfr WDT_CONTR = 0xc1; //看门狗地址

sfr     AUXR   =   0x8e;                         
sfr S2CON = 0x9a;               //UART2 控制寄存器
sfr S2BUF = 0x9b;               //UART2 数据寄存器
sfr T2H   = 0xd6;               //定时器2高8位
sfr T2L   = 0xd7;               //定时器2低8位
sfr   IE2     = 0xaf;  //中断允许寄存器2 最低位为ES2  
sfr IPH    = 0xB7;          
#define   S2RI    0x01                 
#define   S2TI    0x02            

#define SPI_START 0x80
#define SYM_END 0xFE
#define SEG_COUNT 8

#define MAX_CALL_STAFF_COUNT 12
#define CALL_STAFF_BUF_LEN (MAX_CALL_STAFF_COUNT*2+1) //第0字节存储人数

#define SET_TIME_BUF_LEN 8
//#define UP_TIMEOUT_THRESHOLD 12000  //6秒
#define UP_TIMEOUT_THRESHOLD 24000  //12秒
#define SIGN_CARD_NORMAL 0x00  //
#define SIGN_CARD_ALERT 0x01   //呼叫
#define SIGN_ALL_CARD_ALERT 0x02 
#define SIGN_CARD_BAT_LOW 0x04 		 //欠压
#endif
