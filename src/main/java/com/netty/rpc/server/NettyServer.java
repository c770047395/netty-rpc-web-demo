package com.netty.rpc.server;

import com.netty.rpc.codec.RpcDecoder;
import com.netty.rpc.codec.RpcEncoder;
import com.netty.rpc.common.RpcRequest;
import com.netty.rpc.common.RpcResponse;
import com.netty.rpc.handler.HttpHandler;
import com.netty.rpc.handler.ServerHandler;
import com.netty.rpc.serializer.JSONSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Slf4j
@Component
public class NettyServer implements InitializingBean {
    private EventLoopGroup boss = null;
    private EventLoopGroup worker = null;
    @Autowired
    private ServerHandler serverHandler;
    @Autowired
    private HttpHandler httpHandler;
    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    public void start() throws Exception {
        //负责处理客户端连接的线程池
        boss = new NioEventLoopGroup();
        //负责处理读写操作的线程池
        worker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //添加解码器
//                        pipeline.addLast(new RpcEncoder(RpcResponse.class, new JSONSerializer()));
                        //添加编码器
//                        pipeline.addLast(new RpcDecoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(new HttpRequestDecoder());
                        pipeline.addLast(new HttpObjectAggregator(65535));
                        pipeline.addLast(new HttpResponseEncoder());
                        //添加请求处理器
//                        pipeline.addLast(serverHandler);
                        pipeline.addLast(httpHandler);

                    }
                });
        bind(serverBootstrap, 8888);
    }

    /**
     * 如果端口绑定失败，端口数+1,重新绑定
     *
     * @param serverBootstrap
     * @param port
     */
    public void bind(final ServerBootstrap serverBootstrap,int port) {
        try {
            serverBootstrap.bind(port).addListener(future -> {
                if (future.isSuccess()) {
                    log.info("端口[ {} ] 绑定成功",port);
                } else {
                    log.error("端口[ {} ] 绑定失败", port);
                    bind(serverBootstrap, port + 1);
                }
            }).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destory() throws InterruptedException {
        boss.shutdownGracefully().sync();
        worker.shutdownGracefully().sync();
        log.info("关闭Netty");
    }
}