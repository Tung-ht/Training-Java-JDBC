package quanlysv;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class MAIN {
	// attributes:
	public static List<Student> listStudent = new ArrayList<Student>();
	public static Scanner sc = new Scanner(System.in);
	//
	// !_ note _! this is just init an object
	// it will not create a connection
	private static MysqlConnect mysqlConnect = new MysqlConnect();
	private static Statement stmt;
	private static PreparedStatement prpStmt;

	// methods:

	// add all from DB to the arraylist
	public static void addAll() {
		try {
			String sql = "SELECT * FROM `student`";
			stmt = mysqlConnect.connect().createStatement();
			ResultSet res = stmt.executeQuery(sql);
			while (res.next()) {
				Student sv = new Student();
				sv.setId(res.getString("id"));
				sv.setFullName(res.getString("fullName"));
				sv.setClassId(res.getString("classId"));
				sv.setAge(res.getInt("age"));
				sv.setAddress(res.getString("address"));
				if (res.getString("gender").equalsIgnoreCase("nam")) {
					sv.setGender(true);
				} else {
					sv.setGender(false);
				}
				sv.setMaths(res.getFloat("maths"));
				sv.setPhysics(res.getFloat("physics"));
				sv.setChemistry(res.getFloat("chemistry"));
				sv.setAverageScore(res.getFloat("averageScore"));
				listStudent.add(sv);
			}
			// Clean-up environment
			res.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // nothing we can do
			mysqlConnect.disconnect();
		}
	}

	// chuc nang 1
	public static void nhapDS() {
		try {
			Student sv = new Student();
			System.out.printf("->Nhap sinh vien thu %d:%n", listStudent.size() + 1);
			sv.nhap();
			sv.setAverageScore((sv.getMaths() + sv.getPhysics() + sv.getChemistry()) / 3);
			listStudent.add(sv);

			String sql = "INSERT INTO student VALUES(?,?,?,?,?,?,?,?,?,?)";
			prpStmt = mysqlConnect.connect().prepareStatement(sql);
			prpStmt.setString(1, sv.getId());
			prpStmt.setString(2, sv.getFullName());
			prpStmt.setString(3, sv.getClassId());
			prpStmt.setInt(4, sv.getAge());
			prpStmt.setString(5, sv.getAddress());
			if (sv.isGender()) {
				prpStmt.setString(6, "Nam");
			} else {
				prpStmt.setString(6, "Nu");
			}
			prpStmt.setDouble(7, sv.getMaths());
			prpStmt.setDouble(8, sv.getPhysics());
			prpStmt.setDouble(9, sv.getChemistry());
			prpStmt.setDouble(10, sv.getAverageScore());
			prpStmt.execute();
			prpStmt.close();
			String c = "";
			do {
				System.out.println("--->Ban co muon nhap tiep khong: Y/N?");
				c = sc.nextLine();
				if (c.equalsIgnoreCase("y")) {
					nhapDS();
				} else if (c.equalsIgnoreCase("n")) {
					System.out.println("Tro ve menu!");
					break;
				} else {
					System.out.println("!!!Da xay ra loi. Nhap lai!");
				}
			} while (!c.equalsIgnoreCase("y") && !c.equalsIgnoreCase("n")); // nguoi dung nhap # "y" va "n"
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (prpStmt != null)
					prpStmt.close();
			} catch (SQLException se2) {
			} // nothing we can do
			mysqlConnect.disconnect();
		}
	}
	//

	// Chuc nang 2: xuat danh sach theo ABC:
	public static void xuatDS() {
		if (listStudent.size() == 0) {
			System.out.println("Ban chua nhap vao danh sach!");
		} else {
			Collections.sort(listStudent, new nameComparator());
			System.out.printf("%-5s%-20s%-20s%-20s%-20s%-20s%-20s%-5s%-5s%-5s%-30s%n", "STT", "Ten", "Tuoi",
					"Gioi tinh", "Dia chi", "MSSV", "Ma lop", "Toan", "Ly", "Hoa", "Diem trung binh");
			for (int i = 0; i < listStudent.size(); i++) {
				System.out.printf("%-5d", (i + 1));
				listStudent.get(i).xuat();
			}
		}
		String s = "";
		do {
			System.out.println("--->Tro ve menu: Y?");
			s = sc.nextLine();
			if (!s.equalsIgnoreCase("y")) {
				System.out.println("Da xay ra loi! Nhap lai!");
			}
		} while (!s.equalsIgnoreCase("y"));
	}

	// Chuc nang 3:
	public static void hienThiTheoHocLuc() {
		if (listStudent.size() == 0) {
			System.out.println("Ban chua nhap vao danh sach!");
		} else {
			System.out.println("--->Chon xep loai hoc luc:");
			System.out.println("1. Gioi: >= 8.0");
			System.out.println("2. Kha: >= 6.5 && < 8");
			System.out.println("3. Trung binh: >= 5 && < 6.5");
			System.out.println("4. Yeu: < 5");
			System.out.println("5. Tro ve menu.");
			System.out.println("->Nhap lua chon cua ban:");
			int key = sc.nextInt();
			sc.nextLine();
			switch (key) {
			case 1:
				System.out.println("Danh sach sinh vien co hoc luc gioi:");
				int count = 0;
				for (int i = 0; i < listStudent.size(); i++) {
					if (listStudent.get(i).getAverageScore() >= 8.0 && count == 0) {
						System.out.printf("%-5s%-20s%-20s%-20s%-20s%-20s%-20s%-5s%-5s%-5s%-30s%n", "STT", "Ten", "Tuoi",
								"Gioi tinh", "Dia chi", "MSSV", "Ma lop", "Toan", "Ly", "Hoa", "Diem trung binh");
					}
					if (listStudent.get(i).getAverageScore() >= 8.0) {
						System.out.printf("%-5d", (count + 1));
						listStudent.get(i).xuat();
						count++;
					}
				}
				if (count == 0) {
					System.out.println("Khong co sinh vien nao!");
				}
				break;
			case 2:
				System.out.println("Danh sach sinh vien co hoc luc kha:");
				count = 0;
				for (int i = 0; i < listStudent.size(); i++) {
					if (listStudent.get(i).getAverageScore() >= 6.5 && listStudent.get(i).getAverageScore() < 8
							&& count == 0) {
						System.out.printf("%-5s%-20s%-20s%-20s%-20s%-20s%-20s%-5s%-5s%-5s%-30s%n", "STT", "Ten", "Tuoi",
								"Gioi tinh", "Dia chi", "MSSV", "Ma lop", "Toan", "Ly", "Hoa", "Diem trung binh");
					}
					if (listStudent.get(i).getAverageScore() >= 6.5 && listStudent.get(i).getAverageScore() < 8) {
						System.out.printf("%-5d", (count + 1));
						listStudent.get(i).xuat();
						count++;
					}
				}
				if (count == 0) {
					System.out.println("Khong co sinh vien nao!");
				}
				break;
			case 3:
				System.out.println("Danh sach sinh vien co hoc luc trung binh:");
				count = 0;
				for (int i = 0; i < listStudent.size(); i++) {
					if (listStudent.get(i).getAverageScore() >= 5 && listStudent.get(i).getAverageScore() < 6.5
							&& count == 0) {
						System.out.printf("%-5s%-20s%-20s%-20s%-20s%-20s%-20s%-5s%-5s%-5s%-30s%n", "STT", "Ten", "Tuoi",
								"Gioi tinh", "Dia chi", "MSSV", "Ma lop", "Toan", "Ly", "Hoa", "Diem trung binh");
					}
					if (listStudent.get(i).getAverageScore() >= 5 && listStudent.get(i).getAverageScore() < 6.5) {
						System.out.printf("%-5d", (count + 1));
						listStudent.get(i).xuat();
						count++;
					}
				}
				if (count == 0) {
					System.out.println("Khong co sinh vien nao!");
				}
				break;
			case 4:
				System.out.println("Danh sach sinh vien co hoc luc yeu:");
				count = 0;
				for (int i = 0; i < listStudent.size(); i++) {
					if (listStudent.get(i).getAverageScore() < 5 && count == 0) {
						System.out.printf("%-5s%-20s%-20s%-20s%-20s%-20s%-20s%-5s%-5s%-5s%-30s%n", "STT", "Ten", "Tuoi",
								"Gioi tinh", "Dia chi", "MSSV", "Ma lop", "Toan", "Ly", "Hoa", "Diem trung binh");
					}
					if (listStudent.get(i).getAverageScore() < 5) {
						System.out.printf("%-5d", (count + 1));
						listStudent.get(i).xuat();
						count++;
					}
				}
				if (count == 0) {
					System.out.println("Khong co sinh vien nao!");
				}
				break;
			case 5:
				return;// thoat ham, ve menu
			default:
				System.out.println("Khong co chuc nang nay!");
			}

		}
		String s = "";
		do {
			System.out.println("--->Tro ve menu: Y/N?");
			s = sc.nextLine();
			if (!s.equalsIgnoreCase("y") && !s.equalsIgnoreCase("n")) {
				System.out.println("Da xay ra loi! Nhap lai!");
			} else if (s.equalsIgnoreCase("n")) {
				hienThiTheoHocLuc();
			} else { // == "y"
				return;
			}
		} while (!s.equalsIgnoreCase("y") && !s.equalsIgnoreCase("n"));
	}
	//

	// Chuc nang 4:
	public static void timTheoTen() {
		if (listStudent.size() == 0) {
			System.out.println("Ban chua nhap vao danh sach!");
		} else {
			System.out.println("--->Nhap ten sinh vien can tim kiem:");
			String s = sc.nextLine();
			int count = 0;
			for (int i = 0; i < listStudent.size(); i++) {
				if (listStudent.get(i).getFullName().contains(s) && count == 0) {
					System.out.printf("%-5s%-20s%-20s%-20s%-20s%-20s%-20s%-5s%-5s%-5s%-30s%n", "STT", "Ten", "Tuoi",
							"Gioi tinh", "Dia chi", "MSSV", "Ma lop", "Toan", "Ly", "Hoa", "Diem trung binh");
				}
				if (listStudent.get(i).getFullName().toLowerCase().contains(s.toLowerCase())) {
					System.out.printf("%-5d", (count + 1));
					listStudent.get(i).xuat();
					count++;
				}
			}
			if (count == 0) {
				System.out.println("Khong co sinh vien nao!");
			}
		}
		String s = "";
		do {
			System.out.println("--->Tro ve menu: Y/N?");
			s = sc.nextLine();
			if (!s.equalsIgnoreCase("y") && !s.equalsIgnoreCase("n")) {
				System.out.println("Da xay ra loi! Nhap lai!");
			} else if (s.equalsIgnoreCase("n")) {
				timTheoTen();
			} else { // == "y"
				return;
			}
		} while (!s.equalsIgnoreCase("y") && !s.equalsIgnoreCase("n"));

	}
	//

	// Chuc nang 5:
	public static void hienThiNamNu() {
		if (listStudent.size() == 0) {
			System.out.println("Ban chua nhap vao danh sach!");
		} else {
			System.out.println("--->Chon nam/nu:");
			System.out.println("1. Nam.");
			System.out.println("2. Nu.");
			System.out.println("Nhap lua chon cua ban:");
			int key = sc.nextInt();
			sc.nextLine();
			int count = 0;
			switch (key) {
			case 1:
				count = 0;
				System.out.println("Danh sach sinh vien nam:");
				for (int i = 0; i < listStudent.size(); i++) {
					if (count == 0 && listStudent.get(i).isGender()) { // nam = true;
						System.out.printf("%-5s%-20s%-20s%-20s%-20s%-20s%-20s%-5s%-5s%-5s%-30s%n", "STT", "Ten", "Tuoi",
								"Gioi tinh", "Dia chi", "MSSV", "Ma lop", "Toan", "Ly", "Hoa", "Diem trung binh");
					}
					if (listStudent.get(i).isGender()) {
						System.out.printf("%-5d", (count + 1));
						listStudent.get(i).xuat();
						count++;
					}
				}
				if (count == 0) {
					System.out.println("Khong co sinh vien nao!");
				}
				break;
			case 2:
				count = 0;
				System.out.println("Danh sach sinh vien nu:");
				for (int i = 0; i < listStudent.size(); i++) {
					if (count == 0 && !listStudent.get(i).isGender()) { // nu = false;
						System.out.printf("%-5s%-20s%-20s%-20s%-20s%-20s%-20s%-5s%-5s%-5s%-30s%n", "STT", "Ten", "Tuoi",
								"Gioi tinh", "Dia chi", "MSSV", "Ma lop", "Toan", "Ly", "Hoa", "Diem trung binh");
					}
					if (!listStudent.get(i).isGender()) {
						System.out.printf("%-5d", (count + 1));
						listStudent.get(i).xuat();
						count++;
					}
				}
				if (count == 0) {
					System.out.println("Khong co sinh vien nao!");
				}
				break;
			default:
				System.out.println("Khong co chuc nang nay!");
				break;
			}
		}
		String s = "";
		do {
			System.out.println("--->Tro ve menu: Y/N?");
			s = sc.nextLine();
			if (!s.equalsIgnoreCase("y") && !s.equalsIgnoreCase("n")) {
				System.out.println("Da xay ra loi! Nhap lai!");
			} else if (s.equalsIgnoreCase("n")) {
				hienThiNamNu();
			} else { // == "y"
				return;
			}
		} while (!s.equalsIgnoreCase("y") && !s.equalsIgnoreCase("n"));

	}
	//

	// Chuc nang 6:
	public static void suaThongTin() {
		if (listStudent.size() == 0) {
			System.out.println("Ban chua nhap vao danh sach!");
		} else {
			System.out.println("Nhap ma so sinh vien can sua:");
			String s = sc.nextLine();
			int count = 0;
			for (int i = 0; i < listStudent.size(); i++) {
				if (s.equalsIgnoreCase(listStudent.get(i).getId())) { // tim thay
					System.out.println("Da tim thay! Nhap thong tin moi:");
					Student sv = listStudent.get(i);
					count++;
					sv.nhap();
					sv.setAverageScore((sv.getMaths()+sv.getPhysics()+sv.getChemistry())/3);
					try {
						String sql = "UPDATE student SET id = ?, fullName = ?, classId = ?, age = ?, address = ?, gender = ?, maths = ?, physics = ?, chemistry = ?, averageScore = ? WHERE id = ?;";
						prpStmt = mysqlConnect.connect().prepareStatement(sql);
						prpStmt.setString(1, sv.getId());
						prpStmt.setString(2, sv.getFullName());
						prpStmt.setString(3, sv.getClassId());
						prpStmt.setInt(4, sv.getAge());
						prpStmt.setString(5, sv.getAddress());
						if (sv.isGender()) {
							prpStmt.setString(6, "Nam");
						} else {
							prpStmt.setString(6, "Nu");
						}
						prpStmt.setDouble(7, sv.getMaths());
						prpStmt.setDouble(8, sv.getPhysics());
						prpStmt.setDouble(9, sv.getChemistry());
						prpStmt.setDouble(10, sv.getAverageScore());
						prpStmt.setString(11, s);
						prpStmt.execute();
						System.out.println("Cap nhat thanh cong!");
						// clean-up the environment
						prpStmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						try {
							if (prpStmt != null)
								prpStmt.close();
						} catch (SQLException se2) {
						} // nothing we can do
						mysqlConnect.disconnect();
					}
				}
			}
			if (count == 0) {
				System.out.println("Khong tim thay sinh vien!");
			}
		}
		String s = "";
		do {
			System.out.println("--->Tro ve menu: Y/N?");
			s = sc.nextLine();
			if (!s.equalsIgnoreCase("y") && !s.equalsIgnoreCase("n")) {
				System.out.println("Da xay ra loi! Nhap lai!");
			} else if (s.equalsIgnoreCase("n")) {
				suaThongTin();
			} else { // == "y"
				return;
			}
		} while (!s.equalsIgnoreCase("y") && !s.equalsIgnoreCase("n"));
	}
	//

	// Chuc nang 7:
	public static void xoaThongTin() {
		if (listStudent.size() == 0) {
			System.out.println("Ban chua nhap vao danh sach!");
		} else {
			System.out.println("Nhap ma so sinh vien can xoa:");
			String s = sc.nextLine();
			int count = 0;
			for (int i = 0; i < listStudent.size(); i++) {
				if (s.equalsIgnoreCase(listStudent.get(i).getId())) { // tim thay
					Student sv = listStudent.get(i);
					listStudent.remove(i);
					count++;
					try {
						String sql = "DELETE FROM student WHERE id = ?";
						prpStmt = mysqlConnect.connect().prepareStatement(sql);
						prpStmt.setString(1, s);
						prpStmt.execute();
						System.out.println("Xoa thanh cong!");
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			if (count == 0) {
				System.out.println("Khong tim thay sinh vien!");
			}
		}
		String s = "";
		do {
			System.out.println("--->Tro ve menu: Y/N?");
			s = sc.nextLine();
			if (!s.equalsIgnoreCase("y") && !s.equalsIgnoreCase("n")) {
				System.out.println("Da xay ra loi! Nhap lai!");
			} else if (s.equalsIgnoreCase("n")) {
				xoaThongTin();
			} else { // == "y"
				return;
			}
		} while (!s.equalsIgnoreCase("y") && !s.equalsIgnoreCase("n"));

	}
	//

	// Chuc nang 8:
	public static void xoaDb() {

	}
	//

	// menu chuong trinh
	public static void menu() {
		addAll(); // nap du lieu tu DB vao arrlist
		while (true) {
			System.out.println("**********************************************");
			System.out.println("**    	CHUONG TRINH QUAN LY SINH VIEN    	");
			System.out.println();
			System.out.println("**************MENU CHUONG TRINH***************");
			System.out.println("**	1. Nhap thong tin sinh vien.			");
			System.out.println("**	2. Hien thi danh sach sinh vien.		");
			System.out.println("**	3. Hien thi sinh vien theo hoc luc.		");
			System.out.println("** 	4. Tim sinh vien theo ten.				");
			System.out.println("**	5. Hien thi thong tin nam/nu.			");
			System.out.println("**	6. Sua thong tin sinh vien.				");
			System.out.println("** 	7. Xoa sinh vien.						"); // remove from list and disable from
																				// database;
			System.out.println("** 	0. Thoat chuong trinh.					");
			System.out.println("**********************************************");
			System.out.println("** 	Nhap lua chon cua ban:					");

			int key = sc.nextInt();
			sc.nextLine();

			switch (key) {
			case 1:
				System.out.println("--->Ban da chon nhap thong tin sinh vien:");
				nhapDS();
				break;
			case 2:
				System.out.println("--->Ban da chon hien thi danh sach sinh vien:");
				xuatDS();
				break;
			case 3:
				System.out.println("--->Ban da chon hien thi sinh vien theo hoc luc:");
				hienThiTheoHocLuc();
				break;
			case 4:
				System.out.println("--->Ban da chon tim kiem sinh vien theo ten:");
				timTheoTen();
				break;
			case 5:
				System.out.println("--->Ban da chon hien thi thong tin nam/nu:");
				hienThiNamNu();
				break;
			case 6:
				System.out.println("--->Ban da chon sua thong tin sinh vien:");
				suaThongTin();
				break;
			case 7:
				System.out.println("--->Ban da chon xoa sinh vien:");
				xoaThongTin();;
				break;
			case 0:
				System.out.println("--->Ban da chon thoat chuong trinh:");
				String c = "";
				do {
					System.out.println("Ban co muon thoat chuong trinh: Y/N?");
					c = sc.nextLine();
					if (c.equalsIgnoreCase("y")) {
						return; // thoat khoi ham main
					} else if (c.equalsIgnoreCase("n")) {
						break; // break vong do-while
					} else {
						System.out.println("!!!Da xay ra loi. Nhap lai!");
					}
				} while (!c.equalsIgnoreCase("y") && !c.equalsIgnoreCase("n")); // c!= y va c!=n
				break; // break case 0
			default:
				System.out.println("Khong co chuc nang nay!");
				break;
			}
		}
	}
	//

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		menu();
	}
}
