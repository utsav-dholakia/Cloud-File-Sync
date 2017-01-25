import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientProgram {

public static void main(String[] args) {
// TODO Auto-generated method stub
		
	try {
		// open a socket to server
		Socket echoSocket = new Socket("ec2-52-34-150-254.us-west-2.compute.amazonaws.com", 4444); 
		//output stream of the socket
		PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),true);
		//input stream of the socket
		BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));	
				
		//Initial sync function which sends the status returned from server
		int status = initSync(echoSocket); 
		
		if(status == 1)
		{
			System.out.println("Sync Complete");
			int optionSelect;
			do{
				System.out.println("Please select an option from below:");
				System.out.println("1. Upload a file");
				System.out.println("2. Download a file");
				System.out.println("3. Terminate");
				Scanner reader = new Scanner(System.in);  // Reading from System.in
				System.out.println("Enter a number: ");
				optionSelect = reader.nextInt(); 

				switch(optionSelect)
				{
				case 1:
					uploadFile(echoSocket);
					break;
					
				case 2:
					downloadFile(echoSocket);
					break;
					
				case 3:
					out.println("Close Connection");
					System.out.print("Server :" + in.readLine());
					
					reader.close();
					out.close();
					in.close();
			        echoSocket.close();
					break;
				}
			}while(optionSelect != 3);
			
			/*//------------File metadata finding
			String filePath = "/Users/utsavdholakia/OneDrive/Educational/UTD/Advacned Computer Networks/Team Project/Sample File.rtf";
			FileReader fileReader = 
	                new FileReader(filePath);
			
	            // Always wrap FileReader in BufferedReader.
			BufferedReader fileBufferedReader = 
	                new BufferedReader(fileReader);
			Path p = Paths.get(filePath);
		    BasicFileAttributes view
		       = Files.getFileAttributeView(p, BasicFileAttributeView.class)
		              .readAttributes();
			System.out.println("Creation Time : "+view.creationTime());
			System.out.println("Modification Time : "+view.lastModifiedTime());
			*/
			}
			else
			{
				System.out.println("There was an error connecting to the server. Closing the connection.");
				out.close();
				in.close();
		        echoSocket.close();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e){
			e.printStackTrace();
		}	
	}
	
private static void downloadFile(Socket echoSocket) throws IOException,URISyntaxException {
	// TODO Auto-generated method stub
	//output stream of the socket
	
	PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),true);
	//input stream of the socket
	BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));	
	//For file reading and writing
	//DataOutputStream dos = new DataOutputStream (echoSocket.getOutputStream());
	//ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	
	int optionSelect;
	do{
		System.out.println("Please select an option from below:");
		System.out.println("1. Show filelist from server");
		System.out.println("2. Download file");
		System.out.println("3. Done with downloading!!");
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		System.out.println("Enter a number: ");
		optionSelect = reader.nextInt();
		
		switch(optionSelect)
		{
		case 1:			
			String remoteFileList = "";
			out.println("Send File List");
			System.out.println("Client : Send File List");
			
			remoteFileList = in.readLine();	//server sent file list in a string
			out.println("File list received");	//reply back with a message
			System.out.println("Client : File list received");
			//System.out.println("Server :" + remoteFileList);	//server file list printing without formatting first
			String []remoteFileName = remoteFileList.split(";");	//get filenames from the string
			System.out.println("Server file list");
			for(int i = 0; i < remoteFileName.length ; i++)			//print all file names
			{
				System.out.println("["+ i+1 + "] :" + remoteFileName[i]);
			}
			break;
			
		case 2:
			reader = new Scanner(System.in);
			System.out.println("Enter a file name: ");
			String fileToDownload = reader.nextLine();	//use reader variable to read from system.in
			String filePath = "/Users/utsavdholakia/OneDrive/Educational/UTD/Advacned Computer Networks/Team Project/CloudFolder/";
			filePath = filePath.concat(fileToDownload);
			File myFile = new File(filePath);
			//FileOutputStream fileWriter = new FileOutputStream(myFile);
			//DataInputStream dis = new DataInputStream(echoSocket.getInputStream());
			BufferedWriter fileBufferedWriter = new BufferedWriter (new FileWriter(myFile));
			//byte[] byteData = new byte[4096];
			//long fileSize = 0;
			//int byteRead = 0;
			out.println("Download File");
			System.out.println("Client : Download a file");
			if(in.readLine().equals("Send File Name"))
			{
				System.out.println("Server :Send File Name");
				out.println(fileToDownload);
				System.out.println("Client :" + fileToDownload);
			}
			String line = in.readLine();
			//InputStream is = echoSocket.getInputStream();
			//int bytesRead = 0;
			if(line.equals("File Found"))
			{
				/*fileSize = in.read();
				System.out.println("FileSize :" + fileSize);
				byteRead = dis.read(byteData);
				while(byteRead <= fileSize)
				{
					byteRead += dis.read(byteData);
					fileWriter.write(byteData);
					//line = in.readLine();
				}*/
				line = in.readLine();
				while(!line.equals("File Sent"))
				{
					fileBufferedWriter.write(line);
					//fileWriter.writeBytes(line);
					line = in.readLine();
				}
				//line = in.readLine();
				System.out.println("Server :" + line);
				if(line.equals("File Sent"))
				{
					out.println("File Download Success");
					System.out.println("File downloaded successfully!!!");
				}
				else
				{
					out.println("File Download Error");
					System.out.println("Error in downloading file!!!");
				}
				fileBufferedWriter.close();
				//fileWriter.close();
			}
			else if(line.equals("Wrong File Name"))
			{
				System.out.println("Server :" + line);
			}
			//reader.close();
			break;
			
		case 3:
			break;
		}
		//reader.close();
	}while(optionSelect != 3);
}

private static void uploadFile(Socket echoSocket) throws IOException, URISyntaxException {
	// TODO Auto-generated method stub
		//output stream of the socket
		PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),true);
		//input stream of the socket
		BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));	
		//For file reading and writing
		//DataOutputStream dos = new DataOutputStream (echoSocket.getOutputStream());
		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		int optionSelect;
		do{
			System.out.println("Please select an option from below:");
			System.out.println("1. Show filelist from local folder");
			System.out.println("2. Upload file");
			System.out.println("3. Done with uploading!!");
			Scanner reader = new Scanner(System.in);  // Reading from System.in
			System.out.println("Enter a number: ");
			optionSelect = reader.nextInt();
			
			switch(optionSelect)
			{
			case 1:	
				File localFolder = new File("/Users/utsavdholakia/OneDrive/Educational/UTD/Advacned Computer Networks/Team Project/CloudFolder/");
				File[] localListOfFiles = localFolder.listFiles();
				System.out.println("Local file list");
				for(int i = 0; i < localListOfFiles.length ; i++)			//print all file names
				{
					System.out.println("["+ i+1 + "] :" + localListOfFiles[i].getName());
				}
				break;
				
			case 2:
				reader = new Scanner(System.in);
				System.out.println("Enter a file name: ");
				String fileToUpload = reader.nextLine();	//use reader variable to read from system.in
				//System.out.println("Upload File Option -- fileToUpload :" + fileToUpload);
				String filePath = "/Users/utsavdholakia/OneDrive/Educational/UTD/Advacned Computer Networks/Team Project/CloudFolder/";
				//URI URIFilePath = new URI(("file:///"+ filePath.replaceAll(" ", "%20")));
				//filePath = URIFilePath.toString();
				
				//--------------check if the filename is correct
				localFolder = new File(filePath);
				localListOfFiles = localFolder.listFiles();
				//System.out.println("Upload File option -- fileList :" + localListOfFiles.toString());
				boolean fileFound = false;
				for(int i = 0; i < localListOfFiles.length ; i++)
				{
				    if(localListOfFiles[i].getName().equals(fileToUpload))
				    {
				    	fileFound = true;
				    	break;
				    }
				}
				filePath = filePath.concat(fileToUpload);
				//URIFilePath = new URI(("file:///"+ filePath.replaceAll(" ", "%20")));
				//filePath = URIFilePath.toString();
				//System.out.println("Upload File option -- filePath :" + filePath);
				if(fileFound)
				{
					out.println("Upload File");
					System.out.println("Client : Upload a file");
					if(in.readLine().equals("Send File name"))
					{
						System.out.println("Server :Send File Name");
						out.println(fileToUpload);
						System.out.println("Client :" + fileToUpload);
					}
					File myFile = new File(filePath);
					//long fileSize = myFile.length();
					//int byteRead = 0;
					//byte[] byteData = new byte[4096];
					BufferedReader fileBufferedReader = new BufferedReader(new FileReader(myFile));
					//FileInputStream fileReader = new FileInputStream(myFile);
					String line = in.readLine();
					if(line.equals("Send File"))
					{
						while((line = fileBufferedReader.readLine()) != null)
						{
							out.println(line);
						}
						//while((line = fileReader.readLine()) != null)
						//out.println(fileSize);
						/*while((byteRead = fileReader.read(byteData)) != -1)
						{
							dos.write(byteData);
							System.out.println("Client : ByteRead = " + byteRead);
						} */
						out.println("File Sent");
						System.out.println("Client : File Sent");
						if((line = in.readLine()).equals("File Upload Success"))
						{
							System.out.println("File uploaded successfully!!!");
						}
						else if((line = in.readLine()).equals("File Upload Error"))
						{
							System.out.println("Error in uploading file!!!");
						}
						fileBufferedReader.close();
						//fileReader.close();
					}
				}
				else
				{
					System.out.println("File Name is wrong");
				}
				//reader.close();
				break;
				
			case 3:
				break;
			}
			//reader.close();
		}while(optionSelect != 3);
		
}

private static int initSync(Socket echoSocket) throws IOException,UnknownHostException {
		// TODO Auto-generated method stub
		//output stream of the socket
		PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),true);
		//input stream of the socket
		BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));	
		
		System.out.println("Starting synchronization with server...");
		out.println("Sync");
		System.out.println("Client : Sync");
		String inputCommand = in.readLine();
		if(inputCommand.equals("Sync ACK"))
		{
			System.out.println("Server :" + inputCommand);
			out.println("ACK");
			System.out.println("Client : ACK");
			return 1;
		}
		else
		{
			System.out.println("Server :" + inputCommand);
			out.println("Close connection");
			System.out.println("Client : Close Connection");
			System.out.println("Server :" + inputCommand);
			return -1;
		}
}
}
