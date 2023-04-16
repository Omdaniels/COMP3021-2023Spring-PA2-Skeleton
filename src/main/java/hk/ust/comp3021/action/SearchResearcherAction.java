package hk.ust.comp3021.action;

import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.person.User;
import java.util.*;
import java.util.function.Supplier;

public class SearchResearcherAction extends Action {
    public enum SearchResearcherKind {
        PAPER_WITHIN_YEAR,
        JOURNAL_PUBLISH_TIMES,
        KEYWORD_SIMILARITY,
    };

    private String searchFactorX;
    private String searchFactorY;
    private SearchResearcherKind kind;

    private final HashMap<String, List<Paper>> actionResult = new HashMap<String, List<Paper>>();

    public SearchResearcherAction(String id, User user, Date time, String searchFactorX, String searchFactorY, SearchResearcherKind kind) {
        super(id, user, time, ActionType.SEARCH_PAPER);
        this.searchFactorX = searchFactorX;
        this.searchFactorY = searchFactorY;
        this.kind = kind;
    }

    public String getSearchFactorX() {
        return searchFactorX;
    }

    public String getSearchFactorY() {
        return searchFactorY;
    }

    public void setSearchFactorX(String searchFactorX) {
        this.searchFactorX = searchFactorX;
    }

    public void setSearchFactorY(String searchFactorY) {
        this.searchFactorY = searchFactorY;
    }

    public SearchResearcherKind getKind() {
        return kind;
    }

    public void setKind(SearchResearcherKind kind) {
        this.kind = kind;
    }

    public HashMap<String, List<Paper>> getActionResult() {
        return actionResult;
    }

    public void appendToActionResult(String researcher, Paper paper) {
        List<Paper> paperList = this.actionResult.get(researcher);
        if (paperList == null) {
            paperList = new ArrayList<Paper>();
            this.actionResult.put(researcher, paperList);
        }
        paperList.add(paper);
    }

    /**
     * TODO `searchFunc1` indicates the first searching criterion,
     *    i.e., Search researchers who publish papers more than or equal to X times in the recent Y years
     * @param null
     * @return `actionResult` that contains the relevant researchers
     */
    public Supplier<HashMap<String, List<Paper>>> searchFunc1 = () -> {
        int Y = 1; 
        HashMap<String, List<Paper>> result = new HashMap<>();
        for (Map.Entry<String, List<Paper>> entry : actionResult.entrySet()) {
            String researcher = entry.getKey();
            List<Paper> papers = entry.getValue();
            for (Paper paper : papers) {
                if (paper.getYear() == (new Date().getYear() + 1900) - Y) { 
                    result.put(researcher, papers);
                    break; 
                }
            }
        }
        return result;
    };


    /**
     * TODO `searchFunc2` indicates the second searching criterion,
     *    i.e., Search researchers whose papers published in the journal X have abstracts of which the length is more than or equal to Y.
     * @param null
     * @return `actionResult` that contains the relevant researchers
     */
    public Supplier<HashMap<String, List<Paper>>> searchFunc2 = () -> {
        HashMap<String, List<Paper>> relevantResearchers = new HashMap<>();
        for (Map.Entry<String, List<Paper>> entry : actionResult.entrySet()) {
            List<Paper> papers = entry.getValue();
            List<Paper> relevantPapers = new ArrayList<>();
            for (Paper paper : papers) {
                if (paper.getJournal().equals(searchFactorX) && paper.getAbsContent().length() >= Integer.parseInt(searchFactorY)) {
                    relevantPapers.add(paper);
                }
            }
            if (!relevantPapers.isEmpty()) {
                relevantResearchers.put(entry.getKey(), relevantPapers);
            }
        }
        return relevantResearchers;
    };


    public static int getLevenshteinDistance(String str1, String str2) {
        return 0;
    }

    public double getSimilarity(String str1, String str2) {
        return 0;
    }

    /**
     * TODO `searchFunc2` indicates the third searching criterion
     *    i.e., Search researchers whoes keywords have more than or equal to similarity X% as one of those of the researcher Y.
     * @param null
     * @return `actionResult` that contains the relevant researchers
     * PS: 1) In this method, you are required to implement an extra method that calculates the Levenshtein Distance for
     *     two strings S1 and S2, i.e., the edit distance. Based on the Levenshtein Distance, you should calculate their
     *     similarity like `(1 - levenshteinDistance / max(S1.length, S2.length)) * 100`.
     *     2) Note that we need to remove paper(s) from the paper list of whoever are co-authors with the given researcher.
     */
    
    private int calculateLevenshteinDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m+1][n+1];
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i-1) == s2.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j-1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i-1][j], Math.min(dp[i][j-1], dp[i-1][j-1]));
                }
            }
        }
        return dp[m][n];
    }
    

    public Supplier<HashMap<String, List<Paper>>> searchFunc3 = () -> {
        int percentageX = Integer.parseInt(this.searchFactorX);
        String researcherY = this.searchFactorY;
        List<Paper> papersResearcherY = new ArrayList<>();
        papersResearcherY.addAll(this.actionResult.get(researcherY));

        this.actionResult.entrySet().forEach(entry -> {
            List<Paper> listPaper = entry.getValue();

            List<Paper> listPaperFiltered = listPaper.stream()
                    .filter(paper -> !paper.getAuthors().contains(researcherY)) // Remove papers where researcherY is a co-author
                    .filter(paper -> {
                        String keywordsCurrentPaper = paper.getKeywords().stream().collect(Collectors.joining(","));
                        boolean isSimilar = papersResearcherY.stream()
                                .anyMatch(paperResearcherY -> {
                                    String keywordsPaperResearcherY = paperResearcherY.getKeywords().stream().collect(Collectors.joining(","));
                                    int similarity = calculateLevenshteinDistance(keywordsCurrentPaper, keywordsPaperResearcherY);
                                    int maxLength = Math.max(keywordsCurrentPaper.length(), keywordsPaperResearcherY.length());
                                    return (1 - (double) similarity / maxLength) * 100 >= percentageX;
                                });
                        return isSimilar;
                    })
                    .collect(Collectors.toList());

            listPaper.clear();
            listPaper.addAll(listPaperFiltered);
        });

        this.actionResult.entrySet().removeIf(entry -> entry.getValue().size() == 0);
        return this.actionResult;
    };


}
