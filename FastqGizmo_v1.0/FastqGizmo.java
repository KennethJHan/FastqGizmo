import java.io.*;

class FastqGizmo {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static void usage() {
		System.out.println(ANSI_CYAN+"FastqGizmo v1.0 (2018) - https://github.com/KennethJHan/FastqGizmo"+ANSI_RESET);
		System.out.println(ANSI_RED+"USAGE: "+ANSI_GREEN+"FastqGizmo <Tool Name> <read_1.fastq.gz> <read_2.fastq.gz>\n"+ANSI_RESET);

		System.out.println(ANSI_RED+":: FastqGizmo ::"+ANSI_RESET);
		System.out.println(ANSI_YELLOW+"--------------------------------------------------------------------------------------"+ANSI_RESET);
		System.out.println(ANSI_GREEN+"StatReport:"+ANSI_RESET);
		System.out.println(ANSI_CYAN+"    Make HTML Stat Report (BasicStat, NBaseReadStat)"+ANSI_RESET);
		System.out.println(ANSI_YELLOW+"--------------------------------------------------------------------------------------"+ANSI_RESET);
		System.out.println(ANSI_GREEN+"BasicStat:"+ANSI_RESET);
		System.out.println(ANSI_CYAN+"    Shows basic stat (Base count, Base quality)"+ANSI_RESET);
		System.out.println(ANSI_YELLOW+"--------------------------------------------------------------------------------------"+ANSI_RESET);
		System.out.println(ANSI_GREEN+"NBaseReadStat:"+ANSI_RESET);
		System.out.println(ANSI_CYAN+"    Shows N-base read stat (count number of read containing N-base)"+ANSI_RESET);
	}

	public static void main(String[] args) {
		if(args.length != 3) {
			usage();
			System.exit(0);
		}
		String tool = args[0];
		String fq1 = args[1];
		String fq2 = args[2];
		boolean Report = false;

		Fastq f = new Fastq();
		f.setFastq(fq1, fq2);
		switch(tool) {

			case "StatReport":
				Report = true;
				System.out.println(ANSI_GREEN+tool+ANSI_RESET);
				try { f.getBasicStat(Report); f.getNBaseReadStat(Report); } catch(IOException ex) { System.out.println(ex); }
				break;

			case "BasicStat":
				System.out.println(ANSI_GREEN+tool+ANSI_RESET);
				try { f.getBasicStat(Report);	} catch(IOException ex) { System.out.println(ex); }
				break;

			case "NBaseReadStat":
				System.out.println(ANSI_GREEN+tool+ANSI_RESET);
				try { f.getNBaseReadStat(Report);	} catch(IOException ex) { System.out.println(ex); }
				break;

			default:
				usage();
				break;
		}
	}
}
