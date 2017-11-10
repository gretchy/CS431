
public class TLB {
	private int virtualPageNum, pageFrameNum;
	private int v, r, d;
	
	public TLB() {
		this.v = 0;
		this.r = 0;
		this.d = 0;
		this.virtualPageNum = -1;
		this.pageFrameNum = -1;
	}
	public int getVirtualPageNum() {
		return virtualPageNum;
	}
	public void setVirtualPageNum(int virtualPageNum) {
		this.virtualPageNum = virtualPageNum;
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
