#ifndef _DISPLAY_H_
#define _DISPLAY_H_
#include "constant.h"
#include "useful.h"
#include "vvspi.h"
#define DOT_POS_NONE 8
#define DIGIT_NONE 20 //Լ�����ֵ���20ʱ ����ʾ��λ����
#define DISPLAY_BUF_LEN 9 //1��С����λ�� 8������
#define DOT_POS_INDEX 8
extern uchar xdata displayBuf[DISPLAY_BUF_LEN];
void sendDisplay();
void initDisplayBuf();
#endif
