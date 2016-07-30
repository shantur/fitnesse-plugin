package hudson.plugins.fitnesse;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.*;
import hudson.tasks.junit.Messages;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FitnesseResultsRecorder extends Recorder implements SimpleBuildStep {

	private final String fitnessePathToXmlResultsIn;

	@DataBoundConstructor
	public FitnesseResultsRecorder(String fitnessePathToXmlResultsIn) {
		this.fitnessePathToXmlResultsIn = fitnessePathToXmlResultsIn;
	}

	/**
	 * referenced in config.jelly
	 */
	public String getFitnessePathToXmlResultsIn() {
		return fitnessePathToXmlResultsIn;
	}

	/**
	 * {@link BuildStep}
	 */
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}

	@Override
	public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener)
			throws InterruptedException, IOException {
		PrintStream logger = listener.getLogger();
        logger.println(Messages.JUnitResultArchiver_Recording());

        FilePath[] resultFiles = getResultFiles(logger, workspace);
        FitnesseResults results;
        try {
            results = getResults(logger, resultFiles, run.getRootDir());
        } catch (TransformerException e) {
            e.printStackTrace(logger);
            throw new AbortException(String.format("FitNesse reports were found that cannot be read. The cause is: %s", e.getMessage()));
        }
        if (results == null) {
            if (run.getResult() == Result.FAILURE) {
                // most likely a build failed before it gets to the test phase.
                // don't report confusing error message.
                return;
            }
            // most likely a configuration error in the job - e.g. false pattern to match the FitNesse result files
            throw new AbortException(Messages.JUnitResultArchiver_NoTestReportFound());
        }

        synchronized (run) {
            if (results.getBuildResult() != null) {
                run.setResult(results.getBuildResult());
            }
            FitnesseResultsAction action = run.getAction(FitnesseResultsAction.class);
            if (action == null) {
                action = new FitnesseResultsAction(run, results);
                run.addAction(action);
            } else {
                action.mergeResults(results);
                run.save();
            }
        }
	}

	public FilePath[] getResultFiles(PrintStream logger, FilePath workingDirectory) throws IOException,
			InterruptedException {
        logger.println("Working directory is: " + workingDirectory != null ? workingDirectory.getRemote() : "null !!");

		FilePath resultsFile = FitnesseExecutor.getFilePath(logger, workingDirectory, fitnessePathToXmlResultsIn);

		if (resultsFile.exists()) {
			// directly configured single file
			return new FilePath[] { resultsFile };
		} else {
			// glob
			return workingDirectory.list(fitnessePathToXmlResultsIn);
		}
	}

	public FitnesseResults getResults(PrintStream logger, FilePath[] resultsFiles, File rootDir) throws IOException,
			TransformerException, InterruptedException {
		List<FitnesseResults> resultsList = new ArrayList<FitnesseResults>();

		for (FilePath filePath : resultsFiles) {
			FitnesseResults singleResults = getResults(logger, filePath, rootDir);
			resultsList.add(singleResults);
		}

		if (resultsList.isEmpty()) {
			return null;
		}
		if (resultsList.size() == 1) {
			return resultsList.get(0);
		}
		return CompoundFitnesseResults.createFor(resultsList);
	}

	public FitnesseResults getResults(PrintStream logger, FilePath resultsFile, File rootDir) throws IOException,
			TransformerException, InterruptedException {
		InputStream resultsInputStream = null;
		try {
			logger.println("Reading results as " + Charset.defaultCharset().displayName() + " from "
					+ resultsFile.getRemote());
			resultsInputStream = resultsFile.read();

			Path p = Paths.get(resultsFile.getRemote());
			String resultFileName = p.getFileName().toString();

			logger.println("Parsing results... ");
			NativePageCountsParser pageCountsParser = new NativePageCountsParser();
			NativePageCounts pageCounts = pageCountsParser.parse(resultsInputStream, resultFileName, logger, rootDir.getAbsolutePath()
					+ System.getProperty("file.separator"));
			logger.println("resultsFile: " + getFitnessePathToXmlResultsIn());

			logger.println("Got results: " + pageCounts.getSummary());
			return new FitnesseResults(pageCounts);
		} finally {
			if (resultsInputStream != null) {
				try {
					resultsInputStream.close();
				} catch (Exception e) {
					// swallow
				}
			}
		}
	}

	/**
	 * {@link Publisher}
	 */
	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	/**
	 * See <tt>src/main/resources/hudson/plugins/fitnesse/FitnesseResultsRecorder/config.jelly</tt>
	 */
	@Extension
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		public FormValidation doCheckFitnessePathToXmlResultsIn(@QueryParameter String value) throws IOException,
				ServletException {
			if (value.length() == 0)
				return FormValidation.error("Please specify where to read FitNesse results from.");
			if (!value.endsWith("xml"))
				return FormValidation.warning("File does not end with 'xml': is that correct?");
			return FormValidation.ok();
		}

		/**
		 * {@link BuildStepDescriptor}
		 */
		@Override
		@SuppressWarnings("rawtypes")
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			// works with any kind of project
			return true;
		}

		/**
		 * {@link ModelObject}
		 */
		@Override
		public String getDisplayName() {
			return "Publish Fitnesse results report";
		}
	}
}
