package com.phlink.example.server.contoller;

import com.google.protobuf.ByteString;
import com.phlink.example.server.service.push.CacheUtil;
import com.phlink.grpc.examples.proto.Notify;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wen
 * @create 2019-02-15 23:32
 */
@Slf4j
@RestController
public class PushContoller {

    @PostMapping(value="/push-message/{uuid}")
    public void push(@PathVariable String uuid) {
        StreamObserver<Notify> observer = CacheUtil.serverCache.getObserver(uuid);
        if(observer != null ){
            Notify notify = Notify.newBuilder()
                    .setTopicName(Notify.Topic.CHANNEL_1)
                    .setStatus("OK")
                    .setMessage(ByteString.copyFromUtf8("CHANNEL----1"))
                    .build();
            observer.onNext(notify);
        }else{
            log.error("not found");
        }
    }

}
