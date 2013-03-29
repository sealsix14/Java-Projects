ackage PKI;

//Brandon Jacobs
//Jul 18, 2011
//Computer Security 
//Dr.Harris

 
import java.io.*;
import java.util.*;
import java.math.BigInteger;


public class GenerateKeys {
  
	//Encode the message and Integer 
	static BigInteger Encode = new BigInteger("0");
	
	//b will be 1, 128*2, 128*3, etc.
	static BigInteger base = new BigInteger("1");
	
	// Default key length
	static final int DEFAULT_KEY_LENGTH = 512;

	public static void main(String[] args) throws FileNotFoundException {
		Scanner input = new Scanner(System.in);
		boolean interactive = false;		//Set interactive for command Line.
		boolean thatsAll = false;			// Used for ending the program.
		String FileName = "";				//Create empty file name.
		String cleartextFile = "";			//Create empty cleartext File.
		String encryptedFile = "";			//Create empty encrypted File.
		String fileText = "";				//Create variable for the fileText.
		int userOption;						//Initialize userOption.
		int keyLength = 0;					//Initial keyLength to 0.
		
		
		// All command Line arguments were created with thanks to my Father for helping with providing some guidlines 
		//		for the basics of creating command line driven programs. 
		
		
		// Continue to process user requests until requested to STOP
		while(thatsAll == false){
			
		  try{
			
			// Set userOption to 0 each pass 
			userOption = 0;
			
			// If a single command line argument, print the "usage: " information for the program  
			if(args.length == 1) {
				System.out.println("\nUsage: GenerateKeys -o ['0' to gen keys, '1' to encrypt, or '2' to decrypt] -k keyLength -e encryptedFileName -c cleartextFileName\n");
				System.exit(1);
			}
			else if(args.length > 1 && !interactive) {
				
				interactive = false;					// command line driven
				
				userOption = 1;							// default (gen keys)
				cleartextFile = "Message.txt";			// default
				encryptedFile = "Encrypted.txt";		// default
				keyLength = DEFAULT_KEY_LENGTH;			// default
				
				// Determine which argument pairs have been specified and provide defaults for those
				// not provided. The default for the userOption is to generate new keys.
				
				for(int i=0; i < args.length; i+=2){
					
					// userOption - generate keys (0), encrypt (1), or decrypt (2)
					if(args[i].contentEquals("-o")) {
							if((i+1) < args.length)
								userOption = Integer.parseInt(args[i+1]);
					}
					
					// -k "key length"
					else if(args[i].contentEquals("-k")) {
						if((i+1) < args.length)
							keyLength = Integer.parseInt(args[i+1]);
					}
					
					// -c "clear text file name"
					
					else if(args[i].contentEquals("-c")) {
						if((i+1) < args.length)
							cleartextFile = args[i+1];
					}
					
					// -e "encrypted text file name"
					
					else if(args[i].contentEquals("-e")) {
						if((i+1) < args.length)
							encryptedFile = args[i+1];
					}
				}
			}
			else {
				
				interactive = true;
				
				// No command line arguments specified - interactive mode
				
				System.out.println("Options\n----------\n[1] Generate Keys\n[2] Encrypt\n[3] Decrypt");
				System.out.print("Choose Option: ");
				
				// Obtain the userOption from the user input
				
				userOption = input.nextInt();
			}
			
			// User must specify a valid option - gen keys (1), encrypt (2) or decrypt (3)
				
			
			if(userOption != 1 && userOption != 2 && userOption != 3) {
					
				// Error - user entered invalid option so try again or exit
				
				System.out.println("\nInvalid option specific - please choose '1' to generate keys, '2' to Encrypt; '3' to decrypt\n");
				System.exit(1);
			}
			
			// Set file name to default based on function if not specified by user and not interactive mode
			
			if(interactive && (userOption != 1)) {
				
				// Obtain file name from user input if in interactive mode (gen keys option '1' does not need file name)
				
				System.out.print("File: ");
				FileName = input.next();				//The next input will be the file name
			}
			else if((userOption == 2) && !cleartextFile.isEmpty())
				FileName = cleartextFile;
			else if((userOption == 3) && !encryptedFile.isEmpty())
				FileName = encryptedFile;
			
			// Read the cleartext or encrypted text file contents
			
			if(!FileName.isEmpty())
				fileText = readFileAsString(FileName);	//Use the readFileAsString method to convert message into a string to encrypt or decrypt
			
			// If user selected encrypt option, then generate keys and encrypt data writing the keys,
			// modulus, and encrypted text to files
			
			if(userOption == 1) {
				
				if(!interactive && (keyLength == 0))
					keyLength = DEFAULT_KEY_LENGTH;				// Set key length to default if not in interactive mode
				else if(interactive)
					keyLength = 0;								// Force to 0 if switched to interactive mode
				
				KeyGenerator(keyLength);						// Run the keyGenerator program to generate E and D and N and save to files
			}
			
			// Encrypt - encrypt the text using the public key and save to Encrypted.txt
			
			else if(userOption == 2) {
				EncryptPKI(fileText);
			}
			
			// Decrypt - read Encrypted.txt and the private key file and decrypt the text message
			
			else {
				DecryptPKI(fileText);
			}
						
			System.out.print("\nPerform additional functions ('Yes' or 'No')? ");
			
			// See if the user wants to continue with another function or exit the program
			
			String userReponse = input.next();
			
			if (userReponse.compareTo("No") == 0){
				System.out.print("\nThank you - exiting the program.\n");	
				System.exit(1);
			}
			else
				interactive = true;		// must be interactive on 2nd and subsequent pass
			
		  } catch (IOException e) {System.out.println("Invalid Input, Please Try Again");
		  }
	   }
	}
	
	// Function to open a file containing a string and to read the file contents into
	// a String returned to the caller
	
	private static String readFileAsString(String fileName) {
	    
	    File file = new File(fileName);				//Get FileName to read through
	    
	    char[] buffer = null;						//Create char array for the file
	    
	    try {
	    	BufferedReader bufferedReader = new BufferedReader(new FileReader(file));		//Create Buffered Reader to read through file
	        buffer = new char[(int)file.length()];											//the char array will be set to the length of the file
	
	        int i = 0;

        	int c = bufferedReader.read();													//Set c to read the file at each character holding its value in the form of an int

	        while (c != -1) {																//While the file still has something to read, the array adds another char at a time and
	            buffer[i++] = (char)c;														//reads the next char to put into the array.
	            c = bufferedReader.read();
	        }
        }
        catch (FileNotFoundException e) {System.out.println("File Not Found, Please Enter Readable File Then Try Again");
	    }
        catch (IOException e){System.out.println("Invalid Input, please Try Again");
        }

	    return new String(buffer);															//Return a String with the contents of the file to use for Encrypt,Decrypt
	}

	private static void KeyGenerator(int keyLength) throws FileNotFoundException {
	
		int x = 0;
		
		// Request key length from user unless key length value provided by caller
		if(keyLength == 0) {
			Scanner input = new Scanner(System.in);
			System.out.print("Enter BitLength for Keys: ");
			x = input.nextInt();
		}
		else
			x = keyLength;
		
		// Generate a random number to use for the BigInteger prime() function
		Random r = new Random();
		
		BigInteger p,q, N;
		
		p = BigInteger.probablePrime(x,r);
		q = BigInteger.probablePrime(x,r);
		
		// Generate the modulus (to be saved in a file later)
		N = p.multiply(q);					// N = p*q
		String NValue = N.toString();		// Convert to string to write to file
		
		//	Calculate phi(N) = Euler totient function of N
		// 		i.e. the number of integers < N that are relatively prime to N
		// 		Since N = p * q, then phi(N) = (p-1)(q-1)
		BigInteger pMinus1 = p.subtract(BigInteger.ONE);
		BigInteger qMinus1 = q.subtract(BigInteger.ONE);
		BigInteger phiN = qMinus1.multiply(pMinus1);
		
		// Generate e (public key) and d (private key) - stored in file later
		BigInteger e = BigInteger.probablePrime((x-5),r);
		BigInteger d = e.modInverse(phiN);
		
		// Routine that quickly validates public & private key generation
		validateKeyGeneration(e, d, N);
		
		// Convert the keys to strings so they can be written to files
		String E = e.toString();
		String D = d.toString();
		
		//	Create Two files for the public and private keys to be stored
		//		along with modulus N which gives the files contents (e,N) (d,N).
		
		//  Create printwriter to write the values of (e,N) and (d,N)
		//		to the files publickey.txt and privatekey.txt
		
		File file = new File ("PublicKey.txt");
		PrintWriter output = new PrintWriter(file);
		output.print(E + "\n" + NValue);
		output.close();
		
		File file2 = new File ("PrivateKey.txt");
		PrintWriter output2 = new PrintWriter(file2);
		output2.print(D + "\n" + NValue);
		output2.close();
		
		// Output the values of the keys and modulus to the console
		
		System.out.println("Key Length:  " + x);
		System.out.println("Public Key:  " + E);
		System.out.println("Private Key: " + D);
		System.out.println("Modulus:     " + NValue + "\n");
	}
	
	// Validate the public and private key generated by testing a string to encrypt & decrypt to make sure they match
	private static void validateKeyGeneration(BigInteger publicKey, BigInteger privateKey, BigInteger modulus)
	{
		String clearText = "This is a test string in cleartext to use for encryption/decryption.";
		BigInteger biClearText = new BigInteger(clearText.getBytes());
		
		BigInteger encryptedText = biClearText.modPow(publicKey, modulus);
		BigInteger decryptedText = encryptedText.modPow(privateKey, modulus);
		
		String clearText2 = new String(decryptedText.toByteArray());
		
		System.out.println("Clear Text:          " + clearText);
		System.out.println("Clear Text (BI):     " + biClearText);
		System.out.println("Encrypted Text (BI): " + encryptedText);
		System.out.println("Decrypted Text (BI): " + decryptedText);
		System.out.println("Clear Text 2   :     " + clearText2);
		
		if(clearText.compareTo(clearText2) != 0)
			System.out.println("Encryption/Decryption failed:     decrypted string did not match original");
		else
			System.out.println("Encryption/Decryption successful: decrypted string matched original");
		
		System.out.println(" ");
	}
	
	// Encrypt the text argument using the public key and write to the specified file
	private static void EncryptPKI(String textToEncrypt) throws IOException {
		
		FileWriter outputStream = null;
		
		try{
			
			
			// Read from publicKey.txt to obtain (e,N)
			
			FileInputStream fstream = new FileInputStream("PublicKey.txt");
			DataInputStream dstream = new DataInputStream(fstream);
			BufferedReader bstream = new BufferedReader(new InputStreamReader(dstream));
			
			String E;
			String N;
			
			E = bstream.readLine();		// Read the public key
			N = bstream.readLine();		// Read the modulus
			
			// Convert the key and modulus to BigIntegers
			
			BigInteger e = new BigInteger(E);
			BigInteger n = new BigInteger(N);
			
			// To break the encrypted data into multiple blocks, simply determine the number
			// of characters in the String to be encrypted in each block. Then, iterate over
			// the encryption process encrypting the number of characters in a block and then
			// writing each block to the file. The decryption routine would need to know the
			// size of the blocks.
			
			System.out.println("Cleartext String: " + textToEncrypt);
			
			File file = new File ("Encrypted.txt");
			PrintWriter output = new PrintWriter(file);
			
			// Create strings to hold the length and the index of the strings we are trying to encrypt from the overall string
			//		then using a while loop, go through the length of the file and encode each segment writing it to the file
			String encryptSubstr;
			int encryptStringLength = textToEncrypt.length();
			int encryptStringIndex = 0;
			
			while(encryptStringLength > 0) {
				if(encryptStringLength >= 4)
					encryptSubstr = textToEncrypt.substring(encryptStringIndex, encryptStringIndex + 4);
				else
					encryptSubstr = textToEncrypt.substring(encryptStringIndex, encryptStringIndex + encryptStringLength);
				
				Encode = new BigInteger(encryptSubstr.getBytes());
				BigInteger encryptedData = Encode.modPow(e, n);
				String encryptedString = encryptedData.toString();
				
				System.out.println("Encrypted String[" + encryptStringIndex + "]: " + encryptedString);
				
				output.print(encryptedString + "\n");
				
				encryptStringLength -= 4;
				encryptStringIndex  += 4;
			}
			
			output.close();
			
//  original code starts here			
		} finally {
			if(outputStream != null){
				outputStream.close();
			}
		}
}
		
	// Decrypt the text string using the private key
	
	private static void DecryptPKI(String textToDecrypt){
		
		//Decrypt the message using the private key (d,N)
		
		String D = "";
		String N = "";
		
	    try {
	    
	      // Open the private key file to obtain key and modulus
	    	
	      FileReader fileReader = new FileReader("PrivateKey.txt");
	      BufferedReader bufferedReader = new BufferedReader(fileReader);
	      
	      // Read the private key and modulus strings
	      
	      D = bufferedReader.readLine();
	      N = bufferedReader.readLine();
	      
	      fileReader.close();
	      bufferedReader.close();
	      
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
		
	    // Convert the private key and modulus strings to BigIntegers
	    
		BigInteger d = new BigInteger(D);
		BigInteger n = new BigInteger(N);
		
		// If the encrypted data was done in blocks, this routine would need to be
		// modified to decrypt each block separately and then concatenate the clear
		// text strings resulting from decryption together to form the original text
		
		String textToDecryptSegment [] = textToDecrypt.split("\n");
		String clearText = "";
		int segmentIndex = 0;
		
		while(segmentIndex < textToDecryptSegment.length) {
			BigInteger encryptedMessage = new BigInteger(textToDecryptSegment[segmentIndex]);
			BigInteger decryptedMessage = encryptedMessage.modPow(d, n);
			String clearTextSegment = new String(decryptedMessage.toByteArray());
			clearText = clearText.concat(clearTextSegment);
			segmentIndex++;
		}
		
		System.out.println("Cleartext (after decryption): " + clearText + "\n");
	}
}





