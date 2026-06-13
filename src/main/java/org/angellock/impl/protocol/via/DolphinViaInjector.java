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

import com.viaversion.viaversion.platform.NoopInjector;

/**
 * No-op injector that tells ViaVersion our codec handler name.
 * Since we manually insert {@link DolphinViaCodec} into each channel's pipeline,
 * there's nothing to inject automatically.
 */
public class DolphinViaInjector extends NoopInjector {

    public static final String CODEC_NAME = "dolphin-via-codec";

    @Override
    public String getEncoderName() {
        return CODEC_NAME;
    }

    @Override
    public String getDecoderName() {
        return CODEC_NAME;
    }
}
