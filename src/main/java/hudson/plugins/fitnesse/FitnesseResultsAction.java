package hudson.plugins.fitnesse;

import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;
import hudson.tasks.test.AbstractTestResultAction;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.StaplerProxy;

import java.util.ArrayList;
import java.util.Collection;

import static hudson.plugins.fitnesse.CompoundFitnesseResults.createFor;
import static java.util.Arrays.asList;


public class FitnesseResultsAction extends AbstractTestResultAction<FitnesseResultsAction> implements StaplerProxy, SimpleBuildStep.LastBuildAction {
	private FitnesseResults results;

	protected FitnesseResultsAction(Run<?, ?> owner, FitnesseResults results) {
		this.results = results;
		this.results.setRun(owner);
	}

	@Override
	public int getFailCount() {
		return results.getFailCount();
	}

	@Override
	public int getTotalCount() {
		return results.getTotalCount();
	}

	@Override
	public int getSkipCount() {
		return results.getSkipCount();
	}

	@Override
	public FitnesseResults getResult() {
		return results;
	}

	/**
	 * {@link Action}
	 */
	@Override
	public String getUrlName() {
		return "fitnesseReport";
	}

	/**
	 * {@link Action}
	 */
	@Override
	public String getDisplayName() {
		return "FitNesse Results";
	}

	/**
	 * {@link Action}
	 */
	@Override
	public String getIconFileName() {
		return "/plugin/fitnesse/icons/fitnesselogo-32x32.gif";
	}

	/** 
	 * {@link StaplerProxy}
	 */
	public Object getTarget() {
		return results;
	}

	/**
	 * Referenced in summary.jelly and FitnesseProjectAction/jobMain.jelly
	 */
	public String getSummary() {
		return String.format("(%d pages: %d wrong or with exceptions, %d ignored)", getTotalCount(), getFailCount(),
				getSkipCount());
	}

	@Override
	public Collection<? extends Action> getProjectActions() {
		Job<?,?> job = run.getParent();
		Collection<Action> list = new ArrayList<Action>();
		list.add(new FitnesseProjectAction(job));
		list.add(new FitnesseHistoryAction(job));
		return list;
	}

	public void mergeResults(FitnesseResults results) {
		this.results = createFor(asList(this.results, results));
	}
}
