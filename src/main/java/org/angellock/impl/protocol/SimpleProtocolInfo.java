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
 *    License for more details.
 *
 *    You should have received a copy of the GNU General Public License along with this program.  If not, see
 *    <https://www.gnu.org/licenses/>.
 *
 *  https://space.bilibili.com/386644641
 */

package org.angellock.impl.protocol;

import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.protocol.ProtocolPipeline;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import lombok.Getter;

import java.util.UUID;

public class SimpleProtocolInfo implements ProtocolInfo {
    private final ProtocolVersion clientVersion;
    private final ProtocolVersion serverVersion;
    private final UUID uuid;
    private final String username;

    public SimpleProtocolInfo(ProtocolVersion clientVersion, ProtocolVersion serverVersion) {
        this(clientVersion, serverVersion, UUID.randomUUID(), "DolphinBot");
    }

    public SimpleProtocolInfo(ProtocolVersion clientVersion, ProtocolVersion serverVersion, UUID uuid, String username) {
        this.clientVersion = clientVersion;
        this.serverVersion = serverVersion;
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public State getClientState() {
        return null;
    }

    @Override
    public State getServerState() {
        return null;
    }

    @Override
    public void setClientState(State clientState) {

    }

    @Override
    public void setServerState(State serverState) {

    }

    @Override
    public ProtocolVersion protocolVersion() {
        return null;
    }

    @Override
    public void setProtocolVersion(ProtocolVersion protocolVersion) {

    }

    @Override
    public ProtocolVersion serverProtocolVersion() {
        return null;
    }

    @Override
    public void setServerProtocolVersion(ProtocolVersion protocolVersion) {

    }

    @Override
    public int getProtocolVersion() {
        return clientVersion.getVersion();
    }

    @Override
    public int getServerProtocolVersion() {
        return serverVersion.getVersion();
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {

    }

    @Override
    public boolean compressionEnabled() {
        return false;
    }

    @Override
    public void setCompressionEnabled(boolean compressionEnabled) {

    }

    @Override
    public ProtocolPipeline getPipeline() {
        return null;
    }

    @Override
    public void setPipeline(ProtocolPipeline pipeline) {
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {

    }
}
