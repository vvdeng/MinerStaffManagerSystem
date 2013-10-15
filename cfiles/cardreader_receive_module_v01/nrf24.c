#include "nrf24.h"
uchar tx_buf[2]={0x05,0x05};
uchar rx_buf[3]={0};


uchar  sta;

/*����SPIЭ�飬дһ�ֽ����ݵ�nRF24L01��ͬʱ��nRF24L01����һ�ֽ�*/
uchar SPI_Write_Read(uchar Byte)
{
    uchar i;
    for(i=0;i<8;i++)        // ѭ��8��
    {
        MOSI=(Byte&0x80);   // byte���λ�����MOSI
        Byte<<=1;           // ��һλ��λ�����λ
        SCLK=1;             // ����SCK��nRF24L01��MOSI����1λ���ݣ�ͬʱ��MISO���1λ����
        Byte |=MISO;        // ��MISO��byte���λ
        SCLK=0;             // SCK�õ�
    }
    return Byte;            // ���ض�����һ�ֽ�
}
/*д����value��reg�Ĵ���*/
uchar SPI_Write_Read_Register(uchar Reg,uchar value)
{
    uchar Status;
    CSN=0;                        // CSN�õͣ���ʼ��������
    Status=SPI_Write_Read(Reg);   // ѡ��Ĵ�����ͬʱ����״̬��
    SPI_Write_Read(value);        // Ȼ��д���ݵ��üĴ���
    CSN=1;                        // CSN���ߣ��������ݴ���
    return Status;                // ����״̬�Ĵ���
}
/*��reg�Ĵ�����һ�ֽ�*/
uchar SPI_Read(uchar Reg)
{
    uchar result;
    CSN=0;                       // CSN�õͣ���ʼ��������
    SPI_Write_Read(Reg);         // ѡ��Ĵ���
    result=SPI_Write_Read(0);    // Ȼ��ӸüĴ���������
    CSN=1;                       // CSN���ߣ��������ݴ���
    return result;               // ���ؼĴ�������
}
/*��reg�Ĵ�������bytes���ֽڣ�ͨ��������ȡ����ͨ�����ݻ����/���͵�ַ*/
uchar SPI_Read_Buffer(uchar Reg,uchar *Buf,uchar Bytes)
{
    uchar Status,i;
    CSN=0;                       // CSN�õͣ���ʼ��������
    Status=SPI_Write_Read(Reg);  // ѡ��Ĵ�����ͬʱ����״̬��
    for(i=0;i<Bytes;i++)
    {
        Buf[i]=SPI_Write_Read(0);// ����ֽڴ�nRF24L01����
    }
    CSN=1;                       // CSN���ߣ��������ݴ���
    return Status;               // ����״̬�Ĵ���
}
/*��pBuf�����е�����д�뵽nRF24L01��ͨ������д�뷢��ͨ�����ݻ����/���͵�ַ*/
uchar SPI_Write_Buffer(uchar Reg,uchar *Buf,uchar Bytes)
{
    uchar Status,i;
    CSN=0;                       // CSN�õͣ���ʼ��������
    Status=SPI_Write_Read(Reg);  // ѡ��Ĵ�����ͬʱ����״̬��
    for(i=0;i<Bytes;i++)
    {
        SPI_Write_Read(Buf[i]);  // ����ֽ�д��nRF24L01
    }
    CSN=1;                       // CSN���ߣ��������ݴ���
    return Status;               // ����״̬�Ĵ���
}

/*����nRF24L01Ϊ����ģʽ���ȴ����շ����豸�����ݰ�*/
void RX_Mode(uchar *localAddr,uchar addrLen)
{	
	CE=0;
 	CSN=1;  //SPI��ֹ
    SCLK=0; //SPIʱ���õ�
	delayUs(20);
//    SPI_Write_Buffer(WRITE_REG + TX_ADDR, TX_ADDRESS, TX_ADR_WIDTH);    // д�뷢�͵�ַ
    SPI_Write_Read_Register(WRITE_REG + EN_AA, 0x00);                   // ʧ�ܽ���ͨ��0�Զ�Ӧ��
//  SPI_Write_Read_Register(WRITE_REG + EN_RXADDR, 0x00);               // ʧ�ܽ���ͨ��0
    SPI_Write_Read_Register(WRITE_REG + SETUP_RETR, 0x00);              // ʧ���Զ��ط�
    SPI_Write_Read_Register(WRITE_REG + RF_CH, 0x01);                       // ѡ����Ƶͨ��0x00
    SPI_Write_Read_Register(WRITE_REG + RF_SETUP, 0x07);                // ���ݴ�����1Mbps�����书��0dBm���������Ŵ�������
//  SPI_Write_Read_Register(WRITE_REG + CONFIG, 0x7e);                  // CRCʹ�ܣ�16λCRCУ�飬�ϵ�
//  CE=1;
//  delayUs(200);   
    CE=0;
    SPI_Write_Buffer(WRITE_REG + RX_ADDR_P0, localAddr, addrLen);// �����豸����ͨ��0ʹ�úͷ����豸��ͬ�ķ��͵�ַ
	SPI_Write_Read_Register(WRITE_REG + RX_PW_P0, DATA_LEN);     // ����ͨ��0ѡ��ͷ���ͨ����ͬ��Ч���ݿ��
    SPI_Write_Read_Register(WRITE_REG + EN_RXADDR, 0x01);              // ʹ�ܽ���ͨ��0
    SPI_Write_Read_Register(WRITE_REG + CONFIG, 0x3f);                 // ���γ�ȥ�����ź�����жϣ�CRCʹ�ܣ�16λCRCУ�飬�ϵ磬����ģʽ
    CE=1;                                       					   // ����CE���������豸
	delayUs(200);
}

void nRF24L01_TxPacket(uchar * addr,uchar addrLen,uchar *datas,uchar datasLen)
{   
    CE=0;
 	CSN=1;  //SPI��ֹ
    SCLK=0; //SPIʱ���õ�
	delayUs(20);
//    SPI_Write_Buffer(WRITE_REG + TX_ADDR, TX_ADDRESS, TX_ADR_WIDTH);    // д�뷢�͵�ַ
    SPI_Write_Read_Register(WRITE_REG + EN_AA, 0x00);                   // ʧ�ܽ���ͨ��0�Զ�Ӧ��
//  SPI_Write_Read_Register(WRITE_REG + EN_RXADDR, 0x00);               // ʧ�ܽ���ͨ��0
    SPI_Write_Read_Register(WRITE_REG + SETUP_RETR, 0x00);              // ʧ���Զ��ط�
    SPI_Write_Read_Register(WRITE_REG + RF_CH, 0x01);                       // ѡ����Ƶͨ��0x00
    SPI_Write_Read_Register(WRITE_REG + RF_SETUP, 0x07);                // ���ݴ�����1Mbps�����书��0dBm���������Ŵ�������
//  SPI_Write_Read_Register(WRITE_REG + CONFIG, 0x7e);                  // CRCʹ�ܣ�16λCRCУ�飬�ϵ�
//  CE=1;
//  delayUs(200);   
	CE=0;           //StandBy Iģʽ 
    SPI_Write_Buffer(WRITE_REG + TX_ADDR, addr, addrLen);    // д�뷢�͵�ַ              
    SPI_Write_Buffer(WR_TX_PLOAD,datas, datasLen);  // װ������	
    SPI_Write_Read_Register(WRITE_REG + CONFIG, 0x7e);            // IRQ�շ�����ж���Ӧ��16λCRC��������
    CE=1;        //�ø�CE���������ݷ���
    delayUs(200);
	SPI_Write_Read(FLUSH_TX);
	SPI_Write_Read_Register(WRITE_REG + STATUS, 0xff); // ���TX_DS��MAX_RT�жϱ�־
}
