/*
 *  DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 *  Copyright (C) 2025 NeonAngelThreads (https://github.com/NeonAngelThreads)
 *
 *     This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 *     License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any
 *     later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 *     implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 *     License for more details. You should have received a copy of the GNU General Public License along with this
 *     program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  https://space.bilibili.com/386644641
 */

package org.angellock.impl.util;

import lombok.Data;
import lombok.ToString;
import org.geysermc.mcprotocollib.network.ProxyInfo;

@Data
@ToString
public class ProxyObject {
    boolean enabled;
    Info info;

    @Data
    public static class Info{
        String address;
        int port;
        String type;
        String username;
        String password;

        public ProxyInfo.Type getType() {
            return switch (type.toUpperCase()) {
                case "HTTP" -> ProxyInfo.Type.HTTP;
                case "SOCKS4" -> ProxyInfo.Type.SOCKS4;
                case "SOCKS5" -> ProxyInfo.Type.SOCKS5;

                default -> null;
            };
        }

        public boolean isValid(){
            return type != null &&
                    address != null &&
                    !type.isBlank() &&
                    !address.isBlank();
        }
    }
}
