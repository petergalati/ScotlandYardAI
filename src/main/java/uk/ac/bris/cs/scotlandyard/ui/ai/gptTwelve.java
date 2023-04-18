package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;


import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;
import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.Ai;
import uk.ac.bris.cs.scotlandyard.model.Board;
import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard;

public class gptTwelve implements Ai {

	@Nonnull @Override public String name() { return "gptTwelve"; }

	@Nonnull @Override public Move pickMove(
			@Nonnull Board board,
			Pair<Long, TimeUnit> timeoutPair) {
		// returns a random move, replace with your own implementation
//		var moves = board.getAvailableMoves().asList();
//		return moves.get(new Random().nextInt(moves.size()));

		var moves = board.getAvailableMoves().asList();
		return null;
	}

	public int score(Board board) {
		//for mrX

		//checks if detective is present at neighbouring node:
		//	score of 0 is assigned if detective IS present
		//	score of 1 is assigned if detective is NOT present
		int mrxPos = board.getMrXTravelLog().get(-1).location().get();
		ImmutableValueGraph<Integer, ImmutableSet<ScotlandYard.Transport>> graph = board.getSetup().graph;
		Set<Integer> adjNodesSet = graph.adjacentNodes(mrxPos);
		for (int node : adjNodesSet) {
		}


		return 0;



	}
}
