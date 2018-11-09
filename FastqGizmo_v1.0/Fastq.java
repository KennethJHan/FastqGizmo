import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

class Fastq {
	private String r1;
	private String r2;

	private long lineNum;

	private long r1_A;
	private long r1_C;
	private long r1_G;
	private long r1_T;
	private long r1_N;
	private long r2_A;
	private long r2_C;
	private long r2_G;
	private long r2_T;
	private long r2_N;

	private long qualSum1;
	private long qualSum2;

	public void setFastq(String read1, String read2) {
		r1 = read1;
		r2 = read2;
	}

	public List<String> getFastq() {
		String[] ret = { r1, r2 };
		List<String> fastqList = new ArrayList<String>(Arrays.asList(ret));
		return fastqList;
	}

	public List<Integer> countBase(String s) {
		int A, C, G, T, N;
		A = C = G = T = N = 0;
		for(char ch: s.toCharArray()) {
			if(ch == 'A') {
				A++;
			} else if(ch == 'C') {
				C++;
			} else if(ch == 'G') {
				G++;
			} else if(ch == 'T') {
				T++;
			} else if(ch == 'N') {
				N++;
			}
		}
		Integer[] ret = { A, C, G, T, N };
		List<Integer> baseCountList = new ArrayList<Integer>(Arrays.asList(ret));
		return baseCountList;
	}

	public long countNBase(String s) {
		long N = 0;
		for(char ch: s.toCharArray()) {
			if(ch == 'N') {
				N++;
			}
		}
		return N;
	}

	public int calcBaseQual(String s) {
		int qualSum = 0;
		for(char ch: s.toCharArray()) {
			int qual = (int) ch - 33; // phred33
			qualSum += qual;
		}
		return qualSum;
	}

	public String printHashMap(Map<Long, Long> map, boolean Report, int readNum) {
		String ret = "";
		if(!Report)
			System.out.println("Number of N\tNumber of Reads");
		for(Map.Entry ent:map.entrySet()) {
			if(!Report)
				System.out.println(ent.getKey()+"\t"+ent.getValue());
			ret += "<tr><td>Read"+readNum+"</td><td>"+ent.getKey()+"</td>"+"<td>"+ent.getValue()+"</td></tr>";
		}
		if(!Report)
			System.out.println("");
		return ret;
	}

	//public ArrayList<String> getBasicStat() {
	public void getBasicStat(boolean Report) throws IOException {
		String s1;
		String s2;
		List<Integer> r1Count = new ArrayList<Integer>();
		List<Integer> r2Count = new ArrayList<Integer>();

		ArrayList<String> basicStatList = new ArrayList<String>();
		BufferedReader br1 = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(r1))));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(r2))));

		lineNum = 0;
		while(true) {
			s1 = br1.readLine();
			s2 = br2.readLine();
			if(s1 == null || s2 == null)
				break;
			s1 = s1.trim();
			s2 = s2.trim();
			if(lineNum % 4 == 0) {
				// header
			} else if(lineNum % 4 == 1) {
				// seq
				r1Count = countBase(s1);
				r2Count = countBase(s2);
				r1_A += r1Count.get(0);
				r1_C += r1Count.get(1);
				r1_G += r1Count.get(2);
				r1_T += r1Count.get(3);
				r1_N += r1Count.get(4);
				r2_A += r2Count.get(0);
				r2_C += r2Count.get(1);
				r2_G += r2Count.get(2);
				r2_T += r2Count.get(3);
				r2_N += r2Count.get(4);

			} else if(lineNum % 4 == 2) {
				// delimiter
			} else {
				// qual
				qualSum1 += calcBaseQual(s1);
				qualSum2 += calcBaseQual(s2);
			}
			lineNum++;
		}
		long numOfBase1 = r1_A + r1_C + r1_G + r1_T + r1_N;
		long numOfBase2 = r2_A + r2_C + r2_G + r2_T + r2_N;
		double qual1 = (double)qualSum1 / numOfBase1;
		double qual2 = (double)qualSum2 / numOfBase2;
		if(Report) {
			// Write HTML DOCUMENT
			String doc = "<!DOCTYPE html>\n"
						+"<html>\n"
						+"  <head>\n"
						+"    <title>FastqGizmo Report</title>\n"
						+"    <meta charset='utf-8'>\n"
						+"    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">\n"
						+"    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\n"
						+"    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>\n"
						+"    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>"
						+"    <script>"
						+"      google.charts.load('current', {'packages':['bar']});"
						+"      google.charts.setOnLoadCallback(drawChart);"
						+"      function drawChart() {"
						+"        var data = google.visualization.arrayToDataTable(["
						+"          ['Read','A','C','G','T','N'],"
						+"          ['Read1',"+r1_A+","+r1_C+","+r1_G+","+r1_T+","+r1_N+"],"
						+"          ['Read2',"+r2_A+","+r2_C+","+r2_G+","+r2_T+","+r2_N+"],"
						+"        ]);"
						+"        var options = {"
						+"          chart: {"
						+"            title: 'Base by read',"
						+"          }"
						+"        };"
						+"        var chart = new google.charts.Bar(document.getElementById('base_chart'));"
						+"        chart.draw(data, google.charts.Bar.convertOptions(options));"
						+"      }"
						+"    </script>\n"
						+"   <style>\n"
						+"     body {position: relative;}\n"
						+"     ul.nav-pills {top:20px; position: fixed;}\n"
						+"     #title {}\n"
						+"     #BasicStat {}\n"
						+"     #NBaseStat {}\n"
						+"     @media screen and (max-width: 810px) { #title, #BasicStat, #NBaseStat { margin-left:150px; } }\n"
						+"     .footer { color: black; text-align: center; }\n"
						+"   </style>\n"
						+"  </head>\n"
						+"  <body data-spy='scroll' data-target='#contentScrollspy' data-offset='20'>\n"
						+"    <div class='container'>\n"
						+"      <div class='row'>\n"
						+"        <nav class='col-sm-3' id='contentScrollspy'>\n"
						+"          <ul class='nav nav-pills nav-stacked'>\n"
						+"            <li class='active'><a href='#title'>Title</a></li>\n"
						+"            <li><a href='#BasicStat'>Basic Stat</a></li>\n"
						+"            <li><a href='#NBaseStat'>NBase Stat</a></li>\n"
						+"          </ul>\n"
						+"        </nav>\n"
						+"    <a href=\"https://github.com/KennethJHan/FastqGizmo\">"
						+"      <img style=\"position: absolute; top: 0; right: 0; border: 0;\" src=\"https://s3.amazonaws.com/github/ribbons/forkme_right_red_aa0000.png\" alt=\"Fork me on GitHub\">"
						+"    </a>\n"
						+"        <div class='col-sm-9'>\n"
						+"          <div id='title' class='jumbotron'>\n"
						+"            <h1>FastqGizmo</h1>\n"
						+"            <p>Report</p>\n"
						+"          </div>\n"
						+"          <div id='BasicStat'>\n"
						+"            <h3>Basic Stat</h3>\n"
						+"            <table class='table table-striped'>\n"
						+"              <tr>\n"
						+"                <th>Read</th><th>A</th><th>C</th><th>G</th><th>T</th><th>N</th><th>Average Base Quality</th>\n"
						+"              </tr>\n"
						+"              <tr>\n"
						+"                <td>Read1</td><td>"+r1_A+"</td><td>"+r1_C+"</td><td>"+r1_G+"</td><td>"+r1_T+"</td><td>"+r1_N+"</td><td>"+qual1+"</td>\n"
						+"              </tr>\n"
						+"              <tr>\n"
						+"                <td>Read2</td><td>"+r2_A+"</td><td>"+r2_C+"</td><td>"+r2_G+"</td><td>"+r2_T+"</td><td>"+r2_N+"</td><td>"+qual2+"</td>\n"
						+"              </tr>\n"
						+"            </table>\n"
						+"            <br>\n"
						+"            <div id='base_chart' style='width:800px; height: 500px;'></div>\n"
                        +"          </div>\n";


			FileWriter fw = new FileWriter("result.html");
			fw.write(doc);
			fw.close();
		} else {
			System.out.println("#Read1");
			System.out.println("Quality: "+qual1);
			System.out.println("A: "+r1_A+"\nC: "+r1_C+"\nG: "+r1_G+"\nT: "+r1_T+"\nN: "+r1_N+"\n");
			System.out.println("#Read2");
			System.out.println("Quality: "+qual2);
			System.out.println("A: "+r2_A+"\nC: "+r2_C+"\nG: "+r2_G+"\nT: "+r2_T+"\nN: "+r2_N+"\n");
		}
	}

	public void getNBaseReadStat(boolean Report) throws IOException {
		Map<Long, Long> nBaseReadMap1 = new HashMap<Long, Long>();
		Map<Long, Long> nBaseReadMap2 = new HashMap<Long, Long>();

		String s1;
		String s2;

		BufferedReader br1 = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(r1))));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(r2))));

		lineNum = 0;
		while(true) {
			long nCount1, nCount2;
			nCount1 = nCount2 = 0;
			s1 = br1.readLine();
			s2 = br2.readLine();
			if(s1 == null || s2 == null)
				break;
			if(lineNum % 4 == 1) {
				nCount1 = countNBase(s1);
				nCount2 = countNBase(s2);
				Long readCount1 = nBaseReadMap1.get(nCount1);
				Long readCount2 = nBaseReadMap2.get(nCount2);
				if(readCount1 != null)
					nBaseReadMap1.put(nCount1, readCount1+1);
				else
					nBaseReadMap1.put(nCount1, 1L);
				if(readCount2 != null)
					nBaseReadMap2.put(nCount2, readCount2+1);
				else
					nBaseReadMap2.put(nCount2, 1L);
			}
			lineNum++;

		}
		if(Report) {
			String ret1 = printHashMap(nBaseReadMap1, Report, 1);
			String ret2 = printHashMap(nBaseReadMap2, Report, 2);
			// Write HTML DOCUMENT
			String doc = "          <br><br>\n"
						+"          <div id='NBaseStat'>\n"
						+"            <h3>NBaseStat</h3>\n"
						+"              <table class='table table-striped'>\n"
						+"                <tr><th>Read</th><th>Number of N</th><th>Number of Reads</th></tr>"
						+"                "+ret1+"\n"
						+"                "+ret2+"\n"
						+"              </table>\n"
						+"          </div>\n"
						+"        </div>\n"
						+"        <div class='footer'>FastqGizmo <a href=\"https://www.fastqgizmo.com\">https://www.fastqgizmo.com</a></div>\n"
						+"      </div>\n"
						+"    </div>\n"
						+"    <br><br>\n"
						+"  </body>\n"
						+"</head>\n";
			FileWriter fw = new FileWriter("result.html",true);
			fw.write(doc);
			fw.close();

		} else {
			System.out.println("#Read1");
			printHashMap(nBaseReadMap1, Report, 1);
			System.out.println("#Read2");
			printHashMap(nBaseReadMap2, Report, 2);
		}
	}
}

