import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


public class MapModel {
	private int nbCourses;// cac mon hoc 0,1,...,c-1
	private int nbClassCourses;// cac lop-mon 0,1,2,...,cc-1
	private int nbStudents;// cac sinh vien 0,1,2,...,n-1
	private int nbStudentRegistration;// cac sinh vien dang ky 0,1,2,...,nn - 1
	private int[] cap;// cap[i] la so luong max cua lop-mon i (0,1,...,cc-1)
	private Set<Integer>[] D;// D[i] la tap cac lop-mon co the gan cho sinh vien dang ky i (0,...,nn-1)
	private Set<Integer>[] N;// N[j] la cac sinhvien-dangky cua sinh vien j (0,...,n-1)
	private Set<Integer>[] CC;// CC[j] la tap cac lop-mon tuong ung voi mon j
	private boolean[][] conflicts;// conflicts[i][j] = true: la cap lop-mon conflict (TKB trung nhau)
	private Set<Integer> CONFLICT;
	private int[] reg;// r[i] la course ma sinhvien-dangky i dang ky
	private int[] course;// c[i] la lop cua lop-mon i
	// bien
	private int[] x;// x[i] la lop-mon phan cho sinhvien-dangky i (0,1,...,nn-1), x[i] thuoc D[i]
	private int[] load;// load[j] la so sinhvien-dangky vao lop-mon j (0,...,cc-1)
	HashSet<Integer> Q = new HashSet<Integer>();
	
	public static int hashCode(int i, int j){
		if(i > 9999 || j > 9999){
			System.out.println("OVER FLOW, TRY OTHER APPROACH????????");
			System.exit(-1);
		}
		return i*10000 + j;
	}
	public void loadData(String fn){
		try{
			Scanner in = new Scanner(new File(fn));
			List<Integer> L1 = new ArrayList<Integer>();
			List<Integer> L2 = new ArrayList<Integer>();
			List<Integer> L3 = new ArrayList<Integer>();
			HashSet<Integer> C = new HashSet<Integer>();
			HashSet<Integer> S = new HashSet<Integer>();
			HashMap<Integer, Integer> mCourse2Count = new HashMap<Integer, Integer>();
			
			while(true){
				int sr = in.nextInt(); 
				if(sr == -1) break;
				L1.add(sr);// student-registration
				int s = in.nextInt(); L2.add(s);// student
				int c = in.nextInt(); L3.add(c);// course
				C.add(c);
				S.add(s);
				if(mCourse2Count.get(c) == null) mCourse2Count.put(c, 1);
				else mCourse2Count.put(c,mCourse2Count.get(c)+1);
			}
			nbStudents = S.size();
			nbStudentRegistration = L1.size();
			N = new HashSet[nbStudents];
			for(int i = 0; i < nbStudents; i++)
				N[i] = new HashSet<Integer>();
			nbCourses = C.size();
			reg = new int[L1.size()];
			CC = new Set[nbCourses];
			for(int i = 0; i < nbCourses; i++)
				CC[i] = new HashSet<Integer>();
			
			for(int i = 0; i < L1.size(); i++){
				int sr = L1.get(i);
				int s = L2.get(i);
				N[s].add(sr);
				int c = L3.get(i);
				reg[sr] = c;
			}
			
			L1.clear();
			L2.clear();
			L3.clear();
			while(true){
				int cc = in.nextInt();
				if(cc == -1) break;
				L1.add(cc);
				int c = in.nextInt();
				L2.add(c);
				int a = in.nextInt();
				L3.add(a);
			}
			nbClassCourses = L1.size();
			course = new int[nbClassCourses];
			cap = new int[L1.size()];
			for(int i = 0; i < L1.size(); i++){
				int cc = L1.get(i);
				int a = L3.get(i);
				cap[cc] = a;
				int c = L2.get(i);
				CC[c].add(cc);
				course[cc] = c;
			}
			CONFLICT = new HashSet<Integer>();
			while(true){
				int i = in.nextInt();
				if(i == -1) break;
				int j = in.nextInt();
				int codeij = MapModel.hashCode(i, j);
				int codeji = MapModel.hashCode(j, i);
				CONFLICT.add(codeij);
				CONFLICT.add(codeji);
			}
			in.close();

			for(int c: C){
				System.out.print("course " + c + ": " + mCourse2Count.get(c) + ", lopo-mon = ");
				for(int i: CC[c]) System.out.print(i + ", ");System.out.println();
			}
			for(int i = 0;i < nbStudents; i++){
				System.out.print("sinh vien " + i + ": ");
				for(int sr: N[i]) System.out.print(sr + ",");
				System.out.println();
			}
			for(int code: CONFLICT){
				System.out.println("conflict " + code);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void stateModel(){
		x = new int[nbStudentRegistration];// x[i] la lop-mon phan cho sinhvien-dangky i, domain la CC[r[i]]
		load = new int[nbClassCourses];
	}
	public void greedyConstruct(){
		Random R = new Random();
		for(int sr = 0; sr < nbStudentRegistration; sr++) x[sr] = -1;
		for(int i = 0; i < load.length; i++) load[i] = 0;
		
		ArrayList<Integer> cand = new ArrayList<Integer>();
		for(int s = 0; s < nbStudents; s++){
			System.out.println("Consider student " + s);
			for(int sr: N[s]){
				cand.clear();
				for(int cc: CC[reg[sr]]){
					if(load[cc] < cap[cc]){
						boolean ok = true;
						for(int sri: N[s])if(sri != sr && x[sri] > -1){
							int codei = x[sri];
							int code = MapModel.hashCode(codei,cc);
							if(CONFLICT.contains(code)){
								ok = false; break;
							}
						}
						if(ok) cand.add(cc);
					}
				}
				if(cand.size() <= 0){
					Q.add(sr);
					System.out.println("Cannot assign sr " + sr);
				}else{
					int idx = R.nextInt(cand.size());
					x[sr] = cand.get(idx);
					System.out.println("Assign sr " + sr + " to class-course " + x[sr]);
					load[x[sr]]++;
				}
			}
		}
	}
	public void printSolution(){
		for(int s = 0; s < nbStudents; s++){
			System.out.print("Student " + s + " registers courses : ");
			for(int sr: N[s]) System.out.print(reg[sr] + ",");
			System.out.print(", ASSIGNED courses: ");
			for(int sr: N[s])if(x[sr] > -1) System.out.print(course[x[sr]] + ", ");
			System.out.print(", ASSIGNED classes: ");
			for(int sr: N[s])if(x[sr] > -1) System.out.print(x[sr] + ", ");
			System.out.println();
		}
		if(Q.size() > 0)
		System.out.print("REMAIN: "); for(int sr: Q) System.out.print(sr + " "); System.out.println();
	}
	public static void main(String[] args){
		MapModel m = new MapModel();
		m.loadData("sample.txt");
		m.stateModel();
		m.greedyConstruct();
		m.printSolution();
	}
}
