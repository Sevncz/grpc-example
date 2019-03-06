package com.phlink.example.server.service;

import com.phlink.grpc.examples.proto.HelloReply;
import com.phlink.grpc.examples.proto.HelloRequest;
import com.phlink.grpc.examples.proto.SimpleGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

/**
 * @author wen
 * @create 2019-02-15 17:38
 */
@Slf4j
@GrpcService
public class SimpleServerService extends SimpleGrpc.SimpleImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        log.info("request ---------> {}", req.getName());
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello ==> " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

}
