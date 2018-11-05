package evaluation;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import algorithms.BreadthFirstPicking;
import algorithms.Experiment;
import algorithms.RandomWalk;
import algorithms.RecursiveBreadthFirstPicking;
import algorithms.RecursiveNaiveGreedyPicking;
import algorithms.Traversal;
import algorithms.TwoStepLookAheadalgorithm;
import distance.Distance;
import distance.Euclidean;

public class AlgoTest {
	// public static void main(String[] args) throws SQLException,
	// FileNotFoundException, UnsupportedEncodingException
	// {
	@Test
	public void test() throws FileNotFoundException, UnsupportedEncodingException, SQLException {
		Experiment exp;
		int k = 30;
		String aggFunc = "SUM";
		ArrayList<String> groupby = new ArrayList<String>(
				Arrays.asList("is_multi_query", "is_profile_query", "is_event_query", "has_impressions_tbl",
						"has_clicks_tbl", "has_actions_tbl", "has_distinct", "has_list_fn"));
		String yAxis = "slots_millis_reduces";
		String xAxis = "has_list_fn";
		Distance ed = new Euclidean();
		Traversal SRW = new RandomWalk();
		exp = new Experiment("turn", xAxis, yAxis, groupby, "SUM", k, SRW, ed, 0, 0.8, false);
		long duration = exp.timedRunOutput(exp);

		Traversal frontierGreedy = new BreadthFirstPicking();
		exp = new Experiment("turn", xAxis, yAxis, groupby, "SUM", k, frontierGreedy, ed, 0, 0.8, false);
		duration = exp.timedRunOutput(exp);
		assertTrue("Frontier Greedy Euclidean Test", exp.dashboard.maxSubgraphUtility == 146783.0380859375);
		Traversal LA2max = new TwoStepLookAheadalgorithm("max");
		exp = new Experiment("turn", xAxis, yAxis, groupby, "SUM", k, LA2max, ed, 0, 0.8, false);
		duration = exp.timedRunOutput(exp);
		assertTrue("Two Step Look Ahead Max Euclidean Test", exp.dashboard.maxSubgraphUtility == 165367.3768310547);
		Traversal LA2sum = new TwoStepLookAheadalgorithm("sum");
		exp = new Experiment("turn", xAxis, yAxis, groupby, "SUM", k, LA2sum, ed, 0, 0.8, false);
		duration = exp.timedRunOutput(exp);
		assertTrue("Two Step Look Ahead Sum Euclidean Test", exp.dashboard.maxSubgraphUtility == 165367.3768310547);
		Traversal LA5 = new RecursiveBreadthFirstPicking(5);
		exp = new Experiment("turn", xAxis, yAxis, groupby, "SUM", k, LA5, ed, 0, 0.8, false);
		duration = exp.timedRunOutput(exp);
		assertTrue("5 Step Look Ahead Euclidean Test", exp.dashboard.maxSubgraphUtility == 165367.3768310547);
		Traversal LA5_levelwise = new RecursiveNaiveGreedyPicking(5);
		exp = new Experiment("turn", xAxis, yAxis, groupby, "SUM", k, LA5_levelwise, ed, 0, 0.8, false);
		duration = exp.timedRunOutput(exp);
		assertTrue("5 Step Look Ahead Levelwise Euclidean Test", exp.dashboard.maxSubgraphUtility == 165367.3768310547);

	}

}
