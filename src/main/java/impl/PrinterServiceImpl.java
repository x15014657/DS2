package impl;

import com.conorjc.proto.*;
import io.grpc.stub.StreamObserver;
import java.util.EmptyStackException;

public class PrinterServiceImpl extends PrintServiceGrpc.PrintServiceImplBase {

        @Override //Unary
        public void printerStatus(PrinterStatusRequest request, StreamObserver<PrinterStatusResponse> responseObserver) {
            Printer printStatus = request.getStatus();
            boolean status = printStatus.getStatus();

            //create the response
            String result;
            if (!(status)) {
                result = "The Printer is Offline";
            } else {
                result = "The Printer is Online";
            }

            PrinterStatusResponse response = PrinterStatusResponse.newBuilder()
                    .setResult(result)
                    .build();

            //sends response to client
            responseObserver.onNext(response);
            //complete the RPC call
            responseObserver.onCompleted();
        }


        /*-------------------------------------------------------------------------------------------------------------------------- */

        @Override //ServerStreaming
        public void checkPrinter(CheckPrinterRequest request, StreamObserver<CheckPrinterResponse> responseObserver) {
            Printer checkPrinter = request.getStatus();
            boolean status = checkPrinter.getStatus();

            //create the action before the response
            if (status) {
                try {
                    for (int i = 1; i < 4; i++) {
                        String network = ("Network Test " + i + " of 3");
                        String cartridge = ("Cartridge Test " + i + " of 3");
                        String ink = ("Ink Test " + i + " of 3");

                        CheckPrinterResponse response = CheckPrinterResponse.newBuilder()
                                .setNetwork(network)
                                .setCartridge(cartridge)
                                .setInk(ink)
                                .build();

                        responseObserver.onNext(response);
                        Thread.sleep(1L);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("System is offline");
                }
            }
            String result = ("Network Connected: 192.168.0.1, " + " Ink Levels: Good, " + "Cartridges: Aligned. ");
            CheckPrinterResponse response = CheckPrinterResponse.newBuilder()
                    .setResult(result)
                    .build();
            responseObserver.onNext(response);
        }

    /*-------------------------------------------------------------------------------------------------------------------------- */

    //Client Streaming
    public StreamObserver<PrintTestRequest> longPrintTest(StreamObserver<PrintTestResponse> responseObserver) {
        return new StreamObserver<PrintTestRequest>() {
            String result = "";

            @Override
            public void onNext(PrintTestRequest value) {
                //client sends a message
                result += "" + value.getStatus().getTestpage();
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error");
            }

            @Override
            public void onCompleted() {
                //client is done
                responseObserver.onNext(
                        PrintTestResponse.newBuilder()
                                .setResult(result)
                                .build()
                );
                responseObserver.onCompleted();
            }
        };
    }

   //BiDi Streaming
    public StreamObserver<DocumentRequest> documentPrint(StreamObserver<DocumentResponse> responseObserver) {
        StreamObserver<DocumentRequest> requestObserver = new StreamObserver<DocumentRequest>() {
            @Override
            public void onNext(DocumentRequest value) {
                String result = "Printing Document Queue: " + value.getDts().getDocuments();
                DocumentResponse documentPrintResponse = DocumentResponse.newBuilder()
                        .setResult(result)
                        .build();

                responseObserver.onNext(documentPrintResponse);
            }

            @Override
            public void onError(Throwable t) {
                throw new EmptyStackException();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }
}
