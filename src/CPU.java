import java.io.*;

public class CPU {

	public static void main(String[] args) throws FileNotFoundException {

//		if (args.length != 1) {
//			System.out.print("Please pass in a file.");
//			System.exit(0);
//		}
//		
//		BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		
		// copy the original page files to it's own Output folder
		File page_files = copyPageFiles();
		
	}
	
	public static File copyPageFiles() {
		String dst_location = System.getProperty("user.home") + "/cs431/Output";
		int count = 0;

		File src = new File("bin/Page_Files");
		File dst = new File(dst_location + count);
		
		if (!dst.getParentFile().exists()) {
			dst.getParentFile().mkdirs();
		}
		
		while (dst.exists()) {
			count++;
			dst = new File(dst_location + count);
		}
		
		File pf_dst = new File(dst.toString() + "/Page_Files");
		if (!pf_dst.getParentFile().exists()) {
			pf_dst.getParentFile().mkdirs();
		}
		
		try {
			copyFolder(src, pf_dst);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return dst;
	}
	
	public static void copyFolder(File src, File dest) throws IOException {

		if(src.isDirectory()){

			//if directory not exists, create it
			if(!dest.exists()){
				dest.mkdir();
				System.out.println("Directory copied from " + src + "  to " + dest);
			}

			//list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				//construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				//recursive copy
				copyFolder(srcFile,destFile);
			}

		}else{
			//if file, then copy it
			//Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			//copy the file content in bytes
			while ((length = in.read(buffer)) > 0){
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
			System.out.println("File copied from " + src + " to " + dest);
		}
	}

}
