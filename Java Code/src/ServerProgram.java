import java.io.*;
import java.net.*;

public class ServerProgram {

public static void main(String[] args) {
// TODO Auto-generated method stub

	try{
		//Define a new serverSocket 
		ServerSocket serverSocket = new ServerSocket(4444);
		//serverSocket waits for a connection and accepts it and results in a new port=clientSocket
		Socket clientSocket = serverSocket.accept();
		//get output stream from this port
		PrintWriter out = new PrintWriter (clientSocket.getOutputStream(),true);
		//For file reading and writing
		DataOutputStream dos = new DataOutputStream (clientSocket.getOutputStream());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//get input stream for this port
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		int status = initSync(clientSocket);
		
		if(status == 1)
		{
			System.out.println("Sync Complete");
			String inputCommand;
			
			while((inputCommand = in.readLine()) != null)
			{
				File serverFolder = new File("./CloudFolder");
				File[] serverListOfFiles = serverFolder.listFiles();
				System.out.println("Client:" + inputCommand);
				
				if(inputCommand.equals("Close Connection"))
				{
					out.println("Communication done!! Ciao...");
					
					System.out.println("Server : Communication done!! Ciao...");
					
					out.close();
					in.close();
					dos.close();
					clientSocket.close();
					serverSocket.close();
					break;
				}
				else if(inputCommand.equals("Send File List"))
				{
					String serverFileNames = "";
					for(int i = 0; i < serverListOfFiles.length ; i++)
					{
						serverFileNames = serverFileNames.concat(serverListOfFiles[i].getName());
						serverFileNames = serverFileNames.concat(";");
					}
					System.out.println(serverFileNames);
					out.println(serverFileNames);
					if(in.equals("File list received"))
					{
						System.out.println("Client : File list received");
					}
				}
				else if(inputCommand.equals("Download File"))
				{
					
					System.out.println("Client : Download File");
					out.println("Send File Name");
					System.out.println("Server : Send File Name");
					String filePath = "./CloudFolder/";
					String fileName = in.readLine();
					System.out.println("Client : "+ fileName);
					
					//--------------check if the filename is correct
					File localFolder = new File(filePath);
					File[] localListOfFiles = localFolder.listFiles();
					boolean fileFound = false;
					for(int i = 0; i < localListOfFiles.length ; i++)
					{
					    if(localListOfFiles[i].getName().equals(fileName))
					    {
					    	fileFound = true;
					    	break;
					    }
					} 
					if(fileFound)
					{
						out.println("File Found");
						System.out.println("Server : File Found");
						filePath = filePath.concat(fileName);
						File myFile = new File(filePath);
					    BufferedReader fileBufferedReader = new BufferedReader(new FileReader(myFile));
					    //FileInputStream fileReader = new FileInputStream(myFile);
					    //byte[] byteData = new byte[4096];
					    //long fileSize = myFile.length();
					    //int byteRead = 0;
					    //while((line = fileReader.readUTF()) != null)
					    //out.print(fileSize);
					    /*while((byteRead = fileReader.read(byteData)) != -1)
					    {
					    	//out.write(byteData);
					    	dos.write(byteData);
					    }*/
						String line = "";
						while((line = fileBufferedReader.readLine()) != null)
						{
							out.println(line);
						}
						out.println("File Sent");
						System.out.println("Server : File Sent");
						line = in.readLine();
						if(line.equals("File Download Success"))
						{
							System.out.println("Client : File Downloaded Successfully!!");
						}
						else if(line.equals("File Download Error"))
						{
							System.out.println("Client : There was an error in downloading the file");
						}
						fileBufferedReader.close();
						//dos.flush();
						//fileReader.close();
					}
					else
					{
						out.println("Wrong File Name");
						System.out.println("Server : Wrong File Name");
					}
				}
				else if(inputCommand.equals("Upload File"))
				{
					System.out.println("Client : Upload File");
					out.println("Send File name");
					System.out.println("Server : Send File Name");
					String filePath = "./CloudFolder/";
					String fileName = in.readLine();
					System.out.println("Client : "+ fileName);
					
					filePath = filePath.concat(fileName);
					File myFile = new File(filePath);
					BufferedWriter fileBufferedWriter = new BufferedWriter(new FileWriter(myFile));
					//FileOutputStream fileWriter = new FileOutputStream(myFile);
					//DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
					//long fileSize = myFile.length();
					//byte[] byteData = new byte[4096];
					//int byteRead = 0;
					out.println("Send File");
					//fileSize = in.read();
					//System.out.println("FileSize :" + fileSize);
					//byteRead = dis.read(byteData);
					/*while(byteRead <= fileSize)
					{
						byteRead += dis.read(byteData);
						fileWriter.write(byteData);
						//line = in.readLine();
					}*/
					String line = in.readLine();
					while(!line.equals("File Sent"))
					{
						fileBufferedWriter.write(line);
						//fileWriter.writeBytes(line);
						line = in.readLine();
					}
					
					System.out.println("Client : " + line);
					if(line.equals("File Sent"))
					{
						out.println("File Upload Success");
						System.out.println("Server : File Uploaded Successfully!!");
					}
					else
					{
						out.println("File Upload Error");
						System.out.println("Server : There was an error in uploading the file");
					}
					//fileWriter.close();
					fileBufferedWriter.close();
				}	
			}
		}
		else
		{
			System.out.println("There was an error connecting to the client. Closing the connection.");
			out.close();
			in.close();
			clientSocket.close();
			serverSocket.close();
		}	
	}
	catch(IOException e){
		System.out.println("Exception caught when trying to listen to the port");
		System.out.println(e.getMessage());
	}

}

private static int initSync(Socket clientSocket) throws IOException {
	// TODO Auto-generated method stub
	//get output stream from this port
	PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
	//get input stream for this port
	BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	System.out.println("Starting synchronization with client...");
	String inputCommand = in.readLine();
	if(inputCommand.equals("Sync"))
	{
		System.out.println("Client :" + inputCommand);
		out.println("Sync ACK");
		System.out.println("Server : Sync ACK");
		System.out.println("Client :" + in.readLine()); //ACK command to complete sync
		return 1;
	}
	else
	{
		System.out.println("Client :" + inputCommand);
		out.println("Close connection");
		System.out.println("Server : Close Connection");
		return -1;
	}			
}

}
