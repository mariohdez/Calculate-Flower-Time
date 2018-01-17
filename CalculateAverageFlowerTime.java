package lab3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class CalculateAverageFlowerTime {
    public static void main(String[] args) {
	File f = new File("/Users/mariohernandez/Downloads/hi.csv");
	String[] elements = null;
	Scanner scanner = null;
	String line = null;
	Set<Bee> bees = new HashSet<>();
	Map<String, Bee> colonyIdToBee = new HashMap<>();
	File csv = new File("posneg.csv");
	FileWriter fw;
	try {
	    fw = new FileWriter(csv);
	} catch (IOException e1) {
	    e1.printStackTrace();
	    return;
	}
	try {
	    scanner = new Scanner(f);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
	boolean firstLine = true;

	if (scanner == null) {
	    System.exit(-1);
	}

	while (scanner.hasNextLine()) {
	    line = scanner.nextLine();
	    if (firstLine) {
		firstLine = false;
		continue;
	    }
	    elements = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
	    String colonyId = elements[0] + elements[1];
	    Bee bee = colonyIdToBee.get(colonyId);
	    if (bee == null) {
		bee = new Bee();
		bee.colony = elements[0];
		bee.id = elements[1];
		bees.add(bee);
		colonyIdToBee.put(colonyId, bee);
	    }
	    if (elements[8].contains("\"")) {
		quotes(elements, bee);
	    } else {
		noquotes(elements, bee);
	    }
	}
	scanner.close();

	for (Bee b : bees) {
	    for (Map.Entry<String, Double> e : b.flowerToTime.entrySet()) {
		b.flowerToAvg.put(e.getKey(), e.getValue() / b.flowerToCount.get(e.getKey()));
	    }
	}
	try {
	    String CK = "cyan/gray", BG = "blue/green", GK = "green/gray", BC = "blue/cyan";
	    fw.write("Bee ID, Response time, Flower type\n");
	    // fw.write("bid, " + CK + ", " + BG + ", " + GK + ", " + BC + "\n");

	    for (Bee bee : bees) {
		fw.write(bee.id);
		fw.write(",");
		Double d = bee.flowerToAvg.get(CK);

		// 1.
		if (d == null) {
		    fw.write("NA,");
		} else {
		    fw.write(Double.toString(d) + ",");
		}
		fw.write(CK + "\n");

		// 2.
		fw.write(bee.id);
		fw.write(",");
		d = bee.flowerToAvg.get(BG);
		if (d == null) {
		    fw.write("NA,");
		} else {
		    fw.write(Double.toString(d) + ",");
		}
		fw.write(BG + "\n");

		// 3.
		fw.write(bee.id);
		fw.write(",");
		d = bee.flowerToAvg.get(GK);
		if (d == null) {
		    fw.write("NA,");
		} else {
		    fw.write(Double.toString(d) + ",");
		}
		fw.write(GK + "\n");

		// 4.
		fw.write(bee.id);
		fw.write(",");
		d = bee.flowerToAvg.get(BC);
		if (d == null) {
		    fw.write("NA,");
		} else {
		    fw.write(Double.toString(d) + ",");
		}
		fw.write(BC + "\n");
	    }
	    fw.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private static void noquotes(String[] elements, Bee b) {
	String type = elements[elements.length - 2];
	if (!type.equals("D")) {
	    return;
	}
	if (b.flowerToTime.size() > 6) {
	    return;
	}

	SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SSS");
	try {
	    String[] startTime = elements[8].split("\\.");
	    String arg0 = startTime[0] + ":" + startTime[1];
	    Date startDate = sdf.parse(arg0);
	    String[] endTime = elements[9].split("\\.");
	    String arg1 = endTime[0] + ":" + endTime[1];
	    Date endDate = sdf.parse(arg1);
	    double diff = (double) endDate.getTime() - startDate.getTime();
	    double totalTime = diff / 1000.0;
	    String flowerColor = elements[5];
	    Double t = b.flowerToTime.get(flowerColor);
	    Integer cnt = b.flowerToCount.get(flowerColor);
	    if (cnt == null) {
		b.flowerToCount.put(flowerColor, 1);
	    } else {
		b.flowerToCount.put(flowerColor, cnt + 1);
	    }
	    if (t == null) {
		b.flowerToTime.put(flowerColor, totalTime);
	    } else {
		b.flowerToTime.put(flowerColor, totalTime + t);
	    }
	} catch (ParseException e1) {
	    e1.printStackTrace();
	}
    }

    private static void quotes(String[] elements, Bee b) {
	String type = elements[elements.length - 2];
	if (!type.equals("D")) {
	    return;
	}
	if (b.flowerToTime.size() > 6) {
	    return;
	}

	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
	String start = elements[8].split("\"")[1].split(",")[0] + "." + elements[8].split("\"")[1].split(",")[1];
	String end = elements[9].split("\"")[1].split(",")[0] + "." + elements[9].split("\"")[1].split(",")[1];
	try {
	    Date startDate = sdf.parse(start);
	    Date endDate = sdf.parse(end);
	    double diff = endDate.getTime() - startDate.getTime();
	    double totalTime = diff / 1000.0;
	    // 6th column
	    String flowerColor = elements[5];
	    Double t = b.flowerToTime.get(flowerColor);
	    Integer cnt = b.flowerToCount.get(flowerColor);
	    if (cnt == null) {
		b.flowerToCount.put(flowerColor, 1);
	    } else {
		b.flowerToCount.put(flowerColor, cnt + 1);
	    }
	    if (t == null) {
		b.flowerToTime.put(flowerColor, totalTime);
	    } else {
		b.flowerToTime.put(flowerColor, totalTime + t);
	    }
	} catch (ParseException e1) {
	    e1.printStackTrace();
	}
    }

    private static class Bee {
	private String colony;
	private String id;
	Map<String, Double> flowerToTime = new HashMap<>();
	Map<String, Double> flowerToAvg = new HashMap<>();
	Map<String, Integer> flowerToCount = new HashMap<>();

	@Override
	public String toString() {
	    return colony + " " + id;
	}
    }
}
