package com.calvin.service.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 *
 * Created by Calvin.Ding on 2017/1/10.
 */
public class BaseThriftClient {

    protected TMultiplexedProtocol mp;

    protected TTransport transport;

    public BaseThriftClient() {

    }

    public void initProtocol(String ip, String port, String serverName) throws TException {
        this.initProtocol(ip, port, serverName, 3000);
    }

    public void initProtocol(String ip, String port, String serverName, int timeout) throws TException {
        this.transport = new TSocket(ip, Integer.parseInt(port), timeout);
        this.transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        this.mp = new TMultiplexedProtocol(protocol, serverName);
    }

    public void closeProtocol() {
        this.transport.close();
    }

}
