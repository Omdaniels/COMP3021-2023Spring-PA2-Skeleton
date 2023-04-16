package hk.ust.comp3021.action;

import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.person.User;
import java.util.*;
import java.util.function.Function;

public class StatisticalInformationAction extends Action {
    public enum InfoKind {
        AVERAGE,
        MAXIMAL,
    };

    private InfoKind kind;

    private final Map<String, Double> actionResult = new HashMap<String, Double>();

    public StatisticalInformationAction(String id, User user, Date time, InfoKind kind) {
        super(id, user, time, ActionType.STATISTICAL_INFO);
        this.kind = kind;
    }

    public InfoKind getKind() {
        return kind;
    }

    public void setKind(InfoKind kind) {
        this.kind = kind;
    }

    public Map<String, Double> getActionResult() {
        return actionResult;
    }

    public void appendToActionResult(String key, Double value) {
        this.actionResult.put(key, value);
    }

    /**
     * TODO `obtainer1` indicates the first profiling criterion,
     *    i.e., Obtain the average number of papers published by researchers per year.
     * @param a list of papers to be profiled
     * @return `actionResult` that contains the target result
     */

    public Function<List<Paper>, Map<String, Double>> obtainer1 = paperList -> {
        Map<String, List<Paper>> researcherPapers = new HashMap<>();
        paperList.forEach(paper -> paper.getAuthors().forEach(author -> {
            researcherPapers.computeIfAbsent(author, k -> new ArrayList<>()).add(paper);
        }));

        researcherPapers.forEach((author, papers) -> {
            long nbDistinctYears = papers.stream().map(Paper::getYear).distinct().count();
            int nbPapers = papers.size();
            double avgPapersPerYear = nbPapers / (double) nbDistinctYears;
            this.actionResult.put(author, avgPapersPerYear);
        });

        return this.actionResult;
    };

    /**
     * TODO `obtainer2` indicates the second profiling criterion,
     *    i.e., Obtain the journals that receive the most papers every year.
     * @param a list of papers to be profiled
     * @return `actionResult` that contains the target result
     * PS1: If two journals receive the same number of papers in a given year, then we take the default order.
     * PS2: We keep the chronological order of year so that the results of the subsequent year will replace the
     *      results of the previous year if one journal receives the most papers in two or more different years.
     */

    public Function<List<Paper>, Map<String, Double>> obtainer2 = paperList -> {
        List<Integer> distinctYears = paperList.stream().map(Paper::getYear).distinct().sorted().collect(Collectors.toList());
        Map<String, List<Paper>> journalPapers = new HashMap<>();

        distinctYears.forEach(year -> {
            journalPapers.clear();
            paperList.stream()
                    .filter(paper -> paper.getYear() == year)
                    .filter(paper -> paper.getJournal() != null)
                    .forEach(paper -> {
                        journalPapers.computeIfAbsent(paper.getJournal(), k -> new ArrayList<>()).add(paper);
                    });

            Optional<Map.Entry<String, List<Paper>>> entryWithMaxSize = journalPapers.entrySet().stream()
                    .max(Comparator.comparing(entry -> entry.getValue().size()));

            entryWithMaxSize.ifPresent(entry -> this.actionResult.put(entry.getKey(), (double) entry.getValue().size()));
        });

        return this.actionResult;
    };

}
