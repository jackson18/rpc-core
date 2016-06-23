package com.qijiabin.core.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qijiabin.core.common.RpcRequest;
import com.qijiabin.core.common.RpcResponse;
import com.qijiabin.core.rpc.RpcDecoder;
import com.qijiabin.core.rpc.RpcEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * ========================================================
 * 日 期：2016年6月15日 上午10:35:38
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：RPC 客户端（用于发送 RPC 请求）
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientHandler.class);

    private String host;
    private int port;
    private String serialize;
    private RpcResponse response;
    private final Object obj = new Object();

    
    public RpcClientHandler(String host, int port, String serialize) {
        this.host = host;
        this.port = port;
        this.serialize = serialize;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        this.response = response;

        synchronized (obj) {
            obj.notifyAll();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(">>>client caught exception", cause);
        ctx.close();
    }

    public RpcResponse send(RpcRequest request) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                            .addLast(new RpcEncoder(RpcRequest.class, serialize))
                            .addLast(new RpcDecoder(RpcResponse.class, serialize))
                            .addLast(RpcClientHandler.this);
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().writeAndFlush(request).sync();

            synchronized (obj) {
                obj.wait();
            }

            if (response != null) {
                future.channel().closeFuture().sync();
            }
            return response;
        } finally {
            group.shutdownGracefully();
        }
    }
    
}

