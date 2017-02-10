package common.messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

public class DeltaSync {

	static byte[] serverBytes;
	static long fileSize;
	static long clientFileSize;
	static int blocksMatched = 0;
	static int filePointer = 0;
	static int totalDataTransferred = 0;
	static int unmatchedBytes = 0;

	static int defaultChunkSize = 700;
	static int numberOfChunks;
	static List<Map.Entry<Integer, Long>> instructionStream = new ArrayList<>();
	static int chunksPut = 0;
	static int start = 0;
	static byte[] b = new byte[defaultChunkSize];
	static List<Long> serverChecksumTable = new ArrayList<Long>();

	static byte[] clientBytes;

	static CRC32 crc32 = new CRC32();
	
	public static List<Long> createSCT(File file){
		fileSize = file.length();
		System.out.println("IFL:" + file.length());

		serverBytes = new byte[(int) fileSize];
		try {
			readFile(serverBytes, file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		numberOfChunks = (int) Math.ceil(fileSize/(float)defaultChunkSize);

		System.out.println("number of chunks : " + numberOfChunks);

		while ((chunksPut != numberOfChunks)){

			if(fileSize <= 0){
				break;
			}
			if(fileSize < defaultChunkSize  &&  fileSize > 0){
				b = Arrays.copyOfRange(serverBytes, start, start + (int) fileSize);
			} 
			else{
				b = Arrays.copyOfRange(serverBytes, start, start+defaultChunkSize);
				start = start + defaultChunkSize;
			}
			fileSize = fileSize - defaultChunkSize;
			long serverHash = getCRC32Hash(b);
			//add server hash to server checksum table
			serverChecksumTable.add(serverHash);
			chunksPut++;
		}
		
		return serverChecksumTable;

	}
	
	public static List<Map.Entry<Integer, Long>> getInstructionStream(List<Long> SCT, File clientFile){
		clientFileSize = clientFile.length();
		long blockHash;
		int skipCount = 0;

		

		clientBytes =  new byte[(int) clientFileSize];
		try {
			readFile(clientBytes, clientFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("CL:" + clientBytes.length);
		byte[] firstBlock = Arrays.copyOfRange(clientBytes, 0, defaultChunkSize);
		long firstBlockHash = getCRC32Hash(firstBlock);
		if(SCT.contains(firstBlockHash)){
			System.out.println("first block matched");
			long blockRef = SCT.indexOf(firstBlockHash);
			java.util.Map.Entry<Integer, Long> instruction = new java.util.AbstractMap.SimpleEntry<>(1,
					blockRef);
			instructionStream.add(instruction);
			skipCount = defaultChunkSize-1;
			filePointer += defaultChunkSize;
			++blocksMatched;
			totalDataTransferred += 5;
		}
		else{
			System.out.println("first block not matched");
			java.util.Map.Entry<Integer, Long> instruction = new java.util.AbstractMap.SimpleEntry<>(0,
					(long) clientBytes[0]);
			instructionStream.add(instruction);
			skipCount = 0;
			filePointer++;
			++unmatchedBytes;
			totalDataTransferred += 1;
		}

		for(int i=0; i < clientBytes.length - defaultChunkSize; i++){
			//System.out.println("in loop");
			if(skipCount == 0){
				System.out.println("sc = 0");
				crc32.reset();
				crc32.update(clientBytes, i+1, defaultChunkSize);
				blockHash = crc32.getValue();
				if(SCT.contains(blockHash)){
					//IS = 1,ref
					System.out.println("m");
					long blockRef = SCT.indexOf(blockHash);
					java.util.Map.Entry<Integer, Long> instruction = new java.util.AbstractMap.SimpleEntry<>(1,
							blockRef);
					instructionStream.add(instruction);
					skipCount = defaultChunkSize-1;
					filePointer += defaultChunkSize;
					++blocksMatched;
					totalDataTransferred += 5;
				}
				else{
					System.out.println("not matched");
					if(i == clientBytes.length - (defaultChunkSize-1) - 1){
						//copy all bytes of last block (from i+1 to l-1) into IS               
						for(int k=i+1; k<clientBytes.length; k++){
							java.util.Map.Entry<Integer, Long> instruction = new java.util.AbstractMap.SimpleEntry<>(0,
									(long) clientBytes[k]);
							instructionStream.add(instruction);
							++unmatchedBytes;
							++filePointer;
							totalDataTransferred += 1;
						}
						break;
					}
					else{
						//copy current byte into IS.
						java.util.Map.Entry<Integer, Long> instruction = new java.util.AbstractMap.SimpleEntry<>(0,
								(long) clientBytes[i+1]);
						instructionStream.add(instruction);
						skipCount = 0;
						filePointer++;
						++unmatchedBytes;
						totalDataTransferred += 1;
					}
				}
			}
			else{
				//System.out.println("sc");
				--skipCount;
			}
			//copy bytes from FP till last


		}
		
		byte[] lastBlock = Arrays.copyOfRange(clientBytes, (int) filePointer, clientBytes.length);
		long lastHash = getCRC32Hash(lastBlock);
		if(SCT.contains(lastHash)){
			System.out.println("LBM");
			java.util.Map.Entry<Integer, Long> instruction = new java.util.AbstractMap.SimpleEntry<>(1,
					(long)SCT.indexOf(lastHash));
			instructionStream.add(instruction);
			++blocksMatched;
			totalDataTransferred += 5;
		}
		else{
			System.out.println("last not matched");
			for(long j=filePointer; j<clientBytes.length; j++){
				java.util.Map.Entry<Integer, Long> instruction = new java.util.AbstractMap.SimpleEntry<>(0,
						(long)clientBytes[(int)j]);
				instructionStream.add(instruction);
				++unmatchedBytes;
				totalDataTransferred += 1;
			}
		}
		return instructionStream;
		
	}
	public static void constructFile(List<Map.Entry<Integer, Long>> instructionStream, File f) throws IOException{
		//RandomAccessFile raf = new RandomAccessFile("C:\\test.txt", "rw");

		for(int index=0; index<instructionStream.size(); index++){
			System.out.println("in loop");
			if(instructionStream.get(index).getKey() == 1){
				/*
				 * TODO copy the block in new file.
				 */
				FileInputStream fis = new FileInputStream(f);
				FileOutputStream os = new FileOutputStream("C:\\Users\\Sarthak\\workspace\\Cloud Database\\newCopy.txt", true);
				byte[] blockBytes = new byte[defaultChunkSize];
				long blockRef = instructionStream.get(index).getValue();
				fis.skip(blockRef * defaultChunkSize);
				int c;
				int bytesRead = 0;
				while(((c = fis.read()) != -1)){
					blockBytes[bytesRead] = (byte) c;
					bytesRead++;
					if(bytesRead == defaultChunkSize){
						System.out.println("breaking: "+ bytesRead + " " + blockBytes.length);
						break;
					}
						
					
				}
				if(c == -1){
					System.out.println("c == -1" + "BR: " + bytesRead);
				}
				os.write(blockBytes);
				os.close();
				fis.close();
				
				
			}
			else{
				FileInputStream fis = new FileInputStream(f);
				FileOutputStream os = new FileOutputStream("C:\\Users\\Sarthak\\workspace\\Rsync\\newCopy.txt", true);
				os.write(instructionStream.get(index).getValue().intValue());
				fis.close();
				os.close();
			}
		}
	}
	
	public static long getCRC32Hash(byte[] block){
		crc32.reset();
		crc32.update(block, 0, block.length);
		return crc32.getValue();

	}
	
	public static void readFile(byte[] byteArray, File file) throws IOException{
		FileInputStream fis = new FileInputStream(file);
		int c = 0;
		int index = 0;
		while((c = fis.read()) != -1){
			byteArray[index++] = (byte) c;
		}
		
		fis.close();
	}
}
