
public class VirtualPgTable {
	private int pageFrameNum;
	private int v, r, d;
	
	public VirtualPgTable(int i) {
		this.pageFrameNum = i;
		this.v = 0;
		this.r = 0;
		this.d = 0;
	}
	
	public int getPageFrameNum() {
		return pageFrameNum;
	}
	public void setPageFrameNum(int pageFrameNum) {
		this.pageFrameNum = pageFrameNum;
	}
	public int getV() {
		return v;
	}
	public void setV(int v) {
		this.v = v;
	}
	public int getR() {
		return r;
	}
	public void setR(int r) {
		this.r = r;
	}
	public int getD() {
		return d;
	}
	public void setD(int d) {
		this.d = d;
	}

}
