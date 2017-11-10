import java.io.File;

public class OS {

	private CircularLinkedList clock = new CircularLinkedList();
	private int clock_hand;
	
	public OS(double[][] pm) {
		// initialize clock replacement stuff
		for (int row = 0; row < 16; row++) {
			clock.addNodeAtEnd(pm[row][0]);
		}
		
		clock_hand = 0;
	}
	
	public boolean handlePageFault(double[][] pm, VirtualPgTable[] vpt, File outputDir) {		
		// page table page frame number has all pages (2 hex) to get actual file
		// return true if d bit was set on page evicted
		return true;
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
