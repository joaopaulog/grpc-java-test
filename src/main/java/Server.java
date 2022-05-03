import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import test_bidi_stream.BidiStream;
import test_bidi_stream.RPCBidiStreamGrpc;

import java.io.IOException;

public class Server {

    public void run(int port) throws IOException, InterruptedException {
        System.out.println("Server start");

        var server = ServerBuilder
                .forPort(port)
                .addService(new BidiServer())
                .build();

        server.start();
        server.awaitTermination();
    }


    private static class BidiServer extends RPCBidiStreamGrpc.RPCBidiStreamImplBase {

        @Override
        public StreamObserver<BidiStream.Req> go(final StreamObserver<BidiStream.Res> responseObserver) {
            System.out.println("Created a stream.");

            return new StreamObserver<>() {

                private final BidiStream.Res[] buffer = new BidiStream.Res[1024];
                private int bufferIndex = 0;

                @Override
                public void onNext(BidiStream.Req request) {
                    final String response = request.getS()
                        .substring(Math.max(0, request.getI()), Math.min(Math.max(0, request.getI()) + Math.max(1, request.getC()), request.getS().length()))
                        .repeat(Math.max(1, request.getN()));

                    BidiStream.Res res = BidiStream.Res.newBuilder()
                        .setId(request.getId())
                        .setR(response)
                        .build();

                    if (bufferIndex == buffer.length) {
                        sendMessages();
                        bufferIndex = 0;
                    }

                    buffer[bufferIndex++] = res;
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {
                    sendMessages();
                    System.out.println("Closing a stream.");
                    responseObserver.onCompleted();
                }

                private void sendMessages() {
                    for (int i = 0; i < bufferIndex; i++) {
                        responseObserver.onNext(buffer[i]);
                        buffer[i] = null;
                    }
                }
            };
        }
    }


}