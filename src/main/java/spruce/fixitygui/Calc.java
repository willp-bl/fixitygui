package spruce.fixitygui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.sun.xml.internal.ws.api.pipe.Codecs;

import javafx.scene.control.ProgressBar;

public class Calc {

	private Calc() {

	}

	private static long calcNumFiles(File pDir) {
		long count = 0;
		if(pDir.isDirectory()) {
			for(File f:pDir.listFiles()) {
				if(f.isDirectory()) {
					count+=calcNumFiles(f);
				} else {
					count++;
				}
			}
		}
		return count;
	}

	public static Map<String, String> verifyChecksums(File pInputDir, File pChecksumFile, ProgressBar pProgress) {
		Map<String, String> hashes = null;
		Map<String, String> failures = new HashMap<String, String>();
		
		try {
			hashes = loadChecksums(pChecksumFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long count = 0;
		for(String key:hashes.keySet()) {
			System.out.println("Checking: "+key);
			String newSum = "";
			try {
				newSum = DigestUtils.md5Hex(new FileInputStream(pInputDir.getAbsolutePath()+"/"+key)).toUpperCase();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!newSum.equals(hashes.get(key).toUpperCase())) {
				failures.put(key, newSum);
			}
			pProgress.setProgress((double)count++/hashes.size());
		}
		
		if(failures.size()>0) {
			System.out.println("Failures:");
			for(String key:failures.keySet()) {
				System.out.println(key+": "+failures.get(key)+" "+hashes.get(key));
			}
		} else {
			System.out.println("Check ok");
		}

		return failures;
	}

	private static Map<String, String> loadChecksums(File pChecksumFile) throws IOException {
		Map<String, String> hashes = new HashMap<String, String>();
		BufferedReader in = new BufferedReader(new FileReader(pChecksumFile));
		while(in.ready()) {
			String line = in.readLine();
			hashes.put(line.substring(34), line.substring(0, 32));
		}
		in.close();
		return hashes;
	}

	public static long calcChecksums(File pInputDir, File pChecksumFile, ProgressBar pProgress) {
		double max = calcNumFiles(pInputDir);
		double count = 0;
		
		Map<String, String> hashes = new HashMap<String, String>();

		//pProgress.setProgress(count/max);

		try {
			calcChecksums2(pInputDir, pProgress, count, max, hashes);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			saveChecksumsToFile(pInputDir, pChecksumFile, hashes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return (long)max;
	}

	private static void saveChecksumsToFile(File pInputDir, File pChecksumFile, Map<String, String> pHashes) throws IOException {
		//do not overwrite
		if(pChecksumFile.exists()) return;
		PrintWriter out = new PrintWriter(new FileWriter(pChecksumFile));
		for(String key:pHashes.keySet()) {
			out.println(pHashes.get(key).toUpperCase()+"  "+key.substring(pInputDir.getAbsolutePath().length()+1));
		}
		out.close();
		
	}
	
	private static double calcChecksums2(File pInputDir, ProgressBar pProgress, double count, final double max, Map<String, String> hashes) throws FileNotFoundException, IOException {

		for(File f:pInputDir.listFiles()) {
			if(f.isDirectory()) {
				count = calcChecksums2(f, pProgress, count, max, hashes);
			} else {
				//calc checksum here
				hashes.put(f.getAbsolutePath(), DigestUtils.md5Hex(new FileInputStream(f)));//.getAbsolutePath()));
				pProgress.setProgress(count++/max);
			}
		}
		return count;
	}


}
