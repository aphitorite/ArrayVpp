package io.github.arrayv.sorts.templates;

import java.util.function.UnaryOperator;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.Constants;
import io.github.arrayv.utils.Delays;
import io.github.arrayv.utils.Highlights;
import io.github.arrayv.utils.Reads;
import io.github.arrayv.utils.Writes;

public abstract class Sort {
    private Object[] deprecatedMetadataTable = null;

    protected ArrayVisualizer arrayVisualizer;

    protected Delays Delays;
    protected Highlights Highlights;
    protected Reads Reads;
    protected Writes Writes;

    private boolean useShellsortGaps;

    protected Sort(ArrayVisualizer arrayVisualizer) {
        this.arrayVisualizer = arrayVisualizer;

        this.Delays = arrayVisualizer.getDelays();
        this.Highlights = arrayVisualizer.getHighlights();
        this.Reads = arrayVisualizer.getReads();
        this.Writes = arrayVisualizer.getWrites();
    }

    private void initDeprecatedMetadataTable() {
        if (deprecatedMetadataTable != null) return;
        deprecatedMetadataTable = new Object[] {
            true, "", "", "", "", (UnaryOperator<Long>) (n -> -1L), false, false, false, 0, null, 0, ""
        };
    }

    public boolean isFromExtraSorts() {
        return arrayVisualizer.getSortAnalyzer().didSortComeFromExtra(getClass());
    }

    public boolean isSortEnabled() {
        initDeprecatedMetadataTable();
        return (boolean)deprecatedMetadataTable[0];
    }

    public String getSortListName() {
        initDeprecatedMetadataTable();
        return (String)deprecatedMetadataTable[1];
    }


    public String getRunAllSortsName() {
        initDeprecatedMetadataTable();
        return (String)deprecatedMetadataTable[2];
    }


    public String getRunSortName() {
        initDeprecatedMetadataTable();
        return (String)deprecatedMetadataTable[3];
    }

    public String getCategory() {
        initDeprecatedMetadataTable();
        return (String)deprecatedMetadataTable[4];
    }

    public String getAuthors() {
        initDeprecatedMetadataTable();
        return (String)deprecatedMetadataTable[12];
    }

    @SuppressWarnings("unchecked")
	public UnaryOperator<Long> getConstant() {
        initDeprecatedMetadataTable();
        return (UnaryOperator<Long>)deprecatedMetadataTable[5];
    }

    /**
     * Whether this sort is a comparison sort or a distribution sort
     * @deprecated This method now always returns false, as this information is no longer stored
     * @return false
     */
    @Deprecated
    public boolean isComparisonBased() {
        return false;
    }

    public boolean usesBuckets() {
        initDeprecatedMetadataTable();
        return (boolean)deprecatedMetadataTable[6];
    }

    public boolean isRadixSort() {
        initDeprecatedMetadataTable();
        return (boolean)deprecatedMetadataTable[7];
    }

    public boolean isUnreasonablySlow() {
        initDeprecatedMetadataTable();
        return (int)deprecatedMetadataTable[9] > 0;
    }

    public int getUnreasonableLimit() {
        initDeprecatedMetadataTable();
        return (int)deprecatedMetadataTable[9];
    }

    public boolean isBogoSort() {
        initDeprecatedMetadataTable();
        return (boolean)deprecatedMetadataTable[8];
    }

    public String getQuestion() {
        initDeprecatedMetadataTable();
        return (String)deprecatedMetadataTable[10];
    }

    public int getDefaultAnswer() {
        initDeprecatedMetadataTable();
        return (int)deprecatedMetadataTable[11];
    }

    public String getAuthors() {
        initDeprecatedMetadataTable();
        return (String)deprecatedMetadataTable[12];
    }

    public boolean getUseShellsortGaps() {
        return this.useShellsortGaps;
    }

    protected void enableSort(boolean enabled) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[0] = enabled;
    }

    protected void setSortListName(String listName) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[1] = listName;
    }

    protected void setRunAllSortsName(String showcaseName) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[2] = showcaseName;
    }

    protected void setRunSortName(String runName) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[3] = runName;
    }

    protected void setCategory(String category) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[4] = category;
    }

    protected void setAuthors(String authors) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[12] = authors;
    }

    protected void setConstant(String alias) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[5] = Constants.constants.getOrDefault(alias, n -> -1L);
    }

    protected void setConstant(UnaryOperator<Long> rawfn) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[5] = rawfn;
    }

    /**
     * Sets whether this sort is a comparison sort or a distribution sort
     * @deprecated This method doesn't do anything, as this information is no longer stored
     * @param comparisonBased Whether this sort is a comparison sort or a distribution sort
     */
    @Deprecated
    public void setComparisonBased(boolean comparisonBased) {
    }

    public void setBucketSort(boolean bucketSort) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[6] = bucketSort;
    }

    protected void setRadixSort(boolean radixSort) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[7] = radixSort;
    }

    public void setUnreasonablySlow(boolean unreasonableSlow) {
    }

    public void setUnreasonableLimit(int unreasonableLimit) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[9] = unreasonableLimit;
    }

    protected void setBogoSort(boolean bogoSort) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[8] = bogoSort;
    }

    public void setUseShellsortGaps(boolean useGaps) {
        this.useShellsortGaps = useGaps;
    }

    protected void setQuestion(String question) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[10] = question;
    }

    protected void setQuestion(String question, int defaultAnswer) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[10] = question;
        deprecatedMetadataTable[11] = defaultAnswer;
    }

    protected void setAuthors(String authors) {
        initDeprecatedMetadataTable();
        deprecatedMetadataTable[12] = authors;
    }

    public int validateAnswer(int answer) {
        return answer;
    }

    public abstract void runSort(int[] array, int sortLength, int bucketCount) throws Exception; //bucketCount will be zero for comparison-based sorts
}
