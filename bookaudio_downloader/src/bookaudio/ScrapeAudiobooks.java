package bookaudio;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Command(name = "bookaudio", mixinStandardHelpOptions = true, version = "0.1",
		description = "downloads audiobooks from bookaudio.online\n"
				+ "Audiobooks are organized in the following path structure:\n"
				+ "{base directory}/{author}/{book title}/{author} - {boot title} - {file sequence}.mp3")
public class ScrapeAudiobooks implements Callable<Integer> {
	
	@Parameters(paramLabel = "URLs", description = "URLs pointing to audiobook pages", arity = "1..*")
	URL audiobookUrls[];
	
	@Option(names = {"-d", "--directory"}, paramLabel = "<base directory>",
			description = "directory where audiobook directory structure is created, defaults to current directory")
	Path baseDirectory = Paths.get("");
	
	@Option(names = {"-o", "--overwrite"}, paramLabel = "allow overwrite", 
			description = "overwrite existing files instead of skipping them")
	boolean overwrite = false;
	
	@Override
	public Integer call() throws Exception {

		for(URL audiobookUrl : audiobookUrls) {
			Audiobook audiobook = new Audiobook(audiobookUrl);
			audiobook.download(baseDirectory, overwrite);
		}
		
		return 0;
	}
	
	public static void main(String[] args) {
		
		//String targetDirectory = args[0];
		//String audiobookUrls[] = Arrays.copyOfRange(args, 1, args.length);
		
		/*for(String audiobookUrl : audiobookUrls) {
			Audiobook audiobook = new Audiobook(audiobookUrl);
			audiobook.download(targetDirectory);
		}*/
		
		int exitCode = new CommandLine(new ScrapeAudiobooks()).execute(args);
		System.exit(exitCode);
		
		
	}

}
