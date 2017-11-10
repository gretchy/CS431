import java.io.File;

public class OS {

	public OS(double[][] pm) {
		// initialize clock replacement stuff
	}
	
	public boolean handlePageFault(double[][] pm, File outputDir) {
		// need to implement page replacement circular linked list first
		//  ^ probs on creation of OS instance
		
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
