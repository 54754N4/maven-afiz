import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "add-files-into-archive", defaultPhase = LifecyclePhase.COMPILE)
public class AddFilesIntoZips extends AbstractMojo {
	private static final String TEMP_FILE_NAME = "temp.archive";
	
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	MavenProject project;
	
	@Parameter(required = true, property = "archive")
	private File archive;
	
	@Parameter(required = true, property = "files")
	private File[] files;

	public void execute() throws MojoExecutionException, MojoFailureException {
		Path temp = Paths.get(TEMP_FILE_NAME);
		ZipEntry entry;
		try (
			BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(archive.toPath()));
			BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(temp, StandardOpenOption.CREATE));
			ZipInputStream zis = new ZipInputStream(bis);
			ZipOutputStream zos = new ZipOutputStream(bos);
		) {
			// Transfer original files
			while ((entry = zis.getNextEntry()) != null)
				transfer(zos, zis, entry);
			// Add new files
			for (File file : files) {
				entry = new ZipEntry(file.getName());
				try (
					InputStream fis = Files.newInputStream(file.toPath(), StandardOpenOption.CREATE);
					BufferedInputStream is = new BufferedInputStream(fis);
				) {
					transfer(zos, is, entry);
				}
			}
			zos.finish();
			// Replace archive with new one
			Files.deleteIfExists(archive.toPath());
			Files.move(temp, temp.resolveSibling(archive.getName()));
		} catch (Exception e) {
			throw new MojoFailureException(e.getMessage());
		}
	}
	
	// Assumption: all streams were wrapped by Buffered(Input/Output)Streams	
	private static final void transfer(ZipOutputStream zos, InputStream is, ZipEntry entry) throws IOException {
		zos.putNextEntry(entry);
		while (is.available() != 0)
			zos.write(is.read());
		zos.closeEntry();
	}
}
