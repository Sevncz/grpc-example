package com.phlink.example.client;

import com.google.protobuf.ByteString;
import com.phlink.example.client.callback.DefaultObserver;
import com.phlink.grpc.examples.proto.*;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;

@Slf4j
@Service
public class GrpcClientService {
    private static final int BUFFER_SIZE = 1024*1024;

    @GrpcClient("local-grpc-server")
    private Channel serverChannel;

    public String sendMessage(final String name) {
        SimpleGrpc.SimpleBlockingStub stub = SimpleGrpc.newBlockingStub(serverChannel);
        HelloReply response = stub.sayHello(HelloRequest.newBuilder().setName(name).build());
        log.info(response.getMessage());
        return response.getMessage();
    }

    public void uploadFile(final Path localFile, final String filename) {
        log.info("tid: " +  Thread.currentThread().getId() + ", Will try to getBlob");
        StreamObserver<CommonReply> responseObserver = new StreamObserver<CommonReply>() {

            @Override
            public void onNext(CommonReply value) {
                log.info("Client file response onNext");
            }

            @Override
            public void onError(Throwable t) {
                log.info("Client file response onError");
            }

            @Override
            public void onCompleted() {
                log.info("Client file response onCompleted");
            }
        };

        StreamObserver<FileUploadRequest> requestObserver = ExampleServiceGrpc.newStub(serverChannel).fileUpload(responseObserver);
        try {
            if (!Files.exists(localFile)) {
                log.error("文件不存在, {}", localFile.toString());
                return;
            }
            log.info("路径：{}  文件大小: {}", localFile.toString(), Files.size(localFile));
            long bytesSize = Files.size(localFile);
            int kbSize = (int) (bytesSize / 1024);

            try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(localFile.toFile()))) {
                byte[] bbuf = new byte[BUFFER_SIZE];
                int offset = 0;
                int len;
                while ((len = in.read(bbuf, 0, BUFFER_SIZE)) != -1) {
                    ByteString byteString = ByteString.copyFrom(bbuf, 0, len);
                    FileUploadRequest req = FileUploadRequest.newBuilder()
                            .setName(filename)
                            .setType(FileUploadRequest.Type.video)
                            .setData(byteString)
                            .setSize(kbSize)
                            .setOffset(offset)
                            .setTimestamp(System.currentTimeMillis())
                            .build();
                    requestObserver.onNext(req);
                    offset += len;
                }
            }
        } catch (RuntimeException | IOException e) {
            requestObserver.onError(e);
            log.error("上传文件{} 失败", localFile, e);
        } finally {
            requestObserver.onCompleted();
        }
    }

    public void subscribe(Notify.Topic channel) {
        String uuid = UUID.randomUUID().toString();
        log.info("uuid ========= {}", uuid);
        ClientInfo clientInfo = ClientInfo.newBuilder().setClientId(uuid).build();
        ExampleServiceGrpc.ExampleServiceStub asyncStub = ExampleServiceGrpc.newStub(serverChannel);
        TopicInfo topicInfo = TopicInfo.newBuilder().setTopicName(channel.name()).setClientInfo(clientInfo).build();
        asyncStub.subscribe(topicInfo, new DefaultObserver(clientInfo.getClientId()));
    }
}