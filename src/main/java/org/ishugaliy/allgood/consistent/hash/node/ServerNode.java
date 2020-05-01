/*
 * The MIT License
 *
 * Copyright (c) 2020 Iurii Shugalii
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.ishugaliy.allgood.consistent.hash.node;

import org.ishugaliy.allgood.consistent.hash.annotation.Generated;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Implementation of {@link Node} and represents server.
 *
 * @author Iurii Shugalii
 */
public class ServerNode implements Node {
    private final String dc;
    private final String ip;
    private final int port;

    public ServerNode(String ip, int port) {
        this("", ip, port);
    }

    public ServerNode(String dc, String ip, int port) {
        this.dc = dc != null ? dc : "";
        this.ip = ip != null ? ip : "";
        this.port = port;
    }

    public String getDc() {
        return dc;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String getKey() {
        return String.format("%s:%s:%s", dc, ip, port);
    }

    @Override
    @Generated
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerNode)) return false;
        ServerNode that = (ServerNode) o;
        return port == that.port &&
                Objects.equals(dc, that.dc) &&
                Objects.equals(ip, that.ip);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(dc, ip, port);
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", ServerNode.class.getSimpleName() + "[", "]")
                .add("dc='" + dc + "'")
                .add("ip='" + ip + "'")
                .add("port=" + port)
                .toString();
    }
}
