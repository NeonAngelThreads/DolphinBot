/*
 * DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 * Copyright (C) 2025 NeonAngelThreads (https://github.com/NeonAngelThreads)
 *
 *    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 *    License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 *    implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 *    License for more details. You should have received a copy of the GNU General Public License along with this
 *    program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.angellock.impl.protocol.via;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.platform.ViaCodecHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty channel handler that delegates packet reads to ViaVersion's translation pipeline.
 * Inserted into both client-to-proxy and proxy-to-server channels.
 *
 * <p>Mirrors ViaProxy's {@code ViaProxyViaCodec} — a thin wrapper around ViaCodecHandler
 * with added error logging. Does NOT manually fix protocol versions; ViaVersion handles
 * all protocol state internally via the VersionProvider and handshake processing.</p>
 */
public class DolphinViaCodec extends ViaCodecHandler {

    private static final Logger log = LoggerFactory.getLogger("DolphinVia");

    public DolphinViaCodec(UserConnection user) {
        super(user);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("[ViaCodec] INBOUND on channel {}: msgType={}, active={}, shouldTransform={}, clientSide={}",
                ctx.channel().id().asShortText(),
                msg.getClass().getSimpleName(),
                connection.isActive(),
                connection.shouldTransformPacket(),
                connection.isClientSide());
        if (msg instanceof io.netty.buffer.ByteBuf buf) {
            log.debug("[ViaCodec] INBOUND readableBytes={}", buf.readableBytes());
            if (buf.readableBytes() > 0) {
                int dumpLen = Math.min(buf.readableBytes(), 32);
                byte[] dump = new byte[dumpLen];
                buf.markReaderIndex();
                buf.readBytes(dump);
                buf.resetReaderIndex();
                StringBuilder hex = new StringBuilder();
                for (byte b : dump) hex.append(String.format("%02x ", b));
                log.debug("[ViaCodec] INBOUND raw bytes: {}", hex.toString().trim());
            }
        }
        try {
            super.channelRead(ctx, msg);
        } catch (Throwable e) {
            log.warn("[ViaCodec] Translation error INBOUND on channel {}: {}",
                    ctx.channel().id().asShortText(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.debug("[ViaCodec] OUTBOUND on channel {}: msgType={}, active={}, shouldTransform={}, clientSide={}",
                ctx.channel().id().asShortText(),
                msg.getClass().getSimpleName(),
                connection.isActive(),
                connection.shouldTransformPacket(),
                connection.isClientSide());
        if (msg instanceof io.netty.buffer.ByteBuf buf) {
            log.debug("[ViaCodec] OUTBOUND readableBytes={}", buf.readableBytes());
            if (buf.readableBytes() > 0) {
                int dumpLen = Math.min(buf.readableBytes(), 32);
                byte[] dump = new byte[dumpLen];
                buf.markReaderIndex();
                buf.readBytes(dump);
                buf.resetReaderIndex();
                StringBuilder hex = new StringBuilder();
                for (byte b : dump) hex.append(String.format("%02x ", b));
                log.debug("[ViaCodec] OUTBOUND raw bytes (before translation): {}", hex.toString().trim());
            }
        }
        try {
            super.write(ctx, msg, promise);
        } catch (Throwable e) {
            log.warn("[ViaCodec] Translation error OUTBOUND on channel {}: {}",
                    ctx.channel().id().asShortText(), e.getMessage(), e);
            throw e;
        }
    }
}
