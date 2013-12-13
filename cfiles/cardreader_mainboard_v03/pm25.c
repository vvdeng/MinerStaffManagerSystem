#include "pm25.h"
bit g_fFlashOK;  
/************************************************
���Flash�Ƿ�׼������
��ڲ���: ��
���ڲ���:
    0 : û�м�⵽��ȷ��Flash
    1 : Flash׼������
************************************************/
bit FlashCheckID()
{
    uchar dat1, dat2;
    
    CS_PM25 = 0;
    WriteReadByte(SFC_RDID);                         //���Ͷ�ȡID����
    WriteReadByte(0x00);                             //�ն�3���ֽ�
    WriteReadByte(0x00);
    WriteReadByte(0x00);
    dat1 = WriteReadByte(0x00);                      //��ȡ������ID1
    WriteReadByte(0x00);                             //��ȡ�豸ID
    dat2 = WriteReadByte(0x00);                      //��ȡ������ID2
    CS_PM25 = 1;
                                                //����Ƿ�ΪPM25LVxxϵ�е�Flash
    g_fFlashOK = ((dat1 == 0x9d) && (dat2 == 0x7f));
    
    return g_fFlashOK;
}

/************************************************
���Flash��æ״̬
��ڲ���: ��
���ڲ���:
    0 : Flash���ڿ���״̬
    1 : Flash����æ״̬
************************************************/
bit IsFlashBusy()
{
    uchar dat;
    
    CS_PM25 = 0;
	delayUs(10);
    WriteReadByte(SFC_RDSR);                         //���Ͷ�ȡ״̬����
    dat = WriteReadByte(0);                          //��ȡ״̬
    CS_PM25 = 1;
    delayUs(10);
    return (dat & 0x01);                        //״ֵ̬��Bit0��Ϊæ��־
}

/************************************************
ʹ��Flashд����
��ڲ���: ��
���ڲ���: ��
************************************************/
void FlashWriteEnable()
{
    while (IsFlashBusy());                      //Flashæ���
    CS_PM25 = 0;
	delayUs(10);
    WriteReadByte(SFC_WREN);                         //����дʹ������
    CS_PM25 = 1;
	delayUs(10);
}

/************************************************
������ƬFlash
��ڲ���: ��
���ڲ���: ��
************************************************/
void FlashErase()
{
    if (g_fFlashOK)
    {
        FlashWriteEnable();                     //ʹ��Flashд����
        CS_PM25 = 0;
		delayUs(10);
        WriteReadByte(SFC_CHIPER);                   //����Ƭ��������
        CS_PM25 = 1;
		delayUs(10);
    }
}

/************************************************
��Flash�ж�ȡ����
��ڲ���:
    addr   : ��ַ����
    size   : ���ݿ��С
    buffer : �����Flash�ж�ȡ������
���ڲ���:
    ��
************************************************/
/*
void FlashFastRead(ulong addr, ulong size, uchar *buffer)
{
    if (g_fFlashOK)
    {
        while (IsFlashBusy());                  //Flashæ���
        CS_PM25 = 0;
		delayUs(10);
	//	WriteReadByte(SFC_READ);
      WriteReadByte(SFC_FASTREAD);                 //ʹ�ÿ��ٶ�ȡ����
        WriteReadByte(((uchar *)&addr)[1]);           //������ʼ��ַ
        WriteReadByte(((uchar *)&addr)[2]);
        WriteReadByte(((uchar *)&addr)[3]);
        WriteReadByte(0);                            //��Ҫ�ն�һ���ֽ�
        while (size)
        {
            *buffer = WriteReadByte(0);              //�Զ�������ȡ������
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
��Flash�ж�ȡ����
��ڲ���:
    addr   : ��ַ����
    size   : ���ݿ��С
    buffer : �����Flash�ж�ȡ������
���ڲ���:
    ��
************************************************/
void FlashRead(ulong addr, ulong size, uchar *buffer)
{
    if (g_fFlashOK)
    {
        while (IsFlashBusy());                  //Flashæ���
        CS_PM25 = 0;
		delayUs(10);
		WriteReadByte(SFC_READ);
    //  WriteReadByte(SFC_FASTREAD);                 //ʹ�ÿ��ٶ�ȡ����
        WriteReadByte(((uchar *)&addr)[1]);           //������ʼ��ַ
        WriteReadByte(((uchar *)&addr)[2]);
        WriteReadByte(((uchar *)&addr)[3]);
    //    WriteReadByte(0);                            //��Ҫ�ն�һ���ֽ�
        while (size)
        {
            *buffer = WriteReadByte(0);              //�Զ�������ȡ������
            addr++;
            buffer++;
            size--;
        }
        CS_PM25 = 1;
		delayUs(10);
    }
}
/************************************************
д���ݵ�Flash��
��ڲ���:
    addr   : ��ַ����
    size   : ���ݿ��С
    buffer : ������Ҫд��Flash������
���ڲ���: ��
************************************************/
void FlashWrite(ulong addr, ulong size, uchar *buffer)
{
    if (g_fFlashOK)
    {
		while (IsFlashBusy());                  //Flashæ���
		while (size)
	    {

	        FlashWriteEnable();                     //ʹ��Flashд����
	        CS_PM25 = 0;
			delayUs(10);
	        WriteReadByte(SFC_PAGEPROG);                 //����ҳ�������
	        WriteReadByte(((uchar *)&addr)[1]);           //������ʼ��ַ
	        WriteReadByte(((uchar *)&addr)[2]);
	        WriteReadByte(((uchar *)&addr)[3]);
	        while (size)
	        {
	            WriteReadByte(*buffer);                  //����ҳ��д
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
		WriteReadByte(((uchar *)&addr)[1]);           //������ʼ��ַ
		WriteReadByte(((uchar *)&addr)[2]);
		WriteReadByte(((uchar *)&addr)[3]);
		CS_PM25 = 1;
		delayUs(10);
		delayMs(50);
	
}

void resetFlash(){
	//g_fFlashOK=0;
	g_fFlashOK=1;// Ĭ��ʹflashֱ����Ч���������
}


