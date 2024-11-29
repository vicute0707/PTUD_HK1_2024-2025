package entity;

import java.util.ArrayList;
import java.util.List;

public class SanPham {
	private String maSP; // productID
	private String tenSP; // name
	private DanhMuc danhmuc; // category relationship
	private int soLuongTonKho; // stockQuantity
	private double giaNhap; // importPrice
	private double giaBan; // sellPrice
	private String thuongHieu; // brand
	private String hinhAnh; // imagePath
	private String tinhTrang; // status
	private List<BienThe> danhSachBienThe; // List of product variants

	public SanPham() {
		this.danhSachBienThe = new ArrayList<>();
	}

	// Constructor with main fields
	public SanPham(String maSP, String tenSP, DanhMuc danhmuc, double giaNhap, double giaBan, String thuongHieu,
			String hinhAnh) {
		this();
		this.maSP = maSP;
		this.tenSP = tenSP;
		this.danhmuc = danhmuc;
		this.giaNhap = giaNhap;
		this.giaBan = giaBan;
		this.thuongHieu = thuongHieu;
		this.hinhAnh = hinhAnh;
		this.tinhTrang = "active";
	}

	// Getters and Setters
	public String getMaSP() {
		return maSP;
	}

	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}

	public String getTenSP() {
		return tenSP;
	}

	public void setTenSP(String tenSP) {
		this.tenSP = tenSP;
	}

	public DanhMuc getDanhmuc() {
		return danhmuc;
	}

	public void setDanhmuc(DanhMuc danhmuc) {
		this.danhmuc = danhmuc;
	}

	public int getSoLuongTonKho() {
		return soLuongTonKho;
	}

	public void setSoLuongTonKho(int soLuongTonKho) {
		this.soLuongTonKho = soLuongTonKho;
	}

	public double getGiaNhap() {
		return giaNhap;
	}

	public void setGiaNhap(double giaNhap) {
		this.giaNhap = giaNhap;
	}

	public double getGiaBan() {
		return giaBan;
	}

	public void setGiaBan(double giaBan) {
		this.giaBan = giaBan;
	}

	public String getThuongHieu() {
		return thuongHieu;
	}

	public void setThuongHieu(String thuongHieu) {
		this.thuongHieu = thuongHieu;
	}

	public String getHinhAnh() {
		return hinhAnh;
	}

	public void setHinhAnh(String hinhAnh) {
		this.hinhAnh = hinhAnh;
	}

	public String getTinhTrang() {
		return tinhTrang;
	}

	public void setTinhTrang(String tinhTrang) {
		this.tinhTrang = tinhTrang;
	}

	public List<BienThe> getDanhSachBienThe() {
		return danhSachBienThe;
	}

	public void setDanhSachBienThe(List<BienThe> danhSachBienThe) {
		this.danhSachBienThe = danhSachBienThe;
	}

	// Helper method to add variant
	public void themBienThe(BienThe bienThe) {
		danhSachBienThe.add(bienThe);
		// Update total stock quantity
		soLuongTonKho += bienThe.getSoLuong();
	}

	// Helper method to calculate total stock from variants
	public void tinhToanSoLuongTon() {
		soLuongTonKho = danhSachBienThe.stream().mapToInt(BienThe::getSoLuong).sum();
	}

	@Override
	public String toString() {
		return String.format(
				"SanPham[maSP=%s, tenSP=%s, danhmuc=%s, " + "soLuongTon=%d, giaNhap=%.2f, giaBan=%.2f, thuongHieu=%s]",
				maSP, tenSP, danhmuc.getTenDM(), soLuongTonKho, giaNhap, giaBan, thuongHieu);
	}
}