package com.qijiabin.core.client;

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

    private RpcResponse response;
    private final Object obj = new Object();

    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        this.response = response;
        synchronized (obj) {
            obj.notifyAll();
        }
    }

    public RpcResponse send(String host, int port, final String serialize, RpcRequest request) throws Exception {
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
            synchronized (obj) {
            	future.channel().writeAndFlush(request).sync();
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

