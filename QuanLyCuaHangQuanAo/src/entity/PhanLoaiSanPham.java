package entity;

public class PhanLoaiSanPham {
    private String maPL; 
    private SanPham sanpham;  
    private String size;  
    private String color; 
    private int soluong; 
    private String chatlieu; 
    private static String masp;

    // Constructor với đầy đủ tham số
    public PhanLoaiSanPham(String maPL, SanPham sanpham, String size, String color, int soluong, String chatlieu) {
        this.maPL = maPL;
        this.sanpham = sanpham;
        this.size = size;
        this.color = color;
        this.soluong = soluong;
        this.chatlieu = chatlieu;
    }
    

    // Constructor không có sanpham và maPL
    public PhanLoaiSanPham(String maPL,String size, String color, String chatlieu) {
    	this.maPL = maPL;
        this.size = size;
        this.color = color;
        this.chatlieu = chatlieu;
    }
    
    public PhanLoaiSanPham() {
		super();
		// TODO Auto-generated constructor stub
	}


	// Getter và Setter
    public String getSize() {
        return size;  // trả về size dạng String
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }
    

    public static String getMasp() {
		return masp;
	}

	public static void setMasp(String masp) {
		PhanLoaiSanPham.masp = masp;
	}

	public void setColor(String color) {
        this.color = color;
    }

    public String getChatlieu() {
        return chatlieu;
    }

    public void setChatlieu(String chatlieu) {
        this.chatlieu = chatlieu;
    }

    public String getMaPL() {
        return maPL;
    }

    public void setMaPL(String maPL) {
        this.maPL = maPL;
    }

    public SanPham getSanpham() {
        return sanpham;
    }

    public void setSanpham(SanPham sanpham) {
        this.sanpham = sanpham;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }
}
