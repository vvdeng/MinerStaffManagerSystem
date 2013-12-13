#ifndef _DISPLAY_H_
#define _DISPLAY_H_
#include "constant.h"
#include "useful.h"
#include "vvspi.h"
#define DOT_POS_NONE 8
#define DIGIT_NONE 20 //约定数字等于20时 不显示该位数字
#define DISPLAY_BUF_LEN 9 //1个小数点位置 8个数字
#define DOT_POS_INDEX 8
extern uchar xdata displayBuf[DISPLAY_BUF_LEN];
void sendDisplay();
void initDisplayBuf();
#endif
