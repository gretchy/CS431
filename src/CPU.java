import java.io.*;
import java.util.Scanner;

public class CPU {

	private static int tlb_pointer;
	private static TLB[] tlb;
	private static VirtualPgTable[] vpt;
	private static double[][] pm;
	private static OS os;
	
	public static void main(String[] args) throws FileNotFoundException {

		if (args.length != 1) {
			System.out.print("Please pass in a file.");
			System.exit(0);
		}

		File input_file = new File(args[0]);
		if (!input_file.canRead()) {
			System.out.print("Cannot read specified file.");
			System.exit(0);
		}
		
		// copy the original page files to it's own Output folder
		File outputDir = copyPageFiles();
		
		// initialize the TLB, virtual page table, and physical memory
		initialize();
		
		int instruction_counter = 0;
		
		// set up the output csv file
		PrintWriter pw = new PrintWriter(new File(outputDir + "/results.csv"));
		StringBuilder sb = new StringBuilder();
		sb.append("address");
		sb.append(',');
		sb.append("read/write");
		sb.append(',');
		sb.append("soft miss");
		sb.append(',');
		sb.append("hard miss");
		sb.append(',');
		sb.append("hit");
		sb.append(',');
		sb.append("dirty bit set");
		sb.append(',');
		sb.append("value");
		sb.append('\n');
		pw.write(sb.toString());
		pw.close();
		
		Scanner scan = new Scanner(input_file);
		while (scan.hasNextLine()) {
			int readOrWrite = 0;
			if (scan.hasNextInt()) {
				readOrWrite = scan.nextInt();
			} else {
				System.out.println("Program is done.");
				System.exit(0);
			}
			
			String va = scan.next();
			System.out.println(readOrWrite);
			System.out.println(va);
			double value = -1;
			int row = 0;
			if (readOrWrite == 1) {
				value = scan.nextDouble();
				row = 1;
			}
			
			try {
				MMU(row, va, value, outputDir);
				instruction_counter++;
				
				if (instruction_counter % 5 == 0) {
					os.resetTables(tlb, vpt);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		scan.close();
	}
	
	public static void MMU(int readOrWrite, String va, Double value, File outputDir) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(outputDir + "/results.csv", true) );
		StringBuilder sb = new StringBuilder();
		
		sb.append(va);
		sb.append(',');
		sb.append(readOrWrite);
		sb.append(',');
		
		int vpNum = Integer.parseInt(va.substring(0, 2), 16);
		int offset = Integer.parseInt(va.substring(2,4), 16);
		boolean softmiss = true;
		boolean hardmiss = true;
		boolean hit = false;
		boolean dbs = true;
		
		// instruction is to read
		if (readOrWrite == 0) {
			// check tlb first
			for (int i = 0; i < tlb.length; i++) {
				if (vpNum == tlb[i].getVirtualPageNum() && tlb[i].getD() == 0) {
					softmiss = false;
					hardmiss = false;
					hit = true;
					tlb[i].setV(1);
					tlb[i].setR(1);
					
					if (pm[tlb[i].getPageFrameNum()][offset] == -1) {
						dbs = os.handlePageFault(va, pm, vpt, outputDir);
					} else {
						value = pm[tlb[i].getPageFrameNum()][offset];
					}
				} else if (vpNum == tlb[i].getVirtualPageNum() && tlb[i].getD() == 1) {
					softmiss = false;
					hardmiss = false;
					// page fault, entry is dirty
					dbs = os.handlePageFault(va, pm, vpt, outputDir);
				}
			}
			
			if (softmiss) {
				// check page table second
				int pgFrameNum = vpt[vpNum].getPageFrameNum();
				if (pgFrameNum < 16 && vpt[vpNum].getD() == 0) {
					hardmiss = false;
					vpt[vpNum].setR(1);
					vpt[vpNum].setV(1);
					
					if (pm[pgFrameNum][offset] == -1) {
						// page fault
						dbs = os.handlePageFault(va, pm, vpt, outputDir);
					} else {
						value = pm[pgFrameNum][offset];
						// is a softmiss so TLB needs to be updated
						updateTLB(vpNum, pgFrameNum);
					}
	
				} else if (pgFrameNum < 16 && vpt[vpNum].getD() == 1) {
					hardmiss = false;
					// page fault, entry is dirty
					dbs = os.handlePageFault(va, pm, vpt, outputDir);
				}
			}
			
			if (hardmiss) {
				// page fault
				dbs = os.handlePageFault(va, pm, vpt, outputDir);
			}
			
		} else if (readOrWrite == 1) {
		// instruction is to write
			for (int i = 0; i < tlb.length; i++) {
				if (vpNum == tlb[i].getVirtualPageNum()) {
					softmiss = false;
					hardmiss = false;
					hit = true;
					tlb[i].setV(1);
					tlb[i].setR(1);
					tlb[i].setD(1);
					
					if (pm[tlb[i].getPageFrameNum()][offset] == -1) {
						// trap to OS and do page replacement
						dbs = os.handlePageFault(va, pm, vpt, outputDir);
					} else {
						pm[tlb[i].getPageFrameNum()][offset] = value;
					}
				}
			}
			
			if (softmiss) {
				// check page table second
				int pgFrameNum = vpt[vpNum].getPageFrameNum();
				if (pgFrameNum < 16) {
					hardmiss = false;
					vpt[vpNum].setR(1);
					vpt[vpNum].setV(1);
					vpt[vpNum].setD(1);
					
					if (pm[pgFrameNum][offset] == -1) {
						// page fault
						dbs = os.handlePageFault(va, pm, vpt, outputDir);
					} else {
						pm[pgFrameNum][offset] = value;
						// is a softmiss so TLB needs to be updated
						updateTLB(vpNum, pgFrameNum);
					}
	
				}
			}
			
			if (hardmiss) {
				// page fault
				dbs = os.handlePageFault(va, pm, vpt, outputDir);
			}
		} else {
			System.out.println("Expecting a 0 or 1 from input file.");
			System.exit(0);
		}
		
		if (softmiss) {
			sb.append(1);
		} else {
			sb.append(0);
		}
		sb.append(',');

		if (hardmiss) {
			sb.append(1);
		} else {
			sb.append(0);
		}
		sb.append(',');

		if (hit) {
			sb.append(1);
		} else {
			sb.append(0);
		}
		sb.append(',');
		
		if (dbs) {
			sb.append(1);
		} else {
			sb.append(0);
		}
		sb.append(',');
		
		sb.append(value);
		sb.append('\n');
		
		pw.write(sb.toString());
		pw.close();
	}
	
	private static void updateTLB(int vpNum, int pgFrameNum) {
		int index = 0;
		boolean moreSpace = false;
		for (int i = 0; i < tlb.length; i++) {
			if (tlb[i].getPageFrameNum() == -1) {
				// found an empty tlb entry, update it
				index = i;
				moreSpace = true;
			}
		}

		if (moreSpace == false) {
			if (tlb_pointer + 1 == tlb.length) {
				tlb_pointer = 0;
			} else {
				tlb_pointer += 1;
			}
			index = tlb_pointer;
		}
		
		tlb[index].setV(1);
		tlb[index].setD(0);
		tlb[index].setR(1);
		tlb[index].setVirtualPageNum(vpNum);
		tlb[index].setPageFrameNum(pgFrameNum);
		
	}

	private static void initialize() {
		tlb = new TLB[8];
		tlb_pointer = 7;
		for (int i = 0; i < tlb.length; i++) {
			TLB entry = new TLB();
			tlb[i] = entry;
		}
		
		vpt = new VirtualPgTable[256];
		for (int i = 0; i < vpt.length; i++) {
			VirtualPgTable entry = new VirtualPgTable(i);
			vpt[i] = entry;
		}
		
		pm = new double[16][256];
		for (int row = 0; row < 16; row++) {
			for (int col = 0; col < 256; col++) {
				if (col == 0) {
					pm[row][col] = row;
				} else {
				pm[row][col] = -1;			
				}
			}
		}
		
		os = new OS();
	}
	
	public static File copyPageFiles() {
		String dst_location = System.getProperty("user.home") + "/cs431/Output";
		int count = 1;

		File src = new File("bin/Page_Files");
		File dst = new File(dst_location + count);
		
		if (!dst.getParentFile().exists()) {
			dst.getParentFile().mkdirs();
		}
		
		while (dst.exists()) {
			count++;
			dst = new File(dst_location + count);
		}
		
		File pf_dst = new File(dst.toString() + "/Page_Files");
		if (!pf_dst.getParentFile().exists()) {
			pf_dst.getParentFile().mkdirs();
		}
		
		try {
			copyFolder(src, pf_dst);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return dst;
	}
	
	public static void copyFolder(File src, File dest) throws IOException {

		if(src.isDirectory()){

			//if directory not exists, create it
			if(!dest.exists()){
				dest.mkdir();
				System.out.println("Directory copied from " + src + "  to " + dest);
			}

			//list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				//construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				//recursive copy
				copyFolder(srcFile,destFile);
			}

		}else{
			//if file, then copy it
			//Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			//copy the file content in bytes
			while ((length = in.read(buffer)) > 0){
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
			System.out.println("File copied from " + src + " to " + dest);
		}
	}

}
