package org.noear.solon.extend.socketd.protocol;

import org.noear.solon.core.message.FrameFlag;
import org.noear.solon.core.message.Message;
import org.noear.solon.extend.socketd.MessageUtils;
import org.noear.solon.extend.socketd.protocol.util.GzipUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 安全协议
 *
 * @author noear
 * @since 1.2
 * */
public class MessageProtocolCompress implements MessageProtocol {
    protected MessageProtocol baseProtocol = MessageProtocolBase.instance;

    public MessageProtocolCompress() {

    }

    public MessageProtocolCompress(MessageProtocol baseProtocol) {
        this.baseProtocol = baseProtocol;
    }




    public byte[] compress(byte[] bytes) throws Exception {
        return GzipUtil.compress(bytes);
    }

    public byte[] uncompress(byte[] bytes) throws Exception {
        return GzipUtil.uncompress(bytes);
    }

    @Override
    public ByteBuffer encode(Message message) throws Exception {
        ByteBuffer buffer = baseProtocol.encode(message);

        byte[] bytes = compress(buffer.array());
        message = MessageUtils.wrapContainer(bytes);

        return baseProtocol.encode(message);
    }

    @Override
    public Message decode(ByteBuffer buffer) throws Exception {
        Message message = baseProtocol.decode(buffer);

        if (message.flag() == FrameFlag.container) {
            byte[] bytes = uncompress(message.body());
            buffer = ByteBuffer.wrap(bytes);

            message = baseProtocol.decode(buffer);
        }

        return message;
    }
}
