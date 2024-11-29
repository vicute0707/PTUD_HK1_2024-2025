package entity;

public class BienThe {
	private String maBienThe; // variantID
	private String maSP; // productID
	private String size;
	private String mau; // color
	private int soLuong; // quantity
	private double gia; // price
	private String tinhTrang; // status

	public BienThe() {
	}

	public BienThe(String maBienThe, String maSP, String size, String mau, int soLuong, double gia) {
		this.maBienThe = maBienThe;
		this.maSP = maSP;
		this.size = size;
		this.mau = mau;
		this.soLuong = soLuong;
		this.gia = gia;
		this.tinhTrang = "active";
	}

	// Getters and Setters
	public String getMaBienThe() {
		return maBienThe;
	}

	public void setMaBienThe(String maBienThe) {
		this.maBienThe = maBienThe;
	}

	public String getMaSP() {
		return maSP;
	}

	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getMau() {
		return mau;
	}

	public void setMau(String mau) {
		this.mau = mau;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public double getGia() {
		return gia;
	}

	public void setGia(double gia) {
		this.gia = gia;
	}

	public String getTinhTrang() {
		return tinhTrang;
	}

	public void setTinhTrang(String tinhTrang) {
		this.tinhTrang = tinhTrang;
	}

	@Override
	public String toString() {
		return String.format("BienThe[maBT=%s, size=%s, mau=%s, soLuong=%d, gia=%.2f]", maBienThe, size, mau, soLuong,
				gia);
	}
}