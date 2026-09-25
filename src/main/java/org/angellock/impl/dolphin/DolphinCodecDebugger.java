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
 *
 * https://space.bilibili.com/386644641
 */

package org.angellock.impl.dolphin;

import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface DolphinCodecDebugger {
    class Impl implements DolphinCodecDebugger{
        private static final Logger log = LoggerFactory.getLogger("DolphinVia");
        @Override
        public void debugPacket(String s,ChannelHandlerContext ctx, Object msg, UserConnection connection) {
            log.debug(s,
                    ctx.channel().id().asShortText(),
                    msg.getClass().getSimpleName(),
                    connection.isActive(),
                    connection.shouldTransformPacket(),
                    connection.isClientSide());
        }

        @Override
        public void debugByte(String s, byte[] dump) {
            StringBuilder hex = new StringBuilder();
            for (byte b : dump) hex.append(String.format("%02x ", b));
            log.debug(s, hex.toString().trim());
        }

        @Override
        public void debugReadableByte(String s,ByteBuf buf) {

        }
    }

    class EmptyImpl implements DolphinCodecDebugger{
        @Override
        public void debugPacket(String s, ChannelHandlerContext ctx, Object msg, UserConnection connection) {
        }
        @Override
        public void debugByte(String s, byte[] dump) {
        }
        @Override
        public void debugReadableByte(String s, ByteBuf buf) {
        }
    }
    void debugPacket(String s,ChannelHandlerContext ctx, Object msg, UserConnection connection);
    void debugByte(String s,byte[] dump);
    void debugReadableByte(String s,io.netty.buffer.ByteBuf buf);
}
