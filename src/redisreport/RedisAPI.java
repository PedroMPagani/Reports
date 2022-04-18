package org.donutsmp.ghosts.messenger;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bukkit.Bukkit;
import org.donutsmp.ghosts.ghosts.packets.ServerStartEvent;
import redis.clients.jedis.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class RedisAPI {

    public GenericObjectPoolConfig<Jedis> getConfig(){
        GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(Integer.MAX_VALUE);
        poolConfig.setMaxIdle(1000);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnReturn(true);
        poolConfig.setBlockWhenExhausted(false);
        return poolConfig;
    }

    private final JedisPool jedisPool;
    private final ThreadPoolExecutor executor;
    private final List<Thread> pubsubs = new LinkedList<>();

    public RedisAPI(String host, int port, String password){
        if (password.isEmpty()){
            jedisPool = new JedisPool(getConfig(),host,port,0);
        } else {
            jedisPool = new JedisPool(getConfig(),host,port,0,password);
        }
        executor = new ThreadPoolExecutor(4, 128, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        ServerStartEvent serverStartEvent = new ServerStartEvent();
        byte[] encode = serverStartEvent.encode();
        publishData("DonutLobbyGhosts".getBytes(StandardCharsets.UTF_8), encode);
    }

    public void publishData(byte[] channel,byte[] message){
        executor.execute(()->{
            try (Jedis jedis = jedisPool.getResource(); Pipeline pipeline = jedis.pipelined()){
                pipeline.publish(channel,message);
            } catch (Exception io){
                io.printStackTrace();
            }
        });
    }

    public void publishDataTick(byte[] channel,byte[] message){
        executor.execute(()->{
            try (Jedis jedis = jedisPool.getResource(); Pipeline pipeline = jedis.pipelined()){
                pipeline.publish(channel,message);
            } catch (Exception io){
                io.printStackTrace();
            }
        });
    }

    public void registerPubSub(BinaryJedisPubSub sub, byte[] ... names){
        Thread thread = new Thread(()-> {
            try (Jedis jedis = jedisPool.getResource()){
                jedis.subscribe(sub, names);
            } catch (Exception io){
                io.printStackTrace();
            }
        });
        pubsubs.add(thread);
        thread.start();
    }

    public void stop(){
        try {
            this.jedisPool.destroy();
        } catch (Exception ignored){ }
        for (Thread thread : pubsubs) {
            try {
                thread.interrupt();
            } catch (Exception ignored){ }
            try {
                thread.stop();
            } catch (Exception ignored){ }
        }
        executor.shutdown();
    }

    public ThreadPoolExecutor getExecutor(){
        return executor;
    }

}
