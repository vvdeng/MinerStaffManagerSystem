#include "nrf24.h"
uchar tx_buf[2]={0x05,0x05};
uchar rx_buf[3]={0};


uchar  sta;

/*根据SPI协议，写一字节数据到nRF24L01，同时从nRF24L01读出一字节*/
uchar SPI_Write_Read(uchar Byte)
{
    uchar i;
    for(i=0;i<8;i++)        // 循环8次
    {
        MOSI=(Byte&0x80);   // byte最高位输出到MOSI
        Byte<<=1;           // 低一位移位到最高位
        SCLK=1;             // 拉高SCK，nRF24L01从MOSI读入1位数据，同时从MISO输出1位数据
        Byte |=MISO;        // 读MISO到byte最低位
        SCLK=0;             // SCK置低
    }
    return Byte;            // 返回读出的一字节
}
/*写数据value到reg寄存器*/
uchar SPI_Write_Read_Register(uchar Reg,uchar value)
{
    uchar Status;
    CSN=0;                        // CSN置低，开始传输数据
    Status=SPI_Write_Read(Reg);   // 选择寄存器，同时返回状态字
    SPI_Write_Read(value);        // 然后写数据到该寄存器
    CSN=1;                        // CSN拉高，结束数据传输
    return Status;                // 返回状态寄存器
}
/*从reg寄存器读一字节*/
uchar SPI_Read(uchar Reg)
{
    uchar result;
    CSN=0;                       // CSN置低，开始传输数据
    SPI_Write_Read(Reg);         // 选择寄存器
    result=SPI_Write_Read(0);    // 然后从该寄存器读数据
    CSN=1;                       // CSN拉高，结束数据传输
    return result;               // 返回寄存器数据
}
/*从reg寄存器读出bytes个字节，通常用来读取接收通道数据或接收/发送地址*/
uchar SPI_Read_Buffer(uchar Reg,uchar *Buf,uchar Bytes)
{
    uchar Status,i;
    CSN=0;                       // CSN置低，开始传输数据
    Status=SPI_Write_Read(Reg);  // 选择寄存器，同时返回状态字
    for(i=0;i<Bytes;i++)
    {
        Buf[i]=SPI_Write_Read(0);// 逐个字节从nRF24L01读出
    }
    CSN=1;                       // CSN拉高，结束数据传输
    return Status;               // 返回状态寄存器
}
/*把pBuf缓存中的数据写入到nRF24L01，通常用来写入发射通道数据或接收/发送地址*/
uchar SPI_Write_Buffer(uchar Reg,uchar *Buf,uchar Bytes)
{
    uchar Status,i;
    CSN=0;                       // CSN置低，开始传输数据
    Status=SPI_Write_Read(Reg);  // 选择寄存器，同时返回状态字
    for(i=0;i<Bytes;i++)
    {
        SPI_Write_Read(Buf[i]);  // 逐个字节写入nRF24L01
    }
    CSN=1;                       // CSN拉高，结束数据传输
    return Status;               // 返回状态寄存器
}

/*设置nRF24L01为接收模式，等待接收发送设备的数据包*/
void RX_Mode(uchar *localAddr,uchar addrLen)
{	
	CE=0;
 	CSN=1;  //SPI标止
    SCLK=0; //SPI时钟置低
	delayUs(20);
//    SPI_Write_Buffer(WRITE_REG + TX_ADDR, TX_ADDRESS, TX_ADR_WIDTH);    // 写入发送地址
    SPI_Write_Read_Register(WRITE_REG + EN_AA, 0x00);                   // 失能接收通道0自动应答
//  SPI_Write_Read_Register(WRITE_REG + EN_RXADDR, 0x00);               // 失能接收通道0
    SPI_Write_Read_Register(WRITE_REG + SETUP_RETR, 0x00);              // 失能自动重发
    SPI_Write_Read_Register(WRITE_REG + RF_CH, 0x01);                       // 选择射频通道0x00
    SPI_Write_Read_Register(WRITE_REG + RF_SETUP, 0x07);                // 数据传输率1Mbps，发射功率0dBm，低噪声放大器增益
//  SPI_Write_Read_Register(WRITE_REG + CONFIG, 0x7e);                  // CRC使能，16位CRC校验，上电
//  CE=1;
//  delayUs(200);   
    CE=0;
    SPI_Write_Buffer(WRITE_REG + RX_ADDR_P0, localAddr, addrLen);// 接收设备接收通道0使用和发送设备相同的发送地址
	SPI_Write_Read_Register(WRITE_REG + RX_PW_P0, DATA_LEN);     // 接收通道0选择和发送通道相同有效数据宽度
    SPI_Write_Read_Register(WRITE_REG + EN_RXADDR, 0x01);              // 使能接收通道0
    SPI_Write_Read_Register(WRITE_REG + CONFIG, 0x3f);                 // 屏蔽除去接收信号外的中断，CRC使能，16位CRC校验，上电，接收模式
    CE=1;                                       					   // 拉高CE启动接收设备
	delayUs(200);
}

void nRF24L01_TxPacket(uchar * addr,uchar addrLen,uchar *datas,uchar datasLen)
{   
    CE=0;
 	CSN=1;  //SPI标止
    SCLK=0; //SPI时钟置低
	delayUs(20);
//    SPI_Write_Buffer(WRITE_REG + TX_ADDR, TX_ADDRESS, TX_ADR_WIDTH);    // 写入发送地址
    SPI_Write_Read_Register(WRITE_REG + EN_AA, 0x00);                   // 失能接收通道0自动应答
//  SPI_Write_Read_Register(WRITE_REG + EN_RXADDR, 0x00);               // 失能接收通道0
    SPI_Write_Read_Register(WRITE_REG + SETUP_RETR, 0x00);              // 失能自动重发
    SPI_Write_Read_Register(WRITE_REG + RF_CH, 0x01);                       // 选择射频通道0x00
    SPI_Write_Read_Register(WRITE_REG + RF_SETUP, 0x07);                // 数据传输率1Mbps，发射功率0dBm，低噪声放大器增益
//  SPI_Write_Read_Register(WRITE_REG + CONFIG, 0x7e);                  // CRC使能，16位CRC校验，上电
//  CE=1;
//  delayUs(200);   
	CE=0;           //StandBy I模式 
    SPI_Write_Buffer(WRITE_REG + TX_ADDR, addr, addrLen);    // 写入发送地址              
    SPI_Write_Buffer(WR_TX_PLOAD,datas, datasLen);  // 装载数据	
    SPI_Write_Read_Register(WRITE_REG + CONFIG, 0x7e);            // IRQ收发完成中断响应，16位CRC，主发送
    CE=1;        //置高CE，激发数据发送
    delayUs(200);
	SPI_Write_Read(FLUSH_TX);
	SPI_Write_Read_Register(WRITE_REG + STATUS, 0xff); // 清除TX_DS或MAX_RT中断标志
}
