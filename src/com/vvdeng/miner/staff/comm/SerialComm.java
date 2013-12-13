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

import com.vvdeng.miner.constant.StationCommState;
import com.vvdeng.miner.staff.dao.StationLogDAO;
import com.vvdeng.miner.staff.entity.Staff;
import com.vvdeng.miner.staff.entity.SubDevice;
import com.vvdeng.miner.staff.ui.StaffManagerFrame;
import com.vvdeng.miner.staff.utils.DAOUtil;
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

	private byte[] buffers;
	private int currentIndex;
	private byte[] bigBuf;
	private int bigBufIndex;
	private boolean serialCommBusy;
	private boolean serialCommNotConnected;
	// 命令格式定义为0x1xxx 000x
	public static final byte CMD_QUERY_STATE = (byte) 0x80;// 1000
															// 0000轮询各分站，上报采集到的当前标识卡状态。
															// 0x81-0x88
															// 不用做命令好，用作端口号
	public static final byte CMD_SYNCH_TIME = (byte) 0x81;
	public static final byte CMD_CALL_STAFF = (byte) 0x90;
	public static final byte CMD_REQ_TIMEOUT_READER = (byte) 0x91;
	public static final byte CMD_REQ_TIMEOUT_STATION = (byte) 0xa0;

	public static final byte CMD_BROADCAST = (byte) 0x01;
	public static final int SEQ = 0;
//	public static int GlobalData.currentStationIndex = 0;
	public static int commState = 0;
	public static final int STATE_NOTHING = 0;
	public static final int STATE_QUERY_STATION_STATE = 1;
	public static final int STATE_SYNCH_TIME = 2;
	public static final int STATE_CALL_STAFF = 3;
	public static final int STATE_REQ_TIMEOUT_READER = 4;
	public static final int STATE_WAIT_TIMEOUT_READER = 5;
	public static final int STATE_TEST = 6;
	public static final int STATE_SAVING_DATA = 7;
	public static final int STATE_CLEARING_ALLDATA = 8;
	// 分隔符号定义为 0x1xxx YYYx YYY不能为全0
	public static final int SYM_START = 0xfd;
	public static final int SYM_END = 0xfe;

	public static final int STATION_DATA_DELIMN = 0xf8;
	public static final int READER_DATA_DELIMN = 0xf9;
	public static final int STATION_NOT_READY = 1;
	public static final int STATION_OK = 2;

	public static final int READER_TIMEOUT_SYM = 96;

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
		if ((0x80 & 0x82) == 0x80) {

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
			JOptionPane.showMessageDialog(staffManagerFrame,
					"通信异常，请重新插拔计算机串口的连接后\n重启本软件");
			System.exit(0);
			/*
			 * int choice = JOptionPane.showConfirmDialog(null,
			 * "通信异常，请重新插拔计算机串口的连接后\n重启本软件");
			 * if(choice==JOptionPane.YES_OPTION){
			 * 
			 * 
			 * }
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

							// processReqTimeoutReader();
							bigBuf = null;
						}
					}
					if (commState == STATE_TEST) {

						System.out.print(c + " ");
						if ((++testCount) % 100 == 0) {

							System.out.println();
						}
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
		}
	}

	int testCount = 0;

	public void processStationInfo() {
		byte[] oriDataArr = new byte[currentIndex];
		for (int i = 0; i < currentIndex; i++) {
			oriDataArr[i] = buffers[i];
		}
		;
		GlobalData.stationStateMap.put(GlobalData.currentStationIndex, oriDataArr);
		commState = STATE_NOTHING;
		currentIndex = 0;
		try {
			DAOUtil.parseState(oriDataArr);
		} catch (Exception e) {
			e.printStackTrace();
			commState=STATE_NOTHING;
		}
		
		GlobalData.currentBgActivityState=StaffManagerFrame.BackgroundActivity.STATE_ONE_REFRESHED;
		staffManagerFrame.notifyActivity();
		// reqNextStationInfo();
	}

	public void reqCurrentStationInfo() {
		commState = STATE_QUERY_STATION_STATE;
		send(SYM_START);
		send(CMD_QUERY_STATE);
		send(GlobalData.currentStationIndex);

		send(SYM_END);
	}

	public void reqNextStationInfo() {
		GlobalData.currentStationIndex++;
		SubDevice subDevice=GlobalData.subDeviceMap.get(GlobalData.currentStationIndex);
		if(subDevice!=null){
			subDevice.setState(StationCommState.EXCEP.getId());
		}
		if (GlobalData.currentStationIndex <= SysConfiguration.stationNum) {
			reqCurrentStationInfo();
		} else {

			endReqStationsInfo();
		}
	}

	public void startReqStationsInfo() {
		GlobalData.currentStationIndex = 1;
		reqCurrentStationInfo();
	}

	public void startSynchronizeTime(byte[] timeArr) {
		commState = STATE_SYNCH_TIME;
		send(SYM_START);
		send(CMD_SYNCH_TIME);
		send(CMD_BROADCAST);

		for (int i = 0; i < timeArr.length; i++) {
			System.out.println("timeArr" + i + " " + timeArr[i]);
			send(timeArr[i]);
		}
		send(SYM_END);
		commState = STATE_NOTHING;
	}

	public Byte[] startSynchronizeTimeArr(byte[] timeArr) {
		List<Byte> list = new ArrayList<Byte>();
		commState = STATE_SYNCH_TIME;
		list.add((byte) SYM_START);
		list.add((byte) CMD_SYNCH_TIME);
		list.add((byte) CMD_BROADCAST);

		for (int i = 0; i < timeArr.length; i++) {
			System.out.println("timeArr" + i + " " + timeArr[i]);
			list.add(timeArr[i]);
		}
		list.add((byte) SYM_END);
		commState = STATE_NOTHING;
		return list.toArray(new Byte[] {});
	}

	public void callStaff(byte[] staffIdArr) {
		commState = STATE_CALL_STAFF;
		send(SYM_START);
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
		/*
		 * commState = STATE_TEST; testCount=0; send(SYM_START);
		 */
		send(SYM_START);
		send(CMD_REQ_TIMEOUT_READER);
		send(1);// 分站号
		send(2);// 读卡器号
		send(SYM_END);
		commState = STATE_WAIT_TIMEOUT_READER;
		bigBuf = new byte[BIGBUF_SIZE]; // bigBuf用完后需要调用bigBuf=null;
		bigBufIndex = 0;

	}

	public void endReqStationsInfo() {
		// actv

		GlobalData.currentStationIndex = 0;
		GlobalData.currentBgActivityState=StaffManagerFrame.BackgroundActivity.STATE_TOTAL_REFRESHED;
		staffManagerFrame.notifyActivity();
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
