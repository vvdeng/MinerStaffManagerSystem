package com.vvdeng.miner.staff.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;
import javax.swing.JOptionPane;

import com.vvdeng.miner.staff.entity.Staff;
import com.vvdeng.miner.staff.ui.StaffManagerFrame;
import com.vvdeng.miner.staff.utils.GlobalData;
import com.vvdeng.miner.staff.utils.SysConfiguration;

public class SerialComm implements /* Runnable, */SerialPortEventListener {
	public static SerialComm sysSerialComm = null;
	private StaffManagerFrame staffManagerFrame;
	private CommPortIdentifier portId;
	private InputStream inputStream;
	private OutputStream outputStream;
	private SerialPort serialPort;
	private boolean isRefreshed;
	public static int dataType;
	public static int state;

	// private Thread readThread;
	public static final int BUFFER_SIZE = 1024;
	public static final int BIGBUF_SIZE = 16000;
	public static List<String> portNameList;
	public static final int WAIT_TIME = 2000;
	public static final int DATA_TYPE_NOTHING = 0;
	public static final int SEND_LED_MESSAGES = 1;
	public static final int DATA_TYPE_UNITS_INFO = 2;
	public static final int SEND_ALL_LED_MESSAGES = 3;
	public static final int UPDATE_STAFF_INFO = 4;
	public static final int DATA_TYPE_SINGLE_UNIT_INFO = 5;
	public static final int DATA_TYPE_UNITS_INFO_LENTTH = 101;
	// TODO //命令号最高位更改为1 广播1000 0000 ；获取充电状态1010 00000；更新员工信息1100 0000；
	public static final byte CMD_SEND_ALL_LED_MESSAGES = (byte) 0x80;// 广播LED文字信息
	public static final byte CMD_REQ_UNIT_INFO = (byte) 0xA0;// 获取充电状态信息
	public static final byte CMD_UPDATE_STAFF_INFO = (byte) 0xC0;// 更新员工信息
	public static final byte DATA_PRE_UNIT_INFO = 0x50; // 充电状态数据前缀
	public static final byte DATA_PRE__UPDATE_RACK_STAFF = 0x60; // 更新员工信息前缀
	public static final byte DATA_PRE__NULL = 0x00; // 空前缀
	private byte[] buffers;
	private int currentIndex;
	private byte[] bigBuf;
	private int bigBufIndex;
	private boolean serialCommBusy;
	private boolean serialCommNotConnected;
	public static final byte CMD_QUERY_STATE = (byte) 0x80;// 1000
															// 0000轮询各分站，上报采集到的当前标识卡状态。
															// 0x81-0x88
															// 不用做命令好，用作端口号
	public static final byte CMD_SYNCH_TIME = (byte) 0x81;
	public static final byte CMD_CALL_STAFF = (byte) 0x84;
	public static final byte CMD_REQ_TIMEOUT_READER = (byte) 0x85;
	

	public static final byte CMD_BROADCAST = (byte) 0x01;
	public static final int SEQ = 0;
	public static int currentStationIndex = 1;
	public static int commState = 0;
	public static final int STATE_NOTHING = 0;
	public static final int STATE_QUERY_STATION_STATE = 1;
	public static final int STATE_SYNCH_TIME = 2;
	public static final int STATE_CALL_STAFF = 3;
	public static final int STATE_REQ_TIMEOUT_READER = 4;
	public static final int STATE_WAIT_TIMEOUT_READER = 5;
	public static final int SYM_END = 0xfe;

	public static void main(String[] args) {

		// SerialComm serialComm = new SerialComm();
		// while (true) {
		// BufferedReader reader = new BufferedReader(new InputStreamReader(
		// System.in));
		// try {
		// String data = reader.readLine();
		// serialComm.write(data.getBytes());
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		if((0x80&0x82)==0x80){
			
			
		}
	}

	@SuppressWarnings("restriction")
	public SerialComm(StaffManagerFrame staffManagerFrame) {
		this.staffManagerFrame = staffManagerFrame;
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		portNameList = new ArrayList<String>();
		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				portNameList.add(portId.getName());
				if (portId.getName().toUpperCase()
						.equals(SysConfiguration.commPort)) {
					try {
						serialPort = (SerialPort) portId.open("MinerLampApp",
								WAIT_TIME);

					} catch (PortInUseException e) {
						serialCommBusy = true;
						e.printStackTrace();
					}
				}

			}
		}
		Collections.sort(portNameList);
		System.out.println(serialPort);
		if (serialPort == null) {

			serialCommNotConnected = true;
		}
		if (isSerialCommOk()) {
			try {
				if (serialCommBusy == false && serialCommNotConnected == false) {
					inputStream = serialPort.getInputStream();
					outputStream = serialPort.getOutputStream();
				}

			} catch (IOException e) {

				e.printStackTrace();
			}
			try {
				if (serialCommBusy == false && serialCommNotConnected == false) {
					serialPort.addEventListener(this);
				}
			} catch (TooManyListenersException e) {
			}
			serialPort.setInputBufferSize(BUFFER_SIZE);
			serialPort.notifyOnDataAvailable(true);

			try {
				serialPort.setSerialPortParams(SysConfiguration.baudRate,
						SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);
			} catch (UnsupportedCommOperationException e) {
			}
			buffers = new byte[BUFFER_SIZE];
			// readThread = new Thread(this);
			// readThread.start();
		}
	}

	public void send(byte[] bytes) {
		if (bytes == null) {
			return;
		}
		try {
			System.out.print("send bytes...");
			for (int i = 0; i < bytes.length; i++) {
				System.out.print(" bytes[" + i + "] =" + bytes[i]);
			}
			System.out.println();

			outputStream.write(bytes);
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(staffManagerFrame, "通信异常，请重新插拔计算机串口的连接后\n重启本软件");
			System.exit(0);
		/*	int choice = JOptionPane.showConfirmDialog(null,
					"通信异常，请重新插拔计算机串口的连接后\n重启本软件");
			if(choice==JOptionPane.YES_OPTION){
				
				
			}
		*/
		}
	}

	public void send(byte b) {
		send(new byte[] { b });
	}

	public void send(int i) {
		send(new byte[] { (byte) i });
	}

	public void run() {
		// try {
		// Thread.sleep(20000);
		// } catch (InterruptedException e) {
		// }

	}

	public void serialEvent(SerialPortEvent event) {

		switch (event.getEventType()) {

		case SerialPortEvent.BI:
			break;
		case SerialPortEvent.OE:
			break;
		case SerialPortEvent.FE:
			break;
		case SerialPortEvent.PE:
			break;
		case SerialPortEvent.CD:
			break;
		case SerialPortEvent.CTS:
			break;
		case SerialPortEvent.DSR:
			break;
		case SerialPortEvent.RI:
			break;
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;

		case SerialPortEvent.DATA_AVAILABLE:

			try {
				while (inputStream.available() > 0) {
					int c = inputStream.read();
					
					/*
					 * if (isCmd(c)) { continue; }
					 */
					if (commState == STATE_NOTHING) {
						System.out.println("c=" + c + " commState=" + commState
								+ " data=" + c);
						continue;
					}
					if (commState == STATE_QUERY_STATION_STATE) {
						System.out.println("c=" + c + " commState=" + commState
								+ " index=" + currentIndex + " dataType="
								+ dataType + " data=" + c);
						
						buffers[currentIndex++] = (byte) c;
						if (c == SYM_END || currentIndex >= BUFFER_SIZE) {

							processStationInfo();
						}
					}
					if (commState == STATE_WAIT_TIMEOUT_READER) {
						System.out.println("c=" + c + " commState=" + commState
								+ " bigBufIndex=" + bigBufIndex + " dataType="
								+ dataType + " data=" + c);
						
						bigBuf[bigBufIndex++] = (byte) c;
						if (c == SYM_END || bigBufIndex >= BIGBUF_SIZE) {

						//	processReqTimeoutReader();
							bigBuf=null;
						}
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
		}
	}

	public void processStationInfo() {
		byte[] oriDateArr = new byte[currentIndex];
		for (int i = 0; i < currentIndex; i++) {
			oriDateArr[i] = buffers[i];
		}
		;
		GlobalData.stationStateMap.put(currentStationIndex, oriDateArr);
		commState = STATE_NOTHING;
		currentIndex = 0;
		reqNextStationInfo();
	}

	public void reqCurrentStationInfo() {
		commState = STATE_QUERY_STATION_STATE;
		send(CMD_QUERY_STATE);
		send(currentStationIndex);

		send(SYM_END);
	}

	public void reqNextStationInfo() {
		currentStationIndex++;
		if (currentStationIndex <= SysConfiguration.stateNum) {
			reqCurrentStationInfo();
		} else {
			endReqStationsInfo();
		}
	}

	public void startReqStationsInfo() {
		currentStationIndex = 1;
		reqCurrentStationInfo();
	}

	public void startSynchronizeTime(byte[] timeArr) {
		commState = STATE_SYNCH_TIME;
		send(CMD_SYNCH_TIME);
		send(CMD_BROADCAST);

		for (int i = 0; i < timeArr.length; i++) {
			System.out.println("timeArr" + i + " " + timeArr[i]);
			send(timeArr[i]);
		}
		send(SYM_END);
		commState = STATE_NOTHING;
	}

	public void callStaff(byte[] staffIdArr) {
		commState = STATE_CALL_STAFF;
		send(CMD_CALL_STAFF);
		send(CMD_BROADCAST);

		for (int i = 0; i < staffIdArr.length; i++) {
			System.out.println("staffIdArr" + i + " " + staffIdArr[i]);
			send(staffIdArr[i]);
		}
		send(SYM_END);
		commState = STATE_NOTHING;
	}

	public void cancelCallStaff() {
		byte[] staffIdArr = new byte[1];
		staffIdArr[0] = 0;
		callStaff(staffIdArr);
	}
	public void retreiveReaderData() {
		commState = STATE_REQ_TIMEOUT_READER;
		send(CMD_REQ_TIMEOUT_READER);
		send(1);//分站号
		send(1);//读卡器号
		send(SYM_END);
		commState = STATE_WAIT_TIMEOUT_READER;
		bigBuf=new byte[BIGBUF_SIZE]; //bigBuf用完后需要调用bigBuf=null;
		bigBufIndex=0; 
		
	}
	public void endReqStationsInfo() {
		currentStationIndex = 1;
	}

	private boolean isCmd(int c) {
		if ((c & 0x80) == 0x80) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isNewDataCome() {
		return isRefreshed;
	}

	public void clearBuffer() {
		currentIndex = 0;
	}

	public byte[] getBuffers() {
		return buffers;
	}

	public void setBuffers(byte[] buffers) {
		this.buffers = buffers;
	}

	public boolean isCommand(byte b) {
		return (0x80 & b) == 0x80;
	}

	public boolean isSerialCommBusy() {
		return serialCommBusy;
	}

	public boolean isSerialCommNotConnected() {
		return serialCommNotConnected;
	}

	public void setSerialCommNotConnected(boolean serialCommNotConnected) {
		this.serialCommNotConnected = serialCommNotConnected;
	}

	public void setSerialCommBusy(boolean serialCommBusy) {
		this.serialCommBusy = serialCommBusy;
	}

	public boolean isSerialCommOk() {
		return this.serialCommBusy == false
				&& this.serialCommNotConnected == false
				&& this.dataType == DATA_TYPE_NOTHING;
	}
	
}
