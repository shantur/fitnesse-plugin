package hudson.plugins.fitnesse;

import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;

public class FitnesseProjectAction implements Action {


	public final Job<?,?> job;

	public FitnesseProjectAction(Job<?,?> job) {
		this.job = job;
	}

	/**
	 * @return null to hide from left-hand list
	 */
	public String getIconFileName() {
		return null;
	}

	/**
	 * @return null to hide from left-hand list
	 */
	public String getDisplayName() {
		return null;
	}

	/**
	 * @see Action#getUrlName()
	 */
	public String getUrlName() {
		return "fitnesse";
	}

	/**
	 * Used in floatingBox.jelly
	 */
	public boolean hasTrend() {
		return getLatestResults() != null;
	}

	/**
	 * Used in floatingBox.jelly
	 */
	public History getTrend() {
		FitnesseResultsAction latestResults = getLatestResults();
		if (latestResults == null) {
			return null;
		}
		FitnesseResults result = latestResults.getResult();
		return new History(result, 500, 200);
	}

	/**
	 * Used in jobMain.jelly
	 * {@see TestResultProjectAction#getLastTestResultAction()}
	 */
	public FitnesseResultsAction getLatestResults() {
		final Run<?, ?> tb = job.getLastSuccessfulBuild();
		Run<?, ?> b = job.getLastBuild();
		while (b != null) {
			FitnesseResultsAction a = b.getAction(FitnesseResultsAction.class);
			if (a != null) {
				return a;
			} else if (b == tb) {
				// if even the last successful build didn't produce the test result,
				// that means we just don't have any tests configured.
				return null;
			}
			b = b.getPreviousBuild();
		}

		return null;
	}
}
