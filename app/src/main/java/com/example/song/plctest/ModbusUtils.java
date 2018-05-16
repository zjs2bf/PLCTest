package com.example.song.plctest;

import android.util.Log;

import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadCoilsRequest;
import net.wimpi.modbus.msg.ReadCoilsResponse;
import net.wimpi.modbus.msg.ReadInputDiscretesRequest;
import net.wimpi.modbus.msg.ReadInputDiscretesResponse;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadInputRegistersResponse;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteCoilRequest;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.net.TCPMasterConnection;

import java.net.InetAddress;

/**
 * Created by song on 2018/5/10.
 */

public class ModbusUtils {
    /**
     * 查询Function 为Input Status的寄存器
     *
     * @param ip
     * @param address
     * @param slaveId
     * @return
     * @throws ModbusIOException
     * @throws ModbusSlaveException
     * @throws ModbusException
     */
    public static int readDigitalInput(String ip, int port, int address, int slaveId) {
        int data = 0;

        try {
            InetAddress addr = InetAddress.getByName(ip);

            // 建立连接
            TCPMasterConnection con = new TCPMasterConnection(addr);

            con.setPort(port);

            con.connect();

            // 第一个参数是寄存器的地址，第二个参数时读取多少个
            ReadInputDiscretesRequest req = new ReadInputDiscretesRequest(address, 1);//02

            // 这里设置的Slave Id, 读取的时候这个很重要
            req.setUnitID(slaveId);

            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);

            trans.setRequest(req);

            // 执行查询
            trans.execute();

            // 得到结果
            ReadInputDiscretesResponse res = (ReadInputDiscretesResponse) trans.getResponse();

            if(res.getDiscretes().getBit(0)){
                data = 1;
            }

            // 关闭连接
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static int readInputRegister(String ip, int port, int address,//03
                                        int slaveId) {
        int data = 0;

        try {
            InetAddress addr = InetAddress.getByName(ip);
            TCPMasterConnection con = new TCPMasterConnection(addr);

            //Modbus.DEFAULT_PORT;
            con.setPort(port);
            con.connect();

            //这里重点说明下，这个地址和数量一定要对应起来
            ReadInputRegistersRequest req = new ReadInputRegistersRequest(address, 1);

            //这个SlaveId�?定要正确
            req.setUnitID(slaveId);

            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);

            trans.setRequest(req);

            trans.execute();

            ReadInputRegistersResponse res = (ReadInputRegistersResponse) trans.getResponse();

            data = res.getRegisterValue(0);

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static int readDigitalOutput(String ip, int port, int address,
                                        int slaveId) {
        int data = 0;
        try {
            InetAddress addr = InetAddress.getByName(ip);

            TCPMasterConnection con = new TCPMasterConnection(addr);
            con.setPort(port);
            con.connect();

            ReadCoilsRequest req = new ReadCoilsRequest(address, 1);

            req.setUnitID(slaveId);

            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);

            trans.setRequest(req);

            trans.execute();

            ReadCoilsResponse res = ((ReadCoilsResponse) trans.getResponse());

            if(res.getCoils().getBit(0)){
                data = 1;
            }

            con.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return data;
    }

    public static int[] readRegister(String ip, int port, int address,
                                     int slaveId) {
        int[] data = new int[14];//读取的数据储存的数组，return值
        try {
            InetAddress addr = InetAddress.getByName(ip);

            TCPMasterConnection con = new TCPMasterConnection(addr);

            //con.setTimeout(1);
            con.setPort(port);
            con.connect();
            ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest(address, 14);//04
            req.setUnitID(slaveId);

            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);

            trans.setRequest(req);

            trans.execute();

            ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse) trans.getResponse();

            //分别读取每个位置上的数值
            for (int c=0;c<=13;c++){

                data[c] = res.getRegisterValue(c);
            }

            con.close();
        } catch (Exception e) {
            Log.d("ModbusUtil","不能通讯，异常信息为"+ String.valueOf(e));
            e.printStackTrace();
            MainActivity.eState=true;
        }

        return data;
    }

    /**
     * 写入数据到真机，数据类型是RE
     *
     * @param ip
     * @param port
     * @param slaveId
     * @param address
     * @param value
     */
//    public static void writeRegister(String ip, int port, int slaveId,
//                                     int address, int value) {
//
//        try {
//            InetAddress addr = InetAddress.getByName(ip);
//            Log.d("ModbusUtil","addr");
//
//            TCPMasterConnection connection = new TCPMasterConnection(addr);
//            Log.d("ModbusUtil","connection");
//            //connection.setTimeout(1);
//            Log.d("ModbusUtil","timeout1");
//            connection.setPort(port);
//            Log.d("ModbusUtil","port");
//            connection.connect();
//            Log.d("ModbusUtil","connect");
//
//            ModbusTCPTransaction trans = new ModbusTCPTransaction(connection);
//            Log.d("ModbusUtil","new ModbusTCPTransaction");
//
//            UnityRegister register = new UnityRegister(value);
//            Log.d("ModbusUtil","register");
//
//            WriteSingleRegisterRequest req = new WriteSingleRegisterRequest(
//                    address, register);
//            Log.d("ModbusUtil","req");
//
//            req.setUnitID(slaveId);
//            Log.d("ModbusUtil","Slave ID");
//            trans.setRequest(req);
//            Log.d("ModbusUtil","trans.setrequest");
//
//            System.out.println("ModbusSlave: FC" + req.getFunctionCode()
//                    + " ref=" + req.getReference() + " value="
//                    + register.getValue());
//            trans.execute();
//            Log.d("ModbusUtil","执行过写入操作");
//            connection.close();
//        } catch (Exception ex) {
//            Log.d("ModbusUtil","写入错误XXXXXXXXXX"+ex);
//            System.out.println("Error in code");
//            ex.printStackTrace();
//            MainActivity.eState=true;
//        }
//    }

    /**
     * 写入数据到真机的DO类型的寄存器上面
     *
     * @param ip
     * @param port
     * @param slaveId
     * @param address
     * @param value
     */
    public static void writeCoil(String ip, int port, int slaveId,
                                          int address, int value) {

        try {
            InetAddress addr = InetAddress.getByName(ip);

            TCPMasterConnection connection = new TCPMasterConnection(addr);
            connection.setPort(port);
            connection.connect();

            ModbusTCPTransaction trans = new ModbusTCPTransaction(connection);

            boolean val = true;

            if (value == 0) {
                val = false;
            }

            WriteCoilRequest req = new WriteCoilRequest(address, val);

            req.setUnitID(slaveId);
            trans.setRequest(req);

            trans.execute();
            connection.close();
        } catch (Exception ex) {
            System.out.println("writeDigitalOutput Error in code");
            ex.printStackTrace();
        }
    }
}
