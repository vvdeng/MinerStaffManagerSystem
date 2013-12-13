#include "pm25.h"
bit g_fFlashOK;  
/************************************************
检测Flash是否准备就绪
入口参数: 无
出口参数:
    0 : 没有检测到正确的Flash
    1 : Flash准备就绪
************************************************/
bit FlashCheckID()
{
    uchar dat1, dat2;
    
    CS_PM25 = 0;
    WriteReadByte(SFC_RDID);                         //发送读取ID命令
    WriteReadByte(0x00);                             //空读3个字节
    WriteReadByte(0x00);
    WriteReadByte(0x00);
    dat1 = WriteReadByte(0x00);                      //读取制造商ID1
    WriteReadByte(0x00);                             //读取设备ID
    dat2 = WriteReadByte(0x00);                      //读取制造商ID2
    CS_PM25 = 1;
                                                //检测是否为PM25LVxx系列的Flash
    g_fFlashOK = ((dat1 == 0x9d) && (dat2 == 0x7f));
    
    return g_fFlashOK;
}

/************************************************
检测Flash的忙状态
入口参数: 无
出口参数:
    0 : Flash处于空闲状态
    1 : Flash处于忙状态
************************************************/
bit IsFlashBusy()
{
    uchar dat;
    
    CS_PM25 = 0;
	delayUs(10);
    WriteReadByte(SFC_RDSR);                         //发送读取状态命令
    dat = WriteReadByte(0);                          //读取状态
    CS_PM25 = 1;
    delayUs(10);
    return (dat & 0x01);                        //状态值的Bit0即为忙标志
}

/************************************************
使能Flash写命令
入口参数: 无
出口参数: 无
************************************************/
void FlashWriteEnable()
{
    while (IsFlashBusy());                      //Flash忙检测
    CS_PM25 = 0;
	delayUs(10);
    WriteReadByte(SFC_WREN);                         //发送写使能命令
    CS_PM25 = 1;
	delayUs(10);
}

/************************************************
擦除整片Flash
入口参数: 无
出口参数: 无
************************************************/
void FlashErase()
{
    if (g_fFlashOK)
    {
        FlashWriteEnable();                     //使能Flash写命令
        CS_PM25 = 0;
		delayUs(10);
        WriteReadByte(SFC_CHIPER);                   //发送片擦除命令
        CS_PM25 = 1;
		delayUs(10);
    }
}

/************************************************
从Flash中读取数据
入口参数:
    addr   : 地址参数
    size   : 数据块大小
    buffer : 缓冲从Flash中读取的数据
出口参数:
    无
************************************************/
/*
void FlashFastRead(ulong addr, ulong size, uchar *buffer)
{
    if (g_fFlashOK)
    {
        while (IsFlashBusy());                  //Flash忙检测
        CS_PM25 = 0;
		delayUs(10);
	//	WriteReadByte(SFC_READ);
      WriteReadByte(SFC_FASTREAD);                 //使用快速读取命令
        WriteReadByte(((uchar *)&addr)[1]);           //设置起始地址
        WriteReadByte(((uchar *)&addr)[2]);
        WriteReadByte(((uchar *)&addr)[3]);
        WriteReadByte(0);                            //需要空读一个字节
        while (size)
        {
            *buffer = WriteReadByte(0);              //自动连续读取并保存
            addr++;
            buffer++;
            size--;
        }
        CS_PM25 = 1;
		delayUs(10);
    }
}
*/
/************************************************
从Flash中读取数据
入口参数:
    addr   : 地址参数
    size   : 数据块大小
    buffer : 缓冲从Flash中读取的数据
出口参数:
    无
************************************************/
void FlashRead(ulong addr, ulong size, uchar *buffer)
{
    if (g_fFlashOK)
    {
        while (IsFlashBusy());                  //Flash忙检测
        CS_PM25 = 0;
		delayUs(10);
		WriteReadByte(SFC_READ);
    //  WriteReadByte(SFC_FASTREAD);                 //使用快速读取命令
        WriteReadByte(((uchar *)&addr)[1]);           //设置起始地址
        WriteReadByte(((uchar *)&addr)[2]);
        WriteReadByte(((uchar *)&addr)[3]);
    //    WriteReadByte(0);                            //需要空读一个字节
        while (size)
        {
            *buffer = WriteReadByte(0);              //自动连续读取并保存
            addr++;
            buffer++;
            size--;
        }
        CS_PM25 = 1;
		delayUs(10);
    }
}
/************************************************
写数据到Flash中
入口参数:
    addr   : 地址参数
    size   : 数据块大小
    buffer : 缓冲需要写入Flash的数据
出口参数: 无
************************************************/
void FlashWrite(ulong addr, ulong size, uchar *buffer)
{
    if (g_fFlashOK)
    {
		while (IsFlashBusy());                  //Flash忙检测
		while (size)
	    {

	        FlashWriteEnable();                     //使能Flash写命令
	        CS_PM25 = 0;
			delayUs(10);
	        WriteReadByte(SFC_PAGEPROG);                 //发送页编程命令
	        WriteReadByte(((uchar *)&addr)[1]);           //设置起始地址
	        WriteReadByte(((uchar *)&addr)[2]);
	        WriteReadByte(((uchar *)&addr)[3]);
	        while (size)
	        {
	            WriteReadByte(*buffer);                  //连续页内写
	            addr++;
	            buffer++;
	            size--;
	            if ((addr & 0xff) == 0) break;
	        }
	        CS_PM25 = 1;
			delayUs(10);
	    }
	}
}
void eraseSector(ulong addr){
		FlashWriteEnable(); 
		CS_PM25 = 0;
		delayUs(10);
		WriteReadByte(SFC_SECTORER);
		WriteReadByte(((uchar *)&addr)[1]);           //设置起始地址
		WriteReadByte(((uchar *)&addr)[2]);
		WriteReadByte(((uchar *)&addr)[3]);
		CS_PM25 = 1;
		delayUs(10);
		delayMs(50);
	
}

void resetFlash(){
	//g_fFlashOK=0;
	g_fFlashOK=1;// 默认使flash直接有效，不做检测
}


