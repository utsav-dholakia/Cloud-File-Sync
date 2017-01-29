# Cloud File Sync
The project emphasizes on developing an application to exchange data and keep it synchronized between laptop, smartphone (android) and cloud (AWS). 
The application makes use of client-server model using socket programming. The protocol used here is TCP (Transmission control protocol) to send or receive any file. The reason why UDP (User datagram protocol) can’t be used as it is not reliable for transmission of important data and the fear of file being lost. Laptop initiates TCP connection with the cloud and it sends files to the cloud. The cloud upon receiving the file updates it. Similarly smartphone initiates TCP connection with the cloud and sends file to the cloud. The cloud, when it receives the files updates it.