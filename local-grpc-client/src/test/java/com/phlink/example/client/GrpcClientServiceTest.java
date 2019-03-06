package com.phlink.example.client;

import com.phlink.grpc.examples.proto.Notify;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * @author wen
 * @create 2019-02-15 17:56
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LocalGrpcClientApplication.class)
@TestPropertySource(locations="classpath:application.yml")
public class GrpcClientServiceTest {

    @Autowired
    private GrpcClientService grpcClientService;

    @Test
    public void testSendMessage() {
        grpcClientService.sendMessage("wen");
    }

    @Test
    public void testFileUpload() {
        String file = "/Users/wen/dev/testWa/center/minicap.flv";
        Path localFile = Paths.get(file);
        grpcClientService.uploadFile(localFile, "1.flv");
    }

    @Test
    public void testSubscribe() {
        grpcClientService.subscribe(Notify.Topic.CHANNEL_0);
        try {
            TimeUnit.MINUTES.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
