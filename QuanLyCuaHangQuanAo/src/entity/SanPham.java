package entity;

import java.util.ArrayList;
import java.util.List;

public class SanPham {
	private String maSP; // productID
	private String tenSP; // name
	private DanhMuc danhmuc; // category relationship
	private int soLuongTonKho; // stockQuantity
	private String thuongHieu; // brand
	private String hinhAnh; // imagePath
	private String tinhTrang; // status
	private List<ProductVariant> listProductVariants; // List of product variants

	public SanPham() {
		this.listProductVariants = new ArrayList<>();
	}

	// Constructor with main fields
	public SanPham(String maSP, String tenSP, DanhMuc danhmuc, String thuongHieu, String hinhAnh) {
		this();
		this.maSP = maSP;
		this.tenSP = tenSP;
		this.danhmuc = danhmuc;

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

	

	public List<ProductVariant> getListProductVariants() {
		return listProductVariants;
	}

	public void setListProductVariants(List<ProductVariant> listProductVariants) {
		this.listProductVariants = listProductVariants;
	}

	@Override
	public String toString() {
		return String.format(
				"SanPham[maSP=%s, tenSP=%s, danhmuc=%s, " + "soLuongTon=%d, giaNhap=%.2f, giaBan=%.2f, thuongHieu=%s]",
				maSP, tenSP, danhmuc.getTenDM(), soLuongTonKho, thuongHieu);
	}
}