package hudson.plugins.fitnesse;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.plugins.fitnesse.NativePageCounts.Counts;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;

public class HudsonDependentTest {

	@Rule
	public JenkinsRule jenkinsRule = new JenkinsRule();

	@Test
	public void testOwnerOfParentResultsShouldBeOwnerOfChildResults() throws Exception {
		FitnesseResults parent = new FitnesseResults((Counts) null);
		FitnesseResults child = new FitnesseResults((Counts) null);
		parent.setRun(new FreeStyleBuild(jenkinsRule.createFreeStyleProject(getName())));
		parent.addChild(child);
		assertSame(parent.getRun(), child.getRun());
	}

	//	public void testBuildStartingFitnesseWithAbsoluteAndRelativePaths() throws Exception {
	//		FreeStyleProject project = createFreeStyleProject(getName());
	//		project.getBuildersList().clear();
	//		project.getPublishersList().clear();
	//		String resultsFile = "fitnesse-results.xml";
	//
	//		Map<String, String> options = new HashMap<String, String>();
	//		options.put(FitnesseBuilder.START_FITNESSE, Boolean.TRUE.toString());
	//		options.put(FitnesseBuilder.PATH_TO_JAR, getTestResourceFitnesseJar());
	//		options.put(FitnesseBuilder.PATH_TO_ROOT, getTestResourceFitNesseRoot());
	//		options.put(FitnesseBuilder.FITNESSE_PORT, "8081");
	//		options.put(FitnesseBuilder.TARGET_PAGE, "HudsonPlugin.SuiteAll");
	//		options.put(FitnesseBuilder.TARGET_IS_SUITE, Boolean.TRUE.toString());
	//		options.put(FitnesseBuilder.PATH_TO_RESULTS, resultsFile);
	//		FitnesseBuilder builder = new FitnesseBuilder(options);
	//
	//		project.getBuildersList().add(builder);
	//		FitnesseResultsRecorder fitnesseResultsRecorder = new FitnesseResultsRecorder(resultsFile);
	//		project.getPublishersList().add(fitnesseResultsRecorder);
	//		//AbsolutePath build
	//		FreeStyleBuild build = project.scheduleBuild2(0).get();
	//		Assert.assertTrue(build.getLogFile().getAbsolutePath(), Result.FAILURE.equals(build.getResult()));
	//		FitnesseResultsAction resultsAction = build.getAction(FitnesseResultsAction.class);
	//		System.out.println(resultsAction);
	//		assertExpectedResults(resultsAction);
	//
	//		FitnesseProjectAction projectAction = (FitnesseProjectAction) fitnesseResultsRecorder.getProjectActions(project).toArray()[0];
	//		Assert.assertSame(resultsAction, projectAction.getLatestResults());
	//		Assert.assertFalse(projectAction.getTrend().historyAvailable());
	//
	//		project.getBuildersList().clear();
	//		project.getPublishersList().clear();
	//		resultsFile = "fitnesse-results2.xml";
	//
	//		project.getBuildersList().add(new Shell("cp " + builder.getFitnessePathToJar() + " " + build.getWorkspace().getRemote()));
	//		project.getBuildersList().add(new Shell("cp -r " + builder.getFitnessePathToRoot() + " " + build.getWorkspace().getRemote()));
	//		options.put(FitnesseBuilder.PATH_TO_JAR, "fitnesse.jar");
	//		options.put(FitnesseBuilder.PATH_TO_ROOT, "FitNesseRoot");
	//		options.put(FitnesseBuilder.PATH_TO_RESULTS, resultsFile);
	//
	//		project.getBuildersList().add(builder);
	//		fitnesseResultsRecorder = new FitnesseResultsRecorder(resultsFile);
	//		project.getPublishersList().add(fitnesseResultsRecorder);
	//		//RelativePath build
	//		build = project.scheduleBuild2(0).get();
	//		Assert.assertTrue(build.getLogFile().getAbsolutePath(), Result.FAILURE.equals(build.getResult()));
	//		resultsAction = build.getAction(FitnesseResultsAction.class);
	//		assertExpectedResults(resultsAction);
	//
	//		projectAction = (FitnesseProjectAction) fitnesseResultsRecorder.getProjectActions(project).toArray()[0];
	//		Assert.assertSame(resultsAction, projectAction.getLatestResults());
	//		Assert.assertTrue(projectAction.getTrend().historyAvailable());
	//	}
	//
	//	private String getTestResourceFitnesseJar() {
	//		return new File(new File(System.getProperty("user.dir")), "target/test-classes/fitnesse.jar").getAbsolutePath();
	//	}
	//
	//	private String getTestResourceFitNesseRoot() {
	//		return new File(new File(System.getProperty("user.dir")), "target/test-classes/FitNesseRoot").getAbsolutePath();
	//	}
	//
	//	private void assertExpectedResults(FitnesseResultsAction resultsAction) {
	//		Assert.assertEquals("passed", 1, resultsAction.getResult().getPassCount());
	//		Assert.assertEquals("failed", 2, resultsAction.getResult().getFailCount());
	//		Assert.assertEquals("ignored", 1, resultsAction.getResult().getIgnoredCount());
	//		Assert.assertEquals("exceptions", 1, resultsAction.getResult().getExceptionCount());
	//	}
	//
	//	public void testBuildForStartedFitnesse() throws Exception {
	//		File workingDir = new File(getTestResourceFitNesseRoot()).getParentFile();
	//		String[] commands = new String[] {"java", "-jar", getTestResourceFitnesseJar(),
	//			"-d", workingDir.getPath(),
	//			"-r", "FitNesseRoot", "-p", "8082"};
	//		Process process = new ProcessBuilder(commands).directory(workingDir).start();
	//		Thread.sleep(1500);
	//		try {
	//			FreeStyleProject project = createFreeStyleProject(getName());
	//			project.getBuildersList().clear();
	//			project.getPublishersList().clear();
	//			String resultsFile = "fitnesse-results3.xml";
	//
	//			Map<String, String> options = new HashMap<String, String>();
	//			options.put(FitnesseBuilder.START_FITNESSE, Boolean.FALSE.toString());
	//			options.put(FitnesseBuilder.FITNESSE_HOST, "localhost");
	//			options.put(FitnesseBuilder.FITNESSE_PORT, "8082");
	//			options.put(FitnesseBuilder.TARGET_PAGE, "HudsonPlugin.SuiteAll");
	//			options.put(FitnesseBuilder.TARGET_IS_SUITE, Boolean.TRUE.toString());
	//			options.put(FitnesseBuilder.PATH_TO_RESULTS, resultsFile);
	//			FitnesseBuilder builder = new FitnesseBuilder(options);
	//
	//			project.getBuildersList().add(builder);
	//			project.getPublishersList().add(new FitnesseResultsRecorder(resultsFile));
	//			FreeStyleBuild build = project.scheduleBuild2(0).get();
	//			Assert.assertTrue(build.getLogFile().getAbsolutePath(), Result.FAILURE.equals(build.getResult()));
	//			FitnesseResultsAction resultsAction = build.getAction(FitnesseResultsAction.class);
	//			assertExpectedResults(resultsAction);
	//		} finally {
	//			process.destroy();
	//		}
	//	}
	//
	//	public void testBuildStartingFitnesseAndExplodingTheJarFile() throws Exception {
	//		FreeStyleProject project = createFreeStyleProject(getName());
	//		project.getBuildersList().clear();
	//		project.getPublishersList().clear();
	//		String resultsFile = "fitnesse-results4.xml";
	//		project.getBuildersList().add(new Shell("echo")); // only want this to get workspace
	//		FreeStyleBuild build = project.scheduleBuild2(0).get();
	//		FilePath workspace = build.getWorkspace();
	//
	//		Map<String, String> options = new HashMap<String, String>();
	//		options.put(FitnesseBuilder.START_FITNESSE, Boolean.TRUE.toString());
	//		options.put(FitnesseBuilder.PATH_TO_JAR, workspace.child("fitnesse.jar").getRemote());
	//		options.put(FitnesseBuilder.PATH_TO_ROOT, workspace.getRemote());
	//		options.put(FitnesseBuilder.FITNESSE_PORT, "8083");
	//		options.put(FitnesseBuilder.TARGET_PAGE, "FitNesse.SuiteAcceptanceTests.SuiteFixtureTests.SuiteColumnFixtureSpec.TestMissingMethod");
	//		options.put(FitnesseBuilder.TARGET_IS_SUITE, Boolean.FALSE.toString());
	//		options.put(FitnesseBuilder.PATH_TO_RESULTS, resultsFile);
	//		FitnesseBuilder builder = new FitnesseBuilder(options);
	//
	//		project.getBuildersList().clear();
	//		project.getPublishersList().clear();
	//		project.getBuildersList().add(new Shell("cp " + getTestResourceFitnesseJar() + " " + workspace.getRemote()));
	//		project.getBuildersList().add(builder);
	//		project.getPublishersList().add(new FitnesseResultsRecorder(resultsFile));
	//
	//		build = project.scheduleBuild2(0).get();
	//		Assert.assertTrue(build.getLogFile().getAbsolutePath(), Result.FAILURE.equals(build.getResult()));
	//		FitnesseResultsAction resultsAction = build.getAction(FitnesseResultsAction.class);
	//		Assert.assertTrue(resultsAction.getResult().getPassCount() > 0);
	//		Assert.assertEquals(4, resultsAction.getResult().getFailCount());
	//	}

	@Test
	public void testBuildWithoutResults() throws Exception {
		FreeStyleProject project = jenkinsRule.createFreeStyleProject(getName());
		project.getBuildersList().clear();
		project.getPublishersList().clear();
		String resultsFile = "fitnesse-results-inexistant.xml";
		project.getPublishersList().add(new FitnesseResultsRecorder(resultsFile));

		FreeStyleBuild build = project.scheduleBuild2(0).get();
		assertTrue(build.getLogFile().getAbsolutePath(), Result.FAILURE.equals(build.getResult()));
		FitnesseResultsAction resultsAction = build.getAction(FitnesseResultsAction.class);
		assertNull(resultsAction);
	}

	private String getName() {
		return getClass().getSimpleName();
	}
}
