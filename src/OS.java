import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class OS {

	private CircularLinkedList clock = new CircularLinkedList();
	private int clock_hand;
	
	public OS() {	
		// initialize clock replacement linked list
		for (int i = 0; i < 16; i++) {
			clock.addNodeAtEnd(i);
		}
		clock_hand = 0;
	}
	
	public boolean handlePageFault(String va, double[][] pm, VirtualPgTable[] vpt, File outputDir) throws IOException {		
		// page table page frame number has all pages (2 hex) to get actual file
		boolean evicted = false;
		boolean dbs = false;
		
		while (evicted == false) {
			if (vpt[(int)pm[clock_hand][0]].getR() == 1) {
				vpt[(int)pm[clock_hand][0]].setR(0);
			} else {
				// r = 0, evict the page
				if (vpt[(int)pm[clock_hand][0]].getD() == 1) {
					// evicted page has been written to, write back to disk
					String hex = Integer.toHexString((int)pm[clock_hand][0]);
					if (hex.length() == 1) {
						hex = "0" + hex;
					}
					File file = new File(outputDir + "/Page_Files/" + hex + ".pg");
					PrintWriter pw = new PrintWriter(new FileWriter(file, true));
					
					for (int i = 1; i < 256; i++) {
						if (pm[clock_hand][i] != -1) {
							pw.print(pm[clock_hand][i]);
						}
					}
					dbs = true;
					pw.close();
				}
				// remove the page from pm and replace with needed page
				int vpNum = Integer.parseInt(va.substring(0, 2), 16);
				pm[clock_hand][0] = vpNum;
				File file = new File(outputDir + "/Page_Files/" + va.substring(0,2) + ".pg");
				Scanner scan = new Scanner(file);
				for (int i = 1; i < 256; i++ ) {
					pm[clock_hand][i] = scan.nextDouble();
				}
				
				vpt[vpNum].setPageFrameNum(clock_hand);
				vpt[vpNum].setV(1);
				vpt[vpNum].setD(0);
				vpt[vpNum].setR(1);
				
				evicted = true;
				scan.close();
			}
			
			clock_hand++;
			if (clock_hand == 16) {
				clock_hand = 0;
			}
		}
		
		// return true if d bit was set on page evicted
		return dbs;
	}

	public void resetTables(TLB[] tlb, VirtualPgTable[] vpt) {
		for (int i = 0; i < tlb.length; i++) {
			tlb[i].setR(0);
		}
		
		for (int i = 0; i < vpt.length; i++) {
			vpt[i].setR(0);
		}
		
	}
}
