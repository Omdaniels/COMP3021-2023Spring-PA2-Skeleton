package hk.ust.comp3021.action;

import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.person.User;
import java.util.*;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SortPaperAction extends Action {
    public enum SortBase {
        ID,
        TITLE,
        AUTHOR,
        JOURNAL,
    };

    public enum SortKind {
        ASCENDING,
        DESCENDING,
    };

    private SortBase base;

    private SortKind kind;

    private final List<Paper> actionResult = new ArrayList<>();

    public SortPaperAction(String id, User user, Date time, SortBase base, SortKind kind) {
        super(id, user, time, ActionType.SORT_PAPER);
        this.base = base;
        this.kind = kind;
    }

    public SortBase getBase() {
        return base;
    }

    public void setBase(SortBase base) {
        this.base = base;
    }

    public SortKind getKind() {
        return kind;
    }

    public void setKind(SortKind kind) {
        this.kind = kind;
    }

    public List<Paper> getActionResult() {
        return actionResult;
    }

    public void appendToActionResult(Paper paper) {
        this.actionResult.add(paper);
    }

    /**
     * TODO `appendToActionResultByLambda` appends one paper into `actionResult` each time.
     * @param paper to be appended into `actionResult`
     * @return null
     */
    
    public Consumer<Paper> appendToActionResultByLambda = (paper) -> this.actionResult.add(paper);

    /**
     * TODO `kindPredicate` determine whether the sort kind is `SortKind.DESCENDING`.
     * @param kind to be compared with `SortKind.DESCENDING`
     * @return boolean variable that indicates whether they are equal
     */
    
   
 
    public Predicate<SortKind> kindPredicate = (kind) -> kind == SortKind.DESCENDING;


    /**
     * TODO `comparator` requires you to implement four custom comparators for different scenarios.
     * The scenarios are defined by: ID, title, author and journal.
     * @param paper to be sorted
     * @return Given the sort base, there are three conditions for the return:
     * 1) if a = b then return 0;
     * 2) if a > b, then return 1;
     * 3) if a < b, then return -1;
     * PS1: if a = null, then a is considered as smaller than non-null b;
     * PS2: if a and b are both null, then they are considered equal;
     * PS3: for the author comparison, we should compose the string of the author names separated by commas.
     */
    
    public Comparator<Paper> comparator = (Paper p1, Paper p2) -> {
        switch (this.base) {
            case ID:
                String id1 = p1.getPaperID();
                String id2 = p2.getPaperID();
                if (id1 == null && id2 == null) {
                    return 0;
                } else if (id1 == null) {
                    return -1;
                } else if (id2 == null) {
                    return 1;
                } else {
                    return id1.compareTo(id2);
                }
            case TITLE:
                String title1 = p1.getTitle();
                String title2 = p2.getTitle();
                if (title1 == null && title2 == null) {
                    return 0;
                } else if (title1 == null) {
                    return -1;
                } else if (title2 == null) {
                    return 1;
                } else {
                    return title1.compareTo(title2);
                }
            case AUTHOR:
                String author1 = String.join(",", p1.getAuthors());
                String author2 = String.join(",", p2.getAuthors());
                if (author1 == null && author2 == null) {
                    return 0;
                } else if (author1 == null) {
                    return -1;
                } else if (author2 == null) {
                    return 1;
                } else {
                    return author1.compareTo(author2);
                }
            case JOURNAL:
                String journal1 = p1.getJournal();
                String journal2 = p2.getJournal();
                if (journal1 == null && journal2 == null) {
                    return 0;
                } else if (journal1 == null) {
                    return -1;
                } else if (journal2 == null) {
                    return 1;
                } else {
                    return journal1.compareTo(journal2);
                }
            default:
                throw new IllegalArgumentException("Invalid sort base");
        }
    };



    /**
     * TODO `sortFunc` provides a unified interface for sorting papers
     * @param a list of papers to be sorted into `actionResult`
     * @return `actionResult` that contains the papers sorted in the specified order
     */
    public Supplier<List<Paper>> sortFunc = () -> {
    
        this.actionResult.sort(this.comparator);
        return this.actionResult;
    };


}
