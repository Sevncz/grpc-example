package com.phlink.example.server.service;

import com.phlink.example.server.service.push.CacheUtil;
import com.phlink.grpc.examples.proto.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author wen
 * @create 2019-02-15 22:23
 */
@Slf4j
@GrpcService
public class ExampleServerService extends ExampleServiceGrpc.ExampleServiceImplBase {

    @Override
    public StreamObserver<FileUploadRequest> fileUpload(StreamObserver<CommonReply> responseObserver) {
        return new StreamObserver<FileUploadRequest>() {
            private BufferedOutputStream mBufferedOutputStream = null;
            int mmCount = 0;

            @Override
            public void onNext(FileUploadRequest request) {
                log.info("onNext count: " + mmCount);
                mmCount++;

                byte[] data = request.getData().toByteArray();
                int offset = request.getOffset();
                int size = (int) request.getSize();
                String name = request.getName();

                String localPath = "/Users/wen/Desktop";
                Path localFile = Paths.get(localPath, name);
                if(!Files.exists(localFile)){
                    try {
                        Files.createFile(localFile);
                    } catch (IOException e) {
                        log.error("Receive logcat file, create error", e);
                    }
                }
                try {
                    if (mBufferedOutputStream == null) {
                        mBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(localFile.toFile()));
                    }
                    mBufferedOutputStream.write(data);
                    mBufferedOutputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(CommonReply.newBuilder()
                        .setStatus(0)
                        .setMessage("success")
                        .build());
                responseObserver.onCompleted();
                if (mBufferedOutputStream != null) {
                    try {
                        mBufferedOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        mBufferedOutputStream = null;
                    }
                }
            }
        };
    }


    @Override
    public void subscribe(TopicInfo topicInfo, StreamObserver<Notify> responseObserver) {
        CacheUtil.serverCache.loginToServer(topicInfo, responseObserver);
//        responseObserver.onNext(status);
//        responseObserver.onCompleted();
    }

}
